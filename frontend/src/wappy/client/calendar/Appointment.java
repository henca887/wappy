package wappy.client.calendar;

import java.util.Date;

import com.extjs.gxt.ui.client.data.BaseModel;



/**
 * Implementation of an Appointment object.
 */

public class Appointment extends BaseModel {
	// long week because of JSONWrapper, see in Calendar class	
	public Appointment(String subject, String description, String location,
			long startTimeStamp, long endTimeStamp, long weekNr) {
		set("subject", subject);
		set("description", description);
		set("startTimeStamp", startTimeStamp);
		set("endTimeStamp", endTimeStamp);
		set("location", location);
		// Rest of the properties is for view
		set("weekNr", weekNr);		// weekNr is calculated at server
		set("date", getDateFromStamp());
		set("month", getMonth());
		set("weekDay", getWeekDay());
		set("startTime", getStartTime());
		set("endTime", getEndTime());
	}
	
	private Date getDateFromStamp() {
		return WappyTime.getDateFromStamp((Long)get("startTimeStamp"));
	}


	public String getMonth() {
		return WappyTime.getMonthName((Long)get("startTimeStamp"));
	}


	public int getWeekNr() {
		return WappyTime.getWeekNr((Long)get("startTimeStamp"));
	}


	public String getSubject() {
		return get("subject");
	}

	public String getDescription() {
		return get("description");
	}

	public long getStartTimeStamp() {
		return (Long)get("startTimeStamp");
	}

	public long getEndTimeStamp() {
		return (Long)get("endTimeStamp");
	}


	public String getWeekDay() {
		return WappyTime.getWeekDay(getStartTimeStamp());
	}


	public String getDateReadable() {
		return WappyTime.getDateReadable(getStartTimeStamp());
	}


	public String getStartTime() {
		return WappyTime.getTime(getStartTimeStamp());
	}


	public String getEndTime() {
		return WappyTime.getTime(getEndTimeStamp());
	}

	public void setWeekNr(Long weekNr) {
		set("weekNr", weekNr);
	}

	public String getLocation() {
		return get("location");
	}

	
}
