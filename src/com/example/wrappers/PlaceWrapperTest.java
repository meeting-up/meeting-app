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
import java.util.Arrays;

/**
 * PlaceWrapperTest class, to test PlaceWrapper.
 */
public class PlaceWrapperTest extends TestCase {
  /**
   * Test the concatURLValues method.
   */
  public void testConcatURLValues () {
    assertEquals ("Red%7COrange%7CYellow%7CGreen", 
                  PlaceWrapper.concatURLValues("Red","Orange","Yellow","Green"));
    assertEquals ("", PlaceWrapper.concatURLValues("",""));
    assertEquals ("Red", PlaceWrapper.concatURLValues("Red",""));
  }

  public void testGeographicCoordsToString () {
    double[] coords1 = {33.33,97.54,-37.27,-10.011};
    double[] coords2 = {};
    String[] test1 = {"33.33,97.54","-37.27,-10.011"};
    String[] test2 = {};
    assertTrue (Arrays.equals(test1, PlaceWrapper.geographicCoordsToString(coords1)));
    assertTrue (Arrays.equals(test2, PlaceWrapper.geographicCoordsToString(coords2)));
  }
}
