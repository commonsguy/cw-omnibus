/***
  Copyright (c) 2013-2015 CommonsWare, LLC
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

package com.commonsware.android.databind.basic;

import android.app.ListFragment;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import com.commonsware.android.databind.basic.databinding.RowBinding;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class QuestionsFragment extends ListFragment {
  private ArrayList<Question> questions
    =new ArrayList<Question>();
  private HashMap<String, Question> questionMap=
    new HashMap<String, Question>();
  RestAdapter restAdapter=
    new RestAdapter.Builder().setEndpoint("https://api.stackexchange.com")
      .build();
  StackOverflowInterface so=
    restAdapter.create(StackOverflowInterface.class);

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setRetainInstance(true);
    setHasOptionsMenu(true);
  }

  @Override
  public View onCreateView(LayoutInflater inflater,
                           ViewGroup container,
                           Bundle savedInstanceState) {
    View result=
        super.onCreateView(inflater, container,
          savedInstanceState);

    so.questions("android", new Callback<SOQuestions>() {
      @Override
      public void success(SOQuestions results,
                          Response response) {
        for (Item item : results.items) {
          Question question=new Question(item);

          questions.add(question);
          questionMap.put(question.id, question);
        }

        setListAdapter(new QuestionsAdapter(questions));
      }

      @Override
      public void failure(RetrofitError error) {
        onError(error);
      }
    });

    return(result);
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

  @Override
  public void onListItemClick(ListView l, View v, int position, long id) {
    Question question=
      ((QuestionsAdapter)getListAdapter()).getItem(position);

    EventBus.getDefault().post(new QuestionClickedEvent(question));
  }

  private void updateQuestions() {
    ArrayList<String> idList=new ArrayList<String>();

    for (Question question : questions) {
      idList.add(question.id);
    }

    String ids=TextUtils.join(";", idList);

    so.update(ids, new Callback<SOQuestions>() {
      @Override
      public void success(SOQuestions soQuestions,
                          Response response) {
        for (Item item : soQuestions.items) {
          Question question=questionMap.get(item.id);

          if (question!=null) {
            question.updateFromItem(item);
          }
        }
      }

      @Override
      public void failure(RetrofitError error) {
        onError(error);
      }
    });
  }

  private void onError(RetrofitError error) {
    Toast.makeText(getActivity(), error.getMessage(),
      Toast.LENGTH_LONG).show();

    Log.e(getClass().getSimpleName(),
      "Exception from Retrofit request to StackOverflow",
      error);
  }

  class QuestionsAdapter extends ArrayAdapter<Question> {
    QuestionsAdapter(List<Question> items) {
      super(getActivity(), R.layout.row, R.id.title, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      RowBinding rowBinding=
        DataBindingUtil.getBinding(convertView);

      if (rowBinding==null) {
        rowBinding=
          RowBinding.inflate(getActivity().getLayoutInflater(),
            parent, false);
      }

      Question question=getItem(position);
      ImageView icon=rowBinding.icon;

      rowBinding.setQuestion(question);

      Picasso.with(getActivity()).load(question.owner.profileImage)
             .fit().centerCrop()
             .placeholder(R.drawable.owner_placeholder)
             .error(R.drawable.owner_error).into(icon);

      return(rowBinding.getRoot());
    }
  }
}
