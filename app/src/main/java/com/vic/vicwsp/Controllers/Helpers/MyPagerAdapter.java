package com.vic.vicwsp.Controllers.Helpers;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.vic.vicwsp.Views.Fragments.OverdraftOrders;
import com.vic.vicwsp.Views.Fragments.PastOrders;
import com.vic.vicwsp.Views.Fragments.ReceivedOrders;

public class MyPagerAdapter extends FragmentPagerAdapter {

    //View pager adapter for the order screen

    private String[] titles;

    public MyPagerAdapter(FragmentManager fm, String[] titles) {
        super(fm);
        this.titles = titles;
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new PastOrders();
            case 1:
                return new ReceivedOrders();
            case 2:
                return new OverdraftOrders();
            default:
                break;
        }
        return null;
    }
}