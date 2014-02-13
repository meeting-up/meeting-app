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

package com.example.wrappers;

import java.util.List;

/**
 * PlaceResult class used to hold the data about a particular place.
 */
public class PlaceResult {
  public Geometry geometry;
  public String name;
  public String reference;
  public String id;
  public String icon;
  public List<Photo> photos;
  public String[] types;
  public OpeningHours opening_hours;
  public double rating;
  public String vicinity;
  public int price_level;
  public List<Event> events;

  /**
   * This class holds the data in the geometry subsection of a place.
   */
  public class Geometry {
    public ViewPort viewport;
    public Location location;
    /**
     * Holds viewPort info pertaining to a place.
     */
    public class ViewPort {
      public Location northeast;
      public Location southwest;
    }
  }

  /**
   * This class is used for storing location data about a place.
   */
  public class Location {
    public double lat;
    public double lng;
  }

  /**
   * This class holds the data pertaining to each photo for a particular place.
   */
  public class Photo {
    public String photo_reference;
    public String[] html_attributions;
    public int height;
    public int width;
  }

  /**
   * Holds data about events for a place.
   */
  public class Event {
    public String event_id;
    public String summary;
    public String url;
  }

  /**
   * Holds opening hours data
   */
  public class OpeningHours {
    public boolean open_now;
  }

  /**
   * Returns a briefe String containing some information about a place.
   * @Returns a string with info about the place.
   */
  @Override
    public String toString() {
    return "PlaceResult{" +
      "name='" + name + '\'' +
      ", id='" + id + '\'' +
      '}';
  }
}
