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
 * PlaceAutocompletePrediction, holds the results from the query.
 */
public class PlaceAutocompletePrediction {
  // The following fields are populated with the corresponding json label name's value. 
  public String description;
  public String id;
  public String reference;
  public String[]types;
  public List<Term> terms;
  public List<MatchedSubstring> matched_substrings;

  /**
   * Term class, holds term subsection of json.
   */
  public class Term {
    // Below fields are populated with corresponding json.
    public String value;
    public int offset;
  }

  /**
   * MatchedSubstring class used to hold the matched_substrings subsection of json.
   */
  public class MatchedSubstring {
    // Below fields populated with corresponding json 
    public int length;
    public int offset;
  }
}
