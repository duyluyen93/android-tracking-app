package com.example.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.akexorcist.googledirection.model.Route;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.Marker;
import com.google.android.libraries.places.api.Places;
//import com.google.android.gms.location.places.Place;
//import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
//import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

  private GoogleMap mMap;
  private LatLng start = new LatLng(21.0055546, 105.8412741), end;
  //  private LatLng start = new LatLng(21.0276247, 105.9382813);
//  private LatLng end = new LatLng(21.0287747, 105.850176);
  private ArrayList<LatLng> posList = new ArrayList<>();
  private ArrayList<Double> distances = new ArrayList<>();
  private OnTheWay wayPoints = new WayPoints();
  private FirebaseAuth mAuth;
  private AlertDialog.Builder alert;
  private Button button_tracking, log_test, button_bestWay, button_delete;
  private ImageView note;
  private Toolbar toolbar;
  private Chronometer countTime;
  private Leg leg;
  private DecimalFormat decimal = new DecimalFormat("0.0");
  private Marker marker;
  private String[] colors = {"#00a51b", "#646262", "#b7b7b7"};

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    mAuth = FirebaseAuth.getInstance();
    alert = new AlertDialog.Builder(this);
    final FirebaseDatabase database = FirebaseDatabase.getInstance();

    if (!Places.isInitialized()) {
      Places.initialize(
        this,
        "AIzaSyCCsYpflE4JGPJLMMjeE4o796M7ClT9YB8");
    }

    final AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
      getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

// Specify the types of place data to return.
    autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.LAT_LNG, Place.Field.NAME));

// Set up a PlaceSelectionListener to handle the response.
    autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
      @Override
      public void onPlaceSelected(Place place) {
        // TODO: Get info about the selected place.
        Log.d("TAG", "Place: " + place.getName() + ", " + place.getLatLng());
        mMap.clear();
        end = place.getLatLng();
//        posList.add(end);
        marker = mMap.addMarker(new MarkerOptions().position(end));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
      }

      @Override
      public void onError(Status status) {
        // TODO: Handle the error.
        Log.d("", "An error occurred: " + status);
        Toast.makeText(MainActivity.this, "Lỗi", Toast.LENGTH_SHORT).show();
      }
    });

    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
      .findFragmentById(R.id.map);
    mapFragment.getMapAsync(this);
    allIDs();
    setSupportActionBar(toolbar);

    final PrimaryDrawerItem item_login = new PrimaryDrawerItem()
      .withName("Đăng nhập")
      .withIcon(R.drawable.ic_lock_outline_black_24dp);
    final PrimaryDrawerItem item_signin = new PrimaryDrawerItem()
      .withName("Đăng ký")
      .withIcon(R.drawable.ic_account_circle_black_24dp);
    final SecondaryDrawerItem history = new SecondaryDrawerItem()
      .withName("Lịch sử di chuyển")
      .withIcon(R.drawable.ic_subway_black_24dp);
    final SecondaryDrawerItem tracking = new SecondaryDrawerItem()
      .withName("Theo dõi phương tiện")
      .withIcon(R.drawable.ic_eye_black_24dp);
    final PrimaryDrawerItem logout = new PrimaryDrawerItem()
      .withName("Đăng xuất")
      .withIcon(R.drawable.ic_lock_outline_black_24dp);

    AccountHeader header = new AccountHeaderBuilder()
      .withActivity(this).withHeaderBackground(R.drawable.black_banner)
      .addProfiles(
        new ProfileDrawerItem()
          .withName(mAuth.getCurrentUser() != null ? "Tài khoản của bạn" : "")
          .withEmail(mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getEmail() : "Bạn chưa đăng nhập")
      ).build();

    new DrawerBuilder().withActivity(this).withToolbar(toolbar).addDrawerItems(
      mAuth.getCurrentUser() != null ? history : item_login,
      mAuth.getCurrentUser() != null ? tracking : item_signin,
      new DividerDrawerItem(),
      mAuth.getCurrentUser() != null ? logout : null).withAccountHeader(header)
      // click từng item trên menu:
      .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
        @Override
        public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
          if (drawerItem == logout) {
            logOut();
          } else if (drawerItem == item_signin) {
            Intent signUp = new Intent(MainActivity.this, SignUp.class);
            startActivity(signUp);
          } else if (drawerItem == item_login) {
            Intent logIn = new Intent(MainActivity.this, LogIn.class);
            startActivity(logIn);
          } else if (drawerItem == history) {
            Intent history = new Intent(MainActivity.this, History.class);
            startActivity(history);
          } else if (drawerItem == tracking) {
            Intent tracking = new Intent(MainActivity.this, Tracking.class);
            startActivity(tracking);
          }
          return false;
        }
      }).build();

    driverPosition();

    button_tracking.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        timeCount(false);
        new Timer().scheduleAtFixedRate(new TimerTask() {
          @Override
          public void run() {
            posList.add(currentPosition());
            if (posList.size() > 1) {
              Log.d("```LIST", posList.toString());
              GoogleDirection.withServerKey(getString(R.string.google_maps_key))
                .from(wayPoints.startPosition(currentPosition(), posList))
                .to(wayPoints.endPosition(currentPosition(), posList))
                .execute(new DirectionCallback() {
                  @Override
                  public void onDirectionSuccess(Direction direction, String rawBody) {
                    if (direction.isOK()) {
                      Route route = direction.getRouteList().get(0);
                      ArrayList<LatLng> positionList = route.getLegList().get(0).getDirectionPoint();
                      mMap.addPolyline(DirectionConverter.createPolyline(
                        MainActivity.this,
                        positionList, 4,
                        wayPoints.setRouteColor(posList.size())));
                      leg = route.getLegList().get(0);
//                      Toast.makeText(MainActivity.this,
//                        "Khoảng cách: " + leg.getDistance().getText() + "\n"
//                          + "Thời gian: " + leg.getDuration().getText().replaceAll("mins|min", "phút"),
//                        Toast.LENGTH_SHORT).show();
                      String string_distance = leg.getDistance().getText();
                      double distance = Double.parseDouble(string_distance.replaceAll("[^0-9.]", ""));
                      distances.add(string_distance.contains("km")
                        ? distance
                        : formatNumber(distance)
                      );
                      Log.d("=====KC", "" + distances);
                      // khi khoảng cách = 0 thì thư viện trả về khoảng cách = "1m"
//                Toast.makeText(MainActivity.this, ""+distances, Toast.LENGTH_SHORT).show();
                      // Hiện đồng hồ

                      final LinearLayout info = findViewById(R.id.info);
                      final Button stop = findViewById(R.id.stop);    //dừng đếm và gửi dữ liệu lên Firebase
                      stop.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                          clicked = false;
                          Toast.makeText(MainActivity.this, "Đã lưu lại hành trình của bạn", Toast.LENGTH_SHORT).show();
                          Chronometer countTime = findViewById(R.id.time);
                          countTime.stop();
                          String date = new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());
                          String hour = new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime());
                          String time = date + " lúc " + hour;

                          DatabaseReference myRef = database.getReference().child("Users")
                            .child(mAuth.getCurrentUser().getUid())
                            .child("Hành trình")
                            .child(time);
                          myRef.child("Đã đi").setValue(sumOfDistances(distances));
                          myRef.child("Thời gian").setValue(countTime.getText());
                          myRef.child("Đường đi").setValue(posList);
                          stop.setVisibility(View.GONE);
                          info.setVisibility(View.GONE);
                          button_delete.setVisibility(View.VISIBLE);
//                    // Log.d("```LIST", posList.toString());
                        }
                      });
                    } else {
                      Toast.makeText(MainActivity.this, "Không thể dò đường", Toast.LENGTH_SHORT).show();
                      Log.e("Error ", direction.getStatus());
                    }
                  }

                  @Override
                  public void onDirectionFailure(Throwable t) {
                    Toast.makeText(MainActivity.this, "Lỗi " + t.getMessage(), Toast.LENGTH_SHORT).show();
                  }
                });
            }
          }
        }, 0, 7000);
//        button_tracking.setVisibility(View.GONE);
        button_delete.setVisibility(View.GONE);
      }
    });

    button_bestWay.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (end == null) {
          alert.setTitle("Thông báo").setMessage("Bạn cần chọn điểm đến trước!")
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
              }
            }).create().show();
        } else {
          mMap.addMarker(new MarkerOptions().position(currentPosition()));
          GoogleDirection.withServerKey(getString(R.string.google_maps_key))
            .from(currentPosition())
            .to(end)
            .alternativeRoute(true)
            .execute(new DirectionCallback() {
              @Override
              public void onDirectionSuccess(Direction direction, String rawBody) {
                if (direction.isOK()) {
                  for (int i = 0; i < direction.getRouteList().size(); i++) {
                    Route route = direction.getRouteList().get(i);
                    String color = colors[i % colors.length];
                    ArrayList<LatLng> directionPositionList = route.getLegList().get(0).getDirectionPoint();
                    mMap.addPolyline(DirectionConverter.createPolyline(
                      MainActivity.this, directionPositionList,
                      7,
                      Color.parseColor(color))
                    );
                  }
                }
              }

              @Override
              public void onDirectionFailure(Throwable t) {
                Log.e("ERROR", t.getMessage());
              }
            });
          if (mAuth.getCurrentUser() != null) {
            button_tracking.setVisibility(View.VISIBLE);
            button_delete.setVisibility(View.VISIBLE);
            button_bestWay.setVisibility(View.GONE);
            findViewById(R.id.autocomplete_fragment).setVisibility(View.GONE);
            note.setVisibility(View.VISIBLE);
          } // phải login mới hiện ra phần này
          else {
            Toast.makeText(MainActivity.this, "Để thực hiện theo dõi di chuyển, hãy đăng nhập", Toast.LENGTH_LONG).show();
          }
        }
      }
    });

    button_delete.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        button_tracking.setVisibility(View.GONE);
        log_test.setVisibility(View.GONE);
        button_delete.setVisibility(View.GONE);
        button_bestWay.setVisibility(View.VISIBLE);
        findViewById(R.id.autocomplete_fragment).setVisibility(View.VISIBLE);
        note.setVisibility(View.GONE);
        mMap.clear();
      }
    });
  }

  private void allIDs() {
    button_tracking = findViewById(R.id.button_tracking);
    log_test = findViewById(R.id.test_button);
    toolbar = findViewById(R.id.toolbar);
    countTime = findViewById(R.id.time);
    button_bestWay = findViewById(R.id.button_bestWay);
    button_delete = findViewById(R.id.button_delete);
    note = findViewById(R.id.note);
  }

  private void logOut() {
    alert.setTitle("Thông báo").setMessage("Bạn có muốn đăng xuất không?")
      .setPositiveButton("Có", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
          mAuth.signOut();
          Log.i("Sign out", "" + mAuth.getCurrentUser());
          recreate();
        }
      })
      .setNegativeButton("Không", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
          dialog.dismiss();
        }
      });
    alert.show();
  }

  private void driverPosition() {
    // Tự động update vị trí hiện tại sau 5 giây
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    if (mAuth.getCurrentUser() != null) {
      new Timer().scheduleAtFixedRate(new TimerTask() {
        @Override
        public void run() {
          DatabaseReference myRef = database.getReference().child("Drivers")
            .child(mAuth.getCurrentUser().getUid());
          myRef.child("Vị trí").setValue(currentPosition());
        }
      }, 0, 3000);
    }
    else {
      Toast.makeText(this, "ABC", Toast.LENGTH_SHORT).show();
    }
  }

  @Override
  public void onMapReady(GoogleMap googleMap) {
    mMap = googleMap;
    mMap.moveCamera(CameraUpdateFactory.newLatLng(start));
    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(start, 15));
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      return;
    }
    mMap.setMyLocationEnabled(true);
  }

  private LatLng currentPosition() {
    LocationManager locationManager = (LocationManager) getSystemService(
      Context.LOCATION_SERVICE
    );
    if (ActivityCompat.checkSelfPermission(
      this, Manifest.permission.ACCESS_FINE_LOCATION)
      != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
      this,
      Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      return null;
    }
    Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
    return current;
  }

  private String sumOfDistances(ArrayList<Double> arrayList) {
    double sum = 0.0;
    for (int i = 0; i < arrayList.size(); i++) {
      sum += arrayList.get(i);
    }
    return sum + " km";
  }

  private void timeCount(boolean clicked) {
    RelativeLayout map_layout = findViewById(R.id.map_layout);
    LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
    View view = inflater.inflate(R.layout.clock, null, false);
    if (!clicked) {
      map_layout.addView(view);
    }
    else {
      map_layout.removeView(view);
    }
    Chronometer countTime = findViewById(R.id.time);
    countTime.start();
  }

  private double formatNumber(double distance) {
    if (distance < 100) {
      return 0.0;
    } else {
      return Double.parseDouble(decimal.format(distance / 1000));
    }
  }

  @Override
  protected void onResume() {
//    Khi activity bắt đầu chạy thì chờ data gửi đến
    Log.d("!!RESUME", "RESUME");
    boolean reload = getIntent().getBooleanExtra("reload", false);
    if (reload) {     // nếu gửi true thì reload
      recreate();
    }
    super.onResume();
  }
}