package com.ixonos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ixonos.ui.R;

public class VideoListAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Context mContext;
	private int[] images;

	public VideoListAdapter(Context context) {
		mInflater = LayoutInflater.from(context);
		mContext = context;
		images = new int[] { R.drawable.png1, R.drawable.png2, R.drawable.png3,
				R.drawable.png4, R.drawable.png5, R.drawable.png6,
				R.drawable.png7, R.drawable.png8, R.drawable.png9,
				R.drawable.png10, R.drawable.png11, R.drawable.png12,
				R.drawable.png13, R.drawable.png14, R.drawable.png15,
				R.drawable.png16, R.drawable.png17, R.drawable.png18 };
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return images.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.video_list_item, null);
			holder = new ViewHolder();
			holder.icon = (ImageView) convertView.findViewById(R.id.ivVideo);
			holder.icon.setBackgroundDrawable(mContext.getResources()
					.getDrawable(images[position]));
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		return convertView;
	}

	static class ViewHolder {
		ImageView icon;
	}
}
