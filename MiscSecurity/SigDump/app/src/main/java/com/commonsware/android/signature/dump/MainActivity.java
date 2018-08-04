/***
 Copyright (c) 2013-2018 CommonsWare, LLC
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

package com.commonsware.android.signature.dump;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableField;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.commonsware.android.signature.dump.databinding.ActivityMainBinding;
import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class MainActivity extends FragmentActivity {
  private static final DateFormat FORMAT=
    DateFormat.getDateInstance();
  private final DetailModel detailModel=new DetailModel();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    ActivityMainBinding binding=
      DataBindingUtil.setContentView(this, R.layout.activity_main);

    binding.setModel(detailModel);

    binding.packages.setLayoutManager(new LinearLayoutManager(this));
    binding.packages.addItemDecoration(new DividerItemDecoration(this,
      LinearLayoutManager.VERTICAL));
    binding.packages.setAdapter(new PackageAdapter(getLayoutInflater(),
      buildPackageList(), detailModel));
  }

  public List<PackageInfo> buildPackageList() {
    List<PackageInfo> result=
      getPackageManager().getInstalledPackages(PackageManager.GET_SIGNATURES);

    Collections.sort(result, (a, b) -> (a.packageName.compareTo(b.packageName)));

    return result;
  }

  private static class PackageAdapter extends RecyclerView.Adapter<RowHolder> {
    private final LayoutInflater inflater;
    private final List<PackageInfo> packages;
    private final DetailModel detailModel;

    private PackageAdapter(LayoutInflater inflater,
                           List<PackageInfo> packages,
                           DetailModel detailModel) {
      this.inflater=inflater;
      this.packages=packages;
      this.detailModel=detailModel;
    }

    @NonNull
    @Override
    public RowHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                        int viewType) {
      View row=
        inflater.inflate(android.R.layout.simple_list_item_1, parent, false);

      return new RowHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull RowHolder holder,
                                 int position) {
      holder.bind(packages.get(position), detailModel);
    }

    @Override
    public int getItemCount() {
      return packages.size();
    }
  }

  private static class RowHolder extends RecyclerView.ViewHolder {
    private final TextView title;
    private final View row;

    RowHolder(View itemView) {
      super(itemView);

      row=itemView;
      title=itemView.findViewById(android.R.id.text1);
    }

    void bind(final PackageInfo packageInfo, final DetailModel detailModel) {
      title.setText(packageInfo.packageName);

      row.setOnClickListener(v -> {
        detailModel.selected.set(packageInfo.packageName);
        detailModel.sigModel.set(new SigModel(buildCertificate(packageInfo)));
        SigSaver.enqueueWork(title.getContext(), packageInfo);
      });
    }

    private X509Certificate buildCertificate(PackageInfo packageInfo) {
      Signature[] signatures=packageInfo.signatures;
      byte[] raw=signatures[0].toByteArray();
      CertificateFactory certFactory;

      try {
        certFactory=CertificateFactory.getInstance("X509");
      }
      catch (CertificateException e) {
        Log.e(getClass().getSimpleName(),
          "Exception getting CertificateFactory", e);
        return null;
      }

      X509Certificate cert;
      ByteArrayInputStream bin=new ByteArrayInputStream(raw);

      try {
        cert=(X509Certificate)certFactory.generateCertificate(bin);
      }
      catch (CertificateException e) {
        Log.e(getClass().getSimpleName(),
          "Exception getting X509Certificate", e);
        return null;
      }

      return cert;
    }
  }

  public static class DetailModel {
    public final ObservableField<String> selected=new ObservableField<>();
    public final ObservableField<SigModel> sigModel=new ObservableField<>();
  }

  public static class SigModel {
    public final String subject;
    public final String issuer;
    public final String validDates;

    private SigModel(X509Certificate cert) {
      this.subject=cert.getSubjectDN().toString();
      this.issuer=cert.getIssuerDN().toString();
      this.validDates=
        FORMAT.format(cert.getNotBefore())+" to "+
          FORMAT.format(cert.getNotAfter());
    }
  }
}
