package com.seniordesign.kwyjibo.restapi;

import com.seniordesign.kwyjibo.beans.SessionInfo;
import com.seniordesign.kwyjibo.beans.SoundClipInfo;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

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
    Call<SoundClipInfo> uploadSoundClip(
            @Field("stationName") String stationName,
            @Field("clipName") String clipName,
            @Field("username") String username,
            @Field("location") String location,
            @Field("category") String category,
            @Field("userId") String userId,
            @Field("authToken") String authToken);
}
