package com.lemma.lemmasignagesdk.mediaplayer;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import androidx.annotation.NonNull;

import com.lemma.lemmasignagesdk.common.LMLog;
import com.lemma.lemmasignagesdk.common.LMUtils;

import java.util.Timer;
import java.util.TimerTask;


public class LMVideoPlayerView extends FrameLayout implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener,
        MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnVideoSizeChangedListener {

    private static final int SEEK_BAR_LEFT_RIGHT_MARGIN = -15;
    private static final int SEEK_BAR_BOTTOM_MARGIN = 0;
    private static final String TAG = "LMVideoPlayerView";
    private static final double PROGRESS_UPDATE_DELAY = 0.5;
    private boolean onStartNotified = false;
    private SurfaceView surfaceView;
    private MediaPlayer mediaPlayer;
    private LMVideoPlayerListener listener;
    private Timer timer;
    private boolean autoPlayOnForeground;
    private boolean isMute;
    private SeekBar seekBar;

    public LMVideoPlayerView(Context context) {
        super(context);
        initVideoView();
        init();
    }

    private SeekBar createSeekBar() {
        SeekBar seekBar = new SeekBar(getContext());
        seekBar.setThumb(null);
        seekBar.getProgressDrawable().setColorFilter(getResources().getColor(android.R.color.holo_orange_light), PorterDuff.Mode.SRC_ATOP);

        seekBar.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        return seekBar;
    }


    private void init() {

        LayoutParams seekBarParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                LMUtils.convertDpToPixel(3));
        seekBarParams.gravity = Gravity.BOTTOM;
        seekBarParams.leftMargin = LMUtils.convertDpToPixel(SEEK_BAR_LEFT_RIGHT_MARGIN);
        seekBarParams.rightMargin = LMUtils.convertDpToPixel(SEEK_BAR_LEFT_RIGHT_MARGIN);
        seekBarParams.bottomMargin = LMUtils.convertDpToPixel(SEEK_BAR_BOTTOM_MARGIN);
        // Add and align seek bar
        seekBar = createSeekBar();
//        addView(seekBar, seekBarParams);
    }


    private void initVideoView() {
        surfaceView = new SurfaceView(getContext());
        //Add Surface holder callbacks
        surfaceView.getHolder().addCallback(this);
//        LayoutParams layoutparams = new LayoutParams(LayoutParams.MATCH_PARENT,
//                LayoutParams.MATCH_PARENT);
//        layoutparams.gravity = Gravity.CENTER;
//        addView(surfaceView, layoutparams);



        RelativeLayout relativeLayout = new RelativeLayout(getContext());
        RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        relativeParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        relativeParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        relativeParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        relativeParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        relativeLayout.addView(surfaceView, relativeParams);


        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER;

        addView(relativeLayout, params);
    }

    public void setAutoPlayOnForeground(boolean autoPlayOnForeground) {
        this.autoPlayOnForeground = autoPlayOnForeground;
    }

    /**
     * Loads the video for given Uri
     */
    public void load(Uri uri) {
        prepareVideo(uri);
    }

    private void prepareVideo(Uri uri) {
        initMediaPlayer();
        try {
            mediaPlayer.setDataSource(getContext(), uri);
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            LMLog.d(TAG, e.getMessage());
            if (null != listener) {
                listener.onFailure(100, e.getMessage());
            }
        }
    }

    private void initMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnVideoSizeChangedListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mediaPlayer, int w, int e) {
                //The player just pushed the first video frame for rendering after play/resume.
                if (w == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START && !onStartNotified) {
                    listener.onStart();
                    onStartNotified = true;
                    return true;
                }
                return false;
            }
        });
    }

    public void play() {
        if (null != mediaPlayer) {
            mediaPlayer.start();
            if (null != seekBar) {
                seekBar.setMax(getMediaDuration());
            }
        } else {
            LMLog.w(TAG, "mediaPlayer :" + null);
        }
    }

    public void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        } else {
            LMLog.w(TAG, "mediaPlayer :" + mediaPlayer);
        }
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public void setListener(@NonNull LMVideoPlayerListener listener) {
        this.listener = listener;
    }

    public void stop() {
        stopProgressTimer();
        if (null != mediaPlayer) {
            mediaPlayer.stop();
        }
    }

    public void seekTo(int position) {
        if (null != mediaPlayer) {
            mediaPlayer.seekTo(position);
        }
    }

    public int getSeekPosition() {
        if (null != mediaPlayer) {
            return mediaPlayer.getCurrentPosition();
        } else {
            return 0;
        }
    }

    /**
     * Mute audio of VideoPlayer, notifies it to video controller
     */
    public void mute() {
        if (null != mediaPlayer && mediaPlayer.isPlaying()) {
            isMute = true;
            mediaPlayer.setVolume(0, 0);
        } else {
            LMLog.w(TAG, "mediaPlayer :" + mediaPlayer);
        }
    }

    public void unMute() {
        if (null != mediaPlayer && mediaPlayer.isPlaying()) {
            isMute = false;
            mediaPlayer.setVolume(1, 1);
        } else {
            LMLog.w(TAG, "mediaPlayer :" + mediaPlayer);
        }
    }

    public boolean isMute() {
        return isMute;
    }

    public int getMediaDuration() {
        if (null != mediaPlayer) {
            return mediaPlayer.getDuration();
        } else {
            return 0;
        }
    }

    /**
     * Cleanup
     */
    public void destroy() {
        stop();
        removeAllViews();
        surfaceView = null;
        if (null != mediaPlayer) {
            mediaPlayer.release();
        }
        mediaPlayer = null;
        listener = null;
    }

    private void stopProgressTimer() {
        if (null != timer) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        if (null != listener) {
            listener.onBufferUpdate(percent);
        }
    }

    /**
     * Callback invoked once the media player has completed it playback
     * also notifies it to reference of LMVideoPlayerListener
     *
     * @param mp instance of media player
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        if (null != listener) {
            listener.onCompletion();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        String errorMessage;
        switch (extra) {
            case MediaPlayer.MEDIA_ERROR_IO: {
                errorMessage = "MEDIA_ERROR_IO";
                break;
            }
            case MediaPlayer.MEDIA_ERROR_MALFORMED: {
                errorMessage = "MEDIA_ERROR_MALFORMED";
                break;
            }
            case MediaPlayer.MEDIA_ERROR_UNSUPPORTED: {
                errorMessage = "MEDIA_ERROR_UNSUPPORTED";
                break;
            }
            case MediaPlayer.MEDIA_ERROR_TIMED_OUT: {
                errorMessage = "MEDIA_ERROR_TIMED_OUT";
                break;
            }
            default: {
                errorMessage = "error message not found!";
                break;
            }
        }
        LMLog.e(TAG, "errorCode: " + extra + ", errorMsg:" + errorMessage);
        if (null != listener) {
            listener.onFailure(extra, errorMessage);
        }
        return true;
    }

    /**
     * Called when the media content is available for playback.
     * also notifies it to reference of LMVideoPlayerListener
     *
     * @param mp current instance of media player
     */
    @Override
    public void onPrepared(MediaPlayer mp) {
        if (null != listener) {
            listener.onReady(this);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (null != mediaPlayer) {
            mediaPlayer.setDisplay(holder);
            startProgressTimer();
        }
        if (autoPlayOnForeground) {
            play();
        }
    }

    private void startProgressTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (null != seekBar && null != mediaPlayer) {
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                }
            }
        }, 0, (long) (1000 * PROGRESS_UPDATE_DELAY));
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // No Action required
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stopProgressTimer();
        pause();
        if (null != mediaPlayer) {
            mediaPlayer.setDisplay(null);
        }
    }

    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        setVideoSize(mp);
//        setFitToFillAspectRatio(mp, width, height);

    }

    public static int[] getScreenSizeInlcudingTopBottomBar(Context context) {
        int [] screenDimensions = new int[2]; // width[0], height[1]
        int x, y, orientation = context.getResources().getConfiguration().orientation;
        WindowManager wm = ((WindowManager)
                context.getSystemService(Context.WINDOW_SERVICE));
        Display display = wm.getDefaultDisplay();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point screenSize = new Point();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                display.getRealSize(screenSize);
                x = screenSize.x;
                y = screenSize.y;
            } else {
                display.getSize(screenSize);
                x = screenSize.x;
                y = screenSize.y;
            }
        } else {
            x = display.getWidth();
            y = display.getHeight();
        }

        screenDimensions[0] = orientation == Configuration.ORIENTATION_PORTRAIT ? x : y; // width
        screenDimensions[1] = orientation == Configuration.ORIENTATION_PORTRAIT ? y : x; // height

        return screenDimensions;
    }

    private void setFitToFillAspectRatio(MediaPlayer mp, int videoWidth, int videoHeight)
    {
        if(mp != null)
        {
            WindowManager wm = ((WindowManager)
                    getContext().getSystemService(Context.WINDOW_SERVICE));

            Integer screenWidth = getScreenSizeInlcudingTopBottomBar(getContext())[0];
            Integer screenHeight = getScreenSizeInlcudingTopBottomBar(getContext())[1];
            android.view.ViewGroup.LayoutParams videoParams = getLayoutParams();


            if (videoWidth > videoHeight)
            {
                videoParams.width = screenWidth;
                videoParams.height = screenWidth * videoHeight / videoWidth;
            }
            else
            {
                videoParams.width = screenHeight * videoWidth / videoHeight;
                videoParams.height = screenHeight;
            }


            setLayoutParams(videoParams);
        }
    }


    private void setVideoSize(MediaPlayer mediaPlayer) {

        // Get the dimensions of the video
        int videoWidth = mediaPlayer.getVideoWidth();
        int videoHeight = mediaPlayer.getVideoHeight();
        float videoProportion = (float) videoWidth / (float) videoHeight;

        // Get the width of the screen
        int viewWidth = this.getWidth();
        int viewHeight = this.getHeight();
        float screenProportion = (float) viewWidth / (float) viewHeight;

        // Get the SurfaceView layout parameters
        ViewGroup.LayoutParams layoutParams = surfaceView.getLayoutParams();
        if (videoProportion > screenProportion) {
            layoutParams.width = viewWidth;
            layoutParams.height = (int) ((float) viewWidth / videoProportion);
        } else {
            layoutParams.width = (int) (videoProportion * (float) viewHeight);
            layoutParams.height = viewHeight;
        }
        // Commit the layout parameters
        surfaceView.setLayoutParams(layoutParams);

//        // Get the dimensions of the video
//        int videoWidth = mediaPlayer.getVideoWidth();
//        int videoHeight = mediaPlayer.getVideoHeight();
//        float videoProportion = (float) videoWidth / (float) videoHeight;
//
//        // Get the width of the screen
//        int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
//        int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
//        float screenProportion = (float) screenWidth / (float) screenHeight;
//
//        // Get the SurfaceView layout parameters
//        ViewGroup.LayoutParams layoutParams = surfaceView.getLayoutParams();
//        if (videoProportion > screenProportion) {
//            layoutParams.width = screenWidth;
//            layoutParams.height = (int) ((float) screenWidth / videoProportion);
//        } else {
//            layoutParams.width = (int) (videoProportion * (float) screenHeight);
//            layoutParams.height = screenHeight;
//        }
//        // Commit the layout parameters
//        surfaceView.setLayoutParams(layoutParams);
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (null != mediaPlayer) {
            setVideoSize(mediaPlayer);
        }
    }

    /**
     * Video Player callback methodss.
     */
    public interface LMVideoPlayerListener {
        void onReady(LMVideoPlayerView player);

        void onFailure(int errorCode, String errorMessage);

        void onBufferUpdate(int buffer);

        void onCompletion();

        void onStart();

        void onPause();

        void onProgressUpdate(int seekPosition);
    }
}