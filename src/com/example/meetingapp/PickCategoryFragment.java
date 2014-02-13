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
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.geocodeservice.GeocodeResponse;
import com.example.geocodeservice.GeocodeService;

/**
 * Allow the user to select a category for the meeting place.
 */
@SuppressLint("ValidFragment")
public class PickCategoryFragment extends Fragment {
  public static final String TAG = PickCategoryFragment.class.getSimpleName();
  
  /** Refers to the selected category. */
  public static final String CATEGORY = "com.example.meetingapp.CATEGORY";

  private final static GeocodeService mGeocodeService = new GeocodeService();
  
  private static final String[] mCategories = new String[] {
    "Accounting", "Airport", "Amusement Park", "Aquarium", 
    "Art Gallery", "Atm", "Bakery", "Bank", 
    "Bar", "Beauty Salon", "Bicycle Store", "Book Store", 
    "Bowling Alley", "Bus Station", "Cafe", "Campground", 
    "Car Dealer", "Car Rental", "Car Repair", "Car Wash", 
    "Casino", "Cemetery", "Church", "City Hall", 
    "Clothing Store", "Convenience Store", "Courthouse", "Dentist", 
    "Department Store", "Doctor", "Electrician", "Electronics Store", 
    "Embassy", "Establishment", "Finance", "Fire Station", 
    "Florist", "Food", "Funeral Home", "Furniture Store", 
    "Gas Station", "General Contractor", "Grocery Or Supermarket", "Gym", 
    "Hair Care", "Hardware Store", "Health", "Hindu Temple", 
    "Home Goods Store", "Hospital", "Insurance Agency", "Jewelry Store", 
    "Laundry", "Lawyer", "Library", "Liquor Store", 
    "Local Government Office", "Locksmith", "Lodging", "Meal Delivery", 
    "Meal Takeaway", "Mosque", "Movie Rental", "Movie Theater", 
    "Moving Company", "Museum", "Night Club", "Painter", 
    "Park", "Parking", "Pet Store", "Pharmacy", 
    "Physiotherapist", "Place Of Worship", "Plumber", "Police", 
    "Post Office", "Real Estate Agency", "Restaurant", "Roofing Contractor", 
    "Rv Park", "School", "Shoe Store", "Shopping Mall", 
    "Spa", "Stadium", "Storage", "Store", 
    "Subway Station", "Synagogue", "Taxi Stand", "Train Station", 
    "Travel Agency", "University", "Veterinary Care", "Zoo"
  };
  
  private ModifyStateCallback mCallback;
  private TextView mSelectedView;
  private AutoCompleteTextView mCategoryTextView;
  private LinearLayout mLayout;
  private State mState;
  private ArrayAdapter<String> mAdapter;
  
  public PickCategoryFragment(ModifyStateCallback callback) {
    mCallback = callback;
  }
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    mLayout = (LinearLayout) inflater.inflate(
        R.layout.pick_category_fragment, null);
       
    // Display a list of categories and set the item click listener.
    ArrayAdapter<String> adapter = new ArrayAdapter<String>(
        getActivity(), R.layout.list_item, mCategories);
    mCategoryTextView = 
        (AutoCompleteTextView) mLayout.findViewById(R.id.category_picker);
    mCategoryTextView.setAdapter(adapter);
    mCategoryTextView.setThreshold(1);
    
    return mLayout;
  }
  
  
  @Override
  public void onResume() {
    super.onResume();

    mState = mCallback.getState();
    mCategoryTextView.setText(mState.getCategory());
  }
  
  @Override
  public void onPause() {
    mState.putCategory(mCategoryTextView.getText().toString());
    mCallback.putState(mState);
    
    super.onPause();
  }
  
  @Override
  public void onSaveInstanceState(Bundle state) {
    super.onSaveInstanceState(state);
    if (mSelectedView != null) {
      state.putString(CATEGORY, mSelectedView.getText().toString());
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        /* This ID represents the Home or Up button. In the case of this
         * activity, the Up button is shown. Use NavUtils to allow users
         * to navigate up one level in the application structure. For
         * more details, see the Navigation pattern on Android Design:
         *
         * http://developer.android.com/design/patterns/navigation.html#up-vs-back
         */
        NavUtils.navigateUpFromSameTask(getActivity());
        return true;
    }
    return super.onOptionsItemSelected(item);
  }
  
  public class GetAddressTask extends AsyncTask<LatLng, Void, String> {    
    @Override
    protected String doInBackground(LatLng... params) {
      LatLng loc = params[0];
      GeocodeService.Request request = GeocodeService.request(true)
          .latlng(loc.latitude, loc.longitude)
          .build();
      
      GeocodeResponse response;
      try {
        response = mGeocodeService.getAddress(request);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
      
      // if we don't get any results please don't do anything!
      try {
        String address = response.results.get(0).formatted_address;
        return address;
      } catch (IndexOutOfBoundsException noResults) {
        return null;
      }
    }
    
    @Override
    protected void onPostExecute(String address) {
      if (address != null) {
        mAdapter.add(address);
      }
    }
  }
}
