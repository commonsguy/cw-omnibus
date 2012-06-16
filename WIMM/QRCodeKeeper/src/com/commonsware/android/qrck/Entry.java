/***
  Copyright (c) 2012 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
  
  From _The Busy Coder's Guide to Advanced Android Development_
    http://commonsware.com/AdvAndroid
*/

package com.commonsware.android.qrck;

import java.math.BigInteger;
import java.security.MessageDigest;

public class Entry {
  private String name=null;
  private String url=null;
  private String filename=null;
  
  Entry(String name, String url) {
    this.name=name;
    this.url=url;
  }
  
  @Override
  public String toString() {
    return(name);
  }
  
  String getUrl() {
    return(url);
  }
  
  String getFilename() throws Exception {
    if (filename==null) {
      byte[] bytesOfMessage=url.getBytes("UTF-8");
  
      MessageDigest md=MessageDigest.getInstance("MD5");
      byte[] thedigest=md.digest(bytesOfMessage);
      String md5=new BigInteger(1, thedigest).toString(16);
      
      filename=md5+".png";
    }
    
    return(filename);
  }
}
