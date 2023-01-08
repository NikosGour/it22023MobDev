package gr.hua.dit.nikosgourn.andoiddev22023;

import static android.location.LocationManager.GPS_PROVIDER;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.internal.IGoogleMapDelegate;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;

import java.io.Serializable;

import gr.hua.dit.nikosgourn.andoiddev22023.databinding.ActivityMapBinding;


public class MapActivity extends FragmentActivity implements OnMapReadyCallback
{
    private static final String             TAG          = "MapActivity";
    public static        GoogleMap          mMap;
    private              ActivityMapBinding binding;


   
    
  
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        binding = ActivityMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        

        
    }
    
    private void main()
    {
        
        
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    
    
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
        
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

