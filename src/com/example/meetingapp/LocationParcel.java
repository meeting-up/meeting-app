/*
 * Copyright 2014 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.meetingapp;

import com.google.android.gms.maps.model.LatLng;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * LocationParcel to be used for passing data between activities using intents.
 * Stores information about a place.
 */
public class LocationParcel implements Parcelable {
  private double latitude, longitude;
  private String name;
  private String icon;
  private String reference;
  private String photoreference;
  private Bitmap photo; 
  private double rating;
  private int priceLevel;
  private boolean openNow;
  private String vicinity;
  
  @Override
  public void writeToParcel (Parcel dest, int flags) {
    dest.writeDouble(this.latitude);
    dest.writeDouble(this.longitude);
    dest.writeString(this.name);
    dest.writeString(this.reference);
    dest.writeString(this.photoreference);
    dest.writeString(this.icon);
    dest.writeDouble(this.rating);
    dest.writeInt(this.priceLevel);
    dest.writeByte((byte) (this.openNow? 1: 0));
    dest.writeString(this.vicinity);
  }
  
  public static final Parcelable.Creator<LocationParcel> CREATOR
    = new Parcelable.Creator<LocationParcel>() {
    @Override
    public LocationParcel createFromParcel(Parcel in) {
      return new LocationParcel(in);
    }
    
    @Override
    public LocationParcel[] newArray(int size) {
      return new LocationParcel[size];
    }
  };
  
  public LocationParcel(double lat, double lon, String name, String reference, 
      String photoreference, String icon, double rating, int price, 
      boolean open, String vicinity) {
    this.latitude = lat;
    this.longitude = lon;
    this.name = name;
    this.reference = reference;
    this.photoreference = photoreference;
    this.icon = icon;
    this.rating = rating;
    this.priceLevel = price;
    this.openNow = open;
    this.vicinity = vicinity;
  }
  
  public LocationParcel(Parcel source) {
    this.latitude = source.readDouble();
    this.longitude = source.readDouble();
    this.name = source.readString();
    this.reference = source.readString();
    this.photoreference = source.readString();
    this.icon = source.readString();
    this.rating = source.readDouble();
    this.priceLevel = source.readInt();
    this.openNow = (source.readByte() != 0);
    this.vicinity = source.readString();
  }
  
  @Override
  public int describeContents() {
    return 0;
  }
  
  public Bitmap getPhoto() {
    return this.photo;
  }
  
  public void setPhoto(Bitmap image) {
    this.photo = image;
  }
  
  public double getLatitude() {
    return this.latitude;
  }
  
  public double getLongitude() {
    return this.longitude;
  }
  
  public String getReference() {
    return this.reference;
  }
  
  public String getPhotoreference() {
    return this.photoreference;
  }
  
  public LatLng getLatLng() {
    return new LatLng(this.getLatitude(), this.getLongitude());
  }
  
  public String getName() {
    return this.name;
  }
  
  public void setIcon(String icon) {
    this.icon = icon;
  }
  
  public String getIcon() {
    return this.icon;
  }

  /* Rating is on a scale of 1.0 to 5.0, based off user reviews. */
  public void setRating(double rating) {
    this.rating = rating;
  }
  
  public double getRating() {
    return this.rating;
  }
  
  /**
   * Sets the price level.
   * 0 - Free
   * 1 - Inexpensive
   * 2 - Moderate
   * 3 - Expensive
   * 4 - Very expensive
   * 
   * @param price The price, on a scale of 0 to 4.
   */
  public void setPrice(int price) {
    this.priceLevel = price;
  }
  
  public int getPrice() {
    return this.priceLevel;
  }
  
  public void setOpenNow(boolean open) {
    this.openNow = open;
  }
  
  public boolean getOpenNow() {
    return this.openNow;
  }
  
  public void setVicinity(String vicinity) {
    this.vicinity = vicinity;
  }
  
  public String getVicinity() {
    return this.vicinity;
  }
}

