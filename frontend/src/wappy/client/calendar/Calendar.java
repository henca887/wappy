package wappy.client.calendar;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.pathf.gwt.util.json.client.JSONWrapper;

public class Calendar extends Composite {
	private VerticalPanel rootPanel = new VerticalPanel();
	private HorizontalPanel calHeader = new HorizontalPanel();
	private HorizontalPanel headerInfoPanel = new HorizontalPanel();
	private HorizontalPanel headerCtrlPanel = new HorizontalPanel();
	
	private FlexTable mainContent = new FlexTable();
	
	private BookingForm bookingForm;
	private CalendarView calView = new CalendarView();;
	private List<Appointment> appointments = new ArrayList<Appointment>();
	
	private Button addButton  = new Button("Add", new SelectionListener<ButtonEvent>() {
		public void componentSelected(ButtonEvent ce) {
			openBookingForm();
		}
	});

//	private Button refreshButton = new Button("Refresh", new SelectionListener<ButtonEvent>() {
//		public void componentSelected(ButtonEvent ce) {
//			calView.update(appointments);
//		}
//	});
	
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
		
		calHeader.setWidth("100%");
		calHeader.addStyleName("wappy-calendar-header");
		
		calHeader.add(headerInfoPanel);
		calHeader.add(headerCtrlPanel);
//		calHeader.setCellHorizontalAlignment(headerCtrlPanel, HorizontalPanel.ALIGN_RIGHT);
		
		//calView.addStyleName("wappy-calendar-debugColor");
		rootPanel.add(calHeader);
		rootPanel.add(calView);
		initWidget(rootPanel);
	}


	private List<Appointment> getCurrentCalendar() {
		RequestBuilder builder =
            new RequestBuilder(RequestBuilder.POST, 
                               "http://127.0.0.1:8000/wcalendar/get_calendar/");

        try {
        	builder.sendRequest("", new RequestCallback() {
                public void onError(Request request, Throwable exception) {
                    MessageBox.alert("Alert", "Request Error", null);
                }

                public void onResponseReceived(Request request, Response response) {
                	if (response.getStatusCode() == 200) {
                        JSONValue jsonValue = JSONParser.parse(response.getText());
                        JSONObject jsonObject = jsonValue.isObject();
                        if (jsonObject.get("error").isString().isNull() == null ) { // tillräcklig check, lr m null?
                            Info.display("DEBUG: Success",
                            		"Calendar contents have been retrieved!");
                            JSONWrapper root = new JSONWrapper(
                                    JSONParser.parse(response.getText()));
                            JSONWrapper result = root.get("result");
                            for (int i = 0; i < result.size(); i++) {
                            	appointments.add(new Appointment(
                            			result.get(i).get("subject").stringValue(),
                            			result.get(i).get("description").stringValue(),
                            			result.get(i).get("startTimeStamp").longValue(),
                            			result.get(i).get("endTimeStamp").longValue()));
                            }
                            calView.update(appointments);
                        }
                        MessageBox.alert("Error",
                        		jsonObject.get("error").isString().toString(), null);
                    }
                    else {
                    	MessageBox.alert("Alert", "Http Error =(", null);
                    }
                }       
            });
        }
        catch (RequestException e) {
        	MessageBox.alert("Alert", "Http Error =(", null);
        }
		return appointments;
		// TODO Auto-generated method stub
		
	}


	public void onClick(ClickEvent event) {
		Object sender = event.getSource();
		if (sender == addButton) {
			openBookingForm();
		}
	}
	

	private void openBookingForm() {
		bookingForm = new BookingForm(onAppointmentCreated );
	}

}
