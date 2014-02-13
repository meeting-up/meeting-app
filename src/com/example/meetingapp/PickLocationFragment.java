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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

@SuppressLint("ValidFragment")
public class PickLocationFragment extends Fragment implements 
    GoogleMap.OnMapClickListener,
    GoogleMap.OnInfoWindowClickListener {
  private static final String TAG = PickLocationFragment.class.getSimpleName();
  
  public static final String NEW_LAT = "com.example.meetingapp.NEW_LAT";
  public static final String NEW_LONG = "com.example.meetingapp.NEW_LONG";
  public static final String LOCATION = "com.example.meetingapp.LOCATION";
  public static final int PICK_LOCATION = 1;
  
  private ModifyStateCallback mStateCallback;
  private State mState;
  private LatLng mCurrentLocation;
  
  private View mLayout;
  private GoogleMap mMap;

  @SuppressLint("ValidFragment")
  public PickLocationFragment(ModifyStateCallback callback) {
    mStateCallback = callback;
  }
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setupActionBar();
  }
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    mLayout = inflater.inflate(R.layout.pick_location_fragment, null);

    mState = mStateCallback.getState();
    mCurrentLocation = mState.getLocation();
    setUpMap();
    
    return mLayout;
  }
  
  @Override
  public void onResume() {
    super.onResume();

    mState = mStateCallback.getState();
    mCurrentLocation = mState.getLocation();
  }
  
  @Override
  public void onPause() {
    super.onPause();
  }
  
  @Override
  public void onDestroyView() {
    super.onDestroyView();
    
    Fragment fragment = 
        getActivity().getFragmentManager().findFragmentById(R.id.map);
    if (fragment != null) {
      getActivity().getFragmentManager()
        .beginTransaction().remove(fragment).commit();
    }
  }

  private void setupActionBar() {
    getActivity().getActionBar().setTitle("Pick Location");
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        // This ID represents the Home or Up button. In the case of this
        // activity, the Up button is shown. Use NavUtils to allow users
        // to navigate up one level in the application structure. For
        // more details, see the Navigation pattern on Android Design:
        //
        // http://developer.android.com/design/patterns/navigation.html#up-vs-back
        //
        NavUtils.navigateUpFromSameTask(getActivity());
        return true;
    }
    return super.onOptionsItemSelected(item);
  }
  
  @Override
  public void onMapClick(LatLng location) {
    mMap.clear();
    mMap.addMarker(new MarkerOptions()
        .position(location)
        .title("Click to add location"));
    Toast.makeText(getActivity(), "Click the marker again to select this location", 
        Toast.LENGTH_SHORT).show();
  }
  
  @Override
  public void onInfoWindowClick(Marker marker) {
    mState.putMapSelection(marker.getPosition());
    mStateCallback.putState(mState);
    mStateCallback.showOptions();
  }
  
  
  private void setUpMap() {
    if (mMap == null) {
      mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
      if (mMap == null) {
        throw new RuntimeException("Map could not be instantiated!");
      } else {
        Log.d(TAG, "Map has been instantiated.");
      }
    }
    mMap.setOnMapClickListener(this);
    mMap.setOnInfoWindowClickListener(this);
    
    if (mCurrentLocation != null) {
      mMap.moveCamera(CameraUpdateFactory.newLatLng(mCurrentLocation));
    }
  }
}
