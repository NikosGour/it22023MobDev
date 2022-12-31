package gr.hua.dit.nikosgourn.andoiddev22023;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationRequest;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity
{
    
    Button select_boundaries, stop_searching, see_boundaries;
    
    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        select_boundaries = findViewById(R.id.selectBoundariesButton);
        stop_searching    = findViewById(R.id.stopSearchingButton);
        see_boundaries    = findViewById(R.id.seeBoundariesButton);
    
    
        FusedLocationProviderClient
                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        
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
                startActivity(new Intent(this , MapActivity.class));
//            }
            
        });
        
        stop_searching.setOnClickListener(view ->
                                          {
                                              Log.e("MainActivity" , MapActivity.locationService.isGPSEnabled() + "");
                                          });
        see_boundaries.setOnClickListener(view ->
                                          {
                                              if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                                              {
                                                  LocationRequest builder = new LocationRequest.Builder(5000)
                                                          .setMinUpdateDistanceMeters(0)
                                                          .setMinUpdateIntervalMillis(5000)
                                                          .build();
                                              }
    
                                              if (ContextCompat.checkSelfPermission(this , Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                                              {
                                                  fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
                                                      if (location != null)
                                                      {
                                                          Log.e("MainActivity" , location.getLatitude() + " " + location.getLongitude());
                                                      }
                                                  });
                                              }
                                          });
        
        
    }
    
   
}