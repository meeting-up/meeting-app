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

import junit.framework.TestCase;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * DistanceMatrixTest class, to test DistanceMatrix.
 */
public class DistanceMatrixTest extends TestCase {
  /**
   * Test the URL method.
   */
  public void testUrl () {
    DistanceMatrix.Request r = DistanceMatrix.request(false)
        .origins("Melbourne, Victoria, Australia")
        .destinations("83 Quay st, Haymarket NSW")
        .mode(DistanceMatrix.Builder.Mode.DRIVINGMODE)
        .language("fr-fr")
        .avoid(DistanceMatrix.Builder.Avoid.AVOIDHIGHWAYS)
        .units(DistanceMatrix.Builder.Units.IMPERIALUNITS)
        .build ();
    assertEquals(r.url(), 
                 "https://maps.googleapis.com/maps/api/distancematrix/json" +
                 "?origins=Melbourne%2C+Victoria%2C+Australia&destinations=83+Quay+st%2C+Haymarket+NSW&mode=driving" +
                 "&language=fr-fr&avoid=highways&units=imperial&sensor=false");
    
    r = DistanceMatrix.request(false)
        .origins(-33.1818111,154.829,-37.5435,153.211)
        .destinations(-33.2222,150.200,21.432,0.0).build();
    assertEquals(r.url(), 
                 "https://maps.googleapis.com/maps/api/distancematrix/json" +
                 "?origins=-33.1818111%2C154.829%7C-37.5435%2C153.211&destinations=-33.2222%2C150.2%7C21.432%2C0.0" +
                 "&sensor=false");
  }

  /**
   * Test jsonToDistanceMatrixResponseTest method.
   */
  public void testJsonToDistanceMatrixResponse () {
    String jsonData = 
        "{\n"+
        "   \"destination_addresses\" : [ \"83 Quay Street, Haymarket NSW 2000, Australia\" ],\n"+
        "   \"origin_addresses\" : [ \"Melbourne VIC, Australia\" ],\n"+
        "   \"rows\" : [\n"+
        "      {\n"+
        "         \"elements\" : [\n"+
        "            {\n"+
        "               \"distance\" : {\n"+
        "                  \"text\" : \"610 mi\",\n"+
        "                  \"value\" : 981939\n"+
        "               },\n"+
        "               \"duration\" : {\n"+
        "                  \"text\" : \"12 hours 45 mins\",\n"+
        "                  \"value\" : 45927\n"+
        "               },\n"+
        "               \"status\" : \"OK\"\n"+
        "            }\n"+
        "         ]\n"+
        "      }\n"+
        "   ],\n"+
        "   \"status\" : \"OK\"\n"+
        "   }\n";
    InputStream content;
    try {
      content = new ByteArrayInputStream(jsonData.getBytes("UTF-8"));
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException (e);
    }
    DistanceMatrixResponse response = DistanceMatrix.jsonToDistanceMatrixResponse (content);
    assertEquals ("Status",response.status, "OK");
    assertEquals ("Destinations", response.destination_addresses.get(0), 
                  "83 Quay Street, Haymarket NSW 2000, Australia");
    assertEquals ("origins", response.origin_addresses.get(0), "Melbourne VIC, Australia");
    assertEquals ("DistanceText", response.rows.get(0).elements.get(0).distance.text, "610 mi");
    assertEquals ("DistanceValue", response.rows.get(0).elements.get(0).distance.value, 981939);
    assertEquals ("DurationText", response.rows.get(0).elements.get(0).duration.text, 
                  "12 hours 45 mins");
    assertEquals ("DurationValue", response.rows.get(0).elements.get(0).duration.value, 45927);
    assertEquals ("elementStatus", response.rows.get(0).elements.get(0).status, "OK");
  }
}
