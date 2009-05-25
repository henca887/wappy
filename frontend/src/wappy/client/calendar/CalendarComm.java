package wappy.client.calendar;

import wappy.client.ResponseHandler;
import wappy.client.ServerComm;

import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

public class CalendarComm {
	private static final String URL_ADD_APP = "/wcalendar/add_app/";
	private static final String URL_GET_CAL = "/wcalendar/get_cal/";
	private static final String URL_REM_APP = "/wcalendar/rem_app/";
	private static final String URL_EMPTY_CAL = "/wcalendar/empty_cal/";
	
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
	
	public static void getCurrentCalendar(ResponseHandler rh) {
		ServerComm.sendPostRequest(URL_GET_CAL, null, rh);
	}
	
	public static void addAppointment(Appointment appointment, 
			final ResponseHandler rh) {
		final JSONObject jsonArgs = getJSONAppointment(appointment);
		ServerComm.sendPostRequest(URL_ADD_APP, jsonArgs.toString(), rh);
	}
	
	public static void removeAppointment(Appointment app,
			ResponseHandler rh) {
		final JSONObject jsonArgs = getJSONAppointment(app);
		ServerComm.sendPostRequest(URL_REM_APP, jsonArgs.toString(), rh);
	}
	
	public static void emptyCalendar(ResponseHandler rh) {
		ServerComm.sendPostRequest(URL_EMPTY_CAL, null, rh);
	}
}
