package com.hp.saas.agm.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.ListPopupWindow;
import com.hp.saas.agm.app.adapter.SprintAdapter;
import com.hp.saas.agm.app.adapter.StatusAdapter;
import com.hp.saas.agm.app.adapter.TeamAdapter;
import com.hp.saas.agm.app.view.popup.*;
import com.hp.saas.agm.manager.ApplicationManager;
import com.hp.saas.agm.core.model.Entity;
import com.hp.saas.agm.core.model.parser.EntityList;
import com.hp.saas.agm.app.adapter.ReleaseBacklogAdapter;
import com.hp.saas.agm.app.view.CustomListView;
import com.hp.saas.agm.app.view.LoadingView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements OnClickListener {
    //todo: refactor filter implementation to using listener
    private CustomListView lvReleaseBacklog;
    private LoadingView lvLoading;
    private EntityList releaseBacklog = EntityList.empty();
    private Context mContext;
    private ReleaseBacklogAdapter adapter;

    private TextView tvSprintFilter;
    private TextView tvOwnerFilter;
    private TextView tvStatusFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        findView();
        init();
    }

    private void findView() {
        lvReleaseBacklog = (CustomListView)findViewById(R.id.lv_release_backlog);
        lvLoading = (LoadingView)findViewById(R.id.loading);
        tvSprintFilter = (TextView) findViewById(R.id.sprint_filter);
        tvOwnerFilter = (TextView) findViewById(R.id.owner_filter);
        tvStatusFilter = (TextView) findViewById(R.id.status_filter);
    }

    private void init() {
        adapter = new ReleaseBacklogAdapter(this, releaseBacklog);
        lvReleaseBacklog.setAdapter(adapter);

        lvReleaseBacklog.setOnRefreshListener(new CustomListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new AsyncRefresh().execute(0);
            }
        });
        lvReleaseBacklog.setCanLoadMore(false);

        lvReleaseBacklog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
                Entity entity= (Entity) parent.getAdapter().getItem(position);
                Bundle data = new Bundle();
                data.putSerializable("release", entity);
                Intent intent = new Intent(mContext, StoryDetailActivity.class);
                intent.putExtras(data);
                startActivity(intent);
            }
        });

        //click filter bar
        tvSprintFilter.setOnClickListener(filterListener);
        tvOwnerFilter.setOnClickListener(filterListener);
        tvStatusFilter.setOnClickListener(filterListener);

        new NewsAsyncTask(lvLoading).execute(0);
    }

    @Override
    public void onClick(View v) {

    }

    public OnClickListener filterListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            lvReleaseBacklog.fireRefreshEvent();
            /*if (!releaseBacklog.isEmpty()) {
                if(v.getId() == R.id.sprint_filter) {
                    //do switch sprint
                    clickSprint(v);
                }else if(v.getId() == R.id.owner_filter) {
                    //do filter owner
                    clickOwner(v);
                }else if(v.getId() == R.id.status_filter) {
                    //do filter status
                    clickStatus(v);
                }
            }*/

        }
    };

    private void clickStatus(View v) {
        try {
            com.hp.saas.agm.app.view.popup.ListPopupWindow popupWindow = new com.hp.saas.agm.app.view.popup.ListPopupWindow(mContext);
            popupWindow.setTitle("Status");
            ArrayList<String> statusList = new ArrayList<String>();
            statusList.add("New");
            statusList.add("In Progress");
            statusList.add("In Testing");
            statusList.add("Done");
            popupWindow.setAdapter(new StatusAdapter(mContext, statusList, "status"));
            popupWindow.setOnSelectedListener(new PopupListener.ItemSelectedListener() {
                @Override
                public void valueChanged(Object newValue, Object oldValue) {
                  /*  changedField.add("status");
                    releaseBacklogItem.setProperty("status", newValue);
                    tvStatus.setText((String) newValue);*/
                }
            });
            popupWindow.show();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void clickSprint(View v) {
        try {
            com.hp.saas.agm.app.view.popup.ListPopupWindow popupWindow = new com.hp.saas.agm.app.view.popup.ListPopupWindow(mContext);
            popupWindow.setTitle("Sprint");
            popupWindow.setAdapter(new SprintAdapter(mContext, ApplicationManager.getSprintService().getSprints(), "selected"));
            popupWindow.setOnSelectedListener(new PopupListener.ItemSelectedListener() {
                @Override
                public void valueChanged(Object newValue, Object oldValue) {
                  /*  releaseBacklogItem.setProperty("target-rcyc", ((Entity)newValue).getPropertyValue("id"));
                    tvSprint.setText((String) ((Entity) newValue).getPropertyValue("name"));*/
                }
            });
            popupWindow.show();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void clickOwner(View v) {
        try {
            com.hp.saas.agm.app.view.popup.ListPopupWindow popupWindow = new com.hp.saas.agm.app.view.popup.ListPopupWindow(mContext);
            popupWindow.setTitle("Owner");
            Entity team = ApplicationManager.getSprintService().getTeam();
            EntityList teamMembers = ApplicationManager.getTeamMemberService().getTeamMembers(team);
            popupWindow.setAdapter(new TeamAdapter(mContext, teamMembers, "owner"));
            popupWindow.setOnSelectedListener(new PopupListener.ItemSelectedListener() {
                @Override
                public void valueChanged(Object newValue, Object oldValue) {
                /*    releaseBacklogItem.setProperty("owner", ((Entity)newValue).getPropertyValue("name"));
                    tvOwner.setText((String) ((Entity)newValue).getPropertyValue("name"));*/
                }
            });
            popupWindow.show();


        } catch (Exception e) {
            e.printStackTrace();
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
            recentReleaseBacklog = ApplicationManager.getSprintService().getStories();
            if (recentReleaseBacklog.size() > 0) {
                result = 1;
            }
            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            releaseBacklog.addAll(recentReleaseBacklog);
            adapter.notifyDataSetChanged();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

    }

    private class AsyncRefresh extends
            AsyncTask<Integer, Integer, List<Entity>> {
        private List<Entity> recentStories = new ArrayList<Entity>();

        @Override
        protected List<Entity> doInBackground(Integer... params) {
            recentStories = ApplicationManager.getSprintService().getStories(true);
            return recentStories;
        }

        @Override
        protected void onPostExecute(List<Entity> result) {
            super.onPostExecute(result);

            if (result != null) {
               /* for (Entity rc : recentStories) {
                    releaseBacklog.add(0, rc);
                }*/
                releaseBacklog.clear();
                releaseBacklog.addAll(recentStories);
                adapter.notifyDataSetChanged();
                lvReleaseBacklog.onRefreshComplete();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

    }



}