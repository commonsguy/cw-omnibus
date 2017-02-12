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

import android.databinding.BindingAdapter;
import android.databinding.BindingConversion;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import com.commonsware.android.databind.basic.databinding.RowBinding;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.HashMap;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class QuestionsFragment extends RecyclerViewFragment {
  private ArrayList<Question> questions
    =new ArrayList<Question>();
  private HashMap<String, Question> questionMap=
    new HashMap<String, Question>();
  Retrofit retrofit=
    new Retrofit.Builder()
      .baseUrl("https://api.stackexchange.com")
      .addConverterFactory(GsonConverterFactory.create())
      .build();
  StackOverflowInterface so=
    retrofit.create(StackOverflowInterface.class);

  @BindingAdapter({"app:imageUrl", "app:placeholder", "app:error"})
  public static void bindImageView(ImageView iv,
                                   String url,
                                   Drawable placeholder,
                                   Drawable error) {
    Picasso.with(iv.getContext())
      .load(url)
      .fit()
      .centerCrop()
      .placeholder(placeholder)
      .error(error)
      .into(iv);
  }

  @BindingConversion
  public static String ownerToString(Owner owner) {
    return(owner.profileImage);
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setRetainInstance(true);
    setHasOptionsMenu(true);
  }

  @Override
  public void onViewCreated(View view,
                            Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    setLayoutManager(new LinearLayoutManager(getActivity()));

    so.questions("android").enqueue(new Callback<SOQuestions>() {
      @Override
      public void onResponse(Call<SOQuestions> call,
                             Response<SOQuestions> response) {
        for (Item item : response.body().items) {
          Question question=new Question(item);

          questions.add(question);
          questionMap.put(question.getId(), question);
        }

        setAdapter(new QuestionsAdapter(questions));
      }

      @Override
      public void onFailure(Call<SOQuestions> call, Throwable t) {
        onError(t);
      }
    });
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
      idList.add(question.getId());
    }

    String ids=TextUtils.join(";", idList);

    so.update(ids).enqueue(new Callback<SOQuestions>() {
      @Override
      public void onResponse(Call<SOQuestions> call,
                             Response<SOQuestions> response) {
        for (Item item : response.body().items) {
          Question question=questionMap.get(item.id);

          if (question!=null) {
            question.updateFromItem(item);
          }
        }
      }

      @Override
      public void onFailure(Call<SOQuestions> call, Throwable t) {
        onError(t);
      }
    });
  }

  private void onError(Throwable error) {
    Toast.makeText(getActivity(), error.getMessage(),
      Toast.LENGTH_LONG).show();

    Log.e(getClass().getSimpleName(),
      "Exception from Retrofit request to StackOverflow",
      error);
  }

  class QuestionsAdapter
    extends RecyclerView.Adapter<QuestionController> {
    private final ArrayList<Question> questions;

    QuestionsAdapter(ArrayList<Question> questions) {
      this.questions=questions;
    }

    @Override
    public QuestionController onCreateViewHolder(ViewGroup parent,
                                                 int viewType) {
      RowBinding rowBinding=
        RowBinding.inflate(getActivity().getLayoutInflater(),
          parent, false);

      return(new QuestionController(rowBinding, this));
    }

    @Override
    public void onBindViewHolder(QuestionController holder,
                                 int position) {
      holder.bindModel(getItem(position));
    }

    @Override
    public int getItemCount() {
      return(questions.size());
    }

    Question getItem(int position) {
      return(questions.get(position));
    }
  }
}
