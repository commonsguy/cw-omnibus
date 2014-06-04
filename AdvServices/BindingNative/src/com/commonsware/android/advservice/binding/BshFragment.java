package com.commonsware.android.advservice.binding;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class BshFragment extends Fragment implements OnClickListener,
    ServiceConnection {
  private IScript service=null;
  private Button btn=null;

  public View onCreateView(LayoutInflater inflater,
                           ViewGroup container,
                           Bundle savedInstanceState) {
    View result=inflater.inflate(R.layout.main, container, false);

    btn=(Button)result.findViewById(R.id.eval);
    btn.setOnClickListener(this);
    btn.setEnabled(service != null);

    setRetainInstance(true);

    return(result);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    getActivity().getApplicationContext()
                 .bindService(new Intent(getActivity(),
                                         BshService.class), this,
                              Context.BIND_AUTO_CREATE);
  }

  @Override
  public void onDestroy() {
    getActivity().getApplicationContext().unbindService(this);
    disconnect();

    super.onDestroy();
  }

  @Override
  public void onClick(View view) {
    EditText script=(EditText)getView().findViewById(R.id.script);
    String src=script.getText().toString();

    service.executeScript(src);
  }

  @Override
  public void onServiceConnected(ComponentName className, IBinder binder) {
    service=(IScript)binder;
    btn.setEnabled(true);
  }

  @Override
  public void onServiceDisconnected(ComponentName className) {
    disconnect();
  }

  private void disconnect() {
    service=null;
    btn.setEnabled(false);
  }
}
