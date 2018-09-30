package com.trinhbk.lecturelivestream.ui.teacher;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.pen.Spen;
import com.samsung.android.sdk.pen.SpenSettingEraserInfo;
import com.samsung.android.sdk.pen.SpenSettingPenInfo;
import com.samsung.android.sdk.pen.SpenSettingTextInfo;
import com.samsung.android.sdk.pen.SpenSettingViewInterface;
import com.samsung.android.sdk.pen.document.SpenNoteDoc;
import com.samsung.android.sdk.pen.document.SpenObjectBase;
import com.samsung.android.sdk.pen.document.SpenObjectContainer;
import com.samsung.android.sdk.pen.document.SpenObjectImage;
import com.samsung.android.sdk.pen.document.SpenObjectStroke;
import com.samsung.android.sdk.pen.document.SpenObjectTextBox;
import com.samsung.android.sdk.pen.document.SpenPageDoc;
import com.samsung.android.sdk.pen.engine.SpenContextMenuItemInfo;
import com.samsung.android.sdk.pen.engine.SpenControlBase;
import com.samsung.android.sdk.pen.engine.SpenControlListener;
import com.samsung.android.sdk.pen.engine.SpenFlickListener;
import com.samsung.android.sdk.pen.engine.SpenObjectRuntime;
import com.samsung.android.sdk.pen.engine.SpenObjectRuntimeInfo;
import com.samsung.android.sdk.pen.engine.SpenObjectRuntimeManager;
import com.samsung.android.sdk.pen.engine.SpenSurfaceView;
import com.samsung.android.sdk.pen.engine.SpenTextChangeListener;
import com.samsung.android.sdk.pen.engine.SpenTouchListener;
import com.samsung.android.sdk.pen.plugin.interfaces.SpenObjectRuntimeInterface;
import com.samsung.android.sdk.pen.settingui.SpenSettingEraserLayout;
import com.samsung.android.sdk.pen.settingui.SpenSettingPenLayout;
import com.samsung.android.sdk.pen.settingui.SpenSettingTextLayout;
import com.trinhbk.lecturelivestream.R;
import com.trinhbk.lecturelivestream.ui.BaseActivity;
import com.trinhbk.lecturelivestream.ui.dialog.settingvideo.SettingVideoDFragment;
import com.trinhbk.lecturelivestream.ui.utils.Constants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by TrinhBK on 8/29/2018.
 */

public class TeacherActivity extends BaseActivity implements SettingVideoDFragment.OnClickSettingVideo {

    public static final String TAG = TeacherActivity.class.getSimpleName();

    private final int MODE_PEN = 0;
    private final int MODE_IMG_OBJ = 1;
    private final int MODE_TEXT_OBJ = 2;
    private int mMode = MODE_PEN;
    private int mToolType = SpenSurfaceView.TOOL_SPEN;

    private static final int DISPLAY_WIDTH = 720;
    private static final int DISPLAY_HEIGHT = 1280;

    private final int CONTEXT_MENU_RUN_ID = 0;

    private static final int REQUEST_CODE_SETTING_VIDEO = 96;
    public static final int REQUEST_CODE_SELECT_IMAGE_BACKGROUND = 99;
    public static final int REQUEST_CODE_SELECT_IMAGE = 98;
    private static final int REQUEST_CODE_RECORD = 1000;

    private Handler mStrokeHandler;

    private ImageButton ibBrush;
    private ImageButton ibTempBrush;
    private ImageButton ibEraser;
    private ImageButton ibAddImageBackground;
    private ImageButton ibCaptureScreen;
    private ImageButton ibInsertImage;
    private ImageButton ibAddCamera;
    private ImageButton ibAddText;
    private ImageButton ibSelection;
    private ImageButton ibRecognizeShape;
    private ImageButton ibAddPage;
    private ImageButton ibUndo;
    private ImageButton ibRedo;
    private ImageButton ibRecord;
    private ImageButton ibSave;
    private TextView tvNumberPage;

    private FrameLayout penViewContainer;
    private RelativeLayout penViewLayout;

    private SpenNoteDoc mPenNoteDoc;
    private SpenPageDoc mPenPageDoc;
    private SpenSurfaceView mPenSurfaceView;
    private SpenSettingPenLayout mPenSettingView;
    @SuppressWarnings("deprecation")
    private SpenSettingEraserLayout mEraserSettingView;
    private SpenSettingTextLayout mTextSettingView;

    private SpenObjectRuntimeManager mSpenObjectRuntimeManager;
    private List<SpenObjectRuntimeInfo> mSpenObjectRuntimeInfoList;
    private SpenObjectRuntimeInfo mObjectRuntimeInfo;
    private SpenObjectRuntime mVideoRuntime;

    private MediaRecorder mMediaRecorder;
    private MediaProjectionManager mProjectionManager;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    private int mScreenDensity;

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private MediaProjection mMediaProjection;
    private VirtualDisplay mVirtualDisplay;
    private MediaProjectionCallback mMediaProjectionCallback;
    private int statusRecorder = Constants.Action.ACTION_STOP_RECORDING;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);

        initViews();
        initSamSungPen();
        initMedia();
    }

    private void initViews() {
        ibBrush = findViewById(R.id.ivPen);
        ibTempBrush = findViewById(R.id.ibTempBrush);
        ibEraser = findViewById(R.id.ivEraser);
        ibAddImageBackground = findViewById(R.id.ivAddImage);
        ibInsertImage = findViewById(R.id.ibInsertImage);
        ibCaptureScreen = findViewById(R.id.ibCaptureScreen);
        ibAddCamera = findViewById(R.id.ibAddVideo);
        ibAddText = findViewById(R.id.ibText);
        ibSelection = findViewById(R.id.ibSelection);
        ibRecognizeShape = findViewById(R.id.ibBound);
        ibAddPage = findViewById(R.id.ibAddPage);
        ibUndo = findViewById(R.id.ivUndo);
        ibRedo = findViewById(R.id.ivRedo);
        ibRecord = findViewById(R.id.ibRecord);
        ibSave = findViewById(R.id.ibSave);
        tvNumberPage = findViewById(R.id.tvPageNumber);

        penViewContainer = findViewById(R.id.spenViewContainer);
        penViewLayout = findViewById(R.id.spenViewLayout);
    }

    private void initSamSungPen() {
        // Initialize Pen.
        boolean isSpenFeatureEnabled = false;
        Spen spenPackage = new Spen();
        try {
            spenPackage.initialize(this);
            isSpenFeatureEnabled = spenPackage.isFeatureEnabled(Spen.DEVICE_PEN);
        } catch (SsdkUnsupportedException e) {
            Toast.makeText(this, "This device does not support Spen.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            finish();
        } catch (Exception ex) {
            Toast.makeText(this, "Cannot initialize Pen.", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Create PenSettingView
        mPenSettingView = new SpenSettingPenLayout(this, "", penViewLayout);
        if (mPenSettingView == null) {
            Toast.makeText(this, "Cannot create new PenSettingView.",
                    Toast.LENGTH_SHORT).show();
            finish();
        }
        penViewContainer.addView(mPenSettingView);

        // Create EraserSettingView
        //noinspection deprecation
        mEraserSettingView = new SpenSettingEraserLayout(this, "", penViewLayout);
        if (mEraserSettingView == null) {
            Toast.makeText(this, "Cannot create new EraserSettingView.",
                    Toast.LENGTH_SHORT).show();
            finish();
        }
        penViewContainer.addView(mEraserSettingView);

        // Create SurfacePenView
        mPenSurfaceView = new SpenSurfaceView(this);
        if (mPenSurfaceView == null) {
            Toast.makeText(this, "Cannot create new SpenView.", Toast.LENGTH_SHORT).show();
            finish();
        }
        penViewLayout.addView(mPenSurfaceView);

        // Create TextSettingView.
        mTextSettingView = new SpenSettingTextLayout(this, "", new HashMap<>(), penViewLayout);
        if (mTextSettingView == null) {
            Toast.makeText(this, "Cannot create new TextSettingView.", Toast.LENGTH_SHORT).show();
            finish();
        }
        penViewContainer.addView(mTextSettingView);

        mPenSettingView.setCanvasView(mPenSurfaceView);
        mEraserSettingView.setCanvasView(mPenSurfaceView);

        // Get the dimensions of the screen.
        Display display = getWindowManager().getDefaultDisplay();
        Rect rect = new Rect();
        display.getRectSize(rect);
        // Create SpenNoteDoc.
        try {
            mPenNoteDoc = new SpenNoteDoc(this, rect.width(), rect.height());
        } catch (IOException e) {
            Toast.makeText(this, "Cannot create new NoteDoc.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            finish();
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }

        // After adding a page to NoteDoc, get an instance and set it as a member variable.
        mPenPageDoc = mPenNoteDoc.appendPage();
        mPenPageDoc.setBackgroundColor(0xFFD6E6F5);
        mPenPageDoc.clearHistory();

        // Set PageDoc to View.
        mPenSurfaceView.setPageDoc(mPenPageDoc, true);

        tvNumberPage.setText(String.format(getString(R.string.tv_teacher_page_number), mPenNoteDoc.getPageIndexById(mPenPageDoc.getId())));
        initPenSettingInfo();

        // Register the listeners.
        mPenSurfaceView.setColorPickerListener((color, x, y) -> {
            if (mPenSettingView != null) {
                SpenSettingPenInfo penInfo = mPenSettingView.getInfo();
                penInfo.color = color;
                mPenSettingView.setInfo(penInfo);
            }
        });
        mPenSurfaceView.setTextChangeListener(textChangeListener());
        mPenSurfaceView.setFlickListener(flickListener());
        mPenSurfaceView.setControlListener(mControlListener);
        mPenSurfaceView.setPreTouchListener(onPreTouchSurfaceViewListener);
        //noinspection deprecation
        mEraserSettingView.setEraserListener(() -> {
            // Handle the Clear All button in EraserSettingView.
            mPenPageDoc.removeAllObject();
            mPenSurfaceView.update();
        });

        mPenPageDoc.setHistoryListener(mHistoryListener);

        ibBrush.setOnClickListener(view -> {
            mPenSurfaceView.stopTemporaryStroke();
            mPenSurfaceView.setTouchListener(touchListenerBrush());
            if (mPenSurfaceView.getToolTypeAction(mToolType) == SpenSurfaceView.ACTION_STROKE) {
                if (mPenSettingView.isShown()) {
                    mPenSettingView.setVisibility(View.GONE);
                } else {
                    //noinspection deprecation
                    mPenSettingView.setViewMode(SpenSettingPenLayout.VIEW_MODE_EXTENSION);
                    mPenSettingView.setVisibility(View.VISIBLE);
                }
            } else {
                mMode = MODE_PEN;
                selectButton(ibBrush);
                mPenSurfaceView.setToolTypeAction(mToolType, SpenSettingViewInterface.ACTION_STROKE);
            }
        });

        ibTempBrush.setOnClickListener(view -> {
            mPenSurfaceView.startTemporaryStroke();
            mPenSurfaceView.setTouchListener(touchListenerTemporaryBrush());
            if (mPenSurfaceView.getToolTypeAction(mToolType) == SpenSurfaceView.ACTION_STROKE) {
                if (mPenSettingView.isShown()) {
                    mPenSettingView.setVisibility(View.GONE);
                } else {
                    //noinspection deprecation
                    mPenSettingView.setViewMode(SpenSettingPenLayout.VIEW_MODE_EXTENSION);
                    mPenSettingView.setVisibility(View.VISIBLE);
                }
            } else {
                mMode = MODE_PEN;
                selectButton(ibBrush);
                mPenSurfaceView.setToolTypeAction(mToolType, SpenSettingViewInterface.ACTION_STROKE);
            }
        });

        ibEraser.setOnClickListener(view -> {
            // If it is in eraser tool mode.
            if (mPenSurfaceView.getToolTypeAction(mToolType) == SpenSurfaceView.ACTION_ERASER) {
                // If EraserSettingView is displayed, close it.
                if (mEraserSettingView.isShown()) {
                    mEraserSettingView.setVisibility(View.GONE);
                    // If EraserSettingView is not displayed, display it.
                } else {
                    //noinspection deprecation
                    mEraserSettingView.setViewMode(SpenSettingEraserLayout.VIEW_MODE_NORMAL);
                    mEraserSettingView.setVisibility(View.VISIBLE);
                }
                // If it is not in eraser tool mode, change it to eraser tool mode.
            } else {
                selectButton(ibEraser);
                mPenSurfaceView.setToolTypeAction(mToolType, SpenSurfaceView.ACTION_ERASER);
            }
        });

        ibAddImageBackground.setOnClickListener(view -> {
            closeSettingView();
            callGalleryForInputImage(REQUEST_CODE_SELECT_IMAGE_BACKGROUND);
        });

        ibInsertImage.setOnClickListener(view -> {
            closeSettingView();
            callGalleryForInputImage(REQUEST_CODE_SELECT_IMAGE);
        });

        ibAddCamera.setOnClickListener(view -> {
            ibAddCamera.setClickable(false);
            mPenSurfaceView.closeControl();
            createObjectRuntime();
        });

        ibCaptureScreen.setOnClickListener(view -> {
            closeSettingView();
            capturePenSurfaceView();
        });

        ibAddText.setOnClickListener(view -> {
            mPenSurfaceView.closeControl();
            // When Pen is in text mode.
            if (mPenSurfaceView.getToolTypeAction(mToolType) == SpenSurfaceView.ACTION_TEXT) {
                // Close TextSettingView if TextSettingView is displayed.
                if (mTextSettingView.isShown()) {
                    mTextSettingView.setVisibility(View.GONE);
                    // Display TextSettingView if TextSettingView is not displayed.
                } else {
                    //noinspection deprecation
                    mTextSettingView.setViewMode(SpenSettingTextLayout.VIEW_MODE_NORMAL);
                    mTextSettingView.setVisibility(View.VISIBLE);
                }
                // Switch to text mode unless Pen is in text mode.
            } else {
                mMode = MODE_TEXT_OBJ;
                selectButton(ibAddText);
                mPenSurfaceView.setToolTypeAction(mToolType, SpenSurfaceView.ACTION_TEXT);
            }
        });

        ibSelection.setOnClickListener(view -> {
            selectButton(ibSelection);
            mPenSurfaceView.setToolTypeAction(mToolType, SpenSurfaceView.ACTION_SELECTION);
        });

        ibRecognizeShape.setOnClickListener(view -> {
            mMode = MODE_PEN;
            selectButton(ibRecognizeShape);
            mPenSurfaceView.closeControl();
            mPenSurfaceView.setToolTypeAction(mToolType, SpenSurfaceView.ACTION_RECOGNITION);
        });

        ibAddPage.setOnClickListener(view -> {
            mPenSurfaceView.setPageEffectListener(() -> ibAddPage.setClickable(true));
            mPenSurfaceView.closeControl();
            closeSettingView();
            // Create a page next to the current page.
            mPenPageDoc = mPenNoteDoc.insertPage(mPenNoteDoc.getPageIndexById(mPenPageDoc.getId()) + 1);
            mPenPageDoc.setBackgroundColor(0xFFD6E6F5);
            mPenPageDoc.clearHistory();
            view.setClickable(false);
            mPenSurfaceView.setPageDoc(mPenPageDoc, SpenSurfaceView.PAGE_TRANSITION_EFFECT_RIGHT, SpenSurfaceView.PAGE_TRANSITION_EFFECT_TYPE_SHADOW, 0);
            tvNumberPage.setText(String.format(getString(R.string.tv_teacher_page_number), mPenNoteDoc.getPageIndexById(mPenPageDoc.getId())));
        });

        ibUndo.setOnClickListener(undoRedoOnClickListener);
        ibUndo.setEnabled(mPenPageDoc.isUndoable());

        ibRedo.setOnClickListener(undoRedoOnClickListener);
        ibRedo.setEnabled(mPenPageDoc.isRedoable());

        ibRecord.setOnClickListener(view -> {
            switch (statusRecorder) {
                case Constants.Action.ACTION_PAUSE_RECORDING:
                    statusRecorder = Constants.Action.ACTION_RESUME_RECORDING;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        mMediaRecorder.resume();
                    }
                    break;
                case Constants.Action.ACTION_RESUME_RECORDING:
                    statusRecorder = Constants.Action.ACTION_PAUSE_RECORDING;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        mMediaRecorder.pause();
                    }
                    break;
                case Constants.Action.ACTION_START_RECORDING:
                    break;
                case Constants.Action.ACTION_STOP_RECORDING:
                    statusRecorder = Constants.Action.ACTION_START_RECORDING;
                    SettingVideoDFragment dialogFragment = SettingVideoDFragment.newInstance();
                    dialogFragment.show(getSupportFragmentManager(), dialogFragment.getClass().getSimpleName());
//                    initRecorder();
//                    shareScreen();
                    break;
            }
        });

        ibSave.setOnClickListener(view -> {
            closeSettingView();
//            enableButton(false);
//            mPenSurfaceView.startReplay();
//            mPenPageDoc.getTemplateUri();
            mMediaRecorder.stop();
            mMediaRecorder.reset();
            Toast.makeText(this, "Video is saved", Toast.LENGTH_SHORT).show();
            Log.v(TAG, "Stopping Recording");
            stopScreenSharing();
        });

        selectButton(ibBrush);

        // Set up the ObjectRuntimeManager.
        mSpenObjectRuntimeManager = new SpenObjectRuntimeManager(this);
        mSpenObjectRuntimeInfoList = new ArrayList<>();
        mSpenObjectRuntimeInfoList = mSpenObjectRuntimeManager.getObjectRuntimeInfoList();

        if (!isSpenFeatureEnabled) {
            mPenSurfaceView.setToolTypeAction(SpenSurfaceView.TOOL_FINGER, SpenSurfaceView.ACTION_STROKE);
            Toast.makeText(this, "Device does not support Spen. \n You can draw stroke by finger.", Toast.LENGTH_SHORT).show();
        }
    }

    private void initRecorder(String pathVideo, int bitRate, int frameRate) {
        try {
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//            mMediaRecorder.setOutputFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES) + "/" + Calendar.getInstance().getTimeInMillis() + ".mp4");
            mMediaRecorder.setOutputFile(pathVideo);
            mMediaRecorder.setVideoSize(DISPLAY_WIDTH, DISPLAY_HEIGHT);
            mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//            mMediaRecorder.setVideoEncodingBitRate(512 * 1000);
            mMediaRecorder.setVideoEncodingBitRate(bitRate);
//            mMediaRecorder.setVideoFrameRate(30);
            mMediaRecorder.setVideoFrameRate(frameRate);
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            int orientation = ORIENTATIONS.get(rotation + 90);
            mMediaRecorder.setOrientationHint(orientation);
            mMediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDone(String pathVideo, int bitRate, int frameRate) {
        initRecorder(pathVideo, bitRate, frameRate);
        shareScreen();
    }

    private void shareScreen() {
        if (mMediaProjection == null) {
            startActivityForResult(mProjectionManager.createScreenCaptureIntent(), REQUEST_CODE_RECORD);
            return;
        }
        mVirtualDisplay = createVirtualDisplay();
        mMediaRecorder.start();
    }

    private VirtualDisplay createVirtualDisplay() {
        return mMediaProjection.createVirtualDisplay("MainActivity",
                DISPLAY_WIDTH, DISPLAY_HEIGHT, mScreenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mMediaRecorder.getSurface(), null /*Callbacks*/, null
                /*Handler*/);
    }

    private void stopScreenSharing() {
        if (mVirtualDisplay == null) {
            return;
        }
        mVirtualDisplay.release();
        //mMediaRecorder.release(); //If used: mMediaRecorder object cannot be reused again
        destroyMediaProjection();
    }

    private void destroyMediaProjection() {
        if (mMediaProjection != null) {
            mMediaProjection.unregisterCallback(mMediaProjectionCallback);
            mMediaProjection.stop();
            mMediaProjection = null;
        }
        Log.i(TAG, "MediaProjection Stopped");
    }

    private void initMedia() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mScreenDensity = metrics.densityDpi;
        mMediaRecorder = new MediaRecorder();
        mProjectionManager = (MediaProjectionManager) getSystemService
                (Context.MEDIA_PROJECTION_SERVICE);
    }

    SpenControlListener mControlListener = new SpenControlListener() {
        @Override
        public boolean onCreated(ArrayList<SpenObjectBase> objectList, ArrayList<Rect> relativeRectList, ArrayList<SpenContextMenuItemInfo> menu, ArrayList<Integer> styleList, int pressType, PointF point) {
            if (objectList == null) {
                return false;
            }
            // Display the context menu if any SOR information is found.
            if (objectList.get(0).getSorInfo() != null) {
                menu.add(new SpenContextMenuItemInfo(CONTEXT_MENU_RUN_ID, "Run", true));
                return true;
            }
            return true;
        }

        @Override
        public boolean onMenuSelected(
                ArrayList<SpenObjectBase> objectList, int itemId) {
            if (objectList == null) {
                return true;
            }
            if (itemId == CONTEXT_MENU_RUN_ID) {
                SpenObjectBase object = objectList.get(0);
                mPenSurfaceView.getControl().setContextMenuVisible(false);
                mPenSurfaceView.getControl().setStyle(SpenControlBase.STYLE_BORDER_STATIC);
                // Set up listener and make it play.
                mVideoRuntime.setListener(objectRuntimeListener);
                mVideoRuntime.start(object, getRealRect(object.getRect()),
                        mPenSurfaceView.getPan(), mPenSurfaceView.getZoomRatio(),
                        mPenSurfaceView.getFrameStartPosition(), penViewLayout);
                mPenSurfaceView.update();
            }
            return false;
        }

        @Override
        public void onObjectChanged(ArrayList<SpenObjectBase> object) {
        }

        @Override
        public void onRectChanged(RectF rect, SpenObjectBase object) {
        }

        @Override
        public void onRotationChanged(float angle, SpenObjectBase objectBase) {
        }

        @Override
        public boolean onClosed(ArrayList<SpenObjectBase> objectList) {
            if (mVideoRuntime != null)
                mVideoRuntime.stop(true);
            return false;
        }
    };

    private SpenTouchListener onPreTouchSurfaceViewListener = (view, event) -> {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                enableButton(false);
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                enableButton(true);
                break;
        }
        return false;
    };

    private void enableButton(boolean isEnable) {
        ibSelection.setEnabled(isEnable);
        ibBrush.setEnabled(isEnable);
        ibAddCamera.setEnabled(isEnable);
    }

    private RectF getRealRect(RectF rect) {
        float panX = mPenSurfaceView.getPan().x;
        float panY = mPenSurfaceView.getPan().y;
        float zoom = mPenSurfaceView.getZoomRatio();
        PointF startPoint = mPenSurfaceView.getFrameStartPosition();
        RectF realRect = new RectF();
        realRect.set(
                (rect.left - panX) * zoom + startPoint.x,
                (rect.top - panY) * zoom + startPoint.y,
                (rect.right - panX) * zoom + startPoint.x,
                (rect.bottom - panY) * zoom + startPoint.y
        );
        return realRect;
    }

    private void createObjectRuntime() {
        if (mSpenObjectRuntimeInfoList == null || mSpenObjectRuntimeInfoList.size() == 0) {
            return;
        }
        try {
            for (SpenObjectRuntimeInfo info : mSpenObjectRuntimeInfoList) {
                if (info.name.equalsIgnoreCase("Video")) {
                    mVideoRuntime = mSpenObjectRuntimeManager.createObjectRuntime(info);
                    mObjectRuntimeInfo = info;
                    startObjectRuntime();
                    return;
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "ObjectRuntimeInfo class not found.", Toast.LENGTH_SHORT).show();
        } catch (InstantiationException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to access the ObjectRuntimeInfo constructor.", Toast.LENGTH_SHORT).show();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to access the ObjectRuntimeInfo field or method.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "ObjectRuntimeInfo is not loaded.", Toast.LENGTH_SHORT).show();
        }
    }

    private void startObjectRuntime() {
        if (mVideoRuntime == null) {
            Toast.makeText(this, "ObjectRuntime is not loaded \n Load Plug-in First !!", Toast.LENGTH_SHORT).show();
            return;
        }
        SpenObjectBase objectBase = null;
        switch (mVideoRuntime.getType()) {
            case SpenObjectRuntimeInterface.TYPE_NONE:
                return;
            case SpenObjectRuntimeInterface.TYPE_IMAGE:
                objectBase = new SpenObjectImage();
                break;
            case SpenObjectRuntimeInterface.TYPE_STROKE:
                objectBase = new SpenObjectStroke();
                break;
            case SpenObjectRuntimeInterface.TYPE_CONTAINER:
                objectBase = new SpenObjectContainer();
                break;
            default:
                break;
        }
        if (objectBase == null) {
            Toast.makeText(this, "Has no selected object.",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        objectBase.setSorInfo(mObjectRuntimeInfo.className);
        objectBase.setOutOfViewEnabled(false);
        mVideoRuntime.setListener(objectRuntimeListener);
        mPenPageDoc.appendObject(objectBase);
        mPenPageDoc.selectObject(objectBase);
        mPenSurfaceView.update();
        mPenSurfaceView.getControl().setContextMenuVisible(false);
        mVideoRuntime.start(objectBase,
                new RectF(0, 0, mPenPageDoc.getWidth(), mPenPageDoc.getHeight()),
                mPenSurfaceView.getPan(), mPenSurfaceView.getZoomRatio(),
                mPenSurfaceView.getFrameStartPosition(), penViewLayout);
    }

    SpenObjectRuntime.UpdateListener objectRuntimeListener = new SpenObjectRuntime.UpdateListener() {

        @Override
        public void onCompleted(Object objectBase) {
            if (mPenSurfaceView != null) {
                SpenControlBase control = mPenSurfaceView.getControl();
                if (control != null) {
                    control.setContextMenuVisible(true);
                    //noinspection deprecation
                    mPenSurfaceView.updateScreenFrameBuffer();
                    mPenSurfaceView.update();
                }
            }
            ibAddCamera.setClickable(true);
        }

        @Override
        public void onObjectUpdated(RectF rect, Object objectBase) {
            if (mPenSurfaceView != null) {
                SpenControlBase control = mPenSurfaceView.getControl();
                if (control != null) {
                    control.fit();
                    control.invalidate();
                    mPenSurfaceView.update();
                }
            }
        }

        @Override
        public void onCanceled(int state, Object objectBase) {
            if (state == SpenObjectRuntimeInterface.CANCEL_STATE_INSERT) {
                mPenPageDoc.removeObject((SpenObjectBase) objectBase);
                mPenPageDoc.removeSelectedObject();
                mPenSurfaceView.closeControl();
                mPenSurfaceView.update();
            } else if (state == SpenObjectRuntimeInterface.CANCEL_STATE_RUN) {
                mPenSurfaceView.closeControl();
                mPenSurfaceView.update();
            }
            ibAddCamera.setClickable(true);
        }
    };

    @NonNull
    private SpenFlickListener flickListener() {
        return direction -> {
            int pageIndex = mPenNoteDoc.getPageIndexById(mPenPageDoc.getId());
            int pageCount = mPenNoteDoc.getPageCount();
            boolean checkSetPageDoc = false;
            if (pageCount > 1) {
                // Flick left and turn to the previous page.
                if (direction == SpenFlickListener.DIRECTION_LEFT) {
                    mPenPageDoc = mPenNoteDoc.getPage((pageIndex + pageCount - 1) % pageCount);
                    if (mPenSurfaceView.setPageDoc(mPenPageDoc, SpenSurfaceView.PAGE_TRANSITION_EFFECT_LEFT, SpenSurfaceView.PAGE_TRANSITION_EFFECT_TYPE_SHADOW, 0)) {
                        checkSetPageDoc = true;
                    } else {
                        checkSetPageDoc = false;
                        mPenPageDoc = mPenNoteDoc.getPage(pageIndex);
                    }
                    // Flick right and turn to the next page.
                } else if (direction == SpenFlickListener.DIRECTION_RIGHT) {
                    mPenPageDoc = mPenNoteDoc.getPage((pageIndex + 1) % pageCount);
                    if (mPenSurfaceView.setPageDoc(mPenPageDoc, SpenSurfaceView.PAGE_TRANSITION_EFFECT_RIGHT, SpenSurfaceView.PAGE_TRANSITION_EFFECT_TYPE_SHADOW, 0)) {
                        checkSetPageDoc = true;
                    } else {
                        checkSetPageDoc = false;
                        mPenPageDoc = mPenNoteDoc.getPage(pageIndex);
                    }
                }
                if (checkSetPageDoc) {
                    tvNumberPage.setText(String.format(getString(R.string.tv_teacher_page_number), mPenNoteDoc.getPageIndexById(mPenPageDoc.getId())));
                }
                return true;
            }
            return false;
        };
    }

    @NonNull
    private SpenTextChangeListener textChangeListener() {
        return new SpenTextChangeListener() {
            @Override
            public void onChanged(SpenSettingTextInfo spenSettingTextInfo, int state) {
                if (mTextSettingView != null) {
                    if (state == CONTROL_STATE_SELECTED) {
                        mTextSettingView.setInfo(spenSettingTextInfo);
                    }
                }
            }

            @Override
            public boolean onSelectionChanged(int i, int i1) {
                return false;
            }

            @Override
            public void onMoreButtonDown(SpenObjectTextBox spenObjectTextBox) {

            }

            @Override
            public void onFocusChanged(boolean b) {

            }
        };
    }

    @NonNull
    private SpenTouchListener touchListenerBrush() {
        return (view, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP && event.getToolType(0) == mToolType) {
                // Checks whether the control is generated or not.
                SpenControlBase control = mPenSurfaceView.getControl();
                if (control == null) {
                    // When touching the screen in Insert ObjectImage mode.
//                    if (mMode == MODE_IMG_OBJ) {
                    // Set the Bitmap file to ObjectImage.
//                        SpenObjectImage imgObj = new SpenObjectImage();
//                        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_add_shape);
//                        imgObj.setImage(imageBitmap);
                    // Specify the location where ObjectImage is inserted and add PageDoc.
//                        float imgWidth = imageBitmap.getWidth();
//                        float imgHeight = imageBitmap.getHeight();
//                        RectF rect1 = getRealPoint(event.getX(), event.getY(), imgWidth, imgHeight);
//                        imgObj.setRect(rect1, true);
//                        mPenPageDoc.appendObject(imgObj);
//                        mPenSurfaceView.update();
//                        imageBitmap.recycle();
//                        return true;
                    // When touching the screen in Insert ObjectTextBox mode.
//                    } else
                    if (mPenSurfaceView.getToolTypeAction(mToolType) == SpenSurfaceView.ACTION_TEXT) {
                        // Specify the location where ObjectTextBox is inserted and add PageDoc.
                        SpenObjectTextBox textObj = new SpenObjectTextBox();
                        RectF rect1 = getRealPoint(event.getX(), event.getY(), 0, 0);
                        rect1.right += 200;
                        rect1.bottom += 50;
                        textObj.setRect(rect1, true);
                        mPenPageDoc.appendObject(textObj);
                        mPenPageDoc.selectObject(textObj);
                        mPenSurfaceView.update();
                    }
                }
            }
            return false;
        };
    }

    @NonNull
    private SpenTouchListener touchListenerTemporaryBrush() {
        return (v, motionEvent) -> {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // When ACTION_DOWN occurs before mStrokeRunnable is set in a queue, the mStrokeRunnable that waits is removed.
                    if (mStrokeHandler != null) {
                        mStrokeHandler.removeCallbacks(mStrokeRunnable);
                        mStrokeHandler = null;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    // Generate Handler to put mStrokeRunnable in a queue when it takes 1000 milliseconds after ACTION_UP occurred.
                    mStrokeHandler = new Handler();
                    mStrokeHandler.postDelayed(mStrokeRunnable, 1000);
                    break;
            }
            return true;
        };
    }

    private final Runnable mStrokeRunnable = new Runnable() {
        @Override
        public void run() {
            // Get TemporaryStroke to resize the object by 1/2.
            mPenSurfaceView.stopTemporaryStroke();
            mPenSurfaceView.startTemporaryStroke();
            mPenSurfaceView.update();
        }
    };

    private RectF getRealPoint(float x, float y, float width, float height) {
        float panX = mPenSurfaceView.getPan().x;
        float panY = mPenSurfaceView.getPan().y;
        float zoom = mPenSurfaceView.getZoomRatio();
        width *= zoom;
        height *= zoom;
        RectF realRect = new RectF();
        realRect.set(
                (x - width / 2) / zoom + panX, (y - height / 2) / zoom + panY,
                (x + width / 2) / zoom + panX, (y + height / 2) / zoom + panY);
        return realRect;
    }

    private void callGalleryForInputImage(int nRequestCode) {
        // Get an image from the gallery.
        try {
            Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent, nRequestCode);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Cannot find gallery.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void capturePenSurfaceView() {
        // Select the location to save the image.
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SPen/images";
        File fileCacheItem = new File(filePath);
        if (!fileCacheItem.exists()) {
            if (!fileCacheItem.mkdirs()) {
                Toast.makeText(this, "Save Path Creation Error", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        filePath = fileCacheItem.getPath() + "/CaptureImg.png";
        // Save the screen shot as a Bitmap.

//        SpenCapturePage  spenCapturePage = null;
//        spenCapturePage.setPageDoc(mPenPageDoc);
//        spenCapturePage.capturePage(1.0f);
        Bitmap imgBitmap = mPenSurfaceView.captureCurrentView(true);
        OutputStream out = null;
        try {
            // Save the Bitmap in the selected location.
            out = new FileOutputStream(filePath);
            imgBitmap.compress(Bitmap.CompressFormat.PNG, 80, out);
            Toast.makeText(this, "Captured images were stored in the file \'CaptureImg.png\'.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Capture failed.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
//                sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        imgBitmap.recycle();
    }

    private View.OnClickListener undoRedoOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mPenPageDoc == null) {
                return;
            }
            // Undo button is clicked.
            if (v.equals(ibUndo)) {
                if (mPenPageDoc.isUndoable()) {
                    SpenPageDoc.HistoryUpdateInfo[] userData = mPenPageDoc.undo();
                    mPenSurfaceView.updateUndo(userData);
                }
                // Redo button is clicked.
            } else if (v.equals(ibRedo)) {
                if (mPenPageDoc.isRedoable()) {
                    SpenPageDoc.HistoryUpdateInfo[] userData = mPenPageDoc.redo();
                    mPenSurfaceView.updateRedo(userData);
                }
            }
        }
    };

    private SpenPageDoc.HistoryListener mHistoryListener = new SpenPageDoc.HistoryListener() {
        @Override
        public void onCommit(SpenPageDoc page) {
        }

        @Override
        public void onUndoable(SpenPageDoc page, boolean undoable) {
            // Enable or disable Undo button depending on its availability.
            ibUndo.setEnabled(undoable);
        }

        @Override
        public void onRedoable(SpenPageDoc page, boolean redoable) {
            // Enable or disable Redo button depending on its availability.
            ibRedo.setEnabled(redoable);
        }
    };

    private void selectButton(ImageView ivSelected) {
        ibBrush.setSelected(false);
        ibEraser.setSelected(false);
        ibSelection.setSelected(false);
        ivSelected.setSelected(true);
        closeSettingView();
    }

    private void closeSettingView() {
        // Close all the setting views.
        mEraserSettingView.setVisibility(SpenSurfaceView.GONE);
        mPenSettingView.setVisibility(SpenSurfaceView.GONE);
    }

    private void initPenSettingInfo() {
        // Initialize pen settings.
        SpenSettingPenInfo penInfo = new SpenSettingPenInfo();
        penInfo.color = Color.BLUE;
        penInfo.size = 10;
        mPenSurfaceView.setPenSettingInfo(penInfo);
        mPenSettingView.setInfo(penInfo);

        SpenSettingTextInfo textInfo = mTextSettingView.getInfo();
        textInfo.color = Color.BLACK;
        mTextSettingView.setInfo(textInfo);

        // Initialize eraser settings.
        SpenSettingEraserInfo eraserInfo = new SpenSettingEraserInfo();
        eraserInfo.size = 30;
        mPenSurfaceView.setEraserSettingInfo(eraserInfo);
        mEraserSettingView.setInfo(eraserInfo);
    }

    private String getRealPathFromURI(Uri contentURI) {
        Cursor cursor = this.getContentResolver().query(contentURI, null, null, null, null);
        String result = null;
        // for API 19 and above
        if (cursor != null) {
            cursor.moveToFirst();
            String image_id = cursor.getString(0);
            image_id = image_id.substring(image_id.lastIndexOf(":") + 1);
            cursor.close();
            cursor = this.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Images.Media._ID + " = ? ", new String[]{image_id}, null);
            cursor.moveToFirst();
            result = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            cursor.close();
        }
        return result;
    }

    private class MediaProjectionCallback extends MediaProjection.Callback {
        @Override
        public void onStop() {
            if (statusRecorder == Constants.Action.ACTION_RESUME_RECORDING) {
                statusRecorder = Constants.Action.ACTION_STOP_RECORDING;
                mMediaRecorder.stop();
                mMediaRecorder.reset();
                Log.v(TAG, "Recording Stopped");
            }
            mMediaProjection = null;
            stopScreenSharing();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (data == null) {
                Toast.makeText(this, "Cannot find the image", Toast.LENGTH_SHORT).show();
                return;
            }
            // Process image request for the background.
            if (requestCode == REQUEST_CODE_SELECT_IMAGE_BACKGROUND) {
                // Get the image's URI and use the location for background image.
                Uri imageFileUri = data.getData();
                @SuppressLint("Recycle")
                Cursor cursor = getContentResolver().query(Uri.parse(imageFileUri != null ? imageFileUri.toString() : null), null, null, null, null);
                if (cursor != null) {
                    cursor.moveToNext();
                }
                String imageRealPath = getRealPathFromURI(imageFileUri);
                mPenPageDoc.setBackgroundImage(imageRealPath);
                mPenSurfaceView.update();
            }
            if (requestCode == REQUEST_CODE_SELECT_IMAGE) {
                Uri imageFileUri = data.getData();
                @SuppressLint("Recycle")
                Cursor cursor = getContentResolver().query(Uri.parse(imageFileUri != null ? imageFileUri.toString() : null), null, null, null, null);
                if (cursor != null) {
                    cursor.moveToNext();
                }
                String imageRealPath = getRealPathFromURI(imageFileUri);
                SpenObjectImage imgObj = new SpenObjectImage();
                imgObj.setImage(imageRealPath);
                RectF rect1 = new RectF(mPenPageDoc.getWidth() / 4, mPenPageDoc.getWidth() / 4, mPenPageDoc.getWidth() / 2, mPenPageDoc.getHeight() / 2);
                imgObj.setRect(rect1, true);
                mPenPageDoc.appendObject(imgObj);
                mPenPageDoc.selectObject(imgObj);
                mPenSurfaceView.update();
            }

            if (requestCode == REQUEST_CODE_RECORD) {
                mMediaProjectionCallback = new MediaProjectionCallback();
                mMediaProjection = mProjectionManager.getMediaProjection(resultCode, data);
                mMediaProjection.registerCallback(mMediaProjectionCallback, null);
                mVirtualDisplay = createVirtualDisplay();
                mMediaRecorder.start();
                statusRecorder = Constants.Action.ACTION_RESUME_RECORDING;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //   prevent memory leaks when you application closes.

        if (mMediaProjection != null) {
            mMediaProjection.unregisterCallback(mMediaProjectionCallback);
            mMediaProjection.stop();
            mMediaProjection = null;
        }

        if (mPenSettingView != null) {
            mPenSettingView.close();
        }

        if (mStrokeHandler != null) {
            mStrokeHandler.removeCallbacks(mStrokeRunnable);
            mStrokeHandler = null;
        }

        if (mEraserSettingView != null) {
            mEraserSettingView.close();
        }

        if (mSpenObjectRuntimeManager != null) {
            if (mVideoRuntime != null) {
                mVideoRuntime.stop(true);
                mSpenObjectRuntimeManager.unload(mVideoRuntime);
            }
            mSpenObjectRuntimeManager.close();
        }

        if (mPenPageDoc.isRecording()) {
            mPenPageDoc.stopRecord();
        }

        if (mPenSurfaceView.getReplayState() == SpenSurfaceView.REPLAY_STATE_PLAYING) {
            mPenSurfaceView.stopReplay();
        }

        if (mTextSettingView != null) {
            mTextSettingView.close();
        }
        //  Close the text control
        mPenSurfaceView.closeControl();


        if (mPenSurfaceView != null) {
            mPenSurfaceView.close();
            mPenSurfaceView = null;
        }
        if (mPenNoteDoc != null) {
            try {
                mPenNoteDoc.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mPenNoteDoc = null;
        }
    }
}
