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
//	String getters
	public static String getYear(Date date) {
		String dateStr = DateTimeFormat.getLongDateFormat().format(date);
		return dateStr.split(",")[1].trim();
	}
	
	public static String getMonth(Date date) {
		String dateStr = DateTimeFormat.getShortDateFormat().format(date);
		return dateStr.split("/")[0];
	}
	
	public static String getDay(Date date) {
		String dateStr = DateTimeFormat.getShortDateFormat().format(date);
		return dateStr.split("/")[1];
	}
	
	public static String getMonthName(Date date) {
		String dateStr = DateTimeFormat.getLongDateFormat().format(date);
		return dateStr.split(",")[0].split(" ")[0];
	}
	
	public static String getWeekDay(Date date) {
		String dateStr = DateTimeFormat.getFullDateFormat().format(date);
		return dateStr.split(",")[0].trim();
	}
	
	public static String getHour(Date date) {
		String dateStr = DateTimeFormat.getMediumTimeFormat().format(date);
		return dateStr.split(":")[0];
	}
	
	public static String getMin(Date date) {
		String dateStr = DateTimeFormat.getMediumTimeFormat().format(date);
		return dateStr.split(":")[1];
	}
	
//	Integer getters
	public static int getHourNr(Date date) {
		String dateStr = DateTimeFormat.getMediumTimeFormat().format(date);
		return Integer.parseInt(dateStr.split(":")[0]);
	}
	
	public static int getMinNr(Date date) {
		String dateStr = DateTimeFormat.getMediumTimeFormat().format(date);
		return Integer.parseInt(dateStr.split(":")[1]);
	}
	
	public static int getSecNr(Date date) {
		String dateStr = DateTimeFormat.getMediumTimeFormat().format(date);
		return Integer.parseInt(dateStr.split(":")[2]);
	}
	
	public static int getYearNr(Date date) {
		String dateStr = DateTimeFormat.getMediumDateFormat().format(date);
		return Integer.parseInt(dateStr.split(",")[1].trim() );
	}
	
	public static int getMonthNr(Date date) {
		String dateStr = DateTimeFormat.getShortDateFormat().format(date);
		return Integer.parseInt(dateStr.split("/")[0]);
	}
	
	public static int getDayNr(Date date) {
		String dateStr = DateTimeFormat.getShortDateFormat().format(date);
		return Integer.parseInt(dateStr.split("/")[1]);
	}
	
}
