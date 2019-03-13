package com.example.sampletest;

import android.Manifest;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.StitchAppClient;
import com.mongodb.stitch.android.services.mongodb.local.LocalMongoDbService;
import com.mongodb.stitch.core.StitchAppClientConfiguration;

import org.bson.Document;

import java.io.File;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    int REQUEST_CODE=1;

    ActivityCompat.requestPermissions(this, new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE }, REQUEST_CODE);

    Log.d("appLog", "getExternalFilesDirs");
    for (File f : getExternalFilesDirs(null)) {
      Log.d("appLog", f.getAbsolutePath());
    }

    // Got the below path from calling getExternalFilesDirs()
    File newFile =  new File(Environment.getExternalStorageDirectory().getPath() + "/sample.txt");
    try {
      FileOutputStream newFs = new FileOutputStream(newFile);
      newFs.write("test message2".getBytes());
      newFs.close();
      Log.d("appLog", "Successfully wrote test file to SD card");
    } catch (Exception e) {
      Log.e("appLog", "Err: ", e);
    }

    Log.d("appLog", Environment.getExternalStorageDirectory().getPath());

    final StitchAppClientConfiguration.Builder stitchAppClientBuilder = new StitchAppClientConfiguration.Builder();
    Log.d("appLog", "Made Configuration Builder");

    final StitchAppClientConfiguration stitchAppClientConfiguration = stitchAppClientBuilder.withDataDirectory(Environment.getExternalStorageDirectory().getPath()).build();
    Log.d("appLog", "Made Configuration");

    final StitchAppClient stitchAppClient = Stitch.initializeAppClient("stitchdocsexamples-pqwyr", stitchAppClientConfiguration);
    Log.d("appLog", "Made StitchAppClient");

    final MongoClient mongoMobileClient = stitchAppClient.getServiceClient(LocalMongoDbService.clientFactory);
    Log.d("appLog", "Made MongoMobileClient");

    final MongoCollection coll = mongoMobileClient.getDatabase("store").getCollection("items");
    Log.d("appLog", "Made Collection");

    // Test to make sure we can insert / read
    coll.insertOne(new Document().append("hey", "hello"));
    Log.d("appLog", String.format("Found Docs: %s", coll.find().first().toString()));
    Log.d("appLog", String.format("Count of Docs: %d", coll.countDocuments()));
  }
}