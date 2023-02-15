package gr.hua.dit.nikosgourn.androiddev22023;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import gr.hua.dit.nikosgourn.androiddev22023.databinding.ActivityResultsMapBinding;
import gr.hua.dit.nikosgourn.androiddev22023.room.EntranceExitGeoPoint;
import gr.hua.dit.nikosgourn.androiddev22023.room.GeoPoint;
import gr.hua.dit.nikosgourn.androiddev22023.room.MapsSession;

@SuppressLint("MissingPermission")
public class ResultsMapActivity extends FragmentActivity implements OnMapReadyCallback
{
    
    private GoogleMap                 mMap;
    private ContentResolver           contentResolver;
    
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    
        ActivityResultsMapBinding binding =
                ActivityResultsMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        contentResolver = getContentResolver();
    
        Button stop_restart_button = findViewById(R.id.stop_restart_button);
        Button main_menu_button = findViewById(R.id.main_menu_button);
        
        stop_restart_button.setOnClickListener(v -> {
            if (isLocationServiceRunning())
            {
                stopService(new Intent(this , LocationService.class));
                Toast.makeText(this , "Service Stopped" , Toast.LENGTH_SHORT).show();
            }
            else
            {
                startForegroundService(new Intent(this , LocationService.class));
                Toast.makeText(this , "Service Started" , Toast.LENGTH_SHORT).show();
            }
            
        });
        
        main_menu_button.setOnClickListener(v -> finish());
        
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null)
        {
            mapFragment.getMapAsync(this);
        }
        else
        {
            throw new AssertionError("Unreachable, mapFragment cannot be null");
        }
    }
    
    
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap)
    {
        //Google Map Settings
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
    
    
        setupCirclesAndEntranceExitMarkers();
    
    }
    
    private void setupCirclesAndEntranceExitMarkers()
    {
        
        new Thread(() -> {
            Looper.prepare();
            
            //Get the last session id
            Uri uri = GeoPointProvider.GET_ACTIVE_MAPS_SESSIONS_URI;
            Cursor cursor = contentResolver.query(uri , null , null , null , null);
            
            //If there is no active session
            if (cursor == null)
            {
                Toast.makeText(this , "There is no Active Session" , Toast.LENGTH_LONG).show();
                finish();
                return;
            }
            
            cursor.moveToFirst();
            @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(MapsSession.SESSION_ID));
            cursor.close();
            
            
            // Get all the geofence points
            uri = Uri.withAppendedPath(GeoPointProvider.GET_ALL_GEO_POINTS_URI_ADD_SESSION_ID ,
                                          id +
                                          "");
            cursor =
                    contentResolver.query(uri , null , null , new String[]{ String.valueOf(id) } , null);
            
            if (cursor != null)
            {
                cursor.moveToFirst();
                int count = 1;
                while (! cursor.isAfterLast())
                {
                    @SuppressLint("Range") double lat =
                            cursor.getDouble(cursor.getColumnIndex(GeoPoint.LATITUDE));
                    @SuppressLint("Range") double lng =
                            cursor.getDouble(cursor.getColumnIndex(GeoPoint.LONGITUDE));
                    LatLng latLng = new LatLng(lat , lng);
                    
                    // This is needed to be passed into the UI thread otherwise it wont work
                    int finalCount = count;
                    ResultsMapActivity.this.runOnUiThread(() -> {
                        mMap.addMarker(new MarkerOptions().position(latLng).title("GeoFencePoint : "+finalCount));
                        mMap.addCircle(new CircleOptions().center(latLng).radius(100).visible(true).strokeColor(Color.RED));
                    });
                    cursor.moveToNext();
                    count++;
                }
                cursor.close();
            }
            
            // Get all the entrance and exit points
            uri = Uri.withAppendedPath(GeoPointProvider.GET_ALL_ENTRANCE_EXIT_GEO_POINTS_URI_ADD_SESSION_ID,
                                       id +"");
            cursor = contentResolver.query(uri, null, null, null, null);
            
            if (cursor != null)
            {
                cursor.moveToFirst();
                int entrance_count = 1;
                int exit_count = 1;
                while (! cursor.isAfterLast())
                {
                    @SuppressLint("Range") double lat =
                            cursor.getDouble(cursor.getColumnIndex(GeoPoint.LATITUDE));
                    @SuppressLint("Range") double lng =
                            cursor.getDouble(cursor.getColumnIndex(GeoPoint.LONGITUDE));
                    LatLng latLng = new LatLng(lat , lng);
                    
                    int finalCount;
                    String finalTitle;
                    String finalColor;
                    
                    @SuppressLint("Range") boolean isEntrance = Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(EntranceExitGeoPoint.IS_ENTRANCE)));
                    if (isEntrance)
                    {
                        finalCount = entrance_count;
                        finalTitle = "Entrance N° ";
                        finalColor = "#37eb34";
                        entrance_count++;
                    }
                    else
                    {
                        finalCount = exit_count;
                        finalTitle = "Exit N° ";
                        finalColor = "#0404b5";
                        exit_count++;
                    }
                    ResultsMapActivity.this.runOnUiThread(() -> mMap.addMarker(new MarkerOptions().position(latLng).title(finalTitle + finalCount).icon(getMarkerIcon(finalColor))));
                    cursor.moveToNext();
                    
                }
                cursor.close();
            }
            
        }).start();
    }

    
    // https://stackoverflow.com/a/33036461/13250408
    public BitmapDescriptor getMarkerIcon(String color) {
        float[] hsv = new float[3];
        Color.colorToHSV(Color.parseColor(color), hsv);
        return BitmapDescriptorFactory.defaultMarker(hsv[0]);
    }
    
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private boolean isLocationServiceRunning()
    {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
        {
            if (LocationService.class.getName().equals(service.service.getClassName()))
            {
                return true;
            }
        }
        return false;
    }
}
