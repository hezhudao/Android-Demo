package SpeechSearch.tar;

import java.util.ArrayList;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

public class SSDelegate {
	
	
	private SpeechRecognizer speechEnter;
	private static final String TAG = "SpeechSearch";
	private static String COLORSAME="#FFFFFF";
	private static String COLORDIEFFER="#FF0000";
    private static String SIZESAME ="3";
    private static String SIZEDIFFER="5";
	public static String PACKAGENAME = "com.SpeechSearch.tar.SpeechSearch";
	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
	
	private TextView testView;
	private String mSourceString;
	
	/**
	 * @param mSourceString the mSourceString to set
	 */
	public void setmSourceString(String mSourceString) {
		
		this.mSourceString = mSourceString;
	}


	public SSDelegate()
	{
//		speechEnter = SpeechRecognizer.createSpeechRecognizer(aContext);
//		speechEnter.setRecognitionListener((RecognitionListener) new listener());
	}
	
	public void setView(TextView aTextView)
	{
		testView = aTextView;
	}
//	class listener implements RecognitionListener {
//		public void onReadyForSpeech(Bundle params) {
//			Log.d(TAG, "onReadyForSpeech");
//		}
//
//		public void onBeginningOfSpeech() {
//			Log.d(TAG, "onBeginningOfSpeech");
//		}
//
//		public void onRmsChanged(float rmsdB) {
//			Log.d(TAG, "onRmsChanged" + rmsdB);
//		}
//
//		public void onBufferReceived(byte[] buffer) {
//			Log.d(TAG, "onBufferReceived");
//		}
//
//		public void onEndOfSpeech() {
//			Log.d(TAG, "onEndofSpeech");
//		}
//
//		public void onError(int error) {
//			Log.d(TAG, "error " + error);
//		}
//
//		public void onResults(Bundle results) {
//
//			ArrayList<String> data = results
//					.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
//			String mySpeaking=null;
//			mySpeaking = data.get(0);
//			testView.setText(Html.fromHtml(buildSpeakArray("Hello world my god",
//					mySpeaking)));
//			Log.d(TAG, "onResults " + mySpeaking);
//			
//		}
//
//		public void onPartialResults(Bundle partialResults) {
//			Log.d(TAG, "onPartialResults");
//		}
//
//		public void onEvent(int eventType, Bundle params) {
//			Log.d(TAG, "onEvent " + eventType);
//		}
//	}
    public void startSearchbyInternet(Activity aParent)
    {
//    	Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
//				RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
//		intent.putExtra("calling_package",PACKAGENAME);
//		speechEnter.startListening(intent);
    	Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
    	intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
    	RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
    	aParent.startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    	

    }
    
    public String buildSpeakArray(String analyString, String destString) {
		String resultName[] = null;
		boolean resultInclude[];
		String checkResult;
		analyString = analyString.toLowerCase();
		if (analyString.length() > 0) {
			resultName = analyString.split(" ");
		}
		if (resultName == null)
			return null;
		// resultInclude = new boolean[resultName.length];
		String tempHtmlString = new String();
		for (int i = 0; i < resultName.length; i++) {
			Log.d(TAG, "onEvent111 " + resultName[i]);
			if (destString.contains(resultName[i])) {

				tempHtmlString += "<font color="+COLORSAME+">" + resultName[i]
						+ " </font>";
			} else
				tempHtmlString += "<font color="+COLORDIEFFER+">" + resultName[i]
						+ " </font>";
		}

		return tempHtmlString;
	}
    
	public void SearchResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

		// Fill the list view with the strings the recognizer thought it could have heard

			ArrayList matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			String mySpeaking=null;
			
			mySpeaking = (String)matches.get(0);
			Log.v("XXXXXXXXXX", mySpeaking);
			testView.setText(Html.fromHtml(buildSpeakArray(mSourceString,
					mySpeaking)));
			

		}

		

	}
    
}
