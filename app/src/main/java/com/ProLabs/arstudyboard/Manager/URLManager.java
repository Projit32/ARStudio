package com.ProLabs.arstudyboard.Manager;

public class URLManager {

    public static String BaseUrl="YOUR_DOMAIN_NAME";

    public static String getItemFolderUrl()
    {
        return BaseUrl+"/ARSR/objects/";
    }

    public static String getAnimatedItemFolderUrl()
    {
        return BaseUrl+"/ARSR/AnimatedObjects/";
    }



}
