package com.example.secuscan2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import java.util.ArrayList;
import java.util.List;

public class PermissionList extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    String permissiondefault = "default";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbedapp);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar9);
        setSupportActionBar(toolbar);

        final Spinner cSpinner = (Spinner) findViewById(R.id.spinner);
        cSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                permissiondefault = getPermissionFromPosition(position);
                displayInstalledApps(); // Call the method to display installed apps based on selected permission
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_permission);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private String getPermissionFromPosition(int position) {
        switch (position) {
            case 0: return Manifest.permission.INTERNET;
            case 1: return Manifest.permission.READ_EXTERNAL_STORAGE;
            case 2: return Manifest.permission.CAMERA;
            case 3: return Manifest.permission.RECORD_AUDIO;
            case 4: return Manifest.permission.ACCESS_COARSE_LOCATION;
            case 5: return Manifest.permission.READ_SMS;
            case 6: return Manifest.permission.READ_CONTACTS;
            case 7: return Manifest.permission.CALL_PHONE;
            case 8: return Manifest.permission.READ_LOGS;
            case 9: return Manifest.permission.BIND_DEVICE_ADMIN;
            case 10: return Manifest.permission.REBOOT;
            default: return "default";
        }
    }

    private void displayInstalledApps() {
        ListView userInstalledApps = (ListView) findViewById(R.id.lists);
        List<AppList> installedApps = getInstalledApps();
        AppAdapter installedAppAdapter = new AppAdapter(PermissionList.this, installedApps);
        userInstalledApps.setAdapter(installedAppAdapter);
    }

    private List<AppList> getInstalledApps() {
        PackageManager p = getPackageManager();
        List<AppList> res = new ArrayList<>();
        final List<PackageInfo> apps = p.getInstalledPackages(PackageManager.GET_PERMISSIONS);

        for (PackageInfo packageInfo : apps) {
            boolean isSystemApp = isSystemPackage(packageInfo);
            if (packageInfo.requestedPermissions == null) continue;

            for (String permission : packageInfo.requestedPermissions) {
                // Check if the app requests the selected permission
                if (TextUtils.equals(permission, permissiondefault) && !isSystemApp) {
                    String appName = packageInfo.applicationInfo.loadLabel(p).toString();
                    Drawable icon = packageInfo.applicationInfo.loadIcon(p);
                    res.add(new AppList(appName, icon));
                }
            }
        }
        return res;
    }

    private boolean isSystemPackage(PackageInfo pkgInfo) {
        return ((pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_device) {
            Intent intent = new Intent(this, Device.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        } else if (id == R.id.nav_scan) {
            Intent intent = new Intent(this, Scan.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        } else if (id == R.id.nav_listapp) {
            Intent intent = new Intent(this, Listapp.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        } else if (id == R.id.nav_network) {
            Intent intent = new Intent(this, Network.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        } else if (id == R.id.nav_findingip) {
            Intent intent = new Intent(this, FindingIP.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        } else if (id == R.id.nav_folderlock) {
            Intent intent = new Intent(this, Encryption.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        } else if (id == R.id.nav_about) {
            Intent intent = new Intent(this, About.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        } else if (id == R.id.nav_share) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            String shareDes = "SecuScan - App to monitor your device.";
            intent.putExtra(Intent.EXTRA_TEXT, shareDes);
            startActivity(Intent.createChooser(intent, "Share with"));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}