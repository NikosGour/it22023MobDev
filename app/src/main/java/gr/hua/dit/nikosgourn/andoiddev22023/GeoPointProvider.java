package gr.hua.dit.nikosgourn.andoiddev22023;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.room.Entity;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.List;

import gr.hua.dit.nikosgourn.andoiddev22023.room.AllDao;
import gr.hua.dit.nikosgourn.andoiddev22023.room.AppDatabase;
import gr.hua.dit.nikosgourn.andoiddev22023.room.EntranceExitGeoPoint;
import gr.hua.dit.nikosgourn.andoiddev22023.room.GeoPoint;
import gr.hua.dit.nikosgourn.andoiddev22023.room.MapsSession;

public class GeoPointProvider extends ContentProvider
{
    public static final String     AUTHORITY   = "gr.hua.dit.nikosgourn.andoiddev22023";
    
    public static final Uri        CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final Uri    GET_ALL_GEO_POINTS_URI_ADD_SESSION_ID = Uri.withAppendedPath(CONTENT_URI , "points");
    public static final Uri    GET_ALL_ENTRANCE_EXIT_GEO_POINTS_URI_ADD_SESSION_ID = Uri.withAppendedPath(CONTENT_URI , "entrance_exit_points");
    public static final Uri    GET_ACTIVE_MAPS_SESSIONS_URI = Uri.withAppendedPath(CONTENT_URI , "session/active");
    public static final Uri    GET_LATEST_MAPS_SESSIONS_URI = Uri.withAppendedPath(CONTENT_URI , "session");
    public static final Uri    CREATE_MAPS_SESSION_URI = Uri.withAppendedPath(CONTENT_URI , "session/new");
    public static final Uri    CREATE_GEO_POINT_URI = Uri.withAppendedPath(CONTENT_URI , "points/new");
    public static final Uri    CREATE_ENTRANCE_EXIT_GEO_POINT_URI = Uri.withAppendedPath(CONTENT_URI , "entrance_exit_points/new");
    public static final Uri    SWITCH_ACTIVE_STATUS_MAPS_SESSION_URI_ADD_SESSION_ID = Uri.withAppendedPath(CONTENT_URI , "session/switch");
    
    private static final int    GET_ALL_GEO_POINTS = 1;
    private static final int    GET_ALL_ENTRANCE_EXIT_GEO_POINTS = 2;
    private static final int    GET_ACTIVE_MAPS_SESSIONS = 3;
    private static final int    GET_LATEST_MAPS_SESSIONS = 4;
    private static final int    CREATE_MAPS_SESSION = 5;
    private static final int    CREATE_GEO_POINT = 6;
    private static final int    CREATE_ENTRANCE_EXIT_GEO_POINT = 7;
    private static final int    SWITCH_ACTIVE_STATUS_MAPS_SESSION = 8;
    
    private             UriMatcher uriMatcher;
    private             AllDao     allDao;
    
    
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public boolean onCreate()
    {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        // Gets all points for the session with the given id
        uriMatcher.addURI(AUTHORITY , "points/#" , GET_ALL_GEO_POINTS);
        
        // Gets all entrance/exit points for the session with the given id
        uriMatcher.addURI(AUTHORITY , "entrance_exit_points/#" , GET_ALL_ENTRANCE_EXIT_GEO_POINTS);
        
        // Gets the session that is currently active
        uriMatcher.addURI(AUTHORITY , "session/active" , GET_ACTIVE_MAPS_SESSIONS);
        
        // Gets the latest session
        uriMatcher.addURI(AUTHORITY , "session" , GET_LATEST_MAPS_SESSIONS);
        
        // Creates a new session
        uriMatcher.addURI(AUTHORITY , "session/new" , CREATE_MAPS_SESSION);
        
        // Creates a new point
        uriMatcher.addURI(AUTHORITY , "points/new" , CREATE_GEO_POINT);
        
        // Creates a new entrance/exit point
        uriMatcher.addURI(AUTHORITY , "entrance_exit_points/new" , CREATE_ENTRANCE_EXIT_GEO_POINT);
        
        // Switches the sessions active status to the opposite of what it is , given the session id
        uriMatcher.addURI(AUTHORITY , "session/switch/#" , SWITCH_ACTIVE_STATUS_MAPS_SESSION);
        
        AppDatabase database =
                Room.databaseBuilder(getContext() , AppDatabase.class , "Geofence").build();
        
        allDao = database.allDao();
        
        return false;
    }
    
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri , @Nullable String[] projection ,
                        @Nullable String selection , @Nullable String[] selectionArgs ,
                        @Nullable String sortOrder)
    {
        int session_id;
        MatrixCursor matrixCursor;
        switch (uriMatcher.match(uri))
        {
            case GET_ALL_GEO_POINTS:
                
                session_id = Integer.parseInt(uri.getLastPathSegment());
                List<GeoPoint> geoPoints = allDao.getGeoPointsBySessionId(session_id);
                
                matrixCursor =
                        new MatrixCursor(new String[]{ GeoPoint.LATITUDE , GeoPoint.LONGITUDE });
                
                for (GeoPoint geoPoint : geoPoints)
                {
                    matrixCursor.addRow(new Object[]{ geoPoint.latitude , geoPoint.longitude });
                }
                
                return matrixCursor;
            case GET_ALL_ENTRANCE_EXIT_GEO_POINTS:
                
                session_id = Integer.parseInt(uri.getLastPathSegment());
                List<EntranceExitGeoPoint> entranceExitGeoPoints =
                        allDao.getEntranceExitGeoPointsBySessionId(session_id);
                
                matrixCursor =
                        new MatrixCursor(new String[]{ EntranceExitGeoPoint.LATITUDE , EntranceExitGeoPoint.LONGITUDE , EntranceExitGeoPoint.IS_ENTRANCE });
                
                for (EntranceExitGeoPoint entranceExitGeoPoint : entranceExitGeoPoints)
                {
                    matrixCursor.addRow(new Object[]{ entranceExitGeoPoint.latitude , entranceExitGeoPoint.longitude , entranceExitGeoPoint.is_entrance_point });
                }
                
                return matrixCursor;
            case GET_ACTIVE_MAPS_SESSIONS:
                
                MapsSession activeMapsSession = allDao.getActiveMapsSession();
                matrixCursor = new MatrixCursor(new String[]{ MapsSession.SESSION_ID });
                matrixCursor.addRow(new Object[]{ activeMapsSession.session_id });
                return matrixCursor;
            case GET_LATEST_MAPS_SESSIONS:
                
                MapsSession latestMapsSession = allDao.getLatestMapsSession();
                matrixCursor = new MatrixCursor(new String[]{ MapsSession.SESSION_ID });
                matrixCursor.addRow(new Object[]{ latestMapsSession.session_id });
                return matrixCursor;
            default:
                return null;
        }
        
    }
    
    @Nullable
    @Override
    public String getType(@NonNull Uri uri)
    {
        return null;
    }
    
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri , @Nullable ContentValues values)
    {
        switch (uriMatcher.match(uri))
        {
            case CREATE_MAPS_SESSION:
                MapsSession lastMapsSession = allDao.getLatestMapsSession();
                if (lastMapsSession != null)
                {
                    lastMapsSession.is_active = false;
                    allDao.updateMapsSession(lastMapsSession);
                }
                
                
                MapsSession mapsSession = new MapsSession();
                mapsSession.is_active = true;
                mapsSession.session_id = allDao.insertMapsSession(mapsSession);
                return Uri.parse("content://" + AUTHORITY + "/session");
            case CREATE_GEO_POINT:
                GeoPoint geoPoint = new GeoPoint();
                geoPoint.latitude = values.getAsDouble(GeoPoint.LATITUDE);
                geoPoint.longitude = values.getAsDouble(GeoPoint.LONGITUDE);
                geoPoint.session_id = values.getAsInteger(GeoPoint.SESSION_ID);
                allDao.insertGeoPoint(geoPoint);
                return Uri.parse("content://" + AUTHORITY + "/points/" + geoPoint.session_id);
            case CREATE_ENTRANCE_EXIT_GEO_POINT:
                EntranceExitGeoPoint entranceExitGeoPoint = new EntranceExitGeoPoint();
                entranceExitGeoPoint.latitude = values.getAsDouble(EntranceExitGeoPoint.LATITUDE);
                entranceExitGeoPoint.longitude = values.getAsDouble(EntranceExitGeoPoint.LONGITUDE);
                entranceExitGeoPoint.session_id =
                        values.getAsInteger(EntranceExitGeoPoint.SESSION_ID);
                entranceExitGeoPoint.is_entrance_point =
                        values.getAsBoolean(EntranceExitGeoPoint.IS_ENTRANCE);
                allDao.insertEntranceExitGeoPoint(entranceExitGeoPoint);
                return Uri.parse("content://" +
                                 AUTHORITY +
                                 "/entrance_exit_points/" +
                                 entranceExitGeoPoint.session_id);
            default:
                return null;
        }
        
    }
    
    @Override
    public int delete(@NonNull Uri uri , @Nullable String selection ,
                      @Nullable String[] selectionArgs)
    {
        return 0;
    }
    
    @Override
    public int update(@NonNull Uri uri , @Nullable ContentValues values ,
                      @Nullable String selection , @Nullable String[] selectionArgs)
    {
        if (uriMatcher.match(uri) == SWITCH_ACTIVE_STATUS_MAPS_SESSION)
        {
            int session_id = Integer.parseInt(uri.getLastPathSegment());
            MapsSession mapsSession = allDao.getMapsSessionById(session_id);
            mapsSession.is_active = ! mapsSession.is_active;
            allDao.updateMapsSession(mapsSession);
            return 1;
        }
        return 0;
    }
}
