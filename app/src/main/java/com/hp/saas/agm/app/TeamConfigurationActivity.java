package com.hp.saas.agm.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import com.hp.saas.agm.app.adapter.TeamConfigurationAdapter;
import com.hp.saas.agm.app.view.CustomListView;
import com.hp.saas.agm.app.view.LoadingView;
import com.hp.saas.agm.core.model.Entity;
import com.hp.saas.agm.core.model.parser.EntityList;
import com.hp.saas.agm.manager.ApplicationManager;

import java.util.ArrayList;
import java.util.List;

public class TeamConfigurationActivity extends Activity implements OnClickListener {
    //todo: refactor filter implementation to using listener
    private CustomListView lvTeam;
    private LoadingView lvLoading;
    private EntityList teams = EntityList.empty();
    private Context mContext;
    private TeamConfigurationAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_configuration);

        mContext = this;
        findView();
        init();
    }

    private void findView() {
        lvTeam = (CustomListView)findViewById(R.id.lv_team);
        lvLoading = (LoadingView)findViewById(R.id.loading);

    }

    private void init() {
        adapter = new TeamConfigurationAdapter(this, teams, null);
        lvTeam.setAdapter(adapter);

        lvTeam.setOnRefreshListener(new CustomListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new AsyncRefresh().execute(0);
            }
        });
        lvTeam.setCanLoadMore(false);

        lvTeam.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
                Entity entity= (Entity) parent.getAdapter().getItem(position);
                ApplicationManager.getSprintService().selectTeam(entity);
                Intent intent = new Intent(mContext, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


        new NewsAsyncTask(lvLoading).execute(0);
    }

    @Override
    public void onClick(View v) {

    }





    private class NewsAsyncTask extends AsyncTaskBase {
        EntityList recentTeams = null;

        public NewsAsyncTask(LoadingView loadingView) {
            super(loadingView);
        }

        @Override
        protected Integer doInBackground(Integer... params) {
            int result = -1;
            recentTeams = ApplicationManager.getSprintService().getTeams();
            if (recentTeams.size() > 0) {
                result = 1;
            }
            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            teams.addAll(recentTeams);
            adapter.notifyDataSetChanged();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

    }

    private class AsyncRefresh extends
            AsyncTask<Integer, Integer, List<Entity>> {
        private List<Entity> recentTeams = new ArrayList<Entity>();

        @Override
        protected List<Entity> doInBackground(Integer... params) {
            recentTeams = ApplicationManager.getSprintService().getTeams();
            return recentTeams;
        }

        @Override
        protected void onPostExecute(List<Entity> result) {
            super.onPostExecute(result);

            if (result != null) {
               /* for (Entity rc : recentStories) {
                    teams.add(0, rc);
                }*/
                teams.clear();
                teams.addAll(recentTeams);
                adapter.notifyDataSetChanged();
                lvTeam.onRefreshComplete();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

    }



}