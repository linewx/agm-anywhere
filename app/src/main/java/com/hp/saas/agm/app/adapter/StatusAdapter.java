package com.hp.saas.agm.app.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.hp.saas.agm.app.R;

import java.util.List;

public class StatusAdapter extends BaseAdapter {
	protected static final String TAG = "StatusAdapter";
	private Context mContext;
	private List<String> lists;
    private String selected;

	public StatusAdapter(Context context, List<String>statusList, String selected) {
		this.mContext = context;
		this.lists = statusList;
        this.selected = selected;
    }

	@Override
	public int getCount() {
		if (lists != null) {
			return lists.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		return lists.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Holder holder;
		String status = lists.get(position);
		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.status_item,
                    null);
            holder = new Holder();
			holder.tvStatus = (TextView) convertView.findViewById(R.id.status_name);
            holder.ivChecked = (ImageView) convertView.findViewById(R.id.status_selected);





			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}

        holder.tvStatus.setText(status);

        if (status.equals(selected)) {
            holder.ivChecked.setBackgroundColor(mContext.getResources().getColor(R.color.blue));
            holder.tvStatus.setTextColor(mContext.getResources().getColor(R.color.blue));
        }else {
            holder.ivChecked.setBackgroundColor(mContext.getResources().getColor(R.color.gray));
            holder.tvStatus.setTextColor(mContext.getResources().getColor(R.color.black));
        }

		return convertView;
	}

	class Holder {
		ImageView ivChecked;
		TextView tvStatus;
	}

}
