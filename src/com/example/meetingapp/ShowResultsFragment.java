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
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.example.geocodeservice.GeocodeResponse;
import com.example.geocodeservice.GeocodeService;
import com.example.placephotos.PhotoService;
import com.example.wrappers.DistanceMatrix;
import com.example.wrappers.DistanceMatrixResponse;
import com.example.wrappers.PlaceCriteria;
import com.example.wrappers.PlaceResult;
import com.example.wrappers.PlacesResponse;
import com.example.wrappers.PlacesService;
import com.example.wrappers.RankBy;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@SuppressLint("ValidFragment")
public class ShowResultsFragment extends Fragment {
  public static final String TAG = ShowResultsFragment.class.getSimpleName();
  
  private static final GeocodeService mGeocodeService = new GeocodeService();
  private static final PlacesService mPlacesService =
      new PlacesService("AIzaSyCtDiLiH1Qlb1PWogd8bgn8Ww8NK2_CQLk");
  private static final PhotoService mPhotoService = 
      new PhotoService("AIzaSyCtDiLiH1Qlb1PWogd8bgn8Ww8NK2_CQLk");

  public static final String PLACE_RESULTS =
      "com.example.meetingapp.PLACE_RESULTS";
  public static final String PLACE_DETAILS = "com.example.meetingapp.details";
  public static final String SHOW_MAP_TEXT = "Show Map";
  
  private Callback mCallback;
  private ModifyStateCallback mStateCallback;
  private State mState;
  private ArrayList<LocationParcel> mNearbyPlaces;
  private ArrayList<String> mAddresses;
  private ArrayAdapter<LocationParcel> mAdapter;
  
  private ListView mResultsList;
  private RelativeLayout mLayout;
  private ProgressDialog mDialog;
  private Button mButton;
  
  public interface Callback {    
    /**
     * Called once the button at the bottom of the screen is clicked.
     * 
     * @param tag Used to tag the fragment in the back stack, in case it needs
     * to be referenced later. Can be null.
     */
    public void showResultsOnMap(String tag);
    
    /**
     * Called once an item card has been clicked.
     * 
     * @param selection The details of the selected place.
     */
    public void showResult(LocationParcel selection);
  }
  
  public ShowResultsFragment (Callback callback,
      ModifyStateCallback stateCallback) {
    mCallback = callback;
    mStateCallback = stateCallback;
  }
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    // Show the Up button in the action bar.
    setupActionBar();
  }
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    mLayout = (RelativeLayout) inflater.inflate(
        R.layout.show_results_fragment, null);
    
    mResultsList = (ListView) mLayout.findViewById(R.id.results_list);
    
    mButton = (Button) mLayout.findViewById(R.id.show_map);
    mButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        mCallback.showResultsOnMap(TAG);
      }
    });
    
    return mLayout;
  }
  
  @Override
  public void onResume() {
    super.onResume();
    setupActionBar();
    
    /* Change the "Next Page" text as it will no longer go to the next page. */
    mButton.setText(SHOW_MAP_TEXT);
    
    mState = mStateCallback.getState();
    
    mNearbyPlaces = mState.getResults();
    mAdapter = new ResultAdapter(
        getActivity(), R.layout.result_list_item, mNearbyPlaces);
    
    /* If we already have results then don't show them! If we don't, query
     * again just in case.
     */
    if (mNearbyPlaces.size() == 0) {
      setResults(mState.getSelectedLocations());      
    } else {
      displayResults();
    }
    
    mAddresses = mState.getSelectedLocationsAsStrings();
  }
  
  @Override
  public void onPause() {
    super.onPause();
    
    mState.putResults(mNearbyPlaces);
    mStateCallback.putState(mState);
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
    getActivity().getActionBar().setTitle("Results");
  }
  
  /* Triggers generation of results for the query.
   */
  private void setResults(ArrayList<LatLng> locations) {
    LatLng centrepoint = getCentre(locations);
    String category;
    
    /* Turn the categories into an API-friendly format (lower case, and with
     * underscores instead of spaces.)
     */
    try {
      category = mState.getCategory()
          .toLowerCase(Locale.getDefault()).replace(" ", "_");
    } catch (NullPointerException noCategory) {
      category = "";
    }
  
    int minPrice = mState.getMinPrice();
    int maxPrice = mState.getMaxPrice();

    String keyword = mState.getKeywords();
    if (keyword == null) {
      keyword = "";
    }
    
    /* Reset the results so we aren't adding to an old list. */ 
    mNearbyPlaces = new ArrayList<LocationParcel>();
    LookupTask task = new LookupTask(
                                     keyword, category, minPrice, maxPrice, locations);

    task.execute(centrepoint);
  }
  
  private LatLng getCentre(ArrayList<LatLng> locations) {
    double latitudeSum = 0;
    double longitudeSum = 0;
    int numLocations = locations.size();
    
    for (LatLng location : locations) {
      latitudeSum += location.latitude;
      longitudeSum += location.longitude;
    }
    LatLng centre = new LatLng(
        latitudeSum / numLocations, longitudeSum / numLocations);
    
    if (numLocations != 0) {
      mState.putCentre(centre);
      mStateCallback.putState(mState);
    }
    
    return centre;
  }
  
  private void displayResults() {
    mAdapter = new ResultAdapter(
        getActivity(), R.layout.result_list_item, mNearbyPlaces);
    
    mResultsList.setOnItemClickListener(new OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, 
          int position, long id) {
        LocationParcel selection = 
            (LocationParcel) parent.getItemAtPosition(position);
        
        mState.putResult(selection);
        mStateCallback.putState(mState);
        
        mCallback.showResult(selection);
      }
    });
    mResultsList.setAdapter(mAdapter);
    
    SetPhotoTask task = new SetPhotoTask();
    task.execute();
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
      
      if (response.status.equals("ZERO_RESULTS")) {
        return null;
      } else if (response.status.equals("OVER_QUERY_LIMIT")) {
        Log.e(TAG, "This app is over the Geocoding API query limit.");
        return null;
      }
      return response.results.get(0).formatted_address;
    }
    
    @Override
    protected void onPostExecute(String address) {
      if (address != null) {
        mAddresses.add(address);
      }
    }
  }
  
  public class LookupTask extends AsyncTask<LatLng, Void, PlacesResponse> {
    private String keyword;
    private String type;
    private int minPrice;
    private int maxPrice;
    private ArrayList<LatLng> locations;

    public LookupTask(String keyword, String type, 
                      int minPrice, int maxPrice, ArrayList<LatLng> locations) {
      this.keyword = keyword.trim();
      this.type = type;
      this.locations = locations;
      this.minPrice = minPrice;
      this.maxPrice = maxPrice;
    }

    /**
     * Set the value to be used for the radius in the places search.
     * Radius size is determined by the distance of points from the centroid. 
     * @param locations the list of LatLng objects which represent all the locations to be considered.
     * @return the radius to be used. 
     */
    private int setRadius (ArrayList<LatLng> locations) {
      LatLng centre = getCentre (locations);
      int radius;
      int distance = 0;
      double[] destinations = new double[2 * locations.size ()];
      int i = 0;
      for (LatLng location: locations) {
        destinations[i] = location.latitude;
        i++;
        destinations[i] = location.longitude;
        i++;
      }

      double[] origins = {centre.latitude, centre.longitude};
      DistanceMatrix d = new DistanceMatrix ();
      DistanceMatrix.Request r = d.request (false).origins (origins).destinations (destinations).build ();
      DistanceMatrixResponse response = d.search(r);

      for (DistanceMatrixResponse.Rows.Elements element: response.rows.get(0).elements) {
        distance += element.distance.value;
      }

      distance = distance / locations.size ();
      radius = distance / 5;
      if (radius < 1000) {
        radius = 1000;
      } else if (radius > 50000) {
        radius = 50000;
      }

      return radius;
    }    

    @Override
    protected void onPreExecute() {
      if (mDialog == null) {
        mDialog = new ProgressDialog(getActivity());
        mDialog.setTitle("Loading results...");
        mDialog.setMessage("Please wait.");
        mDialog.setCancelable(false);
        mDialog.setIndeterminate(true);
        mDialog.show();
      }
    }
    
    @Override
    protected PlacesResponse doInBackground(LatLng... coords) {
      PlacesService.Request request;
      int radius = setRadius(this.locations);
      request = PlacesService.request(true)
        .radius(radius)
        .types(this.type)
        .keyword(this.keyword)
        .minPrice(this.minPrice)
        .maxPrice(this.maxPrice)
        .location(coords[0].latitude, coords[0].longitude).build();
      Log.d(TAG, "Radius set to: "+radius);
      PlacesResponse response;
      response = mPlacesService.nearbySearch(request);
      response.results = getMostSuitable(response.results);
      return response;
    }

    private List<PlaceResult> getMostSuitable (List<PlaceResult> results) {
      // Todo ask the user if they are going now to use open now criteria 
      PlaceCriteria criteria = new PlaceCriteria ();
      // Some constants (possibly subjective)
      int matches = 7;
      int rating = 4;
      int photo = 4;
      int price;
      // The following line will compile after Vanessa's code is merged 
      int openNow = mState.getOpenNow()?5:0;
      switch (this.maxPrice) {
      case 0: 
      case 1: 
      case 2:
      case 3:
        price = 5-this.maxPrice;
        break;
      case 4:
        price = 0;
        break;
      default: // invalid price 
        price = 3; // Sensible default should never happen.
        break;
      }

      criteria.setPhoto (photo).setRating (rating).setPrice (price).setOpenNow (openNow);
      if (this.type.length () > 0) {
        criteria.setTypes (4, this.type);
      }

      RankBy rank = new RankBy (criteria, results);
      return rank.rankPlaces(matches);
    }
    
    @Override
    protected void onPostExecute(PlacesResponse response) {
      if (response.results == null) {
        Log.e(TAG, "results are null!"); 
      } else {
        Log.d(TAG, "there are results! " + response.results.size() +
            " " + response.status);
      }

      List<PlaceResult> results = response.results;
      String reference = null;
      LatLng place;
      
      for (PlaceResult r: results) {
        place = new LatLng(r.geometry.location.lat, r.geometry.location.lng);
        if (r.photos == null) {
          reference = null;
        } else {
          reference = r.photos.get(0).photo_reference;
        }
        
        boolean openNow = false;
        if (r.opening_hours != null) {
          openNow = r.opening_hours.open_now;
        }
        mNearbyPlaces.add(new LocationParcel(place.latitude, place.longitude, 
            r.name, r.reference, reference, r.icon, r.rating, r.price_level,
            openNow, r.vicinity));
        Log.d(TAG, "Found " + r.name);
      }

      mDialog.dismiss();
      mDialog = null;
      displayResults();
    }
  }
  
  public class SetPhotoTask extends AsyncTask<Void, Void, Void> {
    private boolean accessibilityEnabled;
    
    public SetPhotoTask() {
      /* Set accessibilityEnabled, based on whether the accessibility service is
       * turned on or off.
       */
    }
    
    @Override
    protected Void doInBackground(Void... params) {
      for (LocationParcel parcel : mNearbyPlaces) {
        if (parcel.getPhotoreference() == null) {
          continue;
        }
        PhotoService.Request request = PhotoService.request()
            .sensor(true)
            .photoreference(parcel.getPhotoreference())
            .maxheight(400)
            .build();
        parcel.setPhoto(mPhotoService.getPhoto(request));
        
        /* Don't refresh the views if accessibility is enabled; this will
         * result in the text of the child views being re-spoken over and over.
         * Only refresh the views with photos at the very end.
         */
        if (!accessibilityEnabled) {
          publishProgress();
        }
      }
      return null;
    }
    
    @Override
    protected void onProgressUpdate(Void... argument) {
      mAdapter.notifyDataSetChanged();
    }
    
    @Override
    protected void onPostExecute(Void argument) {
      mAdapter.notifyDataSetChanged();
    }
  }
}
