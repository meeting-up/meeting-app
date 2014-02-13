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

/**
 * Callbacks that allow a fragment to modify the global state.
 */
public interface ModifyStateCallback {
  /**
   * Save the modified state. The current state will be replaced entirely, so 
   * the state that will become the new state should be fetched first with 
   * getState() and modified after that.
   * 
   * @param state The modified state to be saved. Replaces whatever the current
   * state is. 
   */
  public void putState(State state);
  
  /**
   * Retrieves the current state.
   * 
   * @return The current state.
   */
  public State getState();
  
  /**
   * Replaces the current fragments with those allowing user to input options.
   */
  public void showOptions();
}
