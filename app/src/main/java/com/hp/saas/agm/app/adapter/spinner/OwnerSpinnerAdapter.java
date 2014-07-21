package com.hp.saas.agm.app.adapter.spinner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.hp.saas.agm.app.R;
import com.hp.saas.agm.core.model.Entity;
import com.hp.saas.agm.core.model.parser.EntityList;

import java.util.ArrayList;

public class OwnerSpinnerAdapter extends BaseAdapter {
    protected static final String TAG = "OwnerSpinnerAdapter";
    private Context mContext;
    private ArrayList<String> lists;
    private String selected;
    private LayoutInflater mInflater;

    public OwnerSpinnerAdapter(Context context, ArrayList<String> ownerList, String selected) {
        this.mContext = context;
        this.lists = ownerList;
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
            view = mInflater.inflate(R.layout.spinner_content, parent, false);
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
            convertView = View.inflate(mContext, R.layout.spinner_item,
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
