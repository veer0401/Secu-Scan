package com.example.secuscan2;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.navigation.NavigationView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Device extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);

        // Setup device info TextViews
        TextView tx1 = findViewById(R.id.text_os);
        TextView tx2 = findViewById(R.id.text_hardware);
        TextView tx3 = findViewById(R.id.text_device);
        TextView tx4 = findViewById(R.id.text_model);
        TextView tx5 = findViewById(R.id.text_product);

        // Fetch device details
        String osVersion = Build.VERSION.RELEASE;         // OS version
        String hardware = Build.HARDWARE;                 // Hardware
        String serialNumber = Build.SERIAL;               // Device Serial
        String model = Build.MODEL;                       // Device Model
        String productType = Build.TYPE;                  // Device Type

        // Set text to TextViews
        try {
            tx1.setText(osVersion);
            tx2.setText(hardware);
            tx3.setText(serialNumber);
            tx4.setText(model);
            tx5.setText(productType);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Setup buttons
        Button btUpdates = findViewById(R.id.button_updates);
        Button btManageInstalled = findViewById(R.id.button_manages);

        // Button to navigate to device info settings
        btUpdates.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_DEVICE_INFO_SETTINGS);
            startActivity(intent);
        });

        // Button to manage installed security settings
        btManageInstalled.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
                startActivity(intent);
            } else {
                Toast.makeText(Device.this, "Not supported on your OS version!", Toast.LENGTH_SHORT).show();
            }
        });

        // Setup drawer and navigation
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_device);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            this.finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // Handle home button click (if applicable)
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        // Handle navigation items
        if (id == R.id.nav_device) {
            // Current page
        } else if (id == R.id.nav_scan) {
            startNewActivity(Scan.class);
        } else if (id == R.id.nav_listapp) {
            startNewActivity(Listapp.class);
        } else if (id == R.id.nav_permission) {
            startNewActivity(PermissionList.class);
        } else if (id == R.id.nav_network) {
            startNewActivity(Network.class);
        } else if (id == R.id.nav_findingip) {
            startNewActivity(FindingIP.class);
        } else if (id == R.id.nav_folderlock) {
            startNewActivity(Encryption.class);
        } else if (id == R.id.nav_about) {
            startNewActivity(About.class);
        } else if (id == R.id.nav_share) {
            shareApp();
        }

        // Close drawer
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // Method to start a new activity
    private void startNewActivity(Class<?> targetActivity) {
        Intent intent = new Intent(this, targetActivity);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    // Method to share the app
    private void shareApp() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        String shareDescription = "SecuScan - App to monitor your device.";
        intent.putExtra(Intent.EXTRA_TEXT, shareDescription);
        startActivity(Intent.createChooser(intent, "Share with"));
    }
}
