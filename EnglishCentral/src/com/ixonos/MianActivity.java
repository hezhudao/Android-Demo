package com.ixonos;

import java.util.Timer;
import java.util.TimerTask;

import android.R.integer;
import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MianActivity extends Activity implements OnClickListener,
		OnBufferingUpdateListener, OnCompletionListener, OnPreparedListener,
		OnVideoSizeChangedListener, SurfaceHolder.Callback, OnErrorListener {

	private int mVideoWidth;
	private int mVideoHeight;
	private MediaPlayer mMediaPlayer;
	private SurfaceView mPreview;
	private SurfaceHolder holder;
	private String path;
	private Bundle extras;
	private Timer timer;
	private TimerTask timerTask;
	private ListView mListView;
	private CaptionsUtil util;
	private Button mStartButton, mPauseButton;
	private Button mAutoPauseButton;
	private LinearLayout mBottomLinearLayout;
	private TextView mCaptionTextView;
	private int index = 0;
	private Boolean isAutoPause = false;
	private boolean mIsVideoSizeKnown = false;
	private boolean mIsVideoReadyToBePlayed = false;

	private static final String TAG = "MianActivity";
	private static final String MEDIA = "media";
	private static final String CAPTION_MSG = "CAPTION_MSG";
	private static final int LOCAL_AUDIO = 1;
	private static final int STREAM_AUDIO = 2;
	private static final int RESOURCES_AUDIO = 3;
	private static final int LOCAL_VIDEO = 4;
	private static final int STREAM_VIDEO = 5;
	private static final int PROGRESS_MSG = 6;
	private static final int PAUSE_MSG = 7;
	private static final int HIDDEN_MSG = 8;
	private ReportProgressThread thread;

	/**
	 * 
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.main);

		setupView();

		util = new CaptionsUtil(
				"/sdcard/Nokia CTO Rich Green talks about MeeGo.srt");
		// mListView.setAdapter(new CaptionsAdapter(this, util.getSentences()));
	}

	/**
	 * setupView
	 */
	private void setupView() {

		mPreview = (SurfaceView) findViewById(R.id.main_surfaceView);
		mPreview.setOnClickListener(this);
		holder = mPreview.getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mListView = (ListView) findViewById(R.id.main_srt_listview);
		mStartButton = (Button) findViewById(R.id.main_play_Button);
		mStartButton.setOnClickListener(this);
		mPauseButton = (Button) findViewById(R.id.main_pause_Button);
		mPauseButton.setOnClickListener(this);
		mAutoPauseButton = (Button) findViewById(R.id.main_autopause_Button);
		mAutoPauseButton.setOnClickListener(this);
		mBottomLinearLayout = (LinearLayout) findViewById(R.id.bottom_linear);
		mCaptionTextView = (TextView) findViewById(R.id.main_caption_Text);

		ViewThread viewThread = new ViewThread();
		viewThread.setDaemon(true);
		viewThread.start();
	}

	private void playVideo(Integer Media) {
		doCleanUp();
		try {

			switch (Media) {
			case LOCAL_VIDEO:
				/*
				 * TODO: Set the path variable to a local media file path.
				 */
				path = "/sdcard/ice-age-4.mp4";
				if (path == "") {
					// Tell the user to provide a media file URL.
					Toast.makeText(
							MianActivity.this,
							"Please edit MediaPlayerDemo_Video Activity, "
									+ "and set the path variable to your media file path."
									+ " Your media file must be stored on sdcard.",
							Toast.LENGTH_LONG).show();

				}
				break;
			case STREAM_VIDEO:
				/*
				 * TODO: Set path variable to progressive streamable mp4 or 3gpp
				 * format URL. Http protocol should be used. Mediaplayer can
				 * only play "progressive streamable contents" which basically
				 * means: 1. the movie atom has to precede all the media data
				 * atoms. 2. The clip has to be reasonably interleaved.
				 */
				path = "";
				if (path == "") {
					// Tell the user to provide a media file URL.
					Toast.makeText(
							MianActivity.this,
							"Please edit MediaPlayerDemo_Video Activity,"
									+ " and set the path variable to your media file URL.",
							Toast.LENGTH_LONG).show();

				}

				break;

			}

			// Create a new media player and set the listeners
			mMediaPlayer = new MediaPlayer();
			mMediaPlayer.setDataSource(path);
			mMediaPlayer.setDisplay(holder);
			mMediaPlayer.prepare();
			mMediaPlayer.setOnBufferingUpdateListener(this);
			mMediaPlayer.setOnCompletionListener(this);
			mMediaPlayer.setOnPreparedListener(this);
			mMediaPlayer.setOnVideoSizeChangedListener(this);
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mMediaPlayer.setOnErrorListener(this);

		} catch (Exception e) {
			Log.e(TAG, "error: " + e.getMessage(), e);
		}
	}

	public void onBufferingUpdate(MediaPlayer arg0, int percent) {
		Log.d(TAG, "onBufferingUpdate percent:" + percent);

	}

	public void onCompletion(MediaPlayer arg0) {
		Log.d(TAG, "onCompletion called");
		doCleanUp();

	}

	public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
		Log.v(TAG, "onVideoSizeChanged called");
		if (width == 0 || height == 0) {
			Log.e(TAG, "invalid video width(" + width + ") or height(" + height
					+ ")");
			return;
		}
		mIsVideoSizeKnown = true;
		mVideoWidth = width;
		mVideoHeight = height;
		if (mIsVideoReadyToBePlayed && mIsVideoSizeKnown) {
			startVideoPlayback();
		}
	}

	public void onPrepared(MediaPlayer mediaplayer) {
		Log.d(TAG, "onPrepared called");
		mIsVideoReadyToBePlayed = true;
		if (mIsVideoReadyToBePlayed && mIsVideoSizeKnown) {
			startVideoPlayback();

		}
	}

	public void surfaceChanged(SurfaceHolder surfaceholder, int i, int j, int k) {
		Log.d(TAG, "surfaceChanged called");

	}

	public void surfaceDestroyed(SurfaceHolder surfaceholder) {
		Log.d(TAG, "surfaceDestroyed called");
	}

	public void surfaceCreated(SurfaceHolder holder) {
		Log.d(TAG, "surfaceCreated called");
		playVideo(LOCAL_VIDEO);

	}

	@Override
	protected void onPause() {
		super.onPause();
		releaseMediaPlayer();
		doCleanUp();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		releaseMediaPlayer();
		doCleanUp();
	}

	private void releaseMediaPlayer() {
		if (mMediaPlayer != null) {
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
	}

	private void doCleanUp() {
		mVideoWidth = 0;
		mVideoHeight = 0;
		mIsVideoReadyToBePlayed = false;
		mIsVideoSizeKnown = false;

		if (thread != null) {

			// interrupt thread and clean it task:
			thread.interrupt();
		}
		index = 0;
		mCaptionTextView.setText(null);
	}

	private void startVideoPlayback() {
		Log.v(TAG, "startVideoPlayback");
		holder.setFixedSize(mVideoWidth, mVideoHeight);

		mMediaPlayer.start();

		thread = new ReportProgressThread();
		thread.setDaemon(true);
		thread.start();

		// mListView.requestFocusFromTouch();
		// mListView.setSelection(0);
		// startProgressUpdate();
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.main_play_Button:
			mMediaPlayer.start();
			mIsVideoReadyToBePlayed = true;
			Log.v(TAG, mMediaPlayer.isPlaying() + "");

			thread = new ReportProgressThread();
			thread.setDaemon(true);
			thread.start();
			break;
		case R.id.main_pause_Button:
			// mMediaPlayer.pause();
			Log.v(TAG, mMediaPlayer.isPlaying() + "");
			break;
		case R.id.main_autopause_Button:
			isAutoPause = !isAutoPause;
			break;
		case R.id.main_surfaceView:

			// set the control linearlayout to a right visibility state:
			mBottomLinearLayout.setVisibility(mBottomLinearLayout
					.getVisibility() == View.VISIBLE ? View.INVISIBLE
					: View.VISIBLE);
			ViewThread viewThread = new ViewThread();
			viewThread.setDaemon(true);
			viewThread.start();
			break;
		default:
			break;
		}
	}

	private Handler handler = new Handler() {

		public void handleMessage(Message msg) {

			switch (msg.what) {
			case PROGRESS_MSG:

				if (mIsVideoReadyToBePlayed && isAutoPause) {

					// auto pause:
					mMediaPlayer.pause();
				}
				if (!isAutoPause
						&& msg.getData().getInt(CAPTION_MSG) < util
								.getSentences().size() - 1) {

					// auto play:
					thread = new ReportProgressThread();
					thread.setDaemon(true);
					thread.start();
				}

				// mListView.requestFocusFromTouch();
				// mListView.setSelection(msg.what);

				// set the current captions to the caption text;
				mCaptionTextView.setText(util.getSentences()
						.get(msg.getData().getInt(CAPTION_MSG)).getContent());
				break;

			default:
				break;
			}

			super.handleMessage(msg);
		}

	};

	private class ViewThread extends Thread {
		@Override
		public void run() {

			try {

				// sleep current thread 5 seconds:
				Thread.sleep(5000);

				runOnUiThread(new Runnable() {

					@Override
					public void run() {

						// hide the control panel:
						mBottomLinearLayout.setVisibility(View.INVISIBLE);
					}
				});
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private class ReportProgressThread extends Thread {

		@Override
		public void run() {
			Log.v(TAG, "in My Thread");

			final int size = util.getSentences().size();
			Sentence sentence = util.getSentences().get(index);
			Log.v(TAG, "getToTime" + sentence.getToTime()
					+ "sentence.getFromTime:" + sentence.getFromTime());
			try {
				if (index == 0) {

					// the video was played:
					Thread.sleep(sentence.getFromTime());
				} else {

					// clear the captions text:
					Thread.sleep(util.getSentences().get(index - 1).getToTime()
							- util.getSentences().get(index - 1).getFromTime());
					runOnUiThread(new Runnable() {

						@Override
						public void run() {

							// clear the caption's text
							mCaptionTextView.setText(null);
						}
					});

					// need to validate the index to let the index not
					// outofindex and the current thread is in a valid state,
					// if the video was paused or closed. the index will be set
					// to zero.
					if (index > 0)
						Thread.sleep(sentence.getFromTime()
								- util.getSentences().get(index - 1)
										.getToTime());
				}

				// send a message to main handler to update the captions:
				Message message = new Message();
				message.what = PROGRESS_MSG;
				Bundle data = new Bundle();

				// set the current index into data:
				data.putInt(CAPTION_MSG, index);
				message.setData(data);
				handler.sendMessage(message);

				if (index < size - 1)
					index++;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {

		Log.v(TAG, "Error");
		return false;
	}

}