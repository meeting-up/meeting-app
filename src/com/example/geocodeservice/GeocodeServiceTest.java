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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import junit.framework.TestCase;

/**
 * Test for GeocodeService. Currently extremely basic.
 */
public class GeocodeServiceTest extends TestCase {

  private GeocodeService mGeocodeService = new GeocodeService();

  public void testPreconditions() {
    assertTrue(mGeocodeService != null);
  }

  public void testRequestUrl() {
    GeocodeService.Request r = new GeocodeService.Request();
    assertTrue(r != null);
    
    assertTrue(r.url().equals("http://maps.googleapis.com/maps/api/geocode/json?"));
    
    r = GeocodeService.request(true)
        .address("98 Balaclava Rd, Eastwood")
        .build();
    assertTrue(r.url().equals("http://maps.googleapis.com/maps/api/geocode/json?" +
        "address=98+Balaclava+Rd,+Eastwood&sensor=true"));
    
    r = GeocodeService.request(true)
        .address("Kentucky")
        .build();
    assertTrue(r.url().equals("http://maps.googleapis.com/maps/api/geocode/json?" +
        "address=Kentucky&sensor=true"));
    
    r = GeocodeService.request(false)
        .address("98 Balaclava Rd, Eastwood")
        .build();
    assertTrue(r.url().equals("http://maps.googleapis.com/maps/api/geocode/json?" +
        "address=98+Balaclava+Rd,+Eastwood&sensor=false"));
    
    r = GeocodeService.request(false)
        .address("Kentucky")
        .build();
    assertTrue(r.url().equals("http://maps.googleapis.com/maps/api/geocode/json?" +
        "address=Kentucky&sensor=false"));
    
    r = GeocodeService.request(true)
        .latlng(0, 0)
        .build();
    assertTrue(r.url().equals("http://maps.googleapis.com/maps/api/geocode/json?" +
        "latlng=0,0&sensor=true"));
    
    r = GeocodeService.request(false)
        .latlng(0, 0)
        .build();
    assertTrue(r.url().equals("http://maps.googleapis.com/maps/api/geocode/json?" +
        "latlng=0,0&sensor=false"));
    
    // How to test other attributes? - also how to test URL without
    // depending on getAddress() to verify valid URL
  }

  /**
   * Test method for {@link com.example.geocodeservice.GeocodeService#getAddress(com.example.geocodeservice.GeocodeService.Request)}.
   */
  public void testGetAddress() {
    
    GeocodeService.Request mockRequest = mock(GeocodeService.Request.class);
    when(mockRequest.url())
        .thenReturn("https://com")
        .thenReturn("http://maps.googleapis.com/maps/api/geocode/json?" +
                 "latlng=-33.8657603,151.1956948&sensor=true")
        .thenReturn("http://maps.googleapis.com/maps/api/geocode/json?" +
                 "latlng=-33.8657603,2000.1956948&sensor=true");
    
    try {
      mGeocodeService.getAddress(mockRequest);
      fail("GeocodeService.getAddress did not throw an error on invalid URL");
    } catch (RuntimeException expected) {}
    
    try {
      GeocodeResponse data = mGeocodeService.getAddress(mockRequest);
      assertTrue(data.status.equals("OK"));
    } catch (RuntimeException e) {
      fail("GeocodeService.getAddress did not retrieve an address.");
    }
    
    try {
      GeocodeResponse data = mGeocodeService.getAddress(mockRequest);
      assertTrue(data.status.equals("ZERO_RESULTS"));
    } catch (Exception e) {
      fail("GeocodeService.getAddress did not handle an empty response.");
    }
    
    verify(mockRequest, times(3)).url();
  }

}
