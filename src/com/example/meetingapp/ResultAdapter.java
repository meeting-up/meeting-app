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

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.accessibility.AccessibilityManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Adapter for the ListView of Places Search results.
 */
public class ResultAdapter extends ArrayAdapter<LocationParcel> {
  
  private final int mResourceId;
  private final List<LocationParcel> mLocations;
  private final Context mContext;
  
  /**
   * @param context
   * @param resource The layout that will be used when returning a view.
   * @param objects The data that the adapter binds to.
   */
  public ResultAdapter(Context context, int resource, List<LocationParcel> objects) {
    super(context, resource, objects);
    this.mContext = context;
    this.mResourceId = resource;
    this.mLocations = objects;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    View item = convertView;
    ResultHolder holder;
    
    /* If the view exists, then we don't need to initialise the View information
     * behind it.
     */
    if (item == null) {
      /* Create the view and initialise the tag holding the information
       * about its child views.
       */
      LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
      item = inflater.inflate(mResourceId, parent, false);
      
      holder = new ResultHolder();
      holder.title = (TextView) item.findViewById(R.id.place_name);
      holder.opennow = (TextView) item.findViewById(R.id.place_open);
      holder.image = (ImageView) item.findViewById(R.id.place_photo);
      holder.ratings = (TextView) item.findViewById(R.id.price_rating);
      holder.vicinity = (TextView) item.findViewById(R.id.vicinity);
      
      item.setTag(holder);
    } else {
      holder = (ResultHolder) item.getTag();
    }
    
    LocationParcel location = mLocations.get(position);
    
    holder.title.setText(location.getName());
    
    if (location.getOpenNow()) {
      holder.opennow.setText("open now");
    }
    
    String ratingMessage = "";
    
    AccessibilityManager accessibilityManager =
      (AccessibilityManager) mContext.getSystemService(Context.ACCESSIBILITY_SERVICE);
    boolean isAccessibilityEnabled = accessibilityManager.isEnabled();
    /* Display the rating as stars, rounded down to the nearest integer. 
     * We add on the price rating afterwards, separated by a pipe.
     */
    int rating = (int) location.getRating();
    if (rating == 0) {
      ratingMessage = "no rating available";
    } else {
      if (! isAccessibilityEnabled) {
        while (rating > 0) {
          ratingMessage += "\u2605";
          rating--;
        }
      } else {
        ratingMessage += "Rated: " + rating + " out of 5";
      }
    }
    ratingMessage += " | ";
    
    int price = location.getPrice();
    if (! isAccessibilityEnabled) {
      while (price > 0) {
        ratingMessage += "$";
        price--;
      }
    } else { 
      if (price > 0 && price <= 5) {
        ratingMessage += mContext.getResources().getStringArray(R.array.prices)[price];
      }
    }

    holder.ratings.setText(ratingMessage);
    
    String vicinity = location.getVicinity();
    if (vicinity != null) {
      holder.vicinity.setText(vicinity);
    }
    
    /* If a photo is available, set it; otherwise fetch a default picture. */
    Bitmap photo = location.getPhoto();
    if (photo == null) {
      photo = BitmapFactory.decodeResource(
          mContext.getResources(), R.drawable.ic_action_picture);
    }
    holder.image.setImageBitmap(photo);
    
    return item;
  }

  static class ResultHolder {
    TextView title;
    TextView opennow;
    TextView ratings;
    TextView vicinity;
    ImageView image;
  }
}
