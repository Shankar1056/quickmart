package com.bigappcompany.quikmart.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bigappcompany.quikmart.R;
import com.bigappcompany.quikmart.model.TimeModel;

import java.util.ArrayList;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 10 Aug 2017 at 12:56 PM
 */

public class TimeAdapter extends RecyclerView.Adapter<TimeAdapter.ViewHolder> {
	private final ArrayList<TimeModel> mItemList = new ArrayList<>();
	private final ArrayList<TimeModel> mFilteredList = new ArrayList<>();
	private int mSelectedPosition = 0;
	
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		View itemView = inflater.inflate(R.layout.item_combination, parent, false);
		return new ViewHolder(itemView);
	}
	
	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		TimeModel item = mFilteredList.get(position);
		
		holder.titleTV.setText(item.getTimeSlot());
		holder.titleTV.setSelected(mSelectedPosition == position);
	}
	
	@Override
	public int getItemCount() {
		return mFilteredList.size();
	}
	
	public TimeModel getSelectedTime() {
		return mFilteredList.get(mSelectedPosition);
	}
	
	public void addItem(TimeModel item) {
		mItemList.add(item);
		mFilteredList.add(item);
		notifyItemInserted(mFilteredList.size() - 1);
	}
	
	public void notifyDateChanged(int position) {
		
	}
	
	class ViewHolder extends RecyclerView.ViewHolder {
		TextView titleTV;
		
		ViewHolder(View itemView) {
			super(itemView);
			
			titleTV = (TextView) itemView;
			titleTV.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mSelectedPosition = getAdapterPosition();
					notifyDataSetChanged();
				}
			});
		}
	}
}
