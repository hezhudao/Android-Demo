package com.ixonos.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.ixonos.adapter.VideoListAdapter;

public class VideoListActivity  extends Activity{

	private GridView mGridView;
	private VideoListAdapter mListAdapter;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.video_list_activity);

		mListAdapter = new VideoListAdapter(this);
		mGridView = (GridView)findViewById(R.id.videoGrid);
	    mGridView.setAdapter(mListAdapter);
	    mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				startActivity(new Intent(VideoListActivity.this, MianActivity.class));
			}

	    });
	}
}
