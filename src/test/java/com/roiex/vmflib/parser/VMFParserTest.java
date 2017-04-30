package com.roiex.vmflib.parser;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileNotFoundException;

import org.junit.jupiter.api.Test;

public class VMFParserTest {

	@Test
	public void testParsing() throws FileNotFoundException {
		VMFParser parser = new VMFParser();
		repeatTests(parser.parse(this.getClass().getResourceAsStream("/Small Test.vmf")).print(), parser);
		parser.parse(this.getClass().getResourceAsStream("/Large Test.vmf")).print();

	}

	private void repeatTests(String file, VMFParser parser) throws FileNotFoundException {
		for (int i = 0; i < 100; i++) {
			file = parser.parse(file).print();
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
