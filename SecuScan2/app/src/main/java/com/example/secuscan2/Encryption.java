package com.example.secuscan2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class Encryption extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static final int PERMISSIONS_REQUEST_CODE = 0;
    private TextView edthfile;
    private Uri fileUri; // Store the URI instead of file path

    private ActivityResultLauncher<Intent> filePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encryption);
        Toolbar toolbar = findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);

        Button b1 = findViewById(R.id.but_findfile);
        Button b2 = findViewById(R.id.button2);
        Button b3 = findViewById(R.id.button3);
        edthfile = findViewById(R.id.text_pathfile);

        // Initialize the file picker launcher
        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        fileUri = result.getData().getData(); // Store the URI directly
                        if (fileUri != null) {
                            edthfile.setText(getFileName(fileUri)); // Display the file name
                        } else {
                            Log.e("FilePicker", "Could not get file URI");
                            Toast.makeText(this, "Couldn't get file URL", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        b1.setOnClickListener(v -> checkPermissionsAndOpenFilePicker());
        b2.setOnClickListener(v -> encryptFile(fileUri)); // Pass the URI directly
        b3.setOnClickListener(v -> decryptFile());

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_folderlock);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private String getFilePathFromUri(Uri uri) {
        String filePath = null;

        // Check if the URI is a document URI
        if (DocumentsContract.isDocumentUri(getApplicationContext(), uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            String[] split = docId.split(":");
            String type = split[0];

            Uri contentUri = null;
            if ("primary".equalsIgnoreCase(type)) {
                // Handle primary storage
                filePath = Environment.getExternalStorageDirectory() + "/" + split[1];
            } else {
                // Handle non-primary storage (e.g., SD card)
                contentUri = MediaStore.Files.getContentUri("external");
                String selection = MediaStore.Files.FileColumns._ID + "=?";
                String[] selectionArgs = new String[]{split[1]};
                Cursor cursor = getContentResolver().query(contentUri, null, selection, selectionArgs, null);
                if (cursor != null) {
                    int columnIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);
                    if (cursor.moveToFirst() && columnIndex != -1) {
                        filePath = cursor.getString(columnIndex);
                    }
                    cursor.close();
                }
            }
        } else if ("file".equals(uri.getScheme())) {
            // If it's a file URI, get the path directly
            filePath = uri.getPath();
        }

        // Log the final file path or error
        if (filePath == null) {
            Log.e("FilePathError", "File path is null for URI: " + uri.toString());
        } else {
            Log.d("FilePathSuccess", "File path: " + filePath);
        }

        return filePath;
    }

    private void encryptFile(Uri uri) {
        if (uri == null) {
            Toast.makeText(this, "Select a file first!", Toast.LENGTH_SHORT).show();
            return;
        }

        try (InputStream inFile = getContentResolver().openInputStream(uri)) {
            // Use getExternalFilesDir for writable storage
            File outputDir = getExternalFilesDir(null); // This will give you a writable directory
            String fileName = getFileName(uri) + "_encrypted";
            File encryptedFile = new File(outputDir, fileName);

            try (FileOutputStream outFile = new FileOutputStream(encryptedFile);
                 CipherOutputStream cos = new CipherOutputStream(outFile, getCipher(Cipher.ENCRYPT_MODE))) {

                if (inFile == null) {
                    Toast.makeText(this, "File not found", Toast.LENGTH_SHORT).show();
                    return;
                }

                byte[] buf = new byte[1024];
                int readbyte;

                while ((readbyte = inFile.read(buf)) != -1) {
                    cos.write(buf, 0, readbyte);
                }

                cos.flush();
                // Inform the user about the success and the file location
                Toast.makeText(Encryption.this, "Encryption Success! Saved as: " + encryptedFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
                edthfile.setText("Encrypted file: " + encryptedFile.getAbsolutePath()); // Display the file path in the UI
            }
        } catch (Exception e) {
            Log.e("EncryptionError", "Encryption Failed: " + e.getMessage(), e);
            Toast.makeText(this, "Encryption Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    // Helper method to get the file name from the URI
    private String getFileName(Uri uri) {
        String result = null;
        if ("content".equals(uri.getScheme())) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex != -1) {
                        result = cursor.getString(nameIndex);
                    } else {
                        Log.e("FileNameError", "Column DISPLAY_NAME not found");
                    }
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private void decryptFile() {
        if (fileUri == null) {
            Toast.makeText(this, "Select a file first!", Toast.LENGTH_SHORT).show();
            return;
        }

        try (InputStream inFile = getContentResolver().openInputStream(fileUri);
             FileOutputStream outFile = new FileOutputStream(getFileName(fileUri).replace("_encrypted", "_decrypted"));
             CipherInputStream cis = new CipherInputStream(inFile, getCipher(Cipher.DECRYPT_MODE))) {

            if (inFile == null) {
                Toast.makeText(this, "File not found", Toast.LENGTH_SHORT).show();
                return;
            }

            byte[] buf = new byte[1024];
            int readbyte;

            while ((readbyte = cis.read(buf)) != -1) {
                outFile.write(buf, 0, readbyte);
            }

            outFile.flush();
            Toast.makeText(Encryption.this, "Decryption Success!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("DecryptionError", "Decryption Failed: " + e.getMessage(), e);
            Toast.makeText(this, "Decryption Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private Cipher getCipher(int mode) throws Exception {
        byte[] keyBytes = "Wif3NDeRCr3ptErS".getBytes(); // Use a proper key generation method in production
        SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(mode, key);
        return cipher;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED                 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // Permissions granted, open file picker
                openFilePicker();
            } else {
                Toast.makeText(this, "Permissions denied!", Toast.LENGTH_SHORT).show();
            }
        }
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
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void checkPermissionsAndOpenFilePicker() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                // Request MANAGE_EXTERNAL_STORAGE permission
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivity(intent);  // This directs the user to enable the permission
            } else {
                openFilePicker();
            }
        } else {
            // For Android versions below 11, request regular READ/WRITE_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSIONS_REQUEST_CODE);
            } else {
                openFilePicker();
            }
        }
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        filePickerLauncher.launch(intent);
    }
}

