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

package com.example.placedetails;

import java.util.List;

/**
 * This class represents a result returned from a Place Details call. A Place Details response
 * consists of numerous fields and a list of these results, each of which represents a particular
 * Place.
 */
public class DetailsResult {
  public List<AddressType> address_components;
  public String formatted_address;
  public String formatted_phone_number;
  public Geometry geometry;
  public String icon;
  public String id;
  public String international_phone_number;
  public String name;
  public List<Photo> photos;
  public double rating;
  public String reference;
  public List<Review> reviews;
  public List<String> types;
  public String url;
  public int utc_offset;
  public String vicinity;
  public String website;

  @SuppressWarnings("hiding")
  public class AddressType {
    public String long_name;
    public String short_name;
    public List<String> types;
  }
  
  @SuppressWarnings("hiding")
  public class Aspect {
    public int rating;
    public String type;
  }

  public class Geometry {
    public Location location;
  }

  public class Location {
    public float lat;
    public float lng;
  }

  public class Photo {
    public int height;
    public int width;
    public List<String> html_attributions;
    public String photo_reference;
  }
  
  @SuppressWarnings("hiding")
  public class Review {
    public List<Aspect> aspects;
    public String author_name;
    public String author_url;
    public int rating;
    public String text;
    public long time;
  }
}
