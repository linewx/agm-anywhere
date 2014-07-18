package com.hp.saas.agm.app.adapter.spinner;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.hp.saas.agm.core.model.Entity;
import com.hp.saas.agm.core.model.parser.EntityList;
import com.hp.saas.agm.app.R;

public class SprintSpinnerAdapter extends BaseAdapter {
	protected static final String TAG = "SprintAdapter";
	private Context mContext;
	private EntityList lists;
    private String selected;
    private LayoutInflater mInflater;

	public SprintSpinnerAdapter(Context context, EntityList sprintList, String selected) {
		this.mContext = context;
		this.lists = sprintList;
        this.selected = selected;

        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

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
        return createViewFromResource(position, convertView, parent, R.layout.test_spinner_item);
    }

    private View createViewFromResource(int position, View convertView, ViewGroup parent,
                                        int resource) {
        View view;
        TextView text;

        if (convertView == null) {
            view = mInflater.inflate(resource, parent, false);
        } else {
            view = convertView;
        }

        text = (TextView) view.findViewById(R.id.item_name);
        text.setText("test111");

        return view;
    }

}
