package com.roiex.vmflib.types;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class VMFRoot extends VMFClass {

	public VMFRoot() {
		super("");
	}

	public void printToFile(File file) {
		try (PrintWriter writer = new PrintWriter(file);) {
			for (VMFClass vmfClass : subclasses) {
				writer.write(vmfClass.print());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void append(String key, String value) {
		throw new IllegalStateException("A VMFRoot cannot have properties");
	}
}
