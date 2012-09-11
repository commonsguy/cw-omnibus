package com.commonsware.android.anim.ftrans;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class ButtonFragment extends Fragment implements OnClickListener {
  private static final String KEY_INDEX="index";

  public static ButtonFragment newInstance(int index) {
    Bundle args=new Bundle();

    args.putInt(KEY_INDEX, index);

    ButtonFragment result=new ButtonFragment();

    result.setArguments(args);

    return(result);
  }

  public ButtonFragment() {
    super();
  }

  @Override
  public View onCreateView(LayoutInflater inflater,
                           ViewGroup container,
                           Bundle savedInstanceState) {
    Button result=
        (Button)inflater.inflate(R.layout.button, container, false);

    result.setText("Button #" + getArguments().getInt(KEY_INDEX));
    result.setOnClickListener(this);

    return(result);
  }

  @Override
  public void onClick(View arg0) {
    ((MainActivity)getActivity()).onClick();
  }
}
