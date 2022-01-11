package kr.co.nicevan.genotcbarcode.util;

import okhttp3.OkHttpClient;

public class HttpConnection {

    private OkHttpClient client;
    private static HttpConnection instance = new HttpConnection();
    public static HttpConnection getInstance() {
        return instance;
    }

    private HttpConnection() {this.client = new OkHttpClient();}



}
