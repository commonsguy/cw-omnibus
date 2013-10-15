/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.maps.android.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * RotationLayout rotates the contents of the layout by multiples of 90 degrees.
 *
 * May not work with padding.
 */
class RotationLayout extends RelativeLayout {
    private int mRotation;

    public RotationLayout(Context context) {
        super(context);
    }

    public RotationLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RotationLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mRotation == 1 || mRotation == 3) {
            super.onMeasure(heightMeasureSpec, widthMeasureSpec);
            setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    /**
     * @param degrees the rotation, in degrees.
     */
    public void setViewRotation(int degrees) {
        mRotation = ((degrees + 360) % 360) / 90;
    }


    @Override
    public void dispatchDraw(Canvas canvas) {
        if (mRotation == 0) {
            super.dispatchDraw(canvas);
            return;
        }

        if (mRotation == 1) {
            canvas.translate(getWidth(), 0);
            canvas.rotate(90);
        } else if (mRotation == 2) {
            canvas.rotate(180, getWidth() / 2, getHeight() / 2);
        } else {
            canvas.translate(0, getHeight());
            canvas.rotate(270);
        }

        super.dispatchDraw(canvas);
    }
}
