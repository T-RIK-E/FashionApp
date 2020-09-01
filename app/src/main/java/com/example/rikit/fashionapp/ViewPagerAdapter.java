package com.example.rikit.fashionapp;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:

                return new redFragment(); //ChildFragment1 at position 0
            case 1:
                return new browseFragment(); //ChildFragment2 at position 1
            case 2:
                return new greenFragment(); //ChildFragment3 at position 2

        }
        return null; //does not happen
    }

    @Override
    public int getCount() {
        return 3; //three fragments
    }
}