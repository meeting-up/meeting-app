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
import java.util.Arrays;

/**
 * PlacesServiceTest class, to test PlacesService.
 */
public class PlacesServiceTest extends TestCase {
  String data = "{\n"+
    "   \"debug_info\" : [],\n"+
    "   \"html_attributions\" : [\n"+
    "      \"Listings by \\u003ca href=\\\"http://www.yellowpages.com.au/\\\"\\u003eYellow Pages\\u003c/a\\u003e\"\n"+
    "   ],\n"+
    "   \"next_page_token\" : \"CmRTAAAAxrhDUPaML6eeEhXJ33rEpSOrooutzFA5rqHZ0Vng0S5kMLMQ4m7y7MsF0DJvnDAX0eVXOiqBkAX57b2wDbsCBqfC_VDNlNIfV6ZtvCdZhhC0pp4fQmqaypwPDg45HMuhEhDehkSgiN9ntpb4xY3fwA6OGhT5d7vnfb5Um8unaULDtMScAm1u2A\",\n"+
    "   \"results\" : [\n"+
    "      {\n"+
    "         \"geometry\" : {\n"+
    "            \"location\" : {\n"+
    "               \"lat\" : -33.86978,\n"+
    "               \"lng\" : 151.196936\n"+
    "            }\n"+
    "         },\n"+
    "         \"icon\" : \"http://maps.gstatic.com/mapfiles/place_api/icons/generic_business-71.png\",\n"+
    "         \"id\" : \"5e5c1b219283949a7309bf7bf544ed458be059c5\",\n"+
    "         \"name\" : \"Pulse Foods & Health\",\n"+
    "         \"opening_hours\" : {\n"+
    "            \"open_now\" : true\n"+
    "         },\n"+
    "         \"photos\" : [\n"+
    "            {\n"+
    "               \"height\" : 1200,\n"+
    "               \"html_attributions\" : [ \"From a Google User\" ],\n"+
    "               \"photo_reference\" : \"CnRrAAAAIqtBcU7HCOOAhk8iprCYcdkfL_fvq2k5Rqeif53FBHXAuBueJ20RboG8F6k4EytFSZM6xXNMnEVxrqr1tLmprCiuFDix4ha3JMZHOeMav_8iiHns0B7TxNTOaviV5PXLfzOaNvfx8i7-ebbLRuEZBRIQC4MRppzg_zEM5cFs3chwtRoUAnfgoZ7CkqsQQezLT2SNInSvZno\",\n"+
    "               \"width\" : 1200\n"+
    "            }\n"+
    "         ],\n"+
    "         \"reference\" : \"CoQBdQAAALfDFDpAfn1-ljsFbKuCZ1z-8Z00d8W9oX9anaczyLMXLm0UKplacBSI43rvZXOD7b3R6T4dU2KK30GlF3x2fAlJbUOZIi3OIzg18rGRNxMIfaeouNQogD9GtyBzsBTyp4I5uMI3OQapw8cEYUg53bejQIuitbvef1iRbZPtKQc0EhAI3e36nSMQatYwsLC8AEiZGhTttwnHo9GYEHpDZ_twbjXHbdZMqQ\",\n"+
    "         \"types\" : [ \"health\", \"grocery_or_supermarket\", \"food\", \"store\", \"establishment\" ],\n"+
    "         \"vicinity\" : \"60 Union Street, Pyrmont\"\n"+
    "      },\n"+
    "   ],\n"+
    "   \"status\" : \"OK\"\n"+
    "}\n";
  //    InputStream content;

  /**
   * Test the URL method.
   */
  public void testUrl() {
    PlacesService p = new PlacesService("mykey");
    
    PlacesService.Request r = PlacesService.request(false)
        .location(90, 180)
        .keyword("Italian")
        .radius(50000)
        .language("fr-fr")
        .maxPrice(4)
        .minPrice(0)
        .name("anything")
        .openNow()
        .types("food")
        .rankBy("distance")
        .build();
    assertEquals("https://maps.googleapis.com/maps/api/place/nearbysearch/json" +
                 "?location=90.0,180.0&sensor=false&keyword=Italian&radius=50000" +
                 "&language=fr-fr&minprice=0&maxprice=4&name=anything&opennow=&rankby=distance&types=food" +
                 "&key=mykey", r.url("nearbysearch","mykey"));
    
    r = PlacesService.request(false)
        .location(-90, -180)
        .build();
    assertEquals("https://maps.googleapis.com/maps/api/place/nearbysearch/json?" +
        "location=-90.0,-180.0&sensor=false&key=mykey", r.url("nearbysearch", "mykey"));

    r = PlacesService.request(false)
        .radius(1)
        .build();
    assertEquals("https://maps.googleapis.com/maps/api/place/nearbysearch/json?"+
                  "&sensor=false&radius=1&key=mykey", r.url("nearbysearch", "mykey"));
    
    r = PlacesService.request(false)
        .radius(50000)
        .build();
    assertEquals("https://maps.googleapis.com/maps/api/place/nearbysearch/json?&sensor=false" +
                  "&radius=50000&key=mykey", r.url("nearbysearch", "mykey"));
    try {
      r = PlacesService.request(false)
          .radius(0)
          .build();
      fail("Missing exception");
    } catch (IllegalArgumentException e) {
      assertEquals(e.getMessage(), "InvalidRadius");
    }
    
    // Makes no sense, but for testing.
    r = PlacesService.request(false)
        .minPrice(4)
        .maxPrice(0)
        .build();
    assertEquals("https://maps.googleapis.com/maps/api/place/nearbysearch/json?&sensor=false" +
                 "&minprice=4&maxprice=0&key=mykey", r.url("nearbysearch", "mykey"));
    
    InputStream content;
    try {
      content = new ByteArrayInputStream(data.getBytes("UTF-8"));
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
    
    PlacesResponse response = p.jsonToPlacesResponse(content, r);
    r = PlacesService.request(response)
        .build();
    assertEquals("https://maps.googleapis.com/maps/api/place/nearbysearch/" +
                 "json?pagetoken=CmRTAAAAxrhDUPaML6eeEhXJ33rEpSOrooutzFA5" +
                 "rqHZ0Vng0S5kMLMQ4m7y7MsF0DJvnDAX0eVXOiqBkAX57b2wDbsCBqfC" +
                 "_VDNlNIfV6ZtvCdZhhC0pp4fQmqaypwPDg45HMuhEhDehkSgiN9ntpb" +
                 "4xY3fwA6OGhT5d7vnfb5Um8unaULDtMScAm1u2A&sensor=false" +
                 "&key=mykey", r.url("nearbysearch", "mykey"));
  }

  public void testJsonToPlacesResponse() {
    String[] htmlAttribs = {"Listings by <a href=\"http://www.yellowpages.com.au/\">Yellow Pages</a>"};
    String[] types = {"health", "grocery_or_supermarket", "food", "store", "establishment"};
    InputStream content;
    
    try {
      content = new ByteArrayInputStream(data.getBytes("UTF-8"));
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException (e);
    }

    // Create a request which just needs to have a sensor param for this to work.
    PlacesService p = new PlacesService("mykey");
    PlacesService.Request r = PlacesService.request(false)
        .build();
    PlacesResponse response = p.jsonToPlacesResponse(content, r);
    assertEquals("OK", response.status);
    assertTrue(Arrays.equals(htmlAttribs, response.html_attributions));
    assertEquals("CmRTAAAAxrhDUPaML6eeEhXJ33rEpSOrooutzFA5rqHZ0Vng0S5kMLMQ4m7y7MsF0DJvnDA" +
                 "X0eVXOiqBkAX57b2wDbsCBqfC_VDNlNIfV6ZtvCdZhhC0pp4fQmqaypwPDg45" +
                 "HMuhEhDehkSgiN9ntpb4xY3fwA6OGhT5d7vnfb5Um8unaULDtMScAm1u2A", 
                 response.next_page_token);
    assertEquals("&sensor=false", response.sensor);
    PlaceResult result = response.results.get(0);
    assertEquals("Pulse Foods & Health", result.name);
    assertEquals("CoQBdQAAALfDFDpAfn1-ljsFbKuCZ1z-8Z00d8W9oX9anaczyLMXLm0UKplacBSI43rvZXOD7b" +
                  "3R6T4dU2KK30GlF3x2fAlJbUOZIi3OIzg18rGRNxMIfaeouNQogD9GtyBzsBTyp4I5uMI3OQa" +
                  "pw8cEYUg53bejQIuitbvef1iRbZPtKQc0EhAI3e36nSMQatYwsLC8AEiZGhTttwnHo9GYEHpDZ" +
                  "_twbjXHbdZMqQ", result.reference);
    assertEquals("5e5c1b219283949a7309bf7bf544ed458be059c5", result.id);
    assertEquals("http://maps.gstatic.com/mapfiles/place_api/icons/generic_business-71.png",
                  result.icon);
    assertEquals("60 Union Street, Pyrmont", result.vicinity);
    assertEquals(result.geometry.location.lat, -33.86978);
    assertEquals(151.196936, result.geometry.location.lng);
    PlaceResult.Photo photo = result.photos.get(0);
    assertEquals(1200, photo.height);
    assertEquals(1200, photo.width);
    assertEquals("From a Google User", photo.html_attributions[0]);
    assertEquals("CnRrAAAAIqtBcU7HCOOAhk8iprCYcdkfL_fvq2k5Rqeif53FBHXAuBueJ20RboG8F6k4EytFS"+
                 "ZM6xXNMnEVxrqr1tLmprCiuFDix4ha3JMZHOeMav_8iiHns0B7TxNTOaviV5PXLfzOaNvfx8i7"+
                 "-ebbLRuEZBRIQC4MRppzg_zEM5cFs3chwtRoUAnfgoZ7CkqsQQezLT2SNInSvZno", 
                 photo.photo_reference);
    assertTrue(Arrays.equals (types, result.types));
    assertEquals(true, result.opening_hours.open_now);
  }
}
