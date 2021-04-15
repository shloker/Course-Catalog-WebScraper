package comp3111.coursescraper;


import org.junit.Test;

import comp3111.coursescraper.Course;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;


public class ItemTest {

//	@Test
//	public void testSetTitle() {
//		Course i = new Course();
//		i.setTitle("ABCDE");
//		assertEquals(i.getTitle(), "ABCDE");
//	}
	
	@Test
	public void teststartSlotTime()	{
		Slot s = new Slot();
		s.setStart("02:00AM");
		assertEquals(s.getStartHour(), 2);
	}
	
	@Test
	public void testendSlotTime()	{
		Slot s = new Slot();
		s.setEnd("02:00AM");
		assertEquals(s.getEndHour(), 2);
	}
	
	@Test
	public void testVenue()	{
		Slot s = new Slot();
		s.setVenue("Room 316");
		assertEquals(s.getVenue(), "Room 316");
	}
	
	@Test
	public void testDescription()	{
		Course s = new Course();
		s.setDescription("Room 316");
		assertEquals(s.getDescription(), "Room 316");
	}
	
	@Test
	public void testName()	{
		Course s = new Course();
		s.setTitle("Room 3166666666666666");
		assertEquals(s.getTitle(), "Room 3166666666666666");
	}
	
	@Test
	public void testCC()	{
		Course s = new Course();
		s.setCommonCore(true);
		assertEquals(s.getCommonCore(), true);
	}

//	@Test
//	public void testAdd()	{
//		Course s =  new Course();
//		Section x =  new Section();
//		Slot y =  new Slot();
//		x.addSlot(y);
//		s.addSection(x);
//		assertEquals(s.getNumSections(), 1);
//		assertEquals(x.getnumSlots(), 1);
//	}
	
	

	
}
