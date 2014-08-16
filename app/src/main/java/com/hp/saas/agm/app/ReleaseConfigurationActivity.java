package com.hp.saas.agm.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ExpandableListView;
import com.hp.saas.agm.app.adapter.ReleaseExpandTreeAdapter;
import com.hp.saas.agm.app.view.ExpandedTreeView;
import com.hp.saas.agm.app.view.LoadingView;
import com.hp.saas.agm.core.model.Entity;
import com.hp.saas.agm.core.model.parser.EntityList;
import com.hp.saas.agm.manager.ApplicationManager;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class ReleaseConfigurationActivity extends Activity implements OnClickListener {
/*    private CustomListView lvSuggestList;
    private CustomListView lvFullList;*/
    private ExpandedTreeView expandedTreeView;
    private LoadingView lvLoading;
    private Context mContext;
    private ReleaseExpandTreeAdapter releaseAdapter;
    private LinkedHashMap<String, EntityList> releaseList = new LinkedHashMap<String, EntityList>();
    private HashMap<String, String> groupsName = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.config_release_activity);

        mContext = this;
        findView();
        init();
    }

    private void findView() {
        expandedTreeView = (ExpandedTreeView) findViewById(R.id.release_expand_view);
        lvLoading = (LoadingView)findViewById(R.id.loading);

    }

    private void init() {
        expandedTreeView.setHeaderView(LayoutInflater.from(mContext).inflate(
                R.layout.expanded_tree_header, expandedTreeView, false));
        expandedTreeView.setGroupIndicator(null);
        //releaseAdapter = new ExpandedTreeView(mContext, maps, mIphoneTreeView,mSearchView);
        //mIphoneTreeView.setAdapter(mExpAdapter);
        releaseList.put("current", EntityList.empty());
        releaseList.put("coming", EntityList.empty());
        releaseList.put("passed", EntityList.empty());

        groupsName.put("current", "Current Release (recommended)");
        groupsName.put("coming", "Coming Release");
        groupsName.put("passed", "Passed Release");

        releaseAdapter = new ReleaseExpandTreeAdapter(mContext, releaseList, groupsName);
        expandedTreeView.setAdapter(releaseAdapter);

        expandedTreeView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                ReleaseExpandTreeAdapter adapter = (ReleaseExpandTreeAdapter)parent.getExpandableListAdapter();
                Entity release = (Entity)adapter.getChild(groupPosition, childPosition);
                ApplicationManager.getSprintService().selectRelease(release);

                Intent intent = new Intent(mContext, TeamConfigurationActivity.class);
                startActivity(intent);
                //finish();

                return false;
               /* TextView a = (TextView)v.findViewById(R.id.item_name);
                ApplicationManager.getMessageService().show(a.getText().toString());
                return false;*/
            }
        });
        new NewsAsyncTask(lvLoading).execute(0);
    }

    @Override
    public void onClick(View v) {

    }





    private class NewsAsyncTask extends AsyncTaskBase {
        EntityList recentReleases = null;
        EntityList currentReleases = EntityList.empty();
        EntityList comingReleases = EntityList.empty();
        EntityList passedReleases = EntityList.empty();

        public NewsAsyncTask(LoadingView loadingView) {
            super(loadingView);
        }

        @Override
        protected Integer doInBackground(Integer... params) {
            int result = -1;
            recentReleases = ApplicationManager.getSprintService().getReleases();
            for (Entity entity: recentReleases) {
                int distance = ApplicationManager.getSprintService().distanceToNow(entity);
                if (distance == 0) {
                    currentReleases.add(entity);
                }else if(distance > 0) {
                    comingReleases.add(entity);
                }else{
                    passedReleases.add(entity);
                }
            }


            if (recentReleases.size() > 0) {
                result = 1;
            }
            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            releaseList.get("current").addAll(currentReleases);
            releaseList.get("coming").addAll(comingReleases);
            releaseList.get("passed").addAll(passedReleases);
            releaseAdapter.notifyDataSetChanged();

            if (releaseAdapter.getHeadViewClickStatus(0) == 0) {
                releaseAdapter.onHeadViewClick(0, 1);
                expandedTreeView.expandGroup(0);
                expandedTreeView.setSelectedGroup(0);

            } else if (releaseAdapter.getHeadViewClickStatus(0) == 1) {
                releaseAdapter.onHeadViewClick(0, 0);
                expandedTreeView.collapseGroup(0);
            }
            //expandedTreeView.expandGroup(0);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

    }





}