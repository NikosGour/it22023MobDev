package gr.hua.dit.nikosgourn.andoiddev22023;

import static android.location.LocationManager.GPS_PROVIDER;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import gr.hua.dit.nikosgourn.andoiddev22023.databinding.ActivityMapBinding;


public class MapActivity extends FragmentActivity implements OnMapReadyCallback
{
    private static final String             TAG          = "MapActivity";
    private static final int                REQUEST_CODE = 22023;
    private              GoogleMap          mMap;
    private              ActivityMapBinding binding;
    private              LocationManager    locationManager;
    Location location;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        binding = ActivityMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        int[] permission =
                new int[]{ ContextCompat.checkSelfPermission(this , Manifest.permission.ACCESS_FINE_LOCATION) , ContextCompat.checkSelfPermission(this , Manifest.permission.ACCESS_COARSE_LOCATION) };
        
        if (permission[0] == PackageManager.PERMISSION_DENIED ||
            permission[1] != PackageManager.PERMISSION_DENIED)
        {
            
            requestPermissions(new String[]{ Manifest.permission.ACCESS_FINE_LOCATION , Manifest.permission.ACCESS_COARSE_LOCATION } , REQUEST_CODE);
        } else
        {
            main();
        }
        
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
        
        LocationListener locationListener = new LocationListener(locationManager , mMap);
        
        locationManager.requestLocationUpdates(GPS_PROVIDER , 5000 , 50 , locationListener);
        location = locationListener.getCur_location();
  
        //        Circle c = mMap.addCircle(new CircleOptions().center(new LatLng(location.getLatitude() , location.getLongitude())).radius(100).strokeColor(Color.RED).strokeWidth(20));
        LatLng location_LatLng = new LatLng(location.getLatitude() , location.getLongitude());
        //        mMap.addMarker(new MarkerOptions().position(location_LatLng).title("Marker in current location"));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location_LatLng));
    }
    
    public boolean isGPSEnabled()
    {
        if (locationManager == null)
        {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }
        return locationManager.isProviderEnabled(GPS_PROVIDER);
    }
    
    private void turnGPSOn()
    {
    
    }
    
    private void main()
    {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        
        if (! isGPSEnabled())
        {
            turnGPSOn();
        }
        
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode , @NonNull String[] permissions ,
                                           @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode , permissions , grantResults);
        
        if (requestCode == REQUEST_CODE &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED &&
            grantResults[1] == PackageManager.PERMISSION_GRANTED)
        {
            main();
        } else
        {
            Log.e(TAG , "onRequestPermissionsResult: Permission Denied");
        }
    }
}
