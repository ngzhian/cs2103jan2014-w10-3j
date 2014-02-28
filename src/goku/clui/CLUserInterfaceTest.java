package goku.clui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import goku.Command;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CLUserInterfaceTest {

	/** GLOBAL TEST VARIABLES AND OBJECTS **/
	CLUserInterface.CLUIParser parser;
	CLUserInterface ui;
	String[] tags;
	ByteArrayOutputStream outContent;

	@Before
	public void initObjects() {
		parser = new CLUserInterface().getParser();
		ui = new CLUserInterface();
		parser.restOfInput = new String();
		tags = new String[10];
	}
	
	@Before
	public void setOutputStreams() {
		outContent = new ByteArrayOutputStream();
		System.setOut(new PrintStream(outContent));
	}
	
	@After
	public void closeStreams() throws IOException {
		outContent.close();
	}

	/*------------------------------------------/
	/********** splitCmdAndParam Tests **********/
	/*-----------------------------------------*/
	
	@Test
	public void splitFirstWord_WithOneWord() {
		String cmd = new String();
		
		cmd = parser.splitCmdAndParam("add");
		assertEquals("add", cmd);
		assertEquals("", parser.restOfInput);
	}
	
	@Test
	public void splitFirstWord_WithOneWordAndSpace() {
		String cmd = new String();
		
		cmd = parser.splitCmdAndParam("add ");
		assertEquals("add", cmd);
		assertEquals("", parser.restOfInput);
	}
	
	@Test
	public void splitFirstWord_WithNormalString() {
		String cmd = new String();
		
		cmd = parser.splitCmdAndParam("add tasks");
		assertEquals("add", cmd);
		assertEquals("tasks", parser.restOfInput);		
	}
	
	/*----------------------------------------------*/
	/********** determineCommandType Tests **********/
	/*----------------------------------------------*/
	
	@Test
	public void determineAddCommand() {
		assertEquals(Command.Type.ADD, parser.determineCommandType("add"));
	}
	
	@Test
	public void determineAddCommand_a_shortcut() {
		assertEquals(Command.Type.ADD, parser.determineCommandType("a"));
	}
	
	@Test
	public void determineDisplayCommand() {
		assertEquals(Command.Type.DISPLAY, parser.determineCommandType("view"));
	}
	
	@Test
	public void determineEditCommand() {
		assertEquals(Command.Type.EDIT, parser.determineCommandType("update"));
	}
	
	@Test
	public void determineDeleteCommand() {
		assertEquals(Command.Type.DELETE, parser.determineCommandType("delete"));
	}
	
	@Test
	public void determineSearchCommand() {
		assertEquals(Command.Type.SEARCH, parser.determineCommandType("search"));
	}
	
	@Test
	public void determineCommand_InvalidCommand() throws IOException {
		assertEquals(null, parser.determineCommandType("gibberish"));
		assertEquals(CLUserInterface.INPUT_ERROR+'\n', outContent.toString());
	}

	/*--------------------------------------------*/
	/********** determineSortOrder Tests **********/
	/*--------------------------------------------*/
	
	@Test
	public void determineSortOrder_EDF() {
		parser.restOfInput = "sort:EDF";
		assertEquals(Command.SortOrder.EARLIEST_DEADLINE_FIRST,	parser.determineSortOrder());
	}
	
	@Test
	public void determineSortOrder_HPF() {
		parser.restOfInput = "sort:HPF";
		assertEquals(Command.SortOrder.HIGHEST_PRIORITY_FIRST, parser.determineSortOrder());
	}
	
	@Test
	public void determineSortOrder_WithStringLeftEnd() {
		parser.restOfInput = "task is... sort:EDF";
		assertEquals(Command.SortOrder.EARLIEST_DEADLINE_FIRST,	parser.determineSortOrder());
	}
	
	@Test
	public void determineSortOrder_WithStringRightEnd() {
		parser.restOfInput = "sort:EDF testtest";
		assertEquals(Command.SortOrder.EARLIEST_DEADLINE_FIRST,	parser.determineSortOrder());
	}
	
	@Test
	public void determineSortOrder_WithStringBothEnds() {
		parser.restOfInput = "task is... sort:EDF testtest";
		assertEquals(Command.SortOrder.EARLIEST_DEADLINE_FIRST,	parser.determineSortOrder());
	}
	
	@Test
	public void determineSortOrder_WithStringLeftEndNoSpace() {
		parser.restOfInput = "task is...sort:EDF";
		assertEquals(null, parser.determineSortOrder());
	}
	
	@Test
	public void determineSortOrder_WithStringRightEndNoSpace() {
		parser.restOfInput = "sort:EDFtesttest";
		assertEquals(null, parser.determineSortOrder());
	}
	
	@Test
	public void determineSortOrder_WithStringBothEndsNoSpace() {
		parser.restOfInput = "task is...sort:EDFtesttest";
		assertEquals(null, parser.determineSortOrder());
	}
	
	@Test
	public void determineSortOrder_InvalidWithCorrectLength() {
		parser.restOfInput = "task is... sort:HMM testtest";
		assertEquals(null, parser.determineSortOrder());
	}
	
	@Test
	public void determineSortOrder_EmptySortInput() {
		parser.restOfInput = "sort:";
		parser.determineSortOrder();
		assertEquals(CLUserInterface.SORT_ERROR+'\n', outContent.toString());
	}
	
	@Test
	public void determineSortOrder_InvalidWithIncorrectLength() {
		parser.restOfInput = "task is... sort:OOPS testtest";
		parser.determineSortOrder();
		assertEquals(CLUserInterface.SORT_ERROR+'\n', outContent.toString());
	}

	/*------------------------------------------*/
	/********** reconstructInput Tests **********/
	/*------------------------------------------*/
	
	@Test
	public void reconstructInput_EmptyTokens() {
		String[] tokens = {"", "", ""};
		parser.reconstructInput(tokens);
		assertEquals("", parser.restOfInput);
	}
	
	@Test
	public void reconstructInput_Only1Token() {
		String[] tokens = {"", "test", ""};
		parser.reconstructInput(tokens);
		assertEquals("test", parser.restOfInput);
	}
	
	public void reconstructInput_MultipleTokens() {
		String[] tokens = {"this", "is", "the", "test"};
		parser.reconstructInput(tokens);
		assertEquals("this is the test", parser.restOfInput);
		
	}
	
	/*-------------------------------------*/
	/********** extractTags Tests **********/
	/*-------------------------------------*/
	
	@Test
	public void extractTags_NoTags() {
		tags = new String[10];
		parser.restOfInput = "add task";
		assertArrayEquals(tags, parser.extractTags());
		assertEquals("add task", parser.restOfInput);
	}
	
	@Test
	public void extractTags_OneTag() {
		tags = new String[10];
		parser.restOfInput = "add task #homework";
		tags[0] = "homework";
		assertArrayEquals(tags, parser.extractTags());
		assertEquals("add task", parser.restOfInput);
	}
	
	@Test
	public void extractTags_TwoTags() {
		tags = new String[10];
		parser.restOfInput = "add task #homework #urgent";
		tags[0] = "homework";
		tags[1] = "urgent";
		assertArrayEquals(tags, parser.extractTags());
		assertEquals("add task", parser.restOfInput);
	}
	
	@Test
	public void extractTags_MultipleTags() {
		tags = new String[10];
		parser.restOfInput = "add task #homework #urgent #busy";
		tags[0] = "homework";
		tags[1] = "urgent";
		tags[2] = "busy";
		assertArrayEquals(tags, parser.extractTags());
		assertEquals("add task", parser.restOfInput);
	}
	
	@Test
	public void extractTags_NoTagDetectedDueToNoSpacing() {
		tags = new String[10];
		parser.restOfInput = "add task#homework";
		assertArrayEquals(tags, parser.extractTags());
		assertEquals("add task#homework", parser.restOfInput);
	}
	
	@Test
	public void extractTags_MixtureOfGoodAndBadTagsDueToSpacing() {
		tags = new String[10];
		parser.restOfInput = "add task#homework #urgent";
		tags[0] = "urgent";
		assertArrayEquals(tags, parser.extractTags());
		assertEquals("add task#homework", parser.restOfInput);
	}
	
	@Test
	public void extractTags_ExceedLimitOfTenTags() {
		tags = new String[10];
		parser.restOfInput = "add task #homework #homework #homework "
				+ "#homework #homework #homework #homework #homework "
				+ "#homework #homework #homework";
		for(int i=0; i<10; i++) {
			tags[i] = "homework";
		}
		assertArrayEquals(tags, parser.extractTags());
		assertEquals("add task #homework", parser.restOfInput);
		
	}
	
	/*-------------------------------------*/
	/********** makeCommand Tests **********/
	/*-------------------------------------*/
	
	@Test
	public void getInputTest() throws IOException {

		Command test = null;

		// TEST CASE 1: only command word
		test = ui.makeCommand("add");
		assertEquals("add", test.getSource());
		assertEquals(Command.Type.ADD, test.getType());
		assertEquals(Command.SortOrder.EARLIEST_DEADLINE_FIRST,
				test.getSortOrder());

		// TEST CASE 2: command word and task description
		test = ui.makeCommand("add task");
		assertEquals("add task", test.getSource());
		assertEquals(Command.Type.ADD, test.getType());
		assertEquals(Command.SortOrder.EARLIEST_DEADLINE_FIRST,
				test.getSortOrder());

		// TEST CASE 3: command word, task description and sort order:EDF
		test = ui.makeCommand("add task sort:EDF");
		assertEquals("add task sort:EDF", test.getSource());
		assertEquals(Command.Type.ADD, test.getType());
		assertEquals(Command.SortOrder.EARLIEST_DEADLINE_FIRST,
				test.getSortOrder());

		// TEST CASE 4: command word, task description and sort order:HPF
		test = ui.makeCommand("delete task sort:HPF");
		assertEquals("delete task sort:HPF", test.getSource());
		assertEquals(Command.Type.DELETE, test.getType());
		assertEquals(Command.SortOrder.HIGHEST_PRIORITY_FIRST,
				test.getSortOrder());

		// TEST CASE 5: command word, task description and invalid sort order
		test = ui.makeCommand("add tasksort:EDF");
		assertEquals("add tasksort:EDF", test.getSource());
		assertEquals(Command.Type.ADD, test.getType());
		assertEquals(Command.SortOrder.EARLIEST_DEADLINE_FIRST,
				test.getSortOrder());
		
		// TEST CASE 6: command word, task description and non existent sort order
		test = ui.makeCommand("add task sort:ABC");
		System.setErr(new PrintStream(outContent));
		assertEquals(CLUserInterface.SORT_ERROR+'\n', outContent.toString());
		outContent.flush();

		// TEST CASE 7: invalid input
		test = ui.makeCommand("addtasksort:EDF");
		assertEquals("addtasksort:EDF", test.getSource());
		assertEquals(Command.SortOrder.EARLIEST_DEADLINE_FIRST,
				test.getSortOrder());
		assertEquals(CLUserInterface.INPUT_ERROR+'\n', outContent.toString());
		outContent.flush();

	}

}
