package com.bigappcompany.quikmart.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bigappcompany.quikmart.R;
import com.bigappcompany.quikmart.model.CombinationModel;
import com.bigappcompany.quikmart.model.ProductModel;

import java.util.ArrayList;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 01 Aug 2017 at 4:16 PM
 */

public class CombinationAdapter extends RecyclerView.Adapter<CombinationAdapter.ViewHolder> {
	private final ArrayList<CombinationModel> mItemList;
	private final ProductModel mProduct;
	private final OnItemClickListener mListener;
	
	public CombinationAdapter(ProductModel product, OnItemClickListener listener) {
		mProduct = product;
		mItemList = mProduct.getCombinationList();
		mListener = listener;
	}
	
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		View itemView = inflater.inflate(R.layout.item_combination, parent, false);
		return new ViewHolder(itemView);
	}
	
	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		CombinationModel item = mItemList.get(position);
		
		holder.combinationTV.setSelected(position == mProduct.getSelectedIndex());
		holder.combinationTV.setText(item.getCombination());
	}
	
	@Override
	public int getItemCount() {
		return mItemList.size();
	}
	
	public interface OnItemClickListener {
		void onItemClick(int position);
	}
	
	class ViewHolder extends RecyclerView.ViewHolder {
		TextView combinationTV;
		
		ViewHolder(View itemView) {
			super(itemView);
			
			combinationTV = (TextView) itemView;
			itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mListener.onItemClick(getAdapterPosition());
				}
			});
		}
	}
}
