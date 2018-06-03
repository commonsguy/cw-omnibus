/***
  Copyright (c) 2013-2014 CommonsWare, LLC
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

package com.commonsware.android.picasso;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class QuestionsFragment extends Fragment implements
  Callback<SOQuestions> {
  private RVQuestionsAdapter adapter;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setRetainInstance(true);
    adapter=new RVQuestionsAdapter(getLayoutInflater());
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
                           @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.main, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View v,
                            @Nullable Bundle savedInstanceState) {
    super.onViewCreated(v, savedInstanceState);

    RecyclerView rv=v.findViewById(android.R.id.list);

    rv.setLayoutManager(new LinearLayoutManager(getActivity()));
    rv.addItemDecoration(new DividerItemDecoration(getActivity(),
      DividerItemDecoration.VERTICAL));
    rv.setAdapter(adapter);

    Retrofit retrofit=
      new Retrofit.Builder()
        .baseUrl("https://api.stackexchange.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build();
    StackOverflowInterface so=
      retrofit.create(StackOverflowInterface.class);

    so.questions("android").enqueue(this);
  }

  @Override
  public void onResponse(Call<SOQuestions> call,
                         Response<SOQuestions> response) {
    adapter.setQuestions(response.body());
  }

  @Override
  public void onFailure(Call<SOQuestions> call, Throwable t) {
    Toast.makeText(getActivity(), t.getMessage(),
      Toast.LENGTH_LONG).show();
    Log.e(getClass().getSimpleName(),
      "Exception from Retrofit request to StackOverflow", t);
  }

  private static class RVQuestionsAdapter extends RecyclerView.Adapter<RowHolder> {
    private List<Item> items;
    private final LayoutInflater inflater;

    private RVQuestionsAdapter(LayoutInflater inflater) {
      this.inflater=inflater;
    }

    @NonNull
    @Override
    public RowHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                        int viewType) {
      View row=inflater.inflate(R.layout.row, parent, false);

      return new RowHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull RowHolder holder,
                                 int position) {
      holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
      return items==null ? 0 : items.size();
    }

    private void setQuestions(SOQuestions questions) {
      items=questions.items;
      notifyDataSetChanged();
    }
  }

  private static class RowHolder extends RecyclerView.ViewHolder {
    private final TextView title;
    private final ImageView icon;
    private final View row;

    RowHolder(View itemView) {
      super(itemView);
      row=itemView;
      title=itemView.findViewById(R.id.title);
      icon=itemView.findViewById(R.id.icon);
    }

    public void bind(Item item) {
      Picasso.with(icon.getContext())
        .load(item.owner.profileImage)
        .fit()
        .centerCrop()
        .placeholder(R.drawable.owner_placeholder)
        .error(R.drawable.owner_error).into(icon);

      title.setText(Html.fromHtml(item.title));

      row.setOnClickListener(v ->
        icon.getContext()
          .startActivity(new Intent(Intent.ACTION_VIEW,
            Uri.parse(item.link))));
    }
  }
}
