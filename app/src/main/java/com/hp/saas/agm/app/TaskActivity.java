package com.hp.saas.agm.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import com.hp.saas.agm.core.entity.EntityQuery;
import com.hp.saas.agm.manager.ApplicationManager;
import com.hp.saas.agm.core.model.Entity;
import com.hp.saas.agm.core.model.parser.EntityList;
import com.hp.saas.agm.app.adapter.TaskAdapter;
import com.hp.saas.agm.app.view.CustomListView;
import com.hp.saas.agm.app.view.LoadingView;

import java.util.ArrayList;
import java.util.List;

public class TaskActivity extends Activity implements OnClickListener {

    //private CustomListView lvReleaseBacklog;
    //private LoadingView lvLoading;
    //private EntityList releaseBacklog = EntityList.empty();
    private Context mContext;
    private CustomListView lvTasks;
    private LoadingView lvLoading;
    private Entity releaseBacklogItem;
    private Entity story;
    private EntityList taskList = EntityList.empty();
    private TaskAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        mContext = this;
        findView();
        init();
    }

    private void findView() {
        lvTasks = (CustomListView) findViewById(R.id.lv_tasks);
        lvLoading = (LoadingView) findViewById(R.id.loading);
    }

    private void init() {

        Intent intent = getIntent();
        Bundle data = intent.getExtras();

        //get release&story information
        releaseBacklogItem = (Entity)data.getSerializable("releaseBacklogItem");
        story = (Entity)data.getSerializable("story");

        //lvTasks.setAdapter(new TaskAdapter(this, taskList));
        adapter = new TaskAdapter(this, taskList);
        lvTasks.setAdapter(adapter);

        lvTasks.setOnRefreshListener(new CustomListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new AsyncRefresh().execute(0);
            }
        });

        lvTasks.setCanLoadMore(false);

        new NewsAsyncTask(lvLoading).execute(0);
    }

    @Override
    public void onClick(View v) {

    }

    private OnClickListener taskClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext, TaskActivity.class);
            startActivity(intent);
            finish();
        }
    };

    private EntityList getProjectTask() {
        EntityQuery query = new EntityQuery("project-task");
        query.addColumn("id", 1);
        query.addColumn("status", 1);
        query.addColumn("description", 1);
        query.addColumn("estimated", 1);
        query.addColumn("invested", 1);
        query.addColumn("remaining",1);
        query.addColumn("assigned-to", 1);
        query.addColumn("release-backlog-item-id", 1);
        query.setValue("release-backlog-item-id", String.valueOf(releaseBacklogItem.getPropertyValue("id")));
        EntityList list = EntityList.empty();
        try {
            list = ApplicationManager.getEntityService().query(query);
        } catch(Exception e) {
            e.printStackTrace();
        }finally {

        }

        if (list.size() > 0) {
            return list;
        }else {
            return null;
        }
    }

    private class NewsAsyncTask extends AsyncTaskBase {
        EntityList recentReleaseBacklog = null;

        public NewsAsyncTask(LoadingView loadingView) {
            super(loadingView);
        }

        @Override
        protected Integer doInBackground(Integer... params) {
            int result = -1;
            recentReleaseBacklog = getProjectTask();
            if (recentReleaseBacklog.size() > 0) {
                result = 1;
            }
            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);

            taskList.addAll(recentReleaseBacklog);
            adapter.notifyDataSetChanged();
            //((BaseAdapter)lvTasks.getAdapter()).notifyDataSetChanged();

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

    }

    private class AsyncRefresh extends
            AsyncTask<Integer, Integer, List<Entity>> {
        private List<Entity> recentTasks = new ArrayList<Entity>();

        @Override
        protected List<Entity> doInBackground(Integer... params) {
            recentTasks = getProjectTask();
            return recentTasks;
        }

        @Override
        protected void onPostExecute(List<Entity> result) {
            super.onPostExecute(result);
            if (result != null) {
                for (Entity rc : recentTasks) {
                    taskList.add(0, rc);
                }
                //adapter.notifyDataSetChanged();
                lvTasks.onRefreshComplete();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

    }



}