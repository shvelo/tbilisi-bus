package com.tbilisi.bus;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.webkit.WebView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

/**
 * Created by nick on 9/14/13.
 */
public class ScheduleActivity extends Activity{
    public static final String STOP_ID_KEY = "stopId";
    private static final String API =
            "http://transit.ttc.com.ge/pts-portal-services/servlet/stopArrivalTimesServlet?stopId=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        if (getIntent().getExtras() == null){
            A.log("getIntent().getExtras() == null");
            return;
        }
        String stopId = getIntent().getExtras().getString(STOP_ID_KEY);
        if (stopId == null){
            A.log("stopId == null");
            return;
        }
        final WebView wvSchedule = (WebView) findViewById(R.id.wvSchedule);
        String url = API + stopId;
        A.getRequestQueue().add(new StringRequest(url,new Response.Listener<String>() {
            @Override
            public void onResponse(String html) {
                String mime = "text/html";
                String encoding = "utf-8";
                wvSchedule.getSettings().setJavaScriptEnabled(true);
                wvSchedule.loadDataWithBaseURL(null, html, mime, encoding, null);
                A.log(html);
                //add to web views
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                A.log("onErrorResponse");
            }
        }));

    }

}
