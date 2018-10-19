package id.starkey.pelanggan.MapsUbahLoc;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import id.starkey.pelanggan.Maps.MapsActivity;
import id.starkey.pelanggan.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.List;
import java.util.Locale;

public class MapsUbahActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener, GoogleMap.OnCameraIdleListener, GoogleMap.OnCameraMoveStartedListener, View.OnClickListener {

    GoogleMap map;
    LocationManager locationManager;
    private Button bMapPilih;
    static final int REQUEST_LOCATION = 1;
    private PlaceAutocompleteFragment placeAutoComplete;
    private ProgressBar progressBar;
    private GoogleApiClient googleApiClient;
    private TextView tNama, tDetail;
    private static final LatLngBounds BOUNDS_SEMARANG = new LatLngBounds(new LatLng(-6.966667, 110.416664),
            new LatLng(-6.466667, 110.446664));
    private double lat1, lng1;
    private String getDetailAlamat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_ubah);

        //init text view
        tNama = findViewById(R.id.txtNama_ubah);
        tDetail = findViewById(R.id.txtDetail_ubah);

        placeAutoComplete = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_ubah);
        placeAutoComplete.setBoundsBias(BOUNDS_SEMARANG);
        placeAutoComplete.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.d("Maps", "Place selected: " + place.getName());
                //LatLng myPos = new LatLng(latti, longi);
                map.clear();
                LatLng myPos = place.getLatLng();

                map.animateCamera(CameraUpdateFactory.newLatLngZoom(myPos, 17.0f));
                //getDescLoc(latti, longi);
                tNama.setText(place.getName());
                tDetail.setText(place.getAddress());
            }

            @Override
            public void onError(Status status) {
                Log.d("Maps", "An error occurred: " + status);
            }
        });

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        initializeMap();

        //init pb
        progressBar = findViewById(R.id.pb_ubah);

        //init btn
        bMapPilih = findViewById(R.id.btnUbahMap);
        bMapPilih.setOnClickListener(this);

        //get param lat lng
        Bundle bundle = getIntent().getExtras();
        lat1 = bundle.getDouble("lat");
        lng1 = bundle.getDouble("lnt");

    }

    private void initializeMap() {
        if (map == null) {
            MapFragment mapFragment = (MapFragment) getFragmentManager()
                    .findFragmentById(R.id.map_ubah);
            if (mapFragment != null) {
                mapFragment.getMapAsync(MapsUbahActivity.this);
            }

            // check if map is created successfully or not
            if (null == map) {
                //Toast.makeText(getApplicationContext(), "Sorry! unable to create maps", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission
                    (Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        2);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission
                    (Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        2);
            }
            return;
        }

        map.setMyLocationEnabled(true);
        map.setOnMyLocationButtonClickListener(this);
        map.setOnMyLocationClickListener(this);
        map.setOnCameraIdleListener(this);
        map.setOnCameraMoveStartedListener(this);
        getLocation();
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        } else {
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (location != null) {
                map.clear();
                double latti = location.getLatitude();
                double longi = location.getLongitude();

                LatLng myPos = new LatLng(latti, longi);
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(myPos, 17.0f));
                getDescLoc(latti, longi);


            } else {
                map.clear();
                double lattiElse = lat1;
                double longiElse = lng1;

                LatLng myPos = new LatLng(lattiElse, longiElse);

                //MarkerOptions markerOptions = new MarkerOptions().position(myPos);
                //Marker marker = map.addMarker(markerOptions);
                //markerOptions.position(myPos);
                //marker.setPosition(myPos);

                //map.animateCamera(CameraUpdateFactory.newLatLngZoom(myPos, 17.0f));
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(myPos, 17.0f));
                getDescLoc(lattiElse, longiElse);
                //Toast.makeText(this, "Get Loc Kondisi 2", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getDescLoc(double latitude, double longitude) {
        try {
            Geocoder geo = new Geocoder(MapsUbahActivity.this.getApplicationContext(), Locale.getDefault());
            List<Address> addresses = geo.getFromLocation(latitude, longitude, 1);
            if (addresses.isEmpty()) {
                //yourtextfieldname.setText("Waiting for Location");
                Toast.makeText(this, "Waiting for Location", Toast.LENGTH_SHORT).show();
                map.clear();
            } else {
                if (addresses.size() > 0) {
                    //yourtextfieldname.setText(addresses.get(0).getFeatureName() + ", " + addresses.get(0).getLocality() +", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName());
                    //Toast.makeText(getApplicationContext(), "Address:- " + addresses.get(0).getFeatureName() + addresses.get(0).getAdminArea() + addresses.get(0).getLocality(), Toast.LENGTH_LONG).show();
                    //Toast.makeText(getApplicationContext(), "Address:- " + addresses.get(0).getAddressLine(0) + addresses.get(0).getAddressLine(1) + addresses.get(0).getAddressLine(2), Toast.LENGTH_LONG).show();
                    tNama.setText(addresses.get(0).getFeatureName());
                    tDetail.setText(addresses.get(0).getAddressLine(0));

                    getDetailAlamat = tDetail.getText().toString();
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // getFromLocation() may sometimes fail
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_LOCATION:
                getLocation();
                break;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public boolean onMyLocationButtonClick() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return true;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null){
            map.clear();
            double lat = location.getLatitude();
            double lng = location.getLongitude();

            //LatLng myPosGPS = new LatLng(lat, lng);
            //MarkerOptions markerOptions = new MarkerOptions().position(myPosGPS);
            //Marker marker = map.addMarker(markerOptions);
            //markerOptions.position(myPosGPS);
            //marker.setPosition(myPosGPS);

            getDescLoc(lat, lng);
            //Toast.makeText(this, "Kondisi GPS Provider", Toast.LENGTH_SHORT).show();
        } else {
            map.clear();
            //Toast.makeText(this, "Jaringan atau GPS anda lemah", Toast.LENGTH_SHORT).show();
            Location locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            double latNet = locationNet.getLatitude();
            double lngNet = locationNet.getLongitude();

            //LatLng myPos = new LatLng(latNet, lngNet);
            //MarkerOptions markerOptions = new MarkerOptions().position(myPos);
            //Marker marker = map.addMarker(markerOptions);
            //markerOptions.position(myPos);
            //marker.setPosition(myPos);
            getDescLoc(latNet, lngNet);

            //Toast.makeText(this, "Kondisi NETWORK Provider", Toast.LENGTH_SHORT).show();

        }

        //double lat = fromMainLat;
        //double lng = fromMainLng;
        //getDescLoc(lat, lng);
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCameraIdle() {

        LatLng midLatLng = map.getCameraPosition().target;
        getDescLoc(midLatLng.latitude, midLatLng.longitude);
        //latIdle = midLatLng.latitude;
        //lngIdle = midLatLng.longitude;
        progressBar.setIndeterminate(false);
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onCameraMoveStarted(int i) {
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        if (v == bMapPilih){
            Intent resultIntent = new Intent();
            resultIntent.putExtra("detailAlamat", getDetailAlamat);
            setResult(131, resultIntent);
            finish();
            /*
            Intent resultIntent = new Intent();
                resultIntent.putExtra("kunci", nama_kunci[posisi]);
                setResult(110, resultIntent);
                finish();
             */
        }
    }
}
