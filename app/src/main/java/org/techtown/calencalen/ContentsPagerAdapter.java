package org.techtown.calencalen;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import org.techtown.calencalen.FirstFragment;
import org.techtown.calencalen.SecondFragment;
import org.techtown.calencalen.ThirdFragment;


public class ContentsPagerAdapter extends FragmentStatePagerAdapter{
    private int mPageCount;



    public ContentsPagerAdapter(FragmentManager fm, int pageCount) {

        super(fm);

        this.mPageCount = pageCount;

    }



    @Override

    public Fragment getItem(int position) {

        switch (position) {

            case 0:
                FirstFragment FirstFragment = new FirstFragment();
                return FirstFragment;

            case 1:
                SecondFragment SecondFragment = new SecondFragment();
                return SecondFragment;

            case 2:
                ThirdFragment ThirdFragment = new ThirdFragment();
                return ThirdFragment;

            default:
                return null;

        }

    }

    @Override

    public int getCount() {

        return mPageCount;

    }
}
