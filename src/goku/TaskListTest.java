package goku;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import goku.action.MakeActionException;
import goku.util.DateUtil;
import hirondelle.date4j.DateTime;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class TaskListTest {
  private TaskList list;
  private List<Task> returnList;

  @Before
  public void setup() {
    list = new TaskList();
    returnList = new ArrayList<>();
  }

  @Test
  public void addTask_multipleTasks_returnsCorrectSize() {
    assertListIsSize(0);
    list.addTask(new Task());
    assertListIsSize(1);
    list.addTask(new Task());
    assertListIsSize(2);
  }

  @Test
  public void addTask_willAlwaysMaintainSequentialId() {
    list.addTask(new Task());
    int second = list.addTask(new Task());
    int third = list.addTask(new Task());
    list.addTask(new Task());
    list.deleteTaskById(third);
    list.deleteTaskById(second);
    int shouldBeSecond = list.addTask(new Task());
    int shouldBeThird = list.addTask(new Task());
    assertEquals(second, shouldBeSecond);
    assertEquals(third, shouldBeThird);
  }

  @Test
  public void getTaskById_IdNotFound_returnsNull() {
    Task aTask = makeTaskWithTitle("abc");
    Task otherTask = makeTaskWithTitle("abcdef");
    list.addTask(aTask);
    list.addTask(otherTask);

    int idNotFound = aTask.getId() + otherTask.getId() + 1;
    Task returned = list.getTaskById(idNotFound);
    assertNull(returned);
    assertListIsSize(2);
  }

  @Test
  public void getTaskById_IdFound_returnsTaskWithId() {
    Task aTask = makeTaskWithTitle("abc");
    Task otherTask = makeTaskWithTitle("abcdef");
    list.addTask(aTask);
    list.addTask(otherTask);

    Task returned = list.getTaskById(otherTask.getId());
    assertEquals(otherTask.getId(), returned.getId());
    assertEquals(otherTask.getTitle(), returned.getTitle());
    assertListIsSize(2);
  }

  @Test
  public void findTaskByTitle_noMatchFound_returnsEmptyTaskList() {
    Task aTask = makeTaskWithTitle("abc");
    Task otherTask = makeTaskWithTitle("abcdef");
    list.addTask(aTask);
    list.addTask(otherTask);
    assertListIsSize(2);

    Task toFind = new Task();
    toFind.setTitle("123");
    returnList = list.findTaskByTitle("123");
    assertReturnListIsSize(0);
    assertListIsSize(2);
  }

  @Test
  public void findTaskByTitle_matchesFound_returnsTaskListWithMatches() {
    Task aTask = makeTaskWithTitle("abc");
    Task otherTask = makeTaskWithTitle("abcdef");
    list.addTask(aTask);
    list.addTask(otherTask);
    assertListIsSize(2);

    Task toFind = new Task();
    toFind.setTitle("abc");
    returnList = list.findTaskByTitle("abc");
    assertReturnListIsSize(2);
    assertListIsSize(2);
  }

  @Test
  public void findTaskByDeadLine_multipleMatches_returnsListWithMultipleMatches()
      throws Exception {
    Task aTask = makeTaskWithTitleAndDeadline("abc", 3, 8, 2014);
    Task otherTask = makeTaskWithTitleAndDeadline("def", 4, 8, 2014);
    list.addTask(aTask);
    list.addTask(otherTask);

    Task dueTask = makeTaskWithTitleAndDeadline("123", 5, 8, 2014);
    returnList = list.findTaskByDeadline(dueTask.getDeadline());
    assertReturnListIsSize(2);
  }

  @Test
  public void findTaskByDeadLine_uniqueMatch_returnsListWithOneMatch()
      throws Exception {
    Task aTask = makeTaskWithTitleAndDeadline("abc", 3, 8, 2014);
    Task otherTask = makeTaskWithTitleAndDeadline("def", 5, 8, 2014);
    list.addTask(aTask);
    list.addTask(otherTask);

    Task dueTask = makeTaskWithTitleAndDeadline("123", 4, 8, 2014);
    returnList = list.findTaskByDeadline(dueTask.getDeadline());
    assertReturnListIsSize(1);
  }

  @Test
  public void findTaskByDeadLine_noMatch_returnsEmptyList() throws Exception {
    Task aTask = makeTaskWithTitleAndDeadline("abc", 3, 8, 2014);
    Task otherTask = makeTaskWithTitleAndDeadline("def", 5, 8, 2014);
    list.addTask(aTask);
    list.addTask(otherTask);

    Task dueTask = makeTaskWithTitleAndDeadline("123", 2, 8, 2014);
    returnList = list.findTaskByDeadline(dueTask.getDeadline());
    assertReturnListIsSize(0);
  }

  @Test
  public void deleteTaskById_idNotFound_doesNotChangeList() throws Exception {
    Task aTask = makeTaskWithTitle("abc");
    Task otherTask = makeTaskWithTitle("abcdef");
    list.addTask(aTask);
    list.addTask(otherTask);

    int idNotFound = aTask.getId() + otherTask.getId() + 1;
    list.deleteTaskById(idNotFound);
    assertListIsSize(2);
  }

  @Test
  public void deleteTaskById_idFound_removesTaskFromList() {
    Task aTask = makeTaskWithTitle("abc");
    Task otherTask = makeTaskWithTitle("abcdef");
    list.addTask(aTask);
    list.addTask(otherTask);

    assertListIsSize(2);
    list.deleteTaskById(aTask.getId());
    assertListIsSize(1);
    list.deleteTaskById(otherTask.getId());
    assertListIsSize(0);
  }

  @Test
  public void deleteTaskByTitle_noMatch_doesNotChangeList() {
    Task aTask = makeTaskWithTitle("abc");
    Task otherTask = makeTaskWithTitle("123");
    list.addTask(aTask);
    list.addTask(otherTask);
    assertListIsSize(2);

    // Task toDelete = makeTaskWithTitle("xyz");
    returnList = list.deleteTaskByTitle("xyz");
    assertListIsSize(2);
    assertReturnListIsSize(0);
  }

  @Test
  public void deleteTaskByTitle_uniqueMatch_removesTaskFromList() {
    Task aTask = makeTaskWithTitle("abc");
    Task otherTask = makeTaskWithTitle("123");
    list.addTask(aTask);
    list.addTask(otherTask);
    assertListIsSize(2);

    returnList = list.deleteTaskByTitle("ab");
    assertListIsSize(1);
    assertReturnListIsSize(1);
  }

  @Test
  public void deleteTaskByTitle_multipleMatches_doesNotChangeList() {
    Task aTask = makeTaskWithTitle("abc");
    Task otherTask = makeTaskWithTitle("abcdef");
    list.addTask(aTask);
    list.addTask(otherTask);
    assertListIsSize(2);

    returnList = list.deleteTaskByTitle("abc");
    assertReturnListIsSize(2);

  }
  
  @Test (expected = AssertionError.class)
  public void findFreeSlots_AssertionErrorDateShouldNotHaveTime() {
    DateTime now = DateUtil.getNow();
    
    list.findFreeSlots(now);
  }
  
  @Test
  public void findFreeSlots_DateHasNoTasks() {
    DateTime today = DateUtil.getNowDate();
    assertListIsSize(0);
    
    List<String> expected = new ArrayList<String>();
    expected.add("[00:00 - 23:59]");
    
    assertEquals(expected, list.findFreeSlots(today));
  }
  
  @Test
  public void findFreeSlots_DateHasTaskThatOccupiesWholeDay() throws MakeActionException {
    DateTime today = DateUtil.getNowDate();
    Task aTask = makeTaskWithTitleAndTodayPeriod("a");
    list.addTask(aTask);
    
    assertEquals(new ArrayList<String>(), list.findFreeSlots(today));
  }

  private Task makeTaskWithTitle(String title) {
    Task t = new Task();
    t.setTitle(title);
    return t;
  }

  private Task makeTaskWithTitleAndDeadline(String title, int d, int m, int y) {
    Task t = new Task();
    t.setTitle(title);
    t.setDeadline(DateTime.forDateOnly(y, m, d));
    return t;
  }
  
  private Task makeTaskWithTitleAndTodayPeriod(String title) throws MakeActionException {
    Task t = new Task();
    t.setTitle(title);
    DateRange period = new DateRange(DateUtil.getNow().getStartOfDay().truncate(DateTime.Unit.SECOND), 
        DateUtil.getNow().getEndOfDay().truncate(DateTime.Unit.SECOND));
    t.setPeriod(period);    
    return t;
  }

  private void assertListIsSize(int size) {
    assertEquals(size, list.size());
  }

  private void assertReturnListIsSize(int size) {
    assertEquals(size, returnList.size());
  }
}
