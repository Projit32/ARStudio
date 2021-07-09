package com.ProLabs.arstudyboard.Utility;


import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface RequestInterface {
    @GET("/ARSR/objects.php")
    Call<ArrayList<ItemList>> getItemList();

    @GET("/ARSR/AnimatedObjects.php")
    Call<ArrayList<ItemList>> getAnimatedItemList();


}
