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

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.commonsware.android.bluetooth.rxecho.databinding.RosterRowBinding;
import com.github.ivbaranov.rxbluetooth.RxBluetooth;
import java.util.ArrayList;
import java.util.Collection;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import static android.bluetooth.BluetoothAdapter.SCAN_MODE_CONNECTABLE;
import static android.bluetooth.BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE;
import static android.bluetooth.BluetoothAdapter.SCAN_MODE_NONE;

public class RosterFragment extends Fragment {
  interface Contract {
    void showDevice(BluetoothDevice device);
  }

  static final SparseArray<String> SCAN_MODES=new SparseArray<>();

  static {
    SCAN_MODES.put(SCAN_MODE_CONNECTABLE, "SCAN_MODE_CONNECTABLE");
    SCAN_MODES.put(SCAN_MODE_CONNECTABLE_DISCOVERABLE,
      "SCAN_MODE_CONNECTABLE_DISCOVERABLE");
    SCAN_MODES.put(SCAN_MODE_NONE, "SCAN_MODE_NONE");
  }

  private static final int REQUEST_ENABLE_DISCOVERY=4321;
  private final CompositeDisposable subs=new CompositeDisposable();
  private final DevicesAdapter adapter=new DevicesAdapter();
  private MenuItem server, discover, allowDiscovery;
  private RxBluetooth rxBluetooth;
  private boolean isReady=false;
  private RecyclerView rv;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setHasOptionsMenu(true);
    ShoutingEchoService.STATUS.observe(this,
      status -> {
        if (server!=null && status!=null) server.setChecked(status.isRunning);
      });
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater,
                           @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
    return(inflater.inflate(R.layout.roster, container, false));
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    rv=view.findViewById(R.id.devices);

    rv.setLayoutManager(new LinearLayoutManager(getActivity()));
    rv.addItemDecoration(new DividerItemDecoration(getActivity(),
      LinearLayoutManager.VERTICAL));
    rv.setAdapter(adapter);

    rxBluetooth=new RxBluetooth(getActivity().getApplicationContext());

    if (!rxBluetooth.isBluetoothAvailable()) {
      Toast.makeText(getActivity(), R.string.msg_no_bt, Toast.LENGTH_LONG).show();
    }
    else {
      enableBluetooth(false);
    }
  }

  @Override
  public void onDestroy() {
    subs.dispose();
    rxBluetooth.cancelDiscovery();

    super.onDestroy();
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.actions, menu);
    server=menu.findItem(R.id.server);
    discover=menu.findItem(R.id.discover);
    allowDiscovery=menu.findItem(R.id.allow_disco);
    updateMenu();

    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.server:
        if (isServerRunning()) {
          getActivity().stopService(new Intent(getActivity(),
            ShoutingEchoService.class));
        }
        else {
          getActivity().startService(new Intent(getActivity(),
            ShoutingEchoService.class));
        }

        return(true);

      case R.id.discover:
        rxBluetooth.startDiscovery();
        return(true);

      case R.id.allow_disco:
        rxBluetooth.enableDiscoverability(getActivity(), REQUEST_ENABLE_DISCOVERY);
        return(true);
    }

    return(super.onOptionsItemSelected(item));
  }

  void enableBluetooth(boolean didWeAskAlready) {
    if (rxBluetooth.isBluetoothEnabled()) {
      bluetoothReady();
    }
    else if (isThing()) {
      rxBluetooth.enable();

      subs.add(rxBluetooth.observeBluetoothState()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .filter(state -> (BluetoothAdapter.STATE_CONNECTED==state))
        .subscribe(state -> bluetoothReady()));
    }
    else if (didWeAskAlready) {
      Toast.makeText(getActivity(), R.string.msg_away, Toast.LENGTH_LONG).show();
      getActivity().finish();
    }
    else {
      rxBluetooth.enableBluetooth(getActivity(), MainActivity.REQUEST_ENABLE_BLUETOOTH);
    }
  }

  private void bluetoothReady() {
    isReady=true;

    if (isThing()) {
      getActivity().startService(new Intent(getActivity(),
        ShoutingEchoService.class));
      rxBluetooth.enableDiscoverability(getActivity(), REQUEST_ENABLE_DISCOVERY,
        300);
      Toast.makeText(getActivity(), R.string.msg_disco, Toast.LENGTH_LONG).show();
    }
    else {
      updateMenu();
      initAdapter();

      subs.add(rxBluetooth.observeDevices()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(this::addDevice));

      subs.add(rxBluetooth.observeDiscovery()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(s ->
          discover.setEnabled(!BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(s))));
    }
  }

  private void updateMenu() {
    if (isReady && server!=null) {
      server.setEnabled(true);
      server.setChecked(isServerRunning());
      discover.setEnabled(true);
      allowDiscovery.setEnabled(true);
    }
  }

  private void initAdapter() {
    adapter.setItems(rxBluetooth.getBondedDevices());
  }

  private void addDevice(BluetoothDevice device) {
    adapter.addDevice(device);
  }

  private boolean isServerRunning() {
    ShoutingEchoService.Status status=ShoutingEchoService.STATUS.getValue();

    return(status!=null && status.isRunning);
  }

  private boolean isThing() {
    return(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O &&
      (getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_EMBEDDED)));
  }

  private class DevicesAdapter extends RecyclerView.Adapter<RowHolder> {
    private final ArrayList<BluetoothDevice> devices=new ArrayList<>();

    @Override
    public RowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      return(new RowHolder(RosterRowBinding.inflate(getLayoutInflater(), parent, false)));
    }

    @Override
    public void onBindViewHolder(RowHolder holder, int position) {
      holder.bind(devices.get(position));
    }

    @Override
    public int getItemCount() {
      return(devices.size());
    }

    void setItems(Collection<BluetoothDevice> devices) {
      this.devices.clear();

      for (BluetoothDevice device : devices) {
        if (isCandidateDevice(device)) {
          this.devices.add(device);
        }
      }

      notifyDataSetChanged();
    }

    void addDevice(BluetoothDevice device) {
      if (isCandidateDevice(device) && !devices.contains(device)) {
        devices.add(device);
        notifyItemInserted(devices.size()-1);
      }
    }

    boolean isCandidateDevice(BluetoothDevice device) {
      int deviceClass=device.getBluetoothClass().getDeviceClass();

      return(((deviceClass & BluetoothClass.Device.Major.COMPUTER)==
        BluetoothClass.Device.Major.COMPUTER) ||
        ((deviceClass & BluetoothClass.Device.Major.PHONE)==
        BluetoothClass.Device.Major.PHONE) ||
        ((deviceClass & BluetoothClass.Device.Major.AUDIO_VIDEO)==
          BluetoothClass.Device.Major.AUDIO_VIDEO));
    }
  }

  public class RowHolder extends RecyclerView.ViewHolder {
    private final RosterRowBinding binding;

    RowHolder(RosterRowBinding binding) {
      super(binding.getRoot());

      this.binding=binding;
    }

    void bind(BluetoothDevice device) {
      binding.setDevice(device);
      binding.setController(this);
      binding.executePendingBindings();
    }

    public void onClick(BluetoothDevice device) {
      ((Contract)(getActivity())).showDevice(device);
    }
  }
}
