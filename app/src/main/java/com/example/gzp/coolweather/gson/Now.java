package com.example.gzp.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Ben on 2017/6/11.
 */

public class Now {
    @SerializedName("tmp")
    public String temperature;   //温度
    @SerializedName("cond")
    public More more;
    public class More{
        @SerializedName("txt")
        public String info;  //天气类型
    }
}
