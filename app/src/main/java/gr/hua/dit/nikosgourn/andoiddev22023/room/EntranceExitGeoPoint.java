package gr.hua.dit.nikosgourn.andoiddev22023.room;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "entrance_exit_geo_points")
public class EntranceExitGeoPoint
{
    public static final String ID = "id";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String SESSION_ID = "session_id";
    public static final String IS_ENTRANCE = "is_entrance";
    
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ID)
    public int id;
    
    @ColumnInfo(name = LATITUDE)
    public double latitude;
    
    @ColumnInfo(name = LONGITUDE)
    public double longitude;
    
    @ColumnInfo(name = SESSION_ID)
    public int session_id;
    
    @ColumnInfo(name = IS_ENTRANCE)
    public boolean is_entrance_point;
    
    @NonNull
    @Override
    public String toString()
    {
        
        return "EntranceExitGeoPoint{" +
               "id=" + id +
               ", latitude=" + latitude +
               ", longitude=" + longitude +
               ", session_id=" + session_id +
               ", is_entrance_point=" + is_entrance_point +
               '}';
    }
}
