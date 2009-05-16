package wappy.client.calendar;

import java.util.List;

import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

public interface CalendarView {
	
	public void update(Appointment appointment);

	public void update(List<Appointment> appointments);

	public boolean isGridView();

	public void removeAppointment();

	public Appointment getSelected();

}
