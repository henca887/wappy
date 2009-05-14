package wappy.client.calendar;

import java.util.Date;

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
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.pathf.gwt.util.json.client.JSONWrapper;

public class BookingForm {
	private static final String URL_ADD_APP = "/wcalendar/add_appointment/";
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
		formPanel.add(subjectField);
		
		locationField.setFieldLabel("Location");
		locationField.setToolTip("Location is optional");
		locationField.setEmptyText("Optional");
		locationField.setAllowBlank(true);
		locationField.setMaxLength(30);
		formPanel.add(locationField);
	
		
		descriptionField.setAllowBlank(true);
		descriptionField.setFieldLabel("Description");
		descriptionField.setToolTip("Description is optional");
		descriptionField.setEmptyText("Optional");
		
		descriptionField.setPreventScrollbars(true);
		descriptionField.setAutoValidate(true);
		descriptionField.setMaxLength(160);
		descriptionField.setValidationDelay(300);
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
	
	private void saveAppointment(final Appointment appointment) {
		final JSONObject jsonArgs = new JSONObject();
    	jsonArgs.put("subject", new JSONString(appointment.getSubject()));
    	
    	String str = appointment.getDescription();
    	if (str == null) {
    		jsonArgs.put("description", new JSONString("")); // JSONNull instead?
    	}
    	else {
    		jsonArgs.put("description", new JSONString(str));
    	}
    	
    	str = appointment.getLocation();
    	if (str == null) {
    		jsonArgs.put("location", new JSONString(""));
    	}
    	else {
    		jsonArgs.put("location", new JSONString(str));
    	}
    	
    	// Note: JSONNumber is a double
    	jsonArgs.put("startTimeStamp", new JSONNumber(appointment.getStartTimeStamp()));
    	jsonArgs.put("endTimeStamp", new JSONNumber(appointment.getEndTimeStamp()));
    	
    	RequestBuilder builder =
            new RequestBuilder(RequestBuilder.POST, URL.encode(URL_ADD_APP));

        try {
        	builder.sendRequest(jsonArgs.toString(), new RequestCallback() {
                public void onError(Request request, Throwable exception) {
                    MessageBox.alert("Alert:BookingForm", "Request Error", null);
                }

                public void onResponseReceived(Request request, Response response) {
                    if (response.getStatusCode() == 200) {
                    	JSONWrapper root = new JSONWrapper(
                                JSONParser.parse(response.getText()));
                        JSONWrapper error = root.get("error");
                        if (error.isNull()) { // properly checked?
                            JSONWrapper weekNr = root.get("weekNr");
                        	BookingForm.this.appointment.setWeekNr(weekNr.longValue());
                        	Info.display("", "New appointment was added to the calendar!");
                            close();
                            DeferredCommand.addCommand(onAppointmentCreated);
                        }
                        else {
	                        MessageBox.alert("Alert:BookingForm", "DEBUG: " + 
	                        		error.toString(), null);
                        }
                    }
                    else {
                    	MessageBox.alert("Alert:BookingForm", "Http Error =(" + "\n"
                        		+ jsonArgs.toString(), null);
                    }
                }       
            });
        }
        catch (RequestException e) {
        	MessageBox.alert("Alert:BookingForm", "Http Error =(", null);
        }
		
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
