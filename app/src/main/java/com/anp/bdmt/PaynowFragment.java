
package com.anp.bdmt;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * @author anp Created by Jung-Hum Cho on 2016. 7. 12..
 */
public class PaynowFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_paynow, container, false);

        ImageView paynowImageView = (ImageView)view.findViewById(R.id.paynow_image);

        // bundle class get extra
        int page = getArguments().getInt("page");

        String[] imgUris = new String[] {
                "drawable://" + R.drawable.popup_paynow_1,
                "drawable://" + R.drawable.popup_paynow_2,
                "drawable://" + R.drawable.popup_paynow_3,
                "drawable://" + R.drawable.popup_paynow_4
        };

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getActivity())
                .build();
        ImageLoader.getInstance().init(config);
        ImageLoader.getInstance().displayImage(imgUris[page], paynowImageView);

        return view;
    }

}
