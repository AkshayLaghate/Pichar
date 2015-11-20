package com.indcoders.pikcharhindimovies;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Akki on 19/11/15.
 */
public class MoviesPagerAdapter extends FragmentPagerAdapter {

    String[] Titles = {"Popular","This Week","Next Week"};

    public MoviesPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return Titles[position];
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new MovieListFragment();
            case 1:
                return new ThisWeekFragment();
            case 2:
                return new NextWeekFragment();
        }
        return new MovieListFragment();
    }

    @Override
    public int getCount() {
        return Titles.length;
    }
}
