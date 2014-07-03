package com.linewx.maashelper.app.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import com.hp.alm.ali.model.Entity;
import com.hp.alm.ali.model.parser.EntityList;
import com.linewx.maashelper.app.AsyncTaskBase;
import com.linewx.maashelper.app.R;
import com.linewx.maashelper.app.StoryActivity;
import com.linewx.maashelper.app.adapter.ReleaseBacklogAdapter;
import com.linewx.maashelper.app.view.CustomListView;
import com.linewx.maashelper.app.view.CustomListView.OnRefreshListener;
import com.linewx.maashelper.app.view.LoadingView;

import com.hp.alm.ali.manager.ApplicationManager;




import java.util.ArrayList;
import java.util.List;

public class ReleaseBacklogFragment extends Fragment {
	private static final String TAG = "StoriesFragment";
	private Context mContext;
	private View mBaseView;
	private CustomListView mCustomListView;
	private LoadingView mLoadingView;
	private View mSearchView;
	private ReleaseBacklogAdapter adapter;
	private EntityList releaseBacklog = EntityList.empty();



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContext = getActivity();
		mBaseView = inflater.inflate(R.layout.fragment_news, null);
		mSearchView = inflater.inflate(R.layout.common_search_l, null);
		findView();
		init();

        mCustomListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
                Entity entity= (Entity) parent.getAdapter().getItem(position);
                Bundle data = new Bundle();
                data.putSerializable("release", entity);
                Intent intent = new Intent(mContext, StoryActivity.class);
                intent.putExtras(data);
                startActivity(intent);
            }
        });

		return mBaseView;
	}

	private void findView() {
		mCustomListView = (CustomListView) mBaseView.findViewById(R.id.lv_news);
		mLoadingView = (LoadingView) mBaseView.findViewById(R.id.loading);

	}

	private void init() {
		adapter = new ReleaseBacklogAdapter(mContext, releaseBacklog, mCustomListView);
		mCustomListView.setAdapter(adapter);

		mCustomListView.addHeaderView(mSearchView);
		mCustomListView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				new AsyncRefresh().execute(0);
			}
		});
		mCustomListView.setCanLoadMore(false);
		new NewsAsyncTask(mLoadingView).execute(0);
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
				adapter.notifyDataSetChanged();
				mCustomListView.onRefreshComplete();
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

	}



}
