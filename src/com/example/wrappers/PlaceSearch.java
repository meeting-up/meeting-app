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
import java.util.ArrayList;

public class PlaceSearch {
  private PlacesService service;
  private List<PlacesService.Request> requests = new ArrayList<PlacesService.Request> ();

  public PlaceSearch (String key, PlaceQuery query, List<LatLng> locations) {
    PlacesService.Builder builder;
    service = new PlacesService (key);
    for (LatLng location: locations) {
      builder = query.build ();
      requests.add(builder.location (location.latitude, location.longitude).build ());
    }
  }

  public List<PlaceResult> search () {
    PlacesResponse response;
    List<PlaceResult> results = new ArrayList<PlaceResult>();
    for (PlacesService.Request r: requests) {
      response = service.nearbySearch (r);
      results.addAll(response.results);
    }
    results = getUnion (results);
    return results;
  }

  /**
   * Given a list of places return the list of places without duplicates.
   * Quite inefficient. 
   * But we can't use native methods as the objects are different, yet they have the same data possibly. 
   */
  private List<PlaceResult> getUnion (List<PlaceResult> list) { 
    boolean found;
    List<PlaceResult> noDuplicates = new ArrayList<PlaceResult>();
    for (PlaceResult insert: list) {
      found=false;
      for (PlaceResult pr1: noDuplicates) {
        if (insert.id.equals (pr1.id)) {
          found=true;
          break; // It exists no need to continue/insert
        }
      }
      if (! found) {
        noDuplicates.add(insert);
      }
    }
    return noDuplicates;
  }
}
