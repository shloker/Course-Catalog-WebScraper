package comp3111.coursescraper;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.time.LocalTime;
import java.util.Locale;
import java.time.format.DateTimeFormatter;
/**
 *  Slot is the basic unit of time
 */
public class Slot {
	private int day;
	private LocalTime start; // add
	private LocalTime end;
	private String venue;
	private List<String> instructor;
	public static final String DAYS[] = {"Mo", "Tu", "We", "Th", "Fr", "Sa"};
	public static final Map<String, Integer> DAYS_MAP = new HashMap<String, Integer>();
	static {
		for (int i = 0; i < DAYS.length; i++)
			DAYS_MAP.put(DAYS[i], i);
	}
	
	
	/**
	 *  Used to copy while being added to section
	 */

	@Override
	public Slot clone() {
		Slot s = new Slot();
		s.day = this.day;
		s.start = this.start;
		s.end = this.end;
		s.venue = this.venue;
		s.instructor = this.instructor;
		return s;
	}
	/**
	 * @return a String of the slot
	 */
	public String toString() {
		return DAYS[day] + " " + start.toString() + " - " + end.toString() + ":" + venue + " : " + instructor;
	}
	/**
	 * @return the start hour
	 */
	public int getStartHour() {
		return start.getHour();
	}
	/**
	 * @return the start minute
	 */
	public int getStartMinute() {
		return start.getMinute();
	}
	/**
	 * @return the end hour
	 */
	public int getEndHour() {
		return end.getHour();
	}
	/**
	 * @return the end minute
	 */
	public int getEndMinute() {
		return end.getMinute();
	}
	/**
	 * @return the start
	 */
	public LocalTime getStart() {
		return start;
	}
	/**
	 * @param start the start to set
	 */
	public void setStart(String start) {
		this.start = LocalTime.parse(start, DateTimeFormatter.ofPattern("hh:mma", Locale.US));
	}
	/**
	 * @return the end
	 */
	public LocalTime getEnd() {
		return end;
	}
	/**
	 * @param end the end to set
	 */
	public void setEnd(String end) {
		this.end = LocalTime.parse(end, DateTimeFormatter.ofPattern("hh:mma", Locale.US));
	}
	/**
	 * @return the venue
	 */
	public String getVenue() {
		return venue;
	}
	/**
	 * @param venue the venue to set
	 */

	public void setVenue(String venue) {
		this.venue = venue;
	}

	/**
	 * @return the day
	 */
	public int getDay() {
		return day;
	}
	/**
	 * @param day the day to set
	 */
	public void setDay(int day) {
		this.day = day;
	}
	/**
	 * @param the list of instructors
	 */
	public void setInstructor(List<String> s)	{
		this.instructor = s;
	}
	/**
	 * @return the list of instructors
	 */
	public List<String> getInstructor()	{
		return instructor;
	}

}
