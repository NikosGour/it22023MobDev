package gr.hua.dit.nikosgourn.andoiddev22023;

import static android.location.LocationManager.GPS_PROVIDER;

import android.annotation.SuppressLint;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class LocationListener implements android.location.LocationListener
{
    private Location        cur_location;
    private LocationManager locationManager;
    private GoogleMap       mMap;
    
    @SuppressLint("MissingPermission")
    public LocationListener(LocationManager locationManager , GoogleMap mMap)
    {
        this.locationManager = locationManager;
        this.mMap            = mMap;
        this.cur_location    = getLastKnownLocation();
        
        
        /* Δοκιμασα 200 πραγματα για να το κανω να δουλεψει , τιποτα δεν δουλεψε
         * πανω κατω το null ειναι προβλημα μονο σε εναν emulator ο οποιος δεν εχει data
         * οποτε δεν εχει και κανενα previous known location οποτε η λυση ειναι Hardcode :),
         * δεν μου αρεσει αλλα θελω να δουλεβει σε καθε περιπτωση.
         * θα ηθελα πολυ να μου στειλετε ενα email (it22023@hua.gr) για να συζητησουμε
         * πως θα μπορουσε να λυθει το προβλημα
         */
        if (cur_location == null)
        {
            Location l = new Location("");
            l.setLatitude(37.983810);
            l.setLongitude(23.727539);
            
            this.cur_location = l;
        }
    }
    
    @SuppressLint("MissingPermission")
    private Location getLastKnownLocation() {
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }
    
    @Override
    public void onLocationChanged(@NonNull Location location)
    {
        Log.e("LocationListener" , location.toString());
        cur_location = location;
        mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude() , location.getLongitude())).title("Marker"));
    }
    
    public Location getCur_location()
    {
        return cur_location;
    }
    
}


