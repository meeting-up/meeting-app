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

package com.example.geocodeservice;

import java.util.List;

/**
 * This class represents a result that the Geocoding API will give back within the response. GSON
 * will deserialise the response into a GeocodeResult.
 */
public class GeocodeResult {
  public List<AddressComponent> address_components;
  public String formatted_address;
  public Geometry geometry;
  public List<String> types;
  
  /* Unavoidable warning about hidden variables as this is how the class must
   * be made to get it to work with GSON.
   */
  @SuppressWarnings("hiding")
  public class AddressComponent {
    public String long_name;
    public String short_name;
    public List<String> types;
  }

  public class Geometry {
    public Location location;
    public String location_type;
    public Viewport viewport;
  }

  public class Location {
    public double lat;
    public double lng;
  }

  public class Viewport {
    public Location northeast;
    public Location southwest;
  }
}
