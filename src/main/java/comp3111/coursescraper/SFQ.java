
package comp3111.coursescraper;

import java.util.ArrayList;
/**
* regular SFQ class for enrolled courses
*/
public class SFQ {
	private double sfq;
	private String name;
	/**
	 * Default constructor
	 */
	public SFQ()	{
		name = null;
	}
	/**
	 * Copy constructor
	 */
	public SFQ(SFQ s) {
		this.sfq = s.sfq;
		this.name = s.name;
	}
	/**
	 * @param i set the sfq value
	 */
	public void setsfq(double i)	{
		this.sfq = i;
	}
	/**
	 * @return the sfq value
	 */
	public double getsfq()	{
		return sfq;
	}
	/**
	 * @param s set the name of the course
	 */
	public void setname(String s) {
		this.name = s;
	}
	/**
	 *@return the name of the course
	 */
	public String getname()	{
		return name;
	}
}
