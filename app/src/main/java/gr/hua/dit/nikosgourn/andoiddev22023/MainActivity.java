package gr.hua.dit.nikosgourn.andoiddev22023;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;

import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

@RequiresApi(api = Build.VERSION_CODES.Q)
public class MainActivity extends AppCompatActivity
{
    private static final String   TAG          = "MainActivity";
    private static final int      REQUEST_CODE = 22023;
    private static final String[] PERMISSIONS  =
            new String[]{ Manifest.permission.ACCESS_FINE_LOCATION , Manifest.permission.ACCESS_BACKGROUND_LOCATION };
    
    private LocationService locationService;
    private boolean         isBound;
    private Intent          locationServiceIntent;
    Button select_boundaries, stop_searching, see_boundaries;
    
    
    private final ServiceConnection locationServiceConnection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName componentName , IBinder iBinder)
        {
            
            locationService = ((LocationService.LocationBinder) iBinder).getService();
            isBound         = true;
        }
        
        @Override
        public void onServiceDisconnected(ComponentName componentName)
        {
            isBound = false;
        }
    };
    
    @RequiresApi(api = Build.VERSION_CODES.S)
    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        select_boundaries = findViewById(R.id.selectBoundariesButton);
        stop_searching    = findViewById(R.id.stopSearchingButton);
        see_boundaries    = findViewById(R.id.seeBoundariesButton);
        
        BroadcastReceiver broadcastReceiver = new GPSReceiver();
        IntentFilter intentFilter = new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION);
        registerReceiver(broadcastReceiver , intentFilter);
        
        locationServiceIntent = new Intent(this , LocationService.class);
        requestPermissions();
        
        
        select_boundaries.setOnClickListener(view -> {
            startActivity(new Intent(this , MapActivity.class));
        });
        
        stop_searching.setOnClickListener(view -> {
            Intent locationServiceIntent = new Intent(this , LocationService.class);
            stopService(locationServiceIntent);
        });
        
        see_boundaries.setOnClickListener(view -> {
        
        
        });
        
        
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode , @NonNull String[] permissions ,
                                           @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode , permissions , grantResults);
        
        
        if (requestCode == REQUEST_CODE)
        {
            
            if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION))
            {
                Log.e(TAG , "Permission" + permissions[0] + " was " + grantResults[0]);
                
                requestPermissions();
                return;
            }
            
            
            if (permissions[0].equals(Manifest.permission.ACCESS_BACKGROUND_LOCATION))
            {
                Log.e(TAG , "Permission" + permissions[0] + " was " + grantResults[0]);
                
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    requestPermissions();
                } else
                {
                    var snackbar =
                            Snackbar.make(findViewById(android.R.id.content) , "Background Location permissions are required, please go to Settings -> Apps and change the location permission to 'Always'" , Snackbar.LENGTH_LONG);
                    View snackbarView = snackbar.getView();
                    TextView snackbarTextView =
                            (TextView) (snackbarView.findViewById(com.google.android.material.R.id.snackbar_text));
                    snackbarTextView.setMaxLines(3);
                    snackbar.show();
                }
                return;
                
            }
            
        }
    }
    
    private void requestPermissions()
    {
        int[] permission =
                new int[]{ ContextCompat.checkSelfPermission(this , Manifest.permission.ACCESS_FINE_LOCATION) , ContextCompat.checkSelfPermission(this , Manifest.permission.ACCESS_BACKGROUND_LOCATION) };
        
        
        if (permission[0] == PackageManager.PERMISSION_DENIED)
        {
            requestPermissions(new String[]{ Manifest.permission.ACCESS_FINE_LOCATION } , REQUEST_CODE);
            return;
        }
        
        if (permission[1] == PackageManager.PERMISSION_DENIED)
        {
            requestPermissions(new String[]{ Manifest.permission.ACCESS_BACKGROUND_LOCATION } , REQUEST_CODE);
            return;
        }
        
        startForegroundService(locationServiceIntent);
        bindService(locationServiceIntent , locationServiceConnection , Context.BIND_AUTO_CREATE);
        
    }
    
    @Override
    protected void onStop()
    {
        super.onStop();
        if (isBound)
        {
            unbindService(locationServiceConnection);
            isBound = false;
        }
        
    }
}