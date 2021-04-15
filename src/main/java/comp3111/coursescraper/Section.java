package comp3111.coursescraper;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
/**
* Section is a basic unit of enrollment
*/
@SuppressWarnings("unused")
public class Section{
	private static final int MAX_NO_OF_SLOTS = 4;
	private int numSlots;
	private List<String> instructor;
	private Slot [] slots;
	private String sID;
	private String type;
	private boolean enrollment;
	private String codeSec;
	private String nameSec ;
	
	@FXML
	public CheckBox enrolling;
	/**
	 *Default constructor
	 */
	public Section()	{
		slots = new Slot[MAX_NO_OF_SLOTS];
		numSlots = 0;
		for (int i = 0; i < MAX_NO_OF_SLOTS; i++) slots[i] = null;
		enrollment = false;
		sID = null;
		type = null;
		instructor = null;
		enrolling = new CheckBox();
	}
	
	/**
	 * set enrollment of section
	 */	
	public void setEnrolling() {
		enrolling.setSelected(true);
		enrollment = true;
	}
	/**
	 * @return get enrollment of section
	 */
	public CheckBox getEnrolling() {
		return enrolling;
	}
	
	/**
	 * @param s  sets the type of section(L1; T1; LA2)
	 */
	public void setType(String s)	{
		this.type = s;
	}
	/**
	 *@param code sets the code of the section
	 */
	public void setCodeSec(String code) {
		this.codeSec = code;
	}
	/**
	 *@return the code of the section
	 */
	public String getCodeSec() {
		return codeSec;
	}
	/**
	 *@param name the name of the section
	 */
	public void setNameSec(String name) {
		this.nameSec = name;
	}
	/**
	 *@return the name of the section
	 */
	public String getNameSec() {
		return nameSec;
	}
	/**
	 *@return the type of the section
	 */
	public String getType()	{
		return type;
	}
	/**
	 *@param s the slot to be added
	 */
	public void addSlot(Slot s) {
		if (numSlots >= MAX_NO_OF_SLOTS)
			return;
		slots[numSlots++] = s;
	}
	/**
	 *@return the indicated slot
	 */
	public Slot getSlot(int i)	{
		if (i>=0 && i<numSlots)
			return slots[i];
		return null;
	}
	/**
	 *@param enroll set the enrollment status
	 */
	public void setEnrollment(boolean enroll)	{
		this.enrollment = enroll;
	}
	/**
	 *@return the enrollment status
	 */
	public boolean getEnrollment()	{
		return enrollment;
	}
	/**
	 *@return the number of slots
	 */
	public int getnumSlots()	{
		return numSlots;
	}
	/**
	 *@param slots set the number of slots
	 */
	public void setnumSlots(int slots)	{
		this.numSlots = slots;
	}
	/**
	 *@return the section ID
	 */
	public String getsID()	{
		return sID;
	}
	/**
	 *@param sets the section ID
	 */
	public void setsID(String ID)	{
		this.sID = ID;
	}
	/**
	 *@return the string of instructors
	 */
	public List<String> getInstructor()	{
		return instructor;
	}
	/**
	 *@param s set instructors through slot
	 */
	public void throughslot(Slot s) {
		this.instructor = s.getInstructor();
	}
	/**
	 *@param s the list of instructors to be set
	 */
	public void setinstructor(List<String> s)	{
		this.instructor = s;
	}
	/**
	 * increases number of slots by 1
	 */
	public void increaseslotby1()	{
		this.numSlots++;
	}
}
