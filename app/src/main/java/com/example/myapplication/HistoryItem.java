package com.example.myapplication;

public class HistoryItem {
  private String time, distance, moved;

  public HistoryItem(String time, String distance, String moved) {
    this.time = time;
    this.distance = distance;
    this.moved = moved;
  }

  public String getTime() {
    return time;
  }

  public void setTime(String time) {
    this.time = time;
  }

  public String getDistance() {
    return distance;
  }

  public void setDistance(String distance) {
    this.distance = distance;
  }

  public String getMoved() {
    return moved;
  }

  public void setMoved(String moved) {
    this.moved = moved;
  }
}

