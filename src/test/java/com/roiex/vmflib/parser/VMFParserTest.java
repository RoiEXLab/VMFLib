package com.roiex.vmflib.parser;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileNotFoundException;

import org.junit.jupiter.api.Test;

public class VMFParserTest {

	@Test
	public void testReliability() throws FileNotFoundException {
		VMFParser parser = new VMFParser();
		String smallTest = parser.parse(this.getClass().getResourceAsStream("/Small Test.vmf")).print();
		String bigTest = parser.parse(this.getClass().getResourceAsStream("/Large Test.vmf")).print();

		for (int i = 0; i < 10; i++) {
			smallTest = parser.parse(smallTest).print();
			bigTest = parser.parse(bigTest).print();
		}
	}

	@Test
	public void testErrorThrowing() throws FileNotFoundException {
		VMFParser parser = new VMFParser();
		IllegalStateException isexception = assertThrows(IllegalStateException.class,
				() -> parser.parse(this.getClass().getResourceAsStream("/Unbalanced Brackets Test.vmf")));
		assertTrue(isexception.getMessage().contains("brackets"));

		IllegalArgumentException iaexception = assertThrows(IllegalArgumentException.class,
				() -> parser.parse(this.getClass().getResourceAsStream("/Unbalanced Quotes Test.vmf")));
		assertTrue(iaexception.getMessage().contains("quotes"));
	}

}
