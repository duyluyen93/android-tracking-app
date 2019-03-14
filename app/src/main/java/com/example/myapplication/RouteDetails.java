package com.example.myapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.AvoidType;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.model.Step;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RouteDetails extends AppCompatActivity implements OnMapReadyCallback {

  private GoogleMap mMap;
  private ArrayList<LatLng> movedList;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_route_details);
    Toolbar toolbar_details = findViewById(R.id.toolbar_details);
    movedList = new ArrayList<>();
    setSupportActionBar(toolbar_details);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    toolbar_details.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        onBackPressed();
      }
    });

    readLatLng(new GetLatLng() {
      @Override
      public void viewLatLng(ArrayList<LatLng> latLngs) {
        Log.d("```LatLng", movedList.toString());
        GoogleDirection.withServerKey(getString(R.string.google_maps_key))
          .from(movedList.get(0))
          .and(movedList)
          .to(movedList.get(movedList.size() - 1))
          .alternativeRoute(true)
          .execute(new DirectionCallback() {
            @Override
            public void onDirectionSuccess(Direction direction, String rawBody) {
              if (direction.isOK()) {
                Route route = direction.getRouteList().get(0);
                int legCount = route.getLegList().size();
                for (int i = 0; i < legCount; i++) {
                  Leg leg = route.getLegList().get(i);
                  mMap.addMarker(new MarkerOptions().position(leg.getStartLocation().getCoordination()));
                  if (i == legCount - 1) {
                    mMap.addMarker(new MarkerOptions().position(leg.getEndLocation().getCoordination()));
                  }
                  List<Step> stepList = leg.getStepList();
                  ArrayList<PolylineOptions> polylineOptionList = DirectionConverter.createTransitPolyline(
                    RouteDetails.this, stepList, 5, Color.RED, 3, Color.BLUE);
                  for (PolylineOptions polylineOption : polylineOptionList) {
                    mMap.addPolyline(polylineOption);
                  }
                }
              } else {
                Toast.makeText(RouteDetails.this, "Không thể dò đường", Toast.LENGTH_SHORT).show();
                Log.e("Error ", direction.getStatus());
              }
            }

            @Override
            public void onDirectionFailure(Throwable t) {
              Toast.makeText(RouteDetails.this, "Lỗi " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
          });
      }
    });

    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
      .findFragmentById(R.id.map_details);
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

  private void readLatLng(final GetLatLng getLatLng) {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference().child("Users")
      .child(mAuth.getCurrentUser().getUid())
      .child("Hành trình")
      .child(getIntent().getStringExtra("sendFromAdapter"))
      .child("Đường đi");
    myRef.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        for (DataSnapshot snap : dataSnapshot.getChildren()) {
          movedList.add(new LatLng(
            Double.parseDouble(snap.child("latitude").getValue().toString()),
            Double.parseDouble(snap.child("longitude").getValue().toString())
          ));
        }
        getLatLng.viewLatLng(movedList);
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        Log.w("====Value", "Failed to read value.", error.toException());
      }
    });
  }

  interface GetLatLng {
    void viewLatLng(ArrayList<LatLng> latLngs);
  }
}
