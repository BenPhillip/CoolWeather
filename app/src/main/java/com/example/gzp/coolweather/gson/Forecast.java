package com.example.gzp.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Ben on 2017/6/11.
 * 未来一天的天气
 */

public class Forecast {
    //明天的日期
    public String date;
    @SerializedName("tmp")
    public Temperature temperature;
    @SerializedName("cond")
    public More more;

    /**
     * 最高温度和最低温度
     */
    public class Temperature{
        public String max;
        public String min;
    }

    /**
     * 天气类型
     */
    public class More{
        @SerializedName("txt_d")
        public String info;
    }
}
