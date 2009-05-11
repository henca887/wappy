package wappy.client.calendar;

import java.util.List;


import com.extjs.gxt.ui.client.widget.Info;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;

public class CalendarView extends Composite {
	
	VerticalPanel mainContent = new VerticalPanel();
	
	private GridView view;
//	private SimpleView view;
	
	public CalendarView() {
		view = new GridView();
//		view = new SimpleView();
	    mainContent.add(view);
	    
	    initWidget(mainContent);
	}

	public void update(Appointment appointment) {
		view.update(appointment);

	}

	public void update(List<Appointment> appointments) {
		view.update(appointments);
	}

}
