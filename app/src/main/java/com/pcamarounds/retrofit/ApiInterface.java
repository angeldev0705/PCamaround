package com.pcamarounds.retrofit;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * Created by anupamchugh on 29/03/17.
 */

public interface ApiInterface {
   /* @GET("place/autocomplete/json?")
    Call<ResponseBody> doPlaces(@Query(value = "type", encoded = true) String type, @Query(value = "location", encoded = true) String location, @Query(value = "name", encoded = true) String name, @Query(value = "opennow", encoded = true) boolean opennow, @Query(value = "rankby", encoded = true) String rankby, @Query(value = "key", encoded = true) String key);*/

    @GET("place/autocomplete/json?")
    Call<ResponseBody> doPlaces(@QueryMap Map<String, String> options);
}
