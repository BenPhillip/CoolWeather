package com.example.gzp.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Ben on 2017/6/11.
 */

public class Basic {
    @SerializedName("city")
    public String cityName;
    @SerializedName("id")
    public String weatherId;
    //更新时间
    public  Update update;
    public class Update{
        @SerializedName("loc")
        public String updateTime;
    }
}
