package com.hp.saas.agm.app.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.hp.saas.agm.app.utils.FileUtil;
import com.hp.saas.agm.app.view.ExpandedTreeView;
import com.hp.saas.agm.core.model.Entity;
import com.hp.saas.agm.core.model.parser.EntityList;
import com.hp.saas.agm.app.R;

import java.lang.ref.SoftReference;
import java.util.HashMap;

public class ReleaseExpandTreeAdapter extends BaseExpandableListAdapter implements
        ExpandedTreeView.ExpandTreeHeaderAdapter {

	private static final String TAG = "ReleaseExpandTreeAdapter";
	private Context mContext;
	private ExpandedTreeView expandedTreeView;
    private HashMap<String, EntityList> children = new HashMap<String, EntityList>();


	// 伪数据
	private HashMap<Integer, Integer> groupStatusMap;

    private String[] groups = {"current", "coming"};
    private String[] groupsName = {"Current release(recommend)", "comming release"};

	public ReleaseExpandTreeAdapter(Context context, HashMap<String, EntityList> children) {
		this.mContext = context;
		this.children = children;
        groupStatusMap = new HashMap<Integer, Integer>();
	}

	public Object getChild(int groupPosition, int childPosition) {
		return children.get(groups[groupPosition]).get(childPosition);
	}

	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	public int getChildrenCount(int groupPosition) {
		return children.get(groups[groupPosition]).size();
	}

	public Object getGroup(int groupPosition) {
		return groups[groupPosition];
	}

	public int getGroupCount() {
		return groups.length;
	}

	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	public boolean hasStableIds() {
		return true;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		ChildHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.expanded_tree_child, null);
			holder = new ChildHolder();
			holder.nameView = (TextView) convertView
					.findViewById(R.id.item_name);
            convertView.setTag(holder);
		} else {
			holder = (ChildHolder) convertView.getTag();
		}


		holder.nameView.setText(((Entity)getChild(groupPosition, childPosition)).getPropertyValue("name"));
		return convertView;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		GroupHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.expanded_tree_group, null);
			holder = new GroupHolder();
			holder.nameView = (TextView) convertView
					.findViewById(R.id.group_name);
			holder.onLineView = (TextView) convertView
					.findViewById(R.id.online_count);
			holder.iconView = (ImageView) convertView
					.findViewById(R.id.group_indicator);
			convertView.setTag(holder);
		} else {
			holder = (GroupHolder) convertView.getTag();
		}

        if (groupPosition == 0) {
            holder.nameView.setText("Current release(recommend)");
        }else if(groupPosition == 1) {
            holder.nameView.setText("Coming release");
        }else {
            holder.nameView.setText("passed release");
        }

        //holder.nameView.setText(groups[groupPosition]);
        int totalCount = 0;
        for (int i=0; i<groups.length; i++) {
            totalCount = totalCount + getChildrenCount(i);
        }
		holder.onLineView.setText(getChildrenCount(groupPosition) + "/"
				+ totalCount);
		if (isExpanded) {
			holder.iconView.setImageResource(R.drawable.qb_down);
		} else {
			holder.iconView.setImageResource(R.drawable.qb_right);
		}
		return convertView;
	}

	@Override
	public int getTreeHeaderState(int groupPosition, int childPosition) {
		final int childCount = getChildrenCount(groupPosition);
		if (childPosition == childCount - 1) {
			//mSearchView.setVisibility(View.GONE);
			return PINNED_HEADER_PUSHED_UP;
		} else if (childPosition == -1) {
			//mSearchView.setVisibility(View.VISIBLE);
			return PINNED_HEADER_GONE;
		} else {
			//mSearchView.setVisibility(View.GONE);
			return PINNED_HEADER_VISIBLE;
		}
	}

	@Override
	public void configureTreeHeader(View header, int groupPosition,
			int childPosition, int alpha) {
        //if ()
		((TextView) header.findViewById(R.id.group_name))
				.setText(groupsName[groupPosition]);

        int totalCount = 0;
        for (int i=0; i<groups.length; i++) {
            totalCount = totalCount + getChildrenCount(i);
        }

		((TextView) header.findViewById(R.id.online_count))
				.setText(getChildrenCount(groupPosition) + "/"
						+ totalCount);
	}

	@Override
	public void onHeadViewClick(int groupPosition, int status) {
		groupStatusMap.put(groupPosition, status);
	}

	@Override
	public int getHeadViewClickStatus(int groupPosition) {
		if (groupStatusMap.containsKey(groupPosition)) {
			return groupStatusMap.get(groupPosition);
		} else {
			return 0;
		}
	}

	class ChildHolder {
		TextView nameView;
	}

	class GroupHolder {
		TextView nameView;
		TextView onLineView;
		ImageView iconView;
	}

}
