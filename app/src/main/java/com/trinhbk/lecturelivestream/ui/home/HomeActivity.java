package com.trinhbk.lecturelivestream.ui.home;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.trinhbk.lecturelivestream.R;
import com.trinhbk.lecturelivestream.adapter.HomeAdapter;
import com.trinhbk.lecturelivestream.model.Lecture;
import com.trinhbk.lecturelivestream.model.Video;
import com.trinhbk.lecturelivestream.ui.BaseActivity;
import com.trinhbk.lecturelivestream.ui.teacher.TeacherActivity;
import com.trinhbk.lecturelivestream.ui.trimmer.EditVideoActivity;
import com.trinhbk.lecturelivestream.ui.utils.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by TrinhBK on 8/29/2018.
 */

public class HomeActivity extends BaseActivity implements View.OnClickListener {

    public static final String TAG = HomeActivity.class.getSimpleName();

    private FloatingActionButton fabCall;

    private boolean isTeacher;

    private HomeAdapter homeAdapter;
    private List<Video> videos;

    private TextView tvEmpty;
    private RecyclerView rvVideo;



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
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {/* ... */}

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
        }).check();
    }

    private void initView() {
        rvVideo = findViewById(R.id.rcvHome);
        tvEmpty = findViewById(R.id.tvHomeEmpty);
        fabCall = findViewById(R.id.fabHome);
        RecyclerView.LayoutManager recyclerViewLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvVideo.setLayoutManager(recyclerViewLayoutManager);
        videos = new ArrayList<>();
        homeAdapter = new HomeAdapter(videos, new HomeAdapter.OnClickVideo() {
            @Override
            public void onItemWatchVideo(int position) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse( videos.get(position).getVideoPath()));
                intent.setDataAndType(Uri.parse( videos.get(position).getVideoPath()), "video/mp4");
                startActivity(intent);
            }

            @Override
            public void onItemEditVideo(int position) {
                Intent intent = new Intent(HomeActivity.this, EditVideoActivity.class);
                intent.putExtra(Constants.IntentKey.EXTRA_VIDEO_URL, videos.get(position).getVideoPath());
                startActivity(intent);
            }
        });
        rvVideo.setAdapter(homeAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchVideo();
    }

    private void initEvent() {
        fabCall.setOnClickListener(this);
        isTeacher = getIntent().getBooleanExtra(Constants.IntentKey.KEY_INTENT_USER_PERMISSION, false);
    }

    private void fetchVideo() {
        videos.clear();
        searchVid(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES));
        if (videos.size() > 0) {
            homeAdapter.notifyDataSetChanged();
        }
    }

    public void searchVid(File dir) {
        String pattern = ".mp4";
        //Get the listfile of that flder
        File listFile[] = dir.listFiles();

        if (listFile != null) {
            for (int i = 0; i < listFile.length; i++) {
                final int x = i;
                if (listFile[i].isDirectory()) {
//                    walkdir(listFile[i]);
                } else {
                    if (listFile[i].getName().endsWith(pattern)) {
                        // Do what ever u want, add the path of the video to the list
//                        pathVideos.add(listFile[i].getAbsolutePath());
                        Video video = new Video(listFile[i].getName(), listFile[i].getAbsolutePath());
                        videos.add(video);
                    }
                }
            }
        }
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
        if (isTeacher) {
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
