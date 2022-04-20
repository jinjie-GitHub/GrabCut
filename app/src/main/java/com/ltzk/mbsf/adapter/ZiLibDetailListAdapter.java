package com.ltzk.mbsf.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.ltzk.mbsf.MainApplication;
import com.ltzk.mbsf.R;
import com.ltzk.mbsf.activity.GlyphDetailActivity;
import com.ltzk.mbsf.bean.DetailsBean;
import com.ltzk.mbsf.bean.ZiBean;
import com.ltzk.mbsf.utils.ViewUtil;
import com.ltzk.mbsf.widget.MyLoadingImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * update
 */
public class ZiLibDetailListAdapter extends BaseQuickAdapter<ZiBean, BaseViewHolder> {

    Activity activity;
    int width;

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public ZiLibDetailListAdapter(Activity activity) {
        super(R.layout.adapter_zi_lib_detail_item);
        this.activity = activity;
    }

    /**
     * 计算列数
     */
    static final String TAG = "king";
    static final int PHONE = 108;
    static final int TABLET = 108;
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
        Log.d(TAG, "colCount=" + colCount);
        return colCount;
    }

    @Override
    protected void convert(BaseViewHolder holder, ZiBean ziBean) {
        if (ziBean == null) {
            return;
        }

        holder.setText(R.id.tv_name, ziBean.get_from() + "");
        holder.setText(R.id.tv_author, ziBean.get_author() + "");

        final View.OnClickListener mClickListener = v -> {
            final List<DetailsBean> list = new ArrayList<>();
            for (ZiBean glyphsListBean : getData()) {
                if (!TextUtils.isEmpty(glyphsListBean.get_id())) {
                    list.add(DetailsBean.newDetails(glyphsListBean.get_id(), glyphsListBean.get_hanzi()));
                }
            }
            GlyphDetailActivity.safeStart(activity, ziBean.get_id(), list);
        };

        if (TextUtils.isEmpty(ziBean.get_clear_image())) {
            holder.setGone(R.id.iv_edit, false);
            holder.setGone(R.id.line, false);
            holder.getView(R.id.iv_edit).setOnClickListener(mClickListener);
        } else {
            holder.setGone(R.id.iv_edit, true);
            holder.setGone(R.id.line, true);
        }
        holder.getView(R.id.tv_name).setOnClickListener(mClickListener);
        MyLoadingImageView myLoadingImageView = holder.getView(R.id.iv_thumb_url);
        ViewGroup.LayoutParams layoutParams = myLoadingImageView.getLayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        layoutParams.height = width;
        myLoadingImageView.setLayoutParams(layoutParams);

        if (TextUtils.isEmpty(ziBean.get_color_image())) {
            View miView = holder.getView(R.id.miView);
            miView.setLayoutParams(layoutParams);
            holder.setVisible(R.id.miView, true);
        } else {
            holder.setVisible(R.id.miView, false);
        }

        myLoadingImageView.setData(activity, ziBean.getUrlThumb(), -1);
    }
}