package com.seniordesign.kwyjibo.restapi;

import com.seniordesign.kwyjibo.beans.SessionInfo;
import com.seniordesign.kwyjibo.beans.SoundClipInfo;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface POSTRequest {

    @FormUrlEncoded
    @POST("api/AddStation.aspx")
    Call<Boolean> createStation(
            @Field("stationName") String stationName,
            @Field("createdBy") String createdBy,
            @Field("genre") String genre,
            @Field("userId") String userId,
            @Field("authToken") String authToken);

    @FormUrlEncoded
    @POST("api/AddUser.aspx")
    Call<SessionInfo> requestSignup(
            @Field("username") String username,
            @Field("email") String email,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("api/AddSoundToStation.aspx")
    Call<SoundClipInfo> uploadSoundClipInfo(
            @Field("stationName") String stationName,
            @Field("clipName") String clipName,
            @Field("username") String username,
            @Field("location") String location,
            @Field("category") String category,
            @Field("userId") String userId,
            @Field("authToken") String authToken);

    @Multipart
    @POST("api/UploadSoundClip.aspx")
    Call<ResponseBody> uploadSoundClip(
            @Part("description") RequestBody description,
            @Part MultipartBody.Part file);
}
