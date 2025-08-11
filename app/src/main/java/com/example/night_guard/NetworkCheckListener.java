package com.example.night_guard;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

public class NetworkCheckListener extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        if(!isInternetConnected(context)){
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View checkWifiDialog = LayoutInflater.from(context).inflate(R.layout.check_wifi_dialog,null);
            builder.setView(checkWifiDialog);

            Button btn_retryConnection = checkWifiDialog.findViewById(R.id.btn_retryConnection);

            AlertDialog dialog = builder.create();
            dialog.show();
            dialog.setCancelable(false);

            dialog.getWindow().setGravity(Gravity.CENTER);

            btn_retryConnection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    onReceive(context,intent);
                }
            });
        }
    }

    private static boolean isInternetConnected(Context context){
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        if(connectivityManager != null){
            return connectivityManager.getActiveNetworkInfo()!=null && connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();
        }
        return false;
    }
}
