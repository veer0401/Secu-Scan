package com.example.secuscan2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FindingIP extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextView tvJson;
    private EditText edip;
    private double lat, lon;
    private ExecutorService executorService;

    private static final String TAG = "FindingIP"; // For logging purposes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finding_ip);
        Toolbar toolbar = findViewById(R.id.toolbar4);
        setSupportActionBar(toolbar);

        edip = findViewById(R.id.edit_ipin);
        tvJson = findViewById(R.id.textJsonHere);
        Button btfind = findViewById(R.id.but_findip);
//        Button btmap = findViewById(R.id.button_map);

        // Set up button to open map
  /*      btmap.setOnClickListener(view -> {
            if (lat != 0 && lon != 0) {
                Intent i = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("geo:0,0?q=" + lat + "," + lon));
                startActivity(i);
            } else {
                Toast.makeText(FindingIP.this, "Latitude and Longitude not available.", Toast.LENGTH_SHORT).show();
            }
        });

  */      executorService = Executors.newSingleThreadExecutor(); // Initialize executor service

        // Set up button to fetch IP data
        btfind.setOnClickListener(v -> {
            String ipAddress = edip.getText().toString().trim();
            Log.d(TAG, "IP Address entered: " + ipAddress);

            if (!ipAddress.isEmpty() && isValidIP(ipAddress)) {
                fetchData("https://ip-api.com/json/" + ipAddress);
                Toast.makeText(FindingIP.this, "Searching for IP details...", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(FindingIP.this, "Please enter a valid IP address.", Toast.LENGTH_SHORT).show();
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_findingip);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void fetchData(String urlString) {
        executorService.execute(() -> {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("User-Agent", "Mozilla/5.0"); // Add User-Agent header
                connection.connect();

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    updateUI("Error: Unable to connect to the API. Response code: " + connection.getResponseCode());
                    return;
                }

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder buffer = new StringBuilder();

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                String jsonAPI = buffer.toString();
                JSONObject parentObject = new JSONObject(jsonAPI);

                if (!parentObject.getString("status").equals("success")) {
                    updateUI("Error: " + parentObject.getString("message"));
                    return;
                }

                lat = parentObject.getDouble("lat");
                lon = parentObject.getDouble("lon");
                String result = "AsKnow: " + parentObject.getString("as") + "\n" +
                        "City: " + parentObject.getString("city") + "\n" +
                        "Country: " + parentObject.getString("country") + "\n" +
                        "ISP: " + parentObject.getString("isp") + "\n" +
                        "Latitude: " + lat + "\n" +
                        "Longitude: " + lon;

                updateUI(result);

            } catch (MalformedURLException e) {
                updateUI("Error: Malformed URL.");
            } catch (IOException e) {
                updateUI("Error: Unable to retrieve data.");
            } catch (JSONException e) {
                updateUI("Error: JSON parsing error.");
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

}

    // Update UI on the main thread
    private void updateUI(String result) {
        runOnUiThread(() -> {
            tvJson.setText(result);
            Toast.makeText(FindingIP.this, "IP details retrieved.", Toast.LENGTH_SHORT).show();
        });
    }

    // Validate the IP address format
    private boolean isValidIP(String ip) {
        String ipRegex =
                "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
        boolean isValid = ip.matches(ipRegex);
        Log.d(TAG, "Is valid IP: " + isValid);
        return isValid;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown(); // Shutdown the executor service to free resources
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_device) {
            startActivity(new Intent(this, Device.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        } else if (id == R.id.nav_scan) {
            startActivity(new Intent(this, Scan.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        } else if (id == R.id.nav_listapp) {
            startActivity(new Intent(this, Listapp.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        } else if (id == R.id.nav_permission) {
            startActivity(new Intent(this, PermissionList.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        } else if (id == R.id.nav_network) {
            startActivity(new Intent(this, Network.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        } else if (id == R.id.nav_folderlock) {
            startActivity(new Intent(this, Encryption.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        } else if (id == R.id.nav_about) {
            startActivity(new Intent(this, About.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        } else if (id == R.id.nav_share) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            String shareDes = "SecuScan - App to monitor your device.";
            intent.putExtra(Intent.EXTRA_TEXT, shareDes);
            startActivity(Intent.createChooser(intent, "Share with"));
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
