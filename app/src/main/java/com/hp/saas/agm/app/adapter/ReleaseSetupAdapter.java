package com.hp.saas.agm.app.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.hp.saas.agm.app.R;
import com.hp.saas.agm.core.model.Entity;
import com.hp.saas.agm.core.model.parser.EntityList;

public class ReleaseSetupAdapter extends BaseAdapter {
	protected static final String TAG = "ReleaseSetup";
	private Context mContext;
	private EntityList lists;
    private String selected;

	public ReleaseSetupAdapter(Context context, EntityList releaseList) {
		this.mContext = context;
		this.lists = releaseList;
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
		Entity item = lists.get(position);
		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.wizard_selector_item,
                    null);
            holder = new Holder();
			holder.tvItem = (TextView) convertView.findViewById(R.id.item_name);
            convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}

        holder.tvItem.setText(item.getPropertyValue("name"));



		return convertView;
	}

	class Holder {
		TextView tvItem;
	}

}
