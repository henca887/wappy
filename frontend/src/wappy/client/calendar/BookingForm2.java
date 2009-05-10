package wappy.client.calendar;

import java.util.Date;

import com.extjs.gxt.ui.client.util.DateWrapper;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.Time;
import com.extjs.gxt.ui.client.widget.form.TimeField;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;

public class BookingForm2 extends LayoutContainer {
	private VerticalPanel vp;
	
	public BookingForm2() {
		setLayout(new FlowLayout());
		
		vp = new VerticalPanel();
		vp.setSpacing(10);
		
		final Window w = new Window();
		w.setPlain(false);
		w.setWidth(350);
		w.setHeading("Add new appointment");
		w.setResizable(false);
		w.setModal(true);
		w.setAutoHeight(true);
		
		
		FormPanel formPanel = new FormPanel();
		formPanel.setHeaderVisible(false);
//		formPanel.setFrame(true);
//		formPanel.setWidth(350);

		
		DateField date = new DateField();
		date.setFieldLabel("Date");
		formPanel.add(date);
		
		
		/*
		TimeField field = new TimeField();
		field.setFieldLabel("Some time");
		DateWrapper wrap = new DateWrapper();
    	wrap = wrap.clearTime();
    	wrap.addHours(4);

    	field.setMinValue(wrap.asDate());
    	field.setDateValue(new Date());

    	Time time = field.getValue();
    	Date d = time.getDate();

    	Time match = field.findModel(new Date());
    	field.setValue(match);
    	formPanel.add(field);
		*/
		
		
		TimeField startTime = new TimeField();
		startTime.setFieldLabel("Start");
		startTime.setWidth(10);
		formPanel.add(startTime);
		
		w.add(formPanel);
		add(w);
		w.show();
		
		DeferredCommand.addCommand(new Command() {
            public void execute() {
            	w.toFront();
             }
        });
		
	}
}
