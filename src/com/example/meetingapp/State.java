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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

/**
 * A wrapper over the SharedPreferences package, specialised for the 
 * application.
 */
public class State {
  private SharedPreferences mState;
  private SharedPreferences.Editor mStateEditor;

  private static final String PREFERENCES_FILE_KEY =
      "com.example.meetingapp.PREFERENCES_FILE_KEY";
  private static final String CATEGORY = "com.example.meetingapp.CATEGORY";
  private static final String KEYWORD = "com.example.meetingapp.KEYWORD";
  private static final String MIN_PRICE = "com.example.meetingapp.MIN_PRICE";
  private static final String MAX_PRICE = "com.example.meetingapp.MAX_PRICE";
  private static final String OPEN_NOW = "com.example.meetingapp.OPEN_NOW";
 
  /** The default radius to supply if the radius has not been set. */
  public final static int DEFAULT_RADIUS = 1000;
  
  private ArrayList<LocationParcel> mResults;
  private ArrayList<LatLng> mLocations;
  private ArrayList<String> mLocationStrings;
  private LocationParcel mResult;
  private LatLng mCurrLocation;
  private LatLng mMapSelection;
  private LatLng mCentre;
  
  /**
   * Initialises the SharedPreference object and editor.
   */
  @SuppressLint("CommitPrefEdits")
  public State(Context context) {
    mState = context.getSharedPreferences(
        PREFERENCES_FILE_KEY, Context.MODE_PRIVATE);
    mStateEditor = mState.edit();
  }
  
  public void putCategory(String category) {
    mStateEditor.putString(CATEGORY, category);
    mStateEditor.commit();
  }
  
  /**
   * @return    null if the category hasn't been previously set, or 
   * whatever the set category is.
   */
  public String getCategory() {
    return mState.getString(CATEGORY, "");
  }
    
  /**
   * Saves the keywords to memory.
   * 
   * @param input The string which represents the keywords. (all as one String,
   * not as a list of Strings). The separate words should be space-separated.
   */
  public void putKeywords(String input) {
    mStateEditor.putString(KEYWORD, input);
    mStateEditor.commit();
  }
  
  public String getKeywords() {
    return mState.getString(KEYWORD, null);
  }
  
  public void putResults(ArrayList<LocationParcel> results) {
    mResults = results; 
  }
  
  public ArrayList<LocationParcel> getResults() {
    if (mResults == null) {
      mResults = new ArrayList<LocationParcel>();
    }
    return mResults;
  }
  
  public void putLocation(LatLng location) {
    mCurrLocation = location;
  }
  
  public LatLng getLocation() {
    return mCurrLocation;
  }
  
  public void putSelectedLocations(ArrayList<LatLng> locations) {
    mLocations = locations;
  }
  
  public ArrayList<LatLng> getSelectedLocations() {
    if (mLocations == null) {
      return new ArrayList<LatLng>();
    }
    return mLocations;
  }
  
  public void putSelectedLocationsAsStrings(ArrayList<String> locations) {
    mLocationStrings = locations;
  }
  
  public ArrayList<String> getSelectedLocationsAsStrings() {
    if (mLocationStrings == null) {
      mLocationStrings = new ArrayList<String>();
    }
    return mLocationStrings;
  }
  
  public void putResult(LocationParcel result) {
    mResult = result;
  }
  
  public LocationParcel getResult() {
    return mResult;
  }
  
  public void putMapSelection(LatLng selection) {
    mMapSelection = selection;
  }
  
  public LatLng getMapSelection() {
    return mMapSelection;
  }
  
  public void putCentre(LatLng centre) {
    mCentre = centre;
  }
  
  public LatLng getCentre() {
    return mCentre;
  }
  
  public void putMinPrice(int price) {
    mStateEditor.putInt(MIN_PRICE, price);
    mStateEditor.commit();
  }
  
  public int getMinPrice() {
    return mState.getInt(MIN_PRICE, 0);
  }
  
  public void putMaxPrice(int price) {
    mStateEditor.putInt(MAX_PRICE, price);
    mStateEditor.commit();
  }
  
  /**
   * Returns the maximum price, as set by the user.
   * 
   * @return The maximum price set by the user, or "Very Expensive".
   */
  public int getMaxPrice() {
    return mState.getInt(MAX_PRICE, 4);
  }
  
  public void putOpenNow(boolean openNow) {
    mStateEditor.putBoolean(OPEN_NOW, openNow);
    mStateEditor.commit();
  }
  
  public boolean getOpenNow() {
    return mState.getBoolean(OPEN_NOW, false);
  }
  
  /**
   * Clears all set values.
   */
  public void clear() {
    mStateEditor.clear();
    mStateEditor.commit();
    
    mResults = new ArrayList<LocationParcel>();
    mLocations = new ArrayList<LatLng>();
    mLocationStrings = new ArrayList<String>();
    mResult = null;
    mCurrLocation = null;
    mMapSelection = null;
    mCentre = null;
  }
}
