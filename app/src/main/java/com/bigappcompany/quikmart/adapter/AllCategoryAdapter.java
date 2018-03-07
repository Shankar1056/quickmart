package com.bigappcompany.quikmart.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigappcompany.quikmart.R;
import com.bigappcompany.quikmart.model.GroupModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 28 Jul 2017 at 3:05 PM
 */

public class AllCategoryAdapter extends RecyclerView.Adapter<AllCategoryAdapter.ViewHolder> {
	private final ArrayList<GroupModel> mItemList = new ArrayList<>();
	private final OnItemClickListener mListener;
	
	public AllCategoryAdapter(OnItemClickListener listener) {
		mListener = listener;
	}
	
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		View itemView = inflater.inflate(R.layout.item_all_category, parent, false);
		return new ViewHolder(itemView);
	}
	
	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		GroupModel item = mItemList.get(position);
		Context context = holder.itemView.getContext();
		
		holder.titleTV.setText(item.getTitle());
		Picasso.with(context)
			.load(item.getImageURL())
			.fit()
			.centerInside()
			.into(holder.imageIV);
	}
	
	@Override
	public int getItemCount() {
		return mItemList.size();
	}
	
	public void addItem(GroupModel item) {
		mItemList.add(item);
		notifyItemInserted(mItemList.size() - 1);
	}
	
	public interface OnItemClickListener {
		void onItemClick(GroupModel item);
	}
	
	class ViewHolder extends RecyclerView.ViewHolder {
		ImageView imageIV;
		TextView titleTV;
		
		ViewHolder(View itemView) {
			super(itemView);
			
			imageIV = (ImageView) itemView.findViewById(R.id.iv_image);
			titleTV = (TextView) itemView.findViewById(R.id.tv_title);
			
			itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mListener.onItemClick(mItemList.get(getAdapterPosition()));
				}
			});
		}
	}
}
