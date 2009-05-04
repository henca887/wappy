package wappy.client.calendar;

/**
 * A TimeListBox is a ListBox constrained to only showing hours.
 * 
 * TODO: Also allow manual user input other than whole hours.
 */
import com.google.gwt.user.client.ui.ListBox;


public class TimeListBox extends ListBox {
	public TimeListBox() {
		addItem("00:00");
		addItem("01:00");
		addItem("02:00");
		addItem("03:00");
		addItem("04:00");
		addItem("05:00");
		addItem("06:00");
		addItem("07:00");
		addItem("08:00");
		addItem("09:00");
		addItem("10:00");
		
	}
}
