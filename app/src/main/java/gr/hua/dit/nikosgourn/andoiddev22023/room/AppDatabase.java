package gr.hua.dit.nikosgourn.andoiddev22023.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

/**
 *  Database class for the whole app.
 */
@Database(entities = {GeoPoint.class, MapsSession.class, EntranceExitGeoPoint.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase
{
    public abstract AllDao allDao();
}

