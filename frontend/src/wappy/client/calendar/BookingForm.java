// TODO: Check isDateValid()

package wappy.client.calendar;

import java.util.Date;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.datepicker.client.DateBox;

public class BookingForm extends DialogBox{
	
	private VerticalPanel popupContent = new VerticalPanel();
	private HorizontalPanel timePanel = new HorizontalPanel();
	private HorizontalPanel headerPanel = new HorizontalPanel();
	private HorizontalPanel buttonsPanel = new HorizontalPanel();
	private HorizontalPanel subjectPanel = new HorizontalPanel();
	private HorizontalPanel datePanel = new HorizontalPanel();
	private VerticalPanel propertyPanel = new VerticalPanel();
	
	private	CheckBox property1 = new CheckBox("Property 1");
	private	CheckBox property2 = new CheckBox("Property 2");
	private	CheckBox property3 = new CheckBox("Property 3");
	
	private TimeListBox startTimeInput = new TimeListBox();
	private TimeListBox endTimeInput = new TimeListBox();

	private DateBox dateBox = new DateBox();
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
		
		timePanel.add(new HTML("Start"));
		timePanel.add(startTimeInput);
		timePanel.add(new HTML("Finish"));
		timePanel.add(endTimeInput);
		
		subjectPanel.add(new HTML("Subject"));
		subjectPanel.add(subjectInput);
		property1.setValue(true);
		
		propertyPanel.add(property1);
		propertyPanel.add(property2);
		propertyPanel.add(property3);
		
		datePanel.add(new HTML("Date"));
		datePanel.add(dateBox);
		dateBox.setFormat(new DateBox.DefaultFormat(
				DateTimeFormat.getFullDateFormat()));
		//dateBox.getDatePicker();
		
//		headerPanel.add(datePicker);
		headerPanel.add(propertyPanel);
		
		buttonsPanel.add(confirmButton);
		buttonsPanel.add(cancelButton);
		
		popupContent.add(timePanel);
		popupContent.add(datePanel);
		popupContent.add(headerPanel);
		popupContent.add(subjectPanel);
		popupContent.add(descriptionInput);
		popupContent.add(buttonsPanel);
		setWidget(popupContent);
		
		cancelButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				closeBookingForm();
			}
		});
		
		// Add styles
		//addStyleName("wappy-calendar-bookingForm");
		//dateBox.getDatePicker().addStyleName("wappy-calendar-bookingForm-picker");
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

		//		Date date = dateBox.getValue();
		String startTime = startTimeInput.getValue((startTimeInput.getSelectedIndex()));
		String endTime = endTimeInput.getValue((endTimeInput.getSelectedIndex()));

//		int startHour = Integer.parseInt(startTime.split(":")[0]);
//		int startMin = Integer.parseInt(startTime.split(":")[1]);
//		int endHour = Integer.parseInt(endTime.split(":")[0]);
//		int endMin = Integer.parseInt(endTime.split(":")[1]);
//		int year = WappyDateTime.getYearNr(date);
//		int month = WappyDateTime.getMonthNr(date);
//		int dayNr =  WappyDateTime.getDayNr(date);
//		
//		GregorianCalendar startCal = new GregorianCalendar(year, month, dayNr, startHour, startMin, 0);
//		GregorianCalendar endCal = 	new GregorianCalendar(year, month, dayNr, endHour, endMin, 0);
//		Date startDate = startCal.getTime();
//		Date endDate = endCal.getTime();
		
		appointment = new Appointment(subjectInput.getText(), descriptionInput.getText(),
				dateBox.getValue(), startTime, endTime);
		newAppointmentCreated = true;
	
//		Window.alert("subject: " + subjectInput.getText() + "\ndesc: " +
//				descriptionInput.getText() + "\nStartTime: " + startTime +
//				"\nEndTime: " + endTime);
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
