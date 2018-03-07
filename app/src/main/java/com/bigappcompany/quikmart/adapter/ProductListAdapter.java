package com.bigappcompany.quikmart.adapter;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigappcompany.quikmart.R;
import com.bigappcompany.quikmart.model.ProductModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 31 Jul 2017 at 12:15 PM
 */

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ViewHolder> {
	private final ArrayList<ProductModel> mItemList = new ArrayList<>();
	private final OnItemClickListener mListener;
	private Dialog dialog;
	
	public ProductListAdapter(OnItemClickListener listener) {
		mListener = listener;
	}
	
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		View itemView = inflater.inflate(R.layout.item_product_list, parent, false);
		return new ViewHolder(itemView);
	}
	
	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		ProductModel item = mItemList.get(position);
		Context context = holder.itemView.getContext();
		
		holder.titleTV.setText(item.getTitle());
		holder.priceTV.setText(context.getString(R.string.format_price, item.getPrice()));
		
		if (!item.getDiscount().isEmpty()) {
			holder.offTV.setVisibility(View.VISIBLE);
			holder.offTV.setText(item.getDiscount());
		} else {
			holder.offTV.setVisibility(View.INVISIBLE);
		}
		
		if (item.getSelectedIndex() == -1) {
			holder.combinationTV.setVisibility(View.INVISIBLE);
		} else {
			holder.combinationTV.setVisibility(View.VISIBLE);
			holder.combinationTV.setText(item.getCombinationName());
		}
		
		if (item.getQuantity() > 0) {
			holder.decTV.setVisibility(View.VISIBLE);
			holder.quantityTV.setVisibility(View.VISIBLE);
			holder.quantityTV.setText(String.valueOf(item.getQuantity()));
		} else {
			holder.decTV.setVisibility(View.INVISIBLE);
			holder.quantityTV.setVisibility(View.INVISIBLE);
		}
		
		if (!TextUtils.isEmpty(item.getImageUrl())) {
			Picasso.with(context)
				.load(item.getImageUrl())
				.fit()
				.centerInside()
				.into(holder.imageIV);
		}
	}
	
	@Override
	public int getItemCount() {
		return mItemList.size();
	}
	
	public void addItem(ProductModel item) {
		mItemList.add(item);
		notifyItemInserted(mItemList.size() - 1);
	}
	
	public interface OnItemClickListener {
		void onItemClick(ProductModel item);
		
		void onQuantityUpdate(ProductModel item);
	}
	
	class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, CombinationAdapter.OnItemClickListener {
		ImageView imageIV;
		TextView titleTV, offTV, combinationTV, priceTV, decTV, quantityTV, incTV;
		
		ViewHolder(View itemView) {
			super(itemView);
			
			imageIV = (ImageView) itemView.findViewById(R.id.iv_image);
			titleTV = (TextView) itemView.findViewById(R.id.tv_title);
			offTV = (TextView) itemView.findViewById(R.id.tv_off);
			combinationTV = (TextView) itemView.findViewById(R.id.tv_quantity);
			priceTV = (TextView) itemView.findViewById(R.id.tv_price);
			decTV = (TextView) itemView.findViewById(R.id.tv_dec_quantity);
			quantityTV = (TextView) itemView.findViewById(R.id.tv_size);
			incTV = (TextView) itemView.findViewById(R.id.tv_inc_quantity);
			
			combinationTV.setOnClickListener(this);
			decTV.setOnClickListener(this);
			incTV.setOnClickListener(this);
			itemView.setOnClickListener(this);
		}
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.tv_quantity:
					showCombinationDialog();
					break;
				
				case R.id.tv_dec_quantity:
					ProductModel item = mItemList.get(getAdapterPosition());
					item.setQuantity(item.getQuantity() - 1);
					notifyItemChanged(getAdapterPosition());
					mListener.onQuantityUpdate(item);
					break;
				
				case R.id.tv_inc_quantity:
					item = mItemList.get(getAdapterPosition());
					item.setQuantity(item.getQuantity() + 1);
					notifyItemChanged(getAdapterPosition());
					mListener.onQuantityUpdate(item);
					break;
				
				default:
					mListener.onItemClick(mItemList.get(getAdapterPosition()));
			}
		}
		
		private void showCombinationDialog() {
			ProductModel item = mItemList.get(getAdapterPosition());
			dialog = new Dialog(itemView.getContext(), R.style.AppThemeDialog);
			dialog.setContentView(R.layout.dialog_combination);
			dialog.show();
			RecyclerView combinationRV = (RecyclerView) dialog.findViewById(R.id.rv_combination);
			combinationRV.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
			combinationRV.setHasFixedSize(true);
			combinationRV.setAdapter(new CombinationAdapter(item, this));
		}
		
		@Override
		public void onItemClick(int position) {
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
			
			mItemList.get(getAdapterPosition()).setSelectedIndex(position);
			notifyItemChanged(getAdapterPosition());
		}
	}
}
