package com.bigappcompany.quikmart.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bigappcompany.quikmart.R;
import com.bigappcompany.quikmart.model.DateModel;

import java.util.Calendar;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 10 Aug 2017 at 12:56 PM
 */

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.ViewHolder> {
	private final OnItemClickListener mListener;
	private int mSelectedPosition = 0;
	
	public DateAdapter(OnItemClickListener listener) {
		this.mListener = listener;
	}
	
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		View itemView = inflater.inflate(R.layout.item_combination, parent, false);
		return new ViewHolder(itemView);
	}
	
	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, position);
		
		holder.titleTV.setText(getRelativeTime(calendar, position));
		holder.titleTV.setSelected(mSelectedPosition == position);
	}
	
	private String getRelativeTime(Calendar calendar, int position) {
		return position == 0 ? "Today" : position == 1 ? "Tomorrow" : DateFormat.format("EEE, dd MMM", calendar).toString();
	}
	
	@Override
	public int getItemCount() {
		return 7;
	}
	
	public DateModel getSelectedDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, mSelectedPosition);
		return new DateModel(calendar);
	}
	
	public interface OnItemClickListener {
		void onDateChanged(int position);
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
					mListener.onDateChanged(getAdapterPosition());
				}
			});
		}
	}
}
