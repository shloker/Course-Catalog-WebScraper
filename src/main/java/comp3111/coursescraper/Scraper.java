
package comp3111.coursescraper;


import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.DomText;
import java.util.Vector;


/**
 * WebScraper provide a sample code that scrape web content. After it is constructed, you can call the method scrape with a keyword, 
 * the client will go to the default url and parse the page by looking at the HTML DOM.  
 * <br>
 * In this particular sample code, it access to HKUST class schedule and quota page (COMP). 
 * <br>
 * https://w5.ab.ust.hk/wcq/cgi-bin/1830/subject/COMP
 *  <br>
 * where 1830 means the third spring term of the academic year 2018-19 and COMP is the course code begins with COMP.
 * <br>
 * Assume you are working on Chrome, paste the url into your browser and press F12 to load the source code of the HTML. You might be freak
 * out if you have never seen a HTML source code before. Keep calm and move on. Press Ctrl-Shift-C (or CMD-Shift-C if you got a mac) and move your
 * mouse cursor around, different part of the HTML code and the corresponding the HTML objects will be highlighted. Explore your HTML page from
 * body &rarr; div id="classes" &rarr; div class="course" &rarr;. You might see something like this:
 * <br>
 * <pre>
 * {@code
 * <div class="course">
 * <div class="courseanchor" style="position: relative; float: left; visibility: hidden; top: -164px;"><a name="COMP1001">&nbsp;</a></div>
 * <div class="courseinfo">
 * <div class="popup attrword"><span class="crseattrword">[3Y10]</span><div class="popupdetail">CC for 3Y 2010 &amp; 2011 cohorts</div></div><div class="popup attrword"><span class="crseattrword">[3Y12]</span><div class="popupdetail">CC for 3Y 2012 cohort</div></div><div class="popup attrword"><span class="crseattrword">[4Y]</span><div class="popupdetail">CC for 4Y 2012 and after</div></div><div class="popup attrword"><span class="crseattrword">[DELI]</span><div class="popupdetail">Mode of Delivery</div></div>	
 *    <div class="courseattr popup">
 * 	    <span style="font-size: 12px; color: #688; font-weight: bold;">COURSE INFO</span>
 * 	    <div class="popupdetail">
 * 	    <table width="400">
 *         <tbody>
 *             <tr><th>ATTRIBUTES</th><td>Common Core (S&amp;T) for 2010 &amp; 2011 3Y programs<br>Common Core (S&amp;T) for 2012 3Y programs<br>Common Core (S&amp;T) for 4Y programs<br>[BLD] Blended learning</td></tr><tr><th>EXCLUSION</th><td>ISOM 2010, any COMP courses of 2000-level or above</td></tr><tr><th>DESCRIPTION</th><td>This course is an introduction to computers and computing tools. It introduces the organization and basic working mechanism of a computer system, including the development of the trend of modern computer system. It covers the fundamentals of computer hardware design and software application development. The course emphasizes the application of the state-of-the-art software tools to solve problems and present solutions via a range of skills related to multimedia and internet computing tools such as internet, e-mail, WWW, webpage design, computer animation, spread sheet charts/figures, presentations with graphics and animations, etc. The course also covers business, accessibility, and relevant security issues in the use of computers and Internet.</td>
 *             </tr>	
 *          </tbody>
 *      </table>
 * 	    </div>
 *    </div>
 * </div>
 *  <h2>COMP 1001 - Exploring Multimedia and Internet Computing (3 units)</h2>
 *  <table class="sections" width="1012">
 *   <tbody>
 *    <tr>
 *        <th width="85">Section</th><th width="190" style="text-align: left">Date &amp; Time</th><th width="160" style="text-align: left">Room</th><th width="190" style="text-align: left">Instructor</th><th width="45">Quota</th><th width="45">Enrol</th><th width="45">Avail</th><th width="45">Wait</th><th width="81">Remarks</th>
 *    </tr>
 *    <tr class="newsect secteven">
 *        <td align="center">L1 (1765)</td>
 *        <td>We 02:00PM - 03:50PM</td><td>Rm 5620, Lift 31-32 (70)</td><td><a href="/wcq/cgi-bin/1830/instructor/LEUNG, Wai Ting">LEUNG, Wai Ting</a></td><td align="center">67</td><td align="center">0</td><td align="center">67</td><td align="center">0</td><td align="center">&nbsp;</td></tr><tr class="newsect sectodd">
 *        <td align="center">LA1 (1766)</td>
 *        <td>Tu 09:00AM - 10:50AM</td><td>Rm 4210, Lift 19 (67)</td><td><a href="/wcq/cgi-bin/1830/instructor/LEUNG, Wai Ting">LEUNG, Wai Ting</a></td><td align="center">67</td><td align="center">0</td><td align="center">67</td><td align="center">0</td><td align="center">&nbsp;</td>
 *    </tr>
 *   </tbody>
 *  </table>
 * </div>
 *}
 *</pre>
 * <br>
 * The code 
 * <pre>
 * {@code
 * List<?> items = (List<?>) page.getByXPath("//div[@class='course']");
 * }
 * </pre>
 * extracts all result-row and stores the corresponding HTML elements to a list called items. Later in the loop it extracts the anchor tag 
 * &lsaquo; a &rsaquo; to retrieve the display text (by .asText()) and the link (by .getHrefAttribute()).   
 * 
 *
 */
public class Scraper {
	private WebClient client;

	/**
	 * Default Constructor
	 */
	public Scraper() {
		client = new WebClient();
		client.getOptions().setCssEnabled(false);
		client.getOptions().setJavaScriptEnabled(false);
	}
	/**
	 * @return boolean value whether it was successful
	 * @param e HtmlElement to be decoded
	 * @param c Section to which the slot will be added
	 * @param secondRow whether it is a second row or not
	 */
	private boolean addSlot(HtmlElement e, Section c, boolean secondRow) {
		String times[] =  e.getChildNodes().get(secondRow ? 0 : 3).asText().split(" ");
		String venue = e.getChildNodes().get(secondRow ? 1 : 4).asText();
		String inst[] = e.getChildNodes().get(secondRow? 2 : 5).asText().split(System.getProperty("line.separator"));
		List<String> instructor = new ArrayList<String>();
		for (String s:inst)
		{
			instructor.add(s);
		}
		if (times[0].equals("TBA"))
			return false;
		for (int j = 0; j < times[0].length(); j+=2) {
			String code = times[0].substring(j , j + 2);
			if (Slot.DAYS_MAP.get(code) == null)
				break;
			String shour = times[1].substring(0, 2); String smeridien = times[1].substring(5, 7);
			String ehour = times[3].substring(0, 2); String emeridien = times[3].substring(5, 7);
			String eminute = times[3].substring(3, 5);
			int minutes = Integer.parseInt(eminute);
			int stime = Integer.parseInt(shour); int etime = Integer.parseInt(ehour);
			if (stime < 9 && smeridien == "AM")
				break;
			if ((etime > 10 && minutes > 0) && emeridien == "PM")
				break;			
			Slot s = new Slot();
			
			s.setDay(Slot.DAYS_MAP.get(code));
			s.setStart(times[1]);
			s.setEnd(times[3]);
			s.setVenue(venue);
			s.setInstructor(instructor); c.setinstructor(instructor);
			c.addSlot(s);	
		}
		return true;
	}
	/**
	 * @return a vector of SFQ objects
	 * @param the URL of the SFQ website
	 */
	public Vector<SFQ> scrapecourseSFQ(String url)
	{
		try	{
			HtmlPage page = client.getPage(url);
			Vector<SFQ> result = new Vector<SFQ>();
			Controller x = new Controller(); List<Section> booty = x.sectionslistn; Vector<String> myneeds = new Vector<String>(); char compare = ' '; String toadd = "";
			for (int i = 0; i< booty.size(); i++)
			{
				String sumbitch = booty.get(i).getCodeSec(); 
				if (sumbitch.charAt(9) == compare)
				{
					toadd = " " + sumbitch;
				}
				else
				{
					toadd = " " + sumbitch + " ";
				}
				myneeds.add(toadd);
			}
			
			List<?> items = (List<?>) page.getByXPath(".//table");
			for (int i = 2; i < items.size(); i++)
			{
				HtmlElement table = (HtmlElement) items.get(i);
				List<?> rowitems = (List<?>) table.getByXPath(".//tr");
				for (int j = 1; j < rowitems.size(); j++)
				{
					HtmlElement row = (HtmlElement) rowitems.get(j);
					List<?> nodeitems = (List<?>) row.getByXPath(".//td");
					HtmlElement node  = (HtmlElement) nodeitems.get(0);
					String name = node.asText();
					if (myneeds.contains(name))
					{
						HtmlElement tn = (HtmlElement) nodeitems.get(5); String tnumber = tn.asText(); int number = Integer.parseInt(tnumber);
						double sfqscore = 0.0;
						boolean exit = false; int k = j+1;
						while (exit==false)
						{
							HtmlElement nextrow = (HtmlElement) rowitems.get(k++);
							List<?> nextnodeitems = (List<?>) nextrow.getByXPath(".//td");
							HtmlElement namenode = (HtmlElement) nextnodeitems.get(0);							
							HtmlElement typenode = (HtmlElement) nextnodeitems.get(1);
							String type = typenode.asText();
							if(type.length()==4 || type.length()==5 || type.length()==6)
							{
								HtmlElement score = (HtmlElement) nextnodeitems.get(3);
								String sc = score.asText(); String sfc = sc.substring(0,4);
								if (sfc.contains("-"))
								{
									sfqscore += 0.0;
									continue;
								}
								sfqscore += Double.parseDouble(sfc);
							}
							if (namenode.asText().length() > 6)
								exit = true;
						}
						sfqscore /= number;
						SFQ f = new SFQ();
						f.setname(name);
						f.setsfq(sfqscore);
						result.add(f);
					}
				}
			}	
			return result;
		} catch(Exception e)
		{
			System.out.println(e);
			return null;
		}
	}

	
	/**
	 * @return a hashtable of Instructor SFQ objects with each the name of instructors as key
	 * @param url the URL of the SFQ website
	 */
	public Hashtable<String, Isfq> scrapeinstructorSFQ(String url)	{
		try	{
			HtmlPage page = client.getPage(url);
			Hashtable<String, Isfq> result = new Hashtable<String, Isfq>();
			
			List<?> items = (List<?>) page.getByXPath(".//table"); 
			for (int i = 2; i < items.size(); i++)
			{
				HtmlElement table = (HtmlElement) items.get(i);
				List<?> rowitems = (List<?>) table.getByXPath(".//tr");
				for (int j = 1; j < rowitems.size(); j++)
				{
					HtmlElement row = (HtmlElement) rowitems.get(j);
					List<?> nodeitems = (List<?>) row.getByXPath(".//td");
					for (int k = 0; k < nodeitems.size(); k++)
					{
						HtmlElement node = (HtmlElement) nodeitems.get(k);
						String innode = node.asText();
						String check = ",";
						if (result.containsKey(innode))
						{
							HtmlElement number = (HtmlElement) nodeitems.get(k+2); double score = 0;
							String num = number.asText(); String subnum = num.substring(0,4); String boogey = "-";
							if (subnum.contains(boogey))
								continue;
							else
								{score = Double.parseDouble(subnum);}
							Isfq old = result.get(innode);
							Isfq local = new Isfq();
							local = old;
							local.setname(innode);
							result.remove(innode);
							local.addsfq(score);
							result.put(innode, local);
						}
						else if (innode.contains(check))
						{
							HtmlElement number = (HtmlElement) nodeitems.get(k+2); double score = 0;
							String num = number.asText(); String subnum = num.substring(0,4); String boogey = "-";
							if (subnum.contains(boogey))
								continue;
							else
								{score = Double.parseDouble(subnum);}	
							Isfq f = new Isfq();
							f.setname(innode);
							f.addsfq(score);
							result.put(innode, f);
						}
					}
				}
			}
			return result;
			
		}catch (Exception e) {		System.out.println(e);		return null;		}			
	}
	
	
	/**
	 * @return total number of sections
	 * @param baseurl baseurl
	 * @param term term
	 * @param sub subject
	 */
	public int sections(String baseurl, String term, String sub) {
		try {
			HtmlPage page = client.getPage(baseurl + "/" + term + "/subject/" + sub);
			int totalsections = 0;
			List<?> items = (List<?>) page.getByXPath("//div[@class='course']");
			for (int i = 0; i < items.size(); i++) {
				HtmlElement htmlItem = (HtmlElement) items.get(i);
				List<?> sections = (List<?>) htmlItem.getByXPath(".//tr[contains(@class,'newsect')]");
				for ( HtmlElement e: (List<HtmlElement>)sections) {
					HtmlElement etype = e.getFirstByXPath(".//td");
					String type[] = etype.asText().split(" ");
					if(type[0].startsWith("L") || type[0].startsWith("T"))	{
						totalsections++;
					}
				}	
			}			
			return totalsections;
		} catch (Exception e) {
			System.out.println(e);
			return 0;
		}
	}

	/**
	 * @return total number of courses
	 * @param baseurl baseurl
	 * @param term term
	 * @param sub subject
	 */
	public int course(String baseurl, String term, String sub) {
		try {
			HtmlPage page = client.getPage(baseurl + "/" + term + "/subject/" + sub);
			int totalcourses = 0;
			List<?> items = (List<?>) page.getByXPath("//div[@class='course']");
			for (int i = 0; i < items.size(); i++) {
				HtmlElement htmlItem = (HtmlElement) items.get(i);
				List<?> sections = (List<?>) htmlItem.getByXPath(".//tr[contains(@class,'newsect')]");
				for (HtmlElement e: (List<HtmlElement>)sections)
				{
					HtmlElement etype = e.getFirstByXPath(".//td");
					String type[] = etype.asText().split(" ");
					if(type[0].startsWith("L") || type[0].startsWith("T"))	{
						totalcourses++; break;
					}
				}			
			}
			return totalcourses;
		} catch (Exception e) {
			System.out.println(e);
			return 0;
		}
	}
	/**
	 * @return list of scraped courses
	 * @param baseurl baseurl
	 * @param term term
	 * @param sub subject
	 */
	public List<Course> scrape(String baseurl, String term, String sub) throws Exception {

		try {
			
			HtmlPage page = client.getPage(baseurl + "/" + term + "/subject/" + sub);

			
			List<?> items = (List<?>) page.getByXPath("//div[@class='course']");
			
			Vector<Course> result = new Vector<Course>();
			for (int i = 0; i < items.size(); i++) {
				Course c = new Course();
				HtmlElement htmlItem = (HtmlElement) items.get(i);
				
				HtmlElement title = (HtmlElement) htmlItem.getFirstByXPath(".//h2");
				c.setTitle(title.asText());
				
				List<?> popupdetailslist = (List<?>) htmlItem.getByXPath(".//div[@class='popupdetail']/table/tbody/tr");
				HtmlElement exclusion = null;
				HtmlElement commoncore = null;
				for ( HtmlElement e : (List<HtmlElement>)popupdetailslist) {
					HtmlElement t = (HtmlElement) e.getFirstByXPath(".//th");
					HtmlElement d = (HtmlElement) e.getFirstByXPath(".//td");
					if (t.asText().equals("EXCLUSION")) {
						exclusion = d;
					}
					if (t.asText().equals("ATTRIBUTES") && d.asText().contains("4Y programs"))	{
						commoncore = d;
					}
				}
				c.setExclusion((exclusion == null ? "null" : exclusion.asText()));
				c.setCommonCore((commoncore == null ? false : true));
				
				List<?> sections = (List<?>) htmlItem.getByXPath(".//tr[contains(@class,'newsect')]");
				
				
				for ( HtmlElement e: (List<HtmlElement>)sections) {
					HtmlElement etype = e.getFirstByXPath(".//td");
					String type[] = etype.asText().split(" ");
					if(type[0].startsWith("L") || type[0].startsWith("T"))	{
						Section s = new Section();
						s.setType(type[0]); 
						s.setsID(type[1]);	
						boolean x = addSlot(e, s, false);
						boolean y = true;
						e = (HtmlElement)e.getNextSibling();
						if (e != null && !e.getAttribute("class").contains("newsect"))
							 y = addSlot(e, s, true);
						if (x==true && y==true)
							c.addSection(s);
					}					
				}
				result.add(c);
			}
			client.close();
			return result;
		} catch (Exception e) {
			System.out.println(e);
		}
		return null;
	}

	public static int asscnumber = 0;
	/**
	 * @return list of all scraped courses
	 * @param baseurl baseurl
	 * @param term term
	 */
	public Vector<String> scrapeSubjects(String baseurl, String term)
	{	asscnumber++;
		try 
		{
			HtmlPage page = client.getPage(baseurl + '/' + term +'/');
			
			List<?> items = (List<?>) page.getByXPath("//div[@class='depts']/a");
			Vector<String> subjects = new Vector<String>();
			 for(int i = 0 ; i< items.size();i++)
			 {
					HtmlElement htmlItem = (HtmlElement) items.get(i);	
					subjects.add(htmlItem.asText());
			 }
			 return subjects ; 
		} catch(Exception e)
		{
			System.out.print(e);
		}
	return null;
	}	
}

