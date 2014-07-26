package com.hp.saas.agm.app.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.hp.saas.agm.core.model.Entity;
import com.hp.saas.agm.core.model.parser.EntityList;
import com.hp.saas.agm.app.R;

public class TeamAdapter extends BaseAdapter {
	protected static final String TAG = "SprintAdapter";
	private Context mContext;
	private EntityList lists;
    private String selected;

	public TeamAdapter(Context context, EntityList sprintList, String selected) {
		this.mContext = context;
		this.lists = sprintList;
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
		Entity item = lists.get(position);
		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.common_selector_adapter_item,
                    null);
            holder = new Holder();
			holder.tvItem = (TextView) convertView.findViewById(R.id.item_name);
            holder.ivBadge = (ImageView) convertView.findViewById(R.id.item_badge);





			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}

        holder.tvItem.setText(item.getPropertyValue("name"));

        if ((item.getPropertyValue("name")).equals(selected)) {
            holder.ivBadge.setBackgroundColor(mContext.getResources().getColor(R.color.blue));
            holder.tvItem.setTextColor(mContext.getResources().getColor(R.color.blue));
        }else {
            holder.ivBadge.setBackgroundColor(mContext.getResources().getColor(R.color.gray));
            holder.tvItem.setTextColor(mContext.getResources().getColor(R.color.black));
        }

		return convertView;
	}

	class Holder {
		ImageView ivBadge;
		TextView tvItem;
	}

}
