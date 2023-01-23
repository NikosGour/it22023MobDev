package gr.hua.dit.nikosgourn.andoiddev22023.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.List;


/**
 *  Dao interface for the Room database
 *  Contains all the queries for the database
 */
@Dao
public interface AllDao
{
    
    @Insert
    long insertGeoPoint(GeoPoint geoPoint);
    
    @Insert
    long insertMapsSession(MapsSession mapsSession);
    
    @Insert
    long insertEntranceExitGeoPoint(EntranceExitGeoPoint entranceExitGeoPoint);
    
    @Query("SELECT * FROM maps_sessions WHERE is_active = 1")
    MapsSession getActiveMapsSession();
    
    @Query("SELECT * FROM geo_points WHERE session_id = :session_id")
    List<GeoPoint> getGeoPointsBySessionId(int session_id);
    
    @Query("SELECT * FROM entrance_exit_geo_points WHERE session_id = :session_id")
    List<EntranceExitGeoPoint> getEntranceExitGeoPointsBySessionId(int session_id);
    
    @Query("SELECT * FROM maps_sessions ORDER BY session_id DESC LIMIT 1")
    MapsSession getLatestMapsSession();
    
    @Query("SELECT * FROM maps_sessions WHERE session_id = :session_id")
    MapsSession getMapsSessionById(int session_id);
    
    @Update
    void updateMapsSession(MapsSession mapsSession);
    
}
