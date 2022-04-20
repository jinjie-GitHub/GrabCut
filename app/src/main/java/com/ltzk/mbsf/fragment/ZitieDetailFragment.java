package com.ltzk.mbsf.fragment;

import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.ltzk.mbsf.R;
import com.ltzk.mbsf.R2;
import com.ltzk.mbsf.activity.GlyphDetailActivity;
import com.ltzk.mbsf.activity.ZitieActivity;
import com.ltzk.mbsf.api.presenter.ZitieZhujiePresenterImpl;
import com.ltzk.mbsf.api.view.ZitieZhujieView;
import com.ltzk.mbsf.base.MyBaseFragment;
import com.ltzk.mbsf.bean.DetailsBean;
import com.ltzk.mbsf.bean.PointBean;
import com.ltzk.mbsf.bean.RequestBean;
import com.ltzk.mbsf.bean.ZiBean;
import com.ltzk.mbsf.bean.ZitieDetailitemBean;
import com.ltzk.mbsf.utils.Logger;
import com.ltzk.mbsf.utils.ViewUtil;
import com.ltzk.mbsf.widget.bigScaleImage.ImageSource;
import com.ltzk.mbsf.widget.bigScaleImage.MyImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * update on 2021/05/08
 */
public class ZitieDetailFragment extends MyBaseFragment<ZitieZhujieView, ZitieZhujiePresenterImpl> implements ZitieZhujieView {
    private static final String DETAIL = "zitieDetailitemBean";
    private static final String POSITION = "position";

    @BindView(R2.id.lay_pb)
    LinearLayout lay_pb;

    @BindView(R2.id.iv_zitie_sub)
    MyImageView iv_zitie;

    @BindView(R.id.render)
    ImageView mImageView;

    private ZitieDetailitemBean mZitieDetailitemBean;
    public ZitieDetailitemBean getmZitieDetailitemBean() {
        return mZitieDetailitemBean;
    }

    public static ZitieDetailFragment newInstance(int postion, ZitieDetailitemBean zitieDetailitemBean) {
        Logger.d("ZitieDetailFragment:" + postion);
        Bundle bundle = new Bundle();
        bundle.putInt(POSITION, postion);
        bundle.putSerializable(DETAIL, zitieDetailitemBean);
        ZitieDetailFragment zitieDetailFragment = new ZitieDetailFragment();
        zitieDetailFragment.setArguments(bundle);
        return zitieDetailFragment;
    }

    //字典点击过来的遮盖
    private String frame;
    public void setFrame(String frame) {
        this.frame = frame;
    }

    public MyImageView getIv_ziTie() {
        return iv_zitie;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_zitie_detail;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mZitieDetailitemBean = (ZitieDetailitemBean) getArguments().getSerializable(DETAIL);
            mPosition = getArguments().getInt(POSITION);
        }
    }

    @Override
    protected void initView(View rootView) {
        if (mZitieDetailitemBean == null) return;
        iv_zitie.setCallBack(new MyImageView.CallBack() {
            @Override
            public void onClick(float x, float y, float zoom, float sx, float sy) {
                //需要先购买vip
                if (iv_zitie.isShowDama()) {
                    return;
                }

                //图片上没有可以点击的文字，按点击空白区域的场景处理
                if (list_zi == null || list_zi.size() == 0) {
                    onClickBean = new OnClickBean(x, y, zoom, sx, sy);
                    getZhujieData();
                    return;
                }

                onClickOnZitie(x, y, zoom, sx, sy);
            }
        });

        iv_zitie.setChange(state -> ((ZitieActivity) activity).resetLocation());

        //初始化显示loading
        lay_pb.setVisibility(View.VISIBLE);
        Glide.with(this).asBitmap()
                //.override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .load(mZitieDetailitemBean.get_image())
                .addListener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        lay_pb.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        if (resource != null) {
                            lay_pb.setVisibility(View.GONE);
                            final float scale = getScale(resource.getWidth(), resource.getHeight());
                            iv_zitie.setMinimumScaleType(MyImageView.SCALE_TYPE_CUSTOM);
                            iv_zitie.setMinScale(scale);
                            iv_zitie.setMaxScale(iv_zitie.getMinScale() * 16);
                            final ImageSource source = ImageSource.bitmap(resource);
                            source.tilingDisabled();
                            iv_zitie.setImage(source);
                            final float scale2 = getScale(mZitieDetailitemBean.getWidth(), mZitieDetailitemBean.getHeight());
                            iv_zitie.setProportion(scale2 / scale);
                        }
                        return false;
                    }
                }).into(mImageView);

        //初始化从字典进入需要显示定位
        if (frame != null && frame.length() > 0 && ((ZitieActivity) activity).isInit) {
            ((ZitieActivity) activity).isInit = false;
            setZhegai();
            iv_zitie.setShowZhegai(true);
        }
        //不显示注解
        showZhujie(false);
    }

    OnClickBean onClickBean;
    class OnClickBean {
        float x, y, zoom, sx, sy;
        public OnClickBean(float x, float y, float zoom, float sx, float sy) {
            this.x = x;
            this.y = y;
            this.zoom = zoom;
            this.sx = sx;
            this.sy = sy;
        }
    }

    private void onClickOnZitie(float x, float y, float zoom, float sx, float sy) {
        if (list_zi != null) {
            for (int i = 0; i < list_zi.size(); i++) {
                ZiBean ziBean = list_zi.get(i);
                String[] frames = ziBean.get_frame().split(",");
                float left = Float.valueOf(frames[0]) * zoom + sx;
                float top = Float.valueOf(frames[1]) * zoom + sy;
                float right = left + (Float.valueOf(frames[2])) * zoom;
                float bottom = top + Float.valueOf(frames[3]) * zoom;
                RectF rect = new RectF(left, top, right, bottom);
                PointBean pointBean = new PointBean(left, top, rect);
                if (pointBean.isIn(x, y)) {
                    //需要先关掉定位遮盖
                    if (iv_zitie.isShowZhegai()) {//有遮罩时被点击，先去掉遮罩
                        iv_zitie.setShowZhegai(false);
                        iv_zitie.invalidate();
                    } else {
                        //显示手写
                        final List<DetailsBean> list = new ArrayList<>();
                        for (ZiBean glyphsListBean : list_zi) {
                            if (!TextUtils.isEmpty(glyphsListBean.get_id())) {
                                list.add(DetailsBean.newDetails(glyphsListBean.get_id(), glyphsListBean.get_hanzi()));
                            }
                        }
                        GlyphDetailActivity.safeStart(activity, ziBean.get_id(), list);
                    }
                    return;
                }
            }
        }
        //点击的区域没有字，切换是否显示底部和顶部
        ((ZitieActivity) activity).change();
    }

    @Override
    protected void requestDataAutoRefresh() {
        super.requestDataAutoRefresh();
        //显示vip
        ((ZitieActivity) activity).vipExpire();
    }

    int mPosition = 0;
    public void onPause() {
        if (((ZitieActivity) activity).getPosition() != mPosition) {
            iv_zitie.setScaleAndCenter(mScale, new PointF(0, 0));
            if (iv_zitie.isShowZhegai() || iv_zitie.isShowZhujie()) {//有遮罩时被点击，先去掉遮罩
                iv_zitie.setShowZhegai(false);
                iv_zitie.setShowZhujie(false);
                iv_zitie.invalidate();
            }
        }
        super.onPause();
    }

    private float mScale = 0.22f;
    private float getScale(final int dw, final int dh) {
        int width = ViewUtil.getScreenWidth(activity);
        int height = ViewUtil.getScreenHeight(activity);

        float scale_w = width * 0.7f / dw;
        float scale_h = height * 0.7f / dh;
        if (scale_h < scale_w) {
            mScale = scale_h;
        } else {
            mScale = scale_w;
        }
        Logger.d("scale:" + mScale);
        return mScale;
    }

    public void showBigOrTop(final boolean isAll, final View v) {
        if (iv_zitie != null) {
            iv_zitie.post(() -> {
                /*int y1 = ViewUtil.dpToPx(40);
                int y2 = ViewUtil.getScreenHeight(activity) - iv_zitie.getMeasuredHeight();
                int y3 = Math.abs(y2 / 2 - y1);*/
                ObjectAnimator.ofFloat(v, "translationY", isAll ? 0 : ViewUtil.dpToPx(-60))
                        .setDuration(500)
                        .start();
            });
        }
    }

    public void setZhegai() {
        if (frame != null && frame.length() > 0) {
            //字的位置大小信息
            String[] frames = frame.split(",");
            float width2 = Float.parseFloat(frames[2]);
            float height2 = Float.parseFloat(frames[3]);
            iv_zitie.setRect(Float.parseFloat(frames[0]), Float.parseFloat(frames[1]), Float.parseFloat(frames[0]) + width2, Float.parseFloat(frames[1]) + height2);
        }
    }

    /**
     * 控制定位遮罩的显示、隐藏
     * isTrue 是否强制定位
     */
    public void dingweiShow(boolean isTrue) {
        if (iv_zitie == null) {
            return;
        }
        if (frame != null && frame.length() > 0 && (isTrue || !iv_zitie.isShowZhegai())) {
            setZhegai();
            iv_zitie.setShowZhegai(true);
        } else {
            iv_zitie.setShowZhegai(false);
        }
        iv_zitie.invalidate();
    }

    public boolean isShowZhegai() {
        return iv_zitie.isShowZhegai();
    }

    /**
     * 控制注解显示、隐藏
     */
    public void showZhujie(boolean isShow) {
        if (iv_zitie == null) {
            return;
        }
        if (!isShow) {
            iv_zitie.setShowZhujie(false);
            return;
        }

        if ((list_zi == null || list_zi.isEmpty())) {
            getZhujieData();
        } else {
            iv_zitie.setList_zi(list_zi);
            iv_zitie.setShowZhujie(true);
        }
    }

    /**
     * 控制vip打码遮盖是否显示
     */
    public void setLay_vip(int type) {
        if (iv_zitie == null) {
            return;
        }
        if (type == View.GONE) {
            iv_zitie.setShowDama(false);
        } else {
            iv_zitie.setDama(R.drawable.shape_mosaic);
            iv_zitie.setShowDama(true);
        }
    }

    /****************************************************请求注解数据********************************/
    //图片中的子列表
    private List<ZiBean> list_zi = new ArrayList<>();
    private RequestBean requestBean;

    /**
     * 请求注解数据
     */
    private void getZhujieData() {
        requestBean.addParams("zid", mZitieDetailitemBean.getZid());
        requestBean.addParams("page", mZitieDetailitemBean.getPage());
        presenter.zitie_page_glyphs(requestBean, true);
    }

    @Override
    protected ZitieZhujiePresenterImpl getPresenter() {
        requestBean = new RequestBean();
        return new ZitieZhujiePresenterImpl();
    }

    @Override
    public void showProgress() {
        //showProgressDialog("");
    }

    @Override
    public void disimissProgress() {
        //closeProgressDialog();
    }

    @Override
    public void loadDataSuccess(List<ZiBean> list) {//请求注解数据成功
        this.list_zi = list;
        if (onClickBean == null) {
            iv_zitie.setList_zi(list_zi);
            iv_zitie.setShowZhujie(true);
        } else {
            onClickOnZitie(onClickBean.x, onClickBean.y, onClickBean.zoom, onClickBean.sx, onClickBean.sy);
            onClickBean = null;
        }
    }

    @Override
    public void loadDataError(String errorMsg) {
        if (onClickBean != null) {
            onClickBean = null;
            //点击的区域没有字，切换是否显示底部和顶部
            ((ZitieActivity) activity).change();
        }
    }
}