package com.example.gzp.coolweather.gson;

/**
 * Created by Ben on 2017/6/11.
 */

public class AQI {
    public AQICity city;
    public class AQICity{
        public String aqi;
        public String pm25;
    }
}
