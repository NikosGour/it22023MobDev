package gr.hua.dit.nikosgourn.andoiddev22023;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.RequiresApi;

public class GPSReceiver extends BroadcastReceiver
{
    private static final String TAG = "GPSReceiver";
    
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context , Intent intent)
    {
        
        if (intent.getAction().equals("android.location.PROVIDERS_CHANGED"))
        {
            if (intent.getStringExtra(LocationManager.EXTRA_PROVIDER_NAME).equals(LocationManager.GPS_PROVIDER))
            {
                if (intent.getBooleanExtra(LocationManager.EXTRA_PROVIDER_ENABLED , false))
                {
                    Log.e(TAG , "GPS is enabled, starting location service");
                    Intent locationServiceIntent = new Intent(context , LocationService.class);
                    context.startForegroundService(locationServiceIntent);
                } else
                {
                    
                    Log.e(TAG, "GPS got disabled, stopping location service");
                    
                    Intent locationServiceIntent = new Intent(context , LocationService.class);
                    context.stopService(locationServiceIntent);
                }
                
            }
            
        }
        
        
    }
}
