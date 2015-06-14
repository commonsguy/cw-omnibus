/***
  Copyright (c) 2008-2012 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
  
  From _The Busy Coder's Guide to Android Development_
    https://commonsware.com/Android
*/

package com.commonsware.android.search;

import android.app.SearchManager;
import android.content.Intent;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import java.util.ArrayList;
import java.util.List;

public class LoremSearch extends LoremBase {
  @Override
  ListAdapter makeMeAnAdapter(Intent intent) {
    ListAdapter adapter=null; 
    
    if (intent.getAction().equals(Intent.ACTION_SEARCH)) {
      String query=intent.getStringExtra(SearchManager.QUERY);
      List<String> results=searchItems(query);
      
      adapter=new ArrayAdapter<String>(this,
                        android.R.layout.simple_list_item_1,
                        results);
      setTitle("LoremSearch for: "+query);
    }
    
    return(adapter);
  }
  
  private List<String> searchItems(String query) {
    LoremSuggestionProvider
      .getBridge(this)
      .saveRecentQuery(query, null);
    
    List<String> results=new ArrayList<String>();
    
    for (String item : items) {
      if (item.indexOf(query)>-1) {
        results.add(item);
      }
    }
    
    return(results);
  }
}