package gr.hua.dit.nikosgourn.androiddev22023.room;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


/**
 * A MapsSession is a parent object that connects other database objects.
 * a mapsSession has multiple GeoPoints and multiple EntranceExitPoints.
 */
@Entity(tableName = "maps_sessions")
public class MapsSession
{
    public static final String SESSION_ID = "session_id";
    public static final String IS_ACTIVE  = "is_active";
    
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = SESSION_ID)
    public long session_id;
    
    @ColumnInfo(name = IS_ACTIVE)
    public boolean is_active;
}
