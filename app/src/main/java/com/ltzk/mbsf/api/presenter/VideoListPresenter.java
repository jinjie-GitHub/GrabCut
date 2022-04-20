package com.ltzk.mbsf.api.presenter;

import com.ltzk.mbsf.api.Constan;
import com.ltzk.mbsf.api.FileRequestBody;
import com.ltzk.mbsf.api.view.VideoListView;
import com.ltzk.mbsf.base.BasePresenterImp;
import com.ltzk.mbsf.bean.DetailsBean;
import com.ltzk.mbsf.bean.RequestBean;
import com.ltzk.mbsf.bean.ResponseData;
import com.ltzk.mbsf.bean.VideoListBean;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by JinJie on 2021/4/13
 */
public class VideoListPresenter extends BasePresenterImp<VideoListView> {

    @Override
    public void requestSuccess(ResponseData callBack, String flag) {
        if (Constan.response_sussess == callBack.getStat()) {
            if (flag.equals("glyphs")) {
                List<DetailsBean> bean = (List<DetailsBean>) callBack.getData();
                view.glyphs(bean);
            } else if (flag.equals("query")) {
                view.loadDataSuccess((VideoListBean) callBack.getData());
            } else if (flag.equals("upload")) {
                view.upload((VideoListBean.Videos) callBack.getData());
            }
        } else {
            view.loadDataError(callBack.getError().getTitle() + ":"
                    + callBack.getError().getMessage());
        }
    }

    /*public void glyphs(String zid, int page) {
        final RequestBean requestBean = new RequestBean();
        requestBean.addParams("zid", zid);
        requestBean.addParams("page", page);
        modelImpl.getDataFromHttp(serviceApi.glyphs(requestBean.getParams()), this, "glyphs", false);
    }*/

    /**
     * orderby: hot, date, author
     */
    public void glyph_video_query(String gid, String zid, String fid, String uid, String orderby, int loaded) {
        final RequestBean requestBean = new RequestBean();
        requestBean.addParams("gid", gid);
        requestBean.addParams("zid", zid);
        requestBean.addParams("fid", fid);
        requestBean.addParams("uid", uid);
        requestBean.addParams("orderby", orderby);
        requestBean.addParams("loaded", loaded);
        modelImpl.getDataFromHttp(serviceApi.glyph_video_query(requestBean.getParams()), this, "query", true);
    }

    public void glyph_video_publish(String vid) {
        final RequestBean requestBean = new RequestBean();
        requestBean.addParams("vid", vid);
        modelImpl.getDataFromHttp(serviceApi.glyph_video_publish(requestBean.getParams()), this, "publish", false);
    }

    public void glyph_video_unpublish(String vid) {
        final RequestBean requestBean = new RequestBean();
        requestBean.addParams("vid", vid);
        modelImpl.getDataFromHttp(serviceApi.glyph_video_unpublish(requestBean.getParams()), this, "unpublish", false);
    }

    public void glyph_video_private(String vid) {
        final RequestBean requestBean = new RequestBean();
        requestBean.addParams("vid", vid);
        modelImpl.getDataFromHttp(serviceApi.glyph_video_private(requestBean.getParams()), this, "private", false);
    }

    public void glyph_video_public(String vid) {
        final RequestBean requestBean = new RequestBean();
        requestBean.addParams("vid", vid);
        modelImpl.getDataFromHttp(serviceApi.glyph_video_public(requestBean.getParams()), this, "public", false);
    }

    public void glyph_video_delete(String vid) {
        final RequestBean requestBean = new RequestBean();
        requestBean.addParams("vid", vid);
        modelImpl.getDataFromHttp(serviceApi.glyph_video_delete(requestBean.getParams()), this, "delete", false);
    }

    public void glyph_video_upload(String gid, String fmt, String path, FileRequestBody.Callback callback) {
        final RequestBean requestBean = new RequestBean();
        requestBean.addParams("gid", gid);
        requestBean.addParams("fmt", fmt);

        //RequestBody pb = RequestBody.create(MediaType.parse("text/plain"), requestBean.getParams());
        //RequestBody vb = RequestBody.create(MediaType.parse("application/octet-stream"), bytes);

        final File file = new File(path);
        RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
        MultipartBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("p", requestBean.getParams())
                .addFormDataPart("video", file.getName(), fileBody)
                .build();

        FileRequestBody fileRequestBody = new FileRequestBody(body, callback);
        modelImpl.getDataFromHttp(serviceApi.glyph_video_upload(fileRequestBody), this, "upload", true);
    }
}