package gr.hua.dit.nikosgourn.androiddev22023;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import gr.hua.dit.nikosgourn.androiddev22023.room.EntranceExitGeoPoint;
import gr.hua.dit.nikosgourn.androiddev22023.room.GeoPoint;
import gr.hua.dit.nikosgourn.androiddev22023.room.MapsSession;

/**
 * Background service that handles location updates.
 * and saves the entered/exited points to the database.
 */
@SuppressLint("MissingPermission")
@RequiresApi(api = Build.VERSION_CODES.Q)
public class LocationService extends Service
{
    
    // GeoFence points of current session
    private ArrayList<Point> points;
    private Location         lastLocation;
    
    
    class LocationListener implements android.location.LocationListener
    {
        
        @Override
        public void onLocationChanged(@NonNull Location location)
        {
            Log.e(TAG , "onLocationChanged: " + location);
            if (lastLocation != null)
            {
                EntranceExitGeoPoint entranceExitGeoPoint = new EntranceExitGeoPoint();
                
                boolean isLastLocationOutsideOfPoints =
                        ! isPointInCircles(new LatLng(lastLocation.getLatitude() , lastLocation.getLongitude()));
                boolean isCurrentLocationInsideOfPoints =
                        isPointInCircles(new LatLng(location.getLatitude() , location.getLongitude()));
                
                // If the last location was outside of the points and the current location is inside of the points
                // then the user has entered a point
                if (isLastLocationOutsideOfPoints && isCurrentLocationInsideOfPoints)
                {
                    
                    entranceExitGeoPoint.latitude          = location.getLatitude();
                    entranceExitGeoPoint.longitude         = location.getLongitude();
                    entranceExitGeoPoint.session_id        = sessionId;
                    entranceExitGeoPoint.is_entrance_point = true;
                    
                    Log.e(TAG , "onLocationChanged: " + entranceExitGeoPoint);
                    addEntranceExitGeoPoint(entranceExitGeoPoint);
                    
                }
                // If the last location was inside of the points and the current location is outside of the points
                // that means that the user has exited the points
                else if (! isLastLocationOutsideOfPoints && ! isCurrentLocationInsideOfPoints)
                {
                    entranceExitGeoPoint.latitude          = lastLocation.getLatitude();
                    entranceExitGeoPoint.longitude         = lastLocation.getLongitude();
                    entranceExitGeoPoint.session_id        = sessionId;
                    entranceExitGeoPoint.is_entrance_point = false;
                    
                    Log.e(TAG , "onLocationChanged: " + entranceExitGeoPoint);
                    addEntranceExitGeoPoint(entranceExitGeoPoint);
                }
            }
            
            // Update last location to current location for next iteration
            lastLocation = location;
            
            
        }
    }
    
    
    /**
     * Helper Class
     */
    private static class Point
    {
        public double latitude;
        public double longitude;
    }
    
    private static final String TAG = "LocationService";
    
    private LocationManager                  locationManager;
    private LocationService.LocationListener locationListener;
    private ContentResolver                  contentResolver;
    private int                              sessionId;
    
    
    @Override
    public void onCreate()
    {
        super.onCreate();
        points           = new ArrayList<>();
        locationListener = new LocationService.LocationListener();
        locationManager  = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        contentResolver  = getContentResolver();
        lastLocation     = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        
    }
    
    
    @Override
    @SuppressLint("Range")
    public int onStartCommand(Intent intent , int flags , int startId)
    {
        Log.d(TAG , "Starting Location service");
        // Get the points from the latest session
        // and start listening for location updates
        new Thread(() -> {
            
            // Get the latest session id
            Uri uri = GeoPointProvider.GET_LATEST_MAPS_SESSIONS_URI;
            Cursor cursor = contentResolver.query(uri , null , null , null , null);
            
            cursor.moveToFirst();
            sessionId = cursor.getInt(cursor.getColumnIndex(MapsSession.SESSION_ID));
            cursor.close();
            
            // Get the points of the latest session
            uri = Uri.withAppendedPath(GeoPointProvider.GET_ALL_GEO_POINTS_URI_ADD_SESSION_ID,sessionId+"");
            cursor = contentResolver.query(uri , null , null , null , null);
            
            if (cursor != null)
            {
                cursor.moveToFirst();
                while (! cursor.isAfterLast())
                {
                    double latitude = cursor.getDouble(cursor.getColumnIndex(GeoPoint.LATITUDE));
                    double longitude = cursor.getDouble(cursor.getColumnIndex(GeoPoint.LONGITUDE));
                    Log.d(TAG , "onCreate: Point : " + latitude + " " + longitude);
                    
                    Point point = new Point();
                    point.latitude  = latitude;
                    point.longitude = longitude;
                    points.add(point);
                    
                    cursor.moveToNext();
                }
                cursor.close();
            }
            
        }).start();
        
        // Start listening for location updates
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER , Utilities.MIN_TIME_INTERVAL_MILLIS , Utilities.MIN_DISTANCE_METERS , locationListener);
        
        // Run the service in the background
        startForeground(1 , createDummyNotification());
        return super.
                
                onStartCommand(intent , flags , startId);
        
    }
    
    private void addEntranceExitGeoPoint(EntranceExitGeoPoint entranceExitGeoPoint)
    {
        Uri uri = GeoPointProvider.CREATE_ENTRANCE_EXIT_GEO_POINT_URI;
        
        ContentValues contentValues = new ContentValues();
        contentValues.put(EntranceExitGeoPoint.LATITUDE , entranceExitGeoPoint.latitude);
        contentValues.put(EntranceExitGeoPoint.LONGITUDE , entranceExitGeoPoint.longitude);
        contentValues.put(EntranceExitGeoPoint.SESSION_ID , entranceExitGeoPoint.session_id);
        contentValues.put(EntranceExitGeoPoint.IS_ENTRANCE , entranceExitGeoPoint.is_entrance_point);
        
        new Thread(() -> contentResolver.insert(uri , contentValues)).start();
    }
    
    
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
    
    
    // Check if location is inside one of the points of the current session
    private boolean isPointInCircles(LatLng location)
    {
        for (Point point : points)
        {
            double distance =
                    Utilities.distance_point_from_target(new LatLng(location.latitude , location.longitude) , new LatLng(point.latitude , point.longitude));
            if (distance <= 100)
            {
                return true;
            }
        }
        return false;
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