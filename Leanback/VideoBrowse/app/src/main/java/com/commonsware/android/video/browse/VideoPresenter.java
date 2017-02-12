/***
 Copyright (c) 2014-2016 CommonsWare, LLC
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

// inspired by https://github.com/googlesamples/androidtv-Leanback/blob/master/app/src/main/java/com/example/android/tvleanback/CardPresenter.java

package com.commonsware.android.video.browse;

import android.content.ContentUris;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.Presenter;
import android.view.View;
import android.view.ViewGroup;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class VideoPresenter extends Presenter {
  private Context ctxt;

  VideoPresenter(Context ctxt) {
    super();

    this.ctxt=ctxt;
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent) {
    ImageCardView cardView=new ImageCardView(ctxt);

    cardView.setFocusable(true);
    cardView.setFocusableInTouchMode(true);

    return(new Holder(cardView));
  }

  @Override
  public void onBindViewHolder(Presenter.ViewHolder viewHolder,
                               Object item) {
    Video video=(Video)item;
    Holder h=(Holder)viewHolder;
    Resources res=ctxt.getResources();

    h.cardView.setTitleText(video.toString());
    h.cardView.setMainImageDimensions((int)res.getDimension(R.dimen.card_width),
                                      (int)res.getDimension(R.dimen.card_height));

    Uri thumbnailUri=
        ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            video.id);

    h.updateCardViewImage(thumbnailUri);
  }

  @Override
  public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) {
    ((Holder)viewHolder).cardView.setMainImage(null);
  }

  static class Holder extends Presenter.ViewHolder {
    private final ImageCardView cardView;
    private int targetWidth, targetHeight;

    public Holder(View view) {
      super(view);

      cardView=(ImageCardView)view;

      Resources res=view.getContext().getResources();

      targetWidth=(int)res.getDimension(R.dimen.card_width);
      targetHeight=(int)res.getDimension(R.dimen.card_height);
    }

    protected void updateCardViewImage(Uri uri) {
      Picasso.with(cardView.getContext())
        .load(uri)
        .resize(targetWidth, targetHeight)
        .centerCrop()
        .onlyScaleDown()
        .placeholder(R.drawable.ic_media_video_poster)
        .into(new Target() {
          @Override
          public void onBitmapLoaded(Bitmap bitmap,
                                     Picasso.LoadedFrom from) {
            Drawable bmpDrawable=
              new BitmapDrawable(
                cardView.getContext().getResources(),
                bitmap);

            cardView.setMainImage(bmpDrawable);
          }

          @Override
          public void onBitmapFailed(Drawable errorDrawable) {
            cardView.setMainImage(errorDrawable);
          }

          @Override
          public void onPrepareLoad(Drawable placeHolderDrawable) {
            cardView.setMainImage(placeHolderDrawable);
          }
        });
    }
  }
}
