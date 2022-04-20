package com.ltzk.mbsf.api;




import com.ltzk.mbsf.bean.FileBean;
import com.ltzk.mbsf.bean.ResponseData;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;


public interface UploadService {
    @Multipart
    @POST("upload_file")
    Observable<ResponseData<FileBean>> uploadFile(@PartMap Map<String, RequestBody> params);


}
