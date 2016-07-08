
package com.anp.bdmt;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author anp Created by Jung-Hum Cho on 2016. 5. 20..
 */
public class InitializeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_initialize, container, false);

        ImageView initializeImageView = (ImageView)view.findViewById(R.id.initialize_image);

        // bundle class get extra
        int page = getArguments().getInt("page");

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getActivity())
                .build();

        ImageLoader.getInstance().init(config);
        TextView skip = (TextView)view.findViewById(R.id.skip);
        switch (page) {
            case 0:
                ImageLoader.getInstance().displayImage("drawable://" + R.drawable.initialize_1,
                        initializeImageView);
                skip.setVisibility(View.GONE);
                break;
            case 1:
                ImageLoader.getInstance().displayImage("drawable://" + R.drawable.initialize_2,
                        initializeImageView);
                skip.setVisibility(View.GONE);
                break;
            case 2:
                ImageLoader.getInstance().displayImage(
                        "drawable://" + R.drawable.initialize_3_commented, initializeImageView);
                skip.setVisibility(View.VISIBLE);
                break;
        }

        return view;
    }

}
