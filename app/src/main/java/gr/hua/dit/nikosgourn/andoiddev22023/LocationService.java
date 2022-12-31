package gr.hua.dit.nikosgourn.andoiddev22023;

import static android.location.LocationManager.GPS_PROVIDER;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.IBinder;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class LocationService extends Service
{
    
    class LocationBinder extends Binder
    {
        public LocationService getService()
        {
            return LocationService.this;
        }
    }
    
    private IBinder          binder;
    private LocationListener locationListener;
    private LocationManager  locationManager;
    private GoogleMap        mMap;
    
    @Override
    public void onCreate()
    {
        super.onCreate();
        binder = new LocationBinder();
    }
    
    @Override
    public IBinder onBind(Intent intent)
    {
        return binder;
    }
    
    @Override
    public boolean onUnbind(Intent intent)
    {
        mMap = null;
        return super.onUnbind(intent);
    }
    
    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent , int flags , int startId)
    {
        mMap = MapActivity.mMap;
        
        
        if (! isGPSEnabled())
        {
        }
        locationListener = new LocationListener(locationManager , mMap);
        locationManager.requestLocationUpdates(GPS_PROVIDER , 5000 , 50 , locationListener);
        
        Location location = getLocation();
    
        Circle c =
                mMap.addCircle(new CircleOptions().center(new LatLng(location.getLatitude() , location.getLongitude())).radius(100).strokeColor(Color.RED).strokeWidth(20));
        LatLng location_LatLng = new LatLng(location.getLatitude() , location.getLongitude());
        mMap.addMarker(new MarkerOptions().position(location_LatLng).title("Marker in current location"));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location_LatLng));
        
        
        return super.onStartCommand(intent , flags , startId);
    }
    
    public boolean isGPSEnabled()
    {
        if (locationManager == null)
        {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }
        return locationManager.isProviderEnabled(GPS_PROVIDER);
    }
    
    public Location getLocation()
    {
        return locationListener.getCur_location();
    }
    
}