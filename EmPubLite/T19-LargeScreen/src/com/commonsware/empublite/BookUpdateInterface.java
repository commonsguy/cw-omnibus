package com.commonsware.empublite;

import retrofit.http.GET;

public interface BookUpdateInterface {
  @GET("/misc/empublite-update.json")
  BookUpdateInfo update();
}
