package com.xiaopu.customer.activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.xiaopu.customer.ActivityBase;
import com.xiaopu.customer.R;
import com.xiaopu.customer.adapter.HeartrateAdapter;
import com.xiaopu.customer.adapter.OvulationAdapter;
import com.xiaopu.customer.adapter.PregnancyAdapter;
import com.xiaopu.customer.adapter.UrineAdapter;
import com.xiaopu.customer.adapter.WeightAdapter;
import com.xiaopu.customer.data.Feedback;
import com.xiaopu.customer.data.jsonresult.DetectionHeartrate;
import com.xiaopu.customer.data.jsonresult.DetectionOvulation;
import com.xiaopu.customer.data.jsonresult.DetectionPregnant;
import com.xiaopu.customer.data.jsonresult.DetectionUrine;
import com.xiaopu.customer.data.jsonresult.DetectionWeight;
import com.xiaopu.customer.utils.DateUtils;
import com.xiaopu.customer.utils.ToastUtils;
import com.xiaopu.customer.utils.http.HttpCallBack;
import com.xiaopu.customer.utils.http.HttpConstant;
import com.xiaopu.customer.utils.http.HttpResult;
import com.xiaopu.customer.utils.http.HttpUtils;
import com.xiaopu.customer.utils.security.Des;
import com.xiaopu.customer.view.LoadingView.LoadingView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MoreDataActivity extends ActivityBase {
    private static final String LOG_TAG = MoreDataActivity.class.getSimpleName();

    private Context mContext;
    private final static int HEARTRATE_MORE = 1;
    private final static int OVULATION_MORE = 2;
    private final static int PREGNANT_MORE = 3;
    private final static int URINE_MORE = 4;
    private final static int WEIGHT_MORE = 5;

    private ListView mListView;

    private PregnancyAdapter mPreAdapter;

    private HeartrateAdapter mHearAdapter;

    private WeightAdapter mWeightAdapter;

    private OvulationAdapter mOvuAdapter;

    private UrineAdapter mUrineAdapter;

    private TextView tv_start_time;

    private TextView tv_end_time;

    private Button btSearch;

    private LoadingView mLoadingView;

    private TextView tv_no_data;

    private TextView tv_loading_fail;

    private MyClick myClick;

    private Calendar c;

    private List<DetectionHeartrate> listHeartrate = new ArrayList<DetectionHeartrate>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_data);
        mContext = this;
        initView();
        initData();
    }

    private void initView() {
        mListView = (ListView) findViewById(R.id.lv_more_data);
        final int state = getIntent().getIntExtra("data_state", -1);
        switch (state) {
            case HEARTRATE_MORE:
                initActionBar("心电数据");
                break;
            case OVULATION_MORE:
                initActionBar("排卵数据");
                break;
            case PREGNANT_MORE:
                initActionBar("孕检数据");
                break;
            case WEIGHT_MORE:
                initActionBar("体重数据");
                break;
            case URINE_MORE:
                initActionBar("尿检数据");
                break;
        }
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (state == HEARTRATE_MORE) {
                    final DetectionHeartrate detectionHeartrate = listHeartrate.get(position);

                    Map<String, Object> maps = new HashMap<>();
                    maps.put("id", detectionHeartrate.getId());
                    HttpUtils.getInstantce().showSweetDialog();
                    HttpUtils.getInstantce().postWithHead(maps, HttpConstant.Url_getHearDetectionData, new HttpCallBack<String>() {
                        @Override
                        public void onSuccess(HttpResult result) {
                            HttpUtils.getInstantce().dismissSweetDialog();
                            String result_detail = (String) result.getData();
                            Intent mIntent = new Intent(mContext, HeartResultActivity.class);
                            mIntent.putExtra("heartData", result_detail);
                            mIntent.putExtra("result", detectionHeartrate.getChartUrl());
                            mIntent.putExtra("detectionResult", String.valueOf(detectionHeartrate.getHeartRate()));
                            startActivity(mIntent);
                        }

                        @Override
                        public void onFail(String msg) {

                        }
                    });

/*
                    HttpUtils.getInstantce().getHearDetectionData(Integer.valueOf(detectionHeartrate.getId() + ""), new HttpConstant.SampleJsonResultListener<Feedback>() {
                        @Override
                        public void onSuccess(Feedback jsonData) {
                            Intent mIntent = new Intent(mContext, HeartResultActivity.class);
                            mIntent.putExtra("heartData", jsonData.getData().toString());
                            mIntent.putExtra("result", detectionHeartrate.getChartUrl());
                            mIntent.putExtra("detectionResult", String.valueOf(detectionHeartrate.getHeartRate()));
                            startActivity(mIntent);
                        }

                        @Override
                        public void onFailure(Feedback jsonData) {
                        }
                    });
*/
                }
            }
        });

        tv_start_time = (TextView) findViewById(R.id.start_time);
        tv_end_time = (TextView) findViewById(R.id.tv_end_time);
        btSearch = (Button) findViewById(R.id.bt_search);
        tv_no_data = (TextView) findViewById(R.id.tv_no_data);
        tv_loading_fail = (TextView) findViewById(R.id.tv_loading_fail);
        mLoadingView = (LoadingView) findViewById(R.id.loading_view);

    }

    private void initData() {
        myClick = new MyClick();
        tv_start_time.setOnClickListener(myClick);
        tv_end_time.setOnClickListener(myClick);
        btSearch.setOnClickListener(myClick);
        tv_loading_fail.setOnClickListener(myClick);

        tv_start_time.setText(new SimpleDateFormat("yyyy-MM-dd")
                .format(new Date()));
        tv_end_time.setText(new SimpleDateFormat("yyyy-MM-dd")
                .format(new Date()));
    }


    private void toMoreData(String startDate, String endDate) {
        int state = getIntent().getIntExtra("data_state", -1);
        switch (state) {
            case HEARTRATE_MORE:
                heartrateDetectionData(startDate, endDate);
                break;
            case OVULATION_MORE:
                ovulationDetectionData(startDate, endDate);
                break;
            case PREGNANT_MORE:
                pregnantDetectionData(startDate, endDate);
                break;
            case WEIGHT_MORE:
                weightDetectionData(startDate, endDate);
                break;
            case URINE_MORE:
                urineDetectionData(startDate, endDate);
                break;
        }
    }

    public class MyClick implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.start_time:
                    String startDate[] = tv_start_time.getText().toString().split("-");
                    DatePickerDialog start_dateDialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            tv_start_time.setText(year + "-" + String.format("%02d", monthOfYear + 1) + "-" + String.format("%02d", dayOfMonth));
                        }
                    }, Integer.parseInt(startDate[0]), Integer.parseInt(startDate[1]) - 1, Integer.parseInt(startDate[2]));
                    start_dateDialog.setTitle("请选择查询起始日期");
                    start_dateDialog.getDatePicker().setCalendarViewShown(false);
                    start_dateDialog.show();
                    break;
                case R.id.tv_end_time:
                    String endDate[] = tv_end_time.getText().toString().split("-");
                    DatePickerDialog end_dateDialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            tv_end_time.setText(year + "-" + String.format("%02d", monthOfYear + 1) + "-" + String.format("%02d", dayOfMonth));
                        }
                    }, Integer.parseInt(endDate[0]), Integer.parseInt(endDate[1]) - 1, Integer.parseInt(endDate[2]));
                    end_dateDialog.setTitle("请选择查询截止日期");
                    end_dateDialog.getDatePicker().setCalendarViewShown(false);
                    end_dateDialog.show();
                    break;
                case R.id.bt_search:
                    if (DateUtils.compareDate(tv_start_time.getText().toString(), tv_end_time.getText().toString())) {
                        ToastUtils.showErrorMsg("开始时间不能大于结束时间");
                    } else {
                        resetView(mLoadingView);
                        toMoreData(tv_start_time.getText().toString(),
                                tv_end_time.getText().toString());
                    }
                    break;
                case R.id.tv_loading_fail:
                    if (DateUtils.compareDate(tv_start_time.getText().toString(), tv_end_time.getText().toString())) {
                        ToastUtils.showErrorMsg("开始时间不能大于结束时间");
                    } else {
                        resetView(mLoadingView);
                        toMoreData(tv_start_time.getText().toString(),
                                tv_end_time.getText().toString());
                    }
                    break;
            }
        }
    }

    /**
     * 心电数据获取
     */
    private void heartrateDetectionData(String startTime, String endTime) {

        Map<String, Object> maps = new HashMap<>();
        maps.put("startTime", startTime);
        maps.put("endTime", endTime);

        HttpUtils.getInstantce().postWithHead(maps, HttpConstant.Url_getHeartrateDataByTime, new HttpCallBack<List<DetectionHeartrate>>() {
            @Override
            public void onSuccess(HttpResult result) {
                List<DetectionHeartrate> detectionHeartrateList = (List<DetectionHeartrate>) result.getData();
                if (detectionHeartrateList.size() == 0) {
                    resetView(tv_no_data);
                    tv_no_data.setText("暂无数据");
                } else {
                    resetView(mListView);
                    listHeartrate.clear();
                    listHeartrate.addAll(detectionHeartrateList);
                    mHearAdapter = new HeartrateAdapter(listHeartrate, MoreDataActivity.this);
                    mListView.setAdapter(mHearAdapter);
                }

            }

            @Override
            public void onFail(String msg) {
                resetView(tv_loading_fail);
            }
        });

       /* HttpUtils.getInstantce().getHeartrateDataByTimeList(startTime, endTime, new HttpConstant.SampleJsonResultListener<Feedback<List<DetectionHeartrate>>>() {
            @Override
            public void onSuccess(Feedback<List<DetectionHeartrate>> jsonData) {
                if (jsonData.getData().size() == 0) {
                    resetView(tv_no_data);
                    tv_no_data.setText("暂无数据");
                } else {
                    resetView(mListView);
                    listHeartrate.clear();
                    listHeartrate.addAll(jsonData.getData());
                    mHearAdapter = new HeartrateAdapter(listHeartrate, MoreDataActivity.this);
                    mListView.setAdapter(mHearAdapter);
                }

            }

            @Override
            public void onFailure(Feedback<List<DetectionHeartrate>> jsonData) {
                if (jsonData != null && jsonData.getCode() == 404) {
                    resetView(tv_no_data);
                } else {
                    resetView(tv_loading_fail);
                }
            }
        });*/
    }

    /**
     * 排卵数据检测
     */
    private void ovulationDetectionData(String startTime, String endTime) {

        Map<String, Object> maps = new HashMap<>();
        maps.put("startTime", startTime);
        maps.put("endTime", endTime);
        HttpUtils.getInstantce().postWithHead(maps, HttpConstant.Url_getOvulationDataByTime, new HttpCallBack<List<DetectionOvulation>>() {
            @Override
            public void onSuccess(HttpResult result) {
                List<DetectionOvulation> detectionOvulationList = (List<DetectionOvulation>) result.getData();
                if (detectionOvulationList.size() == 0) {
                    resetView(tv_no_data);
                } else {
                    resetView(mListView);
                    mOvuAdapter = new OvulationAdapter(detectionOvulationList, MoreDataActivity.this);
                    mListView.setAdapter(mOvuAdapter);
                }

            }

            @Override
            public void onFail(String msg) {
                resetView(tv_loading_fail);
            }
        });

/*
        HttpUtils.getInstantce().getOvulationDataByTimeList(startTime, endTime, new HttpConstant.SampleJsonResultListener<Feedback<List<DetectionOvulation>>>() {
            @Override
            public void onSuccess(Feedback<List<DetectionOvulation>> jsonData) {
                resetView(mListView);
                mOvuAdapter = new OvulationAdapter(jsonData.getData(), MoreDataActivity.this);
                mListView.setAdapter(mOvuAdapter);

            }

            @Override
            public void onFailure(Feedback<List<DetectionOvulation>> jsonData) {
                if (jsonData != null && jsonData.getCode() == 404) {
                    resetView(tv_no_data);
                } else {
                    resetView(tv_loading_fail);
                }
            }
        });
*/
    }

    /**
     * 尿检数据检测
     */
    private void urineDetectionData(String startTime, String endTime) {
        Map<String, Object> maps = new HashMap<>();
        maps.put("startTime", startTime);
        maps.put("endTime", endTime);

        HttpUtils.getInstantce().postWithHead(maps, HttpConstant.Url_getUrineDataByTime, new HttpCallBack<List<String>>() {
            @Override
            public void onSuccess(HttpResult result) {
                try {
                    List<String> stringList = (List<String>) result.getData();
                    List<DetectionUrine> detectionUrineList = new ArrayList<>();
                    for (String str : stringList) {
                        DetectionUrine detectionUrine = HttpUtils.getInstantce().gson.fromJson(Des.decode(str), new TypeToken<DetectionUrine>() {
                        }.getType());
                        detectionUrineList.add(detectionUrine);
                    }
                    if (detectionUrineList.size() == 0) {
                        resetView(tv_no_data);
                    } else {
                        resetView(mListView);
                        mUrineAdapter = new UrineAdapter(detectionUrineList, MoreDataActivity.this);
                        mListView.setAdapter(mUrineAdapter);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(String msg) {
                resetView(tv_loading_fail);
            }
        });

        /*HttpUtils.getInstantce().getUrineDataByTimeList(startTime, endTime, new HttpConstant.SampleJsonResultListener<Feedback<List<DetectionUrine>>>() {
            @Override
            public void onSuccess(Feedback<List<DetectionUrine>> jsonData) {
                resetView(mListView);
                mUrineAdapter = new UrineAdapter(jsonData.getData(), MoreDataActivity.this);
                mListView.setAdapter(mUrineAdapter);
            }

            @Override
            public void onFailure(Feedback<List<DetectionUrine>> jsonData) {
                if (jsonData != null && jsonData.getCode() == 404) {
                    resetView(tv_no_data);
                } else {
                    resetView(tv_loading_fail);
                }
            }
        });*/
    }


    /**
     * 孕检数据检测
     */
    private void pregnantDetectionData(String startTime, String endTime) {

        Map<String, Object> maps = new HashMap<>();
        maps.put("startTime", startTime);
        maps.put("endTime", endTime);
        HttpUtils.getInstantce().postWithHead(maps, HttpConstant.Url_getPregnantDataByTime, new HttpCallBack<List<DetectionPregnant>>() {
            @Override
            public void onSuccess(HttpResult result) {
                List<DetectionPregnant> detectionPregnantList = (List<DetectionPregnant>) result.getData();
                if (detectionPregnantList.size() == 0) {
                    resetView(tv_no_data);
                } else {
                    resetView(mListView);
                    mPreAdapter = new PregnancyAdapter(detectionPregnantList, MoreDataActivity.this);
                    mListView.setAdapter(mPreAdapter);
                }

            }

            @Override
            public void onFail(String msg) {
                resetView(tv_loading_fail);
            }
        });

      /*  HttpUtils.getInstantce().getPregnantDataByTimeList(startTime, endTime, new HttpConstant.SampleJsonResultListener<Feedback<List<DetectionPregnant>>>() {
            @Override
            public void onSuccess(Feedback<List<DetectionPregnant>> jsonData) {
                resetView(mListView);
                mPreAdapter = new PregnancyAdapter(jsonData.getData(), MoreDataActivity.this);
                mListView.setAdapter(mPreAdapter);

            }

            @Override
            public void onFailure(Feedback<List<DetectionPregnant>> jsonData) {
                if (jsonData != null && jsonData.getCode() == 404) {
                    resetView(tv_no_data);
                } else {
                    resetView(tv_loading_fail);
                }
            }
        });*/
    }

    /**
     * 体重数据检测
     */
    private void weightDetectionData(String startTime, String endTime) {

        Map<String, Object> maps = new HashMap<>();
        maps.put("startTime", startTime);
        maps.put("endTime", endTime);
        HttpUtils.getInstantce().postWithHead(maps, HttpConstant.Url_getWeightDataByTime, new HttpCallBack<List<DetectionWeight>>() {
            @Override
            public void onSuccess(HttpResult result) {
                List<DetectionWeight> detectionWeightList = (List<DetectionWeight>) result.getData();
                if (detectionWeightList.size() == 0) {
                    resetView(tv_no_data);
                } else {
                    resetView(mListView);
                    mWeightAdapter = new WeightAdapter(detectionWeightList, mContext);
                    mListView.setAdapter(mWeightAdapter);
                }
            }

            @Override
            public void onFail(String msg) {
                resetView(tv_loading_fail);
            }
        });


/*
        HttpUtils.getInstantce().getWeightDataByTimeList(startTime, endTime, new HttpConstant.SampleJsonResultListener<Feedback<List<DetectionWeight>>>() {
            @Override
            public void onSuccess(Feedback<List<DetectionWeight>> jsonData) {

                if (jsonData.getData().size() == 0) {
                    resetView(tv_no_data);
                } else {
                    resetView(mListView);
                    mWeightAdapter = new WeightAdapter(jsonData.getData(), MoreDataActivity.this);
                    mListView.setAdapter(mWeightAdapter);
                }

            }

            @Override
            public void onFailure(Feedback<List<DetectionWeight>> jsonData) {
                resetView(tv_loading_fail);
            }
        });
*/
    }

    private void resetView(View v) {
        mListView.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.GONE);
        tv_loading_fail.setVisibility(View.GONE);
        tv_no_data.setVisibility(View.GONE);
        switch (v.getId()) {
            case R.id.lv_more_data:
                mListView.setVisibility(View.VISIBLE);
                break;
            case R.id.loading_view:
                mLoadingView.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_loading_fail:
                tv_loading_fail.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_no_data:
                tv_no_data.setVisibility(View.VISIBLE);
                break;
        }
    }
}
