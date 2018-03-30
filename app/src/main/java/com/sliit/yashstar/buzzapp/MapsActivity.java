package com.sliit.yashstar.buzzapp;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private double CURR_LOC_LAT = 0;
    private double CURR_LOC_LON = 0;
    private Marker mCenterMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        CURR_LOC_LAT = Double.parseDouble(MainActivity.LOC_LATITUDE);
        CURR_LOC_LON = Double.parseDouble(MainActivity.LOC_LONGITUDE);
        // Add a marker in Sydney and move the camera
        LatLng currentLoc = new LatLng(CURR_LOC_LAT, CURR_LOC_LON);
        mCenterMarker = mMap.addMarker(new MarkerOptions().position(currentLoc).title("Current Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLoc));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 15));

        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {

                //Remove previous center if it exists
                if (mCenterMarker != null) {
                    mCenterMarker.remove();
                }

                CameraPosition test = mMap.getCameraPosition();
                //Assign mCenterMarker reference:
                mCenterMarker = mMap.addMarker(new MarkerOptions().position(mMap.getCameraPosition().target).anchor(0.5f, .05f).title("Test"));
                Log.d("Tagg", "Map Coordinate: " + String.valueOf(test));
            }
        });
    }


}
