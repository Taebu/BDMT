
package com.anp.bdmt;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * The type Tabs pager adapter.
 */
public class ShopListPagerAdapter extends FragmentStatePagerAdapter {

    public ShopListPagerAdapter(FragmentManager fm, double lat, double lng,
            String[] tabs, String[] types, boolean life) {
        super(fm);
        mLat = lat;
        mLng = lng;
        mLife = life;
        mTabs = tabs;
        mTypes = types;
    }

    private String[] mTabs;

    private boolean mLife;

    private double mLat, mLng;

    private String[] mTypes;

    @Override
    public Fragment getItem(int position) {

        Bundle args = new Bundle();

        args.putInt("position", position);
        args.putString("type", mTypes[position]);
        args.putDouble("lat", mLat);
        args.putDouble("lng", mLng);

        ShopListFragment shopListFragment = new ShopListFragment();
        shopListFragment.setArguments(args);

        return shopListFragment;
    }

    @Override
    public int getCount() {
        return mTabs.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTabs[position];
    }

}
