package com.ProLabs.arstudyboard.Utility;


import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.POST;

public interface RequestInterface {
    @POST("/ARSR/objects.php")
    Call<ArrayList<ItemList>> getItemList();

    @POST("/ARSR/AnimatedObjects.php")
    Call<ArrayList<ItemList>> getAnimatedItemList();


}
