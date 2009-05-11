package wappy.client.calendar;

import java.util.Date;

import com.extjs.gxt.ui.client.data.BaseModel;



/**
 * Implementation of an Appointment object.
 */

public class Appointment extends BaseModel {
		
	public Appointment(String subject, String description,
			long startTimeStamp, long endTimeStamp) {
		set("subject", subject);
		set("description", description);
		set("startTimeStamp", startTimeStamp);
		set("endTimeStamp", endTimeStamp);
		// Rest of the properties is for view
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
		return WappyTime.getMonth((Long)get("startTimeStamp"));
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

	
}
