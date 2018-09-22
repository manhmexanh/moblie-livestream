package com.trinhbk.lecturelivestream.ui.utils;

import android.os.Environment;

/**
 * Created by TrinhBK on 8/29/2018.
 */

public class Constants {

    public static class IntentKey {
        public final static String KEY_INTENT_USER_PERMISSION = "key_intent_user_permisson";
        public final static String LECTURE = "lecture";
        public static final  String EXTRA_DATA = "data";
        public static final  String EXTRA_RESULT_CODE = "result_code";
        public static final  String EXTRA_VIDEO_URL = "video_url";
        public static final  String EXTRA_PREVIEW_TYPE = "preview_type";
    }

    public static final class file {
        public static final String KEY_INTENT_PATH_DIR = "selected_folder";
        public static final String STORAGE_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();

    }

    public static final class WebRTC {
        public static final String TYPE_MESSAGE = "type";
        public static final String TYPE_REQUEST = "request";
        public static final String TYPE_OFFER = "offer";
        public static final String TYPE_ANSWER = "answer";
        public static final String TYPE_CANDIDATE = "candidate";
        public static final String TYPE_CANCEL = "cancel";
        public static final String TYPE_HANG_UP = "hangup";
        public static final String TYPE_INIT = "init";
        public static final String TYPE_SCREEN = "screen";
        public static final String ROOM_PUBLIC = "room";
        public static final String CONTENT = "content";
        public static final String X = "x";
        public static final String Y = "y";
        public static final String COLOR = "color";
        public static final String CHANGE = "change";

        public static final String KEY_SCREEN_INFO = "screen_info";
    }

    public static final class Action {
        //Action
        public static final int ACTION_START_RECORDING = 1;
        public static final  int ACTION_STOP_RECORDING = 2;
        public static final int ACTION_PAUSE_RECORDING = 3;
        public static final  int ACTION_RESUME_RECORDING = 4;
    }
}
