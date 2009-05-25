package wappy.client.calendar;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.store.GroupingStore;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridGroupRenderer;
import com.extjs.gxt.ui.client.widget.grid.GroupColumnData;
import com.extjs.gxt.ui.client.widget.grid.GroupingView;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.i18n.client.DateTimeFormat;
// TODO: 
// Columns: Change month and day to short format
public class GridsView extends LayoutContainer implements CalendarView {
	private GroupingStore<Appointment> store = new GroupingStore<Appointment>();
	private List<ColumnConfig> config = new ArrayList<ColumnConfig>();
	private final ColumnModel cm;
	private Grid<Appointment> grid;
	
	public GridsView() {
		setLayout(new FitLayout());  
		//setSize(785, 540);
		
		store.groupBy("weekNr");
		store.setDefaultSort("date", SortDir.ASC);
		
	    ColumnConfig date = new ColumnConfig("date", "Date", 80);  
		ColumnConfig month = new ColumnConfig("month", "Month", 70);
		ColumnConfig weekNr = new ColumnConfig("weekNr", "Week", 50);
		ColumnConfig weekDay = new ColumnConfig("weekDay", "Day", 80);  
	    ColumnConfig startTime = new ColumnConfig("startTime", "Start", 50);  
	    ColumnConfig endTime = new ColumnConfig("endTime", "End", 50);  
	    ColumnConfig subject = new ColumnConfig("subject", "Appointment", 100);
	    ColumnConfig location = new ColumnConfig("location", "Location", 100);
	    ColumnConfig description = new ColumnConfig("description", "Description", 150);

	    date.setFixed(true);
	    date.setDateTimeFormat(DateTimeFormat.getFormat("yyyy-MM-dd"));  
	    month.setFixed(true);
	    month.setHidden(true);
	    weekNr.setFixed(true);
	    weekNr.setHidden(true);
	    weekDay.setFixed(true);
	    startTime.setFixed(true);
	    endTime.setFixed(true);
	    subject.setFixed(true);
	    location.setFixed(true);
	    
	    config.add(date);
	    config.add(month);
	    config.add(weekNr);
	    config.add(weekDay);  
	    config.add(startTime);  
	    config.add(endTime);  
	    config.add(subject);
	    config.add(location);
	    config.add(description);
	    
	    cm = new ColumnModel(config); 
	    
	    GroupingView view = new GroupingView();  
	    view.setForceFit(true);  
	    view.setGroupRenderer(new GridGroupRenderer() {  
	      public String render(GroupColumnData data) {  
	        String f = cm.getColumnById(data.field).getHeader();  
	        String l = data.models.size() == 1 ? "Item" : "Items";  
	        return f + ": " + data.group + " (" + data.models.size() + " " + l + ")";  
	      }  
	    });  
	    
	    grid = new Grid<Appointment>(store, cm);  
	    grid.setView(view);   
	    grid.setBorders(true);  
	    grid.setAutoExpandColumn("description");
	   
	    add(grid);
	}
	
	@Override
	public void update(Appointment appointment) {
		store.add(appointment);
	}
	
	@Override
	public void update(List<Appointment> appointments) {
		store.add(appointments);
	}
	
	@Override
	public Appointment getSelected() {
		return grid.getSelectionModel().getSelectedItem();
	}
	
	@Override
	public void removeAppointment(Appointment app) {
		store.remove(app);
	}
	
	@Override
	public void emptyCalendar() {
		store.removeAll();
	}
	
	@Override
	public boolean isGridView() {
		return true;
	}
}
