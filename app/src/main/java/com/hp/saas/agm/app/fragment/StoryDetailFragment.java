package com.hp.saas.agm.app.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import com.hp.saas.agm.core.entity.EntityQuery;
import com.hp.saas.agm.manager.ApplicationManager;
import com.hp.saas.agm.core.model.Entity;
import com.hp.saas.agm.core.model.parser.EntityList;
import com.hp.saas.agm.app.R;
import com.hp.saas.agm.app.StoryActivity;
import com.hp.saas.agm.app.adapter.ReleaseBacklogAdapter;

public class StoryDetailFragment extends Fragment {
	private static final String TAG = "StoriesFragment";
	private Context mContext;
	private View mBaseView;
    private EditText etDescription;
	private View mSearchView;
	private ReleaseBacklogAdapter adapter;
	private EntityList releaseBacklog = EntityList.empty();
    private Entity release;
    private Entity story;



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContext = getActivity();
        //todo: show story detail
		mBaseView = inflater.inflate(R.layout.fragment_story_detail, null);
		findView();
		init();



        StoryActivity storyActivity = (StoryActivity)getActivity();
        release = storyActivity.getStory();

        return mBaseView;
	}

	private void findView() {
/*		mCustomListView = (CustomListView) mBaseView.findViewById(R.id.lv_news);
		mLoadingView = (LoadingView) mBaseView.findViewById(R.id.loading);*/
        etDescription = (EditText) mBaseView.findViewById(R.id.description);
	}

	private void init() {
        new NewsAsyncTask().execute(0);
	}

    private Entity getStory() {
        EntityQuery query = new EntityQuery("requirement");
        query.addColumn("id", 1);
        query.addColumn("name", 1);
        query.addColumn("description", 1);
        query.setValue("id", String.valueOf(release.getPropertyValue("entity-id")));
        EntityList list = EntityList.empty();
        try {
            list = ApplicationManager.getEntityService().query(query);
        } catch(Exception e) {
            e.printStackTrace();
        }finally {

        }

        if (list.size() > 0) {
            return list.get(0);
        }else {
            return null;
        }
    }

	private class NewsAsyncTask extends AsyncTask<Integer, Integer, Integer> {
		Entity recentStory;

		public NewsAsyncTask() {

		}

		@Override
		protected Integer doInBackground(Integer... params) {
			int result = -1;

            recentStory = getStory();
			if (recentStory != null) {
				result = 1;
			}
			return result;
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			//releaseBacklog.addAll(recentReleaseBacklog);
            etDescription.setText(Html.fromHtml(recentStory.getPropertyValue("description")));
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

	}

	/*private class AsyncRefresh extends
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
				//mCustomListView.onRefreshComplete();
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

	}*/



}
