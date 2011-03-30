package com.ixonos.ui;

import java.util.Timer;
import java.util.TimerTask;

import SpeechSearch.tar.SSDelegate;
import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.ixonos.assest.CaptionsUtil;
import com.ixonos.assest.Sentence;
import com.ixonos.threads.MediaControlThread;

public class MianActivity extends Activity implements OnClickListener,
		OnBufferingUpdateListener, OnCompletionListener, OnPreparedListener,
		OnVideoSizeChangedListener, SurfaceHolder.Callback, OnErrorListener,
		ViewSwitcher.ViewFactory {

	private int mVideoWidth;
	private int mVideoHeight;
	private MediaPlayer mMediaPlayer;
	private SurfaceView mPreview;
	private SurfaceHolder holder;
	private String path;
	private Bundle extras;
	private Timer timer;
	private TimerTask timerTask;
	private CaptionsUtil util;
	private Button mStartButton, mNextButton, mRecordButton, mPreviousButton;
	private Button mAutoPauseButton;
	private TextSwitcher mTextPlayer;
	private TextView mRecordTextView;
	private int index = -1;
	private Boolean isAutoPause = false;
	private boolean mIsVideoSizeKnown = false;
	private boolean mIsVideoReadyToBePlayed = false;
	private boolean mIsPaused = false;
	// private MediaControlThread mediaControlThread;
	private SSDelegate sDelegate;

	private static final String TAG = "MianActivity";
	public static final String MEDIA = "media";
	public static final int LOCAL_AUDIO = 1;
	public static final int STREAM_AUDIO = 2;
	public static final int RESOURCES_AUDIO = 3;
	public static final int LOCAL_VIDEO = 4;
	public static final int STREAM_VIDEO = 5;
	public static final int PROGRESS_MSG = 6;
	public static final int HIDDEN_MSG = 8;
	public static final int CLEAR_CAPTION_MSG = 9;
	public static final int PAUSE_MSG = 7;
	public static final String CAPTION_MSG = "CAPTION_MSG";

	/**
	 * 
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.main);

		setupView();

		util = new CaptionsUtil(
				"/sdcard/Nokia CTO Rich Green talks about MeeGo.srt");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		sDelegate.SearchResult(requestCode, resultCode, data);
		super.onActivityResult(requestCode, resultCode, data);

		mRecordTextView.setText(sDelegate.getAccuration() * 100 + "%");
		mRecordTextView.setTextSize(24);
		mRecordTextView.setTextColor(Color.BLUE);
		Display display = getWindowManager().getDefaultDisplay();
		Animation a = new TranslateAnimation(0.0f, 0.0f, display.getHeight(),
				0.0f);
		a.setDuration(1000);
		a.setStartOffset(1000);
		a.setFillAfter(true);
		a.setInterpolator(AnimationUtils.loadInterpolator(this,
				android.R.anim.bounce_interpolator));
		mRecordTextView.startAnimation(a);
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
		mStartButton = (Button) findViewById(R.id.main_play_Button);
		mStartButton.setOnClickListener(this);
		mNextButton = (Button) findViewById(R.id.main_next_Button);
		mNextButton.setOnClickListener(this);
		mAutoPauseButton = (Button) findViewById(R.id.main_autopause_Button);
		mAutoPauseButton.setOnClickListener(this);
		mRecordButton = (Button) findViewById(R.id.main_record_Button);
		mRecordButton.setOnClickListener(this);
		mPreviousButton = (Button) findViewById(R.id.main_previous_Button);
		mPreviousButton.setOnClickListener(this);
		mTextPlayer = (TextSwitcher) findViewById(R.id.tvTextPlayer);
		mTextPlayer.setFactory(this);
		Animation in = AnimationUtils.loadAnimation(this,
				android.R.anim.slide_in_left);
		Animation out = AnimationUtils.loadAnimation(this,
				android.R.anim.slide_out_right);
		mTextPlayer.setInAnimation(in);
		mTextPlayer.setOutAnimation(out);

		sDelegate = new SSDelegate();
		sDelegate.setView(mTextPlayer);

		mRecordTextView = (TextView) findViewById(R.id.main_record_Text);

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
		this.finish();
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
		Log.v(TAG, "onPause");
		// releaseMediaPlayer();
		// doCleanUp();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onRestart()
	 */
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		Log.v(TAG, "onRestart");

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.v(TAG, "onDestroy");

		doCleanUp();
		releaseMediaPlayer();
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

		index = -1;
		mIsPaused = false;
		isAutoPause = false;
		mStartButton.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.play));
		mTextPlayer.setText(null);
		stopProgressUpdate();
	}

	private void startVideoPlayback() {
		Log.v(TAG, "startVideoPlayback");
		holder.setFixedSize(mVideoWidth, mVideoHeight);

		mMediaPlayer.start();
		mStartButton.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.pause));
		startProgressUpdate();
	}

	private boolean mIsTriggeredByUser = false;

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.main_play_Button:
			if (index >= util.getSentences().size()) {
				mMediaPlayer.start();
				return;
			}
			stopProgressUpdate();
			if (!mIsPaused) {

				mIsTriggeredByUser = true;
				mMediaPlayer.pause();
				mIsPaused = true;
				mStartButton.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.play));
			} else {

				mIsPaused = false;
				mStartButton.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.pause));

				if (isAutoPause) {

					// mediaControlThread = new MediaControlThread(util, index,
					// handler, mMediaPlayer.getCurrentPosition());
					// mediaControlThread.start();
					mTextPlayer.setText(util.getSentences().get(index)
							.getContent());

					Sentence sentence = util.getSentences().get(index);

					Log.v(TAG,
							sentence.getFromTime() + ":" + sentence.getToTime()
									+ " : " + mMediaPlayer.getCurrentPosition()
									+ "");
					startAutoPauseTimer(sentence.getToTime()
							- mMediaPlayer.getCurrentPosition());
				} else {
					mTextPlayer.setText(util.getSentences().get(index)
							.getContent());

					startProgressUpdate();
				}
				mMediaPlayer.start();
			}
			// if (index > 0 && isAutoPause)
			// mTextPlayer
			// .setText(util.getSentences().get(index).getContent());
			break;
		case R.id.main_autopause_Button:
			if (index == -1)
				return;
			if (isAutoPause) {

				// mediaControlThread.interrupt();
				mAutoPauseButton.setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.auto_play));
				stopProgressUpdate();
				startProgressUpdate();
			} else {
				stopProgressUpdate();
				Sentence sentence = util.getSentences().get(index);
				startAutoPauseTimer(sentence.getToTime()
						- mMediaPlayer.getCurrentPosition());
				Log.v(TAG, "" + (sentence.getToTime() - sentence.getFromTime())
						+ "");
				// mediaControlThread = new MediaControlThread(util, index,
				// handler, mMediaPlayer.getCurrentPosition());
				// mediaControlThread.start();

				mAutoPauseButton.setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.auto_pause));
			}
			isAutoPause = !isAutoPause;
			Log.v(TAG, "" + isAutoPause);
			break;
		case R.id.main_previous_Button:
			// if (!mIsPaused)
			// index--;
			// // else if(mIsPaused && !mIsTriggeredByUser){
			// // index = index - 2;
			// // }
			// else {
			// if (mIsTriggeredByUser)
			// index = index - 1;
			// else {
			// if (index > 1)
			// index = index - 2;
			// else
			// index = 0;
			// }
			//
			// }
			// Log.v(TAG, "mIsTriggeredByUser" + mIsTriggeredByUser);
			// // else if (mIsPaused && index > 1)
			// // index = index - 2;
			//
			// if (index < 0) {
			// index = 0;
			// } else {
			// seekVideo();
			// }

			final int currentIndex = getNowSentenceIndex(mMediaPlayer
					.getCurrentPosition());
			// if(util.getSentences().get(index).getFromTime()==util.getSentences().get(currentIndex-1).getFromTime()){
			// currentIndex--;
			// }
			if (currentIndex != -1)
				index = currentIndex - 1;
			else {
				index -= 2;
			}
			if (index < 0) {
				index = 0;
			}
			Log.v(TAG, "main_next_Button : " + index + "currentIndex:"
					+ currentIndex);
			seekVideo();

			stopProgressUpdate();
			if (!isAutoPause) {

				startProgressUpdate();
			} else {

				if (!mIsPaused) {
					Sentence sentence = util.getSentences().get(index);
					startAutoPauseTimer(sentence.getToTime()
							- mMediaPlayer.getCurrentPosition());
				}
			}

			// if (mIsPaused)
			// stopProgressUpdate();
			// else if (isAutoPause) {
			// stopProgressUpdate();
			// Sentence sentence = util.getSentences().get(index);
			// startAutoPauseTimer(sentence.getToTime()
			// - mMediaPlayer.getCurrentPosition());
			// }
			break;
		case R.id.main_next_Button:
			Log.v(TAG, "mIsPaused" + mIsPaused + mIsTriggeredByUser);
			// if (!mIsPaused)
			// index++;
			// else if (mIsTriggeredByUser)
			// index++;
			// else if(mIsPaused && !mIsTriggeredByUser && isAutoPause)
			// index++;

			Log.v(TAG, "main_next_Button : " + index);
			final int current = getNowSentenceIndex(mMediaPlayer
					.getCurrentPosition());

			if (current != -1)
				index = current + 1;
			// index++;
			if (index >= util.getSentences().size()) {
				index--;
			} else {

				seekVideo();
				// if (mIsPaused)
				// stopProgressUpdate();
				// else if (isAutoPause) {
				//
				// stopProgressUpdate();
				// Sentence sentence = util.getSentences().get(index);
				// startAutoPauseTimer(sentence.getToTime()
				// - mMediaPlayer.getCurrentPosition());
				// }

				stopProgressUpdate();
				if (!isAutoPause) {

					startProgressUpdate();
				} else {

					if (!mIsPaused) {
						Sentence sentence = util.getSentences().get(index);
						startAutoPauseTimer(sentence.getToTime()
								- mMediaPlayer.getCurrentPosition());
					}
				}
			}

			break;

		case R.id.main_record_Button:
			Log.v(TAG, mMediaPlayer + "");
			if (!mIsPaused)
				mIsTriggeredByUser = true;
			stopProgressUpdate();
			mMediaPlayer.pause();
			mIsPaused = true;
			mStartButton.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.play));
			// if (!isAutoPause)
			// sDelegate.setmSourceString(util.getSentences().get(index)
			// .getContent());
			// else
			// sDelegate.setmSourceString(util.getSentences().get(index - 1)
			// .getContent());
			final int recordIndex = getNowSentenceIndex(mMediaPlayer
					.getCurrentPosition());
			Log.v(TAG, "msg" + recordIndex);
			if (recordIndex != -1)

				sDelegate.setmSourceString(util.getSentences().get(recordIndex)
						.getContent());
			else {
				if (index == util.getSentences().size())
					sDelegate.setmSourceString(util.getSentences().get(index)
							.getContent());
				else
					sDelegate.setmSourceString(util.getSentences()
							.get(index - 1 < 0 ? 0 : index - 1).getContent());
			}
			sDelegate.startSearchbyInternet(this);
			break;
		default:
			break;
		}
	}

	/**
	 * seek the video to a fixed time
	 */
	private void seekVideo() {
		Sentence sentence = util.getSentences().get(index);
		long fromtime = sentence.getFromTime();
		mMediaPlayer.seekTo((int) fromtime);
		mTextPlayer.setText(sentence.getContent());
		Log.v(TAG, "index:" + index + " " + sentence.getFromTime()
				+ mMediaPlayer.getCurrentPosition());
	}

	private Handler handler = new Handler() {

		public void handleMessage(Message msg) {

			switch (msg.what) {
			case PROGRESS_MSG:

				if (mIsVideoReadyToBePlayed) {

					mTextPlayer.setText(util.getSentences()
							.get(msg.getData().getInt(CAPTION_MSG))
							.getContent());
				}

				// if (mIsVideoReadyToBePlayed && isAutoPause) {
				// mIsTriggeredByUser = false;
				// // auto pause:
				// mMediaPlayer.pause();
				// mIsPaused = true;
				// mStartButton.setBackgroundDrawable(getResources()
				// .getDrawable(R.drawable.play));
				// }
				//
				// if (!isAutoPause) {
				// // set the current captions to the caption text;
				// mTextPlayer.setText(util.getSentences()
				// .get(msg.getData().getInt(CAPTION_MSG))
				// .getContent());
				// } else {
				// mTextPlayer.setText(util.getSentences()
				// .get(index > 0 ? index : 0).getContent());
				// // mTextPlayer.setText(util.getSentences()
				// // .get(msg.getData().getInt(CAPTION_MSG) - 1)
				// // .getContent());
				// }
				break;
			case CLEAR_CAPTION_MSG:

				// clear the caption text;
				mTextPlayer.setText(null);
				break;
			case PAUSE_MSG:

				mIsTriggeredByUser = false;
				stopProgressUpdate();
				mMediaPlayer.pause();
				mIsPaused = true;
				mStartButton.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.play));
				mTextPlayer.setText(util.getSentences()
						.get(msg.getData().getInt(CAPTION_MSG)).getContent());
				break;
			case AUTO_PAUSE_MSG:
				mMediaPlayer.pause();
				mIsPaused = true;
				mStartButton.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.play));
				Sentence sentence = util.getSentences().get(index - 1);
				Log.v(TAG,
						"AUTO_PAUSE_MSG: MP:"
								+ mMediaPlayer.getCurrentPosition()
								+ " caption" + sentence.getFromTime() + " "
								+ sentence.getToTime());
				break;
			default:
				break;
			}

			super.handleMessage(msg);
		}

	};

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {

		Log.v(TAG, "Error");
		return false;
	}

	private static final int AUTO_PAUSE_MSG = 0x10000;

	private void startAutoPauseTimer(final float timerInterval) {

		if (timerInterval < 0)
			return;

		if (null == timer) {

			if (null == timerTask) {

				timerTask = new TimerTask() {

					@Override
					public void run() {

						Log.v(TAG,
								"mMediaPlayer"
										+ mMediaPlayer.getCurrentPosition()
										+ "caption:"
										+ util.getSentences().get(index)
												.getFromTime());
						final float timeInterval = util.getSentences()
								.get(index).getToTime()
								- mMediaPlayer.getCurrentPosition();
						if (timeInterval > 0)
							try {
								Thread.sleep((long) timeInterval);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						index++;
						if (index >= util.getSentences().size())
							index--;
						Message message = new Message();
						message.what = AUTO_PAUSE_MSG;
						handler.sendMessage(message);
					}
				};
			}

			timer = new Timer(true);
			timer.schedule(timerTask, (long) timerInterval, Integer.MAX_VALUE); // set
																				// timer
			// duration
		}
	}

	private void startProgressUpdate() {

		if (null == timer) {

			if (null == timerTask) {

				timerTask = new TimerTask() {

					@Override
					public void run() {

						try {

							if (mMediaPlayer == null)
								return;

							long curent = mMediaPlayer.getCurrentPosition();
							int indexNow = getNowSentenceIndex(curent);

							if (index != indexNow && indexNow != -1) {
								Log.v(TAG, "index:" + index + "indexNow:"
										+ indexNow);
								// send a message to main handler to update the
								// captions:
								Message message = new Message();
								message.what = PROGRESS_MSG;
								Bundle data = new Bundle();

								// set the current index into data:
								data.putInt(CAPTION_MSG, indexNow);
								message.setData(data);
								handler.sendMessage(message);
								index = indexNow;
							} else if (indexNow == -1 && index != 0) {

								// send a message to main handler to update the
								// captions:
								Message message = new Message();
								message.what = CLEAR_CAPTION_MSG;

								// handler.sendMessage(message);
							}
						} catch (IllegalStateException e) {
							// TODO: handle exception
							e.printStackTrace();
						}
					}
				};
			}

			timer = new Timer(true);
			timer.schedule(timerTask, 0, 13); // set timer duration
		}
	}

	private int getNowSentenceIndex(long t) {

		for (int i = 0; i < util.getSentences().size(); i++) {

			if (util.getSentences().get(i).isInTime(t)) {

				return i;
			}
		}
		return -1;
	}

	private void stopProgressUpdate() {
		if (timer != null) {
			timerTask.cancel();
			timerTask = null;
			timer.cancel(); // Cancel timer
			timer.purge();
			timer = null;
			handler.removeMessages(PROGRESS_MSG);
			handler.removeMessages(AUTO_PAUSE_MSG);
			handler.removeMessages(PAUSE_MSG);
			handler.removeMessages(CLEAR_CAPTION_MSG);
		}
	}

	@Override
	public View makeView() {
		TextView tv = new TextView(this);
		tv.setGravity(Gravity.CENTER);
		tv.setTextSize(24);
		tv.setTextColor(Color.WHITE);
		return tv;
	}
}