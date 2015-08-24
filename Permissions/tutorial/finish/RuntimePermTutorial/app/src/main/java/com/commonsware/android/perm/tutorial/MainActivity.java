package com.commonsware.android.perm.tutorial;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.commonsware.cwac.cam2.CameraActivity;
import com.commonsware.cwac.cam2.VideoRecorderActivity;
import java.io.File;
import java.util.ArrayList;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends Activity {
  private static final String[] PERMS_ALL={
      CAMERA,
      WRITE_EXTERNAL_STORAGE,
      RECORD_AUDIO
  };
  private static final String[] PERMS_TAKE_PICTURE={
      CAMERA,
      WRITE_EXTERNAL_STORAGE
  };
  private static final int RESULT_PICTURE_TAKEN=1337;
  private static final int RESULT_VIDEO_RECORDED=1338;
  private static final int RESULT_PERMS_INITIAL=1339;
  private static final int RESULT_PERMS_TAKE_PICTURE=1340;
  private static final int RESULT_PERMS_RECORD_VIDEO=1341;
  private static final String PREF_IS_FIRST_RUN="firstRun";
  private static final String PREF_HAVE_REQUESTED_AUDIO="audio";
  private static final String STATE_BREADCRUST=
      "com.commonsware.android.perm.tutorial.breadcrust";
  private File rootDir;
  private View takePicture;
  private View recordVideo;
  private TextView breadcrust;
  private SharedPreferences prefs;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    prefs=PreferenceManager.getDefaultSharedPreferences(this);

    takePicture=findViewById(R.id.take_picture);
    recordVideo=findViewById(R.id.record_video);
    breadcrust=(TextView)findViewById(R.id.breadcrust);

    File downloads=Environment
        .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

    rootDir=new File(downloads, "RuntimePermTutorial");
    rootDir.mkdirs();

    if (isFirstRun() && useRuntimePermissions()) {
      requestPermissions(PERMS_TAKE_PICTURE, RESULT_PERMS_INITIAL);
    }
  }

  @Override
  protected void onResume() {
    super.onResume();

    updateButtons();  // Settings does not terminate process
                      // if permission granted, only if revoked
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);

    if (breadcrust.getVisibility()==View.VISIBLE) {
      outState.putCharSequence(STATE_BREADCRUST, breadcrust.getText());
    }
  }

  @Override
  protected void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);

    CharSequence cs=savedInstanceState.getCharSequence(STATE_BREADCRUST);

    if (cs!=null) {
      breadcrust.setVisibility(View.VISIBLE);
      breadcrust.setText(cs);
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode,
                                  Intent data) {
    Toast t=null;

    if (resultCode==RESULT_OK) {
      if (requestCode==RESULT_PICTURE_TAKEN) {
        t=Toast.makeText(this, R.string.msg_pic_taken,
            Toast.LENGTH_LONG);
      }
      else if (requestCode==RESULT_VIDEO_RECORDED) {
        t=Toast.makeText(this, R.string.msg_vid_recorded,
            Toast.LENGTH_LONG);
      }

      t.show();
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode,
                                         String[] permissions,
                                         int[] grantResults) {
    updateButtons();

    if (requestCode==RESULT_PERMS_TAKE_PICTURE) {
      if (canTakePicture()) {
        takePictureForRealz();
      }
    }
    else if (requestCode==RESULT_PERMS_RECORD_VIDEO) {
      if (canRecordVideo()) {
        recordVideoForRealz();
      }
    }
  }

  public void takePicture(View v) {
    if (canTakePicture()) {
      takePictureForRealz();
    }
    else if (breadcrust.getVisibility()==View.VISIBLE) {
      breadcrust.setVisibility(View.GONE);
      requestPermissions(netPermissions(PERMS_TAKE_PICTURE),
          RESULT_PERMS_TAKE_PICTURE);
    }
    else if (shouldShowTakePictureRationale()) {
      breadcrust.setText(R.string.msg_take_picture);
      breadcrust.setVisibility(View.VISIBLE);
    }
    else {
      throw new IllegalStateException(getString(R.string.msg_state));
    }
  }

  public void recordVideo(View v) {
    if (canRecordVideo()) {
      recordVideoForRealz();
    }
    else if (!haveRequestedAudioPermission() ||
        breadcrust.getVisibility()==View.VISIBLE) {
      breadcrust.setVisibility(View.GONE);
      markRequestedAudioPermission();
      requestPermissions(netPermissions(PERMS_ALL),
          RESULT_PERMS_RECORD_VIDEO);
    }
    else if (shouldShowRecordVideoRationale()) {
      breadcrust.setText(R.string.msg_record_video);
      breadcrust.setVisibility(View.VISIBLE);
    }
    else {
      throw new IllegalStateException(getString(R.string.msg_state));
    }
  }

  private boolean isFirstRun() {
    boolean result=prefs.getBoolean(PREF_IS_FIRST_RUN, true);

    if (result) {
      prefs.edit().putBoolean(PREF_IS_FIRST_RUN, false).apply();
    }

    return(result);
  }

  private boolean haveRequestedAudioPermission() {
    return(prefs.getBoolean(PREF_HAVE_REQUESTED_AUDIO, false));
  }

  private void markRequestedAudioPermission() {
    prefs.edit().putBoolean(PREF_HAVE_REQUESTED_AUDIO, true).apply();
  }

  private void updateButtons() {
    takePicture.setEnabled(couldPossiblyTakePicture());
    recordVideo.setEnabled(couldPossiblyRecordVideo());
  }

  private boolean hasPermission(String perm) {
    if (useRuntimePermissions()) {
      return(checkSelfPermission(perm)==PackageManager.PERMISSION_GRANTED);
    }

    return(true);
  }

  private boolean shouldShowRationale(String perm) {
    if (useRuntimePermissions()) {
      return(!hasPermission(perm) &&
          shouldShowRequestPermissionRationale(perm));
    }

    return(false);
  }

  private boolean wasPermissionRejected(String perm) {
    return(!hasPermission(perm) && !shouldShowRationale(perm));
  }

  private boolean wasAudioRejected() {
    return(!hasPermission(RECORD_AUDIO) &&
            !shouldShowRationale(RECORD_AUDIO) &&
            haveRequestedAudioPermission());
  }

  private boolean canTakePicture() {
    return(hasPermission(CAMERA) &&
        hasPermission(WRITE_EXTERNAL_STORAGE));
  }

  private boolean canRecordVideo() {
    return(canTakePicture() && hasPermission(RECORD_AUDIO));
  }

  private boolean shouldShowTakePictureRationale() {
    return(shouldShowRationale(CAMERA) ||
        shouldShowRationale(WRITE_EXTERNAL_STORAGE));
  }

  private boolean shouldShowRecordVideoRationale() {
    return(shouldShowTakePictureRationale() ||
        shouldShowRationale(RECORD_AUDIO));
  }

  private boolean couldPossiblyTakePicture() {
    return(!wasPermissionRejected(CAMERA) &&
        !wasPermissionRejected(WRITE_EXTERNAL_STORAGE));
  }

  private boolean couldPossiblyRecordVideo() {
    return(couldPossiblyTakePicture() && !wasAudioRejected());
  }

  private String[] netPermissions(String[] wanted) {
    ArrayList<String> result=new ArrayList<String>();

    for (String perm : wanted) {
      if (!hasPermission(perm)) {
        result.add(perm);
      }
    }

    return(result.toArray(new String[result.size()]));
  }

  private boolean useRuntimePermissions() {
    return(Build.VERSION.SDK_INT>Build.VERSION_CODES.LOLLIPOP_MR1);
  }

  private void takePictureForRealz() {
    Intent i=new CameraActivity.IntentBuilder(MainActivity.this)
        .to(new File(rootDir, "test.jpg"))
        .updateMediaStore()
        .build();

    startActivityForResult(i, RESULT_PICTURE_TAKEN);
  }

  private void recordVideoForRealz() {
    Intent i=new VideoRecorderActivity.IntentBuilder(MainActivity.this)
        .quality(VideoRecorderActivity.Quality.HIGH)
        .sizeLimit(5000000)
        .to(new File(rootDir, "test.mp4"))
        .updateMediaStore()
        .forceClassic()
        .build();

    startActivityForResult(i, RESULT_VIDEO_RECORDED);
  }
}