/***
 Copyright (c) 2015 CommonsWare, LLC
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

package com.commonsware.android.debug.util;

import android.app.Activity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.widget.TextView;
import com.commonsware.android.debug.activity.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.StatsSnapshot;

public class PicassoDiagnosticActivity extends Activity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    StatsSnapshot ss=Picasso.with(this).getSnapshot();

    TextView tv=findViewById(R.id.last_updated_value);

    tv.setText(DateUtils.formatDateTime(this, ss.timeStamp,
                                        DateUtils.FORMAT_SHOW_TIME));

    tv=findViewById(R.id.avg_download_size_value);
    tv.setText(Long.toString(ss.averageDownloadSize));

    tv=findViewById(R.id.avg_orig_size_value);
    tv.setText(Long.toString(ss.averageOriginalBitmapSize));

    tv=findViewById(R.id.avg_xform_size_value);
    tv.setText(Long.toString(ss.averageTransformedBitmapSize));

    tv=findViewById(R.id.cache_hits_value);
    tv.setText(Long.toString(ss.cacheHits));

    tv=findViewById(R.id.cache_misses_value);
    tv.setText(Long.toString(ss.cacheMisses));

    tv=findViewById(R.id.download_count_value);
    tv.setText(Long.toString(ss.downloadCount));

    tv=findViewById(R.id.max_size_value);
    tv.setText(Long.toString(ss.maxSize));

    tv=findViewById(R.id.orig_bitmap_count_value);
    tv.setText(Long.toString(ss.originalBitmapCount));

    tv=findViewById(R.id.size_value);
    tv.setText(Long.toString(ss.size));

    tv=findViewById(R.id.total_dl_size_value);
    tv.setText(Long.toString(ss.totalDownloadSize));

    tv=findViewById(R.id.total_orig_size_value);
    tv.setText(Long.toString(ss.totalOriginalBitmapSize));

    tv=findViewById(R.id.total_xform_size_value);
    tv.setText(Long.toString(ss.totalTransformedBitmapSize));

    tv=findViewById(R.id.xform_count_value);
    tv.setText(Long.toString(ss.transformedBitmapCount));
  }
}
