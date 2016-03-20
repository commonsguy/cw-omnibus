/*
 * Copyright (c) 2016 CommonsWare, LLC
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

package info.guardianproject.netcipher.hurl;

import android.content.Intent;

/**
 * Callback interface used for reporting Orbot status
 */
public interface StatusCallback {
  /**
   * Called when Orbot is operational
   *
   * @param statusIntent an Intent containing information about
   *                     Orbot, including proxy ports
   */
  void onEnabled(Intent statusIntent);

  /**
   * Called when Orbot reports that it is starting up
   */
  void onStarting();

  /**
   * Called when Orbot reports that it is shutting down
   */
  void onStopping();

  /**
   * Called when Orbot reports that it is no longer running
   */
  void onDisabled();

  /**
   * Called if our attempt to get a status from Orbot failed
   * after a defined period of time. See statusTimeout() on
   * OrbotInitializer.
   */
  void onStatusTimeout();

  /**
   * Called if Orbot is not yet installed. Usually, you handle
   * this by checking the return value from init() on OrbotInitializer
   * or calling isInstalled() on OrbotInitializer. However, if
   * you have need for it, if a callback is registered before
   * an init() call determines that Orbot is not installed, your
   * callback will be called with onNotYetInstalled().
   */
  void onNotYetInstalled();
}
