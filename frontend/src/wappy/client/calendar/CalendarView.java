package wappy.client.calendar;

import java.util.List;

public interface CalendarView {
	
	public Appointment getSelected();
	
	public void update(Appointment appointment);

	public void update(List<Appointment> appointments);

	public boolean isGridView();

	public void removeAppointment(Appointment appointment);
	
	public void emptyCalendar();

}
