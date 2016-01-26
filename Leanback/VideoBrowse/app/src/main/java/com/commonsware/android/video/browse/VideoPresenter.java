/***
 Copyright (c) 2014 CommonsWare, LLC
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
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class VideoPresenter extends Presenter {
  private Context ctxt;
  private ImageLoader imageLoader;

  VideoPresenter(Context ctxt) {
    super();

    this.ctxt=ctxt;

    ImageLoaderConfiguration ilConfig=
        new ImageLoaderConfiguration.Builder(ctxt).build();

    imageLoader=ImageLoader.getInstance();
    imageLoader.init(ilConfig);
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent) {
    ImageCardView cardView=new ImageCardView(ctxt);

    cardView.setFocusable(true);
    cardView.setFocusableInTouchMode(true);

    return(new Holder(cardView, imageLoader));
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

    h.updateCardViewImage(thumbnailUri.toString());
  }

  @Override
  public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) {
    ((Holder)viewHolder).cardView.setMainImage(null);
  }

  static class Holder extends Presenter.ViewHolder {
    private final ImageLoader imageLoader;
    private final ImageCardView cardView;
    private final ImageSize targetSize;

    public Holder(View view, ImageLoader imageLoader) {
      super(view);

      cardView=(ImageCardView)view;
      this.imageLoader=imageLoader;

      Resources res=view.getContext().getResources();

      targetSize=
          new ImageSize((int)res.getDimension(R.dimen.card_width),
                        (int)res.getDimension(R.dimen.card_height));
    }

    protected void updateCardViewImage(String path) {
      DisplayImageOptions opts=new DisplayImageOptions.Builder()
          .showImageOnLoading(R.drawable.ic_media_video_poster)
          .build();

      imageLoader.loadImage(path, targetSize, opts,
                            new SimpleImageLoadingListener() {
        @Override
        public void onLoadingComplete(String uri, View v, Bitmap bmp) {
          Drawable bmpDrawable=
              new BitmapDrawable(cardView.getContext().getResources(),
                                 bmp);

          cardView.setMainImage(bmpDrawable);
        }
      });
    }
  }
}
