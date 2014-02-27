package goku.clui;

import goku.Command;
import goku.Command.SortOrder;
import goku.Command.Type;
import goku.Result;
import goku.Task;

/**
 * user interface that acts as the intermediate component being user interface and the main logic of GOKU 
 * @author jchiam
 */
public class CLUserInterface implements UserInterface{

	/*** GLOBAL VARIABLES ***/
	private Task toDo = null;
	private static Type commandType = null;
	private static SortOrder sortOrder = null;
	
	
	/**
	 * Method: getUserInput
	 * @param input string
	 * @return Command object
	 * Gets parser to modify necessary global variables then create Command object 
	 */
	public Command getUserInput(String input) {
		
		CLUIParser parser = new CLUIParser();
		
		// Parse, creates task modifies necessary Command object parameters
		toDo = parser.parseString(input);
		
		if(sortOrder == null) {
			return new Command(input, commandType, toDo);
		} else {
			return new Command(input, commandType, toDo, sortOrder);
		}
	}

	public String feedBack(Result result) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	/**
	 * Parser that deals with String input from user to extract necessary information to create Command object
	 * @author jchiam
	 */
	protected static class CLUIParser implements Parser{

		/**GLOBAL VARIABLES **/
		String restOfInput = new String();	// string that holds remaining unprocessed input
		
		/**
		 * Method: parseString
		 * @param input string to be parsed
		 * @return created task
		 */
		public Task parseString(String input) {
			
			String firstWord = splitCmdAndParam(input);
			
			// determine commandType
			commandType = determineCommandType(firstWord);
			
			//FIND STRING FOR SORTORDER
			sortOrder = determineSortOrder();
			
			return null;
		}

		/**
		 * Method: determineSortOrder
		 * @param input
		 */
		protected Command.SortOrder determineSortOrder() {
			
			Command.SortOrder sort = null;
			
			if(restOfInput.contains("sort:")) {
				
				// find out what sort it is
				int indexOfSortCmd = 0;
				if(sortFound("sort:EDF")) {
					sort = Command.SortOrder.EARLIEST_DEADLINE_FIRST;
					indexOfSortCmd = restOfInput.indexOf("sort:EDF");
					
					restOfInput = removeSortInput(indexOfSortCmd);
				}
				else if(sortFound("sort:HPF")) {
					sort = Command.SortOrder.HIGHEST_PRIORITY_FIRST;
					indexOfSortCmd = restOfInput.indexOf("sort:EDF");
					
					restOfInput = removeSortInput(indexOfSortCmd);
				}
				else {
					//TODO throw error message for invalid sort order
				}
			}
			
			return sort;
		}
		
		/**
		 * Method: sortFound
		 * @param sortString to search for
		 * @return true or false if precise sort string is found
		 */
		private boolean sortFound(String sortString) {
			
			// boolean variables to check for clearance on left and right ends of string
			boolean leftClear = false;
			boolean rightClear = false;
			
			if(restOfInput.contains(sortString)) {
				int indexOfSortFound = restOfInput.indexOf(sortString);
				
				// check left
				try {
					if(restOfInput.charAt(indexOfSortFound-1) == ' ') {
						leftClear = true;
					}
				} catch(IndexOutOfBoundsException e) {
					leftClear = true;
				}
				
				// check right
				try {
					if(restOfInput.charAt(indexOfSortFound+sortString.length()) == ' ') {
						rightClear = true;
					}
				} catch(IndexOutOfBoundsException e) {
					rightClear = true;
				}
			}
			
			return leftClear && rightClear;
		}

		/**
		 * Method: removeSortInput
		 * @param restOfInput
		 * @param indexOfSortCmd
		 * @return resultant string without sort input
		 */
		private String removeSortInput(int indexOfSortCmd) {

			String leftEnd = new String();
			String rightEnd = new String();
			
			try {
				leftEnd = restOfInput.substring(0, indexOfSortCmd-1);
			} catch(IndexOutOfBoundsException e) {
				leftEnd = "";
			}
			try {
				rightEnd = restOfInput.substring(indexOfSortCmd+9);
			} catch(IndexOutOfBoundsException e) {
				rightEnd = ""; 
			}
			
			return leftEnd.concat(rightEnd);
		}

		/**
		 * Method: splitCmdAndParam
		 * @param input string
		 * @return word containing command
		 */
		protected String splitCmdAndParam(String input) {
			String cmd = new String();
			
			try {
				int indexOfFirstSpace = input.indexOf(' ');
				
				restOfInput = input.substring(indexOfFirstSpace+1);
				cmd = input.substring(0, indexOfFirstSpace);
			} catch (IndexOutOfBoundsException e) {
				restOfInput = "";
				cmd = input;
			}
			
			return cmd;
		}

		/**
		 * Method: determineCommandType
		 * @param firstWord
		 */
		protected Command.Type determineCommandType(String firstWord) {
			
			Command.Type cmdType = null;
			
			switch(firstWord) {
			
				// Use Case: add
				case "add":
				case "a":
					cmdType =  Command.Type.ADD;
					break;
				// Use Case: view
				case "view":
					cmdType = Command.Type.DISPLAY;
					break;
				// Use Case: update
				case "update":
					cmdType = Command.Type.EDIT;
					break;
				// Use Case: delete
				case "delete":
					cmdType = Command.Type.DELETE;
					break;
				// Use Case: search
				case "search":
					cmdType = Command.Type.SEARCH;
					break;
				default:
					//TODO error handle invalid command
			}
			return cmdType;
		}
		
	}

}
