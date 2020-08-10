package com.example.memorableplaces;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    LocationManager locationManager;
    LocationListener locationListener;
    Marker current;
    boolean start=false;


    int x;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Intent intent =getIntent();
        x= intent.getIntExtra("place",-1);
    }


    boolean f= false;
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //latndLng = new ArrayList<>();
        if(x==-1) {
            locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    updateMap(location);
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
            };
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                Location lastKnownLocation = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
                    updateMap(lastKnownLocation);
            }

            mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng latLng) {


                    if (f) {
                        current.remove();
                        MainActivity.latndLng.set(MainActivity.latndLng.size()-1,latLng);
                    }
                    else {
                        f = true;
                        MainActivity.latndLng.add(latLng);
                    }
                    String address = getAddress(latLng);

                    current = mMap.addMarker(new MarkerOptions().position(latLng).title(address));
                    //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    Intent data = new Intent();
                    data.putExtra("Longitude", String.valueOf(latLng.longitude));
                    data.putExtra("Latitude", String.valueOf(latLng.latitude));
                    data.putExtra("address", address);
                    setResult(RESULT_OK, data);
                    // finish();
                    Toast.makeText(getApplicationContext(),"Successfully Updated",Toast.LENGTH_SHORT).show();

                }
            });
        }
        else{
            LatLng toDisplay = new LatLng(MainActivity.latndLng.get(x).latitude,MainActivity.latndLng.get(x).longitude);
            String address=getAddress(toDisplay);
            mMap.addMarker(new MarkerOptions().position(toDisplay).title(address));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(toDisplay,12));


        }
    }
    public String getAddress(LatLng latLng){
        String address = "";
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> listAddress = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            Log.i("Address 1", listAddress.get(0).toString());


            if (listAddress != null && listAddress.size() > 0) {

                       /* if (listAddress.get(0).getThoroughfare() != null)
                            address += listAddress.get(0).getThoroughfare() + " ";*/
                       if (listAddress.get(0).getLocality() != null)
                           address += listAddress.get(0).getLocality() + " ";
                if (listAddress.get(0).getAdminArea() != null)
                    address += listAddress.get(0).getAdminArea() + ", ";
                if (listAddress.get(0).getCountryName() != null)
                    address += listAddress.get(0).getCountryName();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(address.equals("")){
            SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            address += date.format(new Date());
        }
        return address;
    }

    public void updateMap(Location location){

        LatLng curr = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.addMarker(new MarkerOptions().position(curr).title("Your Location"));
        if(start==false) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curr,12));
            start = true;
        }
    }
}
