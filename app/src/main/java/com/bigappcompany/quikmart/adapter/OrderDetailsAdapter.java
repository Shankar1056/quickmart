package com.bigappcompany.quikmart.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigappcompany.quikmart.R;
import com.bigappcompany.quikmart.model.OrderItemModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 16 Aug 2017 at 3:01 PM
 */

public class OrderDetailsAdapter extends RecyclerView.Adapter<OrderDetailsAdapter.ViewHolder> {
	private ArrayList<OrderItemModel> mItemList = new ArrayList<>();
	
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		View itemView = inflater.inflate(R.layout.item_order_details, parent, false);
		return new ViewHolder(itemView);
	}
	
	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		OrderItemModel item = mItemList.get(position);
		Context context = holder.itemView.getContext();
		
		holder.titleTV.setText(item.getTitle());
		holder.quantityTV.setText(context.getString(R.string.format_quantity, item.getQty()));
		holder.priceTV.setText(context.getString(R.string.format_price, item.getPrice()));
		
		Picasso.with(context)
			.load(item.getImageUrl())
			.fit()
			.centerInside()
			.into(holder.imageIV);
	}
	
	@Override
	public int getItemCount() {
		return mItemList.size();
	}
	
	public void addItems(ArrayList<OrderItemModel> itemList) {
		mItemList = itemList;
	}
	
	public class ViewHolder extends RecyclerView.ViewHolder {
		ImageView imageIV;
		TextView titleTV, quantityTV, priceTV;
		
		public ViewHolder(View itemView) {
			super(itemView);
			
			imageIV = (ImageView) itemView.findViewById(R.id.iv_image);
			titleTV = (TextView) itemView.findViewById(R.id.tv_title);
			quantityTV = (TextView) itemView.findViewById(R.id.tv_quantity);
			priceTV = (TextView) itemView.findViewById(R.id.tv_price);
		}
	}
}
