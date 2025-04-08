package com.example.secuscan2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView; // Import CardView

import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Set the content view to activity_main layout

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar6);
        setSupportActionBar(toolbar);

        // Set up the navigation drawer
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Set up the navigation view
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Set up card view click listeners
        setupCardViewListeners();
    }

    private void setupCardViewListeners() {
        // Find card views by their IDs and cast them to CardView
        CardView cardView1 = findViewById(R.id.cardView1);
        CardView cardView2 = findViewById(R.id.cardView2);
        CardView cardView3 = findViewById(R.id.cardView3);
        CardView cardView4 = findViewById(R.id.cardView4);

        // Set click listeners for each card view
        cardView1.setOnClickListener(v -> startActivity(new Intent(this, Network.class)));
        cardView2.setOnClickListener(v -> startActivity(new Intent(this, Encryption.class)));
        cardView3.setOnClickListener(v -> startActivity(new Intent(this, PermissionList.class)));
        cardView4.setOnClickListener(v -> startActivity(new Intent(this, Device.class)));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        // Close the drawer if it is open, otherwise handle back press normally
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        // Handle toolbar home button click
        if (id == android.R.id.home) {
            finish(); // Close the activity
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent = null;

        // Determine which navigation item was selected and set up the corresponding intent
        if (id == R.id.nav_device) {
            intent = new Intent(this, Device.class);
        } else if (id == R.id.nav_scan) {
            intent = new Intent(this, Scan.class);
        } else if (id == R.id.nav_listapp) {
            intent = new Intent(this, Listapp.class);
        } else if (id == R.id.nav_network) {
            intent = new Intent(this, Network.class);
        } else if (id == R.id.nav_findingip) {
            intent = new Intent(this, FindingIP.class);
        } else if (id == R.id.nav_about) {
            intent = new Intent(this, About.class);
        } else if (id == R.id.nav_share) {
            intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            String shareDes = "SecuScan - App to monitor your device.";
            intent.putExtra(Intent.EXTRA_TEXT, shareDes);
            startActivity(Intent.createChooser(intent, "Share with"));
        }

        // Start the activity if the intent is not null
        if (intent != null) {
            startActivity(intent);
        }

        // Close the navigation drawer
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}