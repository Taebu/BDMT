
package kr.co.cashqc;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import static kr.co.cashqc.MainActivity.SALE_ZONE;

/**
 * The type Tabs pager adapter.
 */
public class ShopListPagerAdapter extends FragmentPagerAdapter {

    public ShopListPagerAdapter(FragmentManager fm, double lat, double lng, int distance) {
        super(fm);
        mLat = lat;
        mLng = lng;
        mDistance = distance;
    }

    double mLat, mLng;

    int mDistance;

    @Override
    public Fragment getItem(int position) {
        // position++;
        // return new ShopListFragment(position);

        Fragment slf = new ShopListFragment();
        Bundle args = new Bundle();
        // Our object is just an integer :-P

//        if (SALE_ZONE) {
//            args.putInt("mType", position);
//        } else {
//            args.putInt("mType", position + 1);
//        }

        args.putInt("mType", SALE_ZONE ? position : position + 1);

        args.putDouble("lat", mLat);
        args.putDouble("lng", mLng);
        args.putInt("distance", mDistance);
        slf.setArguments(args);

        return slf;
    }

    // @Override
    // public Fragment getItem(int position) {
    // return ShopListFragment.newInstance(position+1);
    // }

    @Override
    public CharSequence getPageTitle(int position) {
        String[] mTabs = {"치킨", "피자", "중식", "한식", "닭발", "야식", "족발", "일식"};
        return mTabs[position];
    }

    @Override
    public int getCount() {
        return 9;
    }
}