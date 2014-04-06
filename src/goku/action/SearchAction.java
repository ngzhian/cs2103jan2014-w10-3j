package goku.action;

import goku.DateRange;
import goku.GOKU;
import goku.Result;
import goku.Task;
import goku.TaskList;
import goku.util.DateUtil;
import hirondelle.date4j.DateTime;

import java.util.List;

public class SearchAction extends Action {

  public String title;
  public String deadline;
  public String from;
  public String to;
  public DateTime dline;
  public DateRange period;
  public boolean testFree = false;
  public DateTime dateQuery;

  private static final String MSG_SUCCESS = "Found tasks for \"%s\"...";

  private static final String MSG_FAIL_BY_TITLE = "No relevant tasks for \"%s\".";
  private static final String MSG_FAIL = "No relevant tasks.";
  private static final String IS_FREE = "Specified datetime is available.";
  private static final String NOT_FREE = "Specified datetime is not available.";
  private static final String FREE_SLOTS = "Here are your free timings of the day.";
  private static final String NO_FREE_SLOTS = "Your day is fully packed!";
  public static final String ERR_INSUFFICIENT_ARGS = "Can't search! Try \"search title\"";
  public static final String ERR_NO_VALID_DATE_FOUND = "Can't search! Try entering a valid date after \"free\"";
  private static final String ERR_DEADLINE_PERIOD_CONFLICT = "Can't search! Conflicting deadline and period.";
  private static final String MSG_HAS_OVERDUE = "[!] You have overdue tasks, \"view overdue\" to see them.";

  public SearchAction(GOKU goku) {
    super(goku);
    shouldSaveAfter = false;
  }

  /*
   * Case 1: dateQuery contains specific date and time Checks whether dateQuery
   * contains any tasks whose period coincides If there is => not free, else =>
   * free
   * 
   * Case 2: dateQuery only contains date Shows users the list of free(?)
   * timeslots of the specified day
   */
  public Result checkFreeTime() throws MakeActionException {
    assert (dateQuery != null);

    // Case 1: Specific date and time
    if (dateQuery.getHour() != null) {
      return checkIfFree();
    } else { // Case 2: Only date given
      return checkFreeSlots();
    }
  }

  private Result checkIfFree() {
    if (list.isFree(dateQuery) == true) {
      return new Result(true, IS_FREE, null, null);
    } else {
      return new Result(false, null, NOT_FREE, null);
    }
  }
  
  private Result checkFreeSlots() throws MakeActionException {
    assert dateQuery.getHour() == null;
    
    List<String> freeSlots = list.findFreeSlots(dateQuery);
    
    if (!freeSlots.isEmpty()) {
      return new Result(true, FREE_SLOTS, null, freeSlots, null);
    } else {
      return new Result(false, null, NO_FREE_SLOTS, null);
    }
  }

  @Override
  public Result doIt() {

    Result result = null;

    if (dateQuery != null) {
      try {
        result = checkFreeTime();
      } catch (MakeActionException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    } else if (dline != null && period != null) {
      result = searchByDeadlineInPeriod();
    } else if (dline != null) {
      result = searchByDeadline();
    } else if (period != null) {
      result = searchByPeriod();
    } else {
      result = searchTitle();
    }

    return result;
  }

  public String editMsgIfHaveOverdue(String msg) {
    if (list.getOverdue().size() != 0) {
      msg += System.lineSeparator() + MSG_HAS_OVERDUE;
    }
    return msg;
  }

  public String getDeadline() {
    return deadline;
  }

  public String getTitle() {
    return title;
  }

  public Result searchByDeadline() {
    Task task = new Task();

    assert (dline != null);
    task.setDeadline(dline);
    List<Task> foundTasks = list.findTaskByDeadline(dline);
    if (foundTasks.size() != 0) {
      return new Result(true, String.format(MSG_SUCCESS,
          DateUtil.toString(dline)), null, foundTasks);
    } else {
      return new Result(false, null, editMsgIfHaveOverdue(String.format(
          MSG_FAIL, DateUtil.toString(dline))), null);
    }
  }

  /*
   * Searches for tasks within a given period that has the specified deadline.
   */
  private Result searchByDeadlineInPeriod() {
    // check for conflicting deadline and period
    if (DateUtil.isEarlierThan(dline, period.getStartDate())
        || DateUtil.isLaterThan(dline, period.getEndDate())) {
      return new Result(false, null, ERR_DEADLINE_PERIOD_CONFLICT, null);
    }

    Result byPeriod = searchByPeriod();
    TaskList tasksDueInPeriod = new TaskList();

    if (byPeriod.getTasks() != null) {
      for (Task task : byPeriod.getTasks()) {
        if (task.getDeadline()!=null && DateUtil.isEarlierOrOn(task.getDeadline(), dline)) {
          tasksDueInPeriod.addTaskWithoutSettingId(task);
        }
      }

      return new Result(true, String.format(MSG_SUCCESS, period.toString()),
          null, tasksDueInPeriod.asList());
    } else {
      return new Result(false, null, editMsgIfHaveOverdue(MSG_FAIL), null);
    }
  }

  /*
   * Searches for tasks that fall within a specified period. Tasks that
   * start/end, or has deadline within the period is a match. Example: --- Time
   * line --------------------------------> 1 2 3 4 5 6 7 8 9 10 11
   * ########################## | |-----| |---| | |------| | A B C D E F
   * 
   * | - a task with a deadline at that point in time |-----| a task with a
   * period starting at the first | and ending at second | ### - the period we
   * wish to query for Search should then return tasks, B, C, D, E
   */
  public Result searchByPeriod() {
    Task task = new Task();

    assert (period != null);

    task.setPeriod(period);
    List<Task> foundTasks = list.findTaskByPeriod(period);
    if (foundTasks.size() != 0) {
      return new Result(true, String.format(MSG_SUCCESS, period), null,
          foundTasks);
    } else {
      return new Result(false, null, editMsgIfHaveOverdue(MSG_FAIL), null);
    }
  }

  public Result searchTitle() {
    Task task = new Task();
    task.setTitle(title);
    List<Task> foundTasks = list.findTaskByTitle(title);
    if (foundTasks.size() == 0) {
      return new Result(false, null, editMsgIfHaveOverdue(String.format(
          MSG_FAIL_BY_TITLE, title)), null);
    } else {
      return new Result(true, String.format(MSG_SUCCESS, title), null,
          foundTasks);
    }
  }
}
