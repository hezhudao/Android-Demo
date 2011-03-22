/**
 * 
 */
package com.ixonos.assest;

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
	private static final String REGEX_STRING = "\\d\\d:\\d\\d:\\d{1,2},\\d\\d\\d --> \\d\\d:\\d\\d:\\d{1,2},\\d\\d\\d";
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

					String [] lines= line.split(" --> ");
					line = lines[0];
					sentence = new Sentence();
					
					millsecond = Integer.parseInt(line.substring(line.indexOf(",")+1));
					second = Integer.parseInt(line.substring(line.lastIndexOf(":")+1, line.lastIndexOf(",")));
					minute = Integer.parseInt(line.substring(line.indexOf(":")+1,line.lastIndexOf(":")));
					hour = Integer.parseInt(line.substring(0, line.indexOf(":")));
					times = second + minute * 60 + hour * 3600;

					sentence.setFromTime(times * 1000 + millsecond);

					line = lines[1];
					millsecond = Integer.parseInt(line.substring(line.indexOf(",")+1));
					second = Integer.parseInt(line.substring(line.lastIndexOf(":")+1, line.lastIndexOf(",")));
					minute = Integer.parseInt(line.substring(line.indexOf(":")+1,line.lastIndexOf(":")));
					hour = Integer.parseInt(line.substring(0, line.indexOf(":")));
					times = second + minute * 60 + hour * 3600;

					sentence.setToTime(times * 1000 + millsecond);
					Log.v("XXXXXX", "" + sentence.getFromTime()+" --> "+sentence.getToTime());
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
