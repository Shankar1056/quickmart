package com.bigappcompany.quikmart.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 14 Mar 2017 at 11:34 AM
 */

public class ViewPagerAdapter extends FragmentPagerAdapter {
	private final ArrayList<Fragment> mItemList;
	private final ArrayList<CharSequence> mTitleList;
	
	public ViewPagerAdapter(FragmentManager fm) {
		super(fm);
		mItemList = new ArrayList<>();
		mTitleList = new ArrayList<>();
	}
	
	public ViewPagerAdapter(FragmentManager fm, ArrayList<Fragment> itemList, ArrayList<CharSequence> titleList) {
		super(fm);
		mItemList = itemList;
		mTitleList = titleList;
	}
	
	public void addItem(Fragment item, CharSequence title) {
		mItemList.add(item);
		mTitleList.add(title);
		notifyDataSetChanged();
	}
	
	public void addItem(Fragment item) {
		mItemList.add(item);
		notifyDataSetChanged();
	}
	
	@Override
	public Fragment getItem(int position) {
		return mItemList.get(position);
	}
	
	@Override
	public int getCount() {
		return mItemList.size();
	}
	
	@Override
	public CharSequence getPageTitle(int position) {
		try {
			return mTitleList.get(position);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}
}
