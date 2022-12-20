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
        
        
        // Dokimasa 200 pragmata gia na to kano na doulepsi , tipota den doulepse
        // pano kato to null einai provilma mono se enan emulator o opoios den exei data
        // opote den exei kai kanena previous known location opote i lisi einai Hardcode :),
        // den mou aresei alla thelo na doylevei se kathe case.
        // tha ithela para polu na mou steilete ena email (it22023@hua.gr) gia na sizitisoume
        // pos tha mporouse na lithei to provlima
        // !!!!PS!!!! : grafo Greeklish gt den ksero ama meso tou compression tou Eclass ama tha mporei
        // na kodikopithoun ta ellinika
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


