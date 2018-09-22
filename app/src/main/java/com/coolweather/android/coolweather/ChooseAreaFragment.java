package com.coolweather.android.coolweather;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.coolweather.android.coolweather.db.City;
import com.coolweather.android.coolweather.db.County;
import com.coolweather.android.coolweather.db.Province;
import com.coolweather.android.coolweather.util.HttpUtil;
import com.coolweather.android.coolweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChooseAreaFragment extends Fragment{
    public final static int LEVEL_PROVINCE=0;
    public final static int LEVEL_CITY=1;
    public final static int LEVEL_COUNTY=2;

    private ProgressDialog progressDialog;
    private TextView titletext;
    private Button backbutton;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();

    /*
    省列表
     */
    private List<Province> provincelist;
    /*
    市列表
     */
    private List<City> citylist;
    /*
    县列表
     */
    private List<County> countylist;
    /*
    选择的省
     */
    private Province selectProvince;
    /*
    选择的市
     */
    private City selectCity;
    /*
    当前选择的级别
     */
    private int currentLevel;

    @Override
    public View onCreateView( LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.choose_area,container,false);
        titletext=view.findViewById(R.id.title_text);
        backbutton=view.findViewById(R.id.back_button);
        listView=view.findViewById(R.id.list_view);
        adapter=new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_activated_1,dataList);
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated( Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //给listview增加点击事件的监听
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (currentLevel == LEVEL_PROVINCE){
                    selectProvince = provincelist.get(i);
                    queryCities();
                }else if (currentLevel == LEVEL_CITY){
                    selectCity = citylist.get(i);
                    queryCounties();
                }
            }
        });


        //点击返回按钮
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentLevel == LEVEL_COUNTY){
                    queryCities();
                }else if(currentLevel == LEVEL_CITY){
                    queryProvinces();
                }
            }
        });

        //默认加载省份得到数据
        queryProvinces();
    }

    /*
    查询全国县份，首先在数据库中查询，再进行联网在服务器查询
     */
    private void queryCounties() {
        titletext.setText(selectCity.getCityName());
        backbutton.setVisibility(View.VISIBLE);

        //在数据库中查询省级数据并赋值给省的集合
        countylist = DataSupport.findAll(County.class);
        if (countylist.size()>0){
            dataList.clear();
            for (County county : countylist){
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            //作用从头开始加载listview中的数据
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        }else {
            int provinceCode = selectProvince.getProvinceCode();
            int cityCode = selectCity.getCityCode();
            String address="http://guolin.tech/api/china/"+provinceCode+"/"+cityCode;
            queryfromServer(address,"county");
        }
    }

    /*
    查询全国市份，首先在数据库中查询，再进行联网在服务器查询
     */
    private void queryCities() {
        titletext.setText(selectProvince.getProvinceName());
        backbutton.setVisibility(View.VISIBLE);

        //在数据库中查询省级数据并赋值给省的集合
        citylist = DataSupport.findAll(City.class);
        if (citylist.size()>0){
            dataList.clear();
            for (City city : citylist){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            //作用从头开始加载listview中的数据
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        }else {
            int provinceCode = selectProvince.getProvinceCode();
            String address="http://guolin.tech/api/china/"+provinceCode;
            queryfromServer(address,"city");
        }
    }

    /*
    查询全国省份，首先在数据库中查询，再进行联网在服务器查询
     */
    private void queryProvinces() {
        titletext.setText("中国");
        backbutton.setVisibility(View.GONE);

        //在数据库中查询省级数据并赋值给省的集合
        provincelist = DataSupport.findAll(Province.class);
        if (provincelist.size()>0){
            dataList.clear();
            for (Province province : provincelist){
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            //作用从头开始加载listview中的数据
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        }else {
            String address="http://guolin.tech/api/china";
            queryfromServer(address,"province");
        }
    }
    /*
        根据传入的地址和类型在服务器查询省市县的数据
         */
    private void queryfromServer(String address , final String type){
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText=response.body().toString();
                boolean result = false;
                if ("province".equals(type)){
                    Utility.handleProvinceResponse(responseText);
                }else if ("city".equals(type)){
                    Utility.handleCityResponse(responseText,selectProvince.getId());
                }else if ("county".equals(type)){
                    Utility.handleCountyResponse(responseText,selectCity.getId());
                }

                if (result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)){
                                queryProvinces();
                            }else if ("city".equals(type)){
                                queryCities();
                            }else if ("county".equals(type)){
                                queryCounties();
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                //通过runOnUiThread方法货到主线程处理逻辑
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(),"不好意思哦！加载失败喇。",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /*
    联网的时候显示进度条
     */
    private void showProgressDialog(){
        if (progressDialog == null){
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在进行网络加载数据,请客官稍等...");
            progressDialog.setCanceledOnTouchOutside(false);//不可以取消
        }
        progressDialog.show();//显示进度条
    }

    /*
    联网完毕后关闭进度条
     */
    private void closeProgressDialog(){
        if (progressDialog!=null){
            progressDialog.dismiss();
        }
    }
}
