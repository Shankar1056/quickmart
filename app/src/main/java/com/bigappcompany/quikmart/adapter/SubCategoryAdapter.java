package com.bigappcompany.quikmart.adapter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigappcompany.quikmart.R;
import com.bigappcompany.quikmart.model.CategoryModel;
import com.bigappcompany.quikmart.model.GroupModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 25 Jul 2017 at 5:53 PM
 */

public class SubCategoryAdapter extends RecyclerView.Adapter<SubCategoryAdapter.ViewHolder> {
	private final ArrayList<CategoryModel> mItemList = new ArrayList<>();
	private final OnItemClickListener mListener;
	
	public SubCategoryAdapter(OnItemClickListener listener) {
		mListener = listener;
	}
	
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		View itemView = inflater.inflate(R.layout.item_subcategory, parent, false);
		return new ViewHolder(itemView);
	}
	
	public void addItem(CategoryModel item) {
		mItemList.add(item);
		notifyItemInserted(mItemList.size() - 1);
	}
	
	public interface OnItemClickListener {
		void onViewAll(CategoryModel item);
		
		void onItemClick(GroupModel item);
	}
	
	@Override
	public int getItemCount() {
		return mItemList.size();
	}
	
	class ViewHolder extends RecyclerView.ViewHolder {
		TextView titleTV, viewAllTV;
		RecyclerView groupRV;
		GroupAdapter mAdapter;
		
		ViewHolder(View itemView) {
			super(itemView);
			
			titleTV = (TextView) itemView.findViewById(R.id.tv_title);
			viewAllTV = (TextView) itemView.findViewById(R.id.tv_view_all);
			groupRV = (RecyclerView) itemView.findViewById(R.id.rv_group);
			groupRV.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
			
			viewAllTV.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mListener.onViewAll(mItemList.get(getAdapterPosition()));
				}
			});
		}
	}
	
	private class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {
		private final ArrayList<GroupModel> mItemList;
		
		GroupAdapter(ArrayList<GroupModel> itemList) {
			this.mItemList = itemList;
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
		
		@Override
		public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			View itemView = inflater.inflate(R.layout.item_group, parent, false);
			return new ViewHolder(itemView);
		}
		
		@Override
		public void onBindViewHolder(ViewHolder holder, int position) {
			GroupModel item = mItemList.get(position);
			
			holder.titleTV.setText(item.getTitle());
			Picasso.with(holder.itemView.getContext())
				.load(item.getImageURL())
				.fit()
				.centerInside()
				.into(holder.imageIV);
		}
		
		@Override
		public int getItemCount() {
			return mItemList.size();
		}
		
		
	}
	
	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		CategoryModel item = mItemList.get(position);
		
		holder.titleTV.setText(item.getTitle());
		holder.mAdapter = new GroupAdapter(item.getGroupList());
		holder.groupRV.setAdapter(holder.mAdapter);
	}
	
	
}
