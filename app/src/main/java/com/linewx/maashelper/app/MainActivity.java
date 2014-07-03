package com.linewx.maashelper.app;

import android.content.res.Resources;
import android.graphics.Matrix;
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
import com.linewx.maashelper.app.adapter.ReleaseFragmentAdapter;

import java.util.List;

public class MainActivity extends FragmentActivity implements OnClickListener {

    ViewPager mViewPager;
    private LinearLayout linearLayout1;
    private TextView textView1, textView2, textView3;
    private int currIndex = 0;
    private ImageView imageView;
    private int textViewW = 0;
    private List<View> listViews;
    private Resources resources;
    private View view1, view2, view3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resources = this.getResources();
        initControl();
        initViewPager();
        InitTextView();
        InitImageView();
    }

    private void initControl() {
        imageView = (ImageView) findViewById(R.id.cursor);
        linearLayout1 = (LinearLayout) findViewById(R.id.linearLayout1);
        mViewPager = (ViewPager) findViewById(R.id.page_content);
        mViewPager.setOffscreenPageLimit(2);
    }

    /* ��ʼ��ViewPager */
    private void initViewPager() {


        mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());
        mViewPager.setAdapter(new ReleaseFragmentAdapter(getSupportFragmentManager()));

    }





    private void InitTextView() {
        textView1 = (TextView) findViewById(R.id.story_tab);
        textView2 = (TextView) findViewById(R.id.task_tab);
        textView3 = (TextView) findViewById(R.id.backlog_tab);

        textView1.setOnClickListener(new MyOnClickListener(0));
        textView2.setOnClickListener(new MyOnClickListener(1));
        textView3.setOnClickListener(new MyOnClickListener(2));
    }

    @Override
    public void onClick(View v) {

    }

    public class MyPagerAdapter extends PagerAdapter {
        public List<View> mListViews;

        public MyPagerAdapter(List<View> mListViews) {
            this.mListViews = mListViews;
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView(mListViews.get(arg1));
        }

        @Override
        public void finishUpdate(View arg0) {
        }

        @Override
        public int getCount() {
            return mListViews.size();
        }

        @Override
        public Object instantiateItem(View arg0, int arg1) {
            ((ViewPager) arg0).addView(mListViews.get(arg1), 0);
            return mListViews.get(arg1);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == (arg1);
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View arg0) {
        }
    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageSelected(int arg0) {
            if (textViewW == 0) {
                textViewW = textView1.getWidth();
            }
            Animation animation = new TranslateAnimation(textViewW * currIndex,
                    textViewW * arg0, 0, 0);
            currIndex = arg0;
            animation.setFillAfter(true);
            animation.setDuration(300);
            imageView.startAnimation(animation);
            setTextTitleSelectedColor(arg0);
            setImageViewWidth(textViewW);

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }

    private void setTextTitleSelectedColor(int arg0) {
        int count = mViewPager.getChildCount();
        for (int i = 0; i < count; i++) {
            TextView mTextView = (TextView) linearLayout1.getChildAt(i);
            if (arg0 == i) {
                mTextView.setTextColor(0xffc80000);
            } else {
                mTextView.setTextColor(0xff969696);
            }
        }
    }

    private void setImageViewWidth(int width) {
        if (width != imageView.getWidth()) {
            LinearLayout.LayoutParams laParams = (LinearLayout.LayoutParams) imageView.getLayoutParams();
            laParams.width = width;
            imageView.setLayoutParams(laParams);
        }
    }

    private class MyOnClickListener implements OnClickListener {
        private int index = 0;

        public MyOnClickListener(int i) {
            index = i;
        }

        public void onClick(View v) {
            mViewPager.setCurrentItem(index);
        }
    }

    private void InitImageView() {
        Matrix matrix = new Matrix();
        matrix.postTranslate(0, 0);
        imageView.setImageMatrix(matrix);
    }

}