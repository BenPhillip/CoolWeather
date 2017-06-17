package com.example.gzp.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Ben on 2017/6/11.
 */

/**
 * 舒适情况，洗车建议，运动建议
 */
public class Suggestion {
    @SerializedName("comf")
    public Comfort comfort;
    @SerializedName("cw")
    public CarWash carWash;
    public Sport sport;

    public class Comfort{
        @SerializedName("txt")
        public String info;
    }

    public class CarWash{
        @SerializedName("txt")
        public String info;
    }

    public class Sport{
        @SerializedName("txt")
        public String info;
    }
}
