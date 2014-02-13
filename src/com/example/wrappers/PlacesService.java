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
 * PlacesServices class used for building, sending and converting the response into a useful form.
 */
public class PlacesService extends PlaceWrapper {
  private static final String TAG = PlacesService.class.getSimpleName();
  private static final String BASE = "https://maps.googleapis.com/maps/api/place/";
  private final String mKey;

  /**
   * Constructor method, handles setting up the api key correctly.
   * @param key your google places api key.
   */
  public PlacesService(String key) {
    mKey = key;
  }

  /**
   * request method used for setting up a builder so that params can be set in the request. 
   * @param sensor the manditory sensor param value for the request 
   * i.e. is the device obtaining location from a gps sensor?
   * @return A builder instance. 
   */
  public static Builder request(boolean sensor) {
    return new Builder(sensor);
  }

  /** 
   * Request method, this version is generally used on the 2nd page of results or subsequent results.
   * @param response the response from the last query 
   * which can be used to retrieve additional results for that query.
   * @return builder instance. 
   */
  public static Builder request(PlacesResponse response) {
    return new Builder(response);
  }

  /**
   * The request class, holds all params/values for the given request.
   */
  public static class Request {
    private String location = "";
    private String radius = "";
    private String sensor = "";
    private String language = "";
    private String minPrice = "";
    private String maxPrice = "";
    private String name = "";
    private String openNow = "";
    private String rankBy = "";
    private String pageToken = "";
    private String zagatSelected = "";
    private String types = "";
    private String keyword = "";

    private Request() {
    }

    /**
     * Constructs a URL to be used to retrieve the response based on the Request.
     * @param method the method to be used usually nearbysearch
     * @param key the api key to be used.
     * @return A string containing the url to be queried.
     */
    public String url(String method, String key) {
      String s = BASE + method + "/json?" + pageToken + location + sensor + keyword 
        + radius + language + minPrice + maxPrice + name + openNow + rankBy + zagatSelected
        + types + "&key=" + key;
      return s;
    }

  }
   
  /**
   * Builder class, used for building the request.
   * Allows the setting of params.
   */
  public static class Builder {
    private final Request r = new Request();
    private PlacesResponse response;

    /**
     * Constructor manages setting of the manditory sensor param.
     * @param sensor whether or not the device obtains location data from a gps device.
     */
    public Builder (boolean sensor) {
      r.sensor="&sensor="+sensor;
    }

    /**
     * Constructor method, should be used on the 2nd page of results or subsequent results.
     * Manages the retrieve of the next page of results and setting of sensor param.
     * @param lastResponse the response object received from the last query.
     */
    public Builder (PlacesResponse lastResponse) {
      response = lastResponse;
      r.sensor=response.sensor;
      r.pageToken = "pagetoken="+response.next_page_token;
    }

    /**
     * Sets the location param. 
     * @param lat the latitude of the location to be set.
     * @param lng the longitude of the location to be set.
     * @return the builder instance.
     */
    public Builder location(double lat, double lng) {
      if (lat < -90 || lat > 90 || lng < -180 || lng > 180) 
        throw new IllegalArgumentException ("InvalidCoordinate");
      r.location = "location=" + lat + "," + lng;
      return this;
    }
    
    /**
     * Set the keyword param in the request. 
     * @param keyword the value which the keyword param should be given in the request.
     * @return the builder instance.
     */
    public Builder keyword(String keyword) {
      r.keyword = "&keyword=" + keyword;
      return this;
    }
        
    /**
     * Set the radius param in the request. 
     * @param radius the value which the radius param should be given in the request.
     * @return the builder instance.
     */
    public Builder radius(int radius) {
      if (radius <= 0 || radius > 50000) 
        throw new IllegalArgumentException ("InvalidRadius");
      r.radius = "&radius=" + radius;
      return this;
    }
        
    /**
     * Set the language param in the request. 
     * @param language the value which the language param should be given in the request.
     * @return the builder instance.
     */
    public Builder language (String language) {
      r.language = "&language=" +language;
      return this;
    }
        
    /**
     * Set the minprice param in the request. 
     * @param minPrice the value which the minprice param should be given in the request.
     * @return the builder instance.
     */
    public Builder minPrice(int minPrice) {
      if (minPrice <0 || minPrice > 4) 
        throw new IllegalArgumentException ("InvalidMinPrice");
      r.minPrice = "&minprice=" +minPrice;
      return this;
    }
        
    /**
     * Set the maxPrice param in the request. 
     * @param maxPrice the value which the maxprice param should be given in the request.
     * @return the builder instance.
     */
    public Builder maxPrice(int maxPrice) {
      if (maxPrice <0 || maxPrice > 4) 
        throw new IllegalArgumentException ("InvalidMaxPrice");
      r.maxPrice = "&maxprice=" +maxPrice;
      return this;
    }
        
    /**
     * Set the name param in the request. 
     * @param name the value which the name param should be given in the request.
     * @return the builder instance.
     */
    public Builder name (String name) {
      r.name = "&name=" +name;
      return this;
    }
        
    /**
     * Set the opennow param in the request. 
     * @return the builder instance.
     */        
    public Builder openNow () {
      r.openNow = "&opennow=";
      return this;
    }
        
    /**
     * Set the rankby param in the request. 
     * @param rankBy the value which the rankby param should be given in the request.
     * @return the builder instance.
     */
    public Builder rankBy (String rankBy) {
      r.rankBy = "&rankby=" +rankBy;
      return this;
    }
                
    /**
     * Set the types param in the request. 
     * @param types the value which the types param should be given in the request.
     * @return the builder instance.
     */
    public Builder types(String... types) {
      r.types = "&types=" + concatURLValues(types);
      return this;
    }

    /**
     * Once the request has been set up this method will return the request.
     * @returns a request object which reflects the values each params were set by the builder.
     */    	
    public Request build() {
      return r;
    }
  }
    
  /**
   * Perform a nearbysearch based on a given request.
   * @param r a request to be queried.
   * @return The response from the request in the form of a PlacesResponse object.
   */
  public PlacesResponse nearbySearch(Request r) {
    InputStream content;
    content = googleServiceSearch(r.url ("nearbysearch", mKey));
    return jsonToPlacesResponse (content, r);
  }

  /**
   * Take some json as an inputStream returning it as PlacesResponse object.
   * @param content an InputStream of json to be converted.
   * @param r the current request relative to the json input.
   * @return A placesResponse with the results extracted from the json content.
   */
  public PlacesResponse jsonToPlacesResponse (InputStream content, Request r) {
    PlacesResponse searchResponse = new Gson().fromJson(new InputStreamReader(content), PlacesResponse.class);
    searchResponse.sensor=r.sensor;
    return searchResponse;
  }
}
