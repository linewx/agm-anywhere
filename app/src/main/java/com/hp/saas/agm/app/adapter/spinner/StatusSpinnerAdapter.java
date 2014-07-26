package com.hp.saas.agm.app.adapter.spinner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.hp.saas.agm.app.R;

import java.util.ArrayList;

public class StatusSpinnerAdapter extends BaseAdapter {
    protected static final String TAG = "StatusSpinnerAdapter";
    private Context mContext;
    private ArrayList<String> lists;
    private String selected;
    private LayoutInflater mInflater;

    public StatusSpinnerAdapter(Context context, ArrayList<String> statusList, String selected) {
        this.mContext = context;
        this.lists = statusList;
        this.selected = selected;

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

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
        View view;
        TextView text;

        if (convertView == null) {
            view = mInflater.inflate(R.layout.common_spinner_header, parent, false);
        } else {
            view = convertView;
        }

        text = (TextView) view.findViewById(R.id.item_name);
        String item = lists.get(position);
        text.setText(item);
        return view;
    }

    class Holder {
        ImageView ivBadge;
        TextView tvItem;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        final Holder holder;
        String item = lists.get(position);
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.common_spinner_item,
                    null);
            holder = new Holder();
            holder.tvItem = (TextView) convertView.findViewById(R.id.item_name);
            holder.ivBadge = (ImageView) convertView.findViewById(R.id.item_badge);


            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        holder.tvItem.setText(item);


        if (parent.isSelected()) {
            holder.ivBadge.setBackgroundColor(mContext.getResources().getColor(R.color.blue));
            holder.tvItem.setTextColor(mContext.getResources().getColor(R.color.blue));
        } else {
            holder.ivBadge.setBackgroundColor(mContext.getResources().getColor(R.color.gray));
            holder.tvItem.setTextColor(mContext.getResources().getColor(R.color.black));
        }

        return convertView;
    }
}
