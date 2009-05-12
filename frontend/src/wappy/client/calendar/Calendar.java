package wappy.client.calendar;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.pathf.gwt.util.json.client.JSONWrapper;

public class Calendar extends Composite {
	private VerticalPanel rootPanel = new VerticalPanel();
	private HorizontalPanel calHeader = new HorizontalPanel();
	private HorizontalPanel headerInfoPanel = new HorizontalPanel();
	private HorizontalPanel headerCtrlPanel = new HorizontalPanel();
	
	private BookingForm bookingForm;
	private CalendarView calView = new CalendarView();;
	private List<Appointment> appointments = new ArrayList<Appointment>();
	
	private Button addButton  = new Button("Add", new SelectionListener<ButtonEvent>() {
		public void componentSelected(ButtonEvent ce) {
			openBookingForm();
		}
	});
	
	private Button addSampleButton  = new Button("Add sample data", new SelectionListener<ButtonEvent>() {
		public void componentSelected(ButtonEvent ce) {
			addSampleApps(samples);
		}
	});
	
	private Command onAppointmentCreated = new Command() {
		@Override
		public void execute() {
			appointments.add(bookingForm.getAppointment());
			calView.update(bookingForm.getAppointment());
		}
		
	};
	
	public Calendar() {
		appointments = getCurrentCalendar();
		
		headerInfoPanel.add(new HTML("This is the Calendar header"));
		
		headerCtrlPanel.add(addButton);
		headerCtrlPanel.add(addSampleButton);
		
		calHeader.setWidth("100%");
		calHeader.addStyleName("wappy-calendar-header");
		
		calHeader.add(headerInfoPanel);
		calHeader.add(headerCtrlPanel);
		//calHeader.setCellHorizontalAlignment(headerCtrlPanel, HorizontalPanel.ALIGN_RIGHT);
		
		//calView.addStyleName("wappy-calendar-debugColor");
		rootPanel.add(calHeader);
		rootPanel.add(calView);
		initWidget(rootPanel);
	}

	private List<Appointment> samples = SampleData.getAppointments();
	
	private boolean addSampleApps(final List<Appointment> samples) {
		if (samples.isEmpty()) {
			calView.update(appointments);
			Info.display("", "Sample appointments was added to the calendar!");
			return true;
		}
		final Appointment sample = samples.get(0);
		final JSONObject jsonArgs = getJSONArgs(sample);
    	RequestBuilder builder =
            new RequestBuilder(RequestBuilder.POST, 
                               "http://127.0.0.1:8000/wcalendar/add_appointment/");

        try {
        	builder.sendRequest(jsonArgs.toString(), new RequestCallback() {
                public void onError(Request request, Throwable exception) {
                    MessageBox.alert("Alert:Calendar:addSampleApps", "Request Error", null);
                }

                public void onResponseReceived(Request request, Response response) {
                    if (response.getStatusCode() == 200) {
                    	JSONWrapper root = new JSONWrapper(
                                JSONParser.parse(response.getText()));
                        JSONWrapper error = root.get("error");
                        if (error.isNull()) { // properly checked?
                            JSONWrapper weekNr = root.get("weekNr");
                            sample.setWeekNr(weekNr.longValue());
                        	appointments.add(sample);
                        	samples.remove(0);
                        	addSampleApps(samples);
                        }
                        else {
                        	MessageBox.alert("Error",
	                        		error.getValue().toString(), null);
                        }
                    }
                    else {
                    	MessageBox.alert("Alert:Calendar:addSampleApps", "Http Error =(" + "\n"
                        		+ jsonArgs.toString(), null);
                    }
                }       
            });
        }
        catch (RequestException e) {
        	MessageBox.alert("Alert:Calendar:addSampleApps", "Http Error =(", null);
        	return false;
        }
		return true;
	}
	
	// Helper method to make JSONObjects
	private JSONObject getJSONArgs(Appointment app) {
		JSONObject jsonArgs = new JSONObject();
    	jsonArgs.put("subject", new JSONString(app.getSubject()));
    	
    	String descr = app.getDescription();
    	if (descr == null) {
    		jsonArgs.put("description", new JSONString(""));
    	}
    	else {
    		jsonArgs.put("description", new JSONString(descr));
    	}
    	jsonArgs.put("startTimeStamp", new JSONNumber(app.getStartTimeStamp()));
    	jsonArgs.put("endTimeStamp", new JSONNumber(app.getEndTimeStamp()));
		return jsonArgs;
	}
	
	private List<Appointment> getCurrentCalendar() {
		RequestBuilder builder =
            new RequestBuilder(RequestBuilder.POST, 
                               "http://127.0.0.1:8000/wcalendar/get_calendar/");

        try {
        	builder.sendRequest("", new RequestCallback() {
                public void onError(Request request, Throwable exception) {
                    MessageBox.alert("Alert:Calendar:getCurrentCalendar", "Request Error", null);
                }

                public void onResponseReceived(Request request, Response response) {
                	if (response.getStatusCode() == 200) {
                		JSONWrapper root = new JSONWrapper(
                                JSONParser.parse(response.getText()));
                        JSONWrapper result = root.get("result");
                        JSONWrapper error = root.get("error");
                        
                		if (error.isNull()) { // properly checked?
                			Info.display("DEBUG:getCurrentCalendar: Success",
                            		"Calendar contents have been retrieved!");
                            for (int i = 0; i < result.size(); i++) {
                            	appointments.add(new Appointment(
                            			result.get(i).get("subject").stringValue(),
                            			result.get(i).get("description").stringValue(),
                            			result.get(i).get("startTimeStamp").longValue(),
                            			result.get(i).get("endTimeStamp").longValue(),
                            			result.get(i).get("weekNr").longValue())); // No intValue???!
                            }
                            calView.update(appointments);
                        }
                		else {
                			// User has no calendars
                		}
                    }
                    else {
                    	MessageBox.alert("Alert:Calendar:getCurrentCalendar", "Http Error =(", null);
                    }
                }       
            });
        }
        catch (RequestException e) {
        	MessageBox.alert("Alert:Calendar:getCurrentCalendar", "Http Error =(, Exception", null);
        }
		return appointments;
	}

	
	private void openBookingForm() {
		bookingForm = new BookingForm(onAppointmentCreated );
	}

}
