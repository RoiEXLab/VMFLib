package com.roiex.vmflib.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.roiex.vmflib.types.VMFClass;
import com.roiex.vmflib.types.VMFRoot;

public class VMFParser {

	public static void main(String[] args) throws FileNotFoundException {
		new VMFParser().parseFile(new File("C:\\Users\\Robin\\Desktop\\unnamed.vmf"))
				.printToFile(new File("C:\\Users\\Robin\\Desktop\\copy.vmf"));
	}

	private int lineCount = -1;

	public VMFRoot parseFile(File file) throws FileNotFoundException {
		try (Scanner scanner = new Scanner(new FileInputStream(file))) {
			return addSubEntities(new VMFRoot(), scanner, "");
		} finally {
			lineCount = -1;
		}
	}

	private <T extends VMFClass> T addSubEntities(T parent, Scanner scanner, String preLine) {
		try {
			StringBuilder builder = new StringBuilder();
			whileLoop:
			while (scanner.hasNextLine()) {
				lineCount++;
				String line = !preLine.isEmpty() ? preLine : scanner.nextLine();
				preLine = "";
				for (int i = 0; i < line.length(); i++) {
					char c = line.charAt(i);
					if (Pattern.matches("[A-Za-z]", String.valueOf(c))) {
						builder.append(c);
					} else if (c == '{') {
						parent.append(addSubEntities(new VMFClass(builder.toString()), scanner, line.substring(i + 1)));
						builder.setLength(0);
						continue whileLoop;
					} else if (c == '}') {
						applyProperty(parent, line.substring(0, i));
						return parent;
					}
				}
				applyProperty(parent, line);
			}
		} catch (Exception e) {
			throw new IllegalArgumentException("Error while parsing VMF file at Line " + lineCount, e);
		}
		return parent;
	}

	private boolean applyProperty(VMFClass parent, String line) {
		Pattern pattern = Pattern.compile("\\s*\"(((?!\").)*)\"\\s*\"(((?!\").)*)\"\\s*");
		Matcher matcher = pattern.matcher(line);
		if (matcher.find()) {
			parent.append(matcher.group(1), matcher.group(3));
			return true;
		}
		return false;
	}
}
