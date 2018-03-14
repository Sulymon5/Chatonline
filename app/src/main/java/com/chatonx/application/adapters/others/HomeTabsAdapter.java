package com.chatonx.application.adapters.others;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.chatonx.application.fragments.home.CallsFragment;
import com.chatonx.application.fragments.home.ConversationsFragment;

/**
 * Created by Abderrahim El imame on 27/02/2016.
 * Email : abderrahim.elimame@gmail.com
 */
public class HomeTabsAdapter extends FragmentStatePagerAdapter {


    public HomeTabsAdapter(FragmentManager fm) {
        super(fm);
    }


    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new ConversationsFragment();
            case 1:
                return new CallsFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

}