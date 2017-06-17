package com.example.gzp.coolweather;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gzp.coolweather.db.City;
import com.example.gzp.coolweather.db.County;
import com.example.gzp.coolweather.db.Province;
import com.example.gzp.coolweather.util.HttpUtil;
import com.example.gzp.coolweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


/**
 * Created by Ben on 2017/6/11.
 */

public class ChooseAreaFragment extends Fragment {
    public static final String TAG = "Fragment";
    public static final int LEVEL_PROVINCE=0;
    public static final int LEVEL_CITY=1;
    public static final int LEVEL_COUNTY =2;
    private static final String BASE_URL = "http://guolin.tech/api/china";
    private static final String PROVINCE = "province";
    private static final String CITY = "city";
    private static final String COUNTY = "county";
    private ProgressDialog mProgressDialog;
    private TextView mTitile;
    private Button mBackButton;
    private ListView mListView;
    private ArrayAdapter<String > adapter;
    private List<String> dataList = new ArrayList<>();
    /**
     * 省市县列表
     */
    private List<Province>mProvinceList;
    private List<City>mCityList;
    private List<County>mCountyList;
    /**
     * 选中的省、市、当前级别
     */
    private Province selectedProvince;
    private City selectedCity;
    private int currentLevel;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area,container,false);
        mTitile = (TextView) view.findViewById(R.id.title_text);
        mBackButton = (Button) view.findViewById(R.id.back_button);
        mListView = (ListView) view.findViewById(R.id.list_view);
        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, dataList);
        mListView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = mProvinceList.get(position);
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = mCityList.get(position);
                    queryCounties();
                } else if (currentLevel == LEVEL_COUNTY) {
                    String weatherId = mCountyList.get(position).getWeatherId();
                    Intent intent=new Intent(getActivity(),WeatherActivity.class);
                    intent.putExtra(WeatherActivity.WEATHER_ID, weatherId);
                    startActivity(intent);
                    getActivity().finish();

                }
            }
        });
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLevel == LEVEL_COUNTY) {
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    queryProvinces();
                }
            }
        });
        queryProvinces();
    }

    /**
     * 查询全国所有的省，优先从数据库查询，如果没有查询到再去服务器上查询
     */
    private void queryProvinces() {
        mTitile.setText(R.string.country);
        mBackButton.setVisibility(View.GONE);
        mProvinceList = DataSupport.findAll(Province.class);
        if (mProvinceList.size() > 0) {
            dataList.clear();
            for (Province province : mProvinceList) {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            mListView.setSelection(0);
            currentLevel=LEVEL_PROVINCE;
        }else{
            String address=BASE_URL;
            queryFromServer(address, PROVINCE);
        }
    }

    private void queryCities() {
        mTitile.setText(selectedProvince.getProvinceName());
        mBackButton.setVisibility(View.VISIBLE);
        mCityList = DataSupport.where("provinceid=?", String.valueOf(selectedProvince.getId())).find(City.class);
        if (mCityList.size() > 0) {
            dataList.clear();
            for (City city:mCityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            mListView.setSelection(0);
            currentLevel = LEVEL_CITY;
        }else{
            int provinceCode=selectedProvince.getProvinceCode();
            String address=BASE_URL+"/"+provinceCode;
            queryFromServer(address, CITY);
        }
    }

    private void queryCounties() {
        mTitile.setText(selectedCity.getCityName());
        mBackButton.setVisibility(View.VISIBLE);
        mCountyList = DataSupport.where("cityid=?", String.valueOf(selectedCity.getId())).find(County.class);
        if (mCountyList.size() > 0) {
            dataList.clear();
            for (County county :
                    mCountyList) {
                dataList.add(county.getCountyName());
            }
            Log.d(TAG, "queryCounties: "+dataList.get(0));
            adapter.notifyDataSetChanged();
            mListView.setSelection(0);
            currentLevel= LEVEL_COUNTY;
        }else {
            int provinceCode=selectedProvince.getProvinceCode();
            int cityCode=selectedCity.getCityCode();
            String address=BASE_URL+"/"+provinceCode+"/"+cityCode;
            queryFromServer(address, COUNTY);
        }
    }

    private void queryFromServer(String address, final String type) {
        showProgressDialog();
        HttpUtil.sendOkhttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(),R.string.loading_failed,Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText=response.body().string();
                boolean result=false;
                if (PROVINCE.equals(type)) {
                    result = Utility.handleProvinceResponse(responseText);
                }else if (CITY.equals(type)){
                    result = Utility.handleCityResponse(responseText, selectedProvince.getId());
                } else if (COUNTY.equals(type)) {
                    result = Utility.handleCountyResponse(responseText, selectedCity.getId());
                }

                if (result) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            //重新从数据库读取列表
                            if (PROVINCE.equals(type)) {
                                queryProvinces();
                            } else if (CITY.equals(type)) {
                                queryCities();
                            } else if (COUNTY.equals(type)) {
                                queryCounties();
                            }
                        }
                    });
                }

            }
        });
    }


    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setCanceledOnTouchOutside(false);
        }
        mProgressDialog.show();
    }

    private void closeProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }
}
