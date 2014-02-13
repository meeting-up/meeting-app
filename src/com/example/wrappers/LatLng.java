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

/**
 * LatLng class, here for testing with gradle to avoid android dependencies. 
 * Should be replaced with the android.com.gms.LatLng class eventually.
 */
public class LatLng {
  public final double latitude;
  public final double longitude;

  public LatLng (double lat, double lng) {
    this.latitude = lat;
    this.longitude = lng;
  }
}
