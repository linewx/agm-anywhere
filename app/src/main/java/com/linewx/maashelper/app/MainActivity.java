package com.linewx.maashelper.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.*;
import com.hp.alm.ali.manager.ApplicationManager;
import com.hp.alm.ali.model.Entity;
import com.hp.alm.ali.model.parser.EntityList;
import com.linewx.maashelper.app.adapter.ReleaseBacklogAdapter;
import com.linewx.maashelper.app.adapter.ReleaseFragmentAdapter;
import com.linewx.maashelper.app.view.CustomListView;
import com.linewx.maashelper.app.view.LoadingView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements OnClickListener {

    private CustomListView lvReleaseBacklog;
    private LoadingView lvLoading;
    private EntityList releaseBacklog = EntityList.empty();
    private Context mContext;

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
    }

    private void init() {
        lvReleaseBacklog.setAdapter(new ReleaseBacklogAdapter(this, releaseBacklog));

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

        new NewsAsyncTask(lvLoading).execute(0);
    }

    @Override
    public void onClick(View v) {

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
            //adapter.notifyDataSetChanged();
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
            recentStories = ApplicationManager.getSprintService().getStories();
            return recentStories;
        }

        @Override
        protected void onPostExecute(List<Entity> result) {
            super.onPostExecute(result);
            if (result != null) {
                for (Entity rc : recentStories) {
                    releaseBacklog.add(0, rc);
                }
                //adapter.notifyDataSetChanged();
                lvReleaseBacklog.onRefreshComplete();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

    }



}