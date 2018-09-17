package com.trinhbk.lecturelivestream.ui.home;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.trinhbk.lecturelivestream.R;
import com.trinhbk.lecturelivestream.model.Lecture;
import com.trinhbk.lecturelivestream.ui.BaseActivity;
import com.trinhbk.lecturelivestream.ui.teacher.TeacherActivity;
import com.trinhbk.lecturelivestream.ui.utils.Constants;

import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by TrinhBK on 8/29/2018.
 */

public class HomeActivity extends BaseActivity implements View.OnClickListener {

//    private MessageSender messageSender;

    private FloatingActionButton fabCall;

    private boolean isTeaacher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        requestPermission();
        initView();

        initEvent();
    }

    private void requestPermission() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO
                ).withListener(new MultiplePermissionsListener() {
            @Override public void onPermissionsChecked(MultiplePermissionsReport report) {/* ... */}
            @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
        }).check();
    }

    private void initView() {
        fabCall = findViewById(R.id.fabHome);
    }

    private void initEvent() {
        fabCall.setOnClickListener(this);
        isTeaacher = getIntent().getBooleanExtra(Constants.IntentKey.KEY_INTENT_USER_PERMISSION, false);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fabHome:
                call();
                break;
        }
    }

    private void call() {
        if (isTeaacher) {
//            messageSender.createRoom(Constants.WebRTC.ROOM_PUBLIC);
            Lecture lecture = new Lecture();
            String fileName = new Date().getTime() + "_" + new Random().nextInt(10000);
            lecture.setTitle(fileName + ".mp4");
            lecture.setPhotoUrl(Constants.file.STORAGE_PATH + "/" + fileName + ".jpg");
            lecture.setAudioUrl(Constants.file.STORAGE_PATH + "/" + fileName + ".mp3");
            lecture.setJsonUrl(Constants.file.STORAGE_PATH + "/" + fileName + ".json");
            lecture.setVideoUrl(Constants.file.STORAGE_PATH + "/" + fileName);
            Intent intent = new Intent(HomeActivity.this, TeacherActivity.class);
            intent.putExtra(Constants.IntentKey.LECTURE, lecture);
            startActivity(intent);
//            NoteActivity.start(HomePageActivity.this, lecture, true, RESULT_CODE_NOTE_ACTIVITY);
        }
    }


}
