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
import java.util.ArrayList;

/** PlaceCriteria class used for holding information as to the importance of each criteria.
 * Gives each criteria a weighting and this information is used by the RankBy class.
 */
public class PlaceCriteria {
  // The below fields hold the weighting for the particular criteria.
  public int openNow;
  public int photo;
  public int price;
  public int rating;
  public int types;
  public List<String> typesList = new ArrayList<String>(); // List of types to compare against

  /** 
   * Set the weighting of the rating criteria. 
   * @param rating the weight to give criteria rating.
   * @return the PlaceCriteria instance.
   */
  public PlaceCriteria setRating (int rating) {
    if (rating < 0 || rating > 5) {
      throw new IllegalArgumentException ("Invalid rating weighting");
    }
    this.rating = rating;
    return this;
  }

  /** 
   * Set the weighting of the photo criteria. 
   * @param photo the weight to give criteria photo.
   * @return the PlaceCriteria instance.
   */
  public PlaceCriteria setPhoto (int photo) {
    if (photo < 0 || photo > 5) {
      throw new IllegalArgumentException ("Invalid photo weighting");
    }
    this.photo = photo;
    return this;
  }

  /** 
   * Set the weighting of the price criteria. 
   * @param price the weight to give criteria price.
   * @return the PlaceCriteria instance.
   */
  public PlaceCriteria setPrice (int price) {
    if (price < 0 || price > 5) {
      throw new IllegalArgumentException ("Invalid price weighting");
    }
    this.price = price;
    return this;
  }

  /** 
   * Set the weighting of the open now criteria. 
   * @param openNow the weight to give criteria open now.
   * @return the PlaceCriteria instance.
   */
  public PlaceCriteria setOpenNow (int openNow) {
    if (openNow < 0 || openNow > 5) {
      throw new IllegalArgumentException ("Invalid open now weighting");
    }
    this.openNow = openNow;
    return this;
  }

  /** 
   * Set the weighting of the types criteria. 
   * @param types the weight to give criteria types.
   * @param typesList The list of types to be compared against.
   * @return the PlaceCriteria instance.
   */
  public PlaceCriteria setTypes (int types, String... typesList) {
    if (types < 0 || types > 5) {
      throw new IllegalArgumentException ("Invalid types weighting");
    }
    this.types=types;

    for (String type: typesList) 
      this.typesList.add(type);
    return this;
  }
}
