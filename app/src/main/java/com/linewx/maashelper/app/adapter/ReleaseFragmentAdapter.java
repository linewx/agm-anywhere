package com.linewx.maashelper.app.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;
import com.linewx.maashelper.app.fragment.ReleaseBacklogFragment;

/**
 * Created by lugan on 6/21/2014.
 */
public class ReleaseFragmentAdapter extends FragmentPagerAdapter{
    public ReleaseFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return new ReleaseBacklogFragment();
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
