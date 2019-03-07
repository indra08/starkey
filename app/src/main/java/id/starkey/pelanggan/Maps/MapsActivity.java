package id.starkey.pelanggan.Maps;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import id.starkey.pelanggan.Kunci.IsiDetailKunci.IsiDetailKunciActivity;
import id.starkey.pelanggan.Laundry.IsiDetailLaundry.IsiDetailLaundryActivity;
import id.starkey.pelanggan.R;
import id.starkey.pelanggan.Stempel.IsiDetailStempel.IsiDetailStempelActivity;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        //LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener, GoogleMap.OnCameraIdleListener,
        GoogleMap.OnCameraMoveStartedListener {

    GoogleMap map;
    LocationManager locationManager;
    static final int REQUEST_LOCATION = 1;
    private GoogleApiClient googleApiClient;
    PlaceAutocompleteFragment placeAutoComplete;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 13;
    private Button bMapLanjut;
    private TextView tNama, tDetail;
    private String paramsBtn, paramLat, paramLng, detAlamat;
    double fromMainLat, fromMainLng;
    private ProgressBar progressBar;
    private double latIdle, lngIdle;
    private ImageView ivCenter, ivTop;

    //bias
    private static final LatLngBounds BOUNDS_SEMARANG = new LatLngBounds(new LatLng(-6.966667, 110.416664),
            new LatLng(-6.466667, 110.446664));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //init text view
        tNama = findViewById(R.id.txtNama);
        tDetail = findViewById(R.id.txtDetail);

        //init image view
        ivCenter = findViewById(R.id.markerCenter);
        ivTop = findViewById(R.id.markerTop);

        //get params from menu
        Bundle extra = getIntent().getExtras();
        paramsBtn = extra.getString("parBtn");
        paramLat = extra.getString("lat");
        paramLng = extra.getString("lng");
        //Toast.makeText(this, paramsBtn + paramLat + paramLng, Toast.LENGTH_SHORT).show();
        fromMainLat = Double.parseDouble(paramLat);
        fromMainLng = Double.parseDouble(paramLng);
        //Toast.makeText(this, paramsBtn + fromMainLat + fromMainLng, Toast.LENGTH_SHORT).show();

        //cek condistion for icon center maps and top
        if (paramsBtn.equals("Kunci")){
            ivCenter.setImageResource(R.drawable.ic_map_cus_starkey);
            //ivTop.setImageResource(R.drawable.ic_map_cus_starkey);
            ivTop.setImageResource(R.drawable.ic_search_black_24dp);
        } else if (paramsBtn.equals("Stempel")){
            ivCenter.setImageResource(R.drawable.ic_map_cust_starkey_stempel);
            //ivTop.setImageResource(R.drawable.ic_map_cust_starkey_stempel);
            ivTop.setImageResource(R.drawable.ic_search_black_24dp);
        }

        //init btn
        bMapLanjut = findViewById(R.id.btnLanjutkan);
        bMapLanjut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (paramsBtn.equals("Kunci")){
                    Intent intent = new Intent(MapsActivity.this, IsiDetailKunciActivity.class);
                    intent.putExtra("alamatnya", detAlamat);
                    intent.putExtra("latnya", latIdle);
                    intent.putExtra("lngnya", lngIdle);
                    startActivity(intent);
                } else if (paramsBtn.equals("Laundry")){
                    //Toast.makeText(MapsActivity.this, "Isi Detail Laundry", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MapsActivity.this, IsiDetailLaundryActivity.class);
                    startActivity(intent);
                } else {
                    //Toast.makeText(MapsActivity.this, "Isi Detail Stempel", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MapsActivity.this, IsiDetailStempelActivity.class);
                    intent.putExtra("alamatStemp", detAlamat);
                    intent.putExtra("latnyaStemp", latIdle);
                    intent.putExtra("lngnyaStemp", lngIdle);
                    startActivity(intent);
                }
            }
        });

        placeAutoComplete = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete);
        placeAutoComplete.setBoundsBias(BOUNDS_SEMARANG);
        placeAutoComplete.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.d("Maps", "Place selected: " + place.getName());
                //LatLng myPos = new LatLng(latti, longi);
                map.clear();
                LatLng myPos = place.getLatLng();

                //MarkerOptions markerOptions = new MarkerOptions();
                //markerOptions.position(place.getLatLng());

                //MarkerOptions markerOptions = new MarkerOptions().position(place.getLatLng());
                //Marker marker = map.addMarker(markerOptions);
                //marker.setPosition(place.getLatLng());

                /*
                MarkerOptions a = new MarkerOptions()
    .position(new LatLng(50,6)));
Marker m = map.addMarker(a);
m.setPosition(new LatLng(50,5));
                 */

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

        /*
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
         */

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

        //for location listener if needed
        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 1, this);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();


        //initializeMap();

        //init pb
        progressBar = findViewById(R.id.pb);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        //Toast.makeText(this, "map ready", Toast.LENGTH_SHORT).show();
        //setUpMapIfNeeded();
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
        //map.getUiSettings().setCompassEnabled(true);
        map.setOnMyLocationButtonClickListener(this);
        map.setOnMyLocationClickListener(this);
        map.setOnCameraIdleListener(this);
        map.setOnCameraMoveStartedListener(this);
        getLocation();

    }

    private void initializeMap() {
        if (map == null) {
            Log.e("MAP", "initializeMap: ");
            MapFragment mapFragment = (MapFragment) getFragmentManager()
                    .findFragmentById(R.id.map);
            if (mapFragment != null) {
                mapFragment.getMapAsync(MapsActivity.this);
            }

            // check if map is created successfully or not
            if (null == map) {
                //Toast.makeText(getApplicationContext(), "Sorry! unable to create maps", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        } else {
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if(location == null) location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (location != null) {
                map.clear();
                double latti = location.getLatitude();
                double longi = location.getLongitude();

                LatLng myPos = new LatLng(latti, longi);

                //MarkerOptions markerOptions = new MarkerOptions().position(myPos);
                //Marker marker = map.addMarker(markerOptions);
                //markerOptions.position(myPos);
                //marker.setPosition(myPos);

                /*
                MarkerOptions markerOptions = new MarkerOptions().position(place.getLatLng());
                Marker marker = map.addMarker(markerOptions);
                marker.setPosition(place.getLatLng());
                 */

                //map.animateCamera(CameraUpdateFactory.newLatLngZoom(myPos, 17.0f));
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(myPos, 17.0f));
                getDescLoc(latti, longi);

                //((EditText)findViewById(R.id.etLocationLat)).setText("Latitude: " + latti);
                //((EditText)findViewById(R.id.etLocationLong)).setText("Longitude: " + longi);
                //Toast.makeText(this, "Get Loc Kondisi 1", Toast.LENGTH_SHORT).show();
            } else {
                map.clear();
                //((EditText)findViewById(R.id.etLocationLat)).setText("Unable to find correct location.");
                //((EditText)findViewById(R.id.etLocationLong)).setText("Unable to find correct location. ");
                double lattiElse = fromMainLat;
                double longiElse = fromMainLng;

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
            Geocoder geo = new Geocoder(MapsActivity.this.getApplicationContext(), Locale.getDefault());
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

                    detAlamat = tDetail.getText().toString();
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

    //for location listener if needed
    /*
    @Override
    public void onLocationChanged(Location location) {
        map.clear();
        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());


        // MarkerOptions markerOptions = new MarkerOptions();
        //markerOptions.position(currentLocation);
        //markerOptions.title("i'm here");

        //map.addMarker(markerOptions);

        // map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 17.0f));

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 17.0f));
        getDescLoc(location.getLatitude(), location.getLongitude());
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
     */


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /*
    @Override
    public void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }
     */

    @Override
    public boolean onMyLocationButtonClick() {
        //Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
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
            if(locationNet != null){

                double latNet = locationNet.getLatitude();
                double lngNet = locationNet.getLongitude();

                //LatLng myPos = new LatLng(latNet, lngNet);
                //MarkerOptions markerOptions = new MarkerOptions().position(myPos);
                //Marker marker = map.addMarker(markerOptions);
                //markerOptions.position(myPos);
                //marker.setPosition(myPos);
                getDescLoc(latNet, lngNet);
            }
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
        //LatLng midLatLng = map.getCameraPosition().target;
        LatLng midLatLng = map.getCameraPosition().target;
        getDescLoc(midLatLng.latitude, midLatLng.longitude);
        latIdle = midLatLng.latitude;
        lngIdle = midLatLng.longitude;
        progressBar.setIndeterminate(false);
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onCameraMoveStarted(int i) {
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);
    }
}
