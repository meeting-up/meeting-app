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
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.placedetails.DetailsResponse;
import com.example.placedetails.DetailsResult;
import com.example.placedetails.DetailsService;
import com.example.placephotos.PhotoService;
import com.example.wrappers.StaticMap;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

@SuppressLint("ValidFragment")
public class ShowDetailsFragment extends Fragment {
  private static final String TAG = ShowDetailsFragment.class.getSimpleName();
  
  private ModifyStateCallback mCallback;
  private DetailsService mDetailsService; 
  private State mState;
  private String mAddress;
  private PhotoService mPhotoService;
  private LocationParcel mSelection;
  private DetailsResult mResult;
  private boolean isDetached;

  private TextView mPhoneView;
  private TextView mAddressView;
  private LinearLayout mPhotosView;
  private RatingBar mRatingBar;
  private ProgressBar mProgressBar;
  private TextView mProgressText;
  private LinearLayout mProgressLayout;
  private RelativeLayout mLayout;
  
  public ShowDetailsFragment(ModifyStateCallback callback) {
    mCallback = callback;
  }
  
  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    isDetached = false;
  }
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    mState = mCallback.getState();
    
    mDetailsService = new DetailsService("AIzaSyCtDiLiH1Qlb1PWogd8bgn8Ww8NK2_CQLk");
    mPhotoService = new PhotoService("AIzaSyCtDiLiH1Qlb1PWogd8bgn8Ww8NK2_CQLk");
  }
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    mLayout = (RelativeLayout) inflater.inflate(
        R.layout.show_details_fragment, null);
    
    mAddressView = (TextView) mLayout.findViewById(R.id.address);
    mPhotosView = (LinearLayout) mLayout.findViewById(R.id.photo_gallery);
    mPhoneView = (TextView) mLayout.findViewById(R.id.phone_number);
    mRatingBar = (RatingBar) mLayout.findViewById(R.id.rating_bar);
    mProgressBar = (ProgressBar) mLayout.findViewById(R.id.progress_bar);
    mProgressLayout = (LinearLayout) mLayout.findViewById(R.id.details_loader);
    mProgressText = (TextView) mLayout.findViewById(R.id.progress_detail);

    mSelection = mState.getResult();
    GetDetailsTask task = new GetDetailsTask(mSelection.getReference());
    task.execute();
    
    ImageButton callButton = (ImageButton) mLayout.findViewById(R.id.call_number);
    callButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        callNumber();
      }
    });
    
    ImageButton navigateButton = (ImageButton) mLayout.findViewById(R.id.start_navigation);
    navigateButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        startNavigation();
      }
    });
    
    Button shareButton = (Button) mLayout.findViewById(R.id.share);
    shareButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        sharePlace();
      }
    });
    
    return mLayout;
  }

  @Override
  public void onResume() {
    super.onResume();

    mSelection = mState.getResult();
    
    getActivity().getActionBar().setTitle(mSelection.getName());
  }
  
  @Override
  public void onDetach() {
    isDetached = true;
    super.onDetach();
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
   * Allows the user to call a provided number.
   */
  private void callNumber() {
    /* If there's no phone number, don't try to call. */
    if (mResult.international_phone_number == null) {
      return;
    }
    Intent intent = new Intent(Intent.ACTION_DIAL);
    intent.setData(Uri.parse("tel:" + mResult.formatted_phone_number));
    if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
      startActivity(intent);
    }
  }

  /**
   * Shares the details of a place.
   */
  private void sharePlace() {
    String text = "We are meeting at: ";
    if (mResult.name != null) {
      text=text+mResult.name;
    } else {
      return;
    }

    if (mResult.website != null) {
      text = text + " ("+mResult.website+")";
    }
    if (mResult.formatted_address != null) {
      text = text + "\nLocated at: "+mResult.formatted_address;
      text+= "(https://maps.google.com/maps?q=" + mResult.formatted_address + ")";
    }

    Intent sendIntent = new Intent();
    sendIntent.setAction(Intent.ACTION_SEND);
    sendIntent.putExtra(Intent.EXTRA_TEXT, text);
    sendIntent.setType("text/plain");
    startActivity(sendIntent);
  }

  /*
   * Calls directions to the location of the place.
   */
  private void startNavigation() {
    LatLng location = mState.getLocation();
    String origin = location.latitude + "," + location.longitude;
    String destination = mResult.geometry.location.lat + "," + 
        mResult.geometry.location.lng;
    Intent intent = new Intent(android.content.Intent.ACTION_VIEW, 
                               Uri.parse("http://maps.google.com/maps?saddr=" + 
                                         origin + "&daddr=" + destination));
    startActivity(intent);
  }    
  
  /**
   * Displays the details of a place on the page. Initialises a 
   * @param response Details of a place returned from the Place Details API.
   */
  @SuppressWarnings("unchecked")
  private void showDetails(DetailsResponse response) {
    mResult = response.result;
    
    /* Display rating on the RatingBar. */
    mRatingBar.setRating((float) mResult.rating);
    
    /* If there is an available phone number, display it. */
    if (mResult.formatted_phone_number != null) {
      mPhoneView.setText(mResult.formatted_phone_number);
    }
    
    if (mResult.formatted_address != null) {
      mAddress = mResult.formatted_address;
      mAddressView.setText(mAddress);
    }
    
    GetMapTask mapTask = new GetMapTask();
    mapTask.execute(new LatLng(mResult.geometry.location.lat, 
        mResult.geometry.location.lng));
    
    /* Don't display the photo loader if there are no photos to display. */
    if (mResult.photos == null) {
      mProgressLayout.setVisibility(View.GONE);
      return;
    }
    
    /* Load the photos and start the progress bar. */ 
    ArrayList<String> photoreferences = new ArrayList<String>();
    for (DetailsResult.Photo photo : mResult.photos) {
      photoreferences.add(photo.photo_reference);
    }
    mProgressBar.setIndeterminate(false);
    mProgressBar.setMax(photoreferences.size());
    mProgressBar.setProgress(1);
    
    
    GetPhotosTask task = new GetPhotosTask();
    task.execute(photoreferences);
  }
  
  /**
   * Adds an image to the HorizontalScrollView.
   * @param image Image to be added to the HorizontalScrollView.
   */
  private void addImage(Bitmap image) {
    LinearLayout layout = new LinearLayout(getActivity());
    layout.setGravity(Gravity.CENTER);
    
    /* Set parameters - we want a small divider of 3 pixels so we can
     * easily distinguish different photos.
     */
    LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
        LayoutParams.WRAP_CONTENT);
    params.setMargins(3, 0, 0, 0);
    params.gravity = Gravity.CENTER;
    
    ImageView imageView = new ImageView(getActivity());
    imageView.setScaleType(ScaleType.CENTER);
    imageView.setImageBitmap(image);
    imageView.setLayoutParams(params);
    
    layout.addView(imageView);
    mPhotosView.addView(layout);
  }
  
  public class GetDetailsTask extends AsyncTask<String, Void, DetailsResponse> {
    private String reference;
    
    public GetDetailsTask(String reference) {
      this.reference = reference;
    }
    
    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      mProgressLayout.setVisibility(View.VISIBLE);
      mProgressText.setText("Loading details...");
      mProgressBar.setIndeterminate(true);
    }
    
    @Override
    protected DetailsResponse doInBackground(String... references) {
      DetailsService.Request request = DetailsService.request()
          .sensor(true)
          .reference(this.reference)
          .build();
      return mDetailsService.detailSearch(request);
    }
    
    @Override
    protected void onPostExecute(DetailsResponse response) {
      /* After we finish, we'll start loading the images. If there are no images
       * then the progress layout will be hidden later.
       */
      if (response.status.equals("OK")) {
        mProgressText.setText("Loading images...");
        mProgressBar.setIndeterminate(false);
        showDetails(response);
      }
    }
  }
  
  public class GetPhotosTask extends AsyncTask<ArrayList<String>, Bitmap, Void> {
    @Override
    protected Void doInBackground(ArrayList<String>... references) {
      for (String photoRef : references[0]) {
        /* If the fragment is detached, then there's no point in attempting
         * to load images (unless we pre-load; but that's a job for later.)
         */
        if (isDetached) {
          return null;
        }
        PhotoService.Request request = PhotoService.request()
            .sensor(true)
            .photoreference(photoRef)
            .maxheight(400)
            .build();
        publishProgress(mPhotoService.getPhoto(request)); 
      }
      return null;
    }
    
    @Override
    protected void onProgressUpdate(Bitmap... images) {
      mProgressBar.setProgress(mProgressBar.getProgress() + 1);
      addImage(images[0]);
    }
    
    @Override
    protected void onPostExecute(Void nothing) {
      /* After we're done, we have no need for the progress information
       * any more.
       */
      mProgressLayout.setVisibility(View.GONE);
    }
  }
  
  public class GetMapTask extends AsyncTask<LatLng, Void, Bitmap> {
    @Override
    protected Bitmap doInBackground(LatLng... locations) {
      LatLng loc = locations[0];
      String[] markerStyle = {"color", "blue"};
      String[] markerLocation = { loc.latitude + "," + loc.longitude };
      StaticMap.Request request = StaticMap.request(true)
          .markers(markerStyle, markerLocation)
          .mapType(StaticMap.Builder.MapTypeValues.ROADMAP)
          .size(2000, 2000)
          .build();
      Log.d(TAG, "url is " + request.url(""));
      return BitmapFactory.decodeStream(new StaticMap().fetch(request));
    }
    
    @Override
    protected void onPostExecute(Bitmap image) {
      Log.d(TAG, "attempting to set image");
      ((ImageView) mLayout.findViewById(R.id.place_map))
          .setImageBitmap(image);
    }
  }
}
