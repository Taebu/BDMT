
package com.anp.bdmt;

import com.viewpagerindicator.CirclePageIndicator;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * @author Jung-Hum Cho Created by anp on 15. 6. 5..
 */
public class PaynowDialog extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.dialog_paynow, container, false);

        // remove dialog title
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        // remove dialog background
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // ViewPager viewPager =
        // (ViewPager)view.findViewById(R.id.paynow_pager);
        final WrapContentViewPager viewPager = (WrapContentViewPager)view
                .findViewById(R.id.paynow_pager);
        viewPager.setAdapter(new PaynowPagerAdapter(getChildFragmentManager()));

        CirclePageIndicator circlePageIndicator = (CirclePageIndicator)view
                .findViewById(R.id.paynow_indicator);
        circlePageIndicator.setViewPager(viewPager);
        circlePageIndicator.bringToFront();

        ImageView divider = (ImageView)view.findViewById(R.id.dialog_paynow_divider);

        LinearLayout downLayout = (LinearLayout)view.findViewById(R.id.dialog_paynow_down);
        downLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setAction(Intent.ACTION_VIEW);
                i.setData(Uri.parse("market://details?id=" + "com.lguplus.paynow"));
                startActivity(i);
            }
        });

        LinearLayout payLayout = (LinearLayout)view.findViewById(R.id.dialog_paynow_pay);
        boolean hasCall = getArguments().getBoolean("has_call");
        if (hasCall) {
            payLayout.setVisibility(View.VISIBLE);
            divider.setVisibility(View.VISIBLE);
            payLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    call();
                }
            });
        } else {
            payLayout.setVisibility(View.GONE);
            divider.setVisibility(View.GONE);
        }

        ImageView closeButton = (ImageView)view.findViewById(R.id.dialog_paynow_close);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }

    private void call() {

        String num = getArguments().getString("num");

        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + num));

        startActivity(intent);

        Intent menu = new Intent(getActivity(), CallService.class);

        menu.putExtra("pre_pay", getArguments().getString("pre_pay"));
        menu.putExtra("pay", getArguments().getString("pay"));
        menu.putExtra("img1", getArguments().getString("img1"));
        menu.putExtra("img2", getArguments().getString("img2"));

        getActivity().startService(menu);

        // mNum = "tel:" + getIntent().getStringExtra("tel");
        // startActivity(new Intent(Intent.ACTION_CALL,
        // Uri.parse("tel:010-3745-2742")));
        // PhoneCall.call(mNum, mActivity);
    }

    private class PaynowPagerAdapter extends FragmentPagerAdapter {

        public PaynowPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            Bundle bundle = new Bundle();
            bundle.putInt("page", position);

            Fragment fragment = new PaynowFragment();
            fragment.setArguments(bundle);

            return fragment;
        }

        @Override
        public int getCount() {
            return 4;
        }
    }
}
