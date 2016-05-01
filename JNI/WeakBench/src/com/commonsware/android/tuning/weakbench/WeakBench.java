/***
  Portions Copyright (c) 2010 CommonsWare, LLC
  
  Licensed under the Apache License, Version 2.0 (the "License"); you may
  not use this file except in compliance with the License. You may obtain
  a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/

package com.commonsware.android.tuning.weakbench;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.TextView;

public class WeakBench extends Activity {
  static {
    System.loadLibrary("weakbench");
  }
  
  public native void nsievenative();
  public native void specnative();

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    
    new JavaSieveTask().execute();
  }
  
  /*
   * Code after this point is adapted from the Great Computer Language
   * Shootout. Copyrights are owned by whoever contributed this stuff,
   * or possibly the Shootout itself, since there isn't much information
   * on ownership there. Licensed under a modified BSD license.
   */
  
  private class JavaSieveTask extends AsyncTask<Void, Void, Void> {
    long start=0;
    TextView result=null;
    
    @Override
    protected void onPreExecute() {
      result=(TextView)findViewById(R.id.nsieve_java);
      
      result.setText("running...");		 
    }
    
    @Override
    protected Void doInBackground(Void... unused) {
      start=SystemClock.uptimeMillis();
      
      int n=9;
      int m=(1<<n)*10000;
      boolean[] flags=new boolean[m+1];
      
      nsieve(m,flags);
      
      m=(1<<n-1)*10000;
      nsieve(m,flags);
      
      m=(1<<n-2)*10000;
      nsieve(m,flags);
      
      return(null);
    }
    
    @Override
    protected void onPostExecute(Void unused) {
      long delta=SystemClock.uptimeMillis()-start;
      
      result.setText(String.valueOf(delta));
      new JavaSpecTask().execute();
    }
  }
   
  private class JavaSpecTask extends AsyncTask<Void, Void, Void> {
    long start=0;
    TextView result=null;
    
    @Override
    protected void onPreExecute() {
      result=(TextView)findViewById(R.id.spec_java);
      
      result.setText("running...");		 
    }
    
    @Override
    protected Void doInBackground(Void... unused) {
      start=SystemClock.uptimeMillis();
      
      Approximate(1000);
      
      return(null);
    }
    
    @Override
    protected void onPostExecute(Void unused) {
      long delta=SystemClock.uptimeMillis()-start;
      
      result.setText(String.valueOf(delta));
      new JNISieveTask().execute();
    }
  }
   
  private class JNISieveTask extends AsyncTask<Void, Void, Void> {
    long start=0;
    TextView result=null;
    
    @Override
    protected void onPreExecute() {
      result=(TextView)findViewById(R.id.nsieve_jni);
      
      result.setText("running...");		 
    }
    
    @Override
    protected Void doInBackground(Void... unused) {
      start=SystemClock.uptimeMillis();
      
      nsievenative();
      
      return(null);
    }
    
    @Override
    protected void onPostExecute(Void unused) {
      long delta=SystemClock.uptimeMillis()-start;

      result.setText(String.valueOf(delta));
      new JNISpecTask().execute();
    }
  }
   
  private class JNISpecTask extends AsyncTask<Void, Void, Void> {
    long start=0;
    TextView result=null;
    
    @Override
    protected void onPreExecute() {
      result=(TextView)findViewById(R.id.spec_jni);
      
      result.setText("running...");		 
    }
    
    @Override
    protected Void doInBackground(Void... unused) {
      start=SystemClock.uptimeMillis();
      
      specnative();
      
      return(null);
    }
    
    @Override
    protected void onPostExecute(Void unused) {
      long delta=SystemClock.uptimeMillis()-start;

      result.setText(String.valueOf(delta));
    }
  }

  private static int nsieve(int m, boolean[] isPrime) {
      for (int i=2; i <= m; i++) isPrime[i] = true;
      int count = 0;

      for (int i=2; i <= m; i++) {
         if (isPrime[i]) {
            for (int k=i+i; k <= m; k+=i) isPrime[k] = false;
            count++;
         }
      }
      return count;
  }
  
  private final double Approximate(int n) {
    // create unit vector
    double[] u = new double[n];
    for (int i=0; i<n; i++) u[i] =	1;

    // 20 steps of the power method
    double[] v = new double[n];
    for (int i=0; i<n; i++) v[i] = 0;

    for (int i=0; i<10; i++) {
      MultiplyAtAv(n,u,v);
      MultiplyAtAv(n,v,u);
    }

    // B=AtA				 A multiplied by A transposed
    // v.Bv /(v.v)	 eigenvalue of v
    double vBv = 0, vv = 0;
    for (int i=0; i<n; i++) {
      vBv += u[i]*v[i];
      vv	+= v[i]*v[i];
    }

    return Math.sqrt(vBv/vv);
  }


  /* return element i,j of infinite matrix A */
  private final double A(int i, int j){
    return 1.0/((i+j)*(i+j+1)/2 +i+1);
  }

  /* multiply vector v by matrix A */
  private final void MultiplyAv(int n, double[] v, double[] Av){
    for (int i=0; i<n; i++){
      Av[i] = 0;
      for (int j=0; j<n; j++) Av[i] += A(i,j)*v[j];
    }
  }

  /* multiply vector v by matrix A transposed */
  private final void MultiplyAtv(int n, double[] v, double[] Atv){
    for (int i=0;i<n;i++){
      Atv[i] = 0;
      for (int j=0; j<n; j++) Atv[i] += A(j,i)*v[j];
    }
  }

  /* multiply vector v by matrix A and then by matrix A transposed */
  private final void MultiplyAtAv(int n, double[] v, double[] AtAv){
    double[] u = new double[n];
    MultiplyAv(n,v,u);
    MultiplyAtv(n,u,AtAv);
  }
}