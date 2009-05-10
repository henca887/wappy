package wappy.client.calendar;

import java.util.Date;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
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
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;

public class BookingForm extends LayoutContainer {
	final Window w = new Window();
	Command onAppointmentCreated;
	private long startTimeStamp, endTimeStamp;
	private Appointment appointment;
	
	public BookingForm(Command onAppointmentCreated) {
		this.onAppointmentCreated = onAppointmentCreated;
		setLayout(new FlowLayout());
		
		
		w.setPlain(false);
		w.setWidth(350);
		w.setHeading("Add new appointment");
		w.setResizable(false);
		w.setModal(true);
		w.setAutoHeight(true);
		w.setBlinkModal(true);
		
		final FormPanel formPanel = new FormPanel();
		formPanel.setHeaderVisible(false);
		//formPanel.setWidth(500);	// DEBUG
		
		final DateField dateField = new DateField();
		dateField.setFieldLabel("Date");
		dateField.setAllowBlank(false);
		dateField.setMinValue(new Date()); // Don't allow to book in the past
		dateField.getPropertyEditor().setFormat(DateTimeFormat.getFormat("yyyy-MM-dd"));
		formPanel.add(dateField);
				
		final TimeField startTimeField = new TimeField();
		startTimeField.setFieldLabel("Start");
		startTimeField.setToolTip("Enter a start time");
		startTimeField.setAllowBlank(false);
		startTimeField.setFormat(DateTimeFormat.getFormat("HH:mm"));
		formPanel.add(startTimeField);
		
		
		final TimeField endTimeField = new TimeField();
		endTimeField.setFieldLabel("End");
		endTimeField.setToolTip("Enter an end time");
		endTimeField.setAllowBlank(false);
		endTimeField.setFormat(DateTimeFormat.getFormat("HH:mm"));
		formPanel.add(endTimeField);
		
		final TextField<String> subjectField = new TextField<String>();
		subjectField.setFieldLabel("Subject");
		subjectField.setToolTip("Enter a subject");
		subjectField.setEmptyText("Enter a subject");
		subjectField.setAllowBlank(false);
		formPanel.add(subjectField);
	
		final TextArea descriptionField = new TextArea();
		descriptionField.setAllowBlank(true);
		descriptionField.setFieldLabel("Description");
		descriptionField.setToolTip("Description is optional");
		descriptionField.setEmptyText("Optional: Enter a description, " +
				"or leave it as it is for empty.");
		
		descriptionField.setPreventScrollbars(true);
		descriptionField.setAutoValidate(true);
		descriptionField.setMaxLength(100);
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
						// fortsätt med resten av bokningen
						String subject = subjectField.getValue();
						String descr = descriptionField.getValue();
						appointment = new Appointment(subject, descr,
								startTimeStamp, endTimeStamp);
						saveAppointment(appointment);
					}
					else {
						//ajajaj
						MessageBox.alert("Alert", "End time must be after start time!", null);
						Info.display("Invalid time selection", "End time must be after start time!");
					}
					
				}
				
				
			}
			}));
		
		formPanel.addButton(new Button("Cancel", new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				w.hide();
				
			}
			
		}));

		w.add(formPanel);
		add(w);
		w.show();
		
		DeferredCommand.addCommand(new Command() {
            public void execute() {
            	w.toFront();
             }
        });
		
		dateField.setValue(new Date());
		
		
	}
	
	private void saveAppointment(final Appointment appointment) {
		final JSONObject jsonArgs = new JSONObject();
    	jsonArgs.put("subject", new JSONString(appointment.getSubject()));
    	
    	// TODO: Possible bug with empty descr
//    	jsonArgs.put("description", new JSONString(appointment.getDescription()));
    	String descr = appointment.getDescription();
    	if (descr == null) {
    		jsonArgs.put("description", new JSONString(""));
    	}
    	else {
    		jsonArgs.put("description", new JSONString("DEBUG"));
    	}
    	
    	// Note: JSONNumber is a double
    	jsonArgs.put("startTimeStamp", new JSONNumber(appointment.getStartTimeStamp()));
    	jsonArgs.put("endTimeStamp", new JSONNumber(appointment.getEndTimeStamp()));
    	
    	RequestBuilder builder =
            new RequestBuilder(RequestBuilder.POST, 
                               "http://127.0.0.1:8000/wcalendar/add_appointment/");

        try {
        	builder.sendRequest(jsonArgs.toString(), new RequestCallback() {
                public void onError(Request request, Throwable exception) {
                    MessageBox.alert("Alert", "Request Error", null);
                }

                public void onResponseReceived(Request request, Response response) {
                    if (response.getStatusCode() == 200) {
                        JSONValue jsonValue = JSONParser.parse(response.getText());
                        JSONObject jsonObject = jsonValue.isObject();
                        if (jsonObject.get("error").isString().isNull() == null ) { // tillräcklig check, lr m null?
                            // Success!
                            Info.display("", "New appointment was added to the calendar!");
                            w.hide();
                            DeferredCommand.addCommand(onAppointmentCreated);
                        }
                        MessageBox.alert("Alert", "DEBUG: " + 
                        		jsonObject.get("result").isString().stringValue(), null);
                    }
                    else {
                    	MessageBox.alert("Alert", "Http Error =(" + "\n"
                        		+ jsonArgs.toString(), null);
                    }
                }       
            });
        }
        catch (RequestException e) {
        	MessageBox.alert("Alert", "Http Error =(", null);
        }
		
	}

	public Appointment getAppointment() {
		return this.appointment;
	}
}
