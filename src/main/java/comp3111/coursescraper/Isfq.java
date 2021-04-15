package comp3111.coursescraper;

import java.util.ArrayList;
/**
* Class to get instructors SFQ
*/
public class Isfq {
	private ArrayList<Double> sfq = new ArrayList<Double>();
	private String name;
	private double count = 0;
	private double score = 0;
	/**
	 * constructor
	 */
	public Isfq()	{
		name = null;
	}
	/**
	 * copy constructor
	 */
	public Isfq(Isfq s)
	{
		s.sfq = this.sfq;
		s.name = this.name;
		s.count = this.count;
		s.score = this.score;
	}
	/**
	 * @param s the sfq score to be added
	 */
	public void addsfq(double s)
	{
		sfq.add(s);
		count++;
	}
	/**
	 *@return the final sfq score
	 */
	public double finalsfq()	{
		for (int i = 0; i<sfq.size(); i++)
		{
			score += sfq.get(i);
		}
		score /= count;
		return score;
	}
	
	/**
	 *@param s the instructor name
	 */
	public void setname(String s) {
		this.name = s;
	}
	/**
	 * @return the name
	 */
	public String getname()	{
		return name;
	}
}
