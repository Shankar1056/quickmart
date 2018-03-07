package com.bigappcompany.quikmart.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bigappcompany.quikmart.R;

/**
 * @author Samuel Robert <samuelrbrt16@gmail.com>
 * @created on 20 Mar 2017 at 1:21 PM
 */

public class ProductCartAdapter extends RecyclerView.Adapter<ProductCartAdapter.ViewHolder> {
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_cart, parent, false);
		return new ViewHolder(itemView);
	}
	
	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		
	}
	
	@Override
	public int getItemCount() {
		return 15;
	}
	
	class ViewHolder extends RecyclerView.ViewHolder {
		ViewHolder(View itemView) {
			super(itemView);
		}
	}
}
