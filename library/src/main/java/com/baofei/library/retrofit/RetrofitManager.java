package com.baofei.library.retrofit;

import android.content.Context;

import java.util.HashMap;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by micheal@baofei.com on 2016/9/5.
 */
public class RetrofitManager {

    public static RetrofitManager mManager = new RetrofitManager();

    public static OkHttpClient mClient;

    private Retrofit retrofit;

    public String mBaseUrl;
    public HashMap<String, String> mHeader;

    private int mRetrofitCode;
    private Context mContext;

    private RetrofitManager() {
    }

    public static RetrofitManager getInstance() {
        return mManager;
    }

    public void init(Context context, String url) {
        mBaseUrl = url;
        mClient = createClient();
        mContext = context.getApplicationContext();
        createRetrofit();
    }


    private OkHttpClient createClient() {
        return new OkHttpClient.Builder().build();
    }

    private void createRetrofit() {
        retrofit = new Retrofit.Builder()
                .baseUrl(mBaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(mClient)
                .build();
        mRetrofitCode = retrofit.hashCode();
    }


    public synchronized Retrofit getRetrofit() {
        return retrofit;
    }

    public int getRetrofitCode() {
        return mRetrofitCode;
    }

    public synchronized void resetRetrofit(String url) {
        mBaseUrl = url;
        mClient = null;
        mClient = createClient();
        retrofit = null;
        createRetrofit();
    }

}

