package com.tbilisi.bus;

import android.app.Application;
import android.content.Context;
import android.util.Log;


import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.Volley;

/**
 * Created by nick on 9/14/13.
 */
public class A extends Application {

    private static Context mContext;
//    private static DefaultHttpClient mDefaultHttpClient;
    private static RequestQueue mRequestQueue;

    @Override
    public void onCreate() {
        super.onCreate();
         setUp();
    }

//    private void setUpHttpClient() {
//        BasicHttpParams mHttpParams = new BasicHttpParams();
//
//        // Set the timeout in milliseconds until a connection is established.
//        // The default value is zero, that means the timeout is not used.
//        int timeoutConnection = 15000;
//        HttpConnectionParams.setConnectionTimeout(mHttpParams, timeoutConnection);
//        // Set the default socket timeout (SO_TIMEOUT)
//        // in milliseconds which is the timeout for waiting for data.
//        int timeoutSocket = 20000;
//        HttpConnectionParams.setSoTimeout(mHttpParams, timeoutSocket);
//
//        SchemeRegistry registry = new SchemeRegistry();
//        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
//        final SSLSocketFactory sslSocketFactory = SSLSocketFactory.getSocketFactory();
//        sslSocketFactory.setHostnameVerifier(SSLSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
//        registry.register(new Scheme("https", sslSocketFactory, 443));
//
//        ClientConnectionManager cm = new ThreadSafeClientConnManager(mHttpParams, registry);
//        mDefaultHttpClient = new DefaultHttpClient(cm, mHttpParams);
//    }

    protected void setUp() {
        mContext = getApplicationContext();
        // set Basic Http Params
//        setUpHttpClient();
//        mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext(), new HttpClientStack(mDefaultHttpClient));
        mRequestQueue = Volley.newRequestQueue(this);
    }

    public static RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    public static Context getContext() {
        return mContext;
    }

    public static void log(Object string){
        Log.d("BUS",String.valueOf(string));
    }

}
