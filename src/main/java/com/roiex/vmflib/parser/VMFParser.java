package com.roiex.vmflib.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.roiex.vmflib.types.VMFClass;
import com.roiex.vmflib.types.VMFRoot;

public class VMFParser {

	private int lineCount = 0;
	private StringBuilder lastStrings = new StringBuilder();
	private Scanner scanner;
	private Deque<VMFClass> stack = new LinkedList<>();

	private synchronized VMFRoot parse(Scanner scanner) throws FileNotFoundException {
		try {
			this.scanner = scanner;
			VMFRoot root = new VMFRoot();
			stack.addLast(root);
			parse();
			return (VMFRoot) stack.removeFirst();
		} finally {
			lineCount = -1;
			scanner.close();
			lastStrings.setLength(0);
			stack.clear();// Shouldn't be needed
		}
	}

	public VMFRoot parse(File file) throws FileNotFoundException {
		return parse(new Scanner(file));
	}

	public VMFRoot parse(InputStream stream) throws FileNotFoundException {
		return parse(new Scanner(stream));
	}

	public VMFRoot parse(String string) throws FileNotFoundException {
		return parse(new Scanner(string));
	}

	private void parse() {
		whileLoop: while (scanner.hasNextLine()) {
			String currentLine = scanner.nextLine();
			lineCount++;
			for (int linePos = 0; linePos < currentLine.length(); linePos++) {
				char currentChar = currentLine.charAt(linePos);
				if (currentChar == '{') {
					Pattern pattern = Pattern.compile("(?=[\\s\\n]*)([^\\s\\n\"]+)(?=[\\s\\n]*$)");
					String lastString = lastStrings.toString();
					Matcher matcher = pattern.matcher(lastString);
					if (matcher.find()) {
						VMFClass vmfClass = new VMFClass(matcher.group(1));
						applyProperties(lastString.substring(0, matcher.start()));
						stack.getLast().append(vmfClass);
						stack.addLast(vmfClass);
					}
					lastStrings.setLength(0);
				} else if (currentChar == '}') {
					applyProperties(lastStrings.toString());
					lastStrings.setLength(0);
					if (stack.size() <= 1) {
						throw new IllegalStateException("Unbalanced brackets in File");
					}
					stack.removeLast();
				} else if (currentChar == '/' && currentLine.length() > linePos + 1
						&& currentLine.charAt(linePos + 1) == '/') {
					continue whileLoop;
				} else {
					lastStrings.append(currentChar);
				}
			}
			lastStrings.append('\n');
		}
	}

	private void applyProperties(String remainingString) {
		checkValidity(remainingString);
		remainingString = remainingString.trim();
		Pattern pattern = Pattern.compile("\\s*\"(((?!\").)*)\"\\s*\"(((?!\").)*)\"\\s*");
		Matcher matcher = pattern.matcher(remainingString);
		while (matcher.find()) {
			stack.getLast().append(matcher.group(1), matcher.group(3));
		}
	}

	private void checkValidity(String string) {
		String[] lines = string.split("\n");
		int quoteCount = 0;
		for (int i = 0; i < lines.length; i++) {
			for (int y = 0; y < lines[i].length(); y++) {
				if (lines[i].charAt(y) == '"') {
					quoteCount++;
				}
			}
			if (quoteCount < 4 && quoteCount > 0) {
				throw new IllegalArgumentException("Insufficient quotes in line " + ((lineCount - lines.length) + i)
						+ "\nAt least 4 double quotes are required!\nFound " + quoteCount);
			}
			if (quoteCount % 2 != 0) {
				throw new IllegalArgumentException("Unbalanced quotes in line " + ((lineCount - lines.length) + i));
			}
		}
	}
}
