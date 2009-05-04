// TODO: Check isDateValid()

package wappy.client.calendar;

import java.util.Date;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.datepicker.client.DatePicker;

public class BookingForm extends DialogBox{
	
	//private static PopupPanel popup = new PopupPanel();
	private VerticalPanel popupContent = new VerticalPanel();
	private HorizontalPanel timePanel = new HorizontalPanel();
	private HorizontalPanel headerPanel = new HorizontalPanel();
	private HorizontalPanel buttonsPanel = new HorizontalPanel();
	private VerticalPanel propertyPanel = new VerticalPanel();
	
	private	CheckBox property1 = new CheckBox("Property 1");
	private	CheckBox property2 = new CheckBox("Property 2");
	private	CheckBox property3 = new CheckBox("Property 3");
	
	private TimeListBox startTime = new TimeListBox();
	private TimeListBox endTime = new TimeListBox();
	private Date dateInput;		// = new Date();
	private DateBox dateBox = new DateBox();
//	private DatePicker datePicker = new DatePicker();
	private TextBox subjectInput = new TextBox();
	private TextArea descriptionInput = new TextArea();
	
	private Button cancelButton = new Button("Cancel");
	
	private boolean doneWithBooking = false;
	private boolean newAppointmentCreated = false;
	
	private Appointment appointment;
	private Date now;
	
	public BookingForm(Button confirmButton) {
		now = new Date();
		doneWithBooking = false;
		setText("Add new appointment to the calendar");
		
		timePanel.add(startTime);
		timePanel.add(endTime);
		
		property1.setValue(true);
		
		propertyPanel.add(property1);
		propertyPanel.add(property2);
		propertyPanel.add(property3);
		
		dateBox.setFormat(new DateBox.DefaultFormat(
				DateTimeFormat.getFullDateFormat()));
		//dateBox.getDatePicker();
		
//		headerPanel.add(datePicker);
		headerPanel.add(propertyPanel);
		
		buttonsPanel.add(confirmButton);
		buttonsPanel.add(cancelButton);
		
		popupContent.add(timePanel);
		popupContent.add(dateBox);
		popupContent.add(headerPanel);
		popupContent.add(subjectInput);
		popupContent.add(descriptionInput);
		popupContent.add(buttonsPanel);
		setWidget(popupContent);
		
		cancelButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				closeBookingForm();
			}
		});

//		datePicker.addValueChangeHandler(new ValueChangeHandler<Date>() {
//			public void onValueChange(ValueChangeEvent<Date> event) {
//				setDateInput(event.getValue());
//				
//			}
//		});
	}

	private boolean isSubjectValid() {
		return !subjectInput.getText().isEmpty();
	}
	
	private boolean isDateValid() {
		// TODO Validate date
		Date date = dateBox.getValue();
		if (date != null && date.after(now))
			return true;
		else if (date.equals(now)) {
			Window.alert("Equal date!");
			return false;
		}
		else
			return false;
	}
	
	private void createNewAppointment() {
		// Create Appointment from inputed data
		// TODO Look at DateTimeFormat
		appointment = new Appointment(subjectInput.getText(), descriptionInput.getText(),
				dateBox.getValue());
		newAppointmentCreated = true;
		
	}
	
	private void setDateInput(Date dateInput) {
		this.dateInput = dateInput;
	}
	
	protected void closeBookingForm() {
		doneWithBooking = true;
		hide();
	}
	
	protected void validateInputAndConfirm() {
		if (isSubjectValid() &&
				isDateValid()) {
			closeBookingForm();
			createNewAppointment();
			
		}
		else {
			// TODO Mark wrong input fields
			Window.alert("Required fields are incomplete!");
		}
	}

	public boolean containsValidData() {
		// TODO Auto-generated method stub
		return false;
	}

	public void showBookingForm() {
		newAppointmentCreated = false;
		setPopupPositionAndShow(new PopupPanel.PositionCallback() {
			public void setPosition(int offsetWidth, int offsetHeight) {
				int left = (Window.getClientWidth() - offsetWidth) / 3;
				int top = (Window.getClientHeight() - offsetHeight) / 3;
				setPopupPosition(left, top);
			}
			});
		
	}


	public boolean bookingDone() {
		return doneWithBooking;
	}

	public boolean newAppointmentCreated() {
		return newAppointmentCreated ;
	}
	
	public Appointment getCreatedAppointment() {
		return appointment;
	}



}
