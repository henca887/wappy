package wappy.client.calendar;

import java.util.Date;

/**
 * Implementation of an Appointment object.
 */
public class Appointment {
	private String subject;
	private String description;
	private Date date;
	private String startTime;
	private String endTime;
	private boolean property1 = false;
	private boolean property2 = false;
	private boolean property3 = false;

	public Appointment(String subject, String description, Date date,
			String startTime, String endTime, boolean property1, boolean property2,
			boolean property3) {
		this.subject = subject;
		this.description = description;
		this.date = date;
		this.startTime = startTime;
		this.endTime = endTime;
		this.property1 = property1;
		this.property2 = property2;
		this.property3 = property3;
	}
	
	public Appointment(String subject, String description, Date date,
			String startTime, String endTime) {
		this.subject = subject;
		this.description = description;
		this.date = date;
		this.startTime = startTime;
		this.endTime = endTime;
	}
	
	public Appointment(String subject, String description, Date date) {
		this.subject = subject;
		this.description = description;
		this.date = date;
		this.startTime = "00:00";
		this.endTime = "00:00";
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


	public void setDate(Date date) {
		this.date = date;
	}


	public Date getDate() {
		return date;
	}

}
