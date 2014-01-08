/***
  Copyright (c) 2013 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
  
  From _The Busy Coder's Guide to Android Development_
    http://commonsware.com/Android
 */

package com.commonsware.android.retrofit;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

public interface StackOverflowInterface
{
	/**
	 * Each method in the interface should have an annotation identifying the HTTP
operation to perform, such as @GET or @POST. The parameter to the annotation is the
path for the request and any fixed query parameters. In our case, we are using the
path documented by StackExchange for retrieving questions (/2.1/questions), plus
some fixed query parameters:
• order for whether the results should be ascending (asc) or descending
(desc)
• sort to indicate how the questions should be sorted, such as creation to
sort by time when the question was posted
• site to indicate what StackExchange site we are querying (e.g.,
stackoverflow)
The method name can be whatever you want.
If you have additional query parameters that vary dynamically, you can use the
@Query annotation on String parameters to have them be added to the end of the
URL. In our case, the tagged query parameter will be added with whatever the tags
parameter is to our question() method.
Similarly, you can use {name} placeholders for path segments, and replace those at
runtime via @Path-annotated parameters to the method.
	 * @param tags
	 * @param cb
	 */
	@GET("/2.1/questions?order=desc&sort=creation&site=stackoverflow")
	void questions(@Query("tagged") String tags, Callback<SOQuestions> cb);
}
