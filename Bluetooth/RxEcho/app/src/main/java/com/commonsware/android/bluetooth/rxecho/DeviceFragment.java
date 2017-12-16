/***
 Copyright (c) 2017 CommonsWare, LLC
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

package com.commonsware.android.bluetooth.rxecho;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.commonsware.android.bluetooth.rxecho.databinding.DeviceBinding;
import com.commonsware.android.bluetooth.rxecho.databinding.TranscriptRowBinding;
import com.github.davidmoten.rx2.Bytes;
import com.github.ivbaranov.rxbluetooth.RxBluetooth;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class DeviceFragment extends Fragment {
  private static final String TAG="RxEcho";
  private static final String ARG_DEVICE="device";
  private final TranscriptAdapter adapter=new TranscriptAdapter();
  private DeviceBinding binding;
  private RxBluetooth rxBluetooth;
  private BluetoothSocket socket;
  private Disposable connectionSub, responseSub;
  private PrintWriter out;

  public static Fragment newInstance(BluetoothDevice device) {
    DeviceFragment result=new DeviceFragment();
    Bundle args=new Bundle();

    args.putParcelable(ARG_DEVICE, device);
    result.setArguments(args);

    return(result);
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setRetainInstance(true);
    rxBluetooth=new RxBluetooth(getActivity().getApplicationContext());
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater,
                           @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
    binding=DeviceBinding.inflate(inflater, container, false);
    binding.setController(this);

    return(binding.getRoot());
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    binding.entry.setOnEditorActionListener((v, actionId, event) -> (send()));
    binding.entry.setEnabled(socket!=null);

    RecyclerView rv=view.findViewById(R.id.transcript);

    rv.setAdapter(adapter);
  }

  @Override
  public void onDestroy() {
    disconnect();

    super.onDestroy();
  }

  public void onConnectionChange() {
    if (binding.connected.isChecked()) {
      binding.connected.setEnabled(false);
      connectionSub=rxBluetooth.observeConnectDevice(getDevice(), ShoutingEchoService.SERVICE_ID)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(this::onConnected, this::onConnectionError);
    }
    else {
      disconnect();
    }
  }

  public boolean send() {
    Single.just(binding.entry.getText().toString())
      .observeOn(Schedulers.io())
      .subscribe(message -> {
        out.print(message);
        out.flush();
      });

    return(true);
  }

  private void onConnected(BluetoothSocket socket) throws IOException {
    binding.connected.setEnabled(true);
    binding.entry.setEnabled(true);
    this.socket=socket;
    out=new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
    responseSub=Bytes.from(socket.getInputStream())
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe(bytes -> post(new String(bytes)),
        throwable -> out.close());
  }

  private void onDisconnected() {
    binding.connected.setEnabled(true);
    binding.entry.setEnabled(false);
  }

  private void disconnect() {
    if (connectionSub!=null) {
      connectionSub.dispose();
    }

    if (responseSub!=null) {
      responseSub.dispose();
    }

    if (socket!=null) {
      try {
        socket.close();
      }
      catch (IOException e) {
        Log.e(TAG, "Exception from Bluetooth", e);
      }
    }

    onDisconnected();
  }

  private BluetoothDevice getDevice() {
    return(getArguments().getParcelable(ARG_DEVICE));
  }

  private void post(String message) {
    binding.entry.setText("");
    adapter.add(message);
  }

  private void onConnectionError(Throwable t) {
    Log.e(TAG, "Exception from Bluetooth", t);
    binding.connected.setChecked(false);
    Toast.makeText(getActivity(), R.string.msg_connect_error, Toast.LENGTH_LONG).show();
  }

  public class TranscriptAdapter extends RecyclerView.Adapter<RowHolder> {
    private ArrayList<Entry> entries=new ArrayList<>();

    @Override
    public RowHolder onCreateViewHolder(ViewGroup parent,
                                        int viewType) {
      return(new RowHolder(TranscriptRowBinding
        .inflate(getLayoutInflater(), parent, false)));
    }

    @Override
    public void onBindViewHolder(RowHolder holder, int position) {
      holder.bind(entries.get(position));
    }

    @Override
    public int getItemCount() {
      return(entries.size());
    }

    void add(String message) {
      entries.add(new Entry(message));
      notifyItemInserted(entries.size()-1);
    }
  }

  class RowHolder extends RecyclerView.ViewHolder {
    private final TranscriptRowBinding binding;

    RowHolder(TranscriptRowBinding binding) {
      super(binding.getRoot());

      this.binding=binding;
    }

    void bind(Entry entry) {
      binding.setTimestamp(DateUtils.formatDateTime(getActivity(),
        entry.timestamp, DateUtils.FORMAT_SHOW_TIME));
      binding.setMessage(entry.message);
      binding.executePendingBindings();
    }
  }

  private static class Entry {
    final String message;
    final long timestamp=System.currentTimeMillis();

    Entry(String message) {
      this.message=message;
    }
  }
}
