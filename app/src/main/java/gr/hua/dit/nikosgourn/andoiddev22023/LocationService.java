package gr.hua.dit.nikosgourn.andoiddev22023;

import static android.location.LocationManager.GPS_PROVIDER;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Tasks;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

public class LocationService extends Service
{
    
    class LocationBinder extends Binder
    {
        public LocationService getService()
        {
            return LocationService.this;
        }
    }
    
    static class LocationListener implements android.location.LocationListener
    {
        
        @Override
        public void onLocationChanged(@NonNull Location location)
        {
            Log.e(TAG , "onLocationChanged: " + location);
            
            
        }
    }
    
    private static final String                           TAG = "LocationService";
    private              IBinder                          binder;
    private              LocationManager                  locationManager;
    private              LocationService.LocationListener locationListener;
    
    @Override
    public void onCreate()
    {
        super.onCreate();
        binder           = new LocationBinder();
        locationListener = new LocationService.LocationListener();
        locationManager  = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        
    }
    
    
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent , int flags , int startId)
    {
        
        
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER , Constants.MIN_TIME_INTERVAL_MILLIS , Constants.MIN_DISTANCE_METERS , locationListener);
        
        startForeground(1 , createDummyNotification());
        return super.onStartCommand(intent , flags , startId);
    }
    
    
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private Notification createDummyNotification()
    {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel notificationChannel =
                new NotificationChannel("LocationService" , "My Location Service" , NotificationManager.IMPORTANCE_DEFAULT);
        
        notificationManager.createNotificationChannel(notificationChannel);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this , 0 , new Intent(this , MainActivity.class) , PendingIntent.FLAG_IMMUTABLE);
        
        return new Notification.Builder(this , "LocationService")
                .setContentTitle("Location Service")
                .setContentText("Location Service is running")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent).build();
    }
    
    @Override
    public void onDestroy()
    {
        Log.d(TAG , "Stopping Location Service");
        locationManager.removeUpdates(locationListener);
        super.onDestroy();
    }
    
    
    @Override
    public IBinder onBind(Intent intent)
    {
        return binder;
    }
    
    @Override
    public boolean onUnbind(Intent intent)
    {
        
        return super.onUnbind(intent);
    }
    
}