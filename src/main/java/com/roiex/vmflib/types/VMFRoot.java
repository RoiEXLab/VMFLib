package com.roiex.vmflib.types;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.function.Consumer;

public class VMFRoot extends VMFClass {

	public VMFRoot() {
		super("");
	}

	public void print(File file) {
		try (PrintWriter writer = new PrintWriter(file)) {
			print(writer::write);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void print(Consumer<String> consumer) {
		for (VMFClass vmfClass : subclasses) {
			consumer.accept(vmfClass.print());
		}
	}

	public void print(OutputStream stream) {
		try (PrintWriter writer = new PrintWriter(stream)) {
			print(writer::write);
		}
	}

	@Override
	public String print() {
		StringBuilder builder = new StringBuilder();
		print(builder::append);
		return builder.toString();
	}

	@Override
	public void append(String key, String value) {
		throw new IllegalStateException("A VMFRoot cannot have properties");
	}
}
