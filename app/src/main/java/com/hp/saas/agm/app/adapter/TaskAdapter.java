package com.hp.saas.agm.app.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.hp.saas.agm.core.model.Entity;
import com.hp.saas.agm.core.model.parser.EntityList;
import com.hp.saas.agm.app.R;

/**
 * Created by lugan on 7/4/2014.
 */
public class TaskAdapter extends BaseAdapter{

    private Context context;
    private EntityList taskList;

    public TaskAdapter(Context context, EntityList taskList) {
        this.context = context;
        this.taskList = taskList;
    }

    @Override
    public int getCount() {
        if (taskList != null) {
            return taskList.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return taskList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        final Holder holder;
        Entity task = taskList.get(position);
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.task_task_adapter_item,
                    null);
            holder = new Holder();
            //holder.tvRemaining = (TextView) convertView.findViewById(R.id.task_remaining);
            holder.tvEstimated = (TextView) convertView.findViewById(R.id.task_estimated);
            //holder.tvInvested = (TextView) convertView.findViewById(R.id.task_invested);
            holder.tvStatus = (TextView) convertView.findViewById(R.id.task_status);
            holder.tvDescription = (TextView) convertView.findViewById(R.id.task_description);
            holder.tvAssigned = (TextView) convertView.findViewById(R.id.task_assigned);

            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }


        //holder.tvRemaining.setText(task.getPropertyValue("remaining"));
        holder.tvEstimated.setText(task.getPropertyValue("estimated"));
        //holder.tvInvested.setText(task.getPropertyValue("invested"));

        String status = task.getPropertyValue("status");
        if(status.equals("New")) {
            holder.tvStatus.setBackgroundColor(context.getResources().getColor(R.color.New));
        }else if(status.equals("In Progress")) {
            holder.tvStatus.setBackgroundColor(context.getResources().getColor(R.color.InProgress));
        }else if(status.equals("Completed")) {
            holder.tvStatus.setBackgroundColor(context.getResources().getColor(R.color.Done));
        }
        holder.tvStatus.setText(task.getPropertyValue("status"));


        holder.tvDescription.setText(task.getPropertyValue("description"));
        holder.tvAssigned.setText(task.getPropertyValue("assigned-to"));
        return convertView;
    }

    class Holder {
        TextView tvRemaining;
        TextView tvEstimated;
        TextView tvInvested;
        TextView tvStatus;
        TextView tvDescription;
        TextView tvAssigned;

    }

}
