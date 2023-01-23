package gr.hua.dit.nikosgourn.andoiddev22023;

import com.google.android.gms.maps.model.LatLng;

public class Utilities
{
    public static final int MIN_TIME_INTERVAL_MILLIS = 5000;
    public static final int MIN_DISTANCE_METERS      = 50;
    
    public static double distance_point_from_target(LatLng point, LatLng target )
    {
        final double EARTH_RADIUS = 6371e3;
        double lat1 = Math.toRadians(point.latitude);
        double lat2 = Math.toRadians(target.latitude);
        double lon1 = Math.toRadians(point.longitude);
        double lon2 = Math.toRadians(target.longitude);
        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;
    
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(lat1) * Math.cos(lat2) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a) , Math.sqrt(1 - a));
        return EARTH_RADIUS * c;
    }
}
