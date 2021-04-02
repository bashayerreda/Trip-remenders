package com.example.tripremenders.models;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
public interface MapApi {
    @GET("json?key=AIzaSyCVOvMSNN18_AJKQjfKfoWKxsYNF5GNxK0")
    Call<ResponseFromMap> getDirections(@Query("origin") String origin , @Query("destination") String destination);
}
