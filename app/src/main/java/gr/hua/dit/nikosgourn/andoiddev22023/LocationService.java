package gr.hua.dit.nikosgourn.andoiddev22023;

import static android.location.LocationManager.GPS_PROVIDER;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import gr.hua.dit.nikosgourn.andoiddev22023.room.GeoPoint;
import gr.hua.dit.nikosgourn.andoiddev22023.room.MapsSession;

public class LocationService extends Service
{
    
    
    static class LocationListener implements android.location.LocationListener
    {
        
        @Override
        public void onLocationChanged(@NonNull Location location)
        {
            Log.e(TAG , "onLocationChanged: " + location);
            
        }
    }
    
    private static final String TAG = "LocationService";
    
    private LocationManager                  locationManager;
    private LocationService.LocationListener locationListener;
    private ContentResolver                  contentResolver;
    
    
    @Override
    public void onCreate()
    {
        super.onCreate();
        locationListener = new LocationService.LocationListener();
        locationManager  = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        contentResolver  = getContentResolver();
        
    }
    
    
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent , int flags , int startId)
    {
        new Thread(() -> {
            Uri uri = Uri.parse(GeoPointProvider.CONTENT_URI + "/session");
            Cursor cursor = contentResolver.query(uri , null , null , null , null);
            
            if (cursor != null)
            {
                cursor.moveToFirst();
                @SuppressLint("Range") int session_id =
                        cursor.getInt(cursor.getColumnIndex(MapsSession.SESSION_ID));
                
                uri    = Uri.parse(GeoPointProvider.CONTENT_URI + "/points/" + session_id);
                cursor = contentResolver.query(uri , null , null , null , null);
                
                if (cursor != null)
                {
                    cursor.moveToFirst();
                    while (! cursor.isAfterLast())
                    {
                        @SuppressLint("Range") double latitude =
                                cursor.getDouble(cursor.getColumnIndex(GeoPoint.LATITUDE));
                        @SuppressLint("Range") double longitude =
                                cursor.getDouble(cursor.getColumnIndex(GeoPoint.LONGITUDE));
                        Log.d(TAG , "onCreate: Point : " + latitude + " " + longitude);
                        cursor.moveToNext();
                    }
                    cursor.close();
                }
                
            }
            
            
        }).start();
        
        
        Log.d(TAG , "Starting Location service");
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
        
        return new Notification.Builder(this , "LocationService").setContentTitle("Location Service").setContentText("Location Service is running").setSmallIcon(R.drawable.ic_launcher_foreground).setContentIntent(pendingIntent).build();
    }
    
    @Override
    public void onDestroy()
    {
        Log.d(TAG , "Stopping Location Service");
        locationManager.removeUpdates(locationListener);
        super.onDestroy();
    }
    
    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }
    
    
}