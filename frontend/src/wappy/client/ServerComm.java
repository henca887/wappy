package wappy.client;

import wappy.client.calendar.Appointment;
import wappy.client.groups.Group;

import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

public class ServerComm {
	private static final String URL_ADD_APP = "/wcalendar/add_app/";
	private static final String URL_GET_CAL = "/wcalendar/get_cal/";
	private static final String URL_REM_APP = "/wcalendar/rem_app/";
	private static final String URL_EMPTY_CAL = "/wcalendar/empty_cal/";
	
	private static final String URL_CREATE_GROUP = "/groups/create_group/";
	private static final String URL_GET_GROUPS = "/groups/get_groups/";
	private static final String URL_ADD_MEMBER = "/groups/add_member/";
	private static final String URL_REM_MEMBER = "/groups/rem_member/";
	private static final String URL_REM_GROUP = "/groups/rem_group/";
	
	
	private static void sendPostRequest(final String caller, String url,
			String msg, final ResponseHandler rh) {
		RequestBuilder builder =
            new RequestBuilder(RequestBuilder.POST, url);

        try {
        	builder.sendRequest(msg, new RequestCallback() {
                public void onError(Request request, Throwable exception) {
                    MessageBox.alert(caller, "Request Error", null);
                }

                public void onResponseReceived(Request request, Response response) {
                	if (response.getStatusCode() == 200) {
                		JSONValue jsonValue = 
                			JSONParser.parse(response.getText());
                		rh.on200Response(jsonValue);
                    }
                    else {
                    	MessageBox.alert(caller, "Http Error", null);
                    }
                }       
            });
        }
        catch (RequestException e) {
        	MessageBox.alert(caller, "Http Error, Exception", null);
        }
	}
	
	private static JSONObject getJSONAppointment(Appointment app) {
		JSONObject jsonArgs = new JSONObject();
    	jsonArgs.put("subject", new JSONString(app.getSubject()));
    	
    	String str = app.getDescription();
    	if (str == null) {
    		jsonArgs.put("description", new JSONString(""));
    	}
    	else {
    		jsonArgs.put("description", new JSONString(str));
    	}
    	jsonArgs.put("start_timestamp", new JSONNumber(app.getStartTimeStamp()));
    	jsonArgs.put("end_timestamp", new JSONNumber(app.getEndTimeStamp()));
    	
    	str = app.getLocation();
    	if (str == null) {
    		jsonArgs.put("location", new JSONString(""));
    	}
    	else {
    		jsonArgs.put("location", new JSONString(str));
    	}
		return jsonArgs;
	}
	
	public static void getCurrentCalendar(String caller, ResponseHandler rh) {
		sendPostRequest(caller, URL_GET_CAL, null, rh);
	}
	
	public static void addAppointment(String caller, Appointment appointment, 
			final ResponseHandler rh) {
		final JSONObject jsonArgs = getJSONAppointment(appointment);
    	sendPostRequest(caller, URL_ADD_APP, jsonArgs.toString(), rh);
	}
	
	public static void removeAppointment(String caller, Appointment app,
			ResponseHandler rh) {
		final JSONObject jsonArgs = getJSONAppointment(app);
    	sendPostRequest(caller, URL_REM_APP, jsonArgs.toString(), rh);
	}
	
	public static void emptyCalendar(String caller, ResponseHandler rh) {
		sendPostRequest(caller, URL_EMPTY_CAL, null, rh);
	}
	
	
	private static JSONObject getJSONGroup(Group gr) {
		JSONObject jsonArgs = new JSONObject();
    	jsonArgs.put("name", new JSONString(gr.getName()));
    	jsonArgs.put("is_public", JSONBoolean.getInstance(gr.isPublic()));
    	jsonArgs.put("requests_allowed", JSONBoolean.getInstance(gr.isRequestsAllowed()));
		return jsonArgs;
	}
	
	public static void getGroups(String caller, ResponseHandler rh) {
		sendPostRequest(caller, URL_GET_GROUPS, null, rh);
	}

	public static void createGroup(String caller,
			Group group, ResponseHandler rh) {
		final JSONObject jsonArgs = getJSONGroup(group);
		sendPostRequest(caller, URL_CREATE_GROUP, jsonArgs.toString(), rh);
	}

	public static void addMember(String caller, String group, String usr,
			ResponseHandler rh) {
		JSONObject jsonArgs = new JSONObject();
		jsonArgs.put("group_name", new JSONString(group));
		jsonArgs.put("user_name", new JSONString(usr));
    	sendPostRequest(caller, URL_ADD_MEMBER, jsonArgs.toString(), rh);
	}

	public static void removeMember(String caller, String group, String member,
			ResponseHandler rh) {
		JSONObject jsonArgs = new JSONObject();
		jsonArgs.put("group_name", new JSONString(group));
		jsonArgs.put("user_name", new JSONString(member));
    	sendPostRequest(caller, URL_REM_MEMBER, jsonArgs.toString(), rh);
	}

	public static void removeGroup(String caller, String group,
			ResponseHandler rh) {
		JSONObject jsonArgs = new JSONObject();
		jsonArgs.put("name", new JSONString(group));
		sendPostRequest(caller, URL_REM_GROUP, jsonArgs.toString(), rh);
	}
}
