package org.techtown.movieproject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkStatus {
    // 상수
    public static final int TYPE_MOBILE = 1;
    public static final int TYPE_WIFI = 2;
    public static final int TYPE_NOT_CONNECTED = 3;

    public static int getConnectivity(Context context) {
        ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();

        if(info != null) {
            int type = info.getType();

            if(type == ConnectivityManager.TYPE_MOBILE) {
                return TYPE_MOBILE;
            }else if(type == ConnectivityManager.TYPE_WIFI) {
                return TYPE_WIFI;
            }
        }

        return TYPE_NOT_CONNECTED;
    }
}
