package com.trinhbk.lecturelivestream.ui.dialog.settingvideo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.obsez.android.lib.filechooser.ChooserDialog;
import com.trinhbk.lecturelivestream.R;

import java.io.File;
import java.util.Calendar;

/**
 * Created by TrinhBK on 9/28/2018.
 */

public class SettingVideoDFragment extends DialogFragment {

    private View view;

    private RadioGroup rgQuality;
    private EditText edtName;
    private TextView tvPathName;
    private ImageView ivFolder;
    private Button btnCancel;
    private Button btnDone;

    private OnClickSettingVideo mCallback;

    private String pathFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getAbsolutePath() + "/";
    private int bitRate, frameRate;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnClickSettingVideo) context;
        } catch (ClassCastException e) {
        }
    }

    public static SettingVideoDFragment newInstance() {
        SettingVideoDFragment settingVideoDFragment = new SettingVideoDFragment();
        return settingVideoDFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.dialog_fragment_setting_video, container, false);
        }
        initViews();
        initListener();
        return view;
    }

    private void initViews() {
        rgQuality = view.findViewById(R.id.rgSettingVideo);
        edtName = view.findViewById(R.id.edtVideoName);
        tvPathName = view.findViewById(R.id.tvPathVideo);
        ivFolder = view.findViewById(R.id.btnSettingVideoFolder);
        btnCancel = view.findViewById(R.id.btnSettingVideoCancel);
        btnDone = view.findViewById(R.id.btnSettingVideoDone);
    }

    private void initListener() {
        bitRate = 1000000;
        frameRate = 60;
        tvPathName.setText(pathFile);
        rgQuality.setOnCheckedChangeListener((radioGroup, checkedId) -> {
            switch (checkedId) {
                case R.id.low:
                    bitRate = 500000;
                    frameRate = 24;
                    break;
                case R.id.medium:
                    bitRate = 1000000;
                    frameRate = 60;
                    break;
                case R.id.high:
                    bitRate = 10000000;
                    frameRate = 90;
                    break;
            }
        });
        ivFolder.setOnClickListener(view -> {
            new ChooserDialog().with(getActivity())
                    .withFilter(true, false)
                    .withStartFile(Environment.getExternalStorageState())
                    .withChosenListener((path, pathFile) -> {
//                        Toast.makeText(getContext(), "FOLDER: " + path, Toast.LENGTH_SHORT).show();
                        tvPathName.setText(path);
                    })
                    .build()
                    .show();
        });
        btnCancel.setOnClickListener(view -> dismiss());
        btnDone.setOnClickListener(view -> {
            dismiss();
            pathFile = pathFile + (edtName.getText().toString().isEmpty() ? Calendar.getInstance().getTimeInMillis() : edtName.getText().toString()) + ".mp4";
            mCallback.onDone(pathFile, bitRate, frameRate);
        });
    }

    public interface OnClickSettingVideo {
        void onDone(String pathVideo, int bitRate, int frameRate);
    }
}
