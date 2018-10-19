package id.starkey.pelanggan.Laundry.WaitingLaundry;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import id.starkey.pelanggan.Laundry.TrxLaundry.TrxLaundryActivity;
import id.starkey.pelanggan.R;

public class WaitingLaundryActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView iCancelLaundry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_laundry);

        iCancelLaundry = findViewById(R.id.imgCancelLaundry);
        iCancelLaundry.setOnClickListener(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent jump = new Intent(WaitingLaundryActivity.this, TrxLaundryActivity.class);
                startActivity(jump);
                finish();
            }
        }, 3000);

    }

    @Override
    public void onClick(View v) {
        if (v == iCancelLaundry){
            finish();
        }
    }
}
