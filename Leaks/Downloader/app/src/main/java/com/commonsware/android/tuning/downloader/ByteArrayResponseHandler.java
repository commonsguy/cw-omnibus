/***
  Copyright (c) 2010-2011 CommonsWare, LLC
  
  Licensed under the Apache License, Version 2.0 (the "License"); you may
  not use this file except in compliance with the License. You may obtain
  a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/

package com.commonsware.android.tuning.downloader;

import java.io.IOException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.HttpResponseException;
import org.apache.http.util.EntityUtils;

public class ByteArrayResponseHandler implements ResponseHandler<byte[]> {
  public byte[] handleResponse(final HttpResponse response)
                  throws IOException, HttpResponseException {
    StatusLine statusLine=response.getStatusLine();
    
    if (statusLine.getStatusCode()>=300) {
      throw new HttpResponseException(statusLine.getStatusCode(),
                                        statusLine.getReasonPhrase());
    }

    HttpEntity entity=response.getEntity();
    
    if (entity==null) {
      return(null);
    }
    
    return(EntityUtils.toByteArray(entity));
  }
}

