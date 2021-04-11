package org.techtown.movieproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import org.techtown.movieproject.helper.NetworkStatus;

public class SplashActivity extends AppCompatActivity {
    private AlertDialog networkDialog;
    private Handler handler = new Handler();
    private long delay = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 네트워크 연결상태 점검
        int networkStatus = NetworkStatus.getConnectivity(getApplicationContext());

        if(networkStatus == NetworkStatus.TYPE_NOT_CONNECTED) {                         // 인터넷 연결안됨
            AlertDialog.Builder builder = new AlertDialog.Builder(this);         //android.R.style.Theme_DeviceDefault_Light_Dialog
            builder.setMessage("네트워크가 연결되지 않았습니다.\nWI-FI 또는 데이터를 활성화 해주세요.")
                    .setNegativeButton("다시 시도", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int networkStatus = NetworkStatus.getConnectivity(getApplicationContext());
                            if(networkStatus == NetworkStatus.TYPE_NOT_CONNECTED) {     // 인터넷 연결안됨
                                builder.show();
                            } else {
                                delay = 2500;
                            }
                        }
                    })
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            finish();
                        }
                    });

            networkDialog = builder.create();
            networkDialog.show();
        } else {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);

                    finish();       // 스플래쉬 화면이기 떄문에 백스택에서 제거
                }
            }, delay);
        }
    }
}