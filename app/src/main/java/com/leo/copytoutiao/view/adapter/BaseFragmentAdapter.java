package com.leo.copytoutiao.view.adapter;

import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import java.util.List;

/**
 * Created by 社会主义好
 */

public class BaseFragmentAdapter extends FragmentPagerAdapter {
    private String[] mTitles;
    private int mCount;
    private List<Fragment> mFragments;

    public BaseFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    public BaseFragmentAdapter(FragmentManager fm, int count, String[] titles, List<Fragment> fragments){
        this(fm);
        mCount = count;
        mTitles = titles;
        mFragments = fragments;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (mTitles != null){
            return mTitles[position];
        }
        return null;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mCount;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }
}
