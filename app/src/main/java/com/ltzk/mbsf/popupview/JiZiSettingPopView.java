package com.ltzk.mbsf.popupview;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ltzk.mbsf.R;
import com.ltzk.mbsf.adapter.MenuAdapter;
import com.ltzk.mbsf.bean.JiziBean;
import com.ltzk.mbsf.bean.MenuItemBean;
import com.ltzk.mbsf.utils.ViewUtil;
import com.xujiaji.happybubble.BubbleDialog;

import java.util.ArrayList;
import java.util.List;

public class JiZiSettingPopView extends BubbleDialog {

    private AdapterView.OnItemClickListener mListener;

    public void setOnItemClickListener(AdapterView.OnItemClickListener l) {
        this.mListener = l;
    }

    public JiZiSettingPopView(Activity activity, JiziBean jiziBean) {
        super(activity);
        setTransParentBackground();
        setPosition(Position.BOTTOM);

        final MenuAdapter adapter = new MenuAdapter(activity);
        if (jiziBean != null) {
            boolean v = "V".equals(jiziBean._layout);
            boolean left = "R2L".equals(jiziBean._direction);
            List<MenuItemBean> listMenu = new ArrayList();
            listMenu.add(new MenuItemBean(R.mipmap.jizi_text_edit, "内容修改"));
            listMenu.add(new MenuItemBean(v ? R.mipmap.ic_jizi_layout_h : R.mipmap.ic_jizi_layout_v, v ? "横向排列" : "竖向排列"));
            listMenu.add(new MenuItemBean(left ? R.mipmap.ic_jizi_direction_right : R.mipmap.ic_jizi_direction_left, left ? "从左至右" : "从右至左"));
            listMenu.add(new MenuItemBean(R.mipmap.jizi_square, "清除选字"));
            adapter.setData(listMenu);
        } else {
            List<MenuItemBean> listMenu = new ArrayList();
            listMenu.add(new MenuItemBean(0, "空白集字"));
            listMenu.add(new MenuItemBean(0, "诗文集字"));
            listMenu.add(new MenuItemBean(0, "对联集字"));
            listMenu.add(new MenuItemBean(0, "书论集字"));
            adapter.setData(listMenu);
        }

        View rootView = LayoutInflater.from(activity).inflate(R.layout.widget_jizi_setting_popview, null);
        ListView listView = rootView.findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setVerticalScrollBarEnabled(false);
        listView.setDivider(activity.getResources().getDrawable(R.color.whiteSmoke));
        listView.setDividerHeight(ViewUtil.dpToPx(1));
        listView.setPadding(0, ViewUtil.dpToPx(2), 0, ViewUtil.dpToPx(2));
        addContentView(rootView);

        listView.setOnItemClickListener((AdapterView<?> adapterView, View view, int i, long l) -> {
            dismiss();
            if (mListener != null) {
                mListener.onItemClick(adapterView, view, i, l);
            }
        });
    }
}