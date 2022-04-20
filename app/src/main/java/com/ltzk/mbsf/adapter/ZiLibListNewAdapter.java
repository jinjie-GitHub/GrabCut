package com.ltzk.mbsf.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ltzk.mbsf.MainApplication;
import com.ltzk.mbsf.R;
import com.ltzk.mbsf.R2;
import com.ltzk.mbsf.bean.ZilibBean;
import com.ltzk.mbsf.utils.ViewUtil;
import com.ltzk.mbsf.utils.XClickUtil;
import com.ltzk.mbsf.widget.MyLoadingImageView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 描述：
 * 作者： on 2019-5-16 20:27
 * 邮箱：499629556@qq.com
 */
public class ZiLibListNewAdapter extends RecyclerView.Adapter<ZiLibListNewAdapter.ViewHolder> {

    int width;
    Activity activity;
    CallBack mCallBack;
    List<ZilibBean> list;

    private Runnable runnable;
    private boolean isEdit = false;
    private HashSet<String> set = new HashSet<>();

    public void setRunnable(Runnable r) {
        runnable = r;
    }

    public final void setEdit(boolean edit) {
        isEdit = edit;
        if (!edit) {
            set.clear();
        }
        notifyDataSetChanged();
    }

    public HashSet<String> getSet() {
        return set;
    }

    public List<ZilibBean> getList() {
        return list;
    }

    public void setList(List<ZilibBean> list) {
        this.list = new ArrayList<>(list);
    }

    public ZiLibListNewAdapter(Activity activity, CallBack callBack) {
        this.activity = activity;
        mCallBack = callBack;
    }

    @Override
    public ZiLibListNewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_zilib_my, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ZiLibListNewAdapter.ViewHolder holder, final int position) {
        holder.setPosition(position);

        ZilibBean bean = list.get(position);

        if (TextUtils.isEmpty(bean.get_author())) {
            holder.author.setText("");
            holder.author_line.setVisibility(View.GONE);
        } else {
            String name = bean.get_author();
            holder.author.setText(name.length() > 3 ? name.substring(0, 3) : name);
            holder.author_line.setVisibility(View.VISIBLE);
        }

        //设置标题
        holder.title.setText(bean.get_title());
        //设置图片
        setUrlPic(bean.get_thumbs(), holder, position);

        if (isEdit) {
            holder.ll_check.post(() -> {
                ViewGroup.LayoutParams lp = holder.ll_check.getLayoutParams();
                //lp.height = holder.tl.getMeasuredHeight() + holder.lay.getMeasuredHeight();
                lp.height = holder.tl.getMeasuredHeight();
                holder.ll_check.setLayoutParams(lp);
            });
            holder.ll_check.setVisibility(View.VISIBLE);
            if (set.contains(bean.get_id())) {
                holder.cb.setBackgroundResource(R.drawable.shape_solid_blue);
            } else {
                holder.cb.setBackgroundResource(R.drawable.shape_solid_gray);
            }
        } else {
            holder.ll_check.setVisibility(View.GONE);
        }

        holder.ll_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!set.contains(bean.get_id())) {
                    set.add(bean.get_id());
                    holder.cb.setBackgroundResource(R.drawable.shape_solid_blue);
                } else {
                    set.remove(bean.get_id());
                    holder.cb.setBackgroundResource(R.drawable.shape_solid_gray);
                }
                if (runnable != null) {
                    runnable.run();
                }
            }
        });
    }

    private void setImagePic(MyLoadingImageView imageView, int index, List<String> listUrl) {
        imageView.setTextData("");
        if (index < listUrl.size()) {
            final String url = listUrl.get(index);
            if (url.startsWith("https") || url.startsWith("http")) {
                imageView.setData(activity, url, R.color.whiteSmoke, R.color.whiteSmoke);
            } else {
                imageView.setTextData(url);
            }
        } else {
            imageView.setData(activity, "", R.color.whiteSmoke, R.color.whiteSmoke);
        }
    }

    private void setUrlPic(List<String> listUrl, ViewHolder holder, int postion) {
        if (listUrl == null) {
            listUrl = new ArrayList<>();
        }
        setImagePic(holder.iv_thumb_url0, 0, listUrl);
        setImagePic(holder.iv_thumb_url1, 1, listUrl);
        setImagePic(holder.iv_thumb_url2, 2, listUrl);
        setImagePic(holder.iv_thumb_url3, 3, listUrl);
        setImagePic(holder.iv_thumb_url4, 4, listUrl);
        setImagePic(holder.iv_thumb_url5, 5, listUrl);
        setImagePic(holder.iv_thumb_url6, 6, listUrl);
        setImagePic(holder.iv_thumb_url7, 7, listUrl);
        setImagePic(holder.iv_thumb_url8, 8, listUrl);
        setImagePic(holder.iv_thumb_url9, 9, listUrl);
        setImagePic(holder.iv_thumb_url10, 10, listUrl);
        setImagePic(holder.iv_thumb_url11, 11, listUrl);
        setImagePic(holder.iv_thumb_url12, 12, listUrl);
        setImagePic(holder.iv_thumb_url13, 13, listUrl);
        setImagePic(holder.iv_thumb_url14, 14, listUrl);
        setImagePic(holder.iv_thumb_url15, 15, listUrl);
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    /**
     * 计算列数
     */
    static final String TAG = "ZiLibListAdapter";
    static final int PHONE = 108;
    static final int TABLET = 108;
    final int mWidth = MainApplication.isTablet ? TABLET : PHONE;
    public final int calculate(final Context ctx) {
        int totalWidth = ViewUtil.getScreenWidth(ctx) - ViewUtil.dpToPx(16);
        int itemWidth = ViewUtil.dpToPx(mWidth);
        int spacing = ViewUtil.dpToPx(4);
        int colCount = totalWidth / itemWidth;
        width = itemWidth / 4;

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

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R2.id.iv_thumb_url0)
        MyLoadingImageView iv_thumb_url0;
        @BindView(R2.id.iv_thumb_url1)
        MyLoadingImageView iv_thumb_url1;
        @BindView(R2.id.iv_thumb_url2)
        MyLoadingImageView iv_thumb_url2;
        @BindView(R2.id.iv_thumb_url3)
        MyLoadingImageView iv_thumb_url3;

        @BindView(R2.id.iv_thumb_url4)
        MyLoadingImageView iv_thumb_url4;
        @BindView(R2.id.iv_thumb_url5)
        MyLoadingImageView iv_thumb_url5;
        @BindView(R2.id.iv_thumb_url6)
        MyLoadingImageView iv_thumb_url6;
        @BindView(R2.id.iv_thumb_url7)
        MyLoadingImageView iv_thumb_url7;

        @BindView(R2.id.iv_thumb_url8)
        MyLoadingImageView iv_thumb_url8;
        @BindView(R2.id.iv_thumb_url9)
        MyLoadingImageView iv_thumb_url9;
        @BindView(R2.id.iv_thumb_url10)
        MyLoadingImageView iv_thumb_url10;
        @BindView(R2.id.iv_thumb_url11)
        MyLoadingImageView iv_thumb_url11;

        @BindView(R2.id.iv_thumb_url12)
        MyLoadingImageView iv_thumb_url12;
        @BindView(R2.id.iv_thumb_url13)
        MyLoadingImageView iv_thumb_url13;
        @BindView(R2.id.iv_thumb_url14)
        MyLoadingImageView iv_thumb_url14;
        @BindView(R2.id.iv_thumb_url15)
        MyLoadingImageView iv_thumb_url15;

        @BindView(R2.id.title)
        TextView title;
        @BindView(R2.id.lay)
        LinearLayout lay;

        @BindView(R.id.author)
        TextView author;
        @BindView(R.id.author_line)
        View author_line;

        @BindView(R.id.tl)
        View tl;
        @BindView(R.id.ll_check)
        View ll_check;
        @BindView(R2.id.cb)
        ImageView cb;

        private int position;
        public void setPosition(int position) {
            this.position = position;
        }

        private void setMyLayoutParam(MyLoadingImageView view, int width, int height) {
            view.setOnClickListener(mClickListener);
            TableRow.LayoutParams layoutParam = (TableRow.LayoutParams) view.getLayoutParams();
            layoutParam.width = width;
            layoutParam.height = height;
            view.setLayoutParams(layoutParam);
        }

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            setMyLayoutParam(iv_thumb_url0, width, width);
            setMyLayoutParam(iv_thumb_url1, width, width);
            setMyLayoutParam(iv_thumb_url2, width, width);
            setMyLayoutParam(iv_thumb_url3, width, width);
            setMyLayoutParam(iv_thumb_url4, width, width);
            setMyLayoutParam(iv_thumb_url5, width, width);
            setMyLayoutParam(iv_thumb_url6, width, width);
            setMyLayoutParam(iv_thumb_url7, width, width);
            setMyLayoutParam(iv_thumb_url8, width, width);
            setMyLayoutParam(iv_thumb_url9, width, width);
            setMyLayoutParam(iv_thumb_url10, width, width);
            setMyLayoutParam(iv_thumb_url11, width, width);
            setMyLayoutParam(iv_thumb_url12, width, width);
            setMyLayoutParam(iv_thumb_url13, width, width);
            setMyLayoutParam(iv_thumb_url14, width, width);
            setMyLayoutParam(iv_thumb_url15, width, width);

            //点击名称 跳转到修改名称界面
            lay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (XClickUtil.isFastDoubleClick(v)) {
                        return;
                    }
                    mCallBack.onItemClick(list.get(position));
                }
            });
        }

        private View.OnClickListener mClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallBack.onItemClick(list.get(position));
            }
        };
    }

    public interface CallBack {
        void onItemClick(ZilibBean zilibBean);
    }
}
