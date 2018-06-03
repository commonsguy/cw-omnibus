/***
 Copyright (c) 2017 CommonsWare, LLC
 Licensed under the Apache License, Version 2.0 (the "License"); you may not
 use this file except in compliance with the License. You may obtain a copy
 of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 by applicable law or agreed to in writing, software distributed under the
 License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 OF ANY KIND, either express or implied. See the License for the specific
 language governing permissions and limitations under the License.

 Covered in detail in the book _The Busy Coder's Guide to Android Development_
 https://commonsware.com/Android
 */

package com.commonsware.android.slice.weather;

import android.graphics.Bitmap;
import java.util.List;

public class WeatherResponse {
  public final Properties properties=null;

  public static class Properties {
    public final List<Period> periods=null;
  }

  public static class Period {
    public final String startTime=null;
    public final int temperature;
    public final String temperatureUnit=null;
    public final String icon=null;
    public Bitmap iconBitmap;

    public Period() {
      temperature=0;
    }
  }
}
