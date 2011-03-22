/**
 * 
 */
package com.ixonos.threads;


import com.ixonos.assest.CaptionsUtil;
import com.ixonos.ui.MianActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * @author jashewe
 * 
 */
public class MediaControlThread extends Thread {

	private CaptionsUtil mUtil;
	private int mIndex;
	private Handler mHandler;
	private long mCurrentMillSec;

	public MediaControlThread(CaptionsUtil util, int index, Handler handler,
			long currentMillSec) {

		this.mUtil = util;
		this.mIndex = index;
		this.mHandler = handler;
		this.mCurrentMillSec = currentMillSec;
	}

	@Override
	public void run() {

		Log.v("MediaControlThread", ""+mUtil.getSentences().get(mIndex).getToTime()+" "+mCurrentMillSec+" "+mIndex);
		long sleepMilSec = mUtil.getSentences().get(mIndex+1).getFromTime()
				- mCurrentMillSec;
		try {

			if(sleepMilSec>0){

				Thread.sleep(sleepMilSec);
				Message message = new Message();
				message.what = MianActivity.PAUSE_MSG;
				Bundle data = new Bundle();

				// set the current index into data:
				data.putInt(MianActivity.CAPTION_MSG, mIndex);
				message.setData(data);
				mHandler.sendMessage(message);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private int getNowSentenceIndex(long t) {

		for (int i = 0; i < mUtil.getSentences().size(); i++) {

			if (mUtil.getSentences().get(i).isInTime(t)) {

				return i;
			}
		}
		return -1;
	}
}
