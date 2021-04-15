package comp3111.coursescraper;

/**
 *  Course is offered by a department 
 */
public class Course {
	private static final int DEFAULT_MAX_SECTIONS = 100;

	private String title;
	private String description;
	private String exclusion;
	private Section[] sections;
	private int numSections;
	private boolean commoncore; // add
	private boolean enrollment;
	public String code;
	public String name;
	/**
	 * Default constructor
	 */
	public Course() {
		sections = new Section[DEFAULT_MAX_SECTIONS];
		for (int i = 0; i < DEFAULT_MAX_SECTIONS; i++)
			sections[i] = null;
		numSections = 0;
		enrollment = false;
		exclusion = null;
		code = null;
		name = null;
	}
	/**
	 * @param Section s add the section for each course 
	 */
	public void addSection(Section s) {
		if (numSections >= DEFAULT_MAX_SECTIONS)
			return;
		sections[numSections++] = s;
	}
	/**
	 * @param index i of the section in list of sections 
	 * @return the the section by the index i  
	 */
	public Section getSection(int i) {
		if (i >= 0 && i < numSections)
			return sections[i];
		return null;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.title = title;

		this.code = title.substring(0, 10);
		

		this.name = title.substring(title.indexOf("-")+1, title.length());
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @return the code of a course 
	 */
	public String getCode() {
		return code;
	}
	/**
	 * @return the name (title) of the course 
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the exclusion
	 */
	public String getExclusion() {
		return exclusion;
	}

	/**
	 * @param exclusion
	 *            the exclusion to set
	 */
	public void setExclusion(String exclusion) {
		this.exclusion = exclusion;
	}

	/**
	 * @return the numSections
	 */
	public int getNumSections() {
		return numSections;
	}

	/**
	 * @param numSections
	 *            the numSections to set
	 */
	public void setNumSlots(int numSections) {
		this.numSections = numSections;
	}
	/**
	 * @param c if the course is a common core or not 
	 */
	public void setCommonCore(boolean c) {
		this.commoncore = c;
	}
	/**
	 * @return the the boolean value if it is common core or not 
	 */
	public boolean getCommonCore() {
		return commoncore;
	}
	/**
	 * @return the boolean if the course has been enrolled into or not 
	 */
	public boolean getenrollment() {
		return enrollment;
	}
	/**
	 * set the enrollment boolean variable for each section  
	 */
	public void setenrollment() {
		for (int i = 0; i < numSections; i++) {
			if (sections[i].getEnrollment() == true)
				this.enrollment = true;
		}
	}

}
