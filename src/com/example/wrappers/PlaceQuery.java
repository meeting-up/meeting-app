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

public class PlaceQuery {
  public int radius = -1;
  public boolean sensor;
  public String language;
  public int minPrice = -1;
  public int maxPrice = -1;
  public String name;
  public boolean openNow = false; // false value implies it can be ignored
  public String rankBy;
  public String[] types;
  public String keyword;

  public PlaceQuery (boolean sensor) {
    this.sensor = sensor;
  }
  
  public PlaceQuery setTypes (String[] types) {
    this.types=types;
    return this;
  }

  public PlaceQuery setRadius (int radius) {
    this.radius = radius;
    return this;
  }

  public PlacesService.Builder build () {
    PlacesService.Builder builder = new PlacesService.Builder (sensor);
    if (radius != -1) {
      builder.radius (radius);
    }
    try {
      if (types.length > 0) {
        builder.types(types);
      }
    } catch (NullPointerException e) {
      // Do nothing, types param just wasn't provided 
    }
  
    return builder;
  }
}
