package goku.ui;

import goku.GOKU;
import goku.Result;
import goku.action.Action;
import goku.action.MakeActionException;

/*
 * UnknownAction represents some action which GOKU does not know of.
 * This is likely to be a Command that is not understood by GOKU,
 * such as random gibberish.
 */
public class UnknownAction extends Action {
  public static final String ERR_UNKNOWN_ACTION = "Command \" %s \" is unknown";
  public String command;

  public UnknownAction(GOKU goku, String command) {
    super(goku);
    this.command = command;
  }

  @Override
  public Result doIt() throws MakeActionException {
    System.out.println(command);
    return new Result(false, null, String.format(ERR_UNKNOWN_ACTION, command),
        null);
  }

}