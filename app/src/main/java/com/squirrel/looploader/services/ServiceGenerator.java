package com.squirrel.looploader.services;

import com.squirrel.looploader.BuildConfig;

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
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        private static Retrofit.Builder builder =
                new Retrofit.Builder()
                        .baseUrl(BuildConfig.API_BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create());

        public static <S> S createService(Class<S> serviceClass) {
            Retrofit retrofit = builder.client(httpClient.addInterceptor(sInterceptor).build()).build();
            return retrofit.create(serviceClass);
        }
}
