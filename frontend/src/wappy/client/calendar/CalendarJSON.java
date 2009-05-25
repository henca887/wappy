package wappy.client.calendar;


import java.util.ArrayList;
import java.util.List;

import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.pathf.gwt.util.json.client.JSONWrapper;

public class CalendarJSON {
	private JSONWrapper wrapper;
	
	public CalendarJSON(JSONValue value) {
		this.wrapper = new JSONWrapper(
				JSONParser.parse(value.toString()));
	}

	public boolean noErrors() {
		return wrapper.get("error").isNull();
	}
	
	public String getErrorVal() {
		return wrapper.get("error").getValue().toString();
	}
	
	private JSONWrapper getResult() {
		return wrapper.get("result");
	}

	public Long getWeekNr() {
		return wrapper.get("week_nr").longValue();
	}

	public List<Appointment> getAllAppointments() {
		JSONWrapper result = getResult();
		List<Appointment> appointments = new ArrayList<Appointment>();
		for (int i = 0; i < result.size(); i++) {
        	appointments.add(new Appointment(
        			result.get(i).get("subject").stringValue(),
        			result.get(i).get("description").stringValue(),
        			result.get(i).get("location").stringValue(),
        			result.get(i).get("start_timestamp").longValue(),
        			result.get(i).get("end_timestamp").longValue(),
        			result.get(i).get("week_nr").longValue()));
        }
		return appointments;
	}
}
