package id.starkey.pelanggan.Kunci.PilihKunci;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import id.starkey.pelanggan.Kunci.PilihKunci.Almari.FragmentAlmari;
import id.starkey.pelanggan.Kunci.PilihKunci.Lainnya.FragmentLainnyaKunci;
import id.starkey.pelanggan.Kunci.PilihKunci.Mobil.FragmentMobilKunci;
import id.starkey.pelanggan.Kunci.PilihKunci.Motor.FragmentMotorKunci;
import id.starkey.pelanggan.Kunci.PilihKunci.Pintu.FragmentPintu;
import id.starkey.pelanggan.R;

import java.util.ArrayList;
import java.util.List;

public class PilihKunciActivity extends AppCompatActivity {

    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pilih_kunci);

        // Setting ViewPager for each Tabs
        viewPager = findViewById(R.id.viewpagerKunci);
        setupViewPager(viewPager);

        // Set Tabs inside Toolbar
        TabLayout tabs = findViewById(R.id.result_tabs_kunci);
        tabs.setupWithViewPager(viewPager);
    }

    // Add Fragments to Tabs
    private void setupViewPager(ViewPager viewPager) {

        //pintu-motor-mobil-almari-lain
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(new FragmentPintu(), "Pintu");
        adapter.addFragment(new FragmentMotorKunci(), "Motor");
        adapter.addFragment(new FragmentMobilKunci(), "Mobil");
        adapter.addFragment(new FragmentAlmari(), "Almari");
        adapter.addFragment(new FragmentLainnyaKunci(), "Lain");

        viewPager.setAdapter(adapter);
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
