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

import java.io.*;
import java.net.*;

/** 
 * StaticMap class.
 * Contains the methods required to set google map API params and receive image data back in the form of an InputStream.
 */



public class StaticMap {
  private static final String BASE = "https://maps.googleapis.com/maps/api/staticmap?";
  private final String mKey;

  /**
   * Constructor method. Used to set the optional key param.
   * @param key The value which should be passed for the key param in the request.
   */
  public StaticMap(String key) {
    mKey = "&key="+key;
  }

  /**
   * Constructor method. No key supplied so do not use a key in the request.
   */
  public StaticMap () {
    mKey = "";
  }

  /**
   * Begin a request by instanciating and returning a Builder class. 
   * This allows the relevant params to be set.
   * @param sensor the value to be passed in the request for the manditory sensor param.
   * @return A new Builder object.
   */
  public static Builder request(boolean sensor) {
    return new Builder(sensor);
  }

  /**
   * Request class.
   * Holds the values for all possible params to be passed in the request.
   */
  public static class Request {
    private String center = "";
    private String sensor = "";
    private String zoom = "";
    private String size = "";
    private String visual_refresh = "";
    private String scale = "";
    private String format = "";
    private String maptype = "";
    private String language = "";
    private String region = "";
    private StringBuilder markers = new StringBuilder("");
    private StringBuilder path = new StringBuilder("");
    private String visible = "";
    private String style = "";

    private Request() {
    }

    /**
     * Generate a URL string which will be queried to get the map.
     * @param key The api key to be included in the url.
     * @return A String containing the url to be queried.
     */
    public String url(String key) {
      return BASE + center + zoom + markers.toString () + 
        path.toString () + style.toString () + size + visual_refresh 
        + scale + format + maptype + language + region + visible + sensor + key;
    }
  }

  /**
   * Builder class, used to set all url params and ultimately return a Request object.
   */   
  public static class Builder {
    /**
     * Scale Values enum.
     * Holds all possible scale constants.
     */
    public static enum ScaleValues {
      SCALE_1X, SCALE_2X, SCALE_4X
        }

    /**
     * Format Values enum.
     * Holds all possible formats.
     */
    public static enum FormatValues {
      FORMAT_PNG, FORMAT_PNG8, FORMAT_PNG32, FORMAT_GIF, FORMAT_JPG, 
        FORMAT_JPG_BASELINE
        }

    /**
     * MapTypeValues enum.
     * Holds all possible map types.
     */
    public static enum MapTypeValues {
      ROADMAP, SATELLITEMAP, TERRAINMAP, HYBRIDMAP
        }

    private final Request r = new Request();

    /**
     * Constructor method handles setting manditory sensor field.
     * @param sensor the value of the sensor param to be passed in the url request.
     */
    public Builder (boolean sensor) {
      r.sensor="&sensor="+sensor;
    }

    /**
     * Handle setting of the center param for the request.
     * @param lat the latitude value to be passed in the request.
     * @param lng the longitude value to be passed in the request.
     * @return The Builder instance with the center param set appropriately.
     */
    public Builder center(double lat, double lng) {
      r.center = "center=" + lat + "," + lng;
      return this;
    }

    /**
     * Handle setting of the center param for the request.
     * @param address The address to set the center param to. 
     * @return The Builder instance with the center param set appropriately.
     */
    public Builder center(String address) {
      try {
        r.center = "center=" + URLEncoder.encode(address, "UTF-8");
      } catch (UnsupportedEncodingException e) {
        throw new RuntimeException("UTF-8 encoding not supported");
      }
      return this;
    }

    /**
     * Set the zoom param. 
     * @param zoom the zoom value to be passed to the api in the request. 
     * @return The Builder instance with zoom field set appropriately.
     */
    public Builder zoom (int zoom) {
      if (zoom < 0 || zoom > 21)
        throw new IllegalArgumentException ("InvalidZoomValue");

      r.zoom="&zoom="+zoom;
      return this;
    }

    /**
     * Set the size param in the request.
     * @param horizontal the horizontal size value to be passed in the request.
     * @param vertical the vertical size value to be passed in the request.
     * @return The builder instance with size param set.
     */
    public Builder size(int horizontal, int vertical) {
      r.size = "&size=" + horizontal + "x" + vertical;
      return this;
    }

    /**
     * Set visual_refresh param. 
     * @param visual_refresh value to set visual_refresh param to in the request. 
     * @return builder instance with visual_refresh param set.
     */
    public Builder visual_refresh (boolean visual_refresh) {
      r.visual_refresh="&visual_refresh="+visual_refresh;
      return this;
    }

    /**
     * Set scale param.
     * @param scale the value to pass for the scale param in the request.
     * @return Builder instance with scale field set.
     */
    public Builder scale(ScaleValues scale) {
      int value;
      switch (scale) {
      case SCALE_1X:
        value = 1;
        break;
      case SCALE_2X:
        value = 2;
        break;
      case SCALE_4X:
        value = 4;
        break;
      default:
        throw new IllegalArgumentException("InvalidScale");
      }
      r.scale = "&scale=" + value;
      return this;
    }

    /**
     * Set format param. 
     * @param format the format value to pass to the request.
     * @return the builder instance with format field set.
     */
    public Builder format (FormatValues format) {
      String value = "";
      switch (format) {
      case FORMAT_PNG:
        value = "png";
        break;
      case FORMAT_PNG8:
        value = "png8";
        break;
      case FORMAT_PNG32:
        value = "png32";
        break;
      case FORMAT_GIF:
        value = "gif";
        break;
      case FORMAT_JPG:
        value = "jpg";
        break;
      case FORMAT_JPG_BASELINE:
        value = "jpg-baseline";
        break;
      default:
        throw new IllegalArgumentException("InvalidFormatValue");
      }
      r.format = "&format=" + value;
      return this;
    }

    /**
     * Set maptype param. 
     * @param maptype The value to set the maptype param in the request.
     * @return Builder instance with maptype field set.
     */
    public Builder mapType (MapTypeValues maptype) {
      String value = "";
      switch (maptype) {
      case ROADMAP:
        value = "roadmap";
        break;
      case SATELLITEMAP:
        value = "satellite";
        break;
      case TERRAINMAP:
        value = "terrain";
        break;
      case HYBRIDMAP: 
        value = "hybrid";
        break;
      default:
        throw new IllegalArgumentException("InvalidMapType");
      }
      r.maptype = "&maptype=" + value;
      return this;
    }

    /**
     * Sets the language param in the request.
     * @param language The language value to be passed to the api request.
     * @return Builder instance with language field set.
     */
    public Builder language(String language) {
      r.language = "&language=" + language;
      return this;
    }

    /**
     * Set the region param in the request.
     * @param region The region to be set in the request.
     * @return Builder instance with region field set.
     */
    public Builder region(String region) {
      r.region = "&region=" + region;
      return this;
    }

    /**
     * Set markers param.
     * @param style The list of styles for the marker.
     * @param location the array of locations to place markers.
     * @return Builder instance with markers set.
     */
    public Builder markers(String[] style, String[] location) { 
      r.markers.append(joinStyles("&markers=", style)
                       + joinParamValues("|", location));
      return this;
    }

    /**
     * Set the path param in the request.
     * @param path The list of path values to be sent in the request.
     * @return Builder instance with path field set.
     */
    public Builder path(String... path) {
      r.path.append(joinParamValues("&path=", path));
      return this;
    }

    /**
     * Sets the visible param in the request. 
     * @param visible The value to set visible param in the request. 
     * @return Builder instance with visible field set.
     */
    public Builder visible(String... visible) {
      r.visible = joinParamValues("&visible=", visible);
      return this;
    }

    /**
     * Set style param.
     * @param styles value for the styles param to be sent in the request. 
     * @return Builder isntance with styles field set.
     */
    public Builder style(String... styles) {
      r.style = joinStyles("&styles=", styles);
      return this;
    }

    /**
     * Return the request instance.
     * The request instance has all of the fields pertaining to the params to be sent in the request. 
     * @return The request of the Build instance.
     */
    public Request build() {
      return r;
    }
  }

  /**
   * Fetch the image which reflects the params passed to the static maps api.
   * @param r The request which should be used to fetch the image.
   * @return InputStream holding image data.
   * @throws RuntimeException if we can not get a response.
   */
  public InputStream fetch(Request r) {
    URLConnection connection;
    try {
      URL url = new URL(r.url(mKey));
      connection = url.openConnection();
      return connection.getInputStream();
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    } 
  }


  private static String joinParamValues (String prefix, String[] values) {
    if (values.length == 0) return "";

    StringBuilder sb = new StringBuilder("");
    for (String v : values) {
      sb.append(v);
      sb.append('|');
    }
    sb.deleteCharAt(sb.length() - 1);
    
    try {
      return prefix + URLEncoder.encode(sb.toString(), "UTF-8");
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException("UTF-8 encoding not supported");
    }
  }

  private static String joinStyles (String prefix, String[] styles) {
    if (styles.length == 0) return "";

    StringBuilder sb = new StringBuilder("");
    boolean insertBar = false; 
    for (String s : styles) {
      sb.append(s);
      if (insertBar) {
        sb.append('|');
      } else {
        sb.append (":");
      }
      insertBar=!insertBar;
    }
    sb.deleteCharAt(sb.length() - 1);
    
    try {
      return prefix+URLEncoder.encode(sb.toString(), "UTF-8");
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException("UTF-8 encoding not supported");
    }
  }
}
