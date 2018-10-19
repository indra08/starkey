package id.starkey.pelanggan.Laundry.TrxLaundry;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import id.starkey.pelanggan.Kunci.TrxKunci.TrxKunciActivity;
import id.starkey.pelanggan.Laundry.ReviewLaundry.ReviewLaundryActivity;
import id.starkey.pelanggan.R;
import id.starkey.pelanggan.Ratting.RattingActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class TrxLaundryActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {

    GoogleMap map;
    private TextView tDetail;
    private ImageView iTelpon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trx_laundry);

        //init toolbar
        Toolbar toolbar = findViewById(R.id.toolbarTrxLaundry);
        setSupportActionBar(toolbar);
        /*
        if (getSupportActionBar() != null){
            //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_silang);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
         */
        iTelpon = findViewById(R.id.imgTelponLaundry);
        iTelpon.setOnClickListener(this);

        setTitle("Transaksi Laundry");
        Toast.makeText(this, "Klik detail untuk melihat detail\nAtau Klik telepon untuk masuk ke halaman Ratting", Toast.LENGTH_LONG).show();

        //init map
        initializeMap();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        LatLng from = new LatLng(-7.0049918, 110.4551927);
        map.addMarker(new MarkerOptions().position(from).title("Pengguna").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_marker)));
        map.moveCamera(CameraUpdateFactory.newLatLng(from));

        LatLng sydney = new LatLng(-7.01629, 110.4631213);
        map.addMarker(new MarkerOptions().position(sydney).title("Teknisi").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_marker_biru)));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 17));
    }

    private void initializeMap(){
        if (map == null){
            MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapTrxLaundry);
            if (mapFragment != null){
                mapFragment.getMapAsync(TrxLaundryActivity.this);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent in = new Intent(TrxLaundryActivity.this, ReviewLaundryActivity.class);
        startActivity(in);
        finish();
    }

    @Override
    public void onClick(View v) {
        if (v == iTelpon){
            Intent intent = new Intent(TrxLaundryActivity.this, RattingActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
