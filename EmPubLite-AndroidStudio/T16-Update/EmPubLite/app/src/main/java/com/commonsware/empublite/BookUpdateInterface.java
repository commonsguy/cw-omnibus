package com.commonsware.empublite;

import retrofit2.Call;
import retrofit2.http.GET;

public interface BookUpdateInterface {
  @GET("/misc/empublite-update.json")
  Call<BookUpdateInfo> update();
}