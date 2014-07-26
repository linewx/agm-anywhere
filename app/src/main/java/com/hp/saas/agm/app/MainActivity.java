package com.hp.saas.agm.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import com.hp.saas.agm.app.adapter.SprintAdapter;
import com.hp.saas.agm.app.adapter.StatusAdapter;
import com.hp.saas.agm.app.adapter.TeamAdapter;
import com.hp.saas.agm.app.adapter.spinner.OwnerSpinnerAdapter;
import com.hp.saas.agm.app.adapter.spinner.SprintSpinnerAdapter;
import com.hp.saas.agm.app.adapter.spinner.StatusSpinnerAdapter;
import com.hp.saas.agm.app.view.popup.*;
import com.hp.saas.agm.core.entity.EntityQuery;
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

    private Spinner spSprintFilter;
    private Spinner spOwnerFilter;
    private Spinner spStatusFilter;

    //testing
    ArrayList<String> arr_cars = new ArrayList<String>();
    String selected_car = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        mContext = this;
        findView();
        init();
    }

    private void findView() {
        lvReleaseBacklog = (CustomListView) findViewById(R.id.lv_release_backlog);
        lvLoading = (LoadingView) findViewById(R.id.loading);
        spSprintFilter = (Spinner) findViewById(R.id.sprint_filter);

        spOwnerFilter = (Spinner) findViewById(R.id.owner_filter);
        spStatusFilter = (Spinner) findViewById(R.id.status_filter);
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
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Entity entity = (Entity) parent.getAdapter().getItem(position);
                Bundle data = new Bundle();
                data.putSerializable("release", entity);
                Intent intent = new Intent(mContext, StoryDetailActivity.class);
                intent.putExtras(data);
                startActivity(intent);
            }
        });


        arr_cars.add("Rolls Royce Phantom");
        arr_cars.add("Ferrari California");

        /*//ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, arr_cars);
        //spSprintFilter.setAdapter(adapter);*/
        //SprintSpinnerAdapter spinnerAdapter = new SprintSpinnerAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, arr_cars);


        //init sprint filter
        SprintSpinnerAdapter spinnerAdapter = new SprintSpinnerAdapter(mContext, ApplicationManager.getSprintService().getSprints(), "selected");

        spSprintFilter.setAdapter(spinnerAdapter);
        //spSprintFilter.setSelection(ApplicationManager.getSprintService().lookupCurrentSprintIndex());


        //init owner filter
        ArrayList<String> ownerList = new ArrayList<String>();
        ownerList.add("All Story");
        ownerList.add("My Story");

        OwnerSpinnerAdapter ownerAdapter = new OwnerSpinnerAdapter(mContext, ownerList, "All");
        spOwnerFilter.setAdapter(ownerAdapter);
        //spOwnerFilter.setSelection(0);

        //init satus filter
        ArrayList<String> statusList = new ArrayList<String>();
        statusList.add("All Status");
        statusList.add("New");
        statusList.add("In Progress");
        statusList.add("In Testing");
        statusList.add("Done");


        StatusSpinnerAdapter statusAdapter = new StatusSpinnerAdapter(mContext, statusList, "All");
        spStatusFilter.setAdapter(statusAdapter);
        //spStatusFilter.setSelection(0);

        spSprintFilter.setOnItemSelectedListener(filterListener);
        spOwnerFilter.setOnItemSelectedListener(filterListener);
        spStatusFilter.setOnItemSelectedListener(filterListener);


        new NewsAsyncTask(lvLoading).execute(0);
    }

    @Override
    public void onClick(View v) {

    }

    public AdapterView.OnItemSelectedListener filterListener =new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
            lvReleaseBacklog.fireRefreshEvent();
            // your code here
        }

        @Override
        public void onNothingSelected(AdapterView<?> parentView) {
            // your code here
        }
    };

    private void clickStatus(View v) {
        try {
            com.hp.saas.agm.app.view.popup.ListPopupWindow popupWindow = new com.hp.saas.agm.app.view.popup.ListPopupWindow(mContext);
            popupWindow.setTitle("Status");
            ArrayList<String> statusList = new ArrayList<String>();
            statusList.add("New");
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

    private EntityList getStories(final Entity sprint, final Entity team) {
        EntityQuery query = new EntityQuery("release-backlog-item");
        query.addColumn("release-id", 1);
        query.addColumn("entity-name", 1);
        query.addColumn("status", 1);
        query.addColumn("owner", 1);
        query.addColumn("entity-id", 1);
        query.addColumn("story-points", 1);
        query.setValue("sprint-id", String.valueOf(sprint.getPropertyValue("id")));
        query.setValue("team-id", String.valueOf(team.getPropertyValue("id")));
        //query.addOrder("last-modified", SortOrder.ASCENDING);
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

    private EntityList getReleaseBacklog(Entity sprint, Entity team, String owner, String status) {
        EntityList result = getStories(sprint, team);
        result = filterOwner(result, owner);
        result = filterStatus(result, status);

        return result;
    }

    private EntityList filterOwner(EntityList backlogList, String owner) {
        EntityList result = EntityList.empty();
        if (owner.equals("All Story")) {
            result.addAll(backlogList);
        } else if (owner.equals("My Story")) {
            for (Entity entity : backlogList) {
                if (entity.getPropertyValue("owner").equals(ApplicationManager.getUserService().getUser())) {
                    result.add(entity);
                }
            }
        }
        return result;
    }

    private EntityList filterStatus(EntityList backlogList, String status) {
        EntityList result = EntityList.empty();
        if (status.equals("All Status")) {
            result.addAll(backlogList);
        } else {
            for (Entity entity : backlogList) {
                if (entity.getPropertyValue("status").equals(status)) {
                    result.add(entity);
                }
            }
        }
        return result;
    }

    private class NewsAsyncTask extends AsyncTaskBase {
        EntityList recentReleaseBacklog = null;

        public NewsAsyncTask(LoadingView loadingView) {
            super(loadingView);
        }

        @Override
        protected Integer doInBackground(Integer... params) {
            int result = -1;

            //todo: try to avoid loadSprints every time
            recentReleaseBacklog = getStories(ApplicationManager.getSprintService().getSprint(), ApplicationManager.getSprintService().getTeam());
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
            //recentStories = getStories(ApplicationManager.getSprintService().getSprint(), ApplicationManager.getSprintService().getTeam());

            recentStories = getReleaseBacklog((Entity)spSprintFilter.getSelectedItem(),
                                                ApplicationManager.getSprintService().getTeam(),
                                                (String)spOwnerFilter.getSelectedItem(),
                                                (String)spStatusFilter.getSelectedItem()
                            );
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