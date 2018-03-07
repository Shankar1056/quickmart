package com.bigappcompany.quikmart.adapter;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.bigappcompany.quikmart.R;
import com.bigappcompany.quikmart.model.AddressModel;

import java.util.ArrayList;

/**
 * @author Samuel Robert <samuelrbrt16@gmail.com>
 * @created on 18 Mar 2017 at 4:46 PM
 */

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {
	private final ArrayList<AddressModel> mItemList = new ArrayList<>();
	private final OnItemClickListener mListener;
	private int mSelectedPosition = 0;
	
	public AddressAdapter(OnItemClickListener listener) {
		mListener = listener;
	}
	
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address, parent, false);
		return new ViewHolder(itemView);
	}
	
	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		AddressModel item = mItemList.get(position);
		holder.chosenRB.setChecked(mSelectedPosition == position);
		holder.addressTV.setText(item.getAddress());
	}
	
	@Override
	public int getItemCount() {
		return mItemList.size();
	}
	
	public void addItem(AddressModel item) {
		mItemList.add(item);
		notifyItemInserted(mItemList.size() - 1);
	}
	
	public void updateItem(AddressModel address, int position) {
		mItemList.set(position, address);
		notifyItemChanged(position);
	}
	
	public void deleteItem(int position) {
		mItemList.remove(position);
		mSelectedPosition = mSelectedPosition >= mItemList.size() ? mItemList.size() - 1 : mSelectedPosition;
		notifyDataSetChanged();
	}
	
	public AddressModel getSelectedAddress() {
		return mItemList.get(mSelectedPosition);
	}
	
	public interface OnItemClickListener {
		void onUpdate(AddressModel item, int position);
		
		void onDelete(AddressModel item, int position);
	}
	
	class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		private final RadioButton chosenRB;
		private final TextView addressTV;
		private final ImageView deleteIV;
		
		ViewHolder(View itemView) {
			super(itemView);
			
			chosenRB = (RadioButton) itemView.findViewById(R.id.rb_address);
			addressTV = (TextView) itemView.findViewById(R.id.tv_address);
			deleteIV = (ImageView) itemView.findViewById(R.id.iv_delete);
			chosenRB.setOnClickListener(this);
			deleteIV.setOnClickListener(this);
			itemView.setOnClickListener(this);
		}
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.rb_address:
					mSelectedPosition = getAdapterPosition();
					notifyDataSetChanged();
					break;
				
				case R.id.iv_delete:
					new AlertDialog.Builder(itemView.getContext())
						.setTitle(R.string.title_delete_address)
						.setMessage(R.string.delete_address)
						.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								mListener.onDelete(mItemList.get(getAdapterPosition()), getAdapterPosition());
							}
						})
						.setNegativeButton(android.R.string.cancel, null)
						.create()
						.show();
					break;
				
				default:
					mListener.onUpdate(mItemList.get(getAdapterPosition()), getAdapterPosition());
			}
		}
	}
}
