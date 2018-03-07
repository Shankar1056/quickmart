package com.bigappcompany.quikmart.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 25 Jul 2017 at 6:08 PM
 */

public class GroupModel implements Serializable {
	private final String title;
	private final String imageURL;
	private String groupId;
	
	public GroupModel(JSONObject item) throws JSONException {
		groupId = item.getString("id_category");
		title = item.getString("name");
		imageURL = item.getString("image");
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getImageURL() {
		return imageURL;
	}
	
	public String getGroupId() {
		return groupId;
	}
}
