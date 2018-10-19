package id.starkey.pelanggan.Stempel.EnlargeImgStempel;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;

import id.starkey.pelanggan.R;
import com.squareup.picasso.Picasso;

public class EnlargeImgStempelActivity extends AppCompatActivity {

    private ImageView imgStempel;
    private String urlImageStempel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enlarge_img_stempel);

        //init toolbar
        Toolbar toolbar = findViewById(R.id.toolbarEnlargeImgStempel);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_silang);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
        toolbar.setTitleTextColor(Color.parseColor("#000000"));
        setTitle("Stempel");

        //get param url img
        Bundle bundle = getIntent().getExtras();
        urlImageStempel = bundle.getString("gambarstempel");

        //init image view
        imgStempel = findViewById(R.id.imgBigStempel);
        Picasso.with(EnlargeImgStempelActivity.this)
                .load(urlImageStempel)
                .placeholder(R.drawable.progress_animation)
                .into(imgStempel);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}
