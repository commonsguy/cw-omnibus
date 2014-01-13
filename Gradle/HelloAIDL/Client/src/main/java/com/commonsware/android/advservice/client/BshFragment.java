package com.commonsware.android.advservice.client;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.commonsware.android.advservice.IScript;

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
    btn.setEnabled((service!=null));

    return(result);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    setRetainInstance(true);
    getActivity().getApplicationContext()
                 .bindService(new Intent(
                                         "com.commonsware.android.advservice.IScript"),
                              this, Context.BIND_AUTO_CREATE);
  }

  @Override
  public void onDestroy() {
    getActivity().getApplicationContext().unbindService(this);

    super.onDestroy();
  }

  @Override
  public void onClick(View view) {
    EditText script=(EditText)getView().findViewById(R.id.script);
    String src=script.getText().toString();

    try {
      service.executeScript(src);
    }
    catch (RemoteException e) {
      Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG)
           .show();
    }
  }

  @Override
  public void onServiceConnected(ComponentName className, IBinder binder) {
    service=IScript.Stub.asInterface(binder);
    btn.setEnabled(true);
  }

  @Override
  public void onServiceDisconnected(ComponentName className) {
    service=null;
  }
}
