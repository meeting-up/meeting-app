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

package com.example.placedetails;

import com.google.gson.Gson;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Service that returns information about a place, given its reference as given in a Places Search
 * call. Return is in the form of a DetailsResponse object.
 */
public class DetailsService {
  private static final String TAG = DetailsService.class.getSimpleName();
  private static final String BASE = "https://maps.googleapis.com/maps/api/place/";

  private final String apiKey;

  public DetailsService(String key) {
    apiKey = key;
  }

  public static Builder request() {
    return new Builder();
  }

  public static class Request {
    private String reference = "";
    private String sensor = "";
    private String language = "";
    private String extensions = "";

    private Request() {}

    /**
     * @param method What type of request is to be made - in this case, "details"
     * @param key The API key required to make a query.
     * @return Returns a string URL with the required parameters.
     */
    private String url(String method, String key) {
      String s =
          BASE + method + "/json?" + reference + sensor + language + extensions + "&key=" + key;
      Log.d(TAG, s);
      return s;
    }
  }

  public static class Builder {
    private final Request r = new Request();

    public Builder sensor(boolean sensor) {
      r.sensor = "&sensor=" + sensor;
      return this;
    }

    public Builder reference(String ref) {
      r.reference = "reference=" + ref;
      return this;
    }

    public Builder language(String lang) {
      r.language = "&language=" + lang;
      return this;
    }

    public Builder extensions(String... extensions) {
      if (extensions.length == 0) {
        return this;
      }

      StringBuilder sb = new StringBuilder("&extensions=");
      for (String ext : extensions) {
        sb.append(ext);
        sb.append('|');
      }
      sb.deleteCharAt(sb.length() - 1);
      r.extensions = sb.toString();
      return this;
    }

    public Request build() {
      return r;
    }
  }

  /**
   * @param method What type of Places API request is being made.
   * @param r The request object.
   * @return An HTTP response.
   * @throws RuntimeException If there is an error getting the URL.
   */
  private HttpResponse executeRequest(String method, Request r) {
    try {
      return new DefaultHttpClient().execute(new HttpGet(r.url(method, apiKey)));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * @param r The request being made.
   * @return A DetailsResponse object with the details given by the request.
   */
  public DetailsResponse detailSearch(Request r) {
    HttpResponse response = executeRequest("details", r);
    InputStream content;
    try {
      content = response.getEntity().getContent();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return new Gson().fromJson(new InputStreamReader(content), DetailsResponse.class);
  }
}
