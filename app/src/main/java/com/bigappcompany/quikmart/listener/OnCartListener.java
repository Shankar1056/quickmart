package com.bigappcompany.quikmart.listener;

import com.bigappcompany.quikmart.model.CartItemModel;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 02 Aug 2017 at 7:13 PM
 */

public interface OnCartListener {
	void onCartUpdate(CartItemModel item);
}
