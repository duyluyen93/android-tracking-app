package com.example.myapplication;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class WayPoints extends AppCompatActivity implements OnTheWay {

  @Override
  public LatLng startPosition(LatLng start, ArrayList arrList) {
    int size = arrList.size();
    if (size == 1) {
      return start;
    } else {
      for (int i = 0; i < size; i++) {
        return (LatLng) arrList.get(size - 2);
      }
    }
    return null;
  }

  @Override
  public LatLng endPosition(LatLng end, ArrayList arrList) {
    int size = arrList.size();
    if (size == 1) {
      return end;
    } else {
      for (int i = 0; i < arrList.size(); i++) {
        return (LatLng) arrList.get(size - 1);
      }
    }
    return null;
  }

  @Override
  public int setRouteColor(int i) {
    if (i % 4 == 0) {
      return Color.RED;
    } else if (i % 4 == 1) {
      return Color.CYAN;
    } else if (i % 4 == 2) {
      return Color.BLUE;
    } else if (i % 4 == 3) {
      return Color.MAGENTA;
    }
    return 0;
  }
}
