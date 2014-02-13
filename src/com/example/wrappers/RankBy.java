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

/**
 * RankBy class, used to rank results based on the weighting of particular criteria.
 */
public class RankBy {
  private List<PlaceResult> results;
  private PlaceCriteria criteria;
  private double total;
  private List<Double> ranks;

  /**
   * Constructor method sets up required fields. 
   * @param c the PlaceCriteria instance containing weighting information. 
   * @param r The list of results to be ranked. 
   */
  public RankBy (PlaceCriteria c, List<PlaceResult> r) {
    results = r;
    criteria = c;

    ranks = new ArrayList<Double> ();

    total = criteria.rating + criteria.photo + criteria.openNow + 
      criteria.price + criteria.types;
  }

  /** 
   * Go through a list of places and give each a ranking value.
   */
  void setPlaceRank () {
    int hasPhoto;
    int open;

    for (PlaceResult result: results) {
      try {
        hasPhoto = (result.photos.size () != 0)? 1:0;
      } catch (NullPointerException e) {
        hasPhoto = 0;
      }

      try {
        open = (result.opening_hours.open_now == true)? 1:0;
      } catch (NullPointerException e) {
        open = 0;
      }
      double priceRank = 1-(result.price_level/4.0);
      int hasType = hasType (result.types);

      ranks.add((criteria.rating * (result.rating / 5) + open * criteria.openNow + 
                 criteria.photo * hasPhoto + criteria.price * priceRank + 
                 criteria.types * hasType) / total);
    }
  }

  /**
   * Determine whether a place matches any of the types specified in the PlaceCriteria.
   */
  int hasType (String[] types) {
    if (criteria.typesList.size () == 0) {
      return 0;
    }
    for (String type: types) {
      if (criteria.typesList.contains(type)) {
        return 1;
      }
    }
    return 0;
  }

  /**
   * Rank the places and return the top number of results.
   * @param number how many results to return. 
   * If there are less total results than number then return all results. 
   * @returns a list of sorted results of length number.
   */
  public List<PlaceResult> rankPlaces (int number) {
    setPlaceRank ();
    return getBest(number);
  }

  /**
   * Get the best number of results and return them sorted.
   */
  List<PlaceResult> getBest (int number) {
    number=Math.min(number, results.size ());
    List<PlaceResult> bestResults = new ArrayList<PlaceResult> ();
    // A simple and inefficient approach, but get something working.
    double maxRank=0;
    int bestResult=0;

    for (int i=0; i < number; i++) {
      for (int j =0; j < results.size (); j++) {
        if (ranks.get(j) > maxRank) {
          maxRank=ranks.get(j);
          bestResult=j;
        }
      }

      bestResults.add(results.remove(bestResult));
      ranks.remove(bestResult);
      maxRank=0;
      bestResult=0;
    }
    return bestResults;
  }
}
