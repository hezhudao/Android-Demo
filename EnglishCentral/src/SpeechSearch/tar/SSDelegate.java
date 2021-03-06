package SpeechSearch.tar;

import java.util.ArrayList;

import android.R.string;
import android.app.Activity;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.Html;
import android.util.Log;
import android.widget.TextSwitcher;

public class SSDelegate {

	private SpeechRecognizer speechEnter;
	private static final String TAG = "SpeechSearch";
	private static String COLORSAME = "#FFFFFF";
	private static String COLORDIEFFER = "#FF0000";
	private static String SIZESAME = "3";
	private static String SIZEDIFFER = "5";
	public static String PACKAGENAME = "com.SpeechSearch.tar.SpeechSearch";
	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;

	private TextSwitcher testView;
	private String mSourceString;

	/**
	 * @param mSourceString
	 *            the mSourceString to set
	 */
	public void setmSourceString(String mSourceString) {

		this.mSourceString = mSourceString;
	}

	public SSDelegate() {
		// speechEnter = SpeechRecognizer.createSpeechRecognizer(aContext);
		// speechEnter.setRecognitionListener((RecognitionListener) new
		// listener());
	}

	public void setView(TextSwitcher aTextView) {
		testView = aTextView;

	}

	// class listener implements RecognitionListener {
	// public void onReadyForSpeech(Bundle params) {
	// Log.d(TAG, "onReadyForSpeech");
	// }
	//
	// public void onBeginningOfSpeech() {
	// Log.d(TAG, "onBeginningOfSpeech");
	// }
	//
	// public void onRmsChanged(float rmsdB) {
	// Log.d(TAG, "onRmsChanged" + rmsdB);
	// }
	//
	// public void onBufferReceived(byte[] buffer) {
	// Log.d(TAG, "onBufferReceived");
	// }
	//
	// public void onEndOfSpeech() {
	// Log.d(TAG, "onEndofSpeech");
	// }
	//
	// public void onError(int error) {
	// Log.d(TAG, "error " + error);
	// }
	//
	// public void onResults(Bundle results) {
	//
	// ArrayList<String> data = results
	// .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
	// String mySpeaking=null;
	// mySpeaking = data.get(0);
	// testView.setText(Html.fromHtml(buildSpeakArray("Hello world my god",
	// mySpeaking)));
	// Log.d(TAG, "onResults " + mySpeaking);
	//
	// }
	//
	// public void onPartialResults(Bundle partialResults) {
	// Log.d(TAG, "onPartialResults");
	// }
	//
	// public void onEvent(int eventType, Bundle params) {
	// Log.d(TAG, "onEvent " + eventType);
	// }
	// }
	public void startSearchbyInternet(Activity aParent) {
		// Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		// intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
		// RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
		// intent.putExtra("calling_package",PACKAGENAME);
		// speechEnter.startListening(intent);
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

				tempHtmlString += "<font color=" + COLORSAME + ">"
						+ resultName[i] + " </font>";
			} else
				tempHtmlString += "<font color=" + COLORDIEFFER + ">"
						+ resultName[i] + " </font>";
		}

		return tempHtmlString;
	}

	public String buildSpeakArray(String analyString, String destString,
			boolean aSpecifical) {

		int rightwords = 0;
		accuration = 0;
		String resultName[] = null;
		String sourceName[] = null;
		String checkResult;

		analyString = analyString.toLowerCase();
		analyString = analyString.replaceAll("[,.]", " ");
		if (analyString.length() > 0) {
			resultName = analyString.split(" ");
		}
		if (resultName == null)
			return null;

		boolean resultInclude[] = new boolean[resultName.length];
		// resultInclude = new boolean[resultName.length];
		String tempHtmlString = new String();
		destString = destString.toLowerCase();
		if (destString.length() > 0) {
			sourceName = destString.split(" ");
		}

		for (int j = 0; j < resultName.length; j++) {

			for (int i = 0; i < sourceName.length; i++) {
				if (resultName[j].equals(sourceName[i])) {
					resultInclude[j] = true;
				}

			}
		}
		for (int i = 0; i < resultInclude.length; i++) {
			if (resultInclude[i]) {
				rightwords++;
				Log.v(TAG, rightwords + "");
				tempHtmlString += "<font color=" + COLORSAME + ">"
						+ resultName[i] + " </font>";
			} else
				tempHtmlString += "<font color=" + COLORDIEFFER + ">"
						+ resultName[i] + " </font>";
		}
		if (resultInclude.length > 0) {

			accuration = rightwords * 1.0f / resultInclude.length;
		} else {

			accuration = 0f;
		}
		return tempHtmlString;
	}

	private float accuration = 0f;

	public float getAccuration() {

		return accuration;
	}

	public void SearchResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == VOICE_RECOGNITION_REQUEST_CODE
				&& resultCode == Activity.RESULT_OK) {

			// Fill the list view with the strings the recognizer thought it
			// could have heard

			ArrayList matches = data
					.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			String mySpeaking = null;

			mySpeaking = (String) matches.get(0);
			Log.v("XXXXXXXXXX", matches.toString());
			// testView.setText(Html.fromHtml(buildSpeakArray(mSourceString,
			// mySpeaking, true)));

			testView.setText(Html.fromHtml(buildText(matches, mSourceString)));

		}

	}

	private String buildText(ArrayList resultList, String analyString) {

		analyString = analyString.replaceAll("[,.]", " ").toLowerCase();
		String[] sources = null;
		if (analyString.length() > 0)
			sources = analyString.split(" ");
		String tempHtmlString = new String();
		int rightwords = 0;
		accuration = 0;

		for (int i = 0; i < sources.length; i++) {

			for (int j = 0; j < resultList.size(); j++) {

				if (resultList.get(j).toString().indexOf(sources[i]) != -1) {

					rightwords++;
					tempHtmlString += sources[i] + " ";
					break;
				}
				if (j == resultList.size() - 1) {

					tempHtmlString += "<font color=" + COLORDIEFFER + ">"
							+ sources[i] + " </font> ";
				}
			}
		}
		Log.v(TAG, "rightwords"+rightwords);
		accuration = rightwords * 1.0f / sources.length;
		return tempHtmlString;
	}
}
