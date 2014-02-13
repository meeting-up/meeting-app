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

/**
 * StaticMapTest class, to test Staticmap.
 */
public class StaticMapTest extends TestCase {
  /**
   * Test the URL method.
   */
  public void testUrl () {
    String[] styles = {"color","blue", "label", "S"};
    String[] location = {"40.702147", "-74.015794"};
    
    StaticMap.Request r = StaticMap.request(false)
        .center(-33.8670522, 151.1957362)
        .zoom(21)
        .size(800,600)
        .visual_refresh(true)
        .scale(StaticMap.Builder.ScaleValues.SCALE_2X)
        .format(StaticMap.Builder.FormatValues.FORMAT_PNG)
        .mapType(StaticMap.Builder.MapTypeValues.ROADMAP)
        .language("fr-fr")
        .markers(styles, location).build();
    assertEquals(r.url("&key=mykey"), 
                 "https://maps.googleapis.com/maps/api/staticmap" +
                 "?center=-33.8670522,151.1957362&zoom=21" +
                 "&markers=color%3Ablue%7Clabel%3AS|40.702147%7C-74.015794" +
                 "&size=800x600&visual_refresh=true&scale=2&format=png" +
                 "&maptype=roadmap&language=fr-fr&sensor=false&key=mykey");
    
    r = StaticMap.request(false)
        .center("Sydney, NSW, Australia")
        .build();
    assertEquals(r.url(""), 
                 "https://maps.googleapis.com/maps/api/staticmap" +
                 "?center=Sydney%2C+NSW%2C+Australia&sensor=false");
    try {
      r = StaticMap.request(false)
          .zoom(22)
          .build();
      fail("Missing exception");
    } catch (IllegalArgumentException e) {
      assertEquals(e.getMessage(), "InvalidZoomValue");
    }
    
    try {
      r = StaticMap.request(false)
          .zoom(-1)
          .build();
      fail("Missing exception");
    } catch (IllegalArgumentException e) {
      assertEquals(e.getMessage(), "InvalidZoomValue");
    }
    
    r = StaticMap.request(false)
        .center("Sydney, NSW, Australia")
        .zoom(0)
        .build();
    assertEquals(r.url(""), 
                 "https://maps.googleapis.com/maps/api/staticmap" +
                 "?center=Sydney%2C+NSW%2C+Australia&zoom=0&sensor=false");
  }
}
