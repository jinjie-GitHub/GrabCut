package com.ltzk.mbsf.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Dimension;
import androidx.annotation.DrawableRes;
import androidx.annotation.IntDef;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.stx.xhb.androidx.OnDoubleClickListener;
import com.stx.xhb.androidx.R;
import com.stx.xhb.androidx.XBannerUtils;
import com.stx.xhb.androidx.XBannerViewPager;
import com.stx.xhb.androidx.entity.BaseBannerInfo;
import com.stx.xhb.androidx.transformers.BasePageTransformer;
import com.stx.xhb.androidx.transformers.Transformer;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 1.支持图片无限轮播控件;
 * 2.支持自定义指示器的背景和两种状态指示点;
 * 3.支持隐藏指示器、设置是否轮播、设置轮播时间间隔;
 * 4.支持设置图片描述;
 * 5.支持自定义图片切换动画、以及设置图片切换速度.
 * 6.支持设置提示性文字  不需要的时候直接设置提示性文字数据为null即可;
 */
public class XBanner extends RelativeLayout implements XBannerViewPager.AutoPlayDelegate, ViewPager.OnPageChangeListener {

    private static final int RMP = LayoutParams.MATCH_PARENT;
    private static final int RWC = LayoutParams.WRAP_CONTENT;
    private static final int LWC = LinearLayout.LayoutParams.WRAP_CONTENT;

    private static final int VEL_THRESHOLD = 400;
    public static final int NO_PLACE_HOLDER = -1;
    public static final int MAX_VALUE = Integer.MAX_VALUE;
    private int mPageScrollPosition;
    private float mPageScrollPositionOffset;

    private ViewPager.OnPageChangeListener mOnPageChangeListener;
    private OnItemClickListener mOnItemClickListener;

    /**
     * 指示点位置
     */
    public static final int LEFT = 0;
    public static final int CENTER = 1;
    public static final int RIGHT = 2;

    @IntDef({LEFT, CENTER, RIGHT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface INDICATOR_GRAVITY {
    }

    private AutoSwitchTask mAutoSwitchTask;

    private LinearLayout mPointRealContainerLl;

    private XBannerViewPager mViewPager;

    /**
     * 指示点左右内间距
     */
    private int mPointLeftRightPadding;

    /**
     * 指示点上下内间距
     */
    private int mPointTopBottomPadding;

    /**
     * 指示点容器左右内间距
     */
    private int mPointContainerLeftRightPadding;

    /**
     * 资源集合
     */
    private List<?> mData;

    /**
     * 是否只有一张图片
     */
    private boolean mIsOneImg = false;

    /**
     * 是否开启自动轮播
     */
    private boolean mIsAutoPlay = true;

    /**
     * 自动播放时间
     */
    private int mAutoPlayTime = 5000;

    /**
     * 是否允许用户滑动
     */
    private boolean mIsAllowUserScroll = true;

    /**
     * viewpager从最后一张到第一张的动画效果
     */
    private int mSlideScrollMode = OVER_SCROLL_ALWAYS;

    /**
     * 指示点位置
     */
    private int mPointPosition = CENTER;

    /**
     * 正常状态下的指示点
     */
    private @DrawableRes
    int mPointNormal;

    /**
     * 选中状态下的指示点
     */
    private @DrawableRes
    int mPointSelected;

    /**
     * 指示容器背景
     */
    private Drawable mPointContainerBackgroundDrawable;

    /**
     * 指示容器布局规则
     */
    private LayoutParams mPointRealContainerLp;

    /**
     * 提示语
     */
    private TextView mTipTv;

    /**
     * 提示语字体颜色
     */
    private int mTipTextColor;

    /**
     * 指示点是否可见
     */
    private boolean mPointsIsVisible = true;

    /**
     * 提示语字体大小
     */
    private int mTipTextSize;

    /**
     * 是否展示提示语
     */
    private boolean mIsShowTips;

    /**
     * 提示文案数据
     */
    private List<String> mTipData;

    /**
     * 指示器容器位置
     */
    public static final int TOP = 10;
    public static final int BOTTOM = 12;

    @IntDef({TOP, BOTTOM})
    @Retention(RetentionPolicy.SOURCE)
    public @interface INDICATOR_POSITION {
    }

    private int mPointContainerPosition = BOTTOM;
    private XBannerAdapter mAdapter;

    /*指示器容器*/
    private LayoutParams mPointContainerLp;

    /*是否是数字指示器*/
    private boolean mIsNumberIndicator = false;
    private TextView mNumberIndicatorTv;

    /*数字指示器背景*/
    private Drawable mNumberIndicatorBackground;

    /*只有一张图片时是否显示指示点*/
    private boolean mIsShowIndicatorOnlyOne = false;

    /*默认图片切换速度为1s*/
    private int mPageChangeDuration = 1000;

    /*是否支持提示文字跑马灯效果*/
    private boolean mIsTipsMarquee = false;

    /*是否是第一次不可见*/
    private boolean mIsFirstInvisible = true;

    /*非自动轮播状态下是否可以循环切换*/
    private boolean mIsHandLoop = false;

    private Transformer mTransformer;

    /*轮播框架占位图资源Id*/
    private int mPlaceholderDrawableResId = -1;

    private ImageView mPlaceholderImg;

    /*是否开启一屏显示多个模式*/
    private boolean mIsClipChildrenMode;

    /*一屏显示多个模式左右间距*/
    private int mClipChildrenLeftMargin;

    /*一屏显示多个模式左右间距*/
    private int mClipChildrenRightMargin;

    /*一屏显示多个模式上下间距*/
    private int mClipChildrenTopBottomMargin;

    /*viewpager之间的间距*/
    private int mViewPagerMargin;

    /*少于三张是否支持一屏多显模式*/
    private boolean mIsClipChildrenModeLessThree;

    /**
     * XBanner图片轮播区域底部Margin
     */
    public int mBannerBottomMargin = 0;

    /**
     * 当前下标
     */
    public int currentPos = 0;

    /**
     * 一屏多显模式下指示器是否显示在中间图片位置上，默认开启
     */
    private boolean mShowIndicatorInCenter;
    /**
     * 布局文件
     */
    @LayoutRes
    private int layoutResId = -1;
    /**
     * 一屏多页模式是否可点击侧边切换，默认为true
     */
    private boolean isCanClickSide = true;

    private ImageView.ScaleType mScaleType = ImageView.ScaleType.FIT_XY;

    private static final ImageView.ScaleType[] sScaleTypeArray = {
            ImageView.ScaleType.MATRIX,
            ImageView.ScaleType.FIT_XY,
            ImageView.ScaleType.FIT_START,
            ImageView.ScaleType.FIT_CENTER,
            ImageView.ScaleType.FIT_END,
            ImageView.ScaleType.CENTER,
            ImageView.ScaleType.CENTER_CROP,
            ImageView.ScaleType.CENTER_INSIDE
    };

    /**
     * 请使用 {@link #loadImage} 替换
     */
    @Deprecated
    public void setAdapter(XBannerAdapter adapter) {
        this.mAdapter = adapter;
    }

    public void loadImage(XBannerAdapter mAdapter) {
        this.mAdapter = mAdapter;
    }

    public XBanner(Context context) {
        this(context, null);
    }

    public XBanner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XBanner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        initCustomAttrs(context, attrs);
        initView();
    }

    private void init(Context context) {
        mAutoSwitchTask = new AutoSwitchTask(this);
        mPointLeftRightPadding = XBannerUtils.dp2px(context, 3);
        mPointTopBottomPadding = XBannerUtils.dp2px(context, 6);
        mPointContainerLeftRightPadding = XBannerUtils.dp2px(context, 10);
        mClipChildrenLeftMargin = XBannerUtils.dp2px(context, 30);
        mClipChildrenRightMargin = XBannerUtils.dp2px(context, 30);
        mClipChildrenTopBottomMargin = XBannerUtils.dp2px(context, 10);
        mViewPagerMargin = XBannerUtils.dp2px(context, 10);
        mTipTextSize = XBannerUtils.sp2px(context, 10);
        mTransformer = Transformer.Default;
        /*设置默认提示语字体颜色*/
        mTipTextColor = Color.WHITE;
        /*设置指示器背景*/
        mPointContainerBackgroundDrawable = new ColorDrawable(Color.parseColor("#44aaaaaa"));
    }

    private void initCustomAttrs(Context context, AttributeSet attrs) {

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.XBanner);
        if (typedArray != null) {
            mIsAutoPlay = typedArray.getBoolean(R.styleable.XBanner_isAutoPlay, true);
            mIsHandLoop = typedArray.getBoolean(R.styleable.XBanner_isHandLoop, false);
            mIsTipsMarquee = typedArray.getBoolean(R.styleable.XBanner_isTipsMarquee, false);
            mAutoPlayTime = typedArray.getInteger(R.styleable.XBanner_AutoPlayTime, 5000);
            mPointsIsVisible = typedArray.getBoolean(R.styleable.XBanner_pointsVisibility, true);
            mPointPosition = typedArray.getInt(R.styleable.XBanner_pointsPosition, CENTER);
            mPointContainerLeftRightPadding = typedArray.getDimensionPixelSize(R.styleable.XBanner_pointContainerLeftRightPadding, mPointContainerLeftRightPadding);
            mPointLeftRightPadding = typedArray.getDimensionPixelSize(R.styleable.XBanner_pointLeftRightPadding, mPointLeftRightPadding);
            mPointTopBottomPadding = typedArray.getDimensionPixelSize(R.styleable.XBanner_pointTopBottomPadding, mPointTopBottomPadding);
            mPointContainerPosition = typedArray.getInt(R.styleable.XBanner_pointContainerPosition, BOTTOM);
            mPointContainerBackgroundDrawable = typedArray.getDrawable(R.styleable.XBanner_pointsContainerBackground);
            mPointNormal = typedArray.getResourceId(R.styleable.XBanner_pointNormal, R.drawable.shape_point_normal);
            mPointSelected = typedArray.getResourceId(R.styleable.XBanner_pointSelect, R.drawable.shape_point_select);
            mTipTextColor = typedArray.getColor(R.styleable.XBanner_tipTextColor, mTipTextColor);
            mTipTextSize = typedArray.getDimensionPixelSize(R.styleable.XBanner_tipTextSize, mTipTextSize);
            mIsNumberIndicator = typedArray.getBoolean(R.styleable.XBanner_isShowNumberIndicator, mIsNumberIndicator);
            mNumberIndicatorBackground = typedArray.getDrawable(R.styleable.XBanner_numberIndicatorBacgroud);
            mIsShowIndicatorOnlyOne = typedArray.getBoolean(R.styleable.XBanner_isShowIndicatorOnlyOne, mIsShowIndicatorOnlyOne);
            mPageChangeDuration = typedArray.getInt(R.styleable.XBanner_pageChangeDuration, mPageChangeDuration);
            mPlaceholderDrawableResId = typedArray.getResourceId(R.styleable.XBanner_placeholderDrawable, mPlaceholderDrawableResId);
            mIsClipChildrenMode = typedArray.getBoolean(R.styleable.XBanner_isClipChildrenMode, false);
            mClipChildrenLeftMargin = typedArray.getDimensionPixelSize(R.styleable.XBanner_clipChildrenLeftMargin, mClipChildrenLeftMargin);
            mClipChildrenRightMargin = typedArray.getDimensionPixelSize(R.styleable.XBanner_clipChildrenRightMargin, mClipChildrenRightMargin);
            mClipChildrenTopBottomMargin = typedArray.getDimensionPixelSize(R.styleable.XBanner_clipChildrenTopBottomMargin, mClipChildrenTopBottomMargin);
            mViewPagerMargin = typedArray.getDimensionPixelSize(R.styleable.XBanner_viewpagerMargin, mViewPagerMargin);
            mIsClipChildrenModeLessThree = typedArray.getBoolean(R.styleable.XBanner_isClipChildrenModeLessThree, false);
            mIsShowTips = typedArray.getBoolean(R.styleable.XBanner_isShowTips, false);
            mBannerBottomMargin = typedArray.getDimensionPixelSize(R.styleable.XBanner_bannerBottomMargin, mBannerBottomMargin);
            mShowIndicatorInCenter = typedArray.getBoolean(R.styleable.XBanner_showIndicatorInCenter, true);
            int scaleTypeIndex = typedArray.getInt(R.styleable.XBanner_android_scaleType, -1);
            if (scaleTypeIndex >= 0 && scaleTypeIndex < sScaleTypeArray.length) {
                mScaleType = sScaleTypeArray[scaleTypeIndex];
            }
            typedArray.recycle();
        }
        if (mIsClipChildrenMode) {
            mTransformer = Transformer.Scale;
        }
    }

    private void initView() {

        /*设置指示器背景容器*/
        RelativeLayout pointContainerRl = new RelativeLayout(getContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            pointContainerRl.setBackground(mPointContainerBackgroundDrawable);
        } else {
            pointContainerRl.setBackgroundDrawable(mPointContainerBackgroundDrawable);
        }

        /*设置内边距*/
        pointContainerRl.setPadding(mPointContainerLeftRightPadding, mPointTopBottomPadding, mPointContainerLeftRightPadding, mPointTopBottomPadding);

        /*设定指示器容器布局及位置*/
        mPointContainerLp = new LayoutParams(RMP, RWC);
        mPointContainerLp.addRule(mPointContainerPosition);
        if (mIsClipChildrenMode && mShowIndicatorInCenter) {
            mPointContainerLp.setMargins(mClipChildrenLeftMargin, 0, mClipChildrenRightMargin, 0);
        }
        addView(pointContainerRl, mPointContainerLp);
        mPointRealContainerLp = new LayoutParams(RWC, RWC);
        /*设置指示器容器*/
        if (mIsNumberIndicator) {
            mNumberIndicatorTv = new TextView(getContext());
            mNumberIndicatorTv.setId(R.id.xbanner_pointId);
            mNumberIndicatorTv.setGravity(Gravity.CENTER);
            mNumberIndicatorTv.setSingleLine(true);
            mNumberIndicatorTv.setEllipsize(TextUtils.TruncateAt.END);
            mNumberIndicatorTv.setTextColor(mTipTextColor);
            mNumberIndicatorTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTipTextSize);
            mNumberIndicatorTv.setVisibility(View.INVISIBLE);
            if (mNumberIndicatorBackground != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mNumberIndicatorTv.setBackground(mNumberIndicatorBackground);
                } else {
                    mNumberIndicatorTv.setBackgroundDrawable(mNumberIndicatorBackground);
                }
            }
            pointContainerRl.addView(mNumberIndicatorTv, mPointRealContainerLp);
        } else {
            mPointRealContainerLl = new LinearLayout(getContext());
            mPointRealContainerLl.setOrientation(LinearLayout.HORIZONTAL);
            mPointRealContainerLl.setId(R.id.xbanner_pointId);
            pointContainerRl.addView(mPointRealContainerLl, mPointRealContainerLp);
        }

        /*设置指示器是否可见*/
        if (mPointRealContainerLl != null) {
            if (mPointsIsVisible) {
                mPointRealContainerLl.setVisibility(View.VISIBLE);
            } else {
                mPointRealContainerLl.setVisibility(View.GONE);
            }
        }

        /*设置提示语*/
        LayoutParams pointLp = new LayoutParams(RMP, RWC);
        pointLp.addRule(CENTER_VERTICAL);

        if (mIsShowTips) {
            mTipTv = new TextView(getContext());
            mTipTv.setGravity(Gravity.CENTER_VERTICAL);
            mTipTv.setSingleLine(true);
            if (mIsTipsMarquee) {
                mTipTv.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                mTipTv.setMarqueeRepeatLimit(3);
                mTipTv.setSelected(true);
            } else {
                mTipTv.setEllipsize(TextUtils.TruncateAt.END);
            }
            mTipTv.setTextColor(mTipTextColor);
            mTipTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTipTextSize);
            pointContainerRl.addView(mTipTv, pointLp);
        }

        /*设置指示器布局位置*/
        if (CENTER == mPointPosition) {
            mPointRealContainerLp.addRule(RelativeLayout.CENTER_HORIZONTAL);
            pointLp.addRule(RelativeLayout.LEFT_OF, R.id.xbanner_pointId);
        } else if (LEFT == mPointPosition) {
            mPointRealContainerLp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            if (mTipTv != null) {
                mTipTv.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
            }
            pointLp.addRule(RelativeLayout.RIGHT_OF, R.id.xbanner_pointId);
        } else if (RIGHT == mPointPosition) {
            mPointRealContainerLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            pointLp.addRule(RelativeLayout.LEFT_OF, R.id.xbanner_pointId);
        }
        setBannerPlaceholderDrawable();
    }

    /**
     * 设置图片轮播框架占位图
     */
    private void setBannerPlaceholderDrawable() {
        if (mPlaceholderDrawableResId != NO_PLACE_HOLDER && mPlaceholderImg == null) {
            mPlaceholderImg = new ImageView(getContext());
            mPlaceholderImg.setScaleType(mScaleType);
            mPlaceholderImg.setImageResource(mPlaceholderDrawableResId);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RMP, RMP);
            addView(mPlaceholderImg, layoutParams);
        }
    }

    /**
     * 移除图片轮播框架占位图
     */
    private void removeBannerPlaceHolderDrawable() {
        if (mPlaceholderImg != null && this.equals(mPlaceholderImg.getParent())) {
            removeView(mPlaceholderImg);
            mPlaceholderImg = null;
        }
    }

    /**
     * 设置banner数据
     * 请使用 {@link #setBannerData} 替换
     *
     * @param models
     */
    @Deprecated
    public void setData(@LayoutRes int layoutResId, @NonNull List<?> models, List<String> tips) {
        if (models == null) {
            models = new ArrayList<>();
        }
        if (models.isEmpty()) {
            mIsAutoPlay = false;
            mIsClipChildrenMode = false;
        }
        if (!mIsClipChildrenModeLessThree && models.size() < 3) {
            mIsClipChildrenMode = false;
        }
        this.layoutResId = layoutResId;
        this.mData = models;
        this.mTipData = tips;

        mIsOneImg = models.size() == 1;

        initPoints();
        initViewPager();
        removeBannerPlaceHolderDrawable();
        if (!models.isEmpty()) {
            removeBannerPlaceHolderDrawable();
        } else {
            setBannerPlaceholderDrawable();
        }
    }

    /**
     * 设置数据模型和文案，布局资源默认为ImageView
     * 请使用 {@link #setBannerData} 替换
     *
     * @param models 每一页的数据模型集合
     */
    @Deprecated
    public void setData(@NonNull List<?> models, List<String> tips) {
        setData(R.layout.xbanner_item_image, models, tips);
    }

    /**
     * 设置banner数据
     */
    public void setBannerData(@LayoutRes int layoutResId, @NonNull List<? extends BaseBannerInfo> models) {
        if (models == null) {
            models = new ArrayList<>();
        }
        if (models.isEmpty()) {
            mIsAutoPlay = false;
            mIsClipChildrenMode = false;
        }
        if (!mIsClipChildrenModeLessThree && models.size() < 3) {
            mIsClipChildrenMode = false;
        }
        this.layoutResId = layoutResId;
        mData = models;
        mIsOneImg = models.size() == 1;
        initPoints();
        initViewPager();
        removeBannerPlaceHolderDrawable();
        if (!models.isEmpty()) {
            removeBannerPlaceHolderDrawable();
        } else {
            setBannerPlaceholderDrawable();
        }
    }

    /**
     * 设置数据和文案，布局资源默认为ImageView
     *
     * @param models 轮播数据集合
     */
    public void setBannerData(@NonNull List<? extends BaseBannerInfo> models) {
        setBannerData(R.layout.xbanner_item_image, models);
    }

    /**
     * 设置指示点是否可见
     *
     * @param isVisible
     */
    public void setPointsIsVisible(boolean isVisible) {
        if (null != mPointRealContainerLl) {
            if (isVisible) {
                mPointRealContainerLl.setVisibility(View.VISIBLE);
            } else {
                mPointRealContainerLl.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 对应三个位置 CENTER,RIGHT,LEFT
     *
     * @param position
     */
    public void setPointPosition(@INDICATOR_GRAVITY int position) {
        //设置指示器布局位置
        if (CENTER == position) {
            mPointRealContainerLp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        } else if (LEFT == position) {
            mPointRealContainerLp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        } else if (RIGHT == position) {
            mPointRealContainerLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        }
    }

    /**
     * 设置指示器容器的位置  TOP,BOTTOM
     *
     * @param position
     */
    public void setPointContainerPosition(@INDICATOR_POSITION int position) {
        if (BOTTOM == position) {
            mPointContainerLp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        } else if (TOP == position) {
            mPointContainerLp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        }
    }

    /**
     * 初始化ViewPager
     */
    private void initViewPager() {
        if (mViewPager != null && this.equals(mViewPager.getParent())) {
            this.removeView(mViewPager);
            mViewPager = null;
        }
        currentPos = 0;
        mViewPager = new XBannerViewPager(getContext());
        mViewPager.setAdapter(new XBannerPageAdapter());
        mViewPager.clearOnPageChangeListeners();
        mViewPager.addOnPageChangeListener(this);
        mViewPager.setOverScrollMode(mSlideScrollMode);
        mViewPager.setIsAllowUserScroll(mIsAllowUserScroll);
        mViewPager.setPageTransformer(true, BasePageTransformer.getPageTransformer(mTransformer));
        setPageChangeDuration(mPageChangeDuration);
        LayoutParams layoutParams = new LayoutParams(RMP, RMP);
        layoutParams.setMargins(0, 0, 0, mBannerBottomMargin);
        if (mIsClipChildrenMode) {
            setClipChildren(false);
            mViewPager.setClipToPadding(false);
            mViewPager.setClipChildren(false);
            mViewPager.setPadding(mClipChildrenLeftMargin, mClipChildrenTopBottomMargin, mClipChildrenRightMargin, mBannerBottomMargin);
            mViewPager.setPageMargin(mViewPagerMargin);
        }
        addView(mViewPager, 0, layoutParams);
        /*当图片多于1张时开始轮播*/
        if (!mIsOneImg && mIsAutoPlay && getRealCount() != 0) {
            mViewPager.setCurrentItem(currentPos);
            startAutoPlay();
        } else {
            if (mIsHandLoop && getRealCount() != 0) {
                mViewPager.setCurrentItem(currentPos);
            }
            switchToPoint(0);
        }

        if (getRealCount() <= 1) {
            setPadding(30, 30, 30, 30);
        } else {
            correctChildrenMargin(0);
        }
    }

    /**
     *TODO 修改ViewPager边距
     */
    private void correctChildrenMargin(final int position) {
        if (position == 0) {//left
            mViewPager.setTranslationX(-mClipChildrenLeftMargin / 1.6f);
        } else if (position == getRealCount() - 1) {//right
            mViewPager.setTranslationX(mClipChildrenRightMargin / 1.6f);
        } else {
            mViewPager.setTranslationX(0);
        }
    }

    /**
     * 获取广告页面数量
     *
     * @return
     */
    public int getRealCount() {
        return mData == null ? 0 : mData.size();
    }

    public XBannerViewPager getViewPager() {
        return mViewPager;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset,
                               int positionOffsetPixels) {
        mPageScrollPosition = position;
        mPageScrollPositionOffset = positionOffset;
        if (mTipTv != null && mData != null && mData.size() != 0 && mData.get(0) instanceof BaseBannerInfo) {
            if (positionOffset > 0.5) {
                mTipTv.setText(((BaseBannerInfo) mData.get(getRealPosition(position + 1))).getXBannerTitle());
                mTipTv.setAlpha(positionOffset);
            } else {
                mTipTv.setText(((BaseBannerInfo) mData.get(getRealPosition(position))).getXBannerTitle());
                mTipTv.setAlpha(1 - positionOffset);
            }
        } else if (mTipTv != null && mTipData != null && !mTipData.isEmpty()) {
            if (positionOffset > 0.5) {
                mTipTv.setText(mTipData.get(getRealPosition(position + 1)));
                mTipTv.setAlpha(positionOffset);
            } else {
                mTipTv.setText(mTipData.get(getRealPosition(position)));
                mTipTv.setAlpha(1 - positionOffset);
            }
        }

        if (null != mOnPageChangeListener && getRealCount() != 0) {
            mOnPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    @Override
    public void onPageSelected(int position) {
        if (getRealCount() == 0) {
            return;
        }
        currentPos = getRealPosition(position);
        switchToPoint(currentPos);
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageSelected(currentPos);
        }
        correctChildrenMargin(currentPos);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageScrollStateChanged(state);
        }
    }

    @Override
    public void handleAutoPlayActionUpOrCancel(float xVelocity) {
        assert mViewPager != null;
        if (mPageScrollPosition < mViewPager.getCurrentItem()) {
            // 往右滑
            if (xVelocity > VEL_THRESHOLD || (mPageScrollPositionOffset < 0.7f && xVelocity > -VEL_THRESHOLD)) {
                mViewPager.setBannerCurrentItemInternal(mPageScrollPosition, true);
            } else {
                mViewPager.setBannerCurrentItemInternal(mPageScrollPosition + 1, true);
            }
        } else if (mPageScrollPosition == mViewPager.getCurrentItem()) {
            // 往左滑
            if (xVelocity < -VEL_THRESHOLD || (mPageScrollPositionOffset > 0.3f && xVelocity < VEL_THRESHOLD)) {
                mViewPager.setBannerCurrentItemInternal(mPageScrollPosition + 1, true);
            } else {
                mViewPager.setBannerCurrentItemInternal(mPageScrollPosition, true);
            }
        } else {
            mViewPager.setBannerCurrentItemInternal(mPageScrollPosition, true);
        }
    }

    private class XBannerPageAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            /*当只有一张图片时返回1*/
            if (mIsOneImg) {
                return 1;
            }
            return getRealCount();
        }

        /*@Override
        public float getPageWidth(int position) {
            return 0.8333f;
        }*/

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            final View view = LayoutInflater.from(getContext()).inflate(layoutResId, container, false);
            if (getRealCount() > 0) {
                final int realPosition = getRealPosition(position);
                if (mOnItemClickListener != null && !mData.isEmpty()) {
                    view.setOnClickListener(new OnDoubleClickListener() {
                        @Override
                        public void onNoDoubleClick(View v) {
                            if (isCanClickSide) {
                                setBannerCurrentItem(realPosition, true);
                            }
                            mOnItemClickListener.onItemClick(XBanner.this, mData.get(realPosition), v, realPosition);
                        }
                    });
                }
                if (null != mAdapter && !mData.isEmpty()) {
                    mAdapter.loadBanner(XBanner.this, mData.get(realPosition), view, realPosition);
                }
            }
            container.addView(view, 0);
            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @Override
        public void finishUpdate(@NonNull ViewGroup container) {
            super.finishUpdate(container);
        }
    }

    private int getRealPosition(final int position) {
        return position % getRealCount();
    }

    /**
     * 添加指示点
     */
    private void initPoints() {
        if (mPointRealContainerLl != null) {
            mPointRealContainerLl.removeAllViews();
            //当图片多于1张时添加指示点
            if (getRealCount() > 0 && (mIsShowIndicatorOnlyOne || !mIsOneImg)) {
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LWC, LWC);
                lp.gravity = Gravity.CENTER_VERTICAL;
                lp.setMargins(mPointLeftRightPadding, mPointTopBottomPadding, mPointLeftRightPadding, mPointTopBottomPadding);
                ImageView imageView;
                for (int i = 0; i < getRealCount(); i++) {
                    imageView = new ImageView(getContext());
                    imageView.setLayoutParams(lp);
                    if (mPointNormal != 0 && mPointSelected != 0) {
                        imageView.setImageResource(mPointNormal);
                    }
                    mPointRealContainerLl.addView(imageView);
                }
            }
        }

        if (mNumberIndicatorTv != null) {
            if (getRealCount() > 0 && (mIsShowIndicatorOnlyOne || !mIsOneImg)) {
                mNumberIndicatorTv.setVisibility(View.VISIBLE);
            } else {
                mNumberIndicatorTv.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 切换指示器
     *
     * @param currentPoint
     */
    private void switchToPoint(int currentPoint) {
        if (mPointRealContainerLl != null & mData != null && getRealCount() > 1) {
            for (int i = 0; i < mPointRealContainerLl.getChildCount(); i++) {
                if (i == currentPoint) {
                    ((ImageView) mPointRealContainerLl.getChildAt(i)).setImageResource(mPointSelected);
                } else {
                    ((ImageView) mPointRealContainerLl.getChildAt(i)).setImageResource(mPointNormal);
                }
                mPointRealContainerLl.getChildAt(i).requestLayout();
            }
        }

        if (mTipTv != null && mData != null && mData.size() != 0 && mData.get(0) instanceof BaseBannerInfo) {
            mTipTv.setText(((BaseBannerInfo) mData.get(currentPoint)).getXBannerTitle());
        } else if (mTipTv != null && mTipData != null && !mTipData.isEmpty()) {
            mTipTv.setText(mTipData.get(currentPoint));
        }

        if (mNumberIndicatorTv != null && mData != null && (mIsShowIndicatorOnlyOne || !mIsOneImg)) {
            mNumberIndicatorTv.setText(String.valueOf((currentPoint + 1) + "/" + mData.size()));
        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!mIsOneImg & mViewPager != null) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    float touchX = ev.getRawX();
                    int paddingLeft = mViewPager.getLeft();
                    if (touchX >= paddingLeft && touchX < XBannerUtils.getScreenWidth(getContext()) - paddingLeft) {
                        stopAutoPlay();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    startAutoPlay();
                    break;
                case MotionEvent.ACTION_CANCEL:
                    getParent().requestDisallowInterceptTouchEvent(false);
                case MotionEvent.ACTION_OUTSIDE:
                    startAutoPlay();
                    break;
                default:
                    break;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    public void startAutoPlay() {
        stopAutoPlay();
        if (mIsAutoPlay) {
            postDelayed(mAutoSwitchTask, mAutoPlayTime);
        }
    }

    public void stopAutoPlay() {
        if (mAutoSwitchTask != null) {
            removeCallbacks(mAutoSwitchTask);
        }
    }

    /**
     * 添加ViewPager滚动监听器
     *
     * @param onPageChangeListener
     */
    public void setOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener) {
        mOnPageChangeListener = onPageChangeListener;
    }

    /**
     * 设置图片从最后一张滚动到第一张的动画效果
     *
     * @param slideScrollMode
     */
    public void setSlideScrollMode(int slideScrollMode) {
        mSlideScrollMode = slideScrollMode;
        if (null != mViewPager) {
            mViewPager.setOverScrollMode(slideScrollMode);
        }
    }

    /**
     * 设置是否允许用户手指滑动
     *
     * @param allowUserScrollable true表示允许跟随用户触摸滑动，false反之
     */
    public void setAllowUserScrollable(boolean allowUserScrollable) {
        mIsAllowUserScroll = allowUserScrollable;
        if (null != mViewPager) {
            mViewPager.setIsAllowUserScroll(allowUserScrollable);
        }
    }

    /**
     * 设置是否自动轮播
     *
     * @param mAutoPlayAble
     */
    public void setAutoPlayAble(boolean mAutoPlayAble) {
        this.mIsAutoPlay = mAutoPlayAble;
        stopAutoPlay();
        if (mViewPager != null && mViewPager.getAdapter() != null) {
            mViewPager.getAdapter().notifyDataSetChanged();
        }
    }

    /**
     * 设置自动轮播时间间隔
     */
    public void setAutoPlayTime(int autoPlayTime) {
        this.mAutoPlayTime = autoPlayTime;
    }

    /**
     * 设置翻页动画效果
     *
     * @param transformer
     */
    public void setPageTransformer(Transformer transformer) {
        mTransformer = transformer;
        if (mViewPager != null) {
            initViewPager();
        }
    }

    /**
     * 设置viewpager间距
     *
     * @param viewPagerMargin 单位dp
     */
    public void setViewPagerMargin(@Dimension int viewPagerMargin) {
        this.mViewPagerMargin = viewPagerMargin;
        if (mViewPager != null) {
            mViewPager.setPageMargin(XBannerUtils.dp2px(getContext(), viewPagerMargin));
        }
    }

    /**
     * 自定义翻页动画效果
     *
     * @param transformer
     */
    public void setCustomPageTransformer(ViewPager.PageTransformer transformer) {
        if (transformer != null && mViewPager != null) {
            mViewPager.setPageTransformer(true, transformer);
        }
    }

    /**
     * 设置ViewPager切换速度
     *
     * @param duration
     */
    public void setPageChangeDuration(int duration) {
        if (mViewPager != null) {
            mViewPager.setScrollDuration(duration);
        }
    }

    /**
     * 设置非自动轮播状态下是否可以循环切换
     *
     * @param handLoop
     */
    public void setHandLoop(boolean handLoop) {
        mIsHandLoop = handLoop;
    }

    /**
     * 是否开启一屏多显模式
     *
     * @param mIsClipChildrenMode
     */
    public void setIsClipChildrenMode(boolean mIsClipChildrenMode) {
        this.mIsClipChildrenMode = mIsClipChildrenMode;
    }

    /**
     * 只有单张banner时是否展示指示器
     *
     * @param showIndicatorOnlyOne
     */
    public void setShowIndicatorOnlyOne(boolean showIndicatorOnlyOne) {
        mIsShowIndicatorOnlyOne = showIndicatorOnlyOne;
    }

    /**
     * 获取当前位置
     *
     * @return
     */
    public int getBannerCurrentItem() {
        if (mViewPager == null || mData == null || mData.size() == 0) {
            return -1;
        } else {
            return mViewPager.getCurrentItem();
        }
    }

    /**
     * 切换到指定位置
     *
     * @param position
     */
    public void setBannerCurrentItem(int position) {
        setBannerCurrentItem(position, false);
    }

    /**
     * 切换到指定位置
     *
     * @param position
     */
    public void setBannerCurrentItem(int position, boolean smoothScroll) {
        if (mViewPager == null || mData == null || position > getRealCount() - 1) {
            return;
        }
        if (mIsAutoPlay || mIsHandLoop) {
            int currentItem = mViewPager.getCurrentItem();
            int realCurrentItem = getRealPosition(currentItem);
            int offset = position - realCurrentItem;
            if (offset < 0) {
                for (int i = -1; i >= offset; i--) {
                    mViewPager.setCurrentItem(currentItem + i, smoothScroll);
                }
            } else if (offset > 0) {
                for (int i = 1; i <= offset; i++) {
                    mViewPager.setCurrentItem(currentItem + i, smoothScroll);
                }
            }
            startAutoPlay();
        } else {
            mViewPager.setCurrentItem(position, smoothScroll);
        }
    }

    /**
     * 设置一屏多页模式下是否支持点击侧边切换
     *
     * @param canClickSide
     */
    public void setCanClickSide(boolean canClickSide) {
        isCanClickSide = canClickSide;
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (VISIBLE == visibility) {
            startAutoPlay();
        } else if (GONE == visibility || INVISIBLE == visibility) {
            onInvisibleToUser();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        onInvisibleToUser();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startAutoPlay();
    }

    private static class AutoSwitchTask implements Runnable {
        private final WeakReference<XBanner> mXBanner;

        private AutoSwitchTask(XBanner mXBanner) {
            this.mXBanner = new WeakReference<>(mXBanner);
        }

        @Override
        public void run() {
            XBanner banner = mXBanner.get();
            if (banner != null) {
                if (banner.mViewPager != null) {
                    int currentItem = banner.mViewPager.getCurrentItem() + 1;
                    if (banner.getRealCount() == currentItem) {
                        banner.mViewPager.setCurrentItem(0, true);
                    } else {
                        banner.mViewPager.setCurrentItem(currentItem, true);
                    }
                }
                banner.startAutoPlay();
            }
        }
    }

    private void onInvisibleToUser() {
        stopAutoPlay();
        // 处理 RecyclerView 中从对用户不可见变为可见时卡顿的问题
        if (!mIsFirstInvisible && mIsAutoPlay && mViewPager != null && getRealCount() > 0 && mPageScrollPositionOffset != 0) {
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1, false);
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, false);
        }
        mIsFirstInvisible = false;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(XBanner banner, Object model, View view, int position);
    }

    public interface XBannerAdapter {
        void loadBanner(XBanner banner, Object model, View view, int position);
    }
}
