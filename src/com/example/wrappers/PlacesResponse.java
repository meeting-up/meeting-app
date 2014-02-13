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
 * PlacesResponse class used to hold the results from the json returned by a places query.
 */
public class PlacesResponse {
  // Following fields get given values taken from correspondingly named json fields.
  public String status;
  public String[] html_attributions;
  public String next_page_token = "";
  public List<PlaceResult> results;
  // The following is set manually useful for subsequent pages of results.
  public String sensor; 
}
