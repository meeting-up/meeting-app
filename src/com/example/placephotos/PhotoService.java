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

package com.example.placephotos;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;

/**
 * This class allows a photo to be retrieved, given a photoreference as returned in a Places
 * Search/Details call.
 */
public class PhotoService {
  private static final String TAG = PhotoService.class.getSimpleName();
  private static final String BASE = "https://maps.googleapis.com/maps/api/place/photo?";

  private String apiKey;

  public PhotoService(String key) {
    apiKey = key;
  }

  public static Builder request() {
    return new Builder();
  }

  public static class Request {
    public String key = "";
    public String photoreference = "";
    public String sensor = "";
    public String maxheight = "";
    public String maxwidth = "";

    public Request() {}

    public String url(String key) {
      String URL = BASE + photoreference + sensor + maxheight + maxwidth + "&key=" + key;
      Log.d(TAG, URL);
      return URL;
    }
  }

  public static class Builder {
    private final Request r = new Request();

    public Builder photoreference(String reference) {
      r.photoreference = "photoreference=" + reference;
      return this;
    }

    public Builder sensor(boolean usesSensor) {
      r.sensor = "&sensor=" + usesSensor;
      return this;
    }

    public Builder maxheight(int height) {
      r.maxheight = "&maxheight=" + height;
      return this;
    }

    public Builder maxwidth(int width) {
      r.maxwidth = "&maxwidth=" + width;
      return this;
    }

    public Request build() {
      return r;
    }
  }

  private HttpResponse executeRequest(Request r) {
    try {
      return new DefaultHttpClient().execute(new HttpGet(r.url(apiKey)));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }


  /**
   *
   * @param r Request object which contain the query parameters/
   * @return A Bitmap as returned by the Places Photos API.
   */
  public Bitmap getPhoto(Request r) {
    HttpResponse response = executeRequest(r);
    InputStream content;
    try {
      content = response.getEntity().getContent();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return BitmapFactory.decodeStream(content);
  }
}
