package gr.hua.dit.nikosgourn.andoiddev22023;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
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


@RequiresApi(api = Build.VERSION_CODES.Q)
public class MapActivity extends FragmentActivity implements OnMapReadyCallback
{
    private static final String               TAG = "MapActivity";
    public static        GoogleMap            mMap;
    private              ArrayList<PointBlob> points;
    private              ContentResolver      contentResolver;
    private              Intent               locationServiceIntent;
    
    
    /**
     * Helper class to associate a marker with a circle and its center(lat,lng)
     */
    private static class PointBlob
    {
        public Marker marker;
        public Circle circle;
        public LatLng cords;
    }
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        points = new ArrayList<>();
        ActivityMapBinding binding = ActivityMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        Button startButton = findViewById(R.id.start_button);
        Button cancelButton = findViewById(R.id.cancel_button);
        
        contentResolver = getContentResolver();
        locationServiceIntent = new Intent(this , LocationService.class);
        
        // region Buttons
        startButton.setOnClickListener(v -> {
            Log.d(TAG , "onCreate: " + "Start Button Clicked");
            new Thread(() -> {
                
                // If the service is running, stop it
                // we do this so we can refresh the session on the service
                // if we just use start foreground service on an already running service
                // then the onStartCommand method will not be called, therefore the session
                // will not be refreshed
                if (isLocationServiceRunning())
                {
                    Log.d(TAG , "onCreate: " + "Service is already running");
                    stopService(locationServiceIntent);
                }
                
                // Create a new session
                Uri uri = GeoPointProvider.CREATE_MAPS_SESSION_URI;
                contentResolver.insert(uri , null);
                
                // Get the session id from the previously created session
                uri = GeoPointProvider.GET_LATEST_MAPS_SESSIONS_URI;
                Cursor cursor = contentResolver.query(uri , null , null , null , null);
                cursor.moveToFirst();
                @SuppressLint("Range") int session_id = cursor.getInt(cursor.getColumnIndex(MapsSession.SESSION_ID));
                
                cursor.close();
                
                // Add every point to the database
                uri = GeoPointProvider.CREATE_GEO_POINT_URI;
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
        
        // endregion
        
        
        // Initialize the map
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null)
        {
            mapFragment.getMapAsync(this);
        } else
        {
            throw new AssertionError("Unreachable, mapFragment cannot be null");
        }
        
        
    }
    
    
    /**
     * @param latLng the coordinates of the point
     * @return a key value pair of a PointBlob object and the distance from the location if the point is already in the map, null otherwise
     */
    public AbstractMap.SimpleEntry<PointBlob, Double> distanceFromPoint(LatLng latLng)
    {
        if (points.size() == 0)
        {
            return null;
        }
        
        for (PointBlob blob : points)
        {
            LatLng point = blob.cords;
            
            double d = Utilities.distance_point_from_target(point , latLng);
            if (d < 100)
            {
                return new AbstractMap.SimpleEntry<>(blob , d);
            }
        }
        
        return null;
    }
    
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap)
    {
        // Google map settings
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        
        
        //zoom in on current location
        
        
        mMap.setOnMapLongClickListener(latLng -> {
            
            // if no point exists, add the point
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
            }
            // if there are points on the map
            else
            {
                AbstractMap.SimpleEntry<PointBlob, Double> distance_point =
                        distanceFromPoint(latLng);
                // if user clicked in the area of an existing point
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
                    
                }
                // if user clicked outside of an existing point
                else
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

