package com.commonsware.android.advservice.binding;

import android.app.Activity;
import android.app.Application;
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

public class DownloadFragment extends Fragment implements
    OnClickListener, ServiceConnection {
  private static final String TO_DOWNLOAD="https://commonsware.com/Android/excerpt.pdf";
  private IDownload binding=null;
  private Button btn=null;
  private Application appContext=null;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setRetainInstance(true);

    appContext=(Application)getActivity().getApplicationContext();
    appContext.bindService(new Intent(getActivity(),
        DownloadService.class),
      this, Context.BIND_AUTO_CREATE);
  }

  @Override
  public View onCreateView(LayoutInflater inflater,
                           ViewGroup container,
                           Bundle savedInstanceState) {
    View result=inflater.inflate(R.layout.main, container, false);

    btn=(Button)result.findViewById(R.id.go);
    btn.setOnClickListener(this);
    btn.setEnabled(binding!=null);

    return(result);
  }

  @Override
  public void onDestroy() {
    appContext.unbindService(this);
    disconnect();

    super.onDestroy();
  }

  @Override
  public void onClick(View view) {
    binding.download(TO_DOWNLOAD);
  }

  @Override
  public void onServiceConnected(ComponentName className, IBinder binder) {
    binding=(IDownload)binder;
    btn.setEnabled(true);
  }

  @Override
  public void onServiceDisconnected(ComponentName className) {
    disconnect();
  }

  private void disconnect() {
    binding=null;
    btn.setEnabled(false);
  }
}
