package wappy.client.calendar;

import java.util.Date;


/**
 * Implementation of an Appointment object.
 */
public class Appointment {
	private String subject;
	private String description;
//	private Date date;
	private String year;
	private String month;
	private String day;
	private String weekDay;

//	private String startTime;
//	private String endTime;	
	private String startHour;
	private String startMin;
	private String endHour;
	private String endMin;

//	private boolean property1 = true;
//	private boolean property2 = false;
//	private boolean property3 = false;
	
	public Appointment(String subject, String description,
			Date date, String startTime, String endTime) {
		this.subject = subject;
		this.description = description;

//		this.date = date;
		this.setYear(WappyDateTime.getYear(date));
		this.setMonth(WappyDateTime.getMonth(date));
		this.setDay(WappyDateTime.getDay(date));
		this.setWeekDay(WappyDateTime.getWeekDay(date));
		
		this.setStartHour(startTime.split(":")[0]);
		this.setStartMin(startTime.split(":")[1]);
		this.setEndHour(endTime.split(":")[0]);
		this.setEndMin(endTime.split(":")[1]);
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}


	public String getSubject() {
		return subject;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public String getDescription() {
		return description;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getYear() {
		return year;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getMonth() {
		return month;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public String getDay() {
		return day;
	}

	public void setWeekDay(String weekDay) {
		this.weekDay = weekDay;
	}

	public String getWeekDay() {
		return weekDay;
	}

	public void setStartHour(String startHour) {
		this.startHour = startHour;
	}

	public String getStartHour() {
		return startHour;
	}

	public void setStartMin(String startMin) {
		this.startMin = startMin;
	}

	public String getStartMin() {
		return startMin;
	}

	public void setEndHour(String endHour) {
		this.endHour = endHour;
	}

	public String getEndHour() {
		return endHour;
	}

	public void setEndMin(String endMin) {
		this.endMin = endMin;
	}

	public String getEndMin() {
		return endMin;
	}

}
