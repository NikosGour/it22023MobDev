package gr.hua.dit.nikosgourn.andoiddev22023.room;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "geo_points")
public class GeoPoint
{
    public static final String ID         = "id";
    public static final String LATITUDE   = "latitude";
    public static final String LONGITUDE  = "longitude";
    public static final String SESSION_ID = "session_id";
    
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ID)
    public int id;
    
    @ColumnInfo(name = LATITUDE)
    public double latitude;
    
    @ColumnInfo(name = LONGITUDE)
    public double longitude;
    
    
    @ColumnInfo(name = SESSION_ID)
    public int session_id;
}
