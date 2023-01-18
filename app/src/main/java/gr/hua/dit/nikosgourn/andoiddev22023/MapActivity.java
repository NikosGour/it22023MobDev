package gr.hua.dit.nikosgourn.andoiddev22023;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.AbstractMap;
import java.util.ArrayList;

import gr.hua.dit.nikosgourn.andoiddev22023.databinding.ActivityMapBinding;
import gr.hua.dit.nikosgourn.andoiddev22023.room.GeoPoint;
import gr.hua.dit.nikosgourn.andoiddev22023.room.MapsSession;


public class MapActivity extends FragmentActivity implements OnMapReadyCallback
{
    private static final String               TAG = "MapActivity";
    public static        GoogleMap            mMap;
    private              ActivityMapBinding   binding;
    private              ArrayList<PointBlob> points;
    private              Button               startButton;
    private              Button               cancelButton;
    private              ContentResolver      contentResolver;
    
    
    private LocationService locationService;
    private Intent          locationServiceIntent;
    
    
    private static class PointBlob
    {
        public Marker marker;
        public Circle circle;
        public LatLng cords;
    }
    
    
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        points  = new ArrayList<>();
        binding = ActivityMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    
        locationServiceIntent = new Intent(this , LocationService.class);
        

        
        startButton  = findViewById(R.id.start_button);
        cancelButton = findViewById(R.id.cancel_button);
        
        contentResolver = getContentResolver();
        
        startButton.setOnClickListener(v -> {
            Log.d(TAG , "onCreate: " + "Start Button Clicked");
            new Thread(() -> {
                
                if (isLocationServiceRunning())
                {
                    Log.d(TAG , "onCreate: " + "Service is already running");
                    stopService(locationServiceIntent);
                }
                
                
                
                
                Uri uri = Uri.parse(GeoPointProvider.CONTENT_URI + "/session/new");
                contentResolver.insert(uri , null);
                
                uri = Uri.parse(GeoPointProvider.CONTENT_URI + "/session");
                Cursor cursor = contentResolver.query(uri , null , null , null , null);
                
                cursor.moveToFirst();
                @SuppressLint("Range") int session_id =
                        cursor.getInt(cursor.getColumnIndex(MapsSession.SESSION_ID));
                
                uri = Uri.parse(GeoPointProvider.CONTENT_URI + "/points/new");
                
                for (PointBlob point : points)
                {
                    ContentValues cv = new ContentValues();
                    cv.put(GeoPoint.SESSION_ID , session_id);
                    cv.put(GeoPoint.LATITUDE , point.cords.latitude);
                    cv.put(GeoPoint.LONGITUDE , point.cords.longitude);
                    contentResolver.insert(uri , cv);
                }
                startForegroundService(locationServiceIntent);
                finish();
            }).start();
            
        });
        
        cancelButton.setOnClickListener(v -> {
            Log.d(TAG , "onCreate: " + "Cancel Button Clicked");
            finish();
            
        });
        
        
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        
        
    }
    
    
    @RequiresApi(api = Build.VERSION_CODES.N)
    public AbstractMap.SimpleEntry<PointBlob, Double> distanceFromPoint(LatLng latLng)
    {
        final double EARTH_RADIUS = 6371e3;
        Log.w(TAG , "distanceFromPoint: " + EARTH_RADIUS);
        if (points.size() == 0)
        {
            return null;
        }
        
        for (PointBlob blob : points)
        {
            LatLng point = blob.cords;
            
            double lat1 = Math.toRadians(point.latitude);
            double lat2 = Math.toRadians(latLng.latitude);
            double lon1 = Math.toRadians(point.longitude);
            double lon2 = Math.toRadians(latLng.longitude);
            double dLat = lat2 - lat1;
            double dLon = lon2 - lon1;
            
            double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                       Math.cos(lat1) * Math.cos(lat2) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
            double c = 2 * Math.atan2(Math.sqrt(a) , Math.sqrt(1 - a));
            double d = EARTH_RADIUS * c;
            Log.w(TAG , "distanceFromPoint: " + d);
            
            if (d < 100)
            {
                return new AbstractMap.SimpleEntry<>(blob , d);
            }
        }
        
        return null;
    }
    
    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        
        
        //zoom in on current location
        
        
        mMap.setOnMapLongClickListener(latLng -> {
            
            if (points.size() == 0)
            {
                
                Marker marker = mMap.addMarker(new MarkerOptions().position(latLng));
                Circle circle =
                        mMap.addCircle(new CircleOptions().center(latLng).radius(100).visible(true).strokeColor(Color.RED));
                
                PointBlob blob = new PointBlob();
                blob.marker = marker;
                blob.circle = circle;
                blob.cords  = latLng;
                
                
                points.add(blob);
            } else
            {
                AbstractMap.SimpleEntry<PointBlob, Double> distance_point =
                        distanceFromPoint(latLng);
                if (distance_point != null)
                {
                    
                    PointBlob blob = distance_point.getKey();
                    LatLng point = (LatLng) distance_point.getKey().cords;
                    double distance = (double) distance_point.getValue();
                    
                    Log.w(TAG , "Distance from point: " +
                                point.latitude +
                                " " +
                                point.longitude +
                                " is: " +
                                distance);
                    
                    blob.marker.remove();
                    blob.circle.remove();
                    points.remove(blob);
                    
                } else
                {
                    Marker marker = mMap.addMarker(new MarkerOptions().position(latLng));
                    Circle circle =
                            mMap.addCircle(new CircleOptions().center(latLng).radius(100).visible(true).strokeColor(Color.RED));
                    
                    PointBlob blob = new PointBlob();
                    blob.marker = marker;
                    blob.circle = circle;
                    blob.cords  = latLng;
                    points.add(blob);
                }
            }
            
            
        });
        
    }
    
    
    private boolean isLocationServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (LocationService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    
    
}

