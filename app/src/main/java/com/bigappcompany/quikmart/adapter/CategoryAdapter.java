package com.bigappcompany.quikmart.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bigappcompany.quikmart.R;
import com.bigappcompany.quikmart.model.CategoryModel;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * @author Samuel Robert <samuelrbrt16@gmail.com>
 * @created on 14 Mar 2017 at 4:25 PM
 */

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
	private final ArrayList<CategoryModel> mItemList = new ArrayList<>();
	private final OnItemClickListener mListener;
	
	public CategoryAdapter(OnItemClickListener listener) {
		mListener = listener;
	}
	
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		View itemView = inflater.inflate(R.layout.item_category, parent, false);
		return new ViewHolder(itemView);
	}
	
	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		CategoryModel item = mItemList.get(position);
		
		holder.titleTV.setText(item.getTitle());
		Picasso.with(holder.itemView.getContext())
			.load(item.getImageURL())
			.fit()
			.centerCrop()
			.into(holder.imageIV);
	}
	
	@Override
	public int getItemCount() {
		return mItemList.size();
	}
	
	public void addItem(CategoryModel item) {
		mItemList.add(item);
		notifyItemInserted(mItemList.size() - 1);
	}
	
	public interface OnItemClickListener {
		void onItemClick(CategoryModel item);
	}
	
	class ViewHolder extends RecyclerView.ViewHolder {
		RoundedImageView imageIV;
		TextView titleTV;
		
		ViewHolder(View itemView) {
			super(itemView);
			
			imageIV = (RoundedImageView) itemView.findViewById(R.id.iv_image);
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
