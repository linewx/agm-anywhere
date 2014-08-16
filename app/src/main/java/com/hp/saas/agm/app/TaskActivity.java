package com.hp.saas.agm.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
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
        setContentView(R.layout.task_activity);

        mContext = this;
        findView();
        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.task_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_new:
                newTask();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void newTask() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("releaseBacklogItem", releaseBacklogItem);
        bundle.putSerializable("story", story);
        Intent intent = new Intent(mContext, NewTaskActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
        //finish();
    }


    private void findView() {
        lvTasks = (CustomListView) findViewById(R.id.lv_tasks);
        lvLoading = (LoadingView) findViewById(R.id.loading);
    }

    private void init() {

        Intent intent = getIntent();
        Bundle data = intent.getExtras();

        //get release&story information
        releaseBacklogItem = (Entity) data.getSerializable("releaseBacklogItem");
        story = (Entity) data.getSerializable("story");

        //lvTasks.setAdapter(new TaskAdapter(this, taskList));
        adapter = new TaskAdapter(this, taskList);
        lvTasks.setAdapter(adapter);

        lvTasks.setOnRefreshListener(new CustomListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new AsyncRefresh().execute(0);
            }
        });

        lvTasks.setOnItemLongClickListener(new CustomListView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage("are you sure to delete this item?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int dialogId) {
                                Entity task = (Entity) parent.getAdapter().getItem(position);
                                ApplicationManager.getEntityService().deleteEntity(task);
                                new AsyncRefresh().execute(0);
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int dialogId) {
                                dialog.dismiss();
                                // User cancelled the dialog
                            }
                        });
                // Create the AlertDialog object and return it
                builder.create().show();
                return true;
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
            //finish();
        }
    };

    private EntityList getProjectTask() {
        EntityQuery query = new EntityQuery("project-task");
        query.addColumn("id", 1);
        query.addColumn("status", 1);
        query.addColumn("description", 1);
        query.addColumn("estimated", 1);
        query.addColumn("invested", 1);
        query.addColumn("remaining", 1);
        query.addColumn("assigned-to", 1);
        query.addColumn("release-backlog-item-id", 1);
        query.setValue("release-backlog-item-id", String.valueOf(releaseBacklogItem.getPropertyValue("id")));
        EntityList list = EntityList.empty();
        try {
            list = ApplicationManager.getEntityService().query(query);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }

        if (list.size() > 0) {
            return list;
        } else {
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
            int result = 1;
            recentReleaseBacklog = getProjectTask();
            return result;
            /*if (recentReleaseBacklog.size() > 0) {
                result = 1;
            }
            return result;*/
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            if (recentReleaseBacklog != null) {
                taskList.addAll(recentReleaseBacklog);
            }

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
            taskList.clear();
            if (result != null) {
                for (Entity rc : recentTasks) {
                    taskList.add(0, rc);
                }

            }
            adapter.notifyDataSetChanged();
            lvTasks.onRefreshComplete();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

    }


}