package wappy.client.calendar;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.SplitButton;
import com.extjs.gxt.ui.client.widget.button.ToolButton;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.Command;
import com.pathf.gwt.util.json.client.JSONWrapper;

public class Calendar extends LayoutContainer {

	private static final String URL_GET_CAL = 
		"http://127.0.0.1:8000/wcalendar/get_calendar/";
	private ContentPanel rootPanel = new ContentPanel();
	private BookingForm bookingForm;
	private CalendarView calView = new CalendarView("grid");
	
	private List<Appointment> appointments = new ArrayList<Appointment>();
		
	private Command onAppointmentCreated = new Command() {
		@Override
		public void execute() {
			appointments.add(bookingForm.getAppointment());
			calView.update(bookingForm.getAppointment());
		}
		
	};
	
	private List<Appointment> samples = SampleData.getAppointments();
	public Calendar() {
		setLayout(new FlowLayout());
		appointments = getCurrentCalendar();
		bookingForm = new BookingForm(onAppointmentCreated);
		
		rootPanel.setHeaderVisible(false);
//		rootPanel.setLayout(new FitLayout());
		
		Menu menu = createMenu();
		
		SplitButton addBtn = new SplitButton("Add");
		addBtn.setIconStyle("wcalendar-icon-add");
		addBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				bookingForm.open();
			}
		});
		addBtn.setMenu(menu);
		
//		ButtonBar btnBar = new ButtonBar();
		ToolBar btnBar = new ToolBar();
		btnBar.add(addBtn);
		btnBar.add(new Button("Hej"));
		btnBar.add(new ToolButton("x-tool-save"));
			
		rootPanel.add(btnBar);
		rootPanel.add(calView);
		add(rootPanel);
	}

	
	private Menu createMenu() {
		Menu menu = new Menu();
		MenuItem addApp = new MenuItem("Add new appointment",
				new SelectionListener<MenuEvent>() {
					@Override
					public void componentSelected(MenuEvent ce) {
						bookingForm.open();
					}
			});
		MenuItem addSamples = new MenuItem("Add some samples",
				new SelectionListener<MenuEvent>() {
					@Override
					public void componentSelected(MenuEvent ce) {
						addSampleApps(samples);
					}
				});
		MenuItem addSamplesView = new MenuItem("Add some samples(Only view)",
				new SelectionListener<MenuEvent>() {
					@Override
					public void componentSelected(MenuEvent ce) {
						calView.update(samples);
						Info.display("", "Sample appointments was added to the calendar!");
					}
				});
		
		menu.add(addApp);
		menu.add(addSamples);
		menu.add(addSamplesView);
		return menu;
	}


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
    	
    	String str = app.getDescription();
    	if (str == null) {
    		jsonArgs.put("description", new JSONString(""));
    	}
    	else {
    		jsonArgs.put("description", new JSONString(str));
    	}
    	jsonArgs.put("startTimeStamp", new JSONNumber(app.getStartTimeStamp()));
    	jsonArgs.put("endTimeStamp", new JSONNumber(app.getEndTimeStamp()));
    	
    	str = app.getLocation();
    	if (str == null) {
    		jsonArgs.put("location", new JSONString(""));
    	}
    	else {
    		jsonArgs.put("location", new JSONString(str));
    	}
		return jsonArgs;
	}
	
	private List<Appointment> getCurrentCalendar() {
		RequestBuilder builder =
            new RequestBuilder(RequestBuilder.POST, URL.encode(URL_GET_CAL));

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
                            			result.get(i).get("location").stringValue(),
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

	public boolean isGridView() {
		return calView.isGridView();
	}

}
