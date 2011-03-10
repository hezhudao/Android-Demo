/**
 * 
 */
package com.ixonos;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import android.R.integer;
import android.text.TextUtils;
import android.util.Log;

/**
 * @author jashewe
 * 
 */
public class CaptionsUtil {

	private FileReader fileReader;
	private static final String REGEX_STRING = "\\d\\d:\\d\\d:\\d\\d,\\d\\d\\d --> \\d\\d:\\d\\d:\\d\\d,\\d\\d\\d";
	private int second;
	private int minute;
	private int hour;
	private int millsecond;
	private int index = 0;
	private Sentence sentence;
	private long times;
	private String content;
	private List<Sentence> sentences;
	private BufferedReader bufferedReader;

	public CaptionsUtil(String filePath) {

		getCaptions(filePath);
	}

	/**
	 * getCaptions
	 * @param filePath
	 */
	private void getCaptions(String filePath) {
		File file = new File(filePath);

		try {
			fileReader = new FileReader(file);
			bufferedReader = new BufferedReader(fileReader);
			String line;
			sentences = new ArrayList<Sentence>();

			while ((line = bufferedReader.readLine()) != null) {

				if (Pattern.matches(REGEX_STRING, line)) {

					sentence = new Sentence();

					millsecond = Integer.parseInt(line.substring(9, 12));
					second = Integer.parseInt(line.substring(6, 8));
					minute = Integer.parseInt(line.substring(3, 5));
					hour = Integer.parseInt(line.substring(0, 2));
					times = second + minute * 60 + hour * 3600;
					Log.v("XXX",
							"second" + line.substring(6, 8) + " minute"
									+ line.substring(3, 5) + " hour"
									+ line.substring(0, 2) + times + "");
					Log.v("XXX", line);
					sentence.setFromTime(times * 1000 + millsecond);

					millsecond = Integer.parseInt(line.substring(26, 29));
					second = Integer.parseInt(line.substring(23, 25));
					minute = Integer.parseInt(line.substring(20, 22));
					hour = Integer.parseInt(line.substring(17, 19));
					times = second + minute * 60 + hour * 3600;

					sentence.setToTime(times * 1000 + millsecond);
					Log.v("XXXXXX", ""+sentence.getToTime());
					content = bufferedReader.readLine();
					sentence.setContent(content);

					sentences.add(sentence);
				}
			}

			bufferedReader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @return the sentences
	 */
	public List<Sentence> getSentences() {
		return sentences;
	}
}
