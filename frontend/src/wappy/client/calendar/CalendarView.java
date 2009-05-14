package wappy.client.calendar;

import java.util.List;

import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

public class CalendarView extends LayoutContainer {
	
	LayoutContainer container = new LayoutContainer();
	private LayoutContainer view;

	private String viewName;

	public CalendarView(String viewName) {
		this.viewName = viewName;
		if (viewName == "grid") {
			setLayout(new FitLayout());
			view = new GridsView();
		}
		else if (viewName == "simple") {
			view = new SimpleView();
		}
		
		container.add(view);
	    add(container);
	}
	
	public void update(Appointment appointment) {
		if (viewName == "grid") {
			((GridsView) view).update(appointment);
		}
		else if (viewName == "simple") {
			((SimpleView) view).update(appointment);
		}
	}

	public void update(List<Appointment> appointments) {
		if (viewName == "grid") {
			((GridsView) view).update(appointments);
		}
		else if (viewName == "simple") {
			((SimpleView) view).update(appointments);
		}
	}

	public boolean isGridView() {
		return viewName == "grid";
	}

}
