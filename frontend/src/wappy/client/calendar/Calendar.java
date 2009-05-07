package wappy.client.calendar;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class Calendar extends Composite implements ClickHandler {
	private VerticalPanel rootPanel = new VerticalPanel();
	private HorizontalPanel calHeader = new HorizontalPanel();
	private HorizontalPanel headerInfoPanel = new HorizontalPanel();
	private HorizontalPanel headerCtrlPanel = new HorizontalPanel();
	
	private FlexTable mainContent = new FlexTable();
	
	private Button confirmButton = new Button("Confirm");
	private Button addButton = new Button("Add");
	private Button nextWeekButton = new Button("Next week");
	private Button previousWeekButton = new Button("Previous week");
	
	private BookingForm bookingForm;
	private CalendarView calView = new CalendarView();
	private ArrayList<Appointment> appointments = new ArrayList<Appointment>();
	
	public Calendar() {
		addButton.addClickHandler(this);
		nextWeekButton.addClickHandler(this);
		previousWeekButton.addClickHandler(this);
		confirmButton.addClickHandler(this);
		
		headerInfoPanel.add(new HTML("This is the Calendar header"));
		
		headerCtrlPanel.add(addButton);
//		headerCtrlPanel.add(previousWeekButton);
//		headerCtrlPanel.add(nextWeekButton);
		
		calHeader.setWidth("100%");
		calHeader.addStyleName("wappy-calendar-header");
		
		calHeader.add(headerInfoPanel);
		calHeader.add(headerCtrlPanel);
		calHeader.setCellHorizontalAlignment(headerCtrlPanel, HorizontalPanel.ALIGN_RIGHT);
		
		//calView.addStyleName("wappy-calendar-debugColor");
		rootPanel.add(calHeader);
		rootPanel.add(calView);
		initWidget(rootPanel);
	}


	public void onClick(ClickEvent event) {
		Object sender = event.getSource();
		if (sender == addButton) {
			openBookingForm();
		}
		else if (sender == confirmButton) {
			validateBooking();
		}
	}
	
	private void validateBooking() {
		bookingForm.validateInputAndConfirm();
		if (bookingForm.newAppointmentCreated()) {
			addAppointmentToCalendar(bookingForm.getCreatedAppointment());
		}		
	}

//>>>>>>>>>>>>>>>>>> Serever Comuication >>>>>>>>>>>>>>>>>>>>>>>>>>>
	
	private void addAppointmentToCalendar(Appointment appointment) {
		// TODO Maybe do a duplicate test first?
		
		// TODO Add the new Appointment to database
		addAppointmentToDB(appointment);
		// Update Calendar view
		calView.update(appointment);
	}

	private void addAppointmentToDB(Appointment appointment) {
		// TODO adds only to an ArrayList: appointments
		appointments.add(appointment); // fakeDB
		
		// Packing data into JSON format to send to server
		final JSONObject jsonArgs = new JSONObject();
    	jsonArgs.put("subject", new JSONString(appointment.getSubject()));
    	jsonArgs.put("description", new JSONString(appointment.getDescription()));
    	jsonArgs.put("year", new JSONString(appointment.getYear()));
    	jsonArgs.put("month", new JSONString(appointment.getMonth()));
    	jsonArgs.put("day", new JSONString(appointment.getDay()));
    	jsonArgs.put("week_day", new JSONString(appointment.getWeekDay()));
    	
    	jsonArgs.put("start_hour", new JSONString(appointment.getStartHour()));
    	jsonArgs.put("start_min", new JSONString(appointment.getStartMin()));
    	jsonArgs.put("end_hour", new JSONString(appointment.getEndHour()));
    	jsonArgs.put("end_min", new JSONString(appointment.getEndMin()));
//    	jsonArgs.put("property1", new JSONBoolean(appointment.isProperty1()));
//    	jsonArgs.put("property2", new JSONBoolean(appointment.isProperty2()));
//    	jsonArgs.put("property3", new JSONBoolean(appointment.isProperty3()));
    	
    	
    	//String testStr = "{\"subject\":\"test\",\"description\":\"test\",\"day\":\"testday\"}";
        
		RequestBuilder builder =
            new RequestBuilder(RequestBuilder.POST, 
                               "http://127.0.0.1:8000/wcalendar/add_appointment/");

        try {
        	Request request = builder.sendRequest(jsonArgs.toString(), new RequestCallback() {
                public void onError(Request request, Throwable exception) {
                    Window.alert("Request Error");
                }

                public void onResponseReceived(Request request, Response response) {
                    if (response.getStatusCode() == 200) {
                        JSONValue jsonValue = JSONParser.parse(response.getText());
                        JSONObject jsonObject = jsonValue.isObject();

                        Window.alert("DEBUG: " + jsonObject.get("result").isString().stringValue());
                    }
                    else {
                        Window.alert("Http Error =(" + "\n"
                        		+ jsonArgs.toString());
                    }
                }       
            });
        }
        catch (RequestException e) {
            Window.alert("Http Error =(");
        }
	}

	private void getFromDatabaseBySubject(String subject) {
		
	}
//<<<<<<<<<<<<<<<<<< Serever Comuication <<<<<<<<<<<<<<<<<<<<<<<<<<

	private void openBookingForm() {
		bookingForm = new BookingForm(confirmButton);
		bookingForm.showBookingForm();
	}

}
