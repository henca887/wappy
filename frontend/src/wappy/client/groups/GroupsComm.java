package wappy.client.groups;

import wappy.client.ResponseHandler;
import wappy.client.ServerComm;

import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

public class GroupsComm {
	private static final String URL_CREATE_GROUP = "/groups/create_group/";
	private static final String URL_GET_GROUPS = "/groups/get_groups/";
	private static final String URL_ADD_MEMBER = "/groups/add_member/";
	private static final String URL_REM_MEMBER = "/groups/rem_member/";
	private static final String URL_REM_GROUP = "/groups/rem_group/";

	private static JSONObject getJSONGroup(Group gr) {
		JSONObject jsonArgs = new JSONObject();
    	jsonArgs.put("name", new JSONString(gr.getName()));
    	jsonArgs.put("is_public", JSONBoolean.getInstance(gr.isPublic()));
    	jsonArgs.put("requests_allowed", JSONBoolean.getInstance(gr.isRequestsAllowed()));
		return jsonArgs;
	}
	
	public static void getGroups(ResponseHandler rh) {
		ServerComm.sendPostRequest(URL_GET_GROUPS, null, rh);
	}

	public static void createGroup(Group group, ResponseHandler rh) {
		final JSONObject jsonArgs = getJSONGroup(group);
		ServerComm.sendPostRequest(URL_CREATE_GROUP, jsonArgs.toString(), rh);
	}

	public static void addMember(String grName, String mName, 
			Boolean isAdmin, ResponseHandler rh) {
		JSONObject jsonArgs = new JSONObject();
		jsonArgs.put("group_name", new JSONString(grName));
		jsonArgs.put("user_name", new JSONString(mName));
		jsonArgs.put("is_admin", JSONBoolean.getInstance(isAdmin));
		ServerComm.sendPostRequest(URL_ADD_MEMBER, jsonArgs.toString(), rh);
	}

	public static void removeMember(String group, String member,
			ResponseHandler rh) {
		JSONObject jsonArgs = new JSONObject();
		jsonArgs.put("group_name", new JSONString(group));
		jsonArgs.put("user_name", new JSONString(member));
		ServerComm.sendPostRequest(URL_REM_MEMBER, jsonArgs.toString(), rh);
	}

	public static void removeGroup(String group, ResponseHandler rh) {
		JSONObject jsonArgs = new JSONObject();
		jsonArgs.put("name", new JSONString(group));
		ServerComm.sendPostRequest(URL_REM_GROUP, jsonArgs.toString(), rh);
	}
}
