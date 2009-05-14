package wappy.client.calendar;

import java.util.ArrayList;
import java.util.List;

public class SampleData {
	
	public static List<Appointment> getAppointments() {
		List<Appointment> apps = new ArrayList<Appointment>();
		apps.add(new Appointment("Sub1", 
				"descr1", "Home",
				timeStamp("2009-10-01 08:00"), timeStamp("2009-10-01 10:00"), 0));
		apps.add(new Appointment("Sub1", 
				"Lorem ipsum dolor sit amet, consectetur adipisicing elit," +
				"sed do eiusmod tempor incididunt ut", null,
				timeStamp("2009-10-01 13:00"), timeStamp("2009-10-01 17:00"), 0));
		apps.add(new Appointment("Sub1", 
				"consectetur adipisicing", "Mars",
				timeStamp("2009-11-01 09:00"), timeStamp("2009-11-01 11:00"), 0));
		apps.add(new Appointment("Sub1", 
				"sit amet, consectetur", 
				null, timeStamp("2009-10-13 07:45"), timeStamp("2009-10-13 09:30"), 0));
		apps.add(new Appointment("Sub1", 
				"descr2", 
				null, timeStamp("2009-10-13 14:00"), timeStamp("2009-10-13 16:00"), 0));
		apps.add(new Appointment("Sub1", 
				"Excepteur sint occaecat ", 
				null, timeStamp("2009-10-01 18:00"), timeStamp("2009-10-01 18:30"), 0));
		apps.add(new Appointment("Sub1", 
				"descr1", "Pluto",
				timeStamp("2009-10-05 11:00"), timeStamp("2009-10-05 12:00"), 0));
		apps.add(new Appointment("Sub1", 
				null, 
				null, timeStamp("2009-10-05 20:00"), timeStamp("2009-10-05 21:00"), 0));
		apps.add(new Appointment("Sub1", 
				"Duis aute irure dolor ", 
				null, timeStamp("2009-10-03 17:00"), timeStamp("2009-10-03 19:00"), 0));
		apps.add(new Appointment("Sub1", 
				"descr1", null,
				timeStamp("2009-10-19 08:00"), timeStamp("2009-10-19 10:00"), 0));
		apps.add(new Appointment("Sub1", 
				null, null,
				timeStamp("2009-10-19 10:00"), timeStamp("2009-10-19 12:00"), 0));
		apps.add(new Appointment("Sub1", 
				"descr1", 
				null, timeStamp("2009-10-19 19:00"), timeStamp("2009-10-19 20:00"), 0));
		apps.add(new Appointment("Soccer Game", "AIK vs DIF", "Stockholm",
				timeStamp("2009-10-19 18:00"), timeStamp("2009-10-19 20:00"), 0));
		apps.add(new Appointment("Sub1", 
				"descr1", 
				null, timeStamp("2009-10-28 06:00"), timeStamp("2009-10-28 08:00"), 0));
		apps.add(new Appointment("Sub1", 
				"descr1", 
				null, timeStamp("2009-11-09 09:00"), timeStamp("2009-11-09 10:00"), 0));
		apps.add(new Appointment("Sub1", 
				"descr1", 
				null, timeStamp("2009-11-09 13:00"), timeStamp("2009-11-09 15:00"), 0));
		apps.add(new Appointment("Sub1", 
				"descr1", 
				null, timeStamp("2009-11-04 10:00"), timeStamp("2009-11-04 11:00"), 0));
		apps.add(new Appointment("Meet Joe", null, "At Joe's place",
				timeStamp("2009-11-11 18:00"), timeStamp("2009-11-11 19:00"), 0));
		apps.add(new Appointment("Sub1", 
				null, 
				null, timeStamp("2009-11-21 18:00"), timeStamp("2009-11-21 20:00"), 0));
		apps.add(new Appointment("Sub1", 
				null, 
				null, timeStamp("2009-10-11 14:00"), timeStamp("2009-10-11 15:00"), 0));
		
		return apps;
	}
	
	public static long timeStamp(String dateTimeStr) {
		return WappyTime.getTimeStamp(dateTimeStr);
	}
}
