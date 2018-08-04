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

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.slice.Slice
import androidx.slice.SliceItem
import androidx.slice.widget.SliceLiveData
import de.blox.treeview.BaseTreeAdapter
import de.blox.treeview.TreeNode
import de.blox.treeview.TreeView

class InspectorFragment : Fragment() {
  private lateinit var tree: TreeView

  override fun onCreateView(inflater: LayoutInflater,
                            container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    tree = TreeView(activity).apply {
      lineColor = ResourcesCompat.getColor(activity!!.resources, android.R.color.black, null)
      levelSeparation = 60
    }

    SliceLiveData
        .fromUri(activity!!, Uri.parse(BuildConfig.SLICE_URI))
        .observe(this, Observer<Slice> { this.onSlice(it) })

    return tree
  }

  private fun onSlice(slice: Slice) {
    val adapter = SliceTreeAdapter(activity!!)
    val root = buildTreeNode(null)

    slice.items.forEach { root.addChild(buildTreeNode(it)) }

    adapter.setRootNode(root)
    tree.adapter = adapter
  }

  private fun buildTreeNode(sliceItem: SliceItem?): TreeNode {
    val result = TreeNode(sliceItem)

    if (android.app.slice.SliceItem.FORMAT_SLICE == sliceItem?.format) {
      sliceItem.slice.items.forEach { result.addChild(buildTreeNode(it)) }
    }

    return result
  }

  internal class SliceTreeAdapter(context: Context) : BaseTreeAdapter<SliceItemViewHolder>(context, R.layout.tree_node) {
    override fun onCreateViewHolder(view: View): SliceItemViewHolder = SliceItemViewHolder(view)

    override fun onBindViewHolder(viewHolder: SliceItemViewHolder, data: Any?,
                                  position: Int) {
      viewHolder.bind(data as SliceItem?)
    }
  }

  internal class SliceItemViewHolder(card: View) {
    val title: TextView = card.findViewById(R.id.title)

    fun bind(sliceItem: SliceItem?) {
      title.text = sliceItem?.format ?: "slice"
    }
  }
}
