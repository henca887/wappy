package wappy.client.calendar;

import java.util.Calendar;
import java.util.Date;

import com.extjs.gxt.ui.client.util.DateWrapper;
import com.extjs.gxt.ui.client.widget.form.TimeField;
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
public class WappyTime {
	
	public boolean afterToday(Date date) {
		return date.after(new Date());
	}
//	String getters
	public static String getYear(Date date) {
		String dateStr = DateTimeFormat.getLongDateFormat().format(date);
		return dateStr.split(",")[1].trim();
	}
	
	public static String getMonth(Date date) {
		return DateTimeFormat.getFormat("MMMM").format(date);
//		String dateStr = DateTimeFormat.getShortDateFormat().format(date);
//		return dateStr.split("/")[0];
	}
	
	public static String getMonth(long timeStamp) {
		Date date = new Date();
		date.setTime(timeStamp);
		return getMonth(date);
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
//////////////////////////////////
	public static long getTimeStamp(Date date, Date startDateTime) {
		DateWrapper timeWrapper = new DateWrapper(startDateTime);
		int hours = timeWrapper.getHours();
		int mins = timeWrapper.getMinutes();
		
		DateWrapper dateWrapper = new DateWrapper(date);
		dateWrapper = dateWrapper.clearTime();
		dateWrapper = dateWrapper.addHours(hours);
		dateWrapper = dateWrapper.addMinutes(mins);
		
		return dateWrapper.getTime();
	}
	
	
	public static String getWeekDay(long startTimeStamp) {
		DateWrapper dateWrapper = new DateWrapper(startTimeStamp);
		Date date = dateWrapper.asDate();
		return getWeekDay(date);
	}
	
	public static String getDateReadable(long startTimeStamp) {
		DateWrapper dateWrapper = new DateWrapper(startTimeStamp);
		Date date = dateWrapper.asDate();
		return DateTimeFormat.getFormat("yyyy-MM-dd").format(date);
	}
	
	public static String getTime(long timeStamp) {
		DateWrapper dateWrapper = new DateWrapper(timeStamp);
		Date date = dateWrapper.asDate();
		return DateTimeFormat.getFormat("HH:mm").format(date);
	}
	
	
	public static int getWeekNr(long timeStamp) {
		// TODO: Extract week nr, implement!
		return 0;
	}
	public static Date getDateFromStamp(Long timeStamp) {
		Date date = new Date();
		date.setTime(timeStamp);
		return date;
	}
	
}
