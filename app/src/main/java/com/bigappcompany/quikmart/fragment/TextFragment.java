package com.bigappcompany.quikmart.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bigappcompany.quikmart.R;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 01 Aug 2017 at 6:37 PM
 */

public class TextFragment extends Fragment {
	private static final String ARG_CONTENT = "arg_content";
	
	public static TextFragment newInstance(String content) {
		Bundle args = new Bundle();
		args.putString(ARG_CONTENT, content);
		TextFragment fragment = new TextFragment();
		fragment.setArguments(args);
		return fragment;
	}
	
	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_text, container, false);
		TextView contentTV = (TextView) view.findViewById(R.id.tv_content);
		contentTV.setText(Html.fromHtml(getArguments().getString(ARG_CONTENT)));
		return view;
	}
}
