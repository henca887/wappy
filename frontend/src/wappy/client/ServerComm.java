package wappy.client;

import wappy.client.calendar.Appointment;

import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.pathf.gwt.util.json.client.JSONWrapper;

public class ServerComm {
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
                		JSONWrapper root = new JSONWrapper(
                                JSONParser.parse(response.getText()));
                		rh.on200Response(root);
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
}
