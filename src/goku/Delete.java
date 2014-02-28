package goku;

/*
 * Task is the core of GOKU. GOKU is designed to keep track of tasks, which are
 * analogous to real life tasks which the user wishes to note down.
 */
class Delete extends Action {
  private static final String SUCCESS_MSG = "deleted \"%s\"";
  private static final String FAILURE_MSG = "fail to delete \"%s\"";
  private Task task;

  /*
   * Called by ActionFactory on all actions to build the needed objects for this
   * Action
   */
  @Override
  public void construct() {
    this.task = command.getTask();
  }

  @Override
  Result doIt() {
    return deleteTask(command.getTask());
  }

  public Result deleteTask(Task task) {
    for (int i = 0; i < GOKU.getAllTasks().size(); i++) {
      if (GOKU.getAllTasks().get(i) == task) {
        GOKU.getAllTasks().remove(i);
      }
    }
    return new Result(true, getSuccessMsg(task.getTitle()), null,
        GOKU.getAllTasks());
  }

}