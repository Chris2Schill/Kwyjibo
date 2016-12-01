package com.seniordesign.kwyjibo.database.restapi;

import com.seniordesign.kwyjibo.database.models.RadioStation;
import com.seniordesign.kwyjibo.database.models.SessionInfo;
import com.seniordesign.kwyjibo.database.models.SoundClipInfo;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GETRequest {

    @GET("api/Login.aspx")
    Call<SessionInfo> login(@Query("username") String username, @Query("password") String password);

    @GET("api/AuthenticateUser.aspx")
    Call<Boolean> requestAuthentication(@Query("userId") int userId, @Query("authToken") String authToken);

    @GET("api/GetStations.aspx")
    Call<List<RadioStation>> requestStations();

    @GET("api/GetStationSoundClips.aspx")
    Call<List<SoundClipInfo>> requestSoundClips(@Query("stationId") int id);

    @GET("api/GetCategories.aspx")
    Call<List<String>> requestCategories();

    @GET("api/GetAllSoundClipInfo.aspx")
    Call<List<SoundClipInfo>> getAllSoundClipInfo();

    @GET("api/GetSoundClip.aspx")
    Call<ResponseBody> getSoundClip(@Query("clipId") int clipId);

    @GET("api/GetStationSong.aspx")
    Call<ResponseBody> getStationSong(@Query("stationId") int stationId);


}
