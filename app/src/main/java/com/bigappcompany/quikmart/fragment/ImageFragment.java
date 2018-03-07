package com.bigappcompany.quikmart.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bigappcompany.quikmart.R;
import com.squareup.picasso.Picasso;

/**
 * @author Samuel Robert <samuelrbrt16@gmail.com>
 * @created on 14 Mar 2017 at 3:03 PM
 */

public class ImageFragment extends Fragment {
	private static final String ARG_URL = "arg_url";
	
	public static ImageFragment newInstance(String url) {
		Bundle args = new Bundle();
		args.putString(ARG_URL, url);
		ImageFragment fragment = new ImageFragment();
		fragment.setArguments(args);
		return fragment;
	}
	
	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		ImageView view = (ImageView) inflater.inflate(R.layout.fragment_image, container, false);
		
		Picasso.with(getContext())
			.load(getArguments().getString(ARG_URL))
			.fit()
			.centerCrop()
			.into(view);
		
		return view;
	}
}
