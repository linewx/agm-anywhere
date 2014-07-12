package com.hp.saas.agm.app.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;
import com.hp.saas.agm.app.fragment.StoryDetailFragment;
import com.hp.saas.agm.app.fragment.StoryTaskFragment;

/**
 * Created by lugan on 6/21/2014.
 */
public class StoryFragmentAdapter extends FragmentPagerAdapter{
    public StoryFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        //todo: show detail/task/attachment
        if(position == 0) {
            return new StoryDetailFragment();
        }else if(position == 1) {
            return new StoryTaskFragment();
        }
        return new StoryDetailFragment();
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // 这里Destroy的是Fragment的视图层次，并不是Destroy Fragment对象
        super.destroyItem(container, position, object);
        //Log.i("INFO", "Destroy Item...");
    }
}
