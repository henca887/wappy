package wappy.client.calendar;

import java.util.Date;
import com.google.gwt.i18n.client.DateTimeFormat;

/*
 Output examples from DateTimeFormat: Full/Long/Medium/Short 
 for DateTime/Date/Time 
 Wednesday, June 3, 2009 12:00:00 AM Etc/GMT-2 
 June 3, 2009 12:00:00 AM UTC+2 
 Jun 3, 2009 12:00:00 AM 
 6/3/09 12:00 AM 
 ##################Date################# 
 Wednesday, June 3, 2009 
 June 3, 2009 
 Jun 3, 2009 
 6/3/09 
 ##################Time################# 
 12:00:00 AM Etc/GMT-2 
 12:00:00 AM UTC+2 
 12:00:00 AM 
 12:00 AM 
 */
public class WappyDateTime {
	public static String getDay(Date date) {
		String dateStr = DateTimeFormat.getFullDateFormat().format(date);
		return dateStr.split(",")[0].trim();
	}
	public static String getHour(Date date) {
		String dateStr = DateTimeFormat.getFullDateFormat().format(date);
		return dateStr.split(":")[0];
	}
}
