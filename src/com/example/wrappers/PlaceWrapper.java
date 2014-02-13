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

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
/**
 * PlaceWrapper class.
 * Should be inherited by other wrapper class providing common wrapper functionality.
 */
public class PlaceWrapper {
  private HttpResponse executeRequest(String url) {
    try {
      return new DefaultHttpClient().execute(new HttpGet(url));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Method to perform the request to the server and return the response.
   * @param url The full url to be queried including arguments and there values. 
   * @returns an InputStream containing the response caused by the query.
   */
  public InputStream googleServiceSearch(String url) {
    HttpResponse response = executeRequest(url);
    InputStream content;
    try {
      content = response.getEntity().getContent();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return content;
  }

  /**
   * concatURLValues format a param's arguments correctly.
   * Only used for params that accept multiple values.
   * @param values the array of strings which represent all the values which should be included in the request.
   * @returns A string which has proper URL encoding of the list of values.
   */
  public static String concatURLValues (String... values) {
    if (values.length == 0) return "";
        
    StringBuilder sb = new StringBuilder("");
    for (String v : values) {
      sb.append(v);
      if (v != "") 
        sb.append('|');
    }
    if (sb.length () > 0) 
      sb.deleteCharAt(sb.length() - 1);
    
    try {
      return URLEncoder.encode(sb.toString(), "UTF-8");
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException("UTF-8 encoding not supported");
    }
  }

  /**
   * Take an array of doubles of lat longs and convert to a String array for each lat,lng pair. 
   * @param coords an array of doubles which are lat lng pairs eg. {lat1,lng1,lat2,lng2,...}
   * @returns A string array such that each index of the array is a "lat,lng" pair.
   */
  public static String[] geographicCoordsToString (double[] coords) {
    String[] locations = new String[coords.length/2];
    String current = "";
    for (int i =0; i < coords.length; i++) {
      if (i%2 == 0) {
        current=""+coords[i]+",";
      } else {
        current = current + coords[i];
        locations[i/2] = current;
      }
    }

    return locations;
  }
}
