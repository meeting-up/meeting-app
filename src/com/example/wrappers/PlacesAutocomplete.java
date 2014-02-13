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

import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * PlacesAutocomplete class - used to send/receive requests from the places autocomplete web api.
 */
public class PlacesAutocomplete extends PlaceWrapper {
  private static final String BASE = "https://maps.googleapis.com/maps/api/place/autocomplete/";
  private final String mKey;

  /**
   * Constructor method, controls setting up the api key.
   * @param key your google places api key. 
   */
  public PlacesAutocomplete(String key) {
    mKey = key;
  }

  /**
   * Return a Builder instance which can be used to set all desired params for the Request.
   * @Param sensor: Whether or not a gps sensor is in use.
   * @return A builder instance.
   */
  public static Builder request(boolean sensor) {
    return new Builder(sensor);
  }

  /**
   * Request class.
   * Holds the params and corresponding values to be used in the web request.
   */
  public static class Request {
    private String input = "";
    private String sensor = "";
    private String offset = "";
    private String location = "";
    private String radius = "";
    private String language = "";
    private String types = "";
    private String components = "";

    private Request() {}

    /**
     * Returns the String of the url which should be used in the request, relative to param values.
     * @param key the api key. 
     * @return String, the url to be queried.
     */
    public String url(String key) {
      return BASE + "json?" + input + sensor + offset + location + radius + types + components
        + language + offset + "&key=" + key;
    }
  }

  /**
   * Builder class, used to build up a request by setting params.
   */
  public static class Builder {
    private final Request r = new Request();

    /** 
     * Constructor method, sets up manditory sensor argument. 
     * @param sensor whether or not a gps sensor has been used to obtain location.
     */
    public Builder(boolean sensor) {
      r.sensor = "&sensor=" + sensor;
    }

    /**
     * Sets the input param in the request.
     * @param value the value to be supplied to the input param.
     * @returns this builder instance.
     */
    public Builder input(String value) {
      r.input = "input=" + value;
      return this;
    }

    /**
     * Sets the offset param in the request.
     * @param value the value to be supplied to the offset param.
     * @returns this builder instance.
     */
    public Builder offset(int value) {
      r.offset = "&offset=" + value;
      return this;
    }

    /**
     * Sets the location param in the request.
     * @param lat the latitude of the location to be searched.
     * @param lng the longitude of the location to be used.
     * @returns this builder instance.
     */
    public Builder location(double lat, double lng) {
      r.location = "&location=" + lat + "," + lng;
      return this;
    }

    /**
     * Sets the language param in the request.
     * @param value the value to be supplied to the language param.
     * @returns this builder instance.
     */
    public Builder Language(String value) {
      r.language = "&language=" + value;
      return this;
    }

    /**
     * Sets the radius param in the request.
     * @param value the value to be supplied to the radius param.
     * @returns this builder instance.
     */
    public Builder radius(int value) {
      r.radius = "&radius=" + value;
      return this;
    }

    /**
     * Sets the components param in the request.
     * @param value the value to be supplied to the components param.
     * @returns this builder instance.
     */
    public Builder components(String value) {
      r.components = "&components=" + value;
      return this;
    }

    /**
     * Sets the types param in the request.
     * @param value the value to be supplied to the types param.
     * @returns this builder instance.
     */
    public Builder types(String value) {
      r.types = "&types=" + value;
      return this;
    }

    /**
     * Returns the request reflecting all of the params which were set by the builder.
     * @returns the current request.
     */
    public Request build() {
      return r;
    }
  }

  /**
   * Lookup the current request and return a response. 
   * @param r the request to lookup. 
   * @return a PlacesAutocompleteResponse object.
   */
  public PlacesAutocompleteResponse lookup(Request r) {
    InputStream content;
    content = googleServiceSearch(r.url(mKey));
    return new Gson().fromJson(new InputStreamReader(content), PlacesAutocompleteResponse.class);
  }
}
