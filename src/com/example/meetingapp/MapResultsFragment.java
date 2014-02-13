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
import android.widget.FrameLayout;
import java.util.ArrayList;

@SuppressLint("ValidFragment")
public class MapResultsFragment extends Fragment {
  private static final String TAG = MapResultsFragment.class.getSimpleName();
  
  private ModifyStateCallback mStateCallback;
  private Callback mCallback;
  private State mState;

  private FrameLayout mLayout;
  private GoogleMap mMap;
  
  public interface Callback {
    public void showDetails(LocationParcel result);
  }
  
  public MapResultsFragment(Callback callback, ModifyStateCallback stateCallback) {
    mStateCallback = stateCallback;
    mCallback = callback;
  }
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    mLayout = (FrameLayout) inflater.inflate(
        R.layout.map_results_fragment, null);

    mState = mStateCallback.getState();
    setupActionBar();
    
    return mLayout;
  }
  
  @Override
  public void onResume() {
    super.onResume();
    setUpMap();
    
    ArrayList<LocationParcel> locations = mState.getResults();
    for (LocationParcel parcel : locations) {
      mMap.addMarker(new MarkerOptions()
          .position(new LatLng(parcel.getLatitude(), parcel.getLongitude()))
          .title(parcel.getName()));
    }
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    
    MapFragment fragment = 
        (MapFragment) getFragmentManager().findFragmentById(R.id.result_map);
    
    if (fragment != null) {
      getFragmentManager().beginTransaction().remove(fragment).commit();
    }
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
  
  /**
   * Set up the {@link android.app.ActionBar}.
   */
  private void setupActionBar() {
    getActivity().getActionBar().setTitle("Result Locations");
  }
  
  private void setUpMap() {
    if (mMap == null) {
      mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.result_map)).getMap();
      if (mMap == null) {
        throw new RuntimeException("Map could not be instantiated!");
      }
    }
    
    LatLng centre = getCentre(mState.getResults());
    LatLng currLocation = mState.getLocation();
    
    GoogleMap.OnInfoWindowClickListener listener = new GoogleMap.OnInfoWindowClickListener() {
      /**
       * If an info window is clicked, bring up the details for that place.
       */
      @Override
      public void onInfoWindowClick(Marker marker) {
        String placeName = marker.getTitle();
        
        ArrayList<LocationParcel> locations = mState.getResults();
        for (LocationParcel location : locations) {
          if (location.getName().equals(placeName)) {
            mCallback.showDetails(location);
          }
        }
      }
    };
    mMap.setOnInfoWindowClickListener(listener);
    
    if (centre != null) {
      mMap.moveCamera(CameraUpdateFactory.newLatLng(centre));
    } else if (currLocation != null) {
      mMap.moveCamera(CameraUpdateFactory.newLatLng(currLocation));
    } else {
      Log.d(TAG, "Both currLocation and the centre of locations are unavailable");
    }
  }

  private LatLng getCentre(ArrayList<LocationParcel> locations) {
    double latitudeSum = 0;
    double longitudeSum = 0;
    int numLocations = locations.size();
    
    for (LocationParcel location : locations) {
      latitudeSum += location.getLatitude();
      longitudeSum += location.getLongitude();
    }
    LatLng centre = new LatLng(
        latitudeSum / numLocations, longitudeSum / numLocations);
    
    if (numLocations != 0) {
      mState.putCentre(centre);
      mStateCallback.putState(mState);
    } else {
      centre = null;
    }
    
    return centre;
  }
}
