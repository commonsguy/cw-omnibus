/***
 Copyright (c) 2013-2015 CommonsWare, LLC
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

package com.commonsware.android.databind.basic;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.commonsware.android.databind.basic.databinding.RowBinding;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class QuestionsFragment extends Fragment {
  private ArrayList<Question> questions
    =new ArrayList<Question>();
  private HashMap<String, Question> questionMap=
    new HashMap<String, Question>();
  Retrofit retrofit=
    new Retrofit.Builder()
      .baseUrl("https://api.stackexchange.com")
      .addConverterFactory(GsonConverterFactory.create())
      .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
      .build();
  StackOverflowInterface so=
    retrofit.create(StackOverflowInterface.class);
  private RVQuestionsAdapter adapter;
  private Disposable sub;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setRetainInstance(true);
    setHasOptionsMenu(true);
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
    RecyclerView rv=v.findViewById(android.R.id.list);

    rv.setLayoutManager(new LinearLayoutManager(getActivity()));
    rv.addItemDecoration(new DividerItemDecoration(getActivity(),
      DividerItemDecoration.VERTICAL));
    rv.setAdapter(adapter);

    sub=so.questions("android")
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe(soQuestions -> {
          for (Item item : soQuestions.items) {
            Question question=new Question(item);

            questions.add(question);
            questionMap.put(question.id, question);
          }

          adapter.setQuestions(questions);
        }, t -> {
          Toast.makeText(getActivity(), t.getMessage(),
            Toast.LENGTH_LONG).show();
          Log.e(getClass().getSimpleName(),
            "Exception from Retrofit request to StackOverflow", t);
        }
      );
  }

  @Override
  public void onDestroy() {
    unsub();

    super.onDestroy();
  }

  @Override
  public void onCreateOptionsMenu(Menu menu,
                                  MenuInflater inflater) {
    inflater.inflate(R.menu.actions, menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId()==R.id.refresh) {
      updateQuestions();
    }

    return(super.onOptionsItemSelected(item));
  }

  private void updateQuestions() {
    ArrayList<String> idList=new ArrayList<String>();

    for (Question question : questions) {
      idList.add(question.id);
    }

    String ids=TextUtils.join(";", idList);

    unsub();
    sub=so.update(ids)
      .observeOn(AndroidSchedulers.mainThread())
      .subscribeOn(Schedulers.io())
      .subscribe(result -> {
        for (Item item : result.items) {
          Question question=questionMap.get(item.id);

          if (question!=null) {
            question.updateFromItem(item);
          }
        }
      }, t -> {
        Toast.makeText(getActivity(), t.getMessage(),
          Toast.LENGTH_LONG).show();
        Log.e(getClass().getSimpleName(),
          "Exception from Retrofit request to StackOverflow", t);
      });
  }

  private void unsub() {
    if (sub!=null && !sub.isDisposed()) {
      sub.dispose();
    }
  }

  private static class RVQuestionsAdapter extends RecyclerView.Adapter<RowHolder> {
    private List<Question> questions;
    private final LayoutInflater inflater;

    private RVQuestionsAdapter(LayoutInflater inflater) {
      this.inflater=inflater;
    }

    @NonNull
    @Override
    public RowHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                        int viewType) {
      RowBinding rowBinding=RowBinding.inflate(inflater, parent, false);

      return new RowHolder(rowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RowHolder holder,
                                 int position) {
      holder.bind(questions.get(position));
    }

    @Override
    public int getItemCount() {
      return questions==null ? 0 : questions.size();
    }

    private void setQuestions(List<Question> questions) {
      this.questions=questions;
      notifyDataSetChanged();
    }
  }

  private static class RowHolder extends RecyclerView.ViewHolder {
    private final RowBinding binding;

    RowHolder(RowBinding binding) {
      super(binding.getRoot());
      this.binding=binding;
    }

    public void bind(Question question) {
      Picasso.with(binding.icon.getContext())
        .load(question.owner.profileImage)
        .fit()
        .centerCrop()
        .placeholder(R.drawable.owner_placeholder)
        .error(R.drawable.owner_error).into(binding.icon);

      binding.setQuestion(question);
      binding.getRoot().setOnClickListener(v ->
        binding.icon.getContext()
          .startActivity(new Intent(Intent.ACTION_VIEW,
            Uri.parse(question.link))));
      binding.executePendingBindings();
    }
  }
}
