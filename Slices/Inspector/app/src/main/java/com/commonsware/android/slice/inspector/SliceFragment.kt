/*
Copyright (c) 2018 CommonsWare, LLC
Licensed under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License. You may obtain	a copy
of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
by applicable law or agreed to in writing, software distributed under the
License is distributed on an "AS IS" BASIS,	WITHOUT	WARRANTIES OR CONDITIONS
OF ANY KIND, either express or implied. See the License for the specific
language governing permissions and limitations under the License.

Covered in detail in the book _The Busy Coder's Guide to Android Development_
https://commonsware.com/Android
 */

package com.commonsware.android.slice.inspector

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.slice.widget.SliceLiveData
import androidx.slice.widget.SliceView

private const val ARG_MODE = "mode"

class SliceFragment : Fragment() {
  companion object {
    fun newInstance(mode: Int) =
        SliceFragment().apply {
          arguments = Bundle().apply { putInt(ARG_MODE, mode) }
        }
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View? {
    val result = inflater.inflate(R.layout.fragment_slice, container, false)
    val sliceView = result.findViewById<SliceView>(R.id.slice)

    sliceView.mode = arguments?.getInt(ARG_MODE, SliceView.MODE_LARGE) ?: SliceView.MODE_LARGE

    SliceLiveData
        .fromUri(activity!!, Uri.parse(BuildConfig.SLICE_URI))
        .observe(this, sliceView)

    return result
  }
}