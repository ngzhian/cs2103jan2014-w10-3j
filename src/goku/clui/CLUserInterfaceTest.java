package goku.clui;

import static org.junit.Assert.*;
import goku.Command;
import goku.Task;
import goku.Command.SortOrder;
import goku.Command.Type;

import org.junit.Before;
import org.junit.Test;

public class CLUserInterfaceTest {
	
	/** GLOBAL TEST VARIABLES AND OBJECTS **/
	CLUserInterface.CLUIParser parser;
	
	@Before
	public void initParser() {
		parser = new CLUserInterface.CLUIParser();
		parser.restOfInput = new String();
	}
	
	@Test
	public void splitFirstWordTest() {
		
		String cmd = new String();
		
		// TEST CASE 1: check split for single word
		cmd = parser.splitCmdAndParam("add");
		assertEquals("add", cmd);
		assertEquals("", parser.restOfInput);
		
		// TEST CASE 2: check split for single word with space right after
		cmd = parser.splitCmdAndParam("add ");
		assertEquals("add", cmd);
		assertEquals("", parser.restOfInput);
		
		// TEST CASE 3: check split for single word with string right after
		cmd = parser.splitCmdAndParam("add tasks");
		assertEquals("add", cmd);
		assertEquals("tasks", parser.restOfInput);
		
	}
	
	@Test
	public void determineCommandTypeTest() {
		
		//TEST CASE 1: ADD
		assertEquals(Command.Type.ADD, parser.determineCommandType("add"));
		
		//TEST CASE 2: ADD (short form) - "a"
		assertEquals(Command.Type.ADD, parser.determineCommandType("a"));
		
		//TEST CASE 3: DISPLAY
		assertEquals(Command.Type.DISPLAY, parser.determineCommandType("view"));
		
		//TEST CASE 4: EDIT
		assertEquals(Command.Type.EDIT, parser.determineCommandType("update"));
		
		//TEST CASE 5: DELETE
		assertEquals(Command.Type.DELETE, parser.determineCommandType("delete"));
		
		//TEST CASE 6: SEARCH
		assertEquals(Command.Type.SEARCH, parser.determineCommandType("search"));
		
		
		
	}
	
	@Test
	public void determineSortType() {
		
		//TEST CASE 1: EDF sort
		parser.restOfInput = "sort:EDF";
		assertEquals(Command.SortOrder.EARLIEST_DEADLINE_FIRST, parser.determineSortOrder());
		
		//TEST CASE 2: HPF sort
		parser.restOfInput = "sort:HPF";
		assertEquals(Command.SortOrder.HIGHEST_PRIORITY_FIRST, parser.determineSortOrder());
		
		//TEST CASE 3: left end contains string
		parser.restOfInput = "task is... sort:EDF";
		assertEquals(Command.SortOrder.EARLIEST_DEADLINE_FIRST, parser.determineSortOrder());
		
		//TEST CASE 4: right end contains string
		parser.restOfInput = "sort:EDF testtest";
		assertEquals(Command.SortOrder.EARLIEST_DEADLINE_FIRST, parser.determineSortOrder());
		
		//TEST CASE 5: both ends contain string
		parser.restOfInput = "task is... sort:EDF testtest";
		assertEquals(Command.SortOrder.EARLIEST_DEADLINE_FIRST, parser.determineSortOrder());
		
		//TEST CASE 6: left end contains string (no spacing in between)
		parser.restOfInput = "task is...sort:EDF";
		assertEquals(null, parser.determineSortOrder());
		
		//TEST CASE 7: right end contains string (no spacing in between)
		parser.restOfInput = "sort:EDFtesttest";
		assertEquals(null, parser.determineSortOrder());
		
		//TEST CASE 8: both ends contain string (no spacing in between)
		parser.restOfInput = "task is...sort:EDFtesttest";
		assertEquals(null, parser.determineSortOrder());
		
		//TEST CASE 9: invalid sort order of correct length
		parser.restOfInput = "task is... sort:HMM testtest";
		assertEquals(null, parser.determineSortOrder());
		
		//TEST CASE 10: invalid sort order of wrong length
		parser.restOfInput = "task is... sort:OOPS testtest";
		assertEquals(null, parser.determineSortOrder());
	}
	
	@Test
	public void getInputTest() {
		
		//TEST CASE 1: determine command type with only command word - add
		//parser.parseString("add");
		//assertEquals(Command.Type.ADD, commandType); 
		
	}

}
