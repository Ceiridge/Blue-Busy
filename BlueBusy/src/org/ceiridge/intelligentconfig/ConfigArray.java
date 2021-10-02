package org.ceiridge.intelligentconfig;

import java.util.ArrayList;

public class ConfigArray {
	private String name;
	private ArrayList<Object> vars;
	private ConfigContainer parentContainer;
	private Class<?> clazz;

	public ConfigArray(String name, ConfigContainer parent, Class<?> clazz) throws EmptyNameException {
		if (name.equals("") || name == null) {
			throw new EmptyNameException();
		}
		this.name = name.replace("\n", "");
		this.vars = new ArrayList<Object>();
		this.parentContainer = parent;
		this.clazz = clazz;
	}

	public String getName() {
		return name;
	}
	
	public Class<?> getTypeClass() {
		return clazz;
	}

	public ConfigContainer getParent() throws NullPointerException {
		return parentContainer;
	}

	public ArrayList<Object> getVars() {
		return vars;
	}

	public String getSaveCode() {
		String saveCode = "BEGINARRAY " + clazz.getSimpleName().toUpperCase() + " " + this.getName() + "\n";

		for (Object var : vars) {
			if(!var.getClass().equals(clazz))
				continue;
			
			if (var instanceof String) {
				String superVar = (String) var;
				superVar = superVar.replace("\n", "{NEWL}");
				var = superVar;
			}

			saveCode += "!ARRVALUE " + var + "\n";
		}

		return saveCode + "\nENDARRAY\n";
	}
	
	public Object get(int index) {
		return this.vars.get(index);
	}
	
	public Object remove(int index) {
		return this.vars.remove(index);
	}
	
	public boolean remove(Object obj) {
		return this.vars.remove(obj);
	}
	
	public void add(Object obj) {
		this.vars.add(obj);
	}
}
