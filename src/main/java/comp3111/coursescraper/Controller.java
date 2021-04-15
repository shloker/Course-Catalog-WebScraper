package comp3111.coursescraper;

import java.util.*;
import java.util.function.Supplier;

import java.awt.*;
import java.awt.event.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.paint.Color;
import javafx.event.EventHandler;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import javafx.collections.FXCollections;
import java.util.Random;
import java.util.List;

/**
 * Class to control the JavaFX
 */
public class Controller {

	@FXML
	private Tab tabMain;

	@FXML
	private TextField textfieldTerm;

	@FXML
	private TextField textfieldSubject;

	@FXML
	private Button buttonSearch;

	@FXML
	private TextField textfieldURL;

	@FXML
	private Tab tabStatistic;

	@FXML
	private ComboBox<?> comboboxTimeSlot;

	@FXML
	private Tab tabFilter;

	@FXML
	private Tab tabList;

	@FXML
	private Tab tabTimetable;

	@FXML
	private Tab tabAllSubject;

	@FXML
	private ProgressBar progressbar;

	@FXML
	private TextField textfieldSfqUrl;

	@FXML
	private Button buttonSfqEnrollCourse;

	@FXML
	private Button buttonInstructorSfq;

	@FXML
	private TextArea textAreaConsole;

	@FXML
	private CheckBox AM;

	@FXML
	private CheckBox PM;

	@FXML
	private CheckBox Monday;

	@FXML
	private CheckBox Tuesday;

	@FXML
	private CheckBox Wednesday;

	@FXML
	private CheckBox Thursday;

	@FXML
	private CheckBox Friday;

	@FXML
	private CheckBox Saturday;

	@FXML
	private Button SelectAll;

	@FXML
	private CheckBox CC;

	@FXML
	private CheckBox NoEx;

	@FXML
	private CheckBox LabTut;

	@FXML
	private TableView tableView = new TableView();;

	@FXML
	private TableColumn courseCode = new TableColumn("Course Code");

	@FXML
	private TableColumn courseName = new TableColumn("Course Name");

	@FXML
	private TableColumn section = new TableColumn("Section");

	@FXML
	private TableColumn instructorCol = new TableColumn("Instructor");

	@FXML
	private TableColumn enroll = new TableColumn("Enroll");

	/**
	 * Function to scrape all subjects in the All Subjects Search tab
	 */
	
	public Vector<String> subjects = new Vector<String>();

    int ALL_SUBJECT_SEARCH ;

	private Scraper scraper = new Scraper();

	@FXML
	void allSubjectSearch() {
		buttonSfqEnrollCourse.setDisable(false);
		int TOTAL_NUMBER_OF_COURSES = 0;
    	if (scraper.asscnumber==0)
    	{	
    	subjects=scraper.scrapeSubjects(textfieldURL.getText(), textfieldTerm.getText());
    	 ALL_SUBJECT_SEARCH = subjects.size();
    	System.out.println ("ALL_SUBJECT_SEARCH: "+ALL_SUBJECT_SEARCH);
    	}
    	else
    	{
    		for (int i =0; i <subjects.size();i++)
    		{
    			//System.out.println(subjects);
    			try {
					scraper.scrape(textfieldURL.getText(), textfieldTerm.getText(),subjects.get(i));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    			System.out.println(subjects.get(i)+ "is done.");
    			 
    			progressbar.setStyle("blue");
    			progressbar.setProgress(i/ALL_SUBJECT_SEARCH );
    			progressbar.setVisible(true);
    		}
    	System.out.println("Total number of courses fetched : "+TOTAL_NUMBER_OF_COURSES);
    	}
	}

	/**
	 * Function to refresh the screen and display the filtered courses each time a
	 * filter is pressed
	 * 
	 * @param nvv the list of filtered courses passed from the filter functions
	 */

	static final List<Section> sectionslistn = new ArrayList<Section>();

	@FXML
	void display(List<Course> nvv) {
		textAreaConsole.setText("");
		List<Section> sectionslist = new ArrayList<Section>();
		sectionslist.addAll(sectionslistn);
		for (Course c : nvv) {

			String newline = c.getTitle() + "\n";
			for (int i = 0; i < c.getNumSections(); i++) {
				Section t = c.getSection(i);

				sectionslist.add(t);
				for (int j = 0; j < t.getnumSlots(); j++) {
					Slot x = t.getSlot(j);
					newline += t.getType() + " " + t.getsID() + " : " + x + "\n";
					t.setinstructor(x.getInstructor());
					t.setCodeSec(c.getCode());
					t.setNameSec(c.getName());

				}
			}
			textAreaConsole.setText(textAreaConsole.getText() + "\n" + newline);

		}

		textAreaConsole.setText(textAreaConsole.getText() + "Enrolled Sections are : ");
		for (int i = 0; i < sectionslistn.size(); i++) {
			String newlinee = sectionslistn.get(i).getCodeSec() + sectionslistn.get(i).getNameSec()
					+ sectionslistn.get(i).getType();

			textAreaConsole.setText(textAreaConsole.getText() + "\n" + newlinee);
		}
		ObservableList data = FXCollections.observableList(sectionslist);
		tableView.setItems(data);
		courseCode.setCellValueFactory(new PropertyValueFactory<>("codeSec"));
		section.setCellValueFactory(new PropertyValueFactory<>("type"));
		courseName.setCellValueFactory(new PropertyValueFactory<>("nameSec"));
		instructorCol.setCellValueFactory(new PropertyValueFactory<>("instructor"));
		enroll.setCellValueFactory(new PropertyValueFactory<>("enrolling"));
		tableView.setItems(data);
		tableView.getColumns().setAll(courseCode, section, courseName, instructorCol, enroll);

		for (Section y : sectionslist) {
			y.enrolling.selectedProperty().addListener(new ChangeListener<Boolean>() {

				@Override
				public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) {
					if (new_val) {
						y.setEnrollment(true);
						sectionslistn.add(y);
						refresh(nvv, sectionslistn);
						update_timetable(sectionslistn, new_val, y);
					} else {
						y.setEnrollment(false);
						sectionslistn.remove(y);
						refresh(nvv, sectionslistn);
						update_timetable(sectionslistn, new_val, y);
					}
				}

			});

		}
	}

	/**
	 * Function to refresh the screen and display the filtered courses each time an
	 * enrollment box is changed
	 * 
	 * @param nvv         the list of filtered courses passed from the filter
	 *                    functions
	 * @param sectionlist the static list of sections the student is enrolled into
	 *                    passed when an enrollment box is changed from the listener
	 *                    function
	 */
	@FXML

	void refresh(List<Course> nvv, List<Section> sectionlist) {
		try {
			if (nvv.isEmpty()) {
				textAreaConsole.setText("");
				for (int i = 0; i < sectionlist.size(); i++) {
					String newline = sectionlist.get(i).getCodeSec() + sectionlist.get(i).getNameSec()
							+ sectionlist.get(i).getType();

					textAreaConsole.setText(textAreaConsole.getText() + "\n" + newline);
				}
			}
			textAreaConsole.setText("");
			for (Course u : nvv) {
				String newlinee = u.getTitle() + "\n";
				for (int i = 0; i < u.getNumSections(); i++) {
					Section t = u.getSection(i);

					for (int j = 0; j < t.getnumSlots(); j++) {
						Slot x = t.getSlot(j);
						newlinee += t.getType() + " " + t.getsID() + " : " + x + "\n";
					}
				}
				textAreaConsole.setText(textAreaConsole.getText() + "\n" + newlinee);
			}

			textAreaConsole.setText(textAreaConsole.getText() + "Enrolled Sections are : ");
			for (int i = 0; i < sectionlist.size(); i++) {
				String newline = sectionlist.get(i).getCodeSec() + sectionlist.get(i).getNameSec()
						+ sectionlist.get(i).getType();

				textAreaConsole.setText(textAreaConsole.getText() + "\n" + newline);
			}

		}

		catch (Exception e) {
			String error = "Error 404: Page not Found. The Requested URL was not found.";
			textAreaConsole.setText(error);
		}
	}

	/**
	 * for the button Select All in the filter tab when the button is pressed , all
	 * checkboxes are turned true and their respective functions are called and the
	 * text is changed to de-select all when the button is pressed again, previous
	 * conditions are restored
	 * 
	 * @param nvv the list of filtered courses passed from the filter functions
	 */
	@FXML
	void selectAll() {
		if (SelectAll.getText().equals("Select All")) {
			SelectAll.setText("De-select All");
			Monday.setSelected(true);
			Tuesday.setSelected(true);
			Wednesday.setSelected(true);
			Thursday.setSelected(true);
			Friday.setSelected(true);
			Saturday.setSelected(true);
			CC.setSelected(true);
			AM.setSelected(true);
			PM.setSelected(true);
			NoEx.setSelected(true);
			LabTut.setSelected(true);
			monday();
			tuesday();
			wednesday();
			thursday();
			friday();
			saturday();
			AMT();
			PMT();
			ex();
			cc();
			lab();

		}

		else {
			SelectAll.setText("Select All");
			Monday.setSelected(false);
			Tuesday.setSelected(false);
			Wednesday.setSelected(false);
			Thursday.setSelected(false);
			Friday.setSelected(false);
			Saturday.setSelected(false);
			CC.setSelected(false);
			AM.setSelected(false);
			PM.setSelected(false);
			NoEx.setSelected(false);
			LabTut.setSelected(false);
			textAreaConsole.setText("");
		}

	}

	/**
	 * Filter function for the checkbox AM There are multiple flags that keep count
	 * of the conditions where other checkboxes are ticked or not There are two
	 * conditions when the filter is selected and when the filter is not selected
	 * For either of them, all the courses are scraped and the respective conditions
	 * of each of the other filters are considered the flags help see which course
	 * satisfies the condition- and if it satisfies, it is added the course list
	 * "nvv" they are then displayed using the display() function
	 * 
	 * @exception throws exception e which is a cutom made exception to display
	 *                   error 404 when there is an issue with the scraper
	 */
	@FXML
	void AMT() {
		try {
			int mflag = -1;
			int tflag = -1;
			int wflag = -1;
			int thflag = -1;
			int fflag = -1;
			int sflag = -1;
			int endtime = 0;
			int labchange = 0;
			int lab = 0;
			// if at least one day has changed, check the days condition
			int daychange = 0;
			// what day has been changed
			int daychangem = 0;
			int daychanget = 0;
			int daychangew = 0;
			int daychangeth = 0;
			int daychangef = 0;
			int daychanges = 0;
			int time = 0;
			int typeflag = 0;
			int exchange = 0;
			int cchange = 0;
			if (Monday.isSelected()) {
				mflag = 0;
				daychange = 1;
				daychangem = 1;
			}
			if (Tuesday.isSelected()) {
				tflag = 1;
				daychange = 1;
				daychanget = 1;
			}
			if (Wednesday.isSelected()) {
				wflag = 2;
				daychange = 1;
				daychangew = 1;
			}
			if (Thursday.isSelected()) {
				thflag = 3;
				daychange = 1;
				daychangeth = 1;
			}
			if (Friday.isSelected()) {
				fflag = 4;
				daychange = 1;
				daychangef = 1;
			}
			if (Saturday.isSelected()) {
				sflag = 5;
				daychange = 1;
				daychanges = 1;
			}
			if (PM.isSelected()) {
				time = 1;
			}
			if (LabTut.isSelected()) {
				lab = 1;
				labchange = 1;
			}
			if (NoEx.isSelected()) {

				exchange = 1;
			}
			if (CC.isSelected()) {
				cchange = 1;
			}
			if (AM.isSelected()) {
				List<Course> nvv = new ArrayList<Course>();
				List<Course> v = scraper.scrape(textfieldURL.getText(), textfieldTerm.getText(),
						textfieldSubject.getText());
				for (Course c : v) {
					int timeflag = 0;
					int dayflag = 0;
					int mmflag = 0;
					int ttflag = 0;
					int wwflag = 0;
					int tthflag = 0;
					int ffflag = 0;
					int ssflag = 0;
					int labflag = 0;
					int eflag = 0;
					int cflag = 0;
					if (exchange == 1) {
						if (c.getExclusion().startsWith("nu")) {
							eflag = 1;
						}
					}
					if (cchange == 1) {
						if (c.getCommonCore() == true)
							cflag = 1;
					}
					for (int i = 0; i < c.getNumSections(); i++) {
						Section t = c.getSection(i);
						if (lab == 1) {
							if (t.getType().startsWith("LA") || t.getType().startsWith("T"))
								labflag = 1;
						}

						// for each section get each slot and see if it satisfies the condition or not
						for (int j = 0; j < t.getnumSlots(); j++) {
							Slot x = t.getSlot(j);
							// pm is not selected
							if (time == 0) {
								if (x.getStartHour() < 12)
									timeflag = 1;
							} // pm is selected
							if (time == 1) {
								if (x.getEndHour() >= 12 && x.getStartHour() < 12)
									timeflag = 1;
							}

							// the flags will only change when any of them are selected
							if (x.getDay() == mflag) {
								mmflag = 1;
							} else if (x.getDay() == tflag) {
								ttflag = 1;

							} else if (x.getDay() == wflag) {
								wwflag = 1;

							} else if (x.getDay() == thflag) {
								tthflag = 1;

							} else if (x.getDay() == fflag) {

								ffflag = 1;
							} else if (x.getDay() == sflag) {
								ssflag = 1;

							}
						}
					}
					if (daychange == 1) {
						if ((mmflag == daychangem) && (ttflag == daychanget) && (wwflag == daychangew)
								&& (tthflag == daychangeth) && (ffflag == daychangef) && (ssflag == daychanges)) {
							dayflag = 1;

						}
					}

					if ((exchange == eflag) && (cchange == cflag) && (daychange == dayflag) && (labchange == labflag)) {
						if (timeflag == 1) {
							if (!nvv.contains(c)) {
								nvv.add(c);
							}
						}
					}
					if ((exchange == 0) && (cchange == 0) && (daychange == 0) && (labchange == 0)) {
						if (timeflag == 1) {
							if (!nvv.contains(c)) {
								nvv.add(c);
							}
						}
					}
				}
				display(nvv);
			}

			if (!AM.isSelected()) {
				List<Course> nvv = new ArrayList<Course>();
				List<Course> v = scraper.scrape(textfieldURL.getText(), textfieldTerm.getText(),
						textfieldSubject.getText());
				for (Course c : v) {
					int timeflag = 0;
					int dayflag = 0;
					int mmflag = 0;
					int ttflag = 0;
					int wwflag = 0;
					int tthflag = 0;
					int ffflag = 0;
					int ssflag = 0;
					int labflag = 0;
					int eflag = 0;
					int cflag = 0;
					if (exchange == 1) {
						if (c.getExclusion().startsWith("nu")) {
							eflag = 1;
						}
					}
					if (cchange == 1) {
						if (c.getCommonCore() == true)
							cflag = 1;
					}
					for (int i = 0; i < c.getNumSections(); i++) {
						Section t = c.getSection(i);
						if (lab == 1) {
							if (t.getType().startsWith("LA") || t.getType().startsWith("T"))
								labflag = 1;
						}

						// for each section get each slot and see if it satisfies the condition or not
						for (int j = 0; j < t.getnumSlots(); j++) {
							Slot x = t.getSlot(j);
							// pm is not selected
							if (time == 0) {
								timeflag = 1;
							} // pm is selected
							if (time == 1) {
								if (x.getEndHour() >= 12)
									timeflag = 1;
							}

							// the flags will only change when any of them are selected
							if (x.getDay() == mflag) {
								mmflag = 1;
							} else if (x.getDay() == tflag) {
								ttflag = 1;

							} else if (x.getDay() == wflag) {
								wwflag = 1;

							} else if (x.getDay() == thflag) {
								tthflag = 1;

							} else if (x.getDay() == fflag) {

								ffflag = 1;
							} else if (x.getDay() == sflag) {
								ssflag = 1;

							}
						}
					}
					if (daychange == 1) {
						if ((mmflag == daychangem) && (ttflag == daychanget) && (wwflag == daychangew)
								&& (tthflag == daychangeth) && (ffflag == daychangef) && (ssflag == daychanges)) {
							dayflag = 1;

						}
					}
					if ((exchange == eflag) && (cchange == cflag) && (daychange == dayflag) && (labchange == labflag)) {
						if (timeflag == 1) {
							if (!nvv.contains(c)) {
								nvv.add(c);
							}
						}
					}
					if ((exchange == 0) && (cchange == 0) && (daychange == 0) && (labchange == 0)) {
						if (timeflag == 1) {
							if (!nvv.contains(c)) {
								nvv.add(c);
							}
						}
					}
					if ((exchange == 0) && (cchange == 0) && (daychange == 0) && (labchange == 0) && time == 0) {
						nvv.removeAll(nvv);

					}

				}
				display(nvv);

			}
		} catch (Exception e) {
			String error = "Error 404: Page not Found. The Requested URL was not found.";
			textAreaConsole.setText(error);
		}
	}

	/**
	 * Filter function for the checkbox PM There are multiple flags that keep count
	 * of the conditions where other checkboxes are ticked or not There are two
	 * conditions when the filter is selected and when the filter is not selected
	 * For either of them, all the courses are scraped and the respective conditions
	 * of each of the other filters are considered the flags help see which course
	 * satisfies the condition- and if it satisfies, it is added the course list
	 * "nvv" they are then displayed using the display() function * @exception
	 * throws exception e which is a cutom made exception to display error 404 when
	 * there is an issue with the scraper
	 */
	@FXML
	void PMT() {
		try {
			int mflag = -1;
			int tflag = -1;
			int wflag = -1;
			int thflag = -1;
			int fflag = -1;
			int sflag = -1;
			int endtime = 0;
			int labchange = 0;
			// if at least one day has changed, check the days condition
			int daychange = 0;
			// what day has been changed
			int daychangem = 0;
			int daychanget = 0;
			int daychangew = 0;
			int daychangeth = 0;
			int daychangef = 0;
			int daychanges = 0;
			int time = 0;
			int typeflag = 0;
			int exchange = 0;
			int cchange = 0;
			if (Monday.isSelected()) {
				mflag = 0;
				daychange = 1;
				daychangem = 1;
			}
			if (Tuesday.isSelected()) {
				tflag = 1;
				daychange = 1;
				daychanget = 1;
			}
			if (Wednesday.isSelected()) {
				wflag = 2;
				daychange = 1;
				daychangew = 1;
			}
			if (Thursday.isSelected()) {
				thflag = 3;
				daychange = 1;
				daychangeth = 1;
			}
			if (Friday.isSelected()) {
				fflag = 4;
				daychange = 1;
				daychangef = 1;
			}
			if (Saturday.isSelected()) {
				sflag = 5;
				daychange = 1;
				daychanges = 1;
			}
			if (AM.isSelected()) {
				time = 1;
			}
			if (LabTut.isSelected()) {
				labchange = 1;
			}
			if (NoEx.isSelected()) {
				exchange = 1;
			}
			if (CC.isSelected()) {
				cchange = 1;
			}
			if (PM.isSelected()) {
				List<Course> nvv = new ArrayList<Course>();
				List<Course> v = scraper.scrape(textfieldURL.getText(), textfieldTerm.getText(),
						textfieldSubject.getText());
				for (Course c : v) {
					int timeflag = 0;
					int dayflag = 0;
					int mmflag = 0;
					int ttflag = 0;
					int wwflag = 0;
					int tthflag = 0;
					int ffflag = 0;
					int ssflag = 0;
					int labflag = 0;
					int eflag = 0;
					int cflag = 0;
					if (exchange == 1) {
						if (c.getExclusion().startsWith("nu")) {
							eflag = 1;
						}
					}
					if (cchange == 1) {
						if (c.getCommonCore() == true)
							cflag = 1;
					}
					for (int i = 0; i < c.getNumSections(); i++) {
						Section t = c.getSection(i);
						if (labchange == 1) {
							if (t.getType().startsWith("LA") || t.getType().startsWith("T"))
								labflag = 1;
						}

						// for each section get each slot and see if it satisfies the condition or not
						for (int j = 0; j < t.getnumSlots(); j++) {
							Slot x = t.getSlot(j);
							// am is not selected
							if (time == 0) {
								if (x.getEndHour() >= 12)
									timeflag = 1;
							} // am is selected
							if (time == 1) {
								if (x.getEndHour() >= 12 && x.getStartHour() < 12)
									timeflag = 1;
							}

							// the flags will only change when any of them are selected
							if (x.getDay() == mflag) {
								mmflag = 1;
							} else if (x.getDay() == tflag) {
								ttflag = 1;

							} else if (x.getDay() == wflag) {
								wwflag = 1;

							} else if (x.getDay() == thflag) {
								tthflag = 1;

							} else if (x.getDay() == fflag) {

								ffflag = 1;
							} else if (x.getDay() == sflag) {
								ssflag = 1;

							}
						}
					}
					if (daychange == 1) {
						if ((mmflag == daychangem) && (ttflag == daychanget) && (wwflag == daychangew)
								&& (tthflag == daychangeth) && (ffflag == daychangef) && (ssflag == daychanges)) {
							dayflag = 1;

						}
					}
					if ((exchange == eflag) && (cchange == cflag) && (daychange == dayflag) && (labchange == labflag)) {
						if (timeflag == 1) {
							System.out.println("labflag");
							if (!nvv.contains(c)) {
								nvv.add(c);
							}
						}
					}
					if ((exchange == 0) && (cchange == 0) && (daychange == 0) && (labchange == 0)) {
						if (timeflag == 1) {
							if (!nvv.contains(c)) {
								nvv.add(c);
							}
						}
					}

				}
				display(nvv);
			}

			if (!PM.isSelected()) {
				List<Course> nvv = new ArrayList<Course>();
				List<Course> v = scraper.scrape(textfieldURL.getText(), textfieldTerm.getText(),
						textfieldSubject.getText());
				for (Course c : v) {
					int timeflag = 0;
					int dayflag = 0;
					int mmflag = 0;
					int ttflag = 0;
					int wwflag = 0;
					int tthflag = 0;
					int ffflag = 0;
					int ssflag = 0;
					int labflag = 0;
					int eflag = 0;
					int cflag = 0;
					if (exchange == 1) {
						if (c.getExclusion().startsWith("nu")) {
							eflag = 1;
						}
					}
					if (cchange == 1) {
						if (c.getCommonCore() == true)
							cflag = 1;
					}
					for (int i = 0; i < c.getNumSections(); i++) {
						Section t = c.getSection(i);
						if (labchange == 1) {
							if (t.getType().startsWith("LA") || t.getType().startsWith("T"))
								labflag = 1;
						}

						// for each section get each slot and see if it satisfies the condition or not
						for (int j = 0; j < t.getnumSlots(); j++) {
							Slot x = t.getSlot(j);
							// am is not selected
							if (time == 0) {
								timeflag = 1;
							} // am is selected
							if (time == 1) {
								if (x.getStartHour() < 12)
									timeflag = 1;
							}

							// the flags will only change when any of them are selected
							if (x.getDay() == mflag) {
								mmflag = 1;
							} else if (x.getDay() == tflag) {
								ttflag = 1;

							} else if (x.getDay() == wflag) {
								wwflag = 1;

							} else if (x.getDay() == thflag) {
								tthflag = 1;

							} else if (x.getDay() == fflag) {

								ffflag = 1;
							} else if (x.getDay() == sflag) {
								ssflag = 1;

							}
						}
					}
					if (daychange == 1) {
						if ((mmflag == daychangem) && (ttflag == daychanget) && (wwflag == daychangew)
								&& (tthflag == daychangeth) && (ffflag == daychangef) && (ssflag == daychanges)) {
							dayflag = 1;

						}
					}
					if ((exchange == eflag) && (cchange == cflag) && (daychange == dayflag) && (labchange == labflag)) {
						if (timeflag == 1) {
							if (!nvv.contains(c)) {
								nvv.add(c);
							}
						}
					}
					if ((exchange == 0) && (cchange == 0) && (daychange == 0) && (labchange == 0)) {
						if (timeflag == 1) {
							if (!nvv.contains(c)) {
								nvv.add(c);
							}
						}
					}
					if ((exchange == 0) && (cchange == 0) && (daychange == 0) && (labchange == 0) && time == 0) {
						nvv.removeAll(nvv);
					}
				}
				display(nvv);
			}
		} catch (Exception e) {
			String error = "Error 404: Page not Found. The Requested URL was not found.";
			textAreaConsole.setText(error);
		}

	}

	/**
	 * Filter function for the checkbox Monday There are multiple flags that keep
	 * count of the conditions where other checkboxes are ticked or not There are
	 * two conditions when the filter is selected and when the filter is not
	 * selected For either of them, all the courses are scraped and the respective
	 * conditions of each of the other filters are considered the flags help see
	 * which course satisfies the condition- and if it satisfies, it is added the
	 * course list "nvv" they are then displayed using the display() function
	 * * @exception throws exception e which is a cutom made exception to display
	 * error 404 when there is an issue with the scraper
	 */
	@FXML
	void monday() {
		try {
			int mflag = -1;
			int tflag = -1;
			int wflag = -1;
			int thflag = -1;
			int fflag = -1;
			int sflag = -1;
			int endtime = 0;
			int labchange = 0;
			int lab = 0;
			// if at least one day has changed, check the days condition
			int daychange = 0;
			// what day has been changed
			int daychangem = 0;
			int daychanget = 0;
			int daychangew = 0;
			int daychangeth = 0;
			int daychangef = 0;
			int daychanges = 0;
			int timeam = 0;
			int timepm = 0;
			int typeflag = 0;
			int exchange = 0;
			int cchange = 0;
			if (Monday.isSelected()) {
				mflag = 0;
				daychange = 1;
				daychangem = 1;
			}
			if (Tuesday.isSelected()) {
				tflag = 1;
				daychange = 1;
				daychanget = 1;
			}
			if (Wednesday.isSelected()) {
				wflag = 2;
				daychange = 1;
				daychangew = 1;
			}
			if (Thursday.isSelected()) {
				thflag = 3;
				daychange = 1;
				daychangeth = 1;
			}
			if (Friday.isSelected()) {
				fflag = 4;
				daychange = 1;
				daychangef = 1;
			}
			if (Saturday.isSelected()) {
				sflag = 5;
				daychange = 1;
				daychanges = 1;
			}
			if (AM.isSelected()) {
				timeam = 1;
			}
			if (PM.isSelected()) {
				timepm = 1;
			}
			if (LabTut.isSelected()) {
				lab = 1;
				labchange = 1;
			}
			if (NoEx.isSelected()) {

				exchange = 1;
			}
			if (CC.isSelected()) {
				cchange = 1;
			}
			if (Monday.isSelected()) {
				List<Course> nvv = new ArrayList<Course>();
				List<Course> v = scraper.scrape(textfieldURL.getText(), textfieldTerm.getText(),
						textfieldSubject.getText());
				for (Course c : v) {
					int timeflag = 0;
					int dayflag = 0;
					int mmflag = 0;
					int ttflag = 0;
					int wwflag = 0;
					int tthflag = 0;
					int ffflag = 0;
					int ssflag = 0;
					int labflag = 0;
					int eflag = 0;
					int cflag = 0;
					if (exchange == 1) {
						if (c.getExclusion().startsWith("nu")) {
							eflag = 1;
						}
					}
					if (cchange == 1) {
						if (c.getCommonCore() == true)
							cflag = 1;
					}
					for (int i = 0; i < c.getNumSections(); i++) {
						Section t = c.getSection(i);
						if (lab == 1) {
							if (t.getType().startsWith("LA") || t.getType().startsWith("T"))
								labflag = 1;
						}

						// for each section get each slot and see if it satisfies the condition or not
						for (int j = 0; j < t.getnumSlots(); j++) {
							Slot x = t.getSlot(j);
							// am is not selected and pm is not selected
							if (timeam == 0 && timepm == 0) {
								timeflag = 1; // always 1 and include
							} // am is selected and pm is selected
							if (timeam == 1 && timepm == 1) {
								if (x.getEndHour() >= 12 && x.getStartHour() < 12)
									timeflag = 1;
							}
							// am is false and om is true
							if (timeam == 0 && timepm == 1) {
								if (x.getEndHour() >= 12)
									timeflag = 1;
							}
							if (timeam == 1 && timepm == 0) {
								if (x.getStartHour() < 12)
									timeflag = 1;
							}
							// the flags will only change when any of them are selected
							if (x.getDay() == mflag) {
								mmflag = 1;
							} else if (x.getDay() == tflag) {
								ttflag = 1;

							} else if (x.getDay() == wflag) {
								wwflag = 1;

							} else if (x.getDay() == thflag) {
								tthflag = 1;

							} else if (x.getDay() == fflag) {

								ffflag = 1;
							} else if (x.getDay() == sflag) {
								ssflag = 1;

							}
						}
					}
					if (daychange == 1) {
						if ((mmflag == daychangem) && (ttflag == daychanget) && (wwflag == daychangew)
								&& (tthflag == daychangeth) && (ffflag == daychangef) && (ssflag == daychanges)) {
							dayflag = 1;

						}
					}
					if ((exchange == eflag) && (cchange == cflag) && (daychange == dayflag) && (labchange == labflag)) {
						if (timeflag == 1) {
							if (!nvv.contains(c)) {
								nvv.add(c);
							}
						}
					}
					if ((exchange == 0) && (cchange == 0) && (daychange == 0) && (labchange == 0)) {
						if (timeflag == 1) {
							if (!nvv.contains(c)) {
								nvv.add(c);
							}
						}
					}

				}
				display(nvv);
			}

			if (!Monday.isSelected()) {

				List<Course> nvv = new ArrayList<Course>();
				List<Course> v = scraper.scrape(textfieldURL.getText(), textfieldTerm.getText(),
						textfieldSubject.getText());
				for (Course c : v) {
					int timeflag = 0;
					int dayflag = 0;
					int mmflag = 0;
					int ttflag = 0;
					int wwflag = 0;
					int tthflag = 0;
					int ffflag = 0;
					int ssflag = 0;
					int labflag = 0;
					int eflag = 0;
					int cflag = 0;
					if (exchange == 1) {
						if (c.getExclusion().startsWith("nu")) {
							eflag = 1;

						}
					}
					if (cchange == 1) {
						if (c.getCommonCore() == true)
							cflag = 1;
					}
					for (int i = 0; i < c.getNumSections(); i++) {
						Section t = c.getSection(i);
						if (lab == 1) {
							if (t.getType().startsWith("LA") || t.getType().startsWith("T"))
								labflag = 1;
						}

						// for each section get each slot and see if it satisfies the condition or not
						for (int j = 0; j < t.getnumSlots(); j++) {
							Slot x = t.getSlot(j);
							// am is not selected
							// am is not selected and pm is not selected
							if (timeam == 0 && timepm == 0) {
								timeflag = 1; // always 1 and include
							} // am is selected and pm is selected
							if (timeam == 1 && timepm == 1) {
								if (x.getEndHour() >= 12 && x.getStartHour() < 12)
									timeflag = 1;
							}
							// am is false and om is true
							if (timeam == 0 && timepm == 1) {
								if (x.getEndHour() >= 12)
									timeflag = 1;
							}
							if (timeam == 1 && timepm == 0) {
								if (x.getStartHour() < 12)
									timeflag = 1;
							}

							// the flags will only change when any of them are selected
							if (x.getDay() == mflag) {
								mmflag = 1;
							} else if (x.getDay() == tflag) {
								ttflag = 1;

							} else if (x.getDay() == wflag) {
								wwflag = 1;

							} else if (x.getDay() == thflag) {
								tthflag = 1;

							} else if (x.getDay() == fflag) {

								ffflag = 1;
							} else if (x.getDay() == sflag) {
								ssflag = 1;

							}
						}
					}
					if (daychange == 1) {
						if ((mmflag == daychangem) && (ttflag == daychanget) && (wwflag == daychangew)
								&& (tthflag == daychangeth) && (ffflag == daychangef) && (ssflag == daychanges)) {

							dayflag = 1;

						}
					}
					if ((exchange == eflag) && (cchange == cflag) && (timeflag == 1) && (labchange == labflag)) {

						if (daychange == dayflag) {
							if (!nvv.contains(c)) {
								nvv.add(c);
							}
						}
					}
					if ((exchange == 0) && (cchange == 0) && (daychange == 0) && (labchange == 0)) {
						if (timeflag == 1) {
							if (!nvv.contains(c)) {
								nvv.add(c);
							}
						}
					}
					if ((exchange == 0) && (cchange == 0) && (daychange == 0) && (labchange == 0) && (timeam == 0)
							&& (timepm == 0)) {
						nvv.removeAll(nvv);
					}
				}
				display(nvv);
			}
		} catch (Exception e) {
			String error = "Error 404: Page not Found. The Requested URL was not found.";
			textAreaConsole.setText(error);
		}
	}

	/**
	 * Filter function for the checkbox Tuesday There are multiple flags that keep
	 * count of the conditions where other checkboxes are ticked or not There are
	 * two conditions when the filter is selected and when the filter is not
	 * selected For either of them, all the courses are scraped and the respective
	 * conditions of each of the other filters are considered the flags help see
	 * which course satisfies the condition- and if it satisfies, it is added the
	 * course list "nvv" they are then displayed using the display() function
	 * * @exception throws exception e which is a cutom made exception to display
	 * error 404 when there is an issue with the scraper
	 */
	@FXML
	void tuesday() {
		try {
			int mflag = -1;
			int tflag = -1;
			int wflag = -1;
			int thflag = -1;
			int fflag = -1;
			int sflag = -1;
			int endtime = 0;
			int labchange = 0;
			int lab = 0;
			// if at least one day has changed, check the days condition
			int daychange = 0;
			// what day has been changed
			int daychangem = 0;
			int daychanget = 0;
			int daychangew = 0;
			int daychangeth = 0;
			int daychangef = 0;
			int daychanges = 0;
			int timeam = 0;
			int timepm = 0;
			int typeflag = 0;
			int exchange = 0;
			int cchange = 0;
			if (Monday.isSelected()) {
				mflag = 0;
				daychange = 1;
				daychangem = 1;
			}
			if (Tuesday.isSelected()) {
				tflag = 1;
				daychange = 1;
				daychanget = 1;
			}
			if (Wednesday.isSelected()) {
				wflag = 2;
				daychange = 1;
				daychangew = 1;
			}
			if (Thursday.isSelected()) {
				thflag = 3;
				daychange = 1;
				daychangeth = 1;
			}
			if (Friday.isSelected()) {
				fflag = 4;
				daychange = 1;
				daychangef = 1;
			}
			if (Saturday.isSelected()) {
				sflag = 5;
				daychange = 1;
				daychanges = 1;
			}
			if (AM.isSelected()) {
				timeam = 1;
			}
			if (PM.isSelected()) {
				timepm = 1;
			}
			if (LabTut.isSelected()) {
				lab = 1;
				labchange = 1;
			}
			if (NoEx.isSelected()) {

				exchange = 1;
			}
			if (CC.isSelected()) {
				cchange = 1;
			}
			if (Tuesday.isSelected()) {
				List<Course> nvv = new ArrayList<Course>();
				List<Course> v = scraper.scrape(textfieldURL.getText(), textfieldTerm.getText(),
						textfieldSubject.getText());
				for (Course c : v) {
					int timeflag = 0;
					int dayflag = 0;
					int mmflag = 0;
					int ttflag = 0;
					int wwflag = 0;
					int tthflag = 0;
					int ffflag = 0;
					int ssflag = 0;
					int labflag = 0;
					int eflag = 0;
					int cflag = 0;
					if (exchange == 1) {
						if (c.getExclusion().startsWith("nu")) {
							eflag = 1;
						}
					}
					if (cchange == 1) {
						if (c.getCommonCore() == true)
							cflag = 1;
					}
					for (int i = 0; i < c.getNumSections(); i++) {
						Section t = c.getSection(i);
						if (lab == 1) {
							if (t.getType().startsWith("LA") || t.getType().startsWith("T"))
								labflag = 1;
						}

						// for each section get each slot and see if it satisfies the condition or not
						for (int j = 0; j < t.getnumSlots(); j++) {
							Slot x = t.getSlot(j);
							// am is not selected and pm is not selected
							if (timeam == 0 && timepm == 0) {
								timeflag = 1; // always 1 and include
							} // am is selected and pm is selected
							if (timeam == 1 && timepm == 1) {
								if (x.getEndHour() >= 12 && x.getStartHour() < 12)
									timeflag = 1;
							}
							// am is false and om is true
							if (timeam == 0 && timepm == 1) {
								if (x.getEndHour() >= 12)
									timeflag = 1;
							}
							if (timeam == 1 && timepm == 0) {
								if (x.getStartHour() < 12)
									timeflag = 1;
							}
							// the flags will only change when any of them are selected
							if (x.getDay() == mflag) {
								mmflag = 1;
							} else if (x.getDay() == tflag) {
								ttflag = 1;

							} else if (x.getDay() == wflag) {
								wwflag = 1;

							} else if (x.getDay() == thflag) {
								tthflag = 1;

							} else if (x.getDay() == fflag) {

								ffflag = 1;
							} else if (x.getDay() == sflag) {
								ssflag = 1;

							}
						}
					}
					if (daychange == 1) {
						if ((mmflag == daychangem) && (ttflag == daychanget) && (wwflag == daychangew)
								&& (tthflag == daychangeth) && (ffflag == daychangef) && (ssflag == daychanges)) {
							dayflag = 1;

						}
					}
					if ((exchange == eflag) && (cchange == cflag) && (daychange == dayflag) && (labchange == labflag)) {
						if (timeflag == 1) {
							if (!nvv.contains(c)) {
								nvv.add(c);
							}
						}
					}
					if ((exchange == 0) && (cchange == 0) && (daychange == 0) && (labchange == 0)) {
						if (timeflag == 1) {
							if (!nvv.contains(c)) {
								nvv.add(c);
							}
						}
					}

				}

				display(nvv);
			}

			if (!Tuesday.isSelected()) {

				List<Course> nvv = new ArrayList<Course>();
				List<Course> v = scraper.scrape(textfieldURL.getText(), textfieldTerm.getText(),
						textfieldSubject.getText());
				for (Course c : v) {
					int timeflag = 0;
					int dayflag = 0;
					int mmflag = 0;
					int ttflag = 0;
					int wwflag = 0;
					int tthflag = 0;
					int ffflag = 0;
					int ssflag = 0;
					int labflag = 0;
					int eflag = 0;
					int cflag = 0;
					if (exchange == 1) {
						if (c.getExclusion().startsWith("nu")) {
							eflag = 1;

						}
					}
					if (cchange == 1) {
						if (c.getCommonCore() == true)
							cflag = 1;
					}
					for (int i = 0; i < c.getNumSections(); i++) {
						Section t = c.getSection(i);
						if (lab == 1) {
							if (t.getType().startsWith("LA") || t.getType().startsWith("T"))
								labflag = 1;
						}

						// for each section get each slot and see if it satisfies the condition or not
						for (int j = 0; j < t.getnumSlots(); j++) {
							Slot x = t.getSlot(j);
							// am is not selected
							// am is not selected and pm is not selected
							if (timeam == 0 && timepm == 0) {
								timeflag = 1; // always 1 and include
							} // am is selected and pm is selected
							if (timeam == 1 && timepm == 1) {
								if (x.getEndHour() >= 12 && x.getStartHour() < 12)
									timeflag = 1;
							}
							// am is false and om is true
							if (timeam == 0 && timepm == 1) {
								if (x.getEndHour() >= 12)
									timeflag = 1;
							}
							if (timeam == 1 && timepm == 0) {
								if (x.getStartHour() < 12)
									timeflag = 1;
							}

							// the flags will only change when any of them are selected
							if (x.getDay() == mflag) {
								mmflag = 1;
							} else if (x.getDay() == tflag) {
								ttflag = 1;

							} else if (x.getDay() == wflag) {
								wwflag = 1;

							} else if (x.getDay() == thflag) {
								tthflag = 1;

							} else if (x.getDay() == fflag) {

								ffflag = 1;
							} else if (x.getDay() == sflag) {
								ssflag = 1;

							}
						}
					}
					if (daychange == 1) {
						if ((mmflag == daychangem) && (ttflag == daychanget) && (wwflag == daychangew)
								&& (tthflag == daychangeth) && (ffflag == daychangef) && (ssflag == daychanges)) {

							dayflag = 1;

						}
					}
					if ((exchange == eflag) && (cchange == cflag) && (timeflag == 1) && (labchange == labflag)) {

						if (daychange == dayflag) {
							if (!nvv.contains(c)) {
								nvv.add(c);
							}
						}
					}
					if ((exchange == 0) && (cchange == 0) && (daychange == 0) && (labchange == 0)) {
						if (timeflag == 1) {
							if (!nvv.contains(c)) {
								nvv.add(c);
							}
						}
					}
					if ((exchange == 0) && (cchange == 0) && (daychange == 0) && (labchange == 0) && (timeam == 0)
							&& (timepm == 0)) {
						nvv.removeAll(nvv);
					}

				}
				display(nvv);
			}
		} catch (Exception e) {
			String error = "Error 404: Page not Found. The Requested URL was not found.";
			textAreaConsole.setText(error);
		}
	}

	/**
	 * Filter function for the checkbox Wednesday There are multiple flags that keep
	 * count of the conditions where other checkboxes are ticked or not There are
	 * two conditions when the filter is selected and when the filter is not
	 * selected For either of them, all the courses are scraped and the respective
	 * conditions of each of the other filters are considered the flags help see
	 * which course satisfies the condition- and if it satisfies, it is added the
	 * course list "nvv" they are then displayed using the display() function
	 * * @exception throws exception e which is a cutom made exception to display
	 * error 404 when there is an issue with the scraper
	 */
	@FXML
	void wednesday() {
		try {
			int mflag = -1;
			int tflag = -1;
			int wflag = -1;
			int thflag = -1;
			int fflag = -1;
			int sflag = -1;
			int endtime = 0;
			int labchange = 0;
			int lab = 0;
			// if at least one day has changed, check the days condition
			int daychange = 0;
			// what day has been changed
			int daychangem = 0;
			int daychanget = 0;
			int daychangew = 0;
			int daychangeth = 0;
			int daychangef = 0;
			int daychanges = 0;
			int timeam = 0;
			int timepm = 0;
			int typeflag = 0;
			int exchange = 0;
			int cchange = 0;
			if (Monday.isSelected()) {
				mflag = 0;
				daychange = 1;
				daychangem = 1;
			}
			if (Tuesday.isSelected()) {
				tflag = 1;
				daychange = 1;
				daychanget = 1;
			}
			if (Wednesday.isSelected()) {
				wflag = 2;
				daychange = 1;
				daychangew = 1;
			}
			if (Thursday.isSelected()) {
				thflag = 3;
				daychange = 1;
				daychangeth = 1;
			}
			if (Friday.isSelected()) {
				fflag = 4;
				daychange = 1;
				daychangef = 1;
			}
			if (Saturday.isSelected()) {
				sflag = 5;
				daychange = 1;
				daychanges = 1;
			}
			if (AM.isSelected()) {
				timeam = 1;
			}
			if (PM.isSelected()) {
				timepm = 1;
			}
			if (LabTut.isSelected()) {
				lab = 1;
				labchange = 1;
			}
			if (NoEx.isSelected()) {

				exchange = 1;
			}
			if (CC.isSelected()) {
				cchange = 1;
			}
			if (Wednesday.isSelected()) {
				List<Course> nvv = new ArrayList<Course>();
				List<Course> v = scraper.scrape(textfieldURL.getText(), textfieldTerm.getText(),
						textfieldSubject.getText());
				for (Course c : v) {
					int timeflag = 0;
					int dayflag = 0;
					int mmflag = 0;
					int ttflag = 0;
					int wwflag = 0;
					int tthflag = 0;
					int ffflag = 0;
					int ssflag = 0;
					int labflag = 0;
					int eflag = 0;
					int cflag = 0;
					if (exchange == 1) {
						if (c.getExclusion().startsWith("nu")) {
							eflag = 1;
						}
					}
					if (cchange == 1) {
						if (c.getCommonCore() == true)
							cflag = 1;
					}
					for (int i = 0; i < c.getNumSections(); i++) {
						Section t = c.getSection(i);
						if (lab == 1) {
							if (t.getType().startsWith("LA") || t.getType().startsWith("T"))
								labflag = 1;
						}

						// for each section get each slot and see if it satisfies the condition or not
						for (int j = 0; j < t.getnumSlots(); j++) {
							Slot x = t.getSlot(j);
							// am is not selected and pm is not selected
							if (timeam == 0 && timepm == 0) {
								timeflag = 1; // always 1 and include
							} // am is selected and pm is selected
							if (timeam == 1 && timepm == 1) {
								if (x.getEndHour() >= 12 && x.getStartHour() < 12)
									timeflag = 1;
							}
							// am is false and om is true
							if (timeam == 0 && timepm == 1) {
								if (x.getEndHour() >= 12)
									timeflag = 1;
							}
							if (timeam == 1 && timepm == 0) {
								if (x.getStartHour() < 12)
									timeflag = 1;
							}
							// the flags will only change when any of them are selected
							if (x.getDay() == mflag) {
								mmflag = 1;
							} else if (x.getDay() == tflag) {
								ttflag = 1;

							} else if (x.getDay() == wflag) {
								wwflag = 1;

							} else if (x.getDay() == thflag) {
								tthflag = 1;

							} else if (x.getDay() == fflag) {

								ffflag = 1;
							} else if (x.getDay() == sflag) {
								ssflag = 1;

							}
						}
					}
					if (daychange == 1) {
						if ((mmflag == daychangem) && (ttflag == daychanget) && (wwflag == daychangew)
								&& (tthflag == daychangeth) && (ffflag == daychangef) && (ssflag == daychanges)) {
							dayflag = 1;

						}
					}
					if ((exchange == eflag) && (cchange == cflag) && (daychange == dayflag) && (labchange == labflag)) {
						if (timeflag == 1) {
							if (!nvv.contains(c)) {
								nvv.add(c);
							}
						}
					}
					if ((exchange == 0) && (cchange == 0) && (daychange == 0) && (labchange == 0)) {
						if (timeflag == 1) {
							if (!nvv.contains(c)) {
								nvv.add(c);
							}
						}
					}
				}
				display(nvv);
			}

			if (!Wednesday.isSelected()) {

				List<Course> nvv = new ArrayList<Course>();
				List<Course> v = scraper.scrape(textfieldURL.getText(), textfieldTerm.getText(),
						textfieldSubject.getText());
				for (Course c : v) {
					int timeflag = 0;
					int dayflag = 0;
					int mmflag = 0;
					int ttflag = 0;
					int wwflag = 0;
					int tthflag = 0;
					int ffflag = 0;
					int ssflag = 0;
					int labflag = 0;
					int eflag = 0;
					int cflag = 0;
					if (exchange == 1) {
						if (c.getExclusion().startsWith("nu")) {
							eflag = 1;

						}
					}
					if (cchange == 1) {
						if (c.getCommonCore() == true)
							cflag = 1;
					}
					for (int i = 0; i < c.getNumSections(); i++) {
						Section t = c.getSection(i);
						if (lab == 1) {
							if (t.getType().startsWith("LA") || t.getType().startsWith("T"))
								labflag = 1;
						}

						// for each section get each slot and see if it satisfies the condition or not
						for (int j = 0; j < t.getnumSlots(); j++) {
							Slot x = t.getSlot(j);
							// am is not selected
							// am is not selected and pm is not selected
							if (timeam == 0 && timepm == 0) {
								timeflag = 1; // always 1 and include
							} // am is selected and pm is selected
							if (timeam == 1 && timepm == 1) {
								if (x.getEndHour() >= 12 && x.getStartHour() < 12)
									timeflag = 1;
							}
							// am is false and om is true
							if (timeam == 0 && timepm == 1) {
								if (x.getEndHour() >= 12)
									timeflag = 1;
							}
							if (timeam == 1 && timepm == 0) {
								if (x.getStartHour() < 12)
									timeflag = 1;
							}

							// the flags will only change when any of them are selected
							if (x.getDay() == mflag) {
								mmflag = 1;
							} else if (x.getDay() == tflag) {
								ttflag = 1;

							} else if (x.getDay() == wflag) {
								wwflag = 1;

							} else if (x.getDay() == thflag) {
								tthflag = 1;

							} else if (x.getDay() == fflag) {

								ffflag = 1;
							} else if (x.getDay() == sflag) {
								ssflag = 1;

							}
						}
					}
					if (daychange == 1) {
						if ((mmflag == daychangem) && (ttflag == daychanget) && (wwflag == daychangew)
								&& (tthflag == daychangeth) && (ffflag == daychangef) && (ssflag == daychanges)) {

							dayflag = 1;

						}
					}
					if ((exchange == eflag) && (cchange == cflag) && (timeflag == 1) && (labchange == labflag)) {

						if (daychange == dayflag) {
							if (!nvv.contains(c)) {
								nvv.add(c);
							}
						}
					}
					if ((exchange == 0) && (cchange == 0) && (daychange == 0) && (labchange == 0)) {
						if (timeflag == 1) {
							if (!nvv.contains(c)) {
								nvv.add(c);
							}
						}
					}
					if ((exchange == 0) && (cchange == 0) && (daychange == 0) && (labchange == 0) && (timeam == 0)
							&& (timepm == 0)) {
						nvv.removeAll(nvv);
					}
				}
				display(nvv);

			}
		} catch (Exception e) {
			String error = "Error 404: Page not Found. The Requested URL was not found.";
			textAreaConsole.setText(error);
		}

	}

	/**
	 * Filter function for the checkbox Thursday There are multiple flags that keep
	 * count of the conditions where other checkboxes are ticked or not There are
	 * two conditions when the filter is selected and when the filter is not
	 * selected For either of them, all the courses are scraped and the respective
	 * conditions of each of the other filters are considered the flags help see
	 * which course satisfies the condition- and if it satisfies, it is added the
	 * course list "nvv" they are then displayed using the display() function
	 * * @exception throws exception e which is a cutom made exception to display
	 * error 404 when there is an issue with the scraper
	 */
	@FXML
	void thursday() {
		try {
			int mflag = -1;
			int tflag = -1;
			int wflag = -1;
			int thflag = -1;
			int fflag = -1;
			int sflag = -1;
			int endtime = 0;
			int labchange = 0;
			int lab = 0;
			// if at least one day has changed, check the days condition
			int daychange = 0;
			// what day has been changed
			int daychangem = 0;
			int daychanget = 0;
			int daychangew = 0;
			int daychangeth = 0;
			int daychangef = 0;
			int daychanges = 0;
			int timeam = 0;
			int timepm = 0;
			int typeflag = 0;
			int exchange = 0;
			int cchange = 0;
			if (Monday.isSelected()) {
				mflag = 0;
				daychange = 1;
				daychangem = 1;
			}
			if (Tuesday.isSelected()) {
				tflag = 1;
				daychange = 1;
				daychanget = 1;
			}
			if (Wednesday.isSelected()) {
				wflag = 2;
				daychange = 1;
				daychangew = 1;
			}
			if (Thursday.isSelected()) {
				thflag = 3;
				daychange = 1;
				daychangeth = 1;
			}
			if (Friday.isSelected()) {
				fflag = 4;
				daychange = 1;
				daychangef = 1;
			}
			if (Saturday.isSelected()) {
				sflag = 5;
				daychange = 1;
				daychanges = 1;
			}
			if (AM.isSelected()) {
				timeam = 1;
			}
			if (PM.isSelected()) {
				timepm = 1;
			}
			if (LabTut.isSelected()) {
				lab = 1;
				labchange = 1;
			}
			if (NoEx.isSelected()) {

				exchange = 1;
			}
			if (CC.isSelected()) {
				cchange = 1;
			}
			if (Thursday.isSelected()) {
				List<Course> nvv = new ArrayList<Course>();
				List<Course> v = scraper.scrape(textfieldURL.getText(), textfieldTerm.getText(),
						textfieldSubject.getText());
				for (Course c : v) {
					int timeflag = 0;
					int dayflag = 0;
					int mmflag = 0;
					int ttflag = 0;
					int wwflag = 0;
					int tthflag = 0;
					int ffflag = 0;
					int ssflag = 0;
					int labflag = 0;
					int eflag = 0;
					int cflag = 0;
					if (exchange == 1) {
						if (c.getExclusion().startsWith("nu")) {
							eflag = 1;
						}
					}
					if (cchange == 1) {
						if (c.getCommonCore() == true)
							cflag = 1;
					}
					for (int i = 0; i < c.getNumSections(); i++) {
						Section t = c.getSection(i);
						if (lab == 1) {
							if (t.getType().startsWith("LA") || t.getType().startsWith("T"))
								labflag = 1;
						}

						// for each section get each slot and see if it satisfies the condition or not
						for (int j = 0; j < t.getnumSlots(); j++) {
							Slot x = t.getSlot(j);
							// am is not selected and pm is not selected
							if (timeam == 0 && timepm == 0) {
								timeflag = 1; // always 1 and include
							} // am is selected and pm is selected
							if (timeam == 1 && timepm == 1) {
								if (x.getEndHour() >= 12 && x.getStartHour() < 12)
									timeflag = 1;
							}
							// am is false and om is true
							if (timeam == 0 && timepm == 1) {
								if (x.getEndHour() >= 12)
									timeflag = 1;
							}
							if (timeam == 1 && timepm == 0) {
								if (x.getStartHour() < 12)
									timeflag = 1;
							}
							// the flags will only change when any of them are selected
							if (x.getDay() == mflag) {
								mmflag = 1;
							} else if (x.getDay() == tflag) {
								ttflag = 1;

							} else if (x.getDay() == wflag) {
								wwflag = 1;

							} else if (x.getDay() == thflag) {
								tthflag = 1;

							} else if (x.getDay() == fflag) {

								ffflag = 1;
							} else if (x.getDay() == sflag) {
								ssflag = 1;

							}
						}
					}
					if (daychange == 1) {
						if ((mmflag == daychangem) && (ttflag == daychanget) && (wwflag == daychangew)
								&& (tthflag == daychangeth) && (ffflag == daychangef) && (ssflag == daychanges)) {
							dayflag = 1;

						}
					}
					if ((exchange == eflag) && (cchange == cflag) && (daychange == dayflag) && (labchange == labflag)) {
						if (timeflag == 1) {
							if (!nvv.contains(c)) {
								nvv.add(c);
							}
						}
					}
					if ((exchange == 0) && (cchange == 0) && (daychange == 0) && (labchange == 0)) {
						if (timeflag == 1) {
							if (!nvv.contains(c)) {
								nvv.add(c);
							}
						}
					}
				}
				display(nvv);
			}

			if (!Thursday.isSelected()) {

				List<Course> nvv = new ArrayList<Course>();
				List<Course> v = scraper.scrape(textfieldURL.getText(), textfieldTerm.getText(),
						textfieldSubject.getText());
				for (Course c : v) {
					int timeflag = 0;
					int dayflag = 0;
					int mmflag = 0;
					int ttflag = 0;
					int wwflag = 0;
					int tthflag = 0;
					int ffflag = 0;
					int ssflag = 0;
					int labflag = 0;
					int eflag = 0;
					int cflag = 0;
					if (exchange == 1) {
						if (c.getExclusion().startsWith("nu")) {
							eflag = 1;

						}
					}
					if (cchange == 1) {
						if (c.getCommonCore() == true)
							cflag = 1;
					}
					for (int i = 0; i < c.getNumSections(); i++) {
						Section t = c.getSection(i);
						if (lab == 1) {
							if (t.getType().startsWith("LA") || t.getType().startsWith("T"))
								labflag = 1;
						}

						// for each section get each slot and see if it satisfies the condition or not
						for (int j = 0; j < t.getnumSlots(); j++) {
							Slot x = t.getSlot(j);
							// am is not selected
							// am is not selected and pm is not selected
							if (timeam == 0 && timepm == 0) {
								timeflag = 1; // always 1 and include
							} // am is selected and pm is selected
							if (timeam == 1 && timepm == 1) {
								if (x.getEndHour() >= 12 && x.getStartHour() < 12)
									timeflag = 1;
							}
							// am is false and om is true
							if (timeam == 0 && timepm == 1) {
								if (x.getEndHour() >= 12)
									timeflag = 1;
							}
							if (timeam == 1 && timepm == 0) {
								if (x.getStartHour() < 12)
									timeflag = 1;
							}

							// the flags will only change when any of them are selected
							if (x.getDay() == mflag) {
								mmflag = 1;
							} else if (x.getDay() == tflag) {
								ttflag = 1;

							} else if (x.getDay() == wflag) {
								wwflag = 1;

							} else if (x.getDay() == thflag) {
								tthflag = 1;

							} else if (x.getDay() == fflag) {

								ffflag = 1;
							} else if (x.getDay() == sflag) {
								ssflag = 1;

							}
						}
					}
					if (daychange == 1) {
						if ((mmflag == daychangem) && (ttflag == daychanget) && (wwflag == daychangew)
								&& (tthflag == daychangeth) && (ffflag == daychangef) && (ssflag == daychanges)) {

							dayflag = 1;

						}
					}
					if ((exchange == eflag) && (cchange == cflag) && (timeflag == 1) && (labchange == labflag)) {

						if (daychange == dayflag) {
							if (!nvv.contains(c)) {
								nvv.add(c);
							}
						}
					}
					if ((exchange == 0) && (cchange == 0) && (daychange == 0) && (labchange == 0)) {
						if (timeflag == 1) {
							if (!nvv.contains(c)) {
								nvv.add(c);
							}
						}
					}
					if ((exchange == 0) && (cchange == 0) && (daychange == 0) && (labchange == 0) && (timeam == 0)
							&& (timepm == 0)) {
						nvv.removeAll(nvv);
					}
				}
				display(nvv);
			}
		} catch (Exception e) {
			String error = "Error 404: Page not Found. The Requested URL was not found.";
			textAreaConsole.setText(error);
		}
	}

	/**
	 * Filter function for the checkbox Friday There are multiple flags that keep
	 * count of the conditions where other checkboxes are ticked or not There are
	 * two conditions when the filter is selected and when the filter is not
	 * selected For either of them, all the courses are scraped and the respective
	 * conditions of each of the other filters are considered the flags help see
	 * which course satisfies the condition- and if it satisfies, it is added the
	 * course list "nvv" they are then displayed using the display() function
	 * * @exception throws exception e which is a cutom made exception to display
	 * error 404 when there is an issue with the scraper
	 */
	@FXML
	void friday() {
		try {
			int mflag = -1;
			int tflag = -1;
			int wflag = -1;
			int thflag = -1;
			int fflag = -1;
			int sflag = -1;
			int endtime = 0;
			int labchange = 0;
			int lab = 0;
			// if at least one day has changed, check the days condition
			int daychange = 0;
			// what day has been changed
			int daychangem = 0;
			int daychanget = 0;
			int daychangew = 0;
			int daychangeth = 0;
			int daychangef = 0;
			int daychanges = 0;
			int timeam = 0;
			int timepm = 0;
			int typeflag = 0;
			int exchange = 0;
			int cchange = 0;
			if (Monday.isSelected()) {
				mflag = 0;
				daychange = 1;
				daychangem = 1;
			}
			if (Tuesday.isSelected()) {
				tflag = 1;
				daychange = 1;
				daychanget = 1;
			}
			if (Wednesday.isSelected()) {
				wflag = 2;
				daychange = 1;
				daychangew = 1;
			}
			if (Thursday.isSelected()) {
				thflag = 3;
				daychange = 1;
				daychangeth = 1;
			}
			if (Friday.isSelected()) {
				fflag = 4;
				daychange = 1;
				daychangef = 1;
			}
			if (Saturday.isSelected()) {
				sflag = 5;
				daychange = 1;
				daychanges = 1;
			}
			if (AM.isSelected()) {
				timeam = 1;
			}
			if (PM.isSelected()) {
				timepm = 1;
			}
			if (LabTut.isSelected()) {
				lab = 1;
				labchange = 1;
			}
			if (NoEx.isSelected()) {

				exchange = 1;
			}
			if (CC.isSelected()) {
				cchange = 1;
			}
			if (Friday.isSelected()) {
				List<Course> nvv = new ArrayList<Course>();
				List<Course> v = scraper.scrape(textfieldURL.getText(), textfieldTerm.getText(),
						textfieldSubject.getText());
				for (Course c : v) {
					int timeflag = 0;
					int dayflag = 0;
					int mmflag = 0;
					int ttflag = 0;
					int wwflag = 0;
					int tthflag = 0;
					int ffflag = 0;
					int ssflag = 0;
					int labflag = 0;
					int eflag = 0;
					int cflag = 0;
					if (exchange == 1) {
						if (c.getExclusion().startsWith("nu")) {
							eflag = 1;
						}
					}
					if (cchange == 1) {
						if (c.getCommonCore() == true)
							cflag = 1;
					}
					for (int i = 0; i < c.getNumSections(); i++) {
						Section t = c.getSection(i);
						if (lab == 1) {
							if (t.getType().startsWith("LA") || t.getType().startsWith("T"))
								labflag = 1;
						}

						// for each section get each slot and see if it satisfies the condition or not
						for (int j = 0; j < t.getnumSlots(); j++) {
							Slot x = t.getSlot(j);
							// am is not selected and pm is not selected
							if (timeam == 0 && timepm == 0) {
								timeflag = 1; // always 1 and include
							} // am is selected and pm is selected
							if (timeam == 1 && timepm == 1) {
								if (x.getEndHour() >= 12 && x.getStartHour() < 12)
									timeflag = 1;
							}
							// am is false and om is true
							if (timeam == 0 && timepm == 1) {
								if (x.getEndHour() >= 12)
									timeflag = 1;
							}
							if (timeam == 1 && timepm == 0) {
								if (x.getStartHour() < 12)
									timeflag = 1;
							}
							// the flags will only change when any of them are selected
							if (x.getDay() == mflag) {
								mmflag = 1;
							} else if (x.getDay() == tflag) {
								ttflag = 1;

							} else if (x.getDay() == wflag) {
								wwflag = 1;

							} else if (x.getDay() == thflag) {
								tthflag = 1;

							} else if (x.getDay() == fflag) {

								ffflag = 1;
							} else if (x.getDay() == sflag) {
								ssflag = 1;

							}
						}
					}
					if (daychange == 1) {
						if ((mmflag == daychangem) && (ttflag == daychanget) && (wwflag == daychangew)
								&& (tthflag == daychangeth) && (ffflag == daychangef) && (ssflag == daychanges)) {
							dayflag = 1;

						}
					}
					if ((exchange == eflag) && (cchange == cflag) && (daychange == dayflag) && (labchange == labflag)) {
						if (timeflag == 1) {
							if (!nvv.contains(c)) {
								nvv.add(c);
							}
						}
					}
					if ((exchange == 0) && (cchange == 0) && (daychange == 0) && (labchange == 0)) {
						if (timeflag == 1) {
							if (!nvv.contains(c)) {
								nvv.add(c);
							}
						}
					}
				}
				display(nvv);
			}

			if (!Friday.isSelected()) {

				List<Course> nvv = new ArrayList<Course>();
				List<Course> v = scraper.scrape(textfieldURL.getText(), textfieldTerm.getText(),
						textfieldSubject.getText());
				for (Course c : v) {
					int timeflag = 0;
					int dayflag = 0;
					int mmflag = 0;
					int ttflag = 0;
					int wwflag = 0;
					int tthflag = 0;
					int ffflag = 0;
					int ssflag = 0;
					int labflag = 0;
					int eflag = 0;
					int cflag = 0;
					if (exchange == 1) {
						if (c.getExclusion().startsWith("nu")) {
							eflag = 1;

						}
					}
					if (cchange == 1) {
						if (c.getCommonCore() == true)
							cflag = 1;
					}
					for (int i = 0; i < c.getNumSections(); i++) {
						Section t = c.getSection(i);
						if (lab == 1) {
							if (t.getType().startsWith("LA") || t.getType().startsWith("T"))
								labflag = 1;
						}

						// for each section get each slot and see if it satisfies the condition or not
						for (int j = 0; j < t.getnumSlots(); j++) {
							Slot x = t.getSlot(j);
							// am is not selected
							// am is not selected and pm is not selected
							if (timeam == 0 && timepm == 0) {
								timeflag = 1; // always 1 and include
							} // am is selected and pm is selected
							if (timeam == 1 && timepm == 1) {
								if (x.getEndHour() >= 12 && x.getStartHour() < 12)
									timeflag = 1;
							}
							// am is false and om is true
							if (timeam == 0 && timepm == 1) {
								if (x.getEndHour() >= 12)
									timeflag = 1;
							}
							if (timeam == 1 && timepm == 0) {
								if (x.getStartHour() < 12)
									timeflag = 1;
							}

							// the flags will only change when any of them are selected
							if (x.getDay() == mflag) {
								mmflag = 1;
							} else if (x.getDay() == tflag) {
								ttflag = 1;

							} else if (x.getDay() == wflag) {
								wwflag = 1;

							} else if (x.getDay() == thflag) {
								tthflag = 1;

							} else if (x.getDay() == fflag) {

								ffflag = 1;
							} else if (x.getDay() == sflag) {
								ssflag = 1;

							}
						}
					}
					if (daychange == 1) {
						if ((mmflag == daychangem) && (ttflag == daychanget) && (wwflag == daychangew)
								&& (tthflag == daychangeth) && (ffflag == daychangef) && (ssflag == daychanges)) {

							dayflag = 1;

						}
					}
					if ((exchange == eflag) && (cchange == cflag) && (timeflag == 1) && (labchange == labflag)) {

						if (daychange == dayflag) {
							if (!nvv.contains(c)) {
								nvv.add(c);
							}
						}
					}
					if ((exchange == 0) && (cchange == 0) && (daychange == 0) && (labchange == 0)) {
						if (timeflag == 1) {
							if (!nvv.contains(c)) {
								nvv.add(c);
							}
						}
					}
					if ((exchange == 0) && (cchange == 0) && (daychange == 0) && (labchange == 0) && (timeam == 0)
							&& (timepm == 0)) {
						nvv.removeAll(nvv);
					}
				}
				display(nvv);
			}
		} catch (Exception e) {
			String error = "Error 404: Page not Found. The Requested URL was not found.";
			textAreaConsole.setText(error);
		}
	}

	/**
	 * Filter function for the checkbox Saturday There are multiple flags that keep
	 * count of the conditions where other checkboxes are ticked or not There are
	 * two conditions when the filter is selected and when the filter is not
	 * selected For either of them, all the courses are scraped and the respective
	 * conditions of each of the other filters are considered the flags help see
	 * which course satisfies the condition- and if it satisfies, it is added the
	 * course list "nvv" they are then displayed using the display() function
	 * * @exception throws exception e which is a cutom made exception to display
	 * error 404 when there is an issue with the scraper
	 */
	@FXML
	void saturday() {
		try {
			int mflag = -1;
			int tflag = -1;
			int wflag = -1;
			int thflag = -1;
			int fflag = -1;
			int sflag = -1;
			int endtime = 0;
			int labchange = 0;
			int lab = 0;
			// if at least one day has changed, check the days condition
			int daychange = 0;
			// what day has been changed
			int daychangem = 0;
			int daychanget = 0;
			int daychangew = 0;
			int daychangeth = 0;
			int daychangef = 0;
			int daychanges = 0;
			int timeam = 0;
			int timepm = 0;
			int typeflag = 0;
			int exchange = 0;
			int cchange = 0;
			if (Monday.isSelected()) {
				mflag = 0;
				daychange = 1;
				daychangem = 1;
			}
			if (Tuesday.isSelected()) {
				tflag = 1;
				daychange = 1;
				daychanget = 1;
			}
			if (Wednesday.isSelected()) {
				wflag = 2;
				daychange = 1;
				daychangew = 1;
			}
			if (Thursday.isSelected()) {
				thflag = 3;
				daychange = 1;
				daychangeth = 1;
			}
			if (Friday.isSelected()) {
				fflag = 4;
				daychange = 1;
				daychangef = 1;
			}
			if (Saturday.isSelected()) {
				sflag = 5;
				daychange = 1;
				daychanges = 1;
			}
			if (AM.isSelected()) {
				timeam = 1;
			}
			if (PM.isSelected()) {
				timepm = 1;
			}
			if (LabTut.isSelected()) {
				lab = 1;
				labchange = 1;
			}
			if (NoEx.isSelected()) {

				exchange = 1;
			}
			if (CC.isSelected()) {
				cchange = 1;
			}
			if (Saturday.isSelected()) {
				List<Course> nvv = new ArrayList<Course>();
				List<Course> v = scraper.scrape(textfieldURL.getText(), textfieldTerm.getText(),
						textfieldSubject.getText());
				for (Course c : v) {
					int timeflag = 0;
					int dayflag = 0;
					int mmflag = 0;
					int ttflag = 0;
					int wwflag = 0;
					int tthflag = 0;
					int ffflag = 0;
					int ssflag = 0;
					int labflag = 0;
					int eflag = 0;
					int cflag = 0;
					if (exchange == 1) {
						if (c.getExclusion().startsWith("nu")) {
							eflag = 1;
						}
					}
					if (cchange == 1) {
						if (c.getCommonCore() == true)
							cflag = 1;
					}
					for (int i = 0; i < c.getNumSections(); i++) {
						Section t = c.getSection(i);
						if (lab == 1) {
							if (t.getType().startsWith("LA") || t.getType().startsWith("T"))
								labflag = 1;
						}

						// for each section get each slot and see if it satisfies the condition or not
						for (int j = 0; j < t.getnumSlots(); j++) {
							Slot x = t.getSlot(j);
							// am is not selected and pm is not selected
							if (timeam == 0 && timepm == 0) {
								timeflag = 1; // always 1 and include
							} // am is selected and pm is selected
							if (timeam == 1 && timepm == 1) {
								if (x.getEndHour() >= 12 && x.getStartHour() < 12)
									timeflag = 1;
							}
							// am is false and om is true
							if (timeam == 0 && timepm == 1) {
								if (x.getEndHour() >= 12)
									timeflag = 1;
							}
							if (timeam == 1 && timepm == 0) {
								if (x.getStartHour() < 12)
									timeflag = 1;
							}
							// the flags will only change when any of them are selected
							if (x.getDay() == mflag) {
								mmflag = 1;
							} else if (x.getDay() == tflag) {
								ttflag = 1;

							} else if (x.getDay() == wflag) {
								wwflag = 1;

							} else if (x.getDay() == thflag) {
								tthflag = 1;

							} else if (x.getDay() == fflag) {

								ffflag = 1;
							} else if (x.getDay() == sflag) {
								ssflag = 1;

							}
						}
					}
					if (daychange == 1) {
						if ((mmflag == daychangem) && (ttflag == daychanget) && (wwflag == daychangew)
								&& (tthflag == daychangeth) && (ffflag == daychangef) && (ssflag == daychanges)) {
							dayflag = 1;

						}
					}
					if ((exchange == eflag) && (cchange == cflag) && (daychange == dayflag) && (labchange == labflag)) {
						if (timeflag == 1) {
							if (!nvv.contains(c)) {
								nvv.add(c);
							}
						}
					}
					if ((exchange == 0) && (cchange == 0) && (daychange == 0) && (labchange == 0)) {
						if (timeflag == 1) {
							if (!nvv.contains(c)) {
								nvv.add(c);
							}
						}
					}

				}
				display(nvv);
			}

			if (!Saturday.isSelected()) {

				List<Course> nvv = new ArrayList<Course>();
				List<Course> v = scraper.scrape(textfieldURL.getText(), textfieldTerm.getText(),
						textfieldSubject.getText());
				for (Course c : v) {
					int timeflag = 0;
					int dayflag = 0;
					int mmflag = 0;
					int ttflag = 0;
					int wwflag = 0;
					int tthflag = 0;
					int ffflag = 0;
					int ssflag = 0;
					int labflag = 0;
					int eflag = 0;
					int cflag = 0;
					if (exchange == 1) {
						if (c.getExclusion().startsWith("nu")) {
							eflag = 1;

						}
					}
					if (cchange == 1) {
						if (c.getCommonCore() == true)
							cflag = 1;
					}
					for (int i = 0; i < c.getNumSections(); i++) {
						Section t = c.getSection(i);
						if (lab == 1) {
							if (t.getType().startsWith("LA") || t.getType().startsWith("T"))
								labflag = 1;
						}

						// for each section get each slot and see if it satisfies the condition or not
						for (int j = 0; j < t.getnumSlots(); j++) {
							Slot x = t.getSlot(j);
							// am is not selected
							// am is not selected and pm is not selected
							if (timeam == 0 && timepm == 0) {
								timeflag = 1; // always 1 and include
							} // am is selected and pm is selected
							if (timeam == 1 && timepm == 1) {
								if (x.getEndHour() >= 12 && x.getStartHour() < 12)
									timeflag = 1;
							}
							// am is false and om is true
							if (timeam == 0 && timepm == 1) {
								if (x.getEndHour() >= 12)
									timeflag = 1;
							}
							if (timeam == 1 && timepm == 0) {
								if (x.getStartHour() < 12)
									timeflag = 1;
							}

							// the flags will only change when any of them are selected
							if (x.getDay() == mflag) {
								mmflag = 1;
							} else if (x.getDay() == tflag) {
								ttflag = 1;

							} else if (x.getDay() == wflag) {
								wwflag = 1;

							} else if (x.getDay() == thflag) {
								tthflag = 1;

							} else if (x.getDay() == fflag) {

								ffflag = 1;
							} else if (x.getDay() == sflag) {
								ssflag = 1;

							}
						}
					}
					if (daychange == 1) {
						if ((mmflag == daychangem) && (ttflag == daychanget) && (wwflag == daychangew)
								&& (tthflag == daychangeth) && (ffflag == daychangef) && (ssflag == daychanges)) {

							dayflag = 1;

						}
					}
					if ((exchange == eflag) && (cchange == cflag) && (timeflag == 1) && (labchange == labflag)) {

						if (daychange == dayflag) {
							if (!nvv.contains(c)) {
								nvv.add(c);
							}
						}
					}
					if ((exchange == 0) && (cchange == 0) && (daychange == 0) && (labchange == 0)) {
						if (timeflag == 1) {
							if (!nvv.contains(c)) {
								nvv.add(c);
							}
						}
					}
					if ((exchange == 0) && (cchange == 0) && (daychange == 0) && (labchange == 0) && (timeam == 0)
							&& (timepm == 0)) {
						nvv.removeAll(nvv);
					}
				}
				display(nvv);
			}
		} catch (Exception e) {
			String error = "Error 404: Page not Found. The Requested URL was not found.";
			textAreaConsole.setText(error);
		}
	}

	/**
	 * Filter function for the checkbox NoEx (no exclusion) There are multiple flags
	 * that keep count of the conditions where other checkboxes are ticked or not
	 * There are two conditions when the filter is selected and when the filter is
	 * not selected For either of them, all the courses are scraped and the
	 * respective conditions of each of the other filters are considered the flags
	 * help see which course satisfies the condition- and if it satisfies, it is
	 * added the course list "nvv" they are then displayed using the display()
	 * function * @exception throws exception e which is a cutom made exception to
	 * display error 404 when there is an issue with the scraper
	 */
	@FXML
	void ex() {
		try {
			int mflag = -1;
			int tflag = -1;
			int wflag = -1;
			int thflag = -1;
			int fflag = -1;
			int sflag = -1;
			int endtime = 0;
			int labchange = 0;
			int lab = 0;
			// if at least one day has changed, check the days condition
			int daychange = 0;
			// what day has been changed
			int daychangem = 0;
			int daychanget = 0;
			int daychangew = 0;
			int daychangeth = 0;
			int daychangef = 0;
			int daychanges = 0;
			int timeam = 0;
			int timepm = 0;
			int typeflag = 0;
			int exchange = 0;
			int cchange = 0;
			if (Monday.isSelected()) {
				mflag = 0;
				daychange = 1;
				daychangem = 1;
			}
			if (Tuesday.isSelected()) {
				tflag = 1;
				daychange = 1;
				daychanget = 1;
			}
			if (Wednesday.isSelected()) {
				wflag = 2;
				daychange = 1;
				daychangew = 1;
			}
			if (Thursday.isSelected()) {
				thflag = 3;
				daychange = 1;
				daychangeth = 1;
			}
			if (Friday.isSelected()) {
				fflag = 4;
				daychange = 1;
				daychangef = 1;
			}
			if (Saturday.isSelected()) {
				sflag = 5;
				daychange = 1;
				daychanges = 1;
			}
			if (AM.isSelected()) {
				timeam = 1;
			}
			if (PM.isSelected()) {
				timepm = 1;
			}
			if (LabTut.isSelected()) {
				lab = 1;
				labchange = 1;
			}
			if (NoEx.isSelected()) {

				exchange = 1;
			}
			if (CC.isSelected()) {
				cchange = 1;
			}
			if (NoEx.isSelected()) {
				List<Course> nvv = new ArrayList<Course>();
				List<Course> v = scraper.scrape(textfieldURL.getText(), textfieldTerm.getText(),
						textfieldSubject.getText());
				for (Course c : v) {
					int timeflag = 0;
					int dayflag = 0;
					int mmflag = 0;
					int ttflag = 0;
					int wwflag = 0;
					int tthflag = 0;
					int ffflag = 0;
					int ssflag = 0;
					int labflag = 0;
					int eflag = 0;
					int cflag = 0;
					if (exchange == 1) {
						if (c.getExclusion().startsWith("nu")) {
							eflag = 1;
						}
					}
					if (cchange == 1) {
						if (c.getCommonCore() == true)
							cflag = 1;
					}
					for (int i = 0; i < c.getNumSections(); i++) {
						Section t = c.getSection(i);
						if (lab == 1) {
							if (t.getType().startsWith("LA") || t.getType().startsWith("T"))
								labflag = 1;
						}

						// for each section get each slot and see if it satisfies the condition or not
						for (int j = 0; j < t.getnumSlots(); j++) {
							Slot x = t.getSlot(j);
							// am is not selected and pm is not selected
							if (timeam == 0 && timepm == 0) {
								timeflag = 1; // always 1 and include
							} // am is selected and pm is selected
							if (timeam == 1 && timepm == 1) {
								if (x.getEndHour() >= 12 && x.getStartHour() < 12)
									timeflag = 1;
							}
							// am is false and om is true
							if (timeam == 0 && timepm == 1) {
								if (x.getEndHour() >= 12)
									timeflag = 1;
							}
							if (timeam == 1 && timepm == 0) {
								if (x.getStartHour() < 12)
									timeflag = 1;
							}
							// the flags will only change when any of them are selected
							if (x.getDay() == mflag) {
								mmflag = 1;
							} else if (x.getDay() == tflag) {
								ttflag = 1;

							} else if (x.getDay() == wflag) {
								wwflag = 1;

							} else if (x.getDay() == thflag) {
								tthflag = 1;

							} else if (x.getDay() == fflag) {

								ffflag = 1;
							} else if (x.getDay() == sflag) {
								ssflag = 1;

							}
						}
					}
					if (daychange == 1) {
						if ((mmflag == daychangem) && (ttflag == daychanget) && (wwflag == daychangew)
								&& (tthflag == daychangeth) && (ffflag == daychangef) && (ssflag == daychanges)) {
							dayflag = 1;

						}
					}
					if ((exchange == eflag) && (cchange == cflag) && (daychange == dayflag) && (labchange == labflag)) {
						if (timeflag == 1) {
							if (!nvv.contains(c)) {
								nvv.add(c);
							}
						}
					}
					if ((exchange == 0) && (cchange == 0) && (daychange == 0) && (labchange == 0)) {
						if (timeflag == 1) {
							if (!nvv.contains(c)) {
								nvv.add(c);
							}
						}
					}

				}
				display(nvv);
			}

			if (!NoEx.isSelected()) {

				List<Course> nvv = new ArrayList<Course>();
				List<Course> v = scraper.scrape(textfieldURL.getText(), textfieldTerm.getText(),
						textfieldSubject.getText());
				for (Course c : v) {
					int timeflag = 0;
					int dayflag = 0;
					int mmflag = 0;
					int ttflag = 0;
					int wwflag = 0;
					int tthflag = 0;
					int ffflag = 0;
					int ssflag = 0;
					int labflag = 0;
					int eflag = 0;
					int cflag = 0;
					if (exchange == 1) {
						if (c.getExclusion().startsWith("nu")) {
							eflag = 1;

						}
					}
					if (cchange == 1) {
						if (c.getCommonCore() == true)
							cflag = 1;
					}
					for (int i = 0; i < c.getNumSections(); i++) {
						Section t = c.getSection(i);
						if (lab == 1) {
							if (t.getType().startsWith("LA") || t.getType().startsWith("T"))
								labflag = 1;
						}

						// for each section get each slot and see if it satisfies the condition or not
						for (int j = 0; j < t.getnumSlots(); j++) {
							Slot x = t.getSlot(j);
							// am is not selected
							// am is not selected and pm is not selected
							if (timeam == 0 && timepm == 0) {
								timeflag = 1; // always 1 and include
							} // am is selected and pm is selected
							if (timeam == 1 && timepm == 1) {
								if (x.getEndHour() >= 12 && x.getStartHour() < 12)
									timeflag = 1;
							}
							// am is false and om is true
							if (timeam == 0 && timepm == 1) {
								if (x.getEndHour() >= 12)
									timeflag = 1;
							}
							if (timeam == 1 && timepm == 0) {
								if (x.getStartHour() < 12)
									timeflag = 1;
							}

							// the flags will only change when any of them are selected
							if (x.getDay() == mflag) {
								mmflag = 1;
							} else if (x.getDay() == tflag) {
								ttflag = 1;

							} else if (x.getDay() == wflag) {
								wwflag = 1;

							} else if (x.getDay() == thflag) {
								tthflag = 1;

							} else if (x.getDay() == fflag) {

								ffflag = 1;
							} else if (x.getDay() == sflag) {
								ssflag = 1;

							}
						}
					}
					if (daychange == 1) {
						if ((mmflag == daychangem) && (ttflag == daychanget) && (wwflag == daychangew)
								&& (tthflag == daychangeth) && (ffflag == daychangef) && (ssflag == daychanges)) {

							dayflag = 1;

						}
					}
					if ((exchange == eflag) && (cchange == cflag) && (timeflag == 1) && (labchange == labflag)) {

						if (daychange == dayflag) {
							if (!nvv.contains(c)) {
								nvv.add(c);
							}
						}
					}
					if ((exchange == 0) && (cchange == 0) && (daychange == 0) && (labchange == 0)) {
						if (timeflag == 1) {
							if (!nvv.contains(c)) {
								nvv.add(c);
							}
						}
					}
					if ((exchange == 0) && (cchange == 0) && (daychange == 0) && (labchange == 0) && (timeam == 0)
							&& (timepm == 0)) {
						nvv.removeAll(nvv);
					}
				}
				display(nvv);
			}
		} catch (Exception e) {
			String error = "Error 404: Page not Found. The Requested URL was not found.";
			textAreaConsole.setText(error);
		}
	}

	/**
	 * Filter function for the checkbox Common Core There are multiple flags that
	 * keep count of the conditions where other checkboxes are ticked or not There
	 * are two conditions when the filter is selected and when the filter is not
	 * selected For either of them, all the courses are scraped and the respective
	 * conditions of each of the other filters are considered the flags help see
	 * which course satisfies the condition- and if it satisfies, it is added the
	 * course list "nvv" they are then displayed using the display() function
	 * * @exception throws exception e which is a cutom made exception to display
	 * error 404 when there is an issue with the scraper
	 */
	@FXML
	void cc() {
		try {
			int mflag = -1;
			int tflag = -1;
			int wflag = -1;
			int thflag = -1;
			int fflag = -1;
			int sflag = -1;
			int endtime = 0;
			int labchange = 0;
			int lab = 0;
			// if at least one day has changed, check the days condition
			int daychange = 0;
			// what day has been changed
			int daychangem = 0;
			int daychanget = 0;
			int daychangew = 0;
			int daychangeth = 0;
			int daychangef = 0;
			int daychanges = 0;
			int timeam = 0;
			int timepm = 0;
			int typeflag = 0;
			int exchange = 0;
			int cchange = 0;
			if (Monday.isSelected()) {
				mflag = 0;
				daychange = 1;
				daychangem = 1;
			}
			if (Tuesday.isSelected()) {
				tflag = 1;
				daychange = 1;
				daychanget = 1;
			}
			if (Wednesday.isSelected()) {
				wflag = 2;
				daychange = 1;
				daychangew = 1;
			}
			if (Thursday.isSelected()) {
				thflag = 3;
				daychange = 1;
				daychangeth = 1;
			}
			if (Friday.isSelected()) {
				fflag = 4;
				daychange = 1;
				daychangef = 1;
			}
			if (Saturday.isSelected()) {
				sflag = 5;
				daychange = 1;
				daychanges = 1;
			}
			if (AM.isSelected()) {
				timeam = 1;
			}
			if (PM.isSelected()) {
				timepm = 1;
			}
			if (LabTut.isSelected()) {
				lab = 1;
				labchange = 1;
			}
			if (NoEx.isSelected()) {

				exchange = 1;
			}
			if (CC.isSelected()) {
				cchange = 1;
			}
			if (CC.isSelected()) {
				List<Course> nvv = new ArrayList<Course>();
				List<Course> v = scraper.scrape(textfieldURL.getText(), textfieldTerm.getText(),
						textfieldSubject.getText());
				for (Course c : v) {
					int timeflag = 0;
					int dayflag = 0;
					int mmflag = 0;
					int ttflag = 0;
					int wwflag = 0;
					int tthflag = 0;
					int ffflag = 0;
					int ssflag = 0;
					int labflag = 0;
					int eflag = 0;
					int cflag = 0;
					if (exchange == 1) {
						if (c.getExclusion().startsWith("nu")) {
							eflag = 1;
						}
					}
					if (cchange == 1) {
						if (c.getCommonCore() == true)
							cflag = 1;
					}
					for (int i = 0; i < c.getNumSections(); i++) {
						Section t = c.getSection(i);
						if (lab == 1) {
							if (t.getType().startsWith("LA") || t.getType().startsWith("T"))
								labflag = 1;
						}

						// for each section get each slot and see if it satisfies the condition or not
						for (int j = 0; j < t.getnumSlots(); j++) {
							Slot x = t.getSlot(j);
							// am is not selected and pm is not selected
							if (timeam == 0 && timepm == 0) {
								timeflag = 1; // always 1 and include
							} // am is selected and pm is selected
							if (timeam == 1 && timepm == 1) {
								if (x.getEndHour() >= 12 && x.getStartHour() < 12)
									timeflag = 1;
							}
							// am is false and om is true
							if (timeam == 0 && timepm == 1) {
								if (x.getEndHour() >= 12)
									timeflag = 1;
							}
							if (timeam == 1 && timepm == 0) {
								if (x.getStartHour() < 12)
									timeflag = 1;
							}
							// the flags will only change when any of them are selected
							if (x.getDay() == mflag) {
								mmflag = 1;
							} else if (x.getDay() == tflag) {
								ttflag = 1;

							} else if (x.getDay() == wflag) {
								wwflag = 1;

							} else if (x.getDay() == thflag) {
								tthflag = 1;

							} else if (x.getDay() == fflag) {

								ffflag = 1;
							} else if (x.getDay() == sflag) {
								ssflag = 1;

							}
						}
					}
					if (daychange == 1) {
						if ((mmflag == daychangem) && (ttflag == daychanget) && (wwflag == daychangew)
								&& (tthflag == daychangeth) && (ffflag == daychangef) && (ssflag == daychanges)) {
							dayflag = 1;

						}
					}
					if ((exchange == eflag) && (cchange == cflag) && (daychange == dayflag) && (labchange == labflag)) {
						if (timeflag == 1) {
							if (!nvv.contains(c)) {
								nvv.add(c);
							}
						}
					}
					if ((exchange == 0) && (cchange == 0) && (daychange == 0) && (labchange == 0)) {
						if (timeflag == 1) {
							if (!nvv.contains(c)) {
								nvv.add(c);
							}
						}
					}
				}
				display(nvv);
			}

			if (!CC.isSelected()) {

				List<Course> nvv = new ArrayList<Course>();
				List<Course> v = scraper.scrape(textfieldURL.getText(), textfieldTerm.getText(),
						textfieldSubject.getText());
				for (Course c : v) {
					int timeflag = 0;
					int dayflag = 0;
					int mmflag = 0;
					int ttflag = 0;
					int wwflag = 0;
					int tthflag = 0;
					int ffflag = 0;
					int ssflag = 0;
					int labflag = 0;
					int eflag = 0;
					int cflag = 0;
					if (exchange == 1) {
						if (c.getExclusion().startsWith("nu")) {
							eflag = 1;

						}
					}
					if (cchange == 1) {
						if (c.getCommonCore() == true)
							cflag = 1;
					}
					for (int i = 0; i < c.getNumSections(); i++) {
						Section t = c.getSection(i);
						if (lab == 1) {
							if (t.getType().startsWith("LA") || t.getType().startsWith("T"))
								labflag = 1;
						}

						// for each section get each slot and see if it satisfies the condition or not
						for (int j = 0; j < t.getnumSlots(); j++) {
							Slot x = t.getSlot(j);
							// am is not selected
							// am is not selected and pm is not selected
							if (timeam == 0 && timepm == 0) {
								timeflag = 1; // always 1 and include
							} // am is selected and pm is selected
							if (timeam == 1 && timepm == 1) {
								if (x.getEndHour() >= 12 && x.getStartHour() < 12)
									timeflag = 1;
							}
							// am is false and om is true
							if (timeam == 0 && timepm == 1) {
								if (x.getEndHour() >= 12)
									timeflag = 1;
							}
							if (timeam == 1 && timepm == 0) {
								if (x.getStartHour() < 12)
									timeflag = 1;
							}

							// the flags will only change when any of them are selected
							if (x.getDay() == mflag) {
								mmflag = 1;
							} else if (x.getDay() == tflag) {
								ttflag = 1;

							} else if (x.getDay() == wflag) {
								wwflag = 1;

							} else if (x.getDay() == thflag) {
								tthflag = 1;

							} else if (x.getDay() == fflag) {

								ffflag = 1;
							} else if (x.getDay() == sflag) {
								ssflag = 1;

							}
						}
					}
					if (daychange == 1) {
						if ((mmflag == daychangem) && (ttflag == daychanget) && (wwflag == daychangew)
								&& (tthflag == daychangeth) && (ffflag == daychangef) && (ssflag == daychanges)) {

							dayflag = 1;

						}
					}
					if ((exchange == eflag) && (cchange == cflag) && (timeflag == 1) && (labchange == labflag)) {

						if (daychange == dayflag) {
							if (!nvv.contains(c)) {
								nvv.add(c);
							}
						}
					}
					if ((exchange == 0) && (cchange == 0) && (daychange == 0) && (labchange == 0)) {
						if (timeflag == 1) {
							if (!nvv.contains(c)) {
								nvv.add(c);
							}
						}
					}
					if ((exchange == 0) && (cchange == 0) && (daychange == 0) && (labchange == 0) && (timeam == 0)
							&& (timepm == 0)) {
						nvv.removeAll(nvv);
					}
				}
				display(nvv);
			}
		} catch (Exception e) {
			String error = "Error 404: Page not Found. The Requested URL was not found.";
			textAreaConsole.setText(error);
		}
	}

	/**
	 * Filter function for the checkbox LabTut (including lab and tutorial) There
	 * are multiple flags that keep count of the conditions where other checkboxes
	 * are ticked or not There are two conditions when the filter is selected and
	 * when the filter is not selected For either of them, all the courses are
	 * scraped and the respective conditions of each of the other filters are
	 * considered the flags help see which course satisfies the condition- and if it
	 * satisfies, it is added the course list "nvv" they are then displayed using
	 * the display() function * @exception throws exception e which is a cutom made
	 * exception to display error 404 when there is an issue with the scraper
	 */
	@FXML
	void lab() {
		try {
			int mflag = -1;
			int tflag = -1;
			int wflag = -1;
			int thflag = -1;
			int fflag = -1;
			int sflag = -1;
			int endtime = 0;
			int labchange = 0;
			int lab = 0;
			// if at least one day has changed, check the days condition
			int daychange = 0;
			// what day has been changed
			int daychangem = 0;
			int daychanget = 0;
			int daychangew = 0;
			int daychangeth = 0;
			int daychangef = 0;
			int daychanges = 0;
			int timeam = 0;
			int timepm = 0;
			int typeflag = 0;
			int exchange = 0;
			int cchange = 0;
			if (Monday.isSelected()) {
				mflag = 0;
				daychange = 1;
				daychangem = 1;
			}
			if (Tuesday.isSelected()) {
				tflag = 1;
				daychange = 1;
				daychanget = 1;
			}
			if (Wednesday.isSelected()) {
				wflag = 2;
				daychange = 1;
				daychangew = 1;
			}
			if (Thursday.isSelected()) {
				thflag = 3;
				daychange = 1;
				daychangeth = 1;
			}
			if (Friday.isSelected()) {
				fflag = 4;
				daychange = 1;
				daychangef = 1;
			}
			if (Saturday.isSelected()) {
				sflag = 5;
				daychange = 1;
				daychanges = 1;
			}
			if (AM.isSelected()) {
				timeam = 1;
			}
			if (PM.isSelected()) {
				timepm = 1;
			}
			if (LabTut.isSelected()) {
				lab = 1;
				labchange = 1;
			}
			if (NoEx.isSelected()) {

				exchange = 1;
			}
			if (CC.isSelected()) {
				cchange = 1;
			}
			if (LabTut.isSelected()) {
				List<Course> nvv = new ArrayList<Course>();
				List<Course> v = scraper.scrape(textfieldURL.getText(), textfieldTerm.getText(),
						textfieldSubject.getText());
				for (Course c : v) {
					int timeflag = 0;
					int dayflag = 0;
					int mmflag = 0;
					int ttflag = 0;
					int wwflag = 0;
					int tthflag = 0;
					int ffflag = 0;
					int ssflag = 0;
					int labflag = 0;
					int eflag = 0;
					int cflag = 0;
					if (exchange == 1) {
						if (c.getExclusion().startsWith("nu")) {
							eflag = 1;
						}
					}
					if (cchange == 1) {
						if (c.getCommonCore() == true)
							cflag = 1;
					}
					for (int i = 0; i < c.getNumSections(); i++) {
						Section t = c.getSection(i);
						if (lab == 1) {
							if (t.getType().startsWith("LA") || t.getType().startsWith("T"))
								labflag = 1;
						}

						// for each section get each slot and see if it satisfies the condition or not
						for (int j = 0; j < t.getnumSlots(); j++) {
							Slot x = t.getSlot(j);
							// am is not selected and pm is not selected
							if (timeam == 0 && timepm == 0) {
								timeflag = 1; // always 1 and include
							} // am is selected and pm is selected
							if (timeam == 1 && timepm == 1) {
								if (x.getEndHour() >= 12 && x.getStartHour() < 12)
									timeflag = 1;
							}
							// am is false and om is true
							if (timeam == 0 && timepm == 1) {
								if (x.getEndHour() >= 12)
									timeflag = 1;
							}
							if (timeam == 1 && timepm == 0) {
								if (x.getStartHour() < 12)
									timeflag = 1;
							}
							// the flags will only change when any of them are selected
							if (x.getDay() == mflag) {
								mmflag = 1;
							} else if (x.getDay() == tflag) {
								ttflag = 1;

							} else if (x.getDay() == wflag) {
								wwflag = 1;

							} else if (x.getDay() == thflag) {
								tthflag = 1;

							} else if (x.getDay() == fflag) {

								ffflag = 1;
							} else if (x.getDay() == sflag) {
								ssflag = 1;

							}
						}
					}
					if (daychange == 1) {
						if ((mmflag == daychangem) && (ttflag == daychanget) && (wwflag == daychangew)
								&& (tthflag == daychangeth) && (ffflag == daychangef) && (ssflag == daychanges)) {
							dayflag = 1;

						}
					}
					if ((exchange == eflag) && (cchange == cflag) && (daychange == dayflag) && (labchange == labflag)) {
						if (timeflag == 1) {
							if (!nvv.contains(c)) {
								nvv.add(c);
							}
						}
					}
					if ((exchange == 0) && (cchange == 0) && (daychange == 0) && (labchange == 0)) {
						if (timeflag == 1) {
							if (!nvv.contains(c)) {
								nvv.add(c);
							}
						}
					}

				}
				display(nvv);
			}

			if (!LabTut.isSelected()) {

				List<Course> nvv = new ArrayList<Course>();
				List<Course> v = scraper.scrape(textfieldURL.getText(), textfieldTerm.getText(),
						textfieldSubject.getText());
				for (Course c : v) {
					int timeflag = 0;
					int dayflag = 0;
					int mmflag = 0;
					int ttflag = 0;
					int wwflag = 0;
					int tthflag = 0;
					int ffflag = 0;
					int ssflag = 0;
					int labflag = 0;
					int eflag = 0;
					int cflag = 0;
					if (exchange == 1) {
						if (c.getExclusion().startsWith("nu")) {
							eflag = 1;

						}
					}
					if (cchange == 1) {
						if (c.getCommonCore() == true)
							cflag = 1;
					}
					for (int i = 0; i < c.getNumSections(); i++) {
						Section t = c.getSection(i);
						if (lab == 1) {
							if (t.getType().startsWith("LA") || t.getType().startsWith("T"))
								labflag = 1;
						}

						// for each section get each slot and see if it satisfies the condition or not
						for (int j = 0; j < t.getnumSlots(); j++) {
							Slot x = t.getSlot(j);
							// am is not selected
							// am is not selected and pm is not selected
							if (timeam == 0 && timepm == 0) {
								timeflag = 1; // always 1 and include
							} // am is selected and pm is selected
							if (timeam == 1 && timepm == 1) {
								if (x.getEndHour() >= 12 && x.getStartHour() < 12)
									timeflag = 1;
							}
							// am is false and om is true
							if (timeam == 0 && timepm == 1) {
								if (x.getEndHour() >= 12)
									timeflag = 1;
							}
							if (timeam == 1 && timepm == 0) {
								if (x.getStartHour() < 12)
									timeflag = 1;
							}

							// the flags will only change when any of them are selected
							if (x.getDay() == mflag) {
								mmflag = 1;
							} else if (x.getDay() == tflag) {
								ttflag = 1;

							} else if (x.getDay() == wflag) {
								wwflag = 1;

							} else if (x.getDay() == thflag) {
								tthflag = 1;

							} else if (x.getDay() == fflag) {

								ffflag = 1;
							} else if (x.getDay() == sflag) {
								ssflag = 1;

							}
						}
					}
					if (daychange == 1) {
						if ((mmflag == daychangem) && (ttflag == daychanget) && (wwflag == daychangew)
								&& (tthflag == daychangeth) && (ffflag == daychangef) && (ssflag == daychanges)) {

							dayflag = 1;

						}
					}
					if ((exchange == eflag) && (cchange == cflag) && (timeflag == 1) && (labchange == labflag)) {

						if (daychange == dayflag) {
							if (!nvv.contains(c)) {
								nvv.add(c);
							}
						}
					}
					if ((exchange == 0) && (cchange == 0) && (daychange == 0) && (labchange == 0)) {
						if (timeflag == 1) {
							if (!nvv.contains(c)) {
								nvv.add(c);
							}
						}
					}
					if ((exchange == 0) && (cchange == 0) && (daychange == 0) && (labchange == 0) && (timeam == 0)
							&& (timepm == 0)) {
						nvv.removeAll(nvv);
					}

				}
				display(nvv);
			}
		} catch (Exception e) {
			String error = "Error 404: Page not Found. The Requested URL was not found.";
			textAreaConsole.setText(error);
		}
	}

	/**
	 * initializes the setSfqenrollcourse button to disabled
	 */
	public void initialize() {
		buttonSfqEnrollCourse.setDisable(true);
	}

	/**
	 * calls the search function when clicked on the search button on the main tab
	 * 
	 * @exception throws exception e which is a custom made exception to display
	 *                   error 404 when there is an issue with the scraper
	 */
	@FXML
	void search() {
		buttonSfqEnrollCourse.setDisable(false);
		try {
			textAreaConsole.setText(" ");
			List<Course> v = scraper.scrape(textfieldURL.getText(), textfieldTerm.getText(),
					textfieldSubject.getText());
			for (Course c : v) {
				String newline = c.getTitle() + "\n";
				for (int i = 0; i < c.getNumSections(); i++) {
					Section t = c.getSection(i);
					for (int j = 0; j < t.getnumSlots(); j++) {
						Slot x = t.getSlot(j);
						newline += t.getType() + " " + t.getsID() + " : " + x + "\n";
					}
				}
				textAreaConsole.setText(textAreaConsole.getText() + "\n" + newline);
			}

			int totalsections = scraper.sections(textfieldURL.getText(), textfieldTerm.getText(),
					textfieldSubject.getText());

			textAreaConsole
					.setText(textAreaConsole.getText() + "\n" + "TOTAL NUMBER OF DIFFERENT SECTIONS: " + totalsections);

			int totalvalidcourses = scraper.course(textfieldURL.getText(), textfieldTerm.getText(),
					textfieldSubject.getText());
			;

			textAreaConsole.setText(textAreaConsole.getText() + "\n" + "TOTAL NUMBER OF COURSES: " + totalvalidcourses);

			Vector<String> instructors = new Vector<String>();
			Vector<String> reject = new Vector<String>();
			String timeb = "03:10PM";
			LocalTime boogey = LocalTime.parse(timeb, DateTimeFormatter.ofPattern("hh:mma", Locale.US));
			for (Course c : v) {
				for (int i = 0; i < c.getNumSections(); i++) {
					Section x = c.getSection(i);
					for (int j = 0; j < x.getnumSlots(); j++) {
						Slot y = x.getSlot(j);
						if (y.getStart().isBefore(boogey) && y.getEnd().isAfter(boogey) && y.getDay() == 1) {
							List<String> boo = y.getInstructor();
							for (String s : boo) {
								s.replace("\n", "");
								reject.add(s);
							}

						}
					}
					for (int j = 0; j < x.getnumSlots(); j++) {
						Slot y = x.getSlot(j);
						List<String> name = y.getInstructor();
						for (String s : name) {
							if (!instructors.contains(s) && !s.equals("TBA")) {
								instructors.add(s);
							}
						}
					}
				}
			}
			Vector<String> finallist = new Vector<String>();
			for (int i = 0; i < instructors.size(); i++) {
				String temp = instructors.get(i);
				if (reject.contains(temp))
					continue;
				finallist.add(temp);
			}

			Collections.sort(finallist);
			String line = "";
			for (int i = 0; i < finallist.size(); i++) {
				line += i + 1 + " " + finallist.get(i) + " " + " " + "\n";
			}
			textAreaConsole.setText(textAreaConsole.getText() + "\n"
					+ "INSTRUCTORS WHO DO NOT HAVE A TEACHING ASSIGNMENT AT 3:10PM ON TUESDAY " + "\n" + line);

		} catch (Exception e) {
			String error = "Error 404: Page not Found. The Requested URL was not found.";
			textAreaConsole.setText(error);
		}

	}

	/**
	 * returns the instructors SFQ when clicked on the button on the SFQ tab
	 * 
	 * @exception throws exception e which is a custom made exception to display
	 *                   error 404 when there is an issue with the scraper
	 */
	@FXML
	void findInstructorSfq() {
		Hashtable<String, Isfq> v = scraper.scrapeinstructorSFQ(textfieldSfqUrl.getText());
		String disp = "";
		Set<String> myset = v.keySet();
		for (String key : myset) {
			disp += key + " " + v.get(key).finalsfq() + "\n";
		}
		textAreaConsole.setText(disp);
	}

	/**
	 * returns the SFQ of the enrolled courses when clicked on the button on the SFQ
	 * tab
	 * 
	 * @exception throws exception e which is a cutom made exception to display
	 *                   error 404 when there is an issue with the scraper
	 */
	@FXML
	void findSfqEnrollCourse() {
		Vector<SFQ> v = scraper.scrapecourseSFQ(textfieldSfqUrl.getText());
		String disp = "";
		for (int i = 0; i < v.size(); i++) {
			SFQ c = v.get(i);
			disp += c.getname() + " " + c.getsfq() + "\n";
		}
		textAreaConsole.setText(disp);
	}

	Hashtable<Slot, Label> sitt = new Hashtable<Slot, Label>();
	/**
	 * updates the time table on each update of the enrollment
	 * 
	 * 
	 */
	void update_timetable(List<Section> sectionslistn, boolean addorremove, Section s) {

		AnchorPane ap = (AnchorPane) tabTimetable.getContent();

		List<Color> colours = new ArrayList<Color>();

		final Color[] KELLY_COLORS = {

				Color.web("0xFFB300"), // Vivid Yellow

				Color.web("0x803E75"), // Strong Purple

				Color.web("0xFF6800"), // Vivid Orange

				Color.web("0xA6BDD7"), // Very Light Blue

				Color.web("0xC10020"), // Vivid Red

				Color.web("0xCEA262"), // Grayish Yellow

				Color.web("0x817066"), // Medium Gray

				Color.web("0x007D34"), // Vivid Green

				Color.web("0xF6768E"), // Strong Purplish Pink

				Color.web("0x00538A"), // Strong Blue

				Color.web("0xFF7A5C"), // Strong Yellowish Pink

				Color.web("0x53377A"), // Strong Violet

				Color.web("0xFF8E00"), // Vivid Orange Yellow

				Color.web("0xB32851"), // Strong Purplish Red

				Color.web("0xF4C800"), // Vivid Greenish Yellow

				Color.web("0x7F180D"), // Strong Reddish Brown

				Color.web("0x93AA00"), // Vivid Yellowish Green

				Color.web("0x593315"), // Deep Yellowish Brown

				Color.web("0xF13A13"), // Vivid Reddish Orange

				Color.web("0x232C16"), // Dark Olive Green

		};

		if (addorremove) {

			for (int i = 0; i < sectionslistn.size(); i++)

			{

				for (int j = 0; j < sectionslistn.get(i).getnumSlots(); j++)

				{

					if (!sitt.containsKey(sectionslistn.get(i).getSlot(j)))

					{

						Label ltba = new Label(
								sectionslistn.get(i).getCodeSec() + "\n" + sectionslistn.get(i).getType());

						sitt.put(sectionslistn.get(i).getSlot(j), ltba);

						ltba.setOpacity(0.5);

						ltba.setBackground(
								new Background(new BackgroundFill(KELLY_COLORS[i], CornerRadii.EMPTY, Insets.EMPTY)));

						ltba.setLayoutX((sectionslistn.get(i).getSlot(j).getDay() + 1) * 100);

						ltba.setLayoutY(35 + ((sectionslistn.get(i).getSlot(j).getStartHour() - 9) * 30)
								+ (sectionslistn.get(i).getSlot(j).getStartMinute() * 0.5));

						ltba.setMinWidth(100.0);

						ltba.setMaxWidth(100.0);

						ltba.setMinHeight((sectionslistn.get(i).getSlot(i).getEnd()
								.minusHours(sectionslistn.get(i).getSlot(j).getStart().getHour())
								.minusMinutes(sectionslistn.get(i).getSlot(j).getStart().getMinute()).getHour() * 30)
								+ ((sectionslistn.get(i).getSlot(i).getEnd()
										.minusHours(sectionslistn.get(i).getSlot(j).getStart().getHour())
										.minusMinutes(sectionslistn.get(i).getSlot(j).getStart().getMinute()))
												.getMinute()
										* 0.5));

						ltba.setMaxHeight((sectionslistn.get(i).getSlot(i).getEnd()
								.minusHours(sectionslistn.get(i).getSlot(j).getStart().getHour())
								.minusMinutes(sectionslistn.get(i).getSlot(j).getStart().getMinute()).getHour() * 30)
								+ ((sectionslistn.get(i).getSlot(i).getEnd()
										.minusHours(sectionslistn.get(i).getSlot(j).getStart().getHour())
										.minusMinutes(sectionslistn.get(i).getSlot(j).getStart().getMinute()))
												.getMinute()
										* 0.5));

						ap.getChildren().addAll(ltba);

					}

				}

			}

		}

		if (!addorremove)

		{

			for (int i = 0; i < s.getnumSlots(); i++)

			{

				ap.getChildren().remove(sitt.get(s.getSlot(i)));

				sitt.remove(s.getSlot(i));

			}

		}

	}

}