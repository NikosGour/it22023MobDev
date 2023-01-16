package gr.hua.dit.nikosgourn.andoiddev22023;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
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
import kotlin.NotImplementedError;


public class MapActivity extends FragmentActivity implements OnMapReadyCallback
{
    private static final String               TAG = "MapActivity";
    public static        GoogleMap            mMap;
    private              ActivityMapBinding   binding;
    private              ArrayList<PointBlob> points;
    private Button startButton;
    private Button cancelButton;
    
    
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
        points  = new ArrayList<>();
        binding = ActivityMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        startButton = findViewById(R.id.start_button);
        cancelButton = findViewById(R.id.cancel_button);
        
        startButton.setOnClickListener(v -> {
            Log.d(TAG , "onCreate: " + "Start Button Clicked");
            throw new NotImplementedError();
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
            
            if (d < 50)
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
                        mMap.addCircle(new CircleOptions().center(latLng).radius(50).visible(true).strokeColor(Color.RED));
                
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
                            mMap.addCircle(new CircleOptions().center(latLng).radius(50).visible(true).strokeColor(Color.RED));
                    
                    PointBlob blob = new PointBlob();
                    blob.marker = marker;
                    blob.circle = circle;
                    blob.cords  = latLng;
                    points.add(blob);
                }
            }
            
            
        });
        
        // Add a marker in Sydney and move the camera
        //        LatLng sydney = new LatLng(- 34 , 151);
        //        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        
        //        LocationListener locationListener = new LocationListener(locationManager , mMap);
        //
        //        locationManager.requestLocationUpdates(GPS_PROVIDER , 5000 , 50 , locationListener);
        //        if (! isGPSEnabled())
        //        {
        //        }
        
        
        //        Circle c =
        //                mMap.addCircle(new CircleOptions().center(new LatLng(location.getLatitude() , location.getLongitude())).radius(100).strokeColor(Color.RED).strokeWidth(20));
        //        LatLng location_LatLng = new LatLng(location.getLatitude() , location.getLongitude());
        //        mMap.addMarker(new MarkerOptions().position(location_LatLng).title("Marker in current location"));
        //        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        //        mMap.moveCamera(CameraUpdateFactory.newLatLng(location_LatLng));
        
        
    }
    
    
}

