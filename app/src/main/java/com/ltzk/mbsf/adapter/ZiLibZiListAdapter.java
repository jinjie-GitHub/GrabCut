package com.ltzk.mbsf.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.ltzk.mbsf.MainApplication;
import com.ltzk.mbsf.R;
import com.ltzk.mbsf.bean.ZiBean;
import com.ltzk.mbsf.bean.ZiLibDetailBean;
import com.ltzk.mbsf.utils.ViewUtil;
import com.ltzk.mbsf.widget.MyLoadingImageView;

/**
 * update: 2021/6/4
 */
public class ZiLibZiListAdapter extends BaseQuickAdapter<ZiLibDetailBean, BaseViewHolder> {

    Activity activity;
    int width;

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public ZiLibZiListAdapter(Activity activity) {
        super(R.layout.adapter_zi_lib_detail);
        this.activity = activity;
    }

    /**
     * 计算列数
     */
    static final String TAG = "king";
    static final int PHONE = 54;
    static final int TABLET = 76;
    final int mWidth = MainApplication.isTablet ? TABLET : PHONE;

    public final int calculate(final Context ctx) {
        int totalWidth = ViewUtil.getScreenWidth(ctx) - ViewUtil.dpToPx(16);
        int itemWidth = ViewUtil.dpToPx(mWidth);
        int spacing = ViewUtil.dpToPx(4);
        int colCount = totalWidth / itemWidth;
        width = itemWidth;

        //获取有效的列数
        while (true) {
            if (colCount == 0) {
                colCount = 1;
                break;
            } else {
                int result = totalWidth - (itemWidth * colCount + spacing * (colCount - 1));
                if (result > 3) {
                    break;
                }
                colCount--;
            }
        }
        return colCount;
    }

    @Override
    protected void convert(BaseViewHolder holder, ZiLibDetailBean ziLibDetailBean) {
        if (ziLibDetailBean == null) {
            return;
        }

        ZiBean bean = ziLibDetailBean.getGlyph();
        if (bean == null) {
            return;
        }

        if (ziLibDetailBean.getNum() > 1) {
            holder.setGone(R.id.tv_num, false);
            holder.setText(R.id.tv_num, String.valueOf(ziLibDetailBean.getNum()));
            holder.setGone(R.id.line, false);
        } else {
            holder.setGone(R.id.tv_num, true);
            holder.setText(R.id.tv_num, "");
            holder.setGone(R.id.line, true);
        }

        if (TextUtils.isEmpty(bean.get_hanzi())) {
            holder.setText(R.id.tv_font, bean.get_key() + "");
        } else {
            holder.setText(R.id.tv_font, bean.get_hanzi() + "");
        }

        if (TextUtils.isEmpty(bean.getUrl())) {
            holder.setGone(R.id.iv_thumb_url, true);
            holder.setGone(R.id.tv_def, false);
            TextView textView = holder.getView(R.id.tv_def);
            ViewGroup.LayoutParams layoutParams = textView.getLayoutParams();
            layoutParams.width = width;
            layoutParams.height = width;
            textView.setLayoutParams(layoutParams);
            if (TextUtils.isEmpty(bean.get_hanzi())) {
                holder.setText(R.id.tv_def, bean.get_key() + "");
            } else {
                holder.setText(R.id.tv_def, bean.get_hanzi() + "");
            }
        } else {
            holder.setGone(R.id.iv_thumb_url, false);
            holder.setGone(R.id.tv_def, true);
            MyLoadingImageView myLoadingImageView = holder.getView(R.id.iv_thumb_url);
            ViewGroup.LayoutParams layoutParams = myLoadingImageView.getLayoutParams();
            layoutParams.width = width;
            layoutParams.height = width;
            myLoadingImageView.setLayoutParams(layoutParams);
            myLoadingImageView.setData(activity, bean.getUrlThumb(), -1);
        }

        if (TextUtils.isEmpty(bean.get_color_image())) {
            final View miView = holder.getView(R.id.miView);
            ViewGroup.LayoutParams layoutParams = miView.getLayoutParams();
            layoutParams.width = width;
            layoutParams.height = width;
            miView.setLayoutParams(layoutParams);
            holder.setVisible(R.id.miView, true);
        } else {
            holder.setVisible(R.id.miView, false);
        }
    }
}