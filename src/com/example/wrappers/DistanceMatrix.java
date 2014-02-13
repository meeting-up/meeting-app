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
 * DistanceMatrix class, used to initialise and send a request to the distance matrix web api.
 */
public class DistanceMatrix extends PlaceWrapper {
  private static final String BASE = "https://maps.googleapis.com/maps/api/distancematrix/";

  /**
   * Instanciate a Builder class which can be used to set up the query.
   * @param sensor the value of the sensor param in the request.
   * @returns A builder object.
   */
  public static Builder request (boolean sensor) {
    return new Builder (sensor);
  }

  /**
   * The request class, holds all data for the request query.
   */
  public static class Request {
    private String origins = "";
    private String sensor = "";
    private String destinations = "";
    private String mode = "";
    private String language = "";
    private String avoid = "";
    private String units = "";
    private String departure_time = "";

    private Request() {
    }

    /**
     * URL method, generates the url string.
     * Formed by the values of the params in the Request class.
     * @returns the String of the URL.
     */
    public String url() {
      return BASE + "json?" + origins + destinations + mode + language + avoid + units + sensor;
    }
  }

  /**
   * Builder class. This class provides methods to set up the request.
   */
  public static class Builder {
    private final Request r = new Request();

    /**
     * Enum containing possible mode values.
     */
    public static enum Mode {
      DRIVINGMODE, WALKINGMODE, BICYCLINGMODE
    }

    /**
     * Avoid enum containing possible avoid values. 
     */
    public static enum Avoid {
      AVOIDTOLLS, AVOIDHIGHWAYS
    }

    /**
     * Units enum, contains possible unit values.
     */
    public static enum Units {
      METRICUNITS, IMPERIALUNITS
    }

    /**
     * Builder constructor method.
     * @param sensor The value which sensor param should be set to. 
     */
    public Builder (boolean sensor) {
      r.sensor="&sensor="+sensor;
    }

    /**
     * Set the origins param in the request. 
     * @param origins A String array of origins as addresses.
     * @returns the builder instance.
     */
    public Builder origins (String... origins) {
      r.origins = "origins="+concatURLValues (origins);
      return this;
    }

    /**
     * Set the origins param in the request.
     * @param origins an array of doubles representing lat/long pairs eg. {lat1,lng1,lat2,lng2,...}
     * @returns the builder instance 
     */
    public Builder origins (double... origins) {
      r.origins="origins="+concatURLValues (geographicCoordsToString (origins));
      return this;
    }

    /**
     * Sets the destinations param in the request. 
     * @param destinations an array of addresses. 
     * @returns builder instance.
     */
    public Builder destinations (String... destinations) {
      r.destinations = "&destinations="+concatURLValues (destinations);
      return this;
    }

    /**
     * Set the destinations param in the request. 
     * @param destinations an array of doubles holding lat,long pairs eg. {lat1,lng1,lat2,lng2,...}
     * @returns the builder instance.
     */
    public Builder destinations (double... destinations) {
      r.destinations="&destinations="+concatURLValues (geographicCoordsToString (destinations));
      return this;
    }

    /**
     * Set mode param in request. 
     * @param mode the enum Mode value to set mode to. 
     * @returns builder instance. 
     */
    public Builder mode (Mode mode) {
      String value;
      switch (mode) {
      case DRIVINGMODE:
        value="driving";
        break;
      case WALKINGMODE:
        value="walking";
        break;
      case BICYCLINGMODE:
        value="bicycling";
        break;
      default:
        throw new IllegalArgumentException ("InvalidMode");
      }
      r.mode="&mode="+value;
      return this;
    }

    /**
     * Set language param in request. 
     * @param language Language code to set langauge request param to.
     * @returns builder instance. 
     */
    public Builder language (String language) {
      r.language="&language="+language;
      return this;
    }

    /**
     * Set avoid param in the request.
     * @param avoid One of the desired values to set avoid param to in the Avoid enum.
     * @returns Builder instance.
     */
    public Builder avoid (Avoid avoid) {
      String value = "";
      switch (avoid) {
      case AVOIDTOLLS:
        value="tolls";
        break;
      case AVOIDHIGHWAYS:
        value="highways";
        break;
      default:
        throw new IllegalArgumentException ("InvalidAvoids");
      }
      r.avoid="&avoid="+value;
      return this;
    }

    /**
     * Set the units param in request. 
     * @param units A valid Units enum value to set the units param to in request.
     * @returns builder instance.
     */
    public Builder units (Units units) {
      String value = "";
      switch (units) {
      case METRICUNITS:
        value="metric";
        break;
      case IMPERIALUNITS:
        value="imperial";
        break;
      default:
        throw new IllegalArgumentException ("InvalidUnit");
      }
      r.units="&units="+value;
      return this;
    }

    /**
     * After build has set all params return the resulting request.
     * @returns The request with given params set.
     */
    public Request build() {
      return r;
    }
  }
    
  /**
   * Perform the query, returning the DistanceMatrixResponse containing results.
   * @param r The request to query with.
   * @returns the results in DistanceMatrixResponse object.
   */
  public DistanceMatrixResponse search(Request r) {
    InputStream content;
    content = googleServiceSearch(r.url());
    return jsonToDistanceMatrixResponse (content);
  }

  /**
   * jsonToDistanceMatrixResponse, take a json InputStream and 
   * convert it to a DistanceMatrixResponse object.
   * @param content The json content as an InputStream.
   * @returns the json as a DistanceMatrixResponse object.
   */
  public static DistanceMatrixResponse jsonToDistanceMatrixResponse (InputStream content) {
    return new Gson().fromJson(new InputStreamReader(content), DistanceMatrixResponse.class);
  }
}
