package gr.hua.dit.nikosgourn.andoiddev22023;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.room.Room;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;

import android.database.Cursor;
import android.database.MatrixCursor;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import gr.hua.dit.nikosgourn.andoiddev22023.room.AppDatabase;
import gr.hua.dit.nikosgourn.andoiddev22023.room.GeoPoint;
import gr.hua.dit.nikosgourn.andoiddev22023.room.MapsSession;

@RequiresApi(api = Build.VERSION_CODES.Q)
public class MainActivity extends AppCompatActivity
{
    private static final String   TAG          = "MainActivity";
    private static final int      REQUEST_CODE = 22023;

    ContentResolver contentResolver;
    
 
    
    @RequiresApi(api = Build.VERSION_CODES.S)
    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Button select_boundaries = findViewById(R.id.selectBoundariesButton);
        Button stop_searching    = findViewById(R.id.stopSearchingButton);
        Button see_boundaries    = findViewById(R.id.seeBoundariesButton);
        
        BroadcastReceiver broadcastReceiver = new GPSReceiver();
        IntentFilter intentFilter = new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION);
        registerReceiver(broadcastReceiver , intentFilter);
        
        contentResolver = getContentResolver();
        
        requestPermissions();
        
        // region Buttons
        select_boundaries.setOnClickListener(view -> startActivity(new Intent(this , MapActivity.class)));
        
        stop_searching.setOnClickListener(view -> {
            Intent locationServiceIntent = new Intent(this , LocationService.class);
            stopService(locationServiceIntent);
        });
        
        see_boundaries.setOnClickListener(view -> startActivity(new Intent(this , ResultsMapActivity.class)));
        // endregion
        
        
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode , @NonNull String[] permissions , @NonNull int[] grantResults)
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
                    // If background location is not granted, then the app will not be able to receive location updates when the app is not in the foreground.
                    // We cannot request background location again, so we ask the user to turn on location in device settings.
                    var snackbar = Snackbar.make(findViewById(android.R.id.content) , "Background Location permissions are required, please go to Settings -> Apps and change the location permission to 'Always'" , Snackbar.LENGTH_LONG);
                    View snackbarView = snackbar.getView();
                    TextView snackbarTextView = (TextView) (snackbarView.findViewById(com.google.android.material.R.id.snackbar_text));
                    snackbarTextView.setMaxLines(3);
                    snackbar.show();
                }
    
            }
            
        }
    }
    
    private void requestPermissions()
    {
        int[] permission = new int[]{ ContextCompat.checkSelfPermission(this , Manifest.permission.ACCESS_FINE_LOCATION) , ContextCompat.checkSelfPermission(this , Manifest.permission.ACCESS_BACKGROUND_LOCATION) };
        
        
        if (permission[0] == PackageManager.PERMISSION_DENIED)
        {
            requestPermissions(new String[]{ Manifest.permission.ACCESS_FINE_LOCATION } , REQUEST_CODE);
            return;
        }
        
        if (permission[1] == PackageManager.PERMISSION_DENIED)
        {
            requestPermissions(new String[]{ Manifest.permission.ACCESS_BACKGROUND_LOCATION } , REQUEST_CODE);
        }
    }
}