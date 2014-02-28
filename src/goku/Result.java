package goku;

import java.util.ArrayList;

public class Result {
  private static final String MSG_DEFAULT_SUCCESS = "Success!";
  private static final String MSG_DEFAULT_FAILURE = "Failed.";
  private boolean isSuccess;
  private String successMsg;
  private String errorMsg;
  private ArrayList<Task> tasks;

  public Result(boolean isSuccess, String successMsg, String errorMsg,
      ArrayList<Task> tasks) {
    this.isSuccess = isSuccess;
    this.setSuccessMsg(successMsg);
    this.setErrorMsg(errorMsg);
    this.setTasks(tasks);
  }

  public static Result makeSuccessResult() {
    return new Result(true, MSG_DEFAULT_SUCCESS, null, null);
  }

  public static Result makeFailureResult() {
    return new Result(false, null, MSG_DEFAULT_FAILURE, null);
  }

  public String getErrorMsg() {
    return errorMsg;
  }

  public String getSuccessMsg() {
    return successMsg;
  }

  public ArrayList<Task> getTasks() {
    return tasks;
  }

  public boolean isSuccess() {
    return isSuccess;
  }

  public void setErrorMsg(String errorMsg) {
    this.errorMsg = errorMsg;
  }

  public void setSuccess(boolean isSuccess) {
    this.isSuccess = isSuccess;
  }

  public void setSuccessMsg(String successMsg) {
    this.successMsg = successMsg;
  }

  public void setTasks(ArrayList<Task> tasks) {
    this.tasks = tasks;
  }
}
