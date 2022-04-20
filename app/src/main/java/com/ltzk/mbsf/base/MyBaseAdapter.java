package com.ltzk.mbsf.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by Administrator on 2015/10/9.
 */
public class MyBaseAdapter extends BaseAdapter {


    public Activity activity;
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        return null;
    }


    protected ProgressDialog progressDialog;

    /**
     * 显示提示框
     */
    protected void showProgressDialog(String msg, Activity activity) {

        if (progressDialog == null) {
            progressDialog = new ProgressDialog(activity);
        }

        this.progressDialog.setMessage(msg);
        this.progressDialog.setCanceledOnTouchOutside(false);
        this.progressDialog.show();
    }

    /**
     * 关闭提示框
     */
    protected void closeProgressDialog() {
        if (progressDialog != null)
            progressDialog.dismiss();
    }


    protected List list;
    public void setData(List list) {
        if (list != null) {
            this.list = list;
        }else{
            this.list.clear();
        }
    }

    public void addData(List list) {
        if (list != null) {
            this.list.addAll(list);
        }
    }
}
