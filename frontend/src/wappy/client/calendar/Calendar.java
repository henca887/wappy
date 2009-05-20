package wappy.client.calendar;

import java.util.ArrayList;
import java.util.List;

import wappy.client.ResponseHandler;
import wappy.client.ServerComm;
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
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.pathf.gwt.util.json.client.JSONWrapper;

public class Calendar extends LayoutContainer {
	private Calculator calc = new Calculator();
	private Window calcWin = new Window();

	private ContentPanel rootPanel = new ContentPanel();
	private BookingForm bookingForm;
	private GridsView calView = new GridsView();
	
	private List<Appointment> appointments = new ArrayList<Appointment>();
	
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
		
		getCurrentCalendar();
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
			public void on200Response(JSONWrapper root) {
				JSONWrapper error = root.get("error");
                if (error.isNull()) {
                    JSONWrapper weekNr = root.get("week_nr");
                    sample.setWeekNr(weekNr.longValue());
                    Calendar.this.samples.add(sample);
                    samples.remove(0);
                	addSampleApps(samples);
                }
                else {
                	MessageBox.alert("Error", error.toString(), null);
                }
			}
		};
		ServerComm.addAppointment("Calendar", sample, rh);
	}
	
	private void getCurrentCalendar() {
		ResponseHandler rh = new ResponseHandler() {
			@Override
			public void on200Response(JSONWrapper root) {
				JSONWrapper result = root.get("result");
                JSONWrapper error = root.get("error");
                
        		if (error.isNull()) {
        			Info.display("Calendar",
                    		"Calendar contents have been retrieved!");
                    for (int i = 0; i < result.size(); i++) {
                    	appointments.add(new Appointment(
                    			result.get(i).get("subject").stringValue(),
                    			result.get(i).get("description").stringValue(),
                    			result.get(i).get("location").stringValue(),
                    			result.get(i).get("start_timestamp").longValue(),
                    			result.get(i).get("end_timestamp").longValue(),
                    			result.get(i).get("week_nr").longValue()));
                    }
                    calView.update(appointments);
                }
        		else {
        			// User has no appointments booked
        		}
			}
		};
		ServerComm.getCurrentCalendar("Calendar", rh);
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
			public void on200Response(JSONWrapper root) {
				JSONWrapper error = root.get("error");
                if (error.isNull()) {
                    Info.display("", "Selected Appointment was removed from the calendar!");
                    appointments.remove(app);
                    DeferredCommand.addCommand(new Command() {
                    	@Override
                    	public void execute() {
                    		calView.removeAppointment(app);
                    	}
                    });
                }
                else {
                    MessageBox.alert("Calendar", error.toString(), null);
                }
			}
		};
		ServerComm.removeAppointment("BookingForm", app, rh);
	}
	
	// TODO: Add confirmation dialog
	private void emptyCalendar() {
		ResponseHandler rh = new ResponseHandler() {
			@Override
			public void on200Response(JSONWrapper root) {
				JSONWrapper error = root.get("error");
                if (error.isNull()) {
        			Info.display("Calendar",
        					"All appointments have been removed!");
                    calView.emptyCalendar();
                    appointments.clear();
                }
        		else {
        			Info.display("Calendar", error.toString());
        		}
			}
		};
		ServerComm.emptyCalendar("Calendar", rh);
	}
	
	public boolean isGridView() {
		return calView.isGridView();
	}

}
