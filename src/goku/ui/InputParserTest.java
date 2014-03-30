package goku.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import goku.DateRange;
import goku.GOKU;
import goku.action.Action;
import goku.action.AddAction;
import goku.action.DeleteAction;
import goku.action.DisplayAction;
import goku.action.EditAction;
import goku.action.ExitAction;
import goku.action.MakeActionException;
import goku.action.NoAction;
import goku.action.SearchAction;
import goku.util.DateUtil;
import hirondelle.date4j.DateTime;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Splitter;

public class InputParserTest {
  InputParser p;
  Action a;

  @Before
  public void setUp() throws Exception {
    GOKU goku = new GOKU();
    p = new InputParser(goku);
  }

  @After
  public void tearDown() throws Exception {
    p = null;
    a = null;
  }

  @Test(expected = MakeActionException.class)
  public void parse_AddAction_throwsException() throws Exception {
    a = p.parse("");
    assertTrue(a instanceof NoAction);
    a = p.parse(null);
    assertTrue(a instanceof NoAction);
    a = p.parse("add");
    assertTrue(a instanceof NoAction);
  }

  @Test
  public void parse_AddAction() throws Exception {
    a = p.parse("add this is a title");
    assertTrue(a instanceof AddAction);
    AddAction aa;
    aa = (AddAction) a;
    assertEquals("this is a title", aa.getTitle());
    a = p.parse("add this is a title         ");
    assertTrue(a instanceof AddAction);
    aa = (AddAction) a;
    assertEquals("this is a title", aa.getTitle());

    DateTime now = DateUtil.getNow();
    a = p.parse("add this is a task by    tomorrow    ");
    assertTrue(a instanceof AddAction);
    aa = (AddAction) a;
    assertEquals("this is a task", aa.getTitle());
    DateTime later = aa.dline;
    assertTrue(later.gt(now));

    a = p.parse("add this is a task from today to tomorrow    ");
    assertTrue(a instanceof AddAction);
    aa = (AddAction) a;
    assertEquals("this is a task", aa.getTitle());
    assertNotNull(aa.period);

    a = p.parse("add this is a task from 3pm to 4pm    ");
    assertTrue(a instanceof AddAction);
    aa = (AddAction) a;
    assertEquals("this is a task", aa.getTitle());
    assertNotNull(aa.period);

    a = p.parse("add this is a task by 3pm tomorrow");
    assertTrue(a instanceof AddAction);
    aa = (AddAction) a;
    assertEquals("this is a task", aa.getTitle());
    DateTime due = aa.dline;
    assertEquals(new Integer(15), due.getHour());

    a = p.parse("add this is a task by 1.45pm   tomorrow");
    assertTrue(a instanceof AddAction);
    aa = (AddAction) a;
    assertEquals("this is a task", aa.getTitle());
    due = aa.dline;
    assertEquals(new Integer(13), due.getHour());
    assertEquals(new Integer(45), due.getMinute());

    a = p.parse("add this is a task by 1:45pm   tomorrow");
    assertTrue(a instanceof AddAction);
    aa = (AddAction) a;
    assertEquals("this is a task", aa.getTitle());
    due = aa.dline;
    assertEquals(new Integer(13), due.getHour());
    assertEquals(new Integer(45), due.getMinute());

    a = p.parse("add this is a task from     3pm 12/3     to 1pm 20/8/14");
    assertTrue(a instanceof AddAction);
    aa = (AddAction) a;
    assertEquals("this is a task", aa.getTitle());
    DateTime from = aa.period.getStartDate();
    DateTime to = aa.period.getEndDate();
    assertTrue(from.getHour() == 15);
    assertTrue(from.getDay() == 12);
    assertTrue(from.getMonth() == 3);
    assertTrue(to.getHour() == 13);
    assertTrue(to.getDay() == 20);
    assertTrue(to.getMonth() == 8);
    assertTrue(to.getYear() == 2014);
    assertNotNull(aa.period);
    // assertEquals(aa.from, "3pm 12/3");
    // assertEquals(aa.to, "1pm 20/8/14");
  }

  @Test(expected = MakeActionException.class)
  public void parse_DeleteAction_throwsException() throws Exception {
    a = p.parse("delete");
    assertTrue(a instanceof NoAction);
  }

  @Test
  public void parse_DeleteAction() throws Exception {
    DeleteAction da;

    a = p.parse("delete 1");
    assertTrue(a instanceof DeleteAction);
    da = (DeleteAction) a;
    assertEquals(new Integer(1), da.id);
    assertNull(da.title);

    a = p.parse("delete 1 task");
    assertTrue(a instanceof DeleteAction);
    da = (DeleteAction) a;
    assertEquals("1 task", da.title);

    a = p.parse("delete abc");
    assertTrue(a instanceof DeleteAction);
    da = (DeleteAction) a;
    assertEquals("abc", da.title);
  }

  @Test(expected = MakeActionException.class)
  public void parse_EditAction_throwsException() throws Exception {
    a = p.parse("edit");
    assertTrue(a instanceof NoAction);

    a = p.parse("edit 1");
    assertTrue(a instanceof NoAction);
  }

  @Test
  public void parse_EditAction() throws Exception {
    EditAction ea;

    a = p.parse("edit 1 abc");
    assertTrue(a instanceof EditAction);
    ea = (EditAction) a;
    assertEquals("abc", ea.title);
    assertEquals(1, ea.id);

    a = p.parse("edit 1 123");
    assertTrue(a instanceof EditAction);
    ea = (EditAction) a;
    assertEquals("123", ea.title);
    assertEquals(1, ea.id);
    assertNull(ea.isComplete);
    assertNull(ea.dline);
    assertNull(ea.period);

    a = p.parse("edit 1 from 3pm to 6pm");
    assertTrue(a instanceof EditAction);
    ea = (EditAction) a;
    assertEquals(1, ea.id);
    assertNull(ea.isComplete);
    assertNull(ea.title);
    assertNull(ea.dline);
    assertNotNull(ea.period);

    a = p.parse("edit a abc");
    assertTrue(a instanceof NoAction);

    a = p.parse("edit 1 remove");
    assertTrue(a instanceof EditAction);
    ea = (EditAction) a;
    assertEquals(1, ea.id);
    assertEquals(ea.title, "remove");

    a = p.parse("edit 1 remove rubbish");
    assertTrue(a instanceof EditAction);
    ea = (EditAction) a;
    assertEquals(1, ea.id);
    assertEquals(ea.title, "remove rubbish");

    a = p.parse("edit 1 remove deadline");
    assertTrue(a instanceof EditAction);
    ea = (EditAction) a;
    assertEquals(1, ea.id);
    assertEquals(ea.title, null);
    assertTrue(ea.removeDeadline);

    a = p.parse("do 1 abc");
    assertTrue(a instanceof EditAction);
    ea = (EditAction) a;
    assertEquals(1, ea.id);
    assertTrue(ea.isComplete);
    assertNull(ea.dline);
    assertNull(ea.period);
  }

  @Test
  public void parse_DisplayAction() throws Exception {
    a = p.parse("display");
    assertTrue(a instanceof DisplayAction);

    a = p.parse("display completed");
    assertTrue(a instanceof DisplayAction);
    DisplayAction da = (DisplayAction) a;
    assertTrue(da.viewComplete);

    a = p.parse("display overdue");
    assertTrue(a instanceof DisplayAction);
    da = (DisplayAction) a;
    assertTrue(da.viewOverdue);

    a = p.parse("display over");
    assertTrue(a instanceof DisplayAction);
    da = (DisplayAction) a;
  }

  @Test(expected = MakeActionException.class)
  public void parse_SearchAction_throwsException() throws Exception {
    a = p.parse("search");
    assertTrue(a instanceof NoAction);
  }

  @Test
  public void parse_SearchAction() throws Exception {
    SearchAction sa;

    a = p.parse("search abc");
    assertTrue(a instanceof SearchAction);
    sa = (SearchAction) a;
    assertEquals("abc", sa.title);

    a = p.parse("search from today to tomorrow");
    assertTrue(a instanceof SearchAction);
    sa = (SearchAction) a;
    assertNull(sa.title);
    assertNotNull(sa.period);

    a = p.parse("search by tomorrow");
    assertTrue(a instanceof SearchAction);
    sa = (SearchAction) a;
    assertNull(sa.title);
    assertNotNull(sa.dline);

    a = p.parse("search abc by tomorrow");
    assertTrue(a instanceof SearchAction);
    sa = (SearchAction) a;
    assertNotNull(sa.title);
    assertNotNull(sa.dline);
  }

  @Test(expected = MakeActionException.class)
  public void parse_SearchAction_CheckFree_throwsException() throws Exception {
    a = p.parse("free");
    assertTrue(a instanceof NoAction);
  }

  /*
   * SearchAction - Check Free Feature (multiple inputs all combinations)
   * At the parser level, search action is created first then the testFree parameter
   * is flagged true if "free" is the command word detected. The next two test cases
   * checks that the flagging is working properly. The next two test cases ensures
   * that the dates should only be parsed if they are valid. If it's not valid, the
   * action should not go through. There are only a total of 4 possible scenarios.
   */
  @Test
  public void parse_SearchAction_CheckFreeIsTrue() throws Exception {
    a = p.parse("free today");
    assertTrue(a instanceof SearchAction);

    SearchAction sa = (SearchAction) a;
    assertTrue(sa.testFree);
  }

  @Test
  public void parse_SearchAction_CheckFreeIsFalse() throws Exception {
    a = p.parse("search today");
    assertTrue(a instanceof SearchAction);

    SearchAction sa = (SearchAction) a;
    assertFalse(sa.testFree);
  }

  @Test
  public void parse_SearchAction_CheckFreeDateQueryValid() throws Exception {
    a = p.parse("free tmr 10am");
    assertTrue(a instanceof SearchAction);

    SearchAction sa = (SearchAction) a;
    assertNotNull(sa.dateQuery);
  }

  @Test(expected = MakeActionException.class)
  public void parse_SearchAction_CheckFreeDateQueryInvalid() throws Exception {
    a = p.parse("free meh");
    assertTrue(a instanceof NoAction);
  }

  @Test
  public void parse_ExitAction() throws Exception {
    a = p.parse("exit");
    assertTrue(a instanceof ExitAction);
    a = p.parse("quit");
    assertTrue(a instanceof ExitAction);
    a = p.parse("q");
    assertTrue(a instanceof ExitAction);
  }

  /*
   * extractDate() Specifics
   * 1) Contains date and time => returns DateTime with date and time
   * 2) Contains date only => returns DateTime with date only
   * 3) Contains time only => returns DateTime with today as date and time
   * 4) Returns null if input is not valid
   */
  @Test
  public void extractDate_SpecificDateSpecificTime() {
    List<String> input = Splitter.on(' ').omitEmptyStrings().trimResults()
        .splitToList("by tmr 10am");
    String[] inputArray = input.toArray(new String[input.size()]);
    p.params = inputArray;

    DateTime resultDate = p.extractDate();

    assertNotNull(resultDate.getDay());
    assertEquals((Integer) 10, resultDate.getHour());
  }

  @Test
  public void extractDate_SpecificDateOnly() {
    List<String> input = Splitter.on(' ').omitEmptyStrings().trimResults()
        .splitToList("by tmr");
    String[] inputArray = input.toArray(new String[input.size()]);
    p.params = inputArray;

    DateTime resultDate = p.extractDate();

    assertNotNull(resultDate.getDay());
    assertEquals((Integer) 23, resultDate.getHour());
    assertEquals((Integer) 59, resultDate.getMinute());
  }

  @Test
  public void extractDate_SpecificTimeOnly() {
    List<String> input = Splitter.on(' ').omitEmptyStrings().trimResults()
        .splitToList("by 12pm");
    String[] inputArray = input.toArray(new String[input.size()]);
    p.params = inputArray;

    DateTime resultDate = p.extractDate();

    assertEquals(DateUtil.getNowDate().getDay(), resultDate.getDay());
    assertEquals((Integer) 12, resultDate.getHour());
    assertEquals((Integer) 00, resultDate.getMinute());
  }

  @Test
  public void extractDate_NoValidInput() {
    List<String> input = Splitter.on(' ').omitEmptyStrings().trimResults()
        .splitToList("by aaa");
    String[] inputArray = input.toArray(new String[input.size()]);
    p.params = inputArray;

    DateTime resultDate = p.extractDate();

    assertNull(resultDate);
  }

  /*
   * extractPeriod() Specifics
   * 1) Start: date+time End: date+time => Start: date+time End: date+time
   * 2) Start: date only End: date only => Start: date+00:00 End: date+23:59
   */
  @Test
  public void extractPeriod_SpecificDatesOnly() {
    List<String> input = Splitter.on(' ').omitEmptyStrings().
        trimResults().splitToList("from today to tmr");
        String[] inputArray = input.toArray(new String[input.size()]);
        p.params = inputArray;
        
        DateRange resultRange = p.extractPeriod();
        
        assertEquals(DateUtil.getNowDate(), resultRange.getStartDate());
  }

  @Test
  public void extractPeriod_SpecificDatesSpecificTimes() {

  }

  @Test
  public void extractPeriod_SpecificTimesOnly() {

  }

  @Test
  public void extractPeriod_SpecificDatesStartTimeOnly() {

  }

  @Test
  public void extractPeriod_SpecificDatesEndTimeOnly() {

  }

  @Test
  public void extractPeriod_SpecificStartTimeSpecificEndDateTime() {

  }

  @Test
  public void extractPeriod_InvalidPeriodStartDateAfterEndDate() {

  }
}
