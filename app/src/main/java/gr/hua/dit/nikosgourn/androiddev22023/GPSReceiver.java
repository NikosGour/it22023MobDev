package gr.hua.dit.nikosgourn.androiddev22023;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

/**
 * Broadcast receiver for GPS events.
 */
public class GPSReceiver extends BroadcastReceiver
{
    private static final String TAG = "GPSReceiver";
    
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onReceive(Context context , Intent intent)
    {
        
        if (intent.getAction().equals("android.location.PROVIDERS_CHANGED"))
        {
            // If the provider changed was GPS
            if (intent.getStringExtra(LocationManager.EXTRA_PROVIDER_NAME).equals(LocationManager.GPS_PROVIDER))
            {
                
                // If the GPS is enabled
                if (intent.getBooleanExtra(LocationManager.EXTRA_PROVIDER_ENABLED , false))
                {
                    Log.e(TAG , "GPS is enabled, starting location service");
                    Intent locationServiceIntent = new Intent(context , LocationService.class);
                    context.startForegroundService(locationServiceIntent);
                }
                // If the GPS is disabled
                else
                {
                    
                    Log.e(TAG, "GPS got disabled, stopping location service");
                    
                    Intent locationServiceIntent = new Intent(context , LocationService.class);
                    context.stopService(locationServiceIntent);
                }
                
            }
            
        }
        
        
    }
}
