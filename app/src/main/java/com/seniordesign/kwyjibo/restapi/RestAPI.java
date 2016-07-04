package com.seniordesign.kwyjibo.restapi;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.seniordesign.kwyjibo.activities.MainActivity;
import com.seniordesign.kwyjibo.beans.RadioStation;
import com.seniordesign.kwyjibo.beans.SessionInfo;
import com.seniordesign.kwyjibo.beans.SoundClipInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/*
 * This class provides us with a simple interface to the server. The Callback parameter is how
 * we handle the request after it has completed. onResponse() is the success condition. onFailed()
 * is the failure condition.
 */
public class RestAPI{

    private static final String API_BASE_URL = "http://motw.tech/";

    public static void requestLogin(String username, String password, Callback<SessionInfo> callback){
        GETRequest api = retrofit.create(GETRequest.class);
        final Call<SessionInfo> call = api.login(username, password);
        call.enqueue(callback);
    }

    public static void requestSignup(String username, String email, String password, Callback<SessionInfo> callback){
        POSTRequest api = retrofit.create(POSTRequest.class);
        final Call<SessionInfo> call = api.requestSignup(username, email, password);
        call.enqueue(callback);
    }

    public static void authenticateSession(String userId, String authToken, Callback<Boolean> callback){
        GETRequest api = retrofit.create(GETRequest.class);
        final Call<Boolean> call = api.requestAuthentication(userId, authToken);
        call.enqueue(callback);
    }

    public static void getStations(Callback<List<RadioStation>> callback){
        GETRequest api = retrofit.create(GETRequest.class);
        final Call<List<RadioStation>> call = api.requestStations();
        call.enqueue(callback);
    }

    public static void getStationSoundClips(String stationName, Callback<List<SoundClipInfo>> callback){
        GETRequest api = retrofit.create(GETRequest.class);
        final Call<List<SoundClipInfo>> call = api.requestSoundClips(stationName);
        call.enqueue(callback);
    }

    public static void getCategories(Callback<List<String>> callback){
        GETRequest api = retrofit.create(GETRequest.class);
        final Call<List<String>> call = api.requestCategories();
        call.enqueue(callback);

    }

    public static void createStation(RadioStation station, String userId, String authToken,
                                     Callback<Boolean> callback){
        POSTRequest api = retrofit.create(POSTRequest.class);
        final Call<Boolean> call = api.createStation(station.Name, station.CreatedBy, station.Genre,
                userId, authToken);
        call.enqueue(callback);
    }

    public static void uploadSoundClip(String clipFilepath, SoundClipInfo clipInfo, String stationName,
                                       String userId, String authToken, Callback<SoundClipInfo> callback) {
        POSTRequest api = retrofit.create(POSTRequest.class);

        File file = new File(clipFilepath);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part soundClipFile = MultipartBody.Part.createFormData(file.getName(), file.getName(), requestFile);

        RequestBody station= RequestBody.create(MediaType.parse("multipart/form-data"), stationName);
        RequestBody clipName = RequestBody.create(MediaType.parse("multipart/form-data"), clipInfo.Name);
        RequestBody username = RequestBody.create(MediaType.parse("multipart/form-data"), clipInfo.CreatedBy);
        RequestBody location = RequestBody.create(MediaType.parse("multipart/form-data"), clipInfo.Location);
        RequestBody category = RequestBody.create(MediaType.parse("multipart/form-data"), clipInfo.Category);
        RequestBody id = RequestBody.create(MediaType.parse("multipart/form-data"), userId);
        RequestBody token = RequestBody.create(MediaType.parse("multipart/form-data"), authToken);

        final Call<SoundClipInfo> call = api.uploadSoundClip(soundClipFile, station, clipName,
                username, location, category, id, token);
        call.enqueue(callback);
    }

    public static void getAllSoundClipInfo(Callback<List<SoundClipInfo>> callback){
        GETRequest api = retrofit.create(GETRequest.class);
        final Call<List<SoundClipInfo>> call = api.getAllSoundClipInfo();
        call.enqueue(callback);
    }

    public static void getSoundClip(String clipName, Callback<ResponseBody> callback){
        GETRequest api = retrofit.create(GETRequest.class);
        final Call<ResponseBody> call = api.getSoundClip(clipName);

        call.enqueue(callback);
    }

    private static final Retrofit retrofit = new Retrofit.Builder().baseUrl(API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();



}
