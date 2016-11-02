package com.seniordesign.kwyjibo.database.restapi;

import com.seniordesign.kwyjibo.database.models.SessionInfo;
import com.seniordesign.kwyjibo.database.models.SoundClipInfo;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
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
            @Field("bpm") String bpm,
            @Field("userId") String userId,
            @Field("authToken") String authToken);

    @FormUrlEncoded
    @POST("api/AddUser.aspx")
    Call<SessionInfo> requestSignup(
            @Field("username") String username,
            @Field("email") String email,
            @Field("password") String password
    );

    @Multipart
    @POST("api/UploadSoundClipToStation.aspx")
    Call<SoundClipInfo> uploadSoundClip(
            @Part MultipartBody.Part soundClipFile,
            @Part("stationName") RequestBody stationName,
            @Part("clipName") RequestBody clipName,
            @Part("username") RequestBody username,
            @Part("location") RequestBody location,
            @Part("category") RequestBody category,
            @Part("userId") RequestBody userId,
            @Part("authToken") RequestBody authToken);
}
