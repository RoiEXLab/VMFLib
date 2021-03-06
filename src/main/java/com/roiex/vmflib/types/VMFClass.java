package com.roiex.vmflib.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class VMFClass {

	protected Map<String, String> properties = new HashMap<>();
	protected List<VMFClass> subclasses = new ArrayList<>();
	private String name;
	private VMFClass parent;

	public VMFClass(String name) {
		this.setName(name);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		String trimmedName = name.trim();
		if (trimmedName.matches("[^\\u0000]*[\"\\n\\s{}][^\\u0000]*")) {
			throw new IllegalArgumentException("Class Name '" + name + "' contains Illegal Characters!");
		}
		this.name = trimmedName;
	}

	public VMFClass getParent() {
		return parent;
	}

	public void append(VMFClass subclass) {
		subclass.parent = this;
		subclasses.add(subclass);
	}

	public boolean remove(VMFClass subclass) {
		return subclasses.remove(subclass);
	}

	public boolean removeClassByName(String name) {
		VMFClass toRemove = null;
		for (VMFClass vmfClass : subclasses) {
			if (vmfClass.getName().equalsIgnoreCase(name)) {
				toRemove = vmfClass;
				break;
			}
		}
		if (toRemove != null) {
			return remove(toRemove);
		}
		throw new IllegalArgumentException("This class isn't existing");
	}

	public void append(String key, String value) {
		properties.put(key, value);
	}

	public void remove(String name) {
		properties.remove(name);
	}

	public String print() {
		StringBuilder result = new StringBuilder();
		result.append(name).append('\n').append('{');
		if (properties.size() > 0 || subclasses.size() > 0) {
			result.append('\n');
		}
		Iterator<Entry<String, String>> iterator = properties.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, String> property = iterator.next();
			result.append('\u0009');
			result.append("\"" + property.getKey() + "\" \"" + property.getValue() + "\"");
			if (iterator.hasNext() || subclasses.size() > 0) {
				result.append('\n');
			}
		}
		StringBuilder classes = new StringBuilder();
		for (VMFClass subclass : subclasses) {
			classes.append(subclass.print());
		}
		for (String line : classes.toString().split("\n")) {
			result.append('\u0009').append(line).append('\n');
		}
		result.append('}').append('\n');
		return result.toString();
	}
}
