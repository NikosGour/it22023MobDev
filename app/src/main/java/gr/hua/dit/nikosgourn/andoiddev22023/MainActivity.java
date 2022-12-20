package gr.hua.dit.nikosgourn.andoiddev22023;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity
{
    
    Button select_boundaries, stop_searching, see_boundaries;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        select_boundaries = findViewById(R.id.selectBoundariesButton);
        stop_searching    = findViewById(R.id.stopSearchingButton);
        see_boundaries    = findViewById(R.id.seeBoundariesButton);
        
        select_boundaries.setOnClickListener(view -> {
//            int[] permission =
//                    new int[]{ ContextCompat.checkSelfPermission(this , Manifest.permission.ACCESS_FINE_LOCATION) , ContextCompat.checkSelfPermission(this , Manifest.permission.ACCESS_COARSE_LOCATION) };
//
//            if (permission[0] == PackageManager.PERMISSION_DENIED ||
//                permission[1] != PackageManager.PERMISSION_DENIED)
//            {
//
//                requestPermissions(new String[]{ Manifest.permission.ACCESS_FINE_LOCATION , Manifest.permission.ACCESS_COARSE_LOCATION } , 22023);
//            } else
//            {
                startActivity(new Intent(this , MapActivity.class));
//            }
            
        });
        
        stop_searching.setOnClickListener(view -> Logger.getAnonymousLogger().severe("Stop Searching"));
        see_boundaries.setOnClickListener(view -> Logger.getAnonymousLogger().severe("See Boundaries"));
        
        
    }
    
   
}