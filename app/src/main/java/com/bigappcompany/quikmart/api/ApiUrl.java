package com.bigappcompany.quikmart.api;


/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 2017-04-11 at 4:15 PM
 */

public final class ApiUrl {
	private static final String URL_BASE = "http://13.126.166.145/quikmart-backend/v1/";
	
	public static final String GENERATE_OTP = URL_BASE + "sendOtp";
	public static final String SUBMIT_OTP = URL_BASE + "VerifyOtp";
	public static final String REGISTER = URL_BASE + "userRegister";
	public static final String GCM_TOKEN_UPDATE = URL_BASE + "updatePushToken";
	
	public static final String SLIDERS = URL_BASE + "sliders";
	public static final String MAIN_CATEGORIES = URL_BASE + "categories";
	public static final String SUBCATEGORIES = URL_BASE + "subCategories/";
	
	public static final String CATEGORY_BY_GROUP = URL_BASE + "subCategories/";
	public static final String PRODUCTS_BY_CATEGORY = URL_BASE + "categoryProducts/";
	public static final String GET_PRODUCT_DETAILS = URL_BASE + "products/";
	public static final String CART = URL_BASE + "cart";
	public static final String CHECKOUT = URL_BASE + "checkout/";
	
	public static final String ADDRESS = URL_BASE + "CustomerAddress/";
	public static final String STATES = URL_BASE + "states";
	public static final String TIME_SLOT = URL_BASE + "timeslots";
	public static final String ORDER = URL_BASE + "order";
	public static final String PAYMENT_UPDATE = URL_BASE + "payment";
	public static final String ORDER_DETAILS = URL_BASE + "orderDetails/";
}
