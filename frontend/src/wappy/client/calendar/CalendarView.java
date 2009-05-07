package wappy.client.calendar;

import java.util.Calendar;
import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;

public class CalendarView extends Composite {
	private FlexTable mainContent = new FlexTable();
//	VerticalPanel panel = new VerticalPanel();

	public CalendarView() {
		mainContent.setCellSpacing(0);
	    mainContent.setCellPadding(0);
	    mainContent.setWidth("100%");
	    mainContent.addStyleName("wappy-calendar-mainView");
	    
//	    panel.add(mainContent);
//	    initWidget(panel);
	    
	    initWidget(mainContent);
	    initMainContent();
	}

	private void initMainContent() {
		mainContent.setText(0, 0, "Day");
		mainContent.setText(0, 1, "Date");
		mainContent.setText(0, 2, "Start");
		mainContent.setText(0, 3, "End");
		mainContent.setText(0, 4, "Appointment");
		mainContent.setText(0, 5, "Description");

		
		//panel.add(mainContent);
		
	}

	public void update(Appointment appointment) {
		int row = mainContent.getRowCount();
//		Date date = appointment.getStartDate();
//		mainContent.setText(row, 0, WappyDateTime.getDay(date));
		mainContent.setText(row, 0, appointment.getWeekDay());
		mainContent.setText(row, 1, appointment.getYear() + "-" + 
				appointment.getMonth() + "-" + appointment.getDay());
		mainContent.setText(row, 2, appointment.getStartHour() + ":" + appointment.getStartMin());
		mainContent.setText(row, 3, appointment.getEndHour() + ":" + appointment.getEndMin());
		mainContent.setText(row, 4, appointment.getSubject());
		mainContent.setText(row, 5, appointment.getDescription());
//		mainContent.setText(row +1, 1, DateTimeFormat.getFullDateTimeFormat().format(date));
//		mainContent.setText(row +2, 1, DateTimeFormat.getLongDateTimeFormat().format(date));
//		mainContent.setText(row +3, 1, DateTimeFormat.getMediumDateTimeFormat().format(date));
//		mainContent.setText(row +4, 1, DateTimeFormat.getShortDateTimeFormat().format(date));
//		mainContent.setText(row+5, 1, "##################Date#################");
//		mainContent.setText(row +6, 1, DateTimeFormat.getFullDateFormat().format(date));
//		mainContent.setText(row +7, 1, DateTimeFormat.getLongDateFormat().format(date));
//		mainContent.setText(row +8, 1, DateTimeFormat.getMediumDateFormat().format(date));
//		mainContent.setText(row +9, 1, DateTimeFormat.getShortDateFormat().format(date));
//		mainContent.setText(row+10, 1, "##################Time#################");
//		mainContent.setText(row +11, 1, DateTimeFormat.getFullTimeFormat().format(date));
//		mainContent.setText(row +12, 1, DateTimeFormat.getLongTimeFormat().format(date));
//		mainContent.setText(row +13, 1, DateTimeFormat.getMediumTimeFormat().format(date));
//		mainContent.setText(row +14, 1, DateTimeFormat.getShortTimeFormat().format(date));
	}

}
