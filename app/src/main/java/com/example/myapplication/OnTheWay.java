package com.example.myapplication;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public interface OnTheWay {
  LatLng startPosition(LatLng start, ArrayList arrList);
  LatLng endPosition(LatLng end, ArrayList arrList);
  int setRouteColor(int i);
}
