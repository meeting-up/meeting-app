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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.model.LatLng;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.IntentSender;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.geocodeservice.GeocodeResponse;
import com.example.geocodeservice.GeocodeService;

import java.util.ArrayList;

@SuppressLint("ValidFragment")
public class OptionsFragment extends Fragment implements
    GooglePlayServicesClient.ConnectionCallbacks,
    GooglePlayServicesClient.OnConnectionFailedListener {
  private static final String TAG = OptionsFragment.class.getSimpleName();
  
  public static final String RADIUS = "com.example.meetingapp.RADIUS";
  public static final String KEYWORD = "com.example.meetingapp.KEYWORD";
  
  private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
  
  private static final GeocodeService mGeocodeService = new GeocodeService();

  private ModifyStateCallback mStateCallback;
  private State mState;
  private ArrayList<String> mAddresses;
  private ArrayList<LatLng> mUserLocations;
  private Callback mCallback;
  private String[] mPriceOptions;
  private ArrayAdapter<CharSequence> mCategoryAdapter;
  private LatLng mLastLocation;
  private LocationClient mLocationClient;
  private ArrayAdapter<String> mAdapter;

  private RelativeLayout mLayout;
  private Spinner mMinPriceSpinner;
  private Spinner mMaxPriceSpinner;
  private EditText mKeywordEditText;
  private Spinner mCategorySpinner;
  private CheckBox mOpenNowCheckBox;
  private AutoCompleteTextView mAddressAutocomplete;

  public interface Callback {
    public void nextPage();
    public void pickLocation();
    public void clearBackStack();
  }
  
  public OptionsFragment(Callback callback, ModifyStateCallback stateCallback) {
    mStateCallback = stateCallback;
    mCallback = callback;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mPriceOptions = getActivity().getResources().getStringArray(R.array.prices);
    
    mLocationClient = new LocationClient(getActivity(), this, this);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    mLayout = (RelativeLayout) inflater.inflate(R.layout.options_fragment, null);

    mKeywordEditText = (EditText) mLayout.findViewById(R.id.keyword);
    mMinPriceSpinner = (Spinner) mLayout.findViewById(R.id.min_price);
    mMaxPriceSpinner = (Spinner) mLayout.findViewById(R.id.max_price);
    mCategorySpinner = (Spinner) mLayout.findViewById(R.id.category_picker);
    mOpenNowCheckBox = (CheckBox) mLayout.findViewById(R.id.opennow_checkbox);
    setupSpinners();
    
    Button nextPageButton = (Button) mLayout.findViewById(R.id.next_page);
    nextPageButton.setOnClickListener(new OnClickListener() {
      @Override
      @SuppressWarnings("unused")
      public void onClick(View view) {
        mState.putResults(new ArrayList<LocationParcel>());
        mStateCallback.putState(mState);
        mCallback.nextPage();
      }
    });
    
    Button pickLocationButton = (Button) mLayout.findViewById(R.id.pick_location);
    pickLocationButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        mCallback.pickLocation();
      }
    });
    
    Button addButton = (Button) mLayout.findViewById(R.id.add_text_location);
    addButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        addTextLocation(view);
      }
    });

    
    mUserLocations = new ArrayList<LatLng>();
    mAddresses = new ArrayList<String>();
        
    mAddressAutocomplete = 
        (AutoCompleteTextView) mLayout.findViewById(R.id.enter_location);
    mAddressAutocomplete.setAdapter(
        new PlacesAutocompleteAdapter(getActivity(), R.layout.list_item));
    mAddressAutocomplete.setThreshold(3);
    
    return mLayout;
  }
  
  @Override
  public void onStart() {
    super.onStart();
    mLocationClient.connect();
  }

  @Override
  public void onResume() {
    super.onResume();

    getActivity().getActionBar().setTitle("Specify Meetup Options");
    
    mState = mStateCallback.getState();
    
    mUserLocations = mState.getSelectedLocations();
    mLastLocation = mState.getLocation();
    mAddresses = mState.getSelectedLocationsAsStrings();
    
    mCategorySpinner
        .setSelection(mCategoryAdapter.getPosition(mState.getCategory()));

    LatLng pickedLocation = mState.getMapSelection();
    Log.d(TAG, "pickedLocation is " + pickedLocation == null? "null" : "selected");
    if (pickedLocation != null) {
      mUserLocations.add(pickedLocation);
      GetAddressTask task = new GetAddressTask();
      task.execute(pickedLocation);
      mState.putMapSelection(null);
      mStateCallback.putState(mState);
    }
    
    setupFriends();
  }
  
  @Override
  public void onPause() {
    super.onPause();

    String keyword = mKeywordEditText.getText().toString();
    mState.putKeywords(keyword);

    String minPrice = mMinPriceSpinner.getSelectedItem().toString();
    mState.putMinPrice(spinnerOptionToPrice(minPrice));
    
    String category = mCategorySpinner.getSelectedItem().toString();
    mState.putCategory(category);

    boolean openNow = mOpenNowCheckBox.isChecked();
    mState.putOpenNow(openNow);
    
    mState.putSelectedLocations(mUserLocations);
    
    if (mLastLocation != null) {
      mState.putLocation(
          new LatLng(mLastLocation.latitude, mLastLocation.longitude));
    }
    mState.putSelectedLocationsAsStrings(mAddresses);    
    
    mStateCallback.putState(mState);
  }
  
  @Override
  public void onStop() {
    mLocationClient.disconnect();
    super.onStop();
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
  public void onConnected(Bundle dataBundle) {
    Log.d(TAG, "Connected.");
    
    Location loc = mLocationClient.getLastLocation();
    
    if (loc != null) {
      LatLng location = new LatLng(loc.getLatitude(), loc.getLongitude());
      
      mState.putLocation(location);
      mStateCallback.putState(mState);
      
      if (mAdapter.getCount() == 0) {
        GetAddressTask task = new GetAddressTask();
        task.execute(location);
        
        mUserLocations.add(location);
      }
    }
  }

  /* (non-Javadoc)
   * @see com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks#onDisconnected()
   */
  @Override
  public void onDisconnected() {
    Log.d(TAG, "Location client disconnected");
  }
  
  @Override
  public void onConnectionFailed(ConnectionResult connectionResult) {
    /*
     * Google Play services can resolve some errors it detects.
     * If the error has a resolution, try sending an Intent to
     * start a Google Play services activity that can resolve
     * error.
     */
    if (connectionResult.hasResolution()) {
      try {
        // Start an Activity that tries to resolve the error
        connectionResult.startResolutionForResult(
              getActivity(),
              CONNECTION_FAILURE_RESOLUTION_REQUEST);
        /*
         * Thrown if Google Play services canceled the original
         * PendingIntent
         */
      } catch (IntentSender.SendIntentException e) {
        // Log the error
        Log.w(TAG, "Intent cancelled", e);
      }
    } else {
      Log.e(TAG, "Error: " + connectionResult.getErrorCode());
    }
  }

  private int spinnerOptionToPrice(String price) {
    for (int i = 0; i < mPriceOptions.length; i++) {
      if (price.equals(mPriceOptions[i])) {
        return i;
      }
    }
    throw new RuntimeException("Illegal price option passed!");
  }

  private void setupSpinners() {
    mCategoryAdapter = ArrayAdapter.createFromResource(getActivity(),
        R.array.categories, android.R.layout.simple_spinner_item);
    mCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    mCategorySpinner.setAdapter(mCategoryAdapter);
    
    ArrayAdapter<CharSequence> minPriceAdapter = ArrayAdapter.createFromResource(getActivity(),
        R.array.prices, android.R.layout.simple_spinner_item);
    minPriceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    mMinPriceSpinner.setAdapter(minPriceAdapter);

    ArrayAdapter<CharSequence> maxPriceAdapter = ArrayAdapter.createFromResource(getActivity(),
        R.array.prices, android.R.layout.simple_spinner_item);
    maxPriceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    mMaxPriceSpinner.setAdapter(maxPriceAdapter);
    
    /* set the default option to be "Very Expensive" */
    mMaxPriceSpinner.setSelection(mPriceOptions.length - 1);
  }
  
  private void setupFriends() {
    ListView view = (ListView) mLayout.findViewById(R.id.locations_list);
    mAdapter = new ArrayAdapter<String>(getActivity(), R.layout.friend_item, mAddresses);
    view.setAdapter(mAdapter);
  }
  
  /**
   * @param view Required for onClick to work. 
   */
  public void addTextLocation(View view) {
    String entry = mAddressAutocomplete.getText().toString().trim();
    mAddressAutocomplete.setText("");
    if (!entry.equals("")) {
      GetLatLngTask task = new GetLatLngTask(entry);
      task.execute();
    }
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
  
  public class GetLatLngTask extends AsyncTask<Void, Void, LatLng> {
    private String address;
    
    public GetLatLngTask(String address) {
      this.address = address;
    }
    
    @Override
    protected LatLng doInBackground(Void... params) {
      GeocodeService.Request request = GeocodeService.request(true)
          .address(address.replace(" ", "+"))
          .build();
      GeocodeResponse response;
      try {
        response = mGeocodeService.getAddress(request);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
      
      try {
        double latitude = response.results.get(0).geometry.location.lat;
        double longitude = response.results.get(0).geometry.location.lng;
        
        return new LatLng(latitude, longitude);
      } catch (NullPointerException noResults) {
        return null;
      }
    }
    
    @Override
    protected void onPostExecute(LatLng location) {
      mUserLocations.add(location);
      mAdapter.add(address);
      Toast.makeText(getActivity(), "Location added", Toast.LENGTH_SHORT).show();
    }
  }
}
