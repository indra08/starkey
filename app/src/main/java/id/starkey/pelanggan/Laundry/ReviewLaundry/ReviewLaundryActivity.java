package id.starkey.pelanggan.Laundry.ReviewLaundry;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import id.starkey.pelanggan.Laundry.WaitingLaundry.WaitingLaundryActivity;
import id.starkey.pelanggan.R;

public class ReviewLaundryActivity extends AppCompatActivity implements View.OnClickListener {

    private Button bOrderLaundry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_laundry);

        bOrderLaundry = findViewById(R.id.orderLaundry);
        bOrderLaundry.setOnClickListener(this);

        //init toolbar
        Toolbar toolbar = findViewById(R.id.toolbarReviewLaundry);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_silang);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        setTitle("Review Laundry");

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v == bOrderLaundry){
            Intent intent = new Intent(ReviewLaundryActivity.this, WaitingLaundryActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
