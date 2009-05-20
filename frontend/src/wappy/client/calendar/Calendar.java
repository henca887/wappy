package wappy.client.calendar;

import java.util.ArrayList;
import java.util.List;

import wappy.client.calculator.Calculator;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.SplitButton;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
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
import com.google.gwt.user.client.DeferredCommand;
import com.pathf.gwt.util.json.client.JSONWrapper;

// TODO: move out request methods
public class Calendar extends LayoutContainer {
	private Calculator calc = new Calculator();
	private Window calcWin = new Window();
	
	private static final String URL_GET_CAL = "/wcalendar/get_cal/";
	private static final String URL_ADD_APP = "/wcalendar/add_app/";
	private static final String URL_REM_APP = "/wcalendar/rem_app/";
	
	private ContentPanel rootPanel = new ContentPanel();
	private BookingForm bookingForm;
	private GridsView calView = new GridsView();
	
	private List<Appointment> appointments = new ArrayList<Appointment>();
	private List<Appointment> samples = SampleData.getAppointments();
	
	private Command onAppointmentCreated = new Command() {
		@Override
		public void execute() {
			appointments.add(bookingForm.getAppointment());
			calView.update(bookingForm.getAppointment());
		}
		
	};
	
	public Calendar() {
		setLayout(new FlowLayout());
		
		calcWin.setLayout(new FitLayout());
		calcWin.add(calc);
		
		appointments = getCurrentCalendar();
		bookingForm = new BookingForm(onAppointmentCreated);
		
		rootPanel.setHeaderVisible(false);
//		rootPanel.setLayout(new FitLayout());
		
		Menu addMenu = createAddMenu();
		Menu remMenu = createRemMenu();
		
		SplitButton addBtn = new SplitButton("Add");
		addBtn.setIconStyle("wcalendar-icon-add");
		addBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				bookingForm.open();
			}
		});
		addBtn.setMenu(addMenu);
		
		SplitButton remBtn = new SplitButton("Remove");
		remBtn.setIconStyle("wcalendar-icon-remove");
		remBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				removeAppointment();
			}
		});
		remBtn.setMenu(remMenu);
		
//		ButtonBar btnBar = new ButtonBar();
		ToolBar btnBar = new ToolBar();
		btnBar.add(addBtn);
		btnBar.add(new SeparatorToolItem());
		btnBar.add(remBtn);
		btnBar.add(new SeparatorToolItem());
		btnBar.add(new Button("Calculator", new SelectionListener<ButtonEvent> (){
			@Override
			public void componentSelected(ButtonEvent ce) {
				calcWin.show();
				calcWin.toFront();
			}
		}));

		rootPanel.add(btnBar);
		rootPanel.add(calView);
		add(rootPanel);
	}

	private Menu createAddMenu() {
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
		addApp.setTitle("Add new appointent");
		addSamples.setTitle("Add some sample appointments");
		addSamplesView.setTitle("Adds some sample appointments to the view, not saved in calendar");
		menu.add(addApp);
		menu.add(addSamples);
		menu.add(addSamplesView);
		return menu;
	}

	private Menu createRemMenu() {
		Menu menu = new Menu();
		MenuItem removeApp = new MenuItem("Remove appointment",
				new SelectionListener<MenuEvent>() {
					@Override
					public void componentSelected(MenuEvent ce) {
						removeAppointment();
					}
			});
		MenuItem removeAllApps = new MenuItem("Empty the calendar",
				new SelectionListener<MenuEvent>() {
					@Override
					public void componentSelected(MenuEvent ce) {
						// TODO
					}
				});
		removeApp.setTitle("Removes selected appointment");
		removeAllApps.setTitle("Removes all appointments from current " +
				"calendar (!implemented)");
		menu.add(removeApp);
		menu.add(removeAllApps);
		return menu;
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
	
	private boolean addSampleApps(final List<Appointment> samples) {
		if (samples.isEmpty()) {
			calView.update(appointments);
			Info.display("", "Sample appointments was added to the calendar!");
			return true;
		}
		final Appointment sample = samples.get(0);
		final JSONObject jsonArgs = getJSONArgs(sample);
    	RequestBuilder builder =
            new RequestBuilder(RequestBuilder.POST, URL_ADD_APP);

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
                            JSONWrapper weekNr = root.get("week_nr");
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
	
	private List<Appointment> getCurrentCalendar() {
		RequestBuilder builder =
            new RequestBuilder(RequestBuilder.POST, URL_GET_CAL);

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
                            			result.get(i).get("start_timestamp").longValue(),
                            			result.get(i).get("end_timestamp").longValue(),
                            			result.get(i).get("week_nr").longValue())); // No intValue???!
                            }
                            calView.update(appointments);
                        }
                		else {
                			// User has no appointments booked
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

	private void removeAppointment() {
		Appointment app = calView.getSelected();
		if(app != null) {
			removeAppointment(calView.getSelected());
		}
	}
	
	private void removeAppointment(Appointment app) {
		final JSONObject jsonArgs = getJSONArgs(app);
		
		RequestBuilder builder =
            new RequestBuilder(RequestBuilder.POST, URL_REM_APP);

        try {
        	builder.sendRequest(jsonArgs.toString(), new RequestCallback() {
                public void onError(Request request, Throwable exception) {
                    MessageBox.alert("Alert", "Request Error", null);
                }

                public void onResponseReceived(Request request, Response response) {
                    if (response.getStatusCode() == 200) {
                    	JSONWrapper root = new JSONWrapper(
                                JSONParser.parse(response.getText()));
                        JSONWrapper error = root.get("error");
                        if (error.isNull()) { // properly checked?
                            Info.display("", "Selected Appointment was removed from the calendar!");
                            DeferredCommand.addCommand(new Command() {
                            	@Override
                            	public void execute() {
                            		calView.removeAppointment();
                            	}
                            });
                        }
                        else {
	                        MessageBox.alert("Alert:Calendar:removeAppointment", "DEBUG: " + 
	                        		error.toString(), null);
                        }
                    }
                    else {
                    	MessageBox.alert("Alert:Calendar:removeAppointment", "Http Error =(" + "\n"
                        		+ jsonArgs.toString(), null);
                    }
                }       
        	});
        }
        catch (RequestException e) {
        	MessageBox.alert("Alert:Calendar:removeAppointment", "Http Error =(", null);
        }
	}
	
	public boolean isGridView() {
		return calView.isGridView();
	}

}
