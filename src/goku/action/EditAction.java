package goku.action;

import goku.DateRange;
import goku.GOKU;
import goku.Result;
import goku.Task;
import goku.TaskList;
import hirondelle.date4j.DateTime;

/*
 * Task is the core of GOKU. GOKU is designed to keep track of tasks, which are
 * analogous to real life tasks which the user wishes to note down.
 */
public class EditAction extends Action {
  public static final String ERR_INSUFFICIENT_ARGS = "Can't edit, need ID! Try \"edit 1 new title!\"";
  public static final String ERR_INSUFFICIENT_ARGS_FOR_COMPLETION = "Can't complete, need ID! Try \"do 1\"";
  private static final String MSG_HAS_OVERDUE = "[!] You have overdue tasks, \"view overdue\" to see them.";

  public static final String ERR_NO_ID_GIVEN = ERR_INSUFFICIENT_ARGS;

  private final String MSG_SUCCESS = "edited task %d";

  public int id;

  public String title;
  public DateTime dline;
  public DateRange period;
  public Boolean isComplete;
  public boolean removeDeadline;
  public boolean removePeriod;
  public boolean removeImportant;

  public EditAction(GOKU goku) {
    super(goku);
    // TODO Auto-generated constructor stub
  }

  public void addToUndoList() {
    TaskList currList = new TaskList();
    for (Task t : goku.getTaskList().getArrayList()) {
      currList.addTaskWithoutSettingId(t);
    }

    TaskList newCurrList = new TaskList();
    for (Task t : currList) {
      Task newT = new Task(t);
      newCurrList.addTaskWithoutSettingId(newT);
    }

    goku.getUndoList().offer(newCurrList);
  }

  @Override
  public Result doIt() {
    addToUndoList();
    if (removeDeadline) {
      doRemoveDeadline();
    } else if (removeImportant) {
      doRemoveImportant();
    } else if (removePeriod) {
      doRemovePeriod();
    }
    return updateTask();
  }

  private void doRemoveDeadline() {
    Task t = list.getTaskById(id);
    t.setDeadline(null);
  }

  private void doRemoveImportant() {
    Task t = list.getTaskById(id);
    t.setImpt(false);
  }

  private void doRemovePeriod() {
    Task t = list.getTaskById(id);
    t.setPeriod(null);
  }

  public String editMsgIfHaveOverdue(String msg) {
    if (list.getOverdue().size() != 0) {
      msg += System.lineSeparator() + MSG_HAS_OVERDUE;
    }
    return msg;
  }

  public Result updateTask() {
    Task taskWithEdits = new Task();
    taskWithEdits.setTitle(title);
    taskWithEdits.setDeadline(dline);
    taskWithEdits.setPeriod(period);
    taskWithEdits.setStatus(isComplete);
    Task t = list.getTaskById(id);

    // issue is is that when we are setting a new period
    // dealine is null, so the task has a deadline AND a period
    // and when displaying the deadline is checked first, so the task seems to
    // be not updated
    // but actually it just didn't remove the deadline
    t.updateWith(taskWithEdits);
    if (dline != null) {
      t.setDeadline(dline);
      t.setPeriod(null);
    } else if (period != null) {
      t.setPeriod(period);
      t.setDeadline(null);
    }

    return new Result(true,
        editMsgIfHaveOverdue(String.format(MSG_SUCCESS, id)), null,
        list.getAllIncomplete());
  }

}
