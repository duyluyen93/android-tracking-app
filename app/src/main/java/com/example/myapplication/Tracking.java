package com.example.myapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Tracking extends AppCompatActivity implements OnMapReadyCallback {

  private GoogleMap mMap;
  private ArrayList<LatLng> transports;
  private ArrayList<Marker> markers;
  private Marker marker;
  DatabaseReference myRef;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_tracking);
    Toolbar toolbar_tracking = findViewById(R.id.toolbar_tracking);
    final Button test_button = findViewById(R.id.test_button);
    setSupportActionBar(toolbar_tracking);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    toolbar_tracking.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        onBackPressed();
      }
    });

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    myRef = database.getReference().child("Drivers");

    transports = new ArrayList<>();
    markers = new ArrayList<>();

    viewTransports(new ViewMarkers() {
      @Override
      public void viewTransportMarkers(ArrayList<LatLng> latLngs) {
        for (Marker marker : markers) {
          marker.remove();
          // xoá tất cả các marker đang có trên map
        }
        for (LatLng latLng : transports) {
          markers.add(mMap.addMarker(
            new MarkerOptions().position(latLng)
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.transport_icon))
          ));
          // update tất cả các marker trên map theo vị trí mới
        }
        Log.d("````", transports.toString());
      }
    });

    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
      .findFragmentById(R.id.map_tracking);
    mapFragment.getMapAsync(this);
  }

  @Override
  public void onMapReady(GoogleMap googleMap) {
    mMap = googleMap;
    mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(21.0055546, 105.8412741)));
    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(21.0055546, 105.8412741), 15));
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      return;
    }
    mMap.setMyLocationEnabled(true);
  }

  interface ViewMarkers {
    void viewTransportMarkers(ArrayList<LatLng> latLngs);
  }

  private void viewTransports(final ViewMarkers viewMarkers) {
    myRef.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        transports.clear();
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
          transports.add(new LatLng(
            Double.parseDouble(snapshot.child("Vị trí").child("latitude").getValue().toString()),
            Double.parseDouble(snapshot.child("Vị trí").child("longitude").getValue().toString())
          ));
        }
        Log.d("```TRANSPORT", transports.toString());
        viewMarkers.viewTransportMarkers(transports);
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {
        Log.w("LỖI", databaseError.toException());
      }
    });
  }
}