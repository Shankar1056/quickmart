package com.bigappcompany.quikmart.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Samuel Robert <samuelrbrt16@gmail.com>
 * @created on 14 Mar 2017 at 4:28 PM
 */

public class CategoryModel implements Serializable {
	private final String categoryId;
	private final String imageURL;
	private final String title;
	private final ArrayList<GroupModel> groupList = new ArrayList<>();
	
	public CategoryModel(JSONObject item) throws JSONException {
		categoryId = item.getString("id_category");
		title = item.getString("name_category");
		imageURL = item.getString("category_image");
		
		JSONArray groups = item.getJSONArray("sub_categories");
		for (int i = 0; i < groups.length(); i++) {
			groupList.add(new GroupModel(groups.getJSONObject(i)));
		}
	}
	
	public String getImageURL() {
		return imageURL;
	}
	
	public String getTitle() {
		return title;
	}
	
	public ArrayList<GroupModel> getGroupList() {
		return groupList;
	}
	
	public String getCategoryId() {
		return categoryId;
	}
}
