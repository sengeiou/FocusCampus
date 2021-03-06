package com.sam.globalRentalCar.video;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qiniu.pili.droid.shortvideo.PLBuiltinFilter;
import com.qiniu.pili.droid.shortvideo.PLGifWatermarkSetting;
import com.qiniu.pili.droid.shortvideo.PLImageView;
import com.qiniu.pili.droid.shortvideo.PLMediaFile;
import com.qiniu.pili.droid.shortvideo.PLMixAudioFile;
import com.qiniu.pili.droid.shortvideo.PLPaintView;
import com.qiniu.pili.droid.shortvideo.PLShortVideoEditor;
import com.qiniu.pili.droid.shortvideo.PLSpeedTimeRange;
import com.qiniu.pili.droid.shortvideo.PLTextView;
import com.qiniu.pili.droid.shortvideo.PLVideoEditSetting;
import com.qiniu.pili.droid.shortvideo.PLVideoFilterListener;
import com.qiniu.pili.droid.shortvideo.PLVideoSaveListener;
import com.qiniu.pili.droid.shortvideo.PLWatermarkSetting;
import com.sam.globalRentalCar.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static com.sam.globalRentalCar.video.RecordSettings.RECORD_SPEED_ARRAY;

public class VideoEditActivity extends AppCompatActivity implements PLVideoSaveListener {

    private static final String TAG = "VideoEditActivity";
    private static final String MP4_PATH = "MP4_PATH";
    private static final String PREVIOUS_ORIENTATION = "PREVIOUS_ORIENTATION";

    private static final int REQUEST_CODE_PICK_AUDIO_MIX_FILE = 0;
    private static final int REQUEST_CODE_DUB = 1;
    private static final int REQUEST_CODE_MULTI_AUDIO_MIX_FILE = 2;

    private int mRotation = 0;

    // ???????????????????????????
    private enum PLShortVideoEditorStatus {
        Idle, Playing, Paused,
    }

    private PLShortVideoEditorStatus mShortVideoEditorStatus = PLShortVideoEditorStatus.Idle;

    private GLSurfaceView mPreviewView;
    private RecyclerView mFiltersList;
    private TextSelectorPanel mTextSelectorPanel;
    private CustomProgressDialog mProcessingDialog;
    private ImageButton mMuteButton;
    private ImageButton mPausePalybackButton;
    private AudioMixSettingDialog mAudioMixSettingDialog;
    private PaintSelectorPanel mPaintSelectorPanel;
    private LinearLayout mSpeedPanel;

    private PLShortVideoEditor mShortVideoEditor;
    private String mSelectedFilter;
    private String mSelectedMV;
    private String mSelectedMask;
    private PLWatermarkSetting mWatermarkSetting;
    private PLWatermarkSetting mSaveWatermarkSetting;
    private PLWatermarkSetting mPreviewWatermarkSetting;
    private PLPaintView mPaintView;
    private ImageSelectorPanel mImageSelectorPanel;
    private GifSelectorPanel mGifSelectorPanel;

    private int mFgVolumeBeforeMute = 100;
    private long mMixDuration = 5000; // ms
    private boolean mIsMuted = false;
    private boolean mIsMixAudio = false;
    private boolean mIsUseWatermark = true;

    private String mMp4path;

    private TextView mSpeedTextView;
    private View mVisibleView;

    private int mPreviousOrientation;

    private FrameListView mFrameListView;
    private TimerTask mScrollTimerTask;
    private Timer mScrollTimer;
    private View mCurView;
    private boolean mIsRangeSpeed;

    /**
     * Gif ????????????
     */
    private Map<StickerImageView, PLGifWatermarkSetting> mGifViewSettings = new HashMap<>();
    private FrameLayout mStickerViewGroup;

    private int mAudioMixingMode = -1;       // audio mixing mode: 0 - single audio mixing; 1 - multiple audio mixing; ???????????????????????????????????????????????????????????????????????????
    private boolean mMainAudioFileAdded;
    private int mAudioMixingFileCount = 0;
    private long mInputMp4FileDurationMs = 0;
    private double mSpeed = 1;
    private PLMixAudioFile mMainMixAudioFile;
    private float mMainMixAudioFileVolume = 1;

    public static void start(Activity activity, String mp4Path) {
        Intent intent = new Intent(activity, VideoEditActivity.class);
        intent.putExtra(MP4_PATH, mp4Path);
        activity.startActivity(intent);
    }

    public static void start(Activity activity, String mp4Path, int previousOrientation) {
        Intent intent = new Intent(activity, VideoEditActivity.class);
        intent.putExtra(MP4_PATH, mp4Path);
        intent.putExtra(PREVIOUS_ORIENTATION, previousOrientation);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_edit);

        mSpeedTextView = (TextView) findViewById(R.id.normal_speed_text);
        mMuteButton = (ImageButton) findViewById(R.id.mute_button);
        mMuteButton.setImageResource(R.mipmap.btn_unmute);
        mPausePalybackButton = (ImageButton) findViewById(R.id.pause_playback);
        mSpeedPanel = (LinearLayout) findViewById(R.id.speed_panel);
        mFrameListView = (FrameListView) findViewById(R.id.frame_list_view);
        mStickerViewGroup = findViewById(R.id.sticker_container_view);

        mPreviousOrientation = getIntent().getIntExtra(PREVIOUS_ORIENTATION, 1);

        initPreviewView();
        initTextSelectorPanel();
        initPaintSelectorPanel();
        initImageSelectorPanel();
        initGifSelectorPanel();
        initProcessingDialog();
        initWatermarkSetting();
        initShortVideoEditor();
        initFiltersList();
        initAudioMixSettingDialog();
        initResources();

        mStickerViewGroup.post(new Runnable() {
            @Override
            public void run() {
                initGifViewGroup();
            }
        });
    }

    private void initPreviewView() {
        mPreviewView = (GLSurfaceView) findViewById(R.id.preview);
        mPreviewView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveViewTimeAndHideRect();
            }
        });
    }

    private void initTextSelectorPanel() {
        mTextSelectorPanel = findViewById(R.id.text_selector_panel);
        mTextSelectorPanel.setOnTextSelectorListener(new TextSelectorPanel.OnTextSelectorListener() {
            @Override
            public void onTextSelected(StrokedTextView textView) {
                addText(textView);
            }

            @Override
            public void onViewClosed() {
                setPanelVisibility(mTextSelectorPanel, false);
            }
        });
    }

    private void initPaintSelectorPanel() {
        mPaintSelectorPanel = (PaintSelectorPanel) findViewById(R.id.paint_selector_panel);
        mPaintSelectorPanel.setOnPaintSelectorListener(new PaintSelectorPanel.OnPaintSelectorListener() {
            @Override
            public void onViewClosed() {
                setPanelVisibility(mPaintSelectorPanel, false);

                mPaintView.setPaintEnable(false);
            }

            @Override
            public void onPaintColorSelected(int color) {
                mPaintView.setPaintColor(color);
            }

            @Override
            public void onPaintSizeSelected(int size) {
                mPaintView.setPaintSize(size);
            }

            @Override
            public void onPaintUndoSelected() {
                mPaintView.undo();
            }

            @Override
            public void onPaintClearSelected() {
                mPaintView.clear();
            }
        });
    }

    private void initImageSelectorPanel() {
        mImageSelectorPanel = (ImageSelectorPanel) findViewById(R.id.image_selector_panel);
        mImageSelectorPanel.setOnImageSelectedListener(new ImageSelectorPanel.OnImageSelectedListener() {
            @Override
            public void onImageSelected(Drawable drawable) {
                addImageView(drawable);
            }
        });
    }

    private void initGifSelectorPanel() {
        mGifSelectorPanel = findViewById(R.id.gif_selector_panel);
        mGifSelectorPanel.setOnGifSelectedListener(new GifSelectorPanel.OnGifSelectedListener() {
            @Override
            public void onGifSelected(String gifPath) {
                addGif(gifPath);
            }
        });
    }

    private void initProcessingDialog() {
        mProcessingDialog = new CustomProgressDialog(this);
        mProcessingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                mShortVideoEditor.cancelSave();
            }
        });
    }

    private void initWatermarkSetting() {
        mWatermarkSetting = createWatermarkSetting();
        //??????????????????
        mPreviewWatermarkSetting = createWatermarkSetting();
        mSaveWatermarkSetting = createWatermarkSetting();
    }

    private void initShortVideoEditor() {
        mMp4path = getIntent().getStringExtra(MP4_PATH);
        Log.i(TAG, "editing file: " + mMp4path);

        PLVideoEditSetting setting = new PLVideoEditSetting();
        setting.setSourceFilepath(mMp4path);
        setting.setDestFilepath(Config.EDITED_FILE_PATH);
        setting.setGifPreviewEnabled(false);

        mShortVideoEditor = new PLShortVideoEditor(mPreviewView);
        mShortVideoEditor.setVideoEditSetting(setting);
        mShortVideoEditor.setVideoSaveListener(this);

        mMixDuration = mShortVideoEditor.getDurationMs();

        mFrameListView.setVideoPath(mMp4path);
        mFrameListView.setOnVideoFrameScrollListener(new FrameListView.OnVideoFrameScrollListener() {
            @Override
            public void onVideoFrameScrollChanged(final long timeMs) {
                if (mShortVideoEditorStatus == PLShortVideoEditorStatus.Playing) {
                    pausePlayback();
                }
                mShortVideoEditor.seekTo((int) timeMs);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        changeGifVisiable(timeMs);
                    }
                });
            }
        });

        initTimerTask();
    }

    private void initFiltersList() {
        mFiltersList = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mFiltersList.setLayoutManager(layoutManager);
        mFiltersList.setAdapter(new FilterListAdapter(mShortVideoEditor.getBuiltinFilterList()));
        setPanelVisibility(mFiltersList, true);
    }

    private void initAudioMixSettingDialog() {
        mAudioMixSettingDialog = new AudioMixSettingDialog(this);
        // make dialog create +
        mAudioMixSettingDialog.show();
        mAudioMixSettingDialog.dismiss();
        // make dialog create -
        mAudioMixSettingDialog.setOnAudioVolumeChangedListener(mOnAudioVolumeChangedListener);
        mAudioMixSettingDialog.setOnPositionSelectedListener(mOnPositionSelectedListener);
    }

    /**
     * ?????? GIF ????????? Assets ???SD???
     */
    private void initResources() {
        try {
            File dir = new File(Config.GIF_STICKER_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
                String[] fs = getAssets().list("gif");
                for (String file : fs) {
                    InputStream is = getAssets().open("gif/" + file);
                    FileOutputStream fos = new FileOutputStream(new File(dir, file));
                    byte[] buffer = new byte[1024];
                    int byteCount;
                    while ((byteCount = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, byteCount);
                    }
                    fos.flush();
                    is.close();
                    fos.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * ??????????????????
     */
    public void addText(StrokedTextView selectText) {
        saveViewTimeAndHideRect();

        StickerTextView stickerTextView = (StickerTextView) View.inflate(VideoEditActivity.this, R.layout.sticker_text_view, null);
        stickerTextView.setText(selectText.getText().toString());
        stickerTextView.setTextColor(selectText.getCurrentTextColor());
        stickerTextView.setTypeface(selectText.getTypeface());
        stickerTextView.setShadowLayer(selectText.getShadowRadius(), selectText.getShadowDx(), selectText.getShadowDy(), selectText.getShadowColor());
        mShortVideoEditor.addTextView(stickerTextView);

        stickerTextView.setOnStickerOperateListener(new StickerOperateListener(stickerTextView));
        addSelectorView(stickerTextView);
        showViewBorder(stickerTextView);
    }

    /**
     * ??????????????????
     */
    private void addImageView(Drawable drawable) {
        saveViewTimeAndHideRect();

        StickerImageView stickerImageView = (StickerImageView) View.inflate(VideoEditActivity.this, R.layout.sticker_image_view, null);
        stickerImageView.setImageDrawable(drawable);

        mShortVideoEditor.addImageView(stickerImageView);
        stickerImageView.setOnStickerOperateListener(new StickerOperateListener(stickerImageView));

        addSelectorView(stickerImageView);
        showViewBorder(stickerImageView);
    }

    /**
     * ?????? GIF ??????
     */
    private void addGif(String gifPath) {
        saveViewTimeAndHideRect();

        StickerImageView stickerImageView = (StickerImageView) View.inflate(VideoEditActivity.this, R.layout.sticker_image_view, null);
        stickerImageView.setGifFile(gifPath);
        stickerImageView.startGifPlaying();

        addSelectorView(stickerImageView);

        mStickerViewGroup.addView(stickerImageView);
        mStickerViewGroup.setVisibility(View.VISIBLE);

        PLGifWatermarkSetting gifWatermarkSetting = getGifSettingFromSticker(stickerImageView);
        mGifViewSettings.put(stickerImageView, gifWatermarkSetting);
        mShortVideoEditor.addGifWatermark(gifWatermarkSetting);

        stickerImageView.setOnStickerOperateListener(new StickerOperateListener(stickerImageView));
        showViewBorder(stickerImageView);
    }

    /**
     * ?????????????????????????????????
     */
    private void addSelectorView(View view) {
        View selectorView = mFrameListView.addSelectorView();
        view.setTag(R.id.selector_view, selectorView);
    }

    /**
     * ?????? StickerImageView ??????????????? PLGifWatermarkSetting
     */
    private PLGifWatermarkSetting getGifSettingFromSticker(StickerImageView stickerImageView) {
        PLGifWatermarkSetting gifWatermarkSetting = new PLGifWatermarkSetting();
        gifWatermarkSetting.setFilePath(stickerImageView.getGifPath());
        gifWatermarkSetting.setDisplayPeriod(stickerImageView.getStartTime(), stickerImageView.getEndTime() - stickerImageView.getStartTime());
        gifWatermarkSetting.setPosition((float) stickerImageView.getViewX() / mPreviewView.getWidth(), (float) stickerImageView.getViewY() / mPreviewView.getHeight());
        gifWatermarkSetting.setRotation((int) stickerImageView.getImageDegree());
        gifWatermarkSetting.setAlpha(255);
        gifWatermarkSetting.setSize(stickerImageView.getImageWidth() * stickerImageView.getImageScale() / mPreviewView.getWidth(), stickerImageView.getImageHeight() * stickerImageView.getImageScale() / mPreviewView.getHeight());
        return gifWatermarkSetting;
    }

    /**
     * ????????????
     */
    private void startPlayback() {
        if (mShortVideoEditorStatus == PLShortVideoEditorStatus.Idle) {
            mShortVideoEditor.startPlayback(new PLVideoFilterListener() {
                @Override
                public void onSurfaceCreated() {

                }

                @Override
                public void onSurfaceChanged(int width, int height) {

                }

                @Override
                public void onSurfaceDestroy() {

                }

                @Override
                public int onDrawFrame(int texId, int texWidth, int texHeight, long timestampNs, float[] transformMatrix) {
                    final int time = mShortVideoEditor.getCurrentPosition();
                    //??????????????????????????????????????????????????????
                    if (time > 3000) {
                        mPreviewWatermarkSetting.setPosition(0.01f, 1);
                    } else {
                        mPreviewWatermarkSetting.setPosition(0.01f, 0.01f);
                    }
                    mShortVideoEditor.updatePreviewWatermark(mIsUseWatermark ? mPreviewWatermarkSetting : null);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            changeGifVisiable(time);
                        }
                    });

                    return texId;
                }
            });
            mShortVideoEditorStatus = PLShortVideoEditorStatus.Playing;
        } else if (mShortVideoEditorStatus == PLShortVideoEditorStatus.Paused) {
            mShortVideoEditor.resumePlayback();
            mShortVideoEditorStatus = PLShortVideoEditorStatus.Playing;
        }
        mPausePalybackButton.setImageResource(R.mipmap.btn_pause);
    }

    /**
     * ????????????
     */
    private void stopPlayback() {
        mShortVideoEditor.stopPlayback();
        mShortVideoEditorStatus = PLShortVideoEditorStatus.Idle;
        mPausePalybackButton.setImageResource(R.mipmap.btn_play);
    }

    /**
     * ????????????
     */
    private void pausePlayback() {
        mShortVideoEditor.pausePlayback();
        mShortVideoEditorStatus = PLShortVideoEditorStatus.Paused;
        mPausePalybackButton.setImageResource(R.mipmap.btn_play);
    }

    /**
     * ????????????????????????
     */
    public void onClickShowSpeed(View view) {
        mSpeedPanel.setVisibility((mSpeedPanel.getVisibility() == View.GONE) ? View.VISIBLE : View.GONE);
    }

    /**
     * ????????????????????????
     */
    public void onSpeedClicked(View view) {
        mSpeedTextView.setTextColor(getResources().getColor(R.color.speedTextNormal));

        TextView textView = (TextView) view;
        textView.setTextColor(getResources().getColor(R.color.colorAccent));
        mSpeedTextView = textView;

        double recordSpeed = 1.0;
        switch (view.getId()) {
            case R.id.super_slow_speed_text:
                recordSpeed = RECORD_SPEED_ARRAY[0];
                mIsRangeSpeed = false;
                break;
            case R.id.slow_speed_text:
                recordSpeed = RECORD_SPEED_ARRAY[1];
                mIsRangeSpeed = false;
                break;
            case R.id.normal_speed_text:
                recordSpeed = RECORD_SPEED_ARRAY[2];
                mIsRangeSpeed = false;
                break;
            case R.id.fast_speed_text:
                recordSpeed = RECORD_SPEED_ARRAY[3];
                mIsRangeSpeed = false;
                break;
            case R.id.super_fast_speed_text:
                recordSpeed = RECORD_SPEED_ARRAY[4];
                mIsRangeSpeed = false;
                break;
            case R.id.range_speed_text:
                mIsRangeSpeed = true;
                break;
            default:
                break;
        }

        mSpeed = recordSpeed;
        mShortVideoEditor.setSpeed(mSpeed, true);
    }

    /**
     * ????????????????????????????????? GIF ?????????
     */
    private void changeGifVisiable(final long timeMS) {
        if (mGifViewSettings != null) {
            for (final StickerImageView gifViews : mGifViewSettings.keySet()) {
                if (gifViews.getStartTime() == 0 && gifViews.getEndTime() == 0) {
                    //???????????????????????????????????????
                    gifViews.setVisibility(View.VISIBLE);
                    continue;
                }
                if (timeMS >= gifViews.getStartTime() && timeMS <= gifViews.getEndTime()) {
                    gifViews.setVisibility(View.VISIBLE);
                } else {
                    gifViews.setVisibility(View.GONE);
                }
            }
        }
    }

    private void initTimerTask() {
        mScrollTimerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mShortVideoEditorStatus == PLShortVideoEditorStatus.Playing) {
                            int position = mShortVideoEditor.getCurrentPosition();
                            mFrameListView.scrollToTime(position);
                        }
                    }
                });
            }
        };
        mScrollTimer = new Timer();
        // scroll fps:20
        mScrollTimer.schedule(mScrollTimerTask, 50, 50);
    }

    /**
     * ????????????
     */
    private PLWatermarkSetting createWatermarkSetting() {
        PLWatermarkSetting watermarkSetting = new PLWatermarkSetting();
        watermarkSetting.setResourceId(R.drawable.qiniu_logo);
        watermarkSetting.setPosition(0.01f, 0.01f);
        watermarkSetting.setAlpha(128);
        return watermarkSetting;
    }

    /**
     * ?????? GIF ???????????????
     */
    private void initGifViewGroup() {
        ViewGroup.LayoutParams surfaceLayout = mStickerViewGroup.getLayoutParams();

        //??????????????????????????????????????????
        PLMediaFile mediaFile = new PLMediaFile(mMp4path);
        int outputWidth = mediaFile.getVideoWidth();
        int outputHeight = mediaFile.getVideoHeight();
        int rotation = mediaFile.getVideoRotation();
        if ((rotation == 90 || rotation == 270)) {
            int temp = outputWidth;
            outputWidth = outputHeight;
            outputHeight = temp;
        }
        //?????????????????????????????????
        if (outputWidth > outputHeight) {
            surfaceLayout.width = mPreviewView.getWidth();
            surfaceLayout.height = Math.round((float) outputHeight * mPreviewView.getWidth() / outputWidth);
        } else {
            surfaceLayout.height = mPreviewView.getHeight();
            surfaceLayout.width = Math.round((float) outputWidth * mPreviewView.getHeight() / outputHeight);
        }
        //?????? GIF ???????????????????????????
        mStickerViewGroup.setLayoutParams(surfaceLayout);
        mStickerViewGroup.setTranslationX(mPreviewView.getWidth() - surfaceLayout.width);
        mStickerViewGroup.setTranslationY((mPreviewView.getHeight() - surfaceLayout.height) / 2);
        mStickerViewGroup.requestLayout();
    }

    /**
     * ???????????? View ?????????????????????????????????
     */
    private void saveViewTimeAndHideRect() {
        if (mCurView != null) {
            View rectView = mFrameListView.addSelectedRect((View) mCurView.getTag(R.id.selector_view));
            mCurView.setTag(R.id.rect_view, rectView);
            FrameListView.SectionItem sectionItem = mFrameListView.getSectionByRectView(rectView);
            if (mCurView instanceof StickerImageView && ((StickerImageView) mCurView).getGifPath() != null) {
                ((StickerImageView) mCurView).setTime(sectionItem.getStartTime(), sectionItem.getEndTime());
                saveGifSetting();
            } else {
                mShortVideoEditor.setViewTimeline(mCurView, sectionItem.getStartTime(), (sectionItem.getEndTime() - sectionItem.getStartTime()));
            }
            mCurView.setSelected(false);
            mCurView = null;
        }
    }

    /**
     * ????????????
     */
    public void onClickReset(View v) {
        mSelectedFilter = null;
        mSelectedMV = null;
        mSelectedMask = null;
        mShortVideoEditor.setBuiltinFilter(null);
        mShortVideoEditor.setMVEffect(null, null);
        mShortVideoEditor.setAudioMixFile(null);
        mIsMixAudio = false;
        mAudioMixSettingDialog.clearMixAudio();
    }

    /**
     * ?????????
     */
    public void onClickMix(View v) {
        if (mAudioMixingMode == 1) {
            ToastUtils.s(this, "???????????????????????????????????????????????????");
            return;
        }
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT < 19) {
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("audio/*");
        } else {
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("audio/*");
        }
        mAudioMixingMode = 0;
        startActivityForResult(Intent.createChooser(intent, "????????????????????????"), REQUEST_CODE_PICK_AUDIO_MIX_FILE);
    }

    /**
     * ????????????
     */
    public void onClickMultipleAudioMixing(View v) {
        PLMediaFile mediaFile = new PLMediaFile(mMp4path);
        boolean isPureVideo = !mediaFile.hasAudio();
        mediaFile.release();

        if (mAudioMixingMode == 0) {
            ToastUtils.s(this, "???????????????????????????????????????????????????");
            return;
        }
        if (isPureVideo) {
            ToastUtils.s(this, "?????????????????????????????????????????????????????????");
            return;
        }

        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT < 19) {
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("audio/*");
        } else {
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("audio/*");
        }
        mAudioMixingMode = 1;
        startActivityForResult(Intent.createChooser(intent, "????????????????????????"), REQUEST_CODE_MULTI_AUDIO_MIX_FILE);
    }

    /**
     * ??????
     */
    public void onClickMute(View v) {
        mIsMuted = !mIsMuted;
        mShortVideoEditor.muteOriginAudio(mIsMuted);
        mMuteButton.setImageResource(mIsMuted ? R.mipmap.btn_mute : R.mipmap.btn_unmute);
        if (mIsMuted) {
            mFgVolumeBeforeMute = mAudioMixSettingDialog.getSrcVolumeProgress();
        }
        mAudioMixSettingDialog.setSrcVolumeProgress(mIsMuted ? 0 : mFgVolumeBeforeMute);
        if (mMainMixAudioFile != null) {
            if (mIsMuted) {
                mMainMixAudioFileVolume = mMainMixAudioFile.getVolume();
                mMainMixAudioFile.setVolume(0);
            } else {
                mMainMixAudioFile.setVolume(mMainMixAudioFileVolume);
            }
        }
    }

    /**
     * ????????????????????????
     */
    public void onClickTextSelect(View v) {
        setPanelVisibility(mTextSelectorPanel, true);
    }

    /**
     * ??????
     */
    public void onClickDubAudio(View v) {
        //Intent intent = new Intent(this, VideoDubActivity.class);
        // intent.putExtra(VideoDubActivity.MP4_PATH, mMp4path);
        // startActivityForResult(intent, REQUEST_CODE_DUB);
    }

    /**
     * ???????????? ??????
     */
    public void onClickAudioMixSetting(View v) {
        if (mIsMixAudio) {
            mAudioMixSettingDialog.show();
        } else {
            ToastUtils.s(this, "???????????????????????????");
        }
    }

    /**
     * ????????????
     */
    public void onClickBack(View v) {
        finish();
    }

    /**
     * ????????????
     */
    public void onClickToggleWatermark(View v) {
        mIsUseWatermark = !mIsUseWatermark;
        mShortVideoEditor.setWatermark(mIsUseWatermark ? mWatermarkSetting : null);
    }

    /**
     * ????????????????????????
     */
    public void onClickToggleGifWatermark(View v) {
        setPanelVisibility(mGifSelectorPanel, true);
    }

    /**
     * ????????????
     */
    public void onClickRotate(View v) {
        mRotation = (mRotation + 90) % 360;
        mShortVideoEditor.setRotation(mRotation);
        for (PLGifWatermarkSetting gifWatermarkSetting : mGifViewSettings.values()) {
            mShortVideoEditor.addGifWatermark(gifWatermarkSetting);
        }
        startPlayback();
    }

    /**
     * ?????????????????????????????????
     */
    private void createTextDialog(final PLTextView textView) {
        final EditText edit = new EditText(VideoEditActivity.this);
        edit.setText(textView.getText());

        AlertDialog.Builder builder = new AlertDialog.Builder(VideoEditActivity.this);
        builder.setView(edit);
        builder.setTitle("???????????????");
        builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((StickerTextView) textView).setText(edit.getText().toString());
            }
        });
        builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    /**
     * ??????????????????????????????????????????
     */
    private void showViewBorder(View view) {
        mCurView = view;
        mCurView.setSelected(true);

        pausePlayback();
    }

    /**
     * ??????????????????
     */
    private class StickerOperateListener implements OnStickerOperateListener {

        private View mView;

        StickerOperateListener(View view) {
            mView = view;
        }

        /**
         * ?????????????????????
         */
        @Override
        public void onDeleteClicked() {
            if (mView instanceof StickerTextView) {
                mShortVideoEditor.removeTextView((PLTextView) mView);
            } else {
                if (((StickerImageView) mView).getGifPath() != null) {
                    mStickerViewGroup.removeView(mView);
                    mShortVideoEditor.removeGifWatermark(mGifViewSettings.get(mView));
                    mGifViewSettings.remove(mView);
                } else {
                    mShortVideoEditor.removeImageView((PLImageView) mView);
                }
            }

            View rectView = (View) mView.getTag(R.id.rect_view);
            if (rectView != null) {
                mFrameListView.removeRectView((View) mView.getTag(R.id.rect_view));
            }
            FrameSelectorView selectorView = (FrameSelectorView) mView.getTag(R.id.selector_view);
            if (selectorView != null) {
                mFrameListView.removeSelectorView(selectorView);
            }
            mCurView = null;
        }

        /**
         * ?????????????????????
         */
        @Override
        public void onEditClicked() {
            if (mView instanceof StickerTextView) {
                createTextDialog((StickerTextView) mView);
            }
        }

        /**
         * ??????????????????
         */
        @Override
        public void onStickerSelected() {
            if (mCurView != mView) {
                saveViewTimeAndHideRect();
                mCurView = mView;
                FrameSelectorView selectorView = (FrameSelectorView) mCurView.getTag(R.id.selector_view);
                selectorView.setVisibility(View.VISIBLE);
                View rectView = (View) mCurView.getTag(R.id.rect_view);
                if (rectView != null) {
                    mFrameListView.showSelectorByRectView(selectorView, rectView);
                    mFrameListView.removeRectView(rectView);
                }
            }

        }

    }

    /**
     * ????????????????????????
     */
    public void onClickShowFilters(View v) {
        setPanelVisibility(mFiltersList, true);
        mFiltersList.setAdapter(new FilterListAdapter(mShortVideoEditor.getBuiltinFilterList()));
    }

    /**
     * ??????????????????
     */
    public void onClickShowImages(View v) {
        setPanelVisibility(mImageSelectorPanel, true);
    }

    /**
     * ??????????????????
     */
    public void onClickShowPaint(View v) {
        setPanelVisibility(mPaintSelectorPanel, true);

        if (mPaintView == null) {
            mPaintView = new PLPaintView(this, mPreviewView.getWidth(), mPreviewView.getHeight());
            mShortVideoEditor.addPaintView(mPaintView);
        }
        mPaintView.setPaintEnable(true);
        mPaintSelectorPanel.setup();
    }

    private void setPanelVisibility(View panel, boolean isVisible) {
        setPanelVisibility(panel, isVisible, false);
    }

    /**
     * ???????????????????????????
     */
    private void setPanelVisibility(View panel, boolean isVisible, boolean isEffect) {
        if (panel instanceof TextSelectorPanel || panel instanceof PaintSelectorPanel) {
            if (isVisible) {
                panel.setVisibility(View.VISIBLE);
                mVisibleView = mImageSelectorPanel.getVisibility() == View.VISIBLE ? mImageSelectorPanel : mFiltersList;
                mVisibleView.setVisibility(View.GONE);
            } else {
                panel.setVisibility(View.GONE);
                mVisibleView.setVisibility(View.VISIBLE);
            }
        } else {
            if (isVisible) {
                mImageSelectorPanel.setVisibility(View.GONE);
                mFiltersList.setVisibility(View.GONE);
                mGifSelectorPanel.setVisibility(View.GONE);
            }
            panel.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * ?????? MV ???????????? ????????????
     */
    public void onClickShowMVs(View v) {
        setPanelVisibility(mFiltersList, true);
        try {
            File dir = new File(Environment.getExternalStorageDirectory() + "/ShortVideo/mvs");
            // copy mv assets to sdcard
            if (!dir.exists()) {
                dir.mkdirs();
                String[] fs = getAssets().list("mvs");
                for (String file : fs) {
                    InputStream is = getAssets().open("mvs/" + file);
                    FileOutputStream fos = new FileOutputStream(new File(dir, file));
                    byte[] buffer = new byte[1024];
                    int byteCount;
                    while ((byteCount = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, byteCount);
                    }
                    fos.flush();
                    is.close();
                    fos.close();
                }
            }

            FileReader jsonFile = new FileReader(new File(dir, "plsMVs.json"));
            StringBuilder sb = new StringBuilder();
            int read;
            char[] buf = new char[2048];
            while ((read = jsonFile.read(buf, 0, 2048)) != -1) {
                sb.append(buf, 0, read);
            }
            Log.i(TAG, sb.toString());
            JSONObject json = new JSONObject(sb.toString());
            mFiltersList.setAdapter(new MVListAdapter(json.getJSONArray("MVs")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ????????????????????????
     */
    public void onClickTogglePlayback(View v) {
        if (mShortVideoEditorStatus == PLShortVideoEditorStatus.Playing) {
            saveViewTimeAndHideRect();
            pausePlayback();
        } else {
            saveViewTimeAndHideRect();
            startPlayback();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CODE_PICK_AUDIO_MIX_FILE) {
            String selectedFilepath = GetPathFromUri.getPath(this, data.getData());
            Log.i(TAG, "Select file: " + selectedFilepath);
            if (!TextUtils.isEmpty(selectedFilepath)) {
                mShortVideoEditor.setAudioMixFile(selectedFilepath);
                mAudioMixSettingDialog.setMixMaxPosition(mShortVideoEditor.getAudioMixFileDuration());
                mIsMixAudio = true;
            }
        } else if (requestCode == REQUEST_CODE_MULTI_AUDIO_MIX_FILE) {
            String selectedFilepath = GetPathFromUri.getPath(this, data.getData());
            try {
                if (!mMainAudioFileAdded) {
                    mMainMixAudioFile = new PLMixAudioFile(mMp4path);
                    PLMediaFile mp4File = new PLMediaFile(mMp4path);
                    mInputMp4FileDurationMs = mp4File.getDurationMs();
                    mp4File.release();
                    mShortVideoEditor.addMixAudioFile(mMainMixAudioFile);
                    mMainAudioFileAdded = true;
                }

                PLMixAudioFile audioFile = new PLMixAudioFile(selectedFilepath);
                if (mAudioMixingFileCount == 0) {
                    ToastUtils.s(this, "???????????????????????????");
                    long firstMixingDurationMs = (mInputMp4FileDurationMs <= 5000) ? mInputMp4FileDurationMs : 5000;
                    audioFile.setDurationInVideo(firstMixingDurationMs * 1000);
                } else if (mAudioMixingFileCount == 1) {
                    ToastUtils.s(this, "???????????????????????????");
                    if (mInputMp4FileDurationMs - 5000 < 1000) {
                        ToastUtils.s(this, "?????????????????????????????????????????????????????????");
                        return;
                    }
                    audioFile.setOffsetInVideo(5000 * 1000 * mAudioMixingFileCount);
                    long secondMixingDurationMs = mInputMp4FileDurationMs - 5000;
                    audioFile.setDurationInVideo(secondMixingDurationMs * 1000);
                } else if (mAudioMixingFileCount >= 2) {
                    ToastUtils.s(this, "??????????????????2???????????????");
                    return;
                }
                audioFile.setVolume(0.5f);
                mShortVideoEditor.addMixAudioFile(audioFile);

                mAudioMixingFileCount++;
            } catch (IOException e) {
                e.printStackTrace();
            }

        } /*else if (requestCode == REQUEST_CODE_DUB) {
            String dubMp4Path = data.getStringExtra(VideoDubActivity.DUB_MP4_PATH);
            if (!TextUtils.isEmpty(dubMp4Path)) {
                finish();
                VideoEditActivity.start(this, dubMp4Path);
            }
        }*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        mShortVideoEditor.setBuiltinFilter(mSelectedFilter);
        mShortVideoEditor.setMVEffect(mSelectedMV, mSelectedMask);
        mShortVideoEditor.setWatermark(mIsUseWatermark ? mWatermarkSetting : null);
        for (PLGifWatermarkSetting gifWatermarkSetting : mGifViewSettings.values()) {
            mShortVideoEditor.addGifWatermark(gifWatermarkSetting);
        }
        startPlayback();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopPlayback();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mScrollTimer != null) {
            mScrollTimer.cancel();
            mScrollTimer = null;
        }
        if (mScrollTimerTask != null) {
            mScrollTimerTask.cancel();
            mScrollTimerTask = null;
        }
    }

    @Override
    public void finish() {
        if (0 == mPreviousOrientation) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        super.finish();
    }

    /**
     * ???????????????????????????
     */
    private void setSpeedTimeRanges() {
        PLMediaFile mediaFile = new PLMediaFile(mMp4path);
        long durationMs = mediaFile.getDurationMs();
        mediaFile.release();

        PLSpeedTimeRange plSpeedTimeRange1 = new PLSpeedTimeRange(0.5, 0, durationMs / 3);
        PLSpeedTimeRange plSpeedTimeRange2 = new PLSpeedTimeRange(1, durationMs / 3, durationMs * 2 / 3);
        PLSpeedTimeRange plSpeedTimeRange3 = new PLSpeedTimeRange(2, durationMs * 2 / 3, durationMs);

        ArrayList<PLSpeedTimeRange> speedTimeRanges = new ArrayList<>();
        speedTimeRanges.add(plSpeedTimeRange1);
        speedTimeRanges.add(plSpeedTimeRange2);
        speedTimeRanges.add(plSpeedTimeRange3);

        mShortVideoEditor.setSpeedTimeRanges(speedTimeRanges);
    }

    /**
     * ????????????
     */
    public void onSaveEdit(View v) {
        saveViewTimeAndHideRect();
        startPlayback();
        mProcessingDialog.show();
        if (mIsRangeSpeed) {
            setSpeedTimeRanges();
        }
        if (mMainMixAudioFile != null) {
            mMainMixAudioFile.setSpeed(mSpeed);
            mMainMixAudioFile.setDurationInVideo((int) (mInputMp4FileDurationMs * 1000 / mSpeed));
        }
        mShortVideoEditor.save(new PLVideoFilterListener() {
            @Override
            public void onSurfaceCreated() {

            }

            @Override
            public void onSurfaceChanged(int width, int height) {

            }

            @Override
            public void onSurfaceDestroy() {

            }

            @Override
            public int onDrawFrame(int texId, int texWidth, int texHeight, long timestampNs, float[] transformMatrix) {
                long time = timestampNs / 1000000L;
                //?????????????????????????????????????????????????????????
                if (time > 3000) {
                    mSaveWatermarkSetting.setPosition(0.01f, 1);
                } else {
                    mSaveWatermarkSetting.setPosition(0.01f, 0.01f);
                }
                mShortVideoEditor.updateSaveWatermark(mIsUseWatermark ? mSaveWatermarkSetting : null);
                return texId;
            }
        });
    }

    /**
     * ??????GIF??????
     */
    private void saveGifSetting() {
        if (mCurView != null && mCurView instanceof StickerImageView && ((StickerImageView) mCurView).getGifPath() != null) {
            StickerImageView stickerImageView = (StickerImageView) mCurView;
            PLGifWatermarkSetting gifWatermarkSetting = mGifViewSettings.get(stickerImageView);
            gifWatermarkSetting.setDisplayPeriod(stickerImageView.getStartTime(), stickerImageView.getEndTime() - stickerImageView.getStartTime());
            gifWatermarkSetting.setPosition((float) stickerImageView.getViewX() / mStickerViewGroup.getWidth(), (float) stickerImageView.getViewY() / mStickerViewGroup.getHeight());
            gifWatermarkSetting.setRotation((int) stickerImageView.getImageDegree());
            gifWatermarkSetting.setAlpha(255);
            gifWatermarkSetting.setSize(stickerImageView.getImageWidth() * stickerImageView.getImageScale() / mStickerViewGroup.getWidth(), stickerImageView.getImageHeight() * stickerImageView.getImageScale() / mStickerViewGroup.getHeight());
            mShortVideoEditor.updateGifWatermark(gifWatermarkSetting);
        }
    }

    /**
     * ????????????????????????
     */
    @Override
    public void onSaveVideoSuccess(String filePath) {
        Log.i(TAG, "save edit success filePath: " + filePath);
        mProcessingDialog.dismiss();
        PlaybackActivity.start(VideoEditActivity.this, filePath);
    }

    /**
     * ????????????????????????
     */
    @Override
    public void onSaveVideoFailed(final int errorCode) {
        Log.e(TAG, "save edit failed errorCode:" + errorCode);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProcessingDialog.dismiss();
                ToastUtils.toastErrorCode(VideoEditActivity.this, errorCode);
            }
        });
    }

    /**
     * ????????????????????????
     */
    @Override
    public void onSaveVideoCanceled() {
        mProcessingDialog.dismiss();
    }

    /**
     * ????????????????????????
     */
    @Override
    public void onProgressUpdate(final float percentage) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProcessingDialog.setProgress((int) (100 * percentage));
            }
        });
    }

    private class FilterItemViewHolder extends RecyclerView.ViewHolder {
        public ImageView mIcon;
        public TextView mName;

        public FilterItemViewHolder(View itemView) {
            super(itemView);
            mIcon = (ImageView) itemView.findViewById(R.id.icon);
            mName = (TextView) itemView.findViewById(R.id.name);
        }
    }

    /**
     * ??????????????????
     */
    private class FilterListAdapter extends RecyclerView.Adapter<FilterItemViewHolder> {
        private PLBuiltinFilter[] mFilters;

        public FilterListAdapter(PLBuiltinFilter[] filters) {
            this.mFilters = filters;
        }

        @Override
        public FilterItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View contactView = inflater.inflate(R.layout.filter_item, parent, false);
            FilterItemViewHolder viewHolder = new FilterItemViewHolder(contactView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(FilterItemViewHolder holder, int position) {
            try {
                if (position == 0) {
                    holder.mName.setText("None");
                    Bitmap bitmap = BitmapFactory.decodeStream(getAssets().open("filters/none.png"));
                    holder.mIcon.setImageBitmap(bitmap);
                    holder.mIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mSelectedFilter = null;
                            mShortVideoEditor.setBuiltinFilter(null);
                        }
                    });
                    return;
                }

                final PLBuiltinFilter filter = mFilters[position - 1];
                holder.mName.setText(filter.getName());
                InputStream is = getAssets().open(filter.getAssetFilePath());
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                holder.mIcon.setImageBitmap(bitmap);
                holder.mIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSelectedFilter = filter.getName();
                        mShortVideoEditor.setBuiltinFilter(mSelectedFilter);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return mFilters != null ? mFilters.length + 1 : 0;
        }
    }

    /**
     * MV ????????????
     */
    private class MVListAdapter extends RecyclerView.Adapter<FilterItemViewHolder> {
        private JSONArray mMVArray;

        public MVListAdapter(JSONArray mvArray) {
            this.mMVArray = mvArray;
        }

        @Override
        public FilterItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View contactView = inflater.inflate(R.layout.filter_item, parent, false);
            FilterItemViewHolder viewHolder = new FilterItemViewHolder(contactView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(FilterItemViewHolder holder, int position) {
            final String mvsDir = Config.VIDEO_STORAGE_DIR + "mvs/";

            try {
                if (position == 0) {
                    holder.mName.setText("None");
                    Bitmap bitmap = BitmapFactory.decodeFile(mvsDir + "none.png");
                    holder.mIcon.setImageBitmap(bitmap);
                    holder.mIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mSelectedMV = null;
                            mSelectedMask = null;
                            mShortVideoEditor.setMVEffect(null, null);
                        }
                    });
                    return;
                }

                final JSONObject mv = mMVArray.getJSONObject(position - 1);
                holder.mName.setText(mv.getString("name"));
                Bitmap bitmap = BitmapFactory.decodeFile(mvsDir + mv.getString("coverDir") + ".png");
                holder.mIcon.setImageBitmap(bitmap);
                holder.mIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            mSelectedMV = mvsDir + mv.getString("colorDir") + ".mp4";
                            mSelectedMask = mvsDir + mv.getString("alphaDir") + ".mp4";
                            mShortVideoEditor.setMVEffect(mSelectedMV, mSelectedMask);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return mMVArray != null ? mMVArray.length() + 1 : 0;
        }
    }

    /**
     * ????????????????????????
     */
    private AudioMixSettingDialog.OnAudioVolumeChangedListener mOnAudioVolumeChangedListener = new AudioMixSettingDialog.OnAudioVolumeChangedListener() {
        @Override
        public void onAudioVolumeChanged(int fgVolume, int bgVolume) {
            Log.i(TAG, "fg volume: " + fgVolume + " bg volume: " + bgVolume);
            mShortVideoEditor.setAudioMixVolume(fgVolume / 100f, bgVolume / 100f);
            mIsMuted = fgVolume == 0;
            mMuteButton.setImageResource(mIsMuted ? R.mipmap.btn_mute : R.mipmap.btn_unmute);
        }
    };

    /**
     * ????????????????????????
     */
    private AudioMixSettingDialog.OnPositionSelectedListener mOnPositionSelectedListener = new AudioMixSettingDialog.OnPositionSelectedListener() {
        @Override
        public void onPositionSelected(long position) {
            Log.i(TAG, "selected position: " + position);
            mShortVideoEditor.setAudioMixFileRange(position, position + mMixDuration);
        }
    };
}

