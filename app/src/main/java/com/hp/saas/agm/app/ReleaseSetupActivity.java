package com.hp.saas.agm.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import com.hp.saas.agm.app.adapter.*;
import com.hp.saas.agm.app.view.CustomListView;
import com.hp.saas.agm.app.view.ExpandedTreeView;
import com.hp.saas.agm.app.view.LoadingView;
import com.hp.saas.agm.app.view.popup.PopupListener;
import com.hp.saas.agm.core.model.Entity;
import com.hp.saas.agm.core.model.parser.EntityList;
import com.hp.saas.agm.manager.ApplicationManager;

import java.util.ArrayList;
import java.util.HashMap;

public class ReleaseSetupActivity extends Activity implements OnClickListener {
/*    private CustomListView lvSuggestList;
    private CustomListView lvFullList;*/
    private ExpandedTreeView expandedTreeView;
    private LoadingView lvLoading;
    private Context mContext;
    private ReleaseExpandTreeAdapter releaseAdapter;
    private HashMap<String, EntityList> releaseList = new HashMap<String, EntityList>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release_wizard);

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
        releaseAdapter = new ReleaseExpandTreeAdapter(mContext, releaseList);
        expandedTreeView.setAdapter(releaseAdapter);
        new NewsAsyncTask(lvLoading).execute(0);
    }

    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Entity entity= (Entity) parent.getAdapter().getItem(position);
            ApplicationManager.getMessageService().show(entity.getPropertyValue("name"));
        }
    };

    @Override
    public void onClick(View v) {

    }





    private class NewsAsyncTask extends AsyncTaskBase {
        EntityList recentReleases = null;
        EntityList recentSugguestReleases = null;

        public NewsAsyncTask(LoadingView loadingView) {
            super(loadingView);
        }

        @Override
        protected Integer doInBackground(Integer... params) {
            int result = -1;
            recentReleases = ApplicationManager.getSprintService().getReleases();
            recentSugguestReleases = ApplicationManager.getSprintService().getReleases();
            /*try {
                recentSugguestReleases = ApplicationManager.getSprintService().getClosestEntities("release");
            }catch(Exception e) {
                e.printStackTrace();
            }*/

            if (recentReleases.size() > 0) {
                result = 1;
            }
            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            releaseList.put("current", recentReleases);
            releaseList.put("coming", recentReleases);
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