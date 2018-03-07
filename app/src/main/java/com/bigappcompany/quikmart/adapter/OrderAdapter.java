package com.bigappcompany.quikmart.adapter;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bigappcompany.quikmart.R;
import com.bigappcompany.quikmart.model.OrderModel;

import java.util.ArrayList;

/**
 * @author Samuel Robert <samuelrbrt16@gmail.com>
 * @created on 20 Mar 2017 at 4:55 PM
 */

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
	private final ArrayList<OrderModel> mItemList = new ArrayList<>();
	private final OnItemClickListener mListener;
	
	public OrderAdapter(OnItemClickListener listener) {
		mListener = listener;
	}
	
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		View itemView = inflater.inflate(R.layout.item_order, parent, false);
		return new ViewHolder(itemView);
	}
	
	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		OrderModel item = mItemList.get(position);
		Resources res = holder.itemView.getResources();
		
		holder.dateTV.setText(item.getDate());
		holder.orderIdTV.setText(res.getString(R.string.format_order_id, item.getOrderId()));
		holder.orderStatusTV.setText(item.getOrderStatus());
		holder.totalTV.setText(res.getString(R.string.format_price, item.getTotal()));
	}
	
	@Override
	public int getItemCount() {
		return mItemList.size();
	}
	
	public void addItem(OrderModel item) {
		mItemList.add(item);
		notifyItemInserted(mItemList.size() - 1);
	}
	
	public interface OnItemClickListener {
		void onItemClick(OrderModel item);
	}
	
	class ViewHolder extends RecyclerView.ViewHolder {
		TextView dateTV, orderIdTV, orderStatusTV, totalTV;
		
		ViewHolder(View itemView) {
			super(itemView);
			
			dateTV = (TextView) itemView.findViewById(R.id.tv_date);
			orderIdTV = (TextView) itemView.findViewById(R.id.tv_order_id);
			orderStatusTV = (TextView) itemView.findViewById(R.id.tv_order_status);
			totalTV = (TextView) itemView.findViewById(R.id.tv_total);
			
			itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mListener.onItemClick(mItemList.get(getAdapterPosition()));
				}
			});
		}
	}
}
