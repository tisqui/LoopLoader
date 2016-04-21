package com.squirrel.looploader.services;

import com.google.gson.Gson;
import com.squirrel.looploader.BuildConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by squirrel on 4/9/16.
 */
public class ServiceGenerator {

//    public static final String API_BASE_URL = "http://192.168.169.48:8080";

    private static HttpLoggingInterceptor sInterceptor = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                                                        .readTimeout(240, TimeUnit.SECONDS)
                                                        .connectTimeout(30, TimeUnit.SECONDS);

        private static Retrofit.Builder builder =
                new Retrofit.Builder()
                        .baseUrl(BuildConfig.API_BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create(new Gson()));

        public static <S> S createService(Class<S> serviceClass) {
            Retrofit retrofit = builder.client(httpClient.addInterceptor(sInterceptor).build()).build();
            return retrofit.create(serviceClass);
        }
}
