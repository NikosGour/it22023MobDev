package gr.hua.dit.nikosgourn.andoiddev22023;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
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
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.material.snackbar.Snackbar;

@RequiresApi(api = Build.VERSION_CODES.Q)
public class MainActivity extends AppCompatActivity
{
    private static final String   TAG          = "MainActivity";
    private static final int      REQUEST_CODE = 22023;
    private static final String[] PERMISSIONS  =
            new String[]{ Manifest.permission.ACCESS_FINE_LOCATION , Manifest.permission.ACCESS_COARSE_LOCATION , Manifest.permission.ACCESS_BACKGROUND_LOCATION };
    
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
        
        int[] permission =
                new int[]{ ContextCompat.checkSelfPermission(this , Manifest.permission.ACCESS_FINE_LOCATION) , ContextCompat.checkSelfPermission(this , Manifest.permission.ACCESS_COARSE_LOCATION) , ContextCompat.checkSelfPermission(this , Manifest.permission.ACCESS_BACKGROUND_LOCATION) };
        
        if (permission[0] == PackageManager.PERMISSION_DENIED ||
            permission[1] == PackageManager.PERMISSION_DENIED ||
            permission[2] == PackageManager.PERMISSION_DENIED)
        {
            requestPermissions(PERMISSIONS , REQUEST_CODE);
        } else
        {
            startService(locationServiceIntent);
            bindService(locationServiceIntent , locationServiceConnection , Context.BIND_AUTO_CREATE);
        }
        
        
        select_boundaries.setOnClickListener(view -> {
            //            int[] permission =
            //                    new int[]{ ContextCompat.checkSelfPermission(this , Manifest.permission.ACCESS_FINE_LOCATION) , ContextCompat.checkSelfPermission(this , Manifest.permission.ACCESS_COARSE_LOCATION) };
            //
            //            if (permission[0] == PackageManager.PERMISSION_DENIED ||
            //                permission[1] != PackageManager.PERMISSION_DENIED)
            //            {
            //
            //                requestPermissions(new String[]{ Manifest.permission.ACCESS_FINE_LOCATION , Manifest.permission.ACCESS_COARSE_LOCATION } , 22023);
            //            } else
            //            {
            
            
            //            }
            
            
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
        
        if (requestCode == REQUEST_CODE &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED &&
            grantResults[1] == PackageManager.PERMISSION_GRANTED &&
            grantResults[2] == PackageManager.PERMISSION_GRANTED)
        {
            startForegroundService(locationServiceIntent);
            bindService(locationServiceIntent , locationServiceConnection , Context.BIND_AUTO_CREATE);
        } else
        {
            Log.e(TAG , "onRequestPermissionsResult: Permission Denied");
            Snackbar.make(findViewById(android.R.id.content) , "Location permissions are required, please go to Settings -> Apps and change the permissions" , Snackbar.LENGTH_LONG).show();
        }
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