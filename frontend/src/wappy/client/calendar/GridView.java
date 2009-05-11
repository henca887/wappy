package wappy.client.calendar;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.store.GroupingStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridGroupRenderer;
import com.extjs.gxt.ui.client.widget.grid.GroupColumnData;
import com.extjs.gxt.ui.client.widget.grid.GroupingView;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.google.gwt.i18n.client.DateTimeFormat;

public class GridView extends LayoutContainer {
	private GroupingStore<Appointment> store = new GroupingStore<Appointment>();
	private List<ColumnConfig> config = new ArrayList<ColumnConfig>();
	private final ColumnModel cm;
	private Grid<Appointment> grid;
	private ContentPanel panel = new ContentPanel();
	
	public GridView() {
		setLayout(new FlowLayout(10));  

//		store.groupBy("month");
//		store.groupBy("date"); 
		store.groupBy("weekNr");
		
	    ColumnConfig date = new ColumnConfig("date", "Date", 80);  
		ColumnConfig month = new ColumnConfig("month", "Month", 1);
		ColumnConfig weekNr = new ColumnConfig("weekNr", "Week", 1);
		ColumnConfig weekDay = new ColumnConfig("weekDay", "Day", 80);  
	    ColumnConfig startTime = new ColumnConfig("startTime", "Start", 80);  
	    ColumnConfig endTime = new ColumnConfig("endTime", "End", 80);  
	    ColumnConfig subject = new ColumnConfig("subject", "Appointment", 80);
	    ColumnConfig description = new ColumnConfig("description", "Description", 200);
	    date.setDateTimeFormat(DateTimeFormat.getFormat("yyyy-MM-dd"));  
	    month.setHidden(true); 
	    weekNr.setHidden(true);
	    
	    config.add(date);
	    config.add(month);
	    config.add(weekNr);
	    config.add(weekDay);  
	    config.add(startTime);  
	    config.add(endTime);  
	    config.add(subject);
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
	    
	    panel.setHeading("Calendar Grid View");  
	    panel.setIconStyle("icon-table");  
	    panel.setCollapsible(true);
	    panel.setFrame(true);  
	    panel.setSize(760, 520);  
	    panel.setLayout(new FitLayout());  
	    panel.add(grid);  
	  
	    add(panel);  
	}


	public void update(Appointment appointment) {
		store.add(appointment);
	}

	public void update(List<Appointment> appointments) {
		updateMethod1(appointments);

	}

	private void updateMethod1(List<Appointment> appointments) {
		store.add(appointments);   
	}
	
	
}
