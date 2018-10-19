package id.starkey.pelanggan.Laundry.IsiDetailLaundry;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import id.starkey.pelanggan.Laundry.ReviewLaundry.ReviewLaundryActivity;
import id.starkey.pelanggan.R;

import java.util.Calendar;
import java.util.Date;

public class IsiDetailLaundryActivity extends AppCompatActivity implements View.OnClickListener {

    private Spinner spinner;
    String PaketLaundry [] = {"Regular Kiloan(7000)", "Express Kiloan (10000)", "Satuan Boneka Besar (25000)", "Satuan Kemeja (6000)"};
    private Button bLaundry;
    private EditText etPenjemputan;
    private CardView cDetailPenjemputan;
    private static TextView resTgl, resJam, tvTglPenjemputan, tvJamPenjemputan;
    //resTgl, resJam, antarin, TglText, JamText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_isi_detail_laundry);

        //init toolbar
        Toolbar toolbar = findViewById(R.id.toolbarIsiDetailLaundry);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_silang);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        setTitle("Laundry");

        //init et
        //etPenjemputan = findViewById(R.id.txtPenjemputan);
        //etPenjemputan.setOnClickListener(this);

        //init tv
        resTgl = findViewById(R.id.txtTglJemput);
        resJam = findViewById(R.id.txtJamJemput);

        //init cv
        cDetailPenjemputan = findViewById(R.id.cvJadwalPenjemputan);
        cDetailPenjemputan.setOnClickListener(this);

        //init btn
        bLaundry = findViewById(R.id.btnLanjutkanLaundry);
        bLaundry.setOnClickListener(this);

        spinner = findViewById(R.id.spinnerJenisLaundry);
        ArrayAdapter layananAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, PaketLaundry);
        layananAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(layananAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v == bLaundry){
            Intent intent = new Intent(IsiDetailLaundryActivity.this, ReviewLaundryActivity.class);
            startActivity(intent);
        }

        if (v == cDetailPenjemputan){
            getTanggal();
        }
        /*
        if (v == etPenjemputan){
            getTanggal();
        }
         */

    }

    public void getTanggal(){
        DatePickerFragment mDatePicker = new DatePickerFragment();
        //mDatePicker.show(getSupportFragmentManager(), "Pilih Tanggal");
        mDatePicker.show(getFragmentManager(), "Pilih Tanggal");
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Date today = new Date();
            final Calendar c = Calendar.getInstance();

            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

           /* c.setTime(today);
            c.add(Calendar.MONTH,1);
            long minDate = c.getTime().getTime();
            //return new DatePickerDialog(getActivity(), this, year, month, day);
            DatePickerDialog pickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);
            pickerDialog.getDatePicker().setMinDate(minDate);*/

            final DatePickerDialog datePickerDialog = new DatePickerDialog(
                    getActivity(), this,
                    year, month, day);
            DatePicker datePicker = datePickerDialog.getDatePicker();

            // c.add(Calendar.MONTH, +1);
            c.add(Calendar.DAY_OF_MONTH,+20);
            long oneMonthAhead = c.getTimeInMillis();
            datePicker.setMaxDate(oneMonthAhead);
            datePicker.setMinDate(System.currentTimeMillis() - 1000);
            datePickerDialog.show();

            return datePickerDialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // displayCurrentTime.setText("Selected date: " + String.valueOf(year) + " - " + String.valueOf(month) + " - " + String.valueOf(day));
               //Toast.makeText(getActivity(), "Selected date: " + String.valueOf(year) + " - "+ String.valueOf(month) + " - " + String.valueOf(day), Toast.LENGTH_SHORT).show();
            //TglText.setVisibility(View.VISIBLE);
            resTgl.setText(String.valueOf(day)+"/"+String.valueOf(month+1)+"/"+String.valueOf(year));

            getTime();
        }

        private void getTime() {
            TimePicker mTimePicker = new TimePicker();
            mTimePicker.show(getFragmentManager(), "Select time");
        }

        public static class TimePicker extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                final Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

               /* c.add(Calendar.HOUR_OF_DAY,+5);
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
                */

                final TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),this,
                        c.get(Calendar.HOUR_OF_DAY)-3,c.get(Calendar.MINUTE),false);
                timePickerDialog.show();
                return timePickerDialog;
            }
            @Override
            public void onTimeSet(android.widget.TimePicker view, int hourOfDay, int minute) {
                //JamText.setVisibility(View.VISIBLE);
                resJam.setText(String.valueOf(hourOfDay) + ":" + String.valueOf(minute));
                //antarin.setText("Klik untuk merubah");
            }
        }
    }
}
