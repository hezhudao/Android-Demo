/**
 * 
 */
package com.ixonos;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * @author jashewe
 * 
 */
public class CaptionsAdapter extends BaseAdapter {

	private List<Sentence> sentences = new ArrayList<Sentence>();
	private LayoutInflater mInflater;

	public CaptionsAdapter(Context context, List<Sentence> sentences) {

		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.sentences = sentences;
	}

	/**
	 * @return the sentences
	 */
	public List<Sentence> getSentences() {
		return sentences;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return sentences.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return sentences.get(position);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		RecentViewHolder holder;
		if (convertView == null) {

			holder = new RecentViewHolder();
			convertView = mInflater.inflate(R.layout.captions_list, null);
			holder.contentTextView = (TextView) convertView
					.findViewById(R.id.captions_textview);

			convertView.setTag(holder);
		} else {
			holder = (RecentViewHolder) convertView.getTag();
		}

		if (null != holder) {
			
			Sentence sentence = (Sentence) getItem(position);
			holder.contentTextView.setText(sentence.getContent());
		}
		
		return convertView;
	}

	static class RecentViewHolder {

		TextView contentTextView;
	}
}
