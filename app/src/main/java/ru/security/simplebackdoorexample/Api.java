package ru.security.simplebackdoorexample;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

public interface Api {

    @POST("/mybackdoor/")
    Observable<Response<ResponseBody>> postData(@Body JSONObject body);

}
