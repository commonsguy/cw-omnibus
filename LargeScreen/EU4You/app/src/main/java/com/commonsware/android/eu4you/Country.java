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
    https://commonsware.com/Android
 */

package com.commonsware.android.eu4you;

import java.util.ArrayList;

public class Country {
  int name;
  int flag;
  int url;
  static ArrayList<Country> EU=new ArrayList<Country>();

  static {
    EU.add(new Country(R.string.austria, R.drawable.austria,
                       R.string.austria_url));
    EU.add(new Country(R.string.belgium, R.drawable.belgium,
                       R.string.belgium_url));
    EU.add(new Country(R.string.bulgaria, R.drawable.bulgaria,
                       R.string.bulgaria_url));
    EU.add(new Country(R.string.cyprus, R.drawable.cyprus,
                       R.string.cyprus_url));
    EU.add(new Country(R.string.czech_republic,
                       R.drawable.czech_republic,
                       R.string.czech_republic_url));
    EU.add(new Country(R.string.denmark, R.drawable.denmark,
                       R.string.denmark_url));
    EU.add(new Country(R.string.estonia, R.drawable.estonia,
                       R.string.estonia_url));
    EU.add(new Country(R.string.finland, R.drawable.finland,
                       R.string.finland_url));
    EU.add(new Country(R.string.france, R.drawable.france,
                       R.string.france_url));
    EU.add(new Country(R.string.germany, R.drawable.germany,
                       R.string.germany_url));
    EU.add(new Country(R.string.greece, R.drawable.greece,
                       R.string.greece_url));
    EU.add(new Country(R.string.hungary, R.drawable.hungary,
                       R.string.hungary_url));
    EU.add(new Country(R.string.ireland, R.drawable.ireland,
                       R.string.ireland_url));
    EU.add(new Country(R.string.italy, R.drawable.italy,
                       R.string.italy_url));
    EU.add(new Country(R.string.latvia, R.drawable.latvia,
                       R.string.latvia_url));
    EU.add(new Country(R.string.lithuania, R.drawable.lithuania,
                       R.string.lithuania_url));
    EU.add(new Country(R.string.luxembourg, R.drawable.luxembourg,
                       R.string.luxembourg_url));
    EU.add(new Country(R.string.malta, R.drawable.malta,
                       R.string.malta_url));
    EU.add(new Country(R.string.netherlands, R.drawable.netherlands,
                       R.string.netherlands_url));
    EU.add(new Country(R.string.poland, R.drawable.poland,
                       R.string.poland_url));
    EU.add(new Country(R.string.portugal, R.drawable.portugal,
                       R.string.portugal_url));
    EU.add(new Country(R.string.romania, R.drawable.romania,
                       R.string.romania_url));
    EU.add(new Country(R.string.slovakia, R.drawable.slovakia,
                       R.string.slovakia_url));
    EU.add(new Country(R.string.slovenia, R.drawable.slovenia,
                       R.string.slovenia_url));
    EU.add(new Country(R.string.spain, R.drawable.spain,
                       R.string.spain_url));
    EU.add(new Country(R.string.sweden, R.drawable.sweden,
                       R.string.sweden_url));
    EU.add(new Country(R.string.united_kingdom,
                       R.drawable.united_kingdom,
                       R.string.united_kingdom_url));
  }

  Country(int name, int flag, int url) {
    this.name=name;
    this.flag=flag;
    this.url=url;
  }
}
