package com.example.secuscan2;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;

public class About extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // Initialize toolbar
        Toolbar toolbar = findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);

        // Initialize drawer layout and toggle
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Initialize navigation view
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_about);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        // Handle action bar item clicks here
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent = null;

        // Navigation logic
        if (id == R.id.nav_device) {
            intent = new Intent(this, Device.class);
        } else if (id == R.id.nav_scan) {
            intent = new Intent(this, Scan.class);
        } else if (id == R.id.nav_listapp) {
            intent = new Intent(this, Listapp.class);
        } else if (id == R.id.nav_permission) {
            intent = new Intent(this, PermissionList.class);
        } else if (id == R.id.nav_network) {
            intent = new Intent(this, Network.class);
        } else if (id == R.id.nav_findingip) {
            intent = new Intent(this, FindingIP.class);
        } else if (id == R.id.nav_folderlock) {
            intent = new Intent(this, Encryption.class);
        } else if (id == R.id.nav_about) {
            // Already on the About page, do nothing
        } else if (id == R.id.nav_share) {
            // Share intent
            intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            String shareDes = "SecuScan - App to monitor your device.";
            intent.putExtra(Intent.EXTRA_TEXT, shareDes);
            startActivity(Intent.createChooser(intent, "Share with"));
        }

        // Start the new activity if intent is not null
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        // Close the navigation drawer
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
