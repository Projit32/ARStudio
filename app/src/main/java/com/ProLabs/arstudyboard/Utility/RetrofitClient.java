package com.ProLabs.arstudyboard.Utility;


import com.ProLabs.arstudyboard.Manager.URLManager;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    Retrofit retrofit;


    public RetrofitClient()
    {
        retrofit= new Retrofit
                .Builder()
                .baseUrl(URLManager.BaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }


    public Call<ArrayList<ItemList>> getItemListCall()
    {
        return retrofit.create(RequestInterface.class).getItemList();
    }

    public Call<ArrayList<ItemList>> getAnimatedItemListCall()
    {
        return retrofit.create(RequestInterface.class).getAnimatedItemList();
    }

}
