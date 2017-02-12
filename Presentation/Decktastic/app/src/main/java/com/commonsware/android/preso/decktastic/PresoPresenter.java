/***
 Copyright (c) 2014 CommonsWare, LLC
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

package com.commonsware.android.preso.decktastic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.Presenter;
import android.view.View;
import android.view.ViewGroup;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class PresoPresenter extends Presenter {
  private static final int CARD_WIDTH=400;
  private static final int CARD_HEIGHT=300;

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent) {
    ImageCardView cardView=new ImageCardView(parent.getContext());

    cardView.setFocusable(true);
    cardView.setFocusableInTouchMode(true);

    return(new Holder(cardView));
  }

  @Override
  public void onBindViewHolder(Presenter.ViewHolder viewHolder,
                               Object item) {
    PresoContents preso=(PresoContents)item;
    Holder h=(Holder)viewHolder;

    h.cardView.setTitleText(preso.toString());
    h.cardView.setMainImageDimensions(CARD_WIDTH, CARD_HEIGHT);
    h.updateCardViewImage(preso.getSlideImage(0));
  }

  @Override
  public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) {
    ((Holder)viewHolder).cardView.setMainImage(null);
  }

  @Override
  public void onViewAttachedToWindow(Presenter.ViewHolder viewHolder) {
    // no-op
  }

  static int convertDpToPixel(Context ctxt, int dp) {
    float density=ctxt.getResources().getDisplayMetrics().density;

    return(Math.round((float)dp*density));
  }

  static class Holder extends Presenter.ViewHolder {
    private ImageCardView cardView;
    private PicassoImageCardViewTarget viewTarget;

    public Holder(View view) {
      super(view);

      cardView=(ImageCardView)view;
      viewTarget=new PicassoImageCardViewTarget(cardView);
    }

    protected void updateCardViewImage(String path) {
      Picasso.with(cardView.getContext())
              .load("file:///android_asset/" + path)
              .resize(convertDpToPixel(cardView.getContext(), CARD_WIDTH),
                  convertDpToPixel(cardView.getContext(), CARD_HEIGHT))
              .into(viewTarget);
    }
  }

  private static class PicassoImageCardViewTarget implements Target {
    private ImageCardView imageCardView;

    public PicassoImageCardViewTarget(ImageCardView imageCardView) {
      this.imageCardView=imageCardView;
    }

    @Override
    public void onBitmapLoaded(Bitmap bmp, Picasso.LoadedFrom lf) {
      Drawable bmpDrawable=
          new BitmapDrawable(imageCardView.getContext().getResources(),
                             bmp);

      imageCardView.setMainImage(bmpDrawable);
    }

    @Override
    public void onBitmapFailed(Drawable d) {
      imageCardView.setMainImage(d);
    }

    @Override
    public void onPrepareLoad(Drawable d) {
      imageCardView.setMainImage(d);
    }
  }
}
