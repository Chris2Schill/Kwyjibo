package com.seniordesign.kwyjibo.restapi;

import com.seniordesign.kwyjibo.beans.RadioStation;
import com.seniordesign.kwyjibo.beans.SessionInfo;
import com.seniordesign.kwyjibo.beans.SoundClipInfo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GETRequest {

    @GET("api/Login.aspx")
    Call<SessionInfo> login(@Query("username") String username, @Query("password") String password);

    @GET("api/AuthenticateUser.aspx")
    Call<Boolean> requestAuthentication(@Query("userId") String userId, @Query("authToken") String authToken);

    @GET("api/GetStations.aspx")
    Call<List<RadioStation>> requestStations();

    @GET("api/GetStationSoundClips.aspx")
    Call<List<SoundClipInfo>> requestSoundClips(@Query("stationName") String name);

    @GET("api/GetCategories.aspx")
    Call<List<String>> requestCategories();



}
