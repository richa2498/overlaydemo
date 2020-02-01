package com.example.overlay;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private final int REQUEST_CODE = 1;
    private Marker homemarker,destMarker;

    Polyline line;
    Polygon shape;
    LocationManager locationManager;
    LocationListener locationListener;
    private final int Shape_POINT = 3;
    List<Marker> markers = new ArrayList<>();


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

        // Add a marker in Sydney and move the camera
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                //set the home location;

                setHomeLocation(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };



        if(!checkPermission()){
            requestPermission();
        }else{
            getLocation();
        }
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                //clearMap();
               // clearMap();
                Location location = new Location("Your Hevene");
                location.setLatitude(latLng.latitude);
                location.setLongitude(latLng.longitude);

                //set Marker
                setMarker(location);
            }
        });

    }

    private void setMarker(Location location){
        LatLng userLat = new LatLng(location.getLatitude(),location.getLongitude());
        MarkerOptions options = new MarkerOptions().position(userLat).title("Your Destination").snippet("You are going thre").draggable(true);
//        destMarker = mMap.addMarker(options);
//        if (destMarker == null){
//            destMarker = mMap.addMarker(options);
//
//        }else {
//           // line.remove();
//            clearMap();
//           // line.remove();
//            destMarker = mMap.addMarker(options);
//
//        }
//        drawLine();

        if (markers.size() == Shape_POINT){
            clearMap();
        }

        markers.add(mMap.addMarker(options));

        if (markers.size() == Shape_POINT){
            drawDShape();
        }
    }

    @SuppressLint("MissingPermission")
    private  void getLocation(){
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,0,locationListener);
        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        //set the known loaction as home location;
        setHomeLocation(lastKnownLocation);

    }
    private void requestPermission(){
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
    }

    private boolean checkPermission(){
        int permStatus = ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION);
        return  permStatus == PackageManager.PERMISSION_GRANTED;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (REQUEST_CODE == requestCode){
            if  (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,10,locationListener);
            }
        }
    }
    private void setHomeLocation(Location location){

        LatLng userLoaction = new LatLng(location.getLatitude(),location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(userLoaction).title("Location Is Hell.😅").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)).snippet("wel-come to hell");
        homemarker = mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLoaction,15));
    }
    private void clearMap(){
//        if (destMarker != null){
//          destMarker.remove();
//          destMarker = null;
//        }


        for (Marker marker : markers)
            marker.remove();

        markers.clear();
        shape.remove();
        shape = null;


    }
    private void drawLine(){
        PolylineOptions options = new PolylineOptions().add(homemarker.getPosition()).add(destMarker.getPosition()).color(Color.BLUE).width(10);
        line = mMap.addPolyline(options);
    }
    private void drawDShape(){

        PolygonOptions options = new PolygonOptions().fillColor(0x330000FF).strokeWidth(5).strokeColor(Color.RED);

        for (int i = 0; i<Shape_POINT; i++){
            options.add(markers.get(i).getPosition());
            shape = mMap.addPolygon(options);

        }
    }


}
