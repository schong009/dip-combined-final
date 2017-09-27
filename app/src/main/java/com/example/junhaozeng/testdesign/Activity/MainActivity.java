package com.example.junhaozeng.testdesign.Activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.junhaozeng.testdesign.Fragments.HealthFragment;
import com.example.junhaozeng.testdesign.Fragments.PetFragment;
import com.example.junhaozeng.testdesign.Fragments.SettingsFragment;
import com.example.junhaozeng.testdesign.R;
import com.example.junhaozeng.testdesign.StepService.StepService;
import com.example.junhaozeng.testdesign.Utils.UpdateUiCallBack;

public class MainActivity extends AppCompatActivity
             {

    private BottomNavigationView bottomNavigationView;
    private Fragment currentFragment;
    private FragmentManager fragmentManager;
    private boolean isBind = false;

    private void init() {
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        fragmentManager = getSupportFragmentManager();
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //Manually displaying the first fragment - one time only
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, PetFragment.newInstance());
        transaction.commit();
        //Used to select an item programmatically
        bottomNavigationView.getMenu().getItem(1).setChecked(true);

    }



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_health:
                    currentFragment = new HealthFragment();
                    break;
                case R.id.navigation_pet:
                    currentFragment = new PetFragment();
                    break;
                case R.id.navigation_settings:
                    currentFragment = new SettingsFragment();
                    break;
            }
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragment_container, currentFragment).commit();
            return true;


        }
    };

    private void setUpService() {
        Intent intent = new Intent(this, StepService.class);
        isBind = bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        startService(intent);
    }

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            // Initialize the steps
            StepService stepService = ((StepService.StepBinder) iBinder).getService();
            // tv_steps.setText(String.valueOf(stepService.getStepCount()));

            stepService.registerCallBack(new UpdateUiCallBack() {
                @Override
                public void updateUi(int steps) {
                    // tv_steps.setText(String.valueOf(steps));
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        setUpService();


    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isBind) {
            this.unbindService(serviceConnection);
        }
    }
}
