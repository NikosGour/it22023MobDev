package gr.hua.dit.nikosgourn.androiddev22023;

import com.google.android.gms.maps.model.LatLng;

/**
 * Utils class contains Constants and utility methods used across all classes
 */
public class Utilities
{
    public static final int MIN_TIME_INTERVAL_MILLIS = 5000;
    public static final int MIN_DISTANCE_METERS      = 50;
    
    /**
     * Calculates the distance between two points in the surface of the earth
     * @param point the point to be checked
     * @param target from where the distance is measured
     * @return the distance between the two points in meters
     */
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
