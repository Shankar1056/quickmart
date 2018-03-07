package com.bigappcompany.quikmart.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigappcompany.quikmart.R;
import com.bigappcompany.quikmart.listener.OnCartListener;
import com.bigappcompany.quikmart.model.CartItemModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * @author Samuel Robert <samuelrbrt16@gmail.com>
 * @created on 18 Mar 2017 at 11:01 AM
 */

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
	private final ArrayList<CartItemModel> mItemList = new ArrayList<>();
	private final OnCartListener mListener;
	
	public CartAdapter(OnCartListener listener) {
		mListener = listener;
	}
	
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		View itemView = inflater.inflate(R.layout.item_cart, parent, false);
		return new ViewHolder(itemView);
	}
	
	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		Context context = holder.itemView.getContext();
		CartItemModel item = mItemList.get(position);
		
		holder.titleTV.setText(item.getTitle());
		holder.qtyTV.setText(String.valueOf(item.getQty()) + " kg");
		holder.sizeTV.setText(String.valueOf(item.getQty()));
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
	
	public void addItem(CartItemModel item) {
		mItemList.add(item);
		notifyItemInserted(mItemList.size() - 1);
	}
	
	public void clear() {
		mItemList.clear();
		notifyDataSetChanged();
	}
	
	public double getTotalPrice() {
		double price = 0;
		for (CartItemModel item : mItemList) {
			price += item.getPrice() * item.getQty();
		}
		return price;
	}
	
	class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		ImageView imageIV;
		TextView titleTV, qtyTV, priceTV, sizeTV;
		
		ViewHolder(View itemView) {
			super(itemView);
			
			imageIV = (ImageView) itemView.findViewById(R.id.iv_image);
			titleTV = (TextView) itemView.findViewById(R.id.tv_title);
			qtyTV = (TextView) itemView.findViewById(R.id.tv_quantity);
			priceTV = (TextView) itemView.findViewById(R.id.tv_price);
			sizeTV = (TextView) itemView.findViewById(R.id.tv_size);
			itemView.findViewById(R.id.tv_inc_quantity).setOnClickListener(this);
			itemView.findViewById(R.id.tv_dec_quantity).setOnClickListener(this);
		}
		
		@Override
		public void onClick(View v) {
			CartItemModel item = mItemList.get(getAdapterPosition());
			
			switch (v.getId()) {
				case R.id.tv_inc_quantity:
					item.setQty(item.getQty() + 1);
					mListener.onCartUpdate(item);
					break;
				
				case R.id.tv_dec_quantity:
					item.setQty(item.getQty() - 1);
					mListener.onCartUpdate(item);
					break;
			}
		}
	}
}
