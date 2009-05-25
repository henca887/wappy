package wappy.client.calendar;

import java.util.ArrayList;
import java.util.List;

import wappy.client.ResponseHandler;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.SplitButton;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;

public class Calendar extends ContentPanel {
	//private ContentPanel rootPanel = new ContentPanel();
	private BookingForm bookingForm;
	private GridsView calView = new GridsView();
	
	private List<Appointment> appointments = new ArrayList<Appointment>();
	
	private Command onAppointmentCreated = new Command() {
		@Override
		public void execute() {
			Appointment app = bookingForm.getAppointment();
			appointments.add(app);
			calView.update(app);
		}
		
	};
	
	public Calendar() {
		setLayout(new FitLayout());
		
		getCurrentCalendar();
		bookingForm = new BookingForm(onAppointmentCreated);
		
		setHeaderVisible(false);
		
		Menu addMenu = createAddMenu();
		Menu remMenu = createRemMenu();
		
		SplitButton addBtn = new SplitButton("Add");
		addBtn.setIconStyle("wappy-icon-add");
		addBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				bookingForm.open();
			}
		});
		addBtn.setMenu(addMenu);
		
		SplitButton remBtn = new SplitButton("Remove");
		remBtn.setIconStyle("wappy-icon-remove");
		remBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				removeAppointment();
			}
		});
		remBtn.setMenu(remMenu);
		
//		ButtonBar btnBar = new ButtonBar();
		ToolBar toolBar = new ToolBar();
		toolBar.add(addBtn);
		toolBar.add(new SeparatorToolItem());
		toolBar.add(remBtn);
		toolBar.add(new SeparatorToolItem());
		
		setTopComponent(toolBar);
		add(calView);
		//add(rootPanel);
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
						List<Appointment> samples = SampleData.getAppointments();
						addSampleApps(samples);
					}
				});
		MenuItem addSamplesView = new MenuItem("Add some samples(Only view)",
				new SelectionListener<MenuEvent>() {
					@Override
					public void componentSelected(MenuEvent ce) {
						calView.update(SampleData.getAppointments());
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
		MenuItem emptyCalendar = new MenuItem("Empty the calendar",
				new SelectionListener<MenuEvent>() {
					@Override
					public void componentSelected(MenuEvent ce) {
						emptyCalendar();
					}
				});
		removeApp.setTitle("Removes selected appointment");
		emptyCalendar.setTitle("Removes all appointments from current " +
				"calendar");
		menu.add(removeApp);
		menu.add(emptyCalendar);
		return menu;
	}

//	private void errorAlert() {
//		MessageBox.alert("Calendar", "Request error!", null);
//	}
//	
//	private void failureAlert() {
//		MessageBox.alert("Calendar", "HTTP error!", null);
//	}
//	
//	private void exceptionAlert() {
//		MessageBox.alert("Calendar", "Request exception raised!", null);
//	}
	
	
	private List<Appointment> samples = new ArrayList<Appointment>();
	private void addSampleApps(final List<Appointment> samples) {
		if (samples.isEmpty()) {
			calView.update(this.samples);
			this.samples.clear();
			Info.display("Calendar",
					"Sample appointments was added to the calendar!");
			return;
		}
		final Appointment sample = samples.get(0);
		ResponseHandler rh = new ResponseHandler() {
			@Override
			public void onSuccess(JSONValue value) {
				CalendarJSON jsonUtil = new CalendarJSON(value);            
                if (jsonUtil.noErrors()) {
                    sample.setWeekNr(jsonUtil.getWeekNr());
                    Calendar.this.samples.add(sample);
                    samples.remove(0);
                	addSampleApps(samples);
                }
                else {
                	MessageBox.alert("Calendar", jsonUtil.getErrorVal(), null);
                }
			}
		};
		CalendarComm.addAppointment(sample, rh);
	}
	
	private void getCurrentCalendar() {
		ResponseHandler rh = new ResponseHandler() {
			@Override
			public void onSuccess(JSONValue value) {
				CalendarJSON jsonUtil = new CalendarJSON(value);            
                if (jsonUtil.noErrors()) {
        			appointments = jsonUtil.getAllAppointments();
                    calView.update(appointments);
                }
        		else {
        			// User has no appointments booked
        		}
			}
		};
		CalendarComm.getCurrentCalendar(rh);
	}

	private void removeAppointment() {
		Appointment app = calView.getSelected();
		if(app != null) {
			removeAppointment(app);
		}
	}
	
	private void removeAppointment(final Appointment app) {
		ResponseHandler rh = new ResponseHandler() {
			@Override
			public void onSuccess(JSONValue value) {
				CalendarJSON jsonUtil = new CalendarJSON(value);            
                if (jsonUtil.noErrors()) {
                    Info.display("", "Selected Appointment was removed from " +
                    		"the calendar!");
                    appointments.remove(app);
                    DeferredCommand.addCommand(new Command() {
                    	@Override
                    	public void execute() {
                    		calView.removeAppointment(app);
                    	}
                    });
                }
                else {
                    MessageBox.alert("Calendar", jsonUtil.getErrorVal(), null);
                }
			}
		};
		CalendarComm.removeAppointment(app, rh);
	}
	
	// TODO: Add confirmation dialog
	private void emptyCalendar() {
		ResponseHandler rh = new ResponseHandler() {
			@Override
			public void onSuccess(JSONValue value) {
				CalendarJSON jsonUtil = new CalendarJSON(value);            
                if (jsonUtil.noErrors()) {
        			Info.display("Calendar",
        					"All appointments have been removed!");
                    calView.emptyCalendar();
                    appointments.clear();
                }
        		else {
        			MessageBox.alert("Calendar", jsonUtil.getErrorVal(), null);
        		}
			}
		};
		CalendarComm.emptyCalendar(rh);
	}
	
	public boolean isGridView() {
		return calView.isGridView();
	}

}
