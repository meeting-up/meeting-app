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

import java.util.List;

/**
 * DistanceMatrixResponse class holds results from a query.
 */
public class DistanceMatrixResponse {
  /* Each of the following fields are populated with corresponding tag names in json response. */
  public String status;
  public List<String> destination_addresses;
  public List<String> origin_addresses;
  public List<Rows> rows;

  /**
   * Rows subsection of the json.
   */
  public class Rows {
    /* Holds the elements of each row. */
    public List<Elements> elements;

    /**
     * Element class, that is holds each element subsection in each row. 
     */
    public class Elements {
      /* Fields below correspond to section of the element subsection of the json with corresponding names. */
      public Distance distance;
      public Duration duration;
      public String status;

      /**
       * Holds distance subsection of elements. 
       */
      public class Distance {
        public String text;
        public int value;
      }

      /**
       * Holds duration subsection of elements section.
       */
      public class Duration {
        public String text;
        public int value;
      }
    }
  }
}
