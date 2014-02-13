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

package com.example.meetingapp;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {
  private static final String TAG = MainActivity.class.getSimpleName();
  
  public static final String STATE_INFORMATION = "com.example.meetingapp.STATE";
  public static final String NEXT_PAGE_BUTTON = "Next Page";

  private State mState;
  
  /* Callbacks to allow child fragments to modify the global state, or shared
   * resources such as buttons.
   */
  private final ModifyStateCallback mStateCallback = new ModifyStateCallback() {
    @Override
    public State getState() {
      return mState;
    }
    
    @Override
    public void putState(State state) {
      mState = state;
    }
    
    /**
     * Shows the options, which the user can place input into.
     */
    @Override
    public void showOptions() {
      switchFragment(
          new OptionsFragment(mOptionsCallback, mStateCallback), null);
    }
  };
  
  private final ShowResultsFragment.Callback mShowResultsCallback 
      = new ShowResultsFragment.Callback() {    
    @Override
    public void showResultsOnMap(String tag) {
      switchFragment(
          new MapResultsFragment(mMapResultsCallback, mStateCallback), tag);
    }

    @Override
    public void showResult(LocationParcel selection) {
      switchFragment(new ShowDetailsFragment(mStateCallback), null);
    }    
  };
  
  private final OptionsFragment.Callback mOptionsCallback
      = new OptionsFragment.Callback() {
    @Override
    public void nextPage() {
      switchFragment(
        new ShowResultsFragment(mShowResultsCallback, mStateCallback), null);
    }
    
    @Override
    public void pickLocation() {
      switchFragment(new PickLocationFragment(mStateCallback), null);
    }
    
    @Override
    public void clearBackStack() {
      clearPage();
    }
  };
  
  private final MapResultsFragment.Callback mMapResultsCallback
      = new MapResultsFragment.Callback() {
    @Override
    public void showDetails(LocationParcel result) {
      mState.putResult(result);
      mStateCallback.putState(mState);
      getFragmentManager().popBackStack();
      switchFragment(
          new ShowDetailsFragment(mStateCallback), null);
    }
  };
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    
    mState = new State(this);
    
    switchFragment(new OptionsFragment(mOptionsCallback, mStateCallback), null);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.new_search, menu);
    return true;
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
      // Handle presses on the action bar items
      switch (item.getItemId()) {
          case R.id.action_new_search:
              mState.clear();
              switchFragment(
                  new OptionsFragment(mOptionsCallback, mStateCallback), null);
              
              /* clear it twice as activities may save 
               * their state before being closed
               */
              mState.clear();
              return true;
          default:
              return super.onOptionsItemSelected(item);
      }
  }
  
  public void clearPage() {
    getFragmentManager().popBackStack(
        null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
  }
  
  private void switchFragment(Fragment fragment, String tag) {
    FragmentTransaction transaction = getFragmentManager().beginTransaction();
    
    transaction.replace(R.id.fragment_container, fragment, tag)
      .addToBackStack(null)
      .commit();
  }
}
