package wappy.client.calendar;

import java.util.Date;

import wappy.client.ResponseHandler;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.TimeField;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;

public class BookingForm {
	final Window w = new Window();
	Command onAppointmentCreated;
	private long startTimeStamp, endTimeStamp;
	private Appointment appointment;
	
	private final FormPanel formPanel = new FormPanel();
	private final DateField dateField = new DateField();
	private final TimeField startTimeField = new TimeField();
	private final TimeField endTimeField = new TimeField();
	private final TextField<String> subjectField = new TextField<String>();
	private final TextArea descriptionField = new TextArea();
	private final TextField<String> locationField = new TextField<String>();
	
	public BookingForm(final Command onAppointmentCreated) {
		this.onAppointmentCreated = onAppointmentCreated;
		
		w.setLayout(new FlowLayout());
		w.setPlain(false);
		w.setWidth(350);
		w.setHeading("Add new appointment");
		w.setResizable(false);
		w.setModal(true);
		w.setBlinkModal(true);
		w.setAutoHeight(true);
		
		formPanel.setHeaderVisible(false);
		
		dateField.setFieldLabel("Date");
		dateField.setAllowBlank(false);
		dateField.setMinValue(new Date()); // Don't allow to book in the past
		dateField.getPropertyEditor().setFormat(DateTimeFormat.getFormat("yyyy-MM-dd"));
		dateField.setValue(new Date());
		formPanel.add(dateField);
		
		startTimeField.setFieldLabel("Start");
		startTimeField.setToolTip("Enter a start time");
		startTimeField.setAllowBlank(false);
		startTimeField.setFormat(DateTimeFormat.getFormat("HH:mm"));
		startTimeField.setTypeAhead(true);
		formPanel.add(startTimeField);
		
		
		endTimeField.setFieldLabel("End");
		endTimeField.setToolTip("Enter an end time");
		endTimeField.setAllowBlank(false);
		endTimeField.setFormat(DateTimeFormat.getFormat("HH:mm"));
		endTimeField.setTypeAhead(true);
		formPanel.add(endTimeField);
		
		
		subjectField.setFieldLabel("Subject");
		subjectField.setToolTip("Enter a subject");
		subjectField.setAllowBlank(false);
		subjectField.setMaxLength(30);
		subjectField.setAutoValidate(true);
		subjectField.setValidationDelay(300);
		formPanel.add(subjectField);
		
		locationField.setFieldLabel("Location");
		locationField.setToolTip("Location is optional");
		locationField.setEmptyText("Optional");
		locationField.setAllowBlank(true);
		locationField.setMaxLength(30);
		locationField.setAutoValidate(true);
		locationField.setValidationDelay(300);
		formPanel.add(locationField);
	
		
		
		descriptionField.setFieldLabel("Description");
		descriptionField.setAllowBlank(true);
		descriptionField.setToolTip("Description is optional");
		descriptionField.setEmptyText("Optional");
		descriptionField.setPreventScrollbars(true);
		descriptionField.setAutoValidate(true);
		descriptionField.setValidationDelay(300);
		descriptionField.setMaxLength(160);
		formPanel.add(descriptionField);

		formPanel.setButtonAlign(HorizontalAlignment.CENTER);
		
		formPanel.addButton(new Button("Submit", new SelectionListener<ButtonEvent>(){
			public void componentSelected(ButtonEvent ce) {
				
				if (formPanel.isValid(false)) {
					Date startDateTime = startTimeField.getDateValue();
					Date endDateTime = endTimeField.getDateValue();
					if (endDateTime.after(startDateTime)) {
						Date date = dateField.getValue();
						startTimeStamp = WappyTime.getTimeStamp(date, startDateTime);
						endTimeStamp = WappyTime.getTimeStamp(date, endDateTime);
						String subject = subjectField.getValue();
						String descr = descriptionField.getValue();
						String location = locationField.getValue();
						// DEBUG:SOLVED: better if weekNr could be generated on client
						long weekNr = 0;
						appointment = new Appointment(subject, descr,
								location, startTimeStamp, endTimeStamp, weekNr);
						saveAppointment(appointment);
					}
					else {
						MessageBox.alert("Alert", "End time must be after start time!", null);
					}
				}
			}
		}));
		
		formPanel.addButton(new Button("Cancel", new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				close();
			}
		}));

		w.add(formPanel);

	}
	
	private void saveAppointment(Appointment app) {
		ResponseHandler rh = new ResponseHandler() {
			@Override
			public void onSuccess(JSONValue value) {
				CalendarJSON jsonUtil = new CalendarJSON(value);            
                if (jsonUtil.noErrors()) {
		            long weekNr = jsonUtil.getWeekNr();
		        	BookingForm.this.appointment.setWeekNr(weekNr);
		        	Info.display("", "New appointment was added to the calendar!");
		            close();
		            DeferredCommand.addCommand(onAppointmentCreated);
		        }
		        else {
		            MessageBox.alert("BookingForm", jsonUtil.getErrorVal(), null);
		        }
			}
		};
		CalendarComm.addAppointment(app, rh);
	}

	private void close() {
		startTimeField.clearSelections();
		endTimeField.clearSelections();
		subjectField.setValue(null);
		subjectField.clearInvalid();
		locationField.setValue(null);
		locationField.clearInvalid();
		descriptionField.setValue(null);
		descriptionField.clearInvalid();
		w.hide();
	}
	
	public void open() {
		w.show();
    }
	
	public Appointment getAppointment() {
		return this.appointment;
	}
}
