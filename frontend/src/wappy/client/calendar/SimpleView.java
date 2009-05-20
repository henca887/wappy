package wappy.client.calendar;

import java.util.List;

import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.google.gwt.user.client.ui.FlexTable;

public class SimpleView extends LayoutContainer implements CalendarView {
	private FlexTable table = new FlexTable();
	private ContentPanel panel = new ContentPanel();
	
	public SimpleView() {
		table.setCellSpacing(0);
	    table.setCellPadding(0);
	    table.setWidth("100%");
	    //table.addStyleName("wappy-calendar-simpleView");
		table.setText(0, 0, "Day");
		table.setText(0, 1, "Date");
		table.setText(0, 2, "Start");
		table.setText(0, 3, "End");
		table.setText(0, 4, "Appointment");
		table.setText(0, 5, "Description");
		
		panel.setHeading("Calendar Simple View");
		panel.setWidth(600);
		panel.add(table);
		add(panel);
	}
	
	@Override
	public void update(Appointment appointment) {
		int row = table.getRowCount();

		table.setText(row, 0, appointment.getWeekDay());
		table.setText(row, 1, appointment.getDateReadable());
		table.setText(row, 2, appointment.getStartTime());
		table.setText(row, 3, appointment.getEndTime());
		table.setText(row, 4, appointment.getSubject());
		table.setText(row, 5, appointment.getDescription());
	}
	
	@Override
	public void update(List<Appointment> appointments) {
		int row = table.getRowCount();
		for (int i = 0; i < appointments.size(); i++) {
			table.setText(row, 0, appointments.get(i).getWeekDay());
			table.setText(row, 1, appointments.get(i).getDateReadable());
			table.setText(row, 2, appointments.get(i).getStartTime());
			table.setText(row, 3, appointments.get(i).getEndTime());
			table.setText(row, 4, appointments.get(i).getSubject());
			table.setText(row, 5, appointments.get(i).getDescription());
			row++;
		}
	}
	
	@Override
	public void removeAppointment(Appointment app) {
		
	}

	@Override
	public void emptyCalendar() {
		int row = table.getRowCount();
		while (row > 1) {
			table.removeRow(row);
			row--;
		}
	}
	
	@Override
	public Appointment getSelected() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isGridView() {
		return false;
	}
}
