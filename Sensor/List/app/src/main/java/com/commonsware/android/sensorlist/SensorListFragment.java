package com.commonsware.android.sensorlist;

import android.app.ListFragment;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SensorListFragment extends ListFragment implements
    SensorEventListener {
  private SensorManager mgr=null;
  private ArrayAdapter<Sensor> adapter=null;

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    setRetainInstance(true);

    mgr=
        (SensorManager)getActivity().getSystemService(Context.SENSOR_SERVICE);
    adapter=new SensorListAdapter();

    setListAdapter(adapter);
  }

  @Override
  public void onPause() {
    mgr.unregisterListener(this);
    super.onPause();
  }

  @Override
  public void onListItemClick(ListView l, View v, int position, long id) {
    mgr.unregisterListener(this);
    mgr.registerListener(this, adapter.getItem(position),
                         SensorManager.SENSOR_DELAY_NORMAL);
  }

  @Override
  public void onAccuracyChanged(Sensor sensor, int accuracy) {
    // TODO Auto-generated method stub
  }

  @Override
  public void onSensorChanged(SensorEvent event) {
    mgr.unregisterListener(this);

    Toast.makeText(getActivity(),
                   String.format(getActivity().getString(R.string.toast_template),
                                 event.values[0], event.values[1],
                                 event.values[2]), Toast.LENGTH_LONG)
         .show();
  }

  private class SensorListAdapter extends ArrayAdapter<Sensor> {
    SensorListAdapter() {
      super(getActivity(), android.R.layout.simple_list_item_1,
            mgr.getSensorList(Sensor.TYPE_ALL));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      View result=super.getView(position, convertView, parent);

      ((TextView)result).setText(getItem(position).getName());

      return(result);
    }
  }
}
