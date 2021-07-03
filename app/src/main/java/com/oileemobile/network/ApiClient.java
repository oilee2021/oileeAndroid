package com.oileemobile.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import com.oileemobile.utils.PrefManager;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    //public static final String BASE_URL = "https://oilee.ilogixdigital.com/api/";
    public static final String BASE_URL = "https://app.oileemobile.com/api/";
    private static ApiClient _client = null;

    private final Api _service;

    private ApiClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(interceptor);

        long defTimeout = 40;
        httpClient.connectTimeout(defTimeout, TimeUnit.SECONDS);
        httpClient.readTimeout(defTimeout, TimeUnit.SECONDS).writeTimeout(defTimeout, TimeUnit.SECONDS);
        httpClient.addInterceptor(chain -> {
            Request original = chain.request();
            Request request;
            Headers.Builder headersBuilder = new Headers.Builder();
            headersBuilder.add("Accept", "application/json");
            if(!PrefManager.getString(PrefManager.TOKEN).equals("")) {
                headersBuilder.add("Authorization", "Bearer " + PrefManager.getString(PrefManager.TOKEN));
            }
            request = original.newBuilder()
                    .headers(headersBuilder.build())
                    .method(original.method(), original.body()).build();
            return chain.proceed(request);
        });

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        OkHttpClient client = httpClient.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();
        _service = retrofit.create(Api.class);
    }

    public static Api current() {
        return getInstance().getService();
    }

    private static ApiClient getInstance() {
        if (_client == null) {
            _client = new ApiClient();
        }
        return _client;
    }

    private Api getService() {
        return _service;
    }
}
