/***
  Copyright (c) 2008-2012 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain	a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS,	WITHOUT	WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
	
  From _The Busy Coder's Guide to Android Development_
    http://commonsware.com/Android
*/

package com.commonsware.android.weather2;

class Forecast {
  String time="";
  Integer temp=null;
  String iconUrl="";
  
  String getTime() {
    return(time);
  }

  void setTime(String time) {
    this.time=time.substring(0,16).replace('T', ' ');
  }
  
  Integer getTemp() {
    return(temp);
  }
  
  void setTemp(Integer temp) {
    this.temp=temp;
  }
  
  String getIcon() {
    return(iconUrl);
  }
  
  void setIcon(String iconUrl) {
    this.iconUrl=iconUrl;
  }
}
