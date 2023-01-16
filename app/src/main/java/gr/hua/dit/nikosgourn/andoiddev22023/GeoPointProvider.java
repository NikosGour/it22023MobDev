package gr.hua.dit.nikosgourn.andoiddev22023;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
    public static final String     AUTHORITY = "gr.hua.dit.nikosgourn.andoiddev22023";
    private             UriMatcher uriMatcher;
    private             AllDao     allDao;
    
    
    @Override
    public boolean onCreate()
    {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY , "points/#" , 1);
        uriMatcher.addURI(AUTHORITY , "entrance_exit_points/#" , 2);
        uriMatcher.addURI(AUTHORITY , "session/active" , 3);
        uriMatcher.addURI(AUTHORITY , "session" , 4);
        uriMatcher.addURI(AUTHORITY , "session/new" , 5);
        uriMatcher.addURI(AUTHORITY , "points/new" , 6);
        uriMatcher.addURI(AUTHORITY , "entrance_exit_points/new" , 7);
        uriMatcher.addURI(AUTHORITY , "session/switch/#" , 8);
        
        
        allDao =
                Room.databaseBuilder(getContext() , AppDatabase.class , "Geofence").build().allDao();
        
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
            case 1:
                
                session_id = Integer.parseInt(uri.getLastPathSegment());
                List<GeoPoint> geoPoints = allDao.getGeoPointsBySessionId(session_id);
                
                matrixCursor = new MatrixCursor(new String[]{ GeoPoint.LATITUDE , GeoPoint.LONGITUDE });
                
                for (GeoPoint geoPoint : geoPoints)
                {
                    matrixCursor.addRow(new Object[]{ geoPoint.latitude , geoPoint.longitude });
                }
                
                return matrixCursor;
            case 2:
                
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
            case 3:
                
                MapsSession activeMapsSession = allDao.getActiveMapsSession();
                matrixCursor = new MatrixCursor(new String[]{ MapsSession.SESSION_ID });
                matrixCursor.addRow(new Object[]{ activeMapsSession.session_id });
                return matrixCursor;
            case 4:
                
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
            case 5:
                MapsSession mapsSession = new MapsSession();
                mapsSession.session_id = allDao.insertMapsSession(mapsSession);
                return Uri.parse("content://" + AUTHORITY + "/session/" + mapsSession.session_id);
            case 6:
                GeoPoint geoPoint = new GeoPoint();
                geoPoint.latitude = values.getAsDouble(GeoPoint.LATITUDE);
                geoPoint.longitude = values.getAsDouble(GeoPoint.LONGITUDE);
                geoPoint.session_id = values.getAsInteger(GeoPoint.SESSION_ID);
                allDao.insertGeoPoint(geoPoint);
                return Uri.parse("content://" + AUTHORITY + "/points/" + geoPoint.session_id);
            case 7:
                EntranceExitGeoPoint entranceExitGeoPoint = new EntranceExitGeoPoint();
                entranceExitGeoPoint.latitude = values.getAsDouble(EntranceExitGeoPoint.LATITUDE);
                entranceExitGeoPoint.longitude = values.getAsDouble(EntranceExitGeoPoint.LONGITUDE);
                entranceExitGeoPoint.session_id = values.getAsInteger(EntranceExitGeoPoint.SESSION_ID);
                entranceExitGeoPoint.is_entrance_point = values.getAsBoolean(EntranceExitGeoPoint.IS_ENTRANCE);
                allDao.insertEntranceExitGeoPoint(entranceExitGeoPoint);
                return Uri.parse("content://" + AUTHORITY + "/entrance_exit_points/" + entranceExitGeoPoint.session_id);
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
        if (uriMatcher.match(uri) == 8)
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
