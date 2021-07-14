package com.example.v_clone;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface PostApi {
    @Multipart
    @POST("/file/upload/")
    Call<ResponseBody> upload(
            @Part MultipartBody.Part file,
            @Part("") MultipartBody.Part Name,
            @Part("") MultipartBody.Part UserId,
            @Part("") MultipartBody.Part Text
    );
}
