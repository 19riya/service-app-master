package com.hrproject.RetrofitConfig;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient
{
    //   public static final String BASE_URL ="http://demoimg.com/website-d/service/UrbanJson/";

   public static final String BASE_URL="\n" + "http://thedruggist.co.in/service/UrbanJson/";

    public static Retrofit getClient()
    {
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(350, TimeUnit.SECONDS)
                .readTimeout(300, TimeUnit.SECONDS).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL).client(client)
                .addConverterFactory(GsonConverterFactory.create(new Gson())).build();
        return retrofit;
    }

}
