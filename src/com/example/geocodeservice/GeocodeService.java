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

import com.google.gson.Gson;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Service that takes an address (in address form, latitude/longitude pair) or a number of component
 * filters and returns geographic coordinates and additional information about that particular area.
 */
public class GeocodeService {
  private static final String TAG = GeocodeService.class.getSimpleName();
  private static final String BASE = "http://maps.googleapis.com/maps/api/geocode/json?";

  private static final List<String> ALLOWED_COMPONENTS =
      Arrays.asList("route", "locality", "administrative_area", "postal_code", "country");
  private static final String BOUNDS_FORMAT = "&bounds=%f,%f|%f,%f";

  /**
   * Creates a new Builder object which is used to construct a request.
   * 
   * @param usesSensor  Whether the request being generated comes from a device
   * using a sensor or not.
   * @return A new Builder object.
   */
  public static Builder request(boolean usesSensor) {
    return new Builder(usesSensor);
  }

  public static class Request {
    private String address = "";
    private String latlng = "";
    private String components = "";
    private String sensor = "";
    private String bounds = "";
    private String language = "";
    private String region = "";


    public String url() {
      /*
       * if only the components are specified then it'll be our start argument so we need to get rid
       * of the beginning &
       */
      if (address == "" && latlng == "" && components != "") {
        components = components.substring(1);
      }
      String s = BASE + address + latlng + components + sensor + bounds + language + region;
      Log.d(TAG, s);
      return s;
    }
  }

  public static class Builder {
    private final Request r = new Request();
    
    public Builder(boolean usesSensor) {
      r.sensor = "&sensor=" + (usesSensor? "true" : "false");
    }

    public Builder address(String addr) {
      try {
        r.address = "address=" + URLEncoder.encode(addr.replace(" ", "+"), "UTF-8");
      } catch (UnsupportedEncodingException e) {
        throw new RuntimeException("UTF-8 encoding not supported");
      }
      return this;
    }

    public Builder latlng(double latitude, double longitude) {
      r.latlng = "latlng=" + latitude + "," + longitude;
      return this;
    }

    /**
     * Sets the components of a request to a form like "administrative_area:TX|country:US"
     *
     * @param arguments A map of component-value key-pairs
     * @return A Builder object.
     */
    public Builder components(HashMap<String, String> arguments) {
      String value;
      boolean firstValue = true;

      for (String component : ALLOWED_COMPONENTS) {
        value = arguments.get(component);

        if (value != null) {
          if (!firstValue) {
            r.components += "|";
          } else {
            firstValue = false;
          }
          r.components += component + ":" + value;
        }
      }
      return this;
    }
    
    public Builder bounds(double lat1, double long1, double lat2, double long2) {
      r.bounds = String.format(BOUNDS_FORMAT, lat1, long1, lat2, long2);
      return this;
    }

    public Builder language(String lang) {
      r.language = "&language=" + lang;
      return this;
    }

    public Builder region(String regionCode) {
      r.region = "&region" + regionCode;
      return this;
    }

    public Request build() {
      return r;
    }
  }

  private HttpResponse executeRequest(Request r) {
    try {
      return new DefaultHttpClient().execute(new HttpGet(r.url()));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   *
   * @param r A Request object which contains information about the query to be made to the
   *        Geocoding service.
   * @return A GeocodeResponse which contains the response given back.
   */
  public GeocodeResponse getAddress(Request r) {
    HttpResponse response = executeRequest(r);
    InputStream content;
    try {
      content = response.getEntity().getContent();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    Log.d(TAG, "geocoding request made successfully");
    return new Gson().fromJson(new InputStreamReader(content), GeocodeResponse.class);
  }
}
