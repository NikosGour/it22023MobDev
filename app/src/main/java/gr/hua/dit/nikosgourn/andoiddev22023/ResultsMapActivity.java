package gr.hua.dit.nikosgourn.andoiddev22023;

import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import gr.hua.dit.nikosgourn.andoiddev22023.databinding.ActivityResultsMapBinding;
import gr.hua.dit.nikosgourn.andoiddev22023.room.GeoPoint;
import gr.hua.dit.nikosgourn.andoiddev22023.room.MapsSession;

@SuppressLint("MissingPermission")
public class ResultsMapActivity extends FragmentActivity implements OnMapReadyCallback
{
    
    private GoogleMap                 mMap;
    private ActivityResultsMapBinding binding;
    private ContentResolver           contentResolver;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        binding = ActivityResultsMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        contentResolver = getContentResolver();
        
        
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    
    
    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        
        
        new Thread(() -> {
            
            Uri uri = GeoPointProvider.GET_ACTIVE_MAPS_SESSIONS_URI;
            Cursor cursor = contentResolver.query(uri , null , null , null , null);
            
            if (cursor == null)
            {
                Toast.makeText(this , "There is no Active Session" , Toast.LENGTH_LONG).show();
                finish();
            }
            
            cursor.moveToFirst();
            @SuppressLint("Range") int id =
                    cursor.getInt(cursor.getColumnIndex(MapsSession.SESSION_ID));
            cursor.close();
            
            uri    = Uri.withAppendedPath(GeoPointProvider.GET_ALL_GEO_POINTS_URI_ADD_SESSION_ID ,
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
                    int finalCount = count;
                    ResultsMapActivity.this.runOnUiThread(() -> {
                        mMap.addMarker(new MarkerOptions().position(latLng).title("GeoFencePoint : " +
                                                                                  finalCount));
                        
                        mMap.addCircle(new CircleOptions().center(latLng).radius(100).visible(true).strokeColor(Color.RED));
                    });
                    cursor.moveToNext();
                    count++;
                }
            }
            
        }).start();
        
    }
}