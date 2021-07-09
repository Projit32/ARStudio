package com.ProLabs.arstudyboard.Manager;

public class URLManager {

    public static String StableChannelUrl="YOUR_DOMAIN_NAME";
    public static String DevChannelUrl="";

    public static String BaseUrl=StableChannelUrl;
    public static Boolean isDevChannel=false;

    public static void toggleChannel()
    {
            isDevChannel = !isDevChannel;
            BaseUrl = (isDevChannel) ? DevChannelUrl : StableChannelUrl;
    }

    public static String getItemFolderUrl()
    {
        return BaseUrl+"/ARSR/objects/";
    }

    public static String getAnimatedItemFolderUrl()
    {
        return BaseUrl+"/ARSR/AnimatedObjects/";
    }

    public static void clearDevChannelURL() { DevChannelUrl=StableChannelUrl; }

    public static void resetDevChannelUrl()
    {
        DevChannelUrl=StableChannelUrl;
    }



}
