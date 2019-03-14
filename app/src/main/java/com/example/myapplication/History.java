package com.example.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class History extends AppCompatActivity implements FirebaseCallback {

  private Toolbar toolbar_history;
  private RecyclerView recyclerView;
  private ArrayList<HistoryItem> items;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_history);
    allIDs();
    setSupportActionBar(toolbar_history);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    toolbar_history.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        onBackPressed();
      }
    });

    items = new ArrayList<>();
    readData();
  }

  private void allIDs() {
    toolbar_history = findViewById(R.id.toolbar_history);
    recyclerView = findViewById(R.id.recyclerView);
  }

  @Override
  public void onCallBack(ArrayList<HistoryItem> itemsList) {
    LinearLayoutManager linearLayout = new LinearLayoutManager(History.this, LinearLayoutManager.VERTICAL, false);
    recyclerView.setLayoutManager(linearLayout);
    recyclerView.setHasFixedSize(true);
    HistoryAdapter adapter = new HistoryAdapter(itemsList, History.this);
    recyclerView.setAdapter(adapter);
  }

//  public interface FirebaseCallback {
//    void onCallBack(ArrayList<HistoryItem> itemsList);
//  }

  private void readData() {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference().child("Users")
      .child(mAuth.getCurrentUser().getUid())
      .child("Hành trình");
    // Read from the database
    myRef.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        for (DataSnapshot snap : dataSnapshot.getChildren()) {
          items.add(new HistoryItem(
              snap.getKey(),
              snap.child("Đã đi").getValue().toString(),
              snap.child("Thời gian").getValue().toString()
            )
          );
        }
        onCallBack(items);
      }

      @Override
      public void onCancelled(DatabaseError error) {
        // Failed to read value
        Log.w("====Value", "Failed to read value.", error.toException());
      }
    });
  }
}