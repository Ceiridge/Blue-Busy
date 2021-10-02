package org.ceiridge.intelligentconfig;

import java.util.ArrayList;
import java.util.HashMap;

public class ConfigContainer {

	private String name;
	private HashMap<String, Object> vars;
	private ArrayList<ConfigContainer> containers;
	private ArrayList<ConfigArray> arrays;
	private ConfigContainer parentContainer;

	public ConfigContainer(String name, ConfigContainer parent) throws EmptyNameException {
		if (name.equals("") || name == null) {
			throw new EmptyNameException();
		}
		this.name = name.replace("\n", "");
		this.vars = new HashMap<String, Object>();
		this.containers = new ArrayList<ConfigContainer>();
		this.arrays = new ArrayList<ConfigArray>();
		this.parentContainer = parent;
	}

	public ConfigContainer(String name) throws EmptyNameException {
		this(name, null);
	}

	public String getName() {
		return name;
	}

	public ConfigContainer getParent() throws NullPointerException {
		return parentContainer;
	}

	public HashMap<String, Object> getVarMap() {
		return vars;
	}

	public ArrayList<ConfigContainer> getContainerList() {
		return containers;
	}

	public ArrayList<ConfigArray> getArrayList() {
		return arrays;
	}

	public String getSaveCode() {
		String saveCode = "BEGINCONTAINER " + this.getName() + "\n";

		for (String varKey : vars.keySet()) {
			Object var = vars.get(varKey);

			String beginValueCode = "!VALUE ";

			if (var instanceof String) {
				String superVar = (String) var;
				superVar = superVar.replace("\n", "{NEWL}");
				var = superVar;
				beginValueCode = "!STRVALUE ";
			} else

			if (var instanceof Integer) {
				beginValueCode = "!INTVALUE ";
			} else

			if (var instanceof Boolean) {
				beginValueCode = "!BOLVALUE ";
			} else

			if (var instanceof Float) {
				beginValueCode = "!FLOVALUE ";
			} else

			if (var instanceof Double) {
				beginValueCode = "!DOUVALUE ";
			} else

			if (var instanceof Long) {
				beginValueCode = "!LONVALUE ";
			} else

			if (var instanceof Byte) {
				beginValueCode = "!BYTEVALUE ";
			}


			if (!beginValueCode.equals("!VALUE "))
				saveCode += beginValueCode + varKey.replace("\n", "").replace(" ", "_") + " " + var + "\n";
		}

		for (ConfigContainer cont : containers) {
			saveCode += cont.getSaveCode();
		}

		for (ConfigArray array : arrays) {
			saveCode += array.getSaveCode();
		}

		return saveCode + "\nENDCONTAINER\n";
	}

	public ConfigContainer getContainer(String containerName) throws EmptyNameException {
		for (ConfigContainer cont : containers) {
			if (cont.getName().trim().equalsIgnoreCase(containerName.trim()))
				return cont;
		}
		ConfigContainer cc = new ConfigContainer(containerName, this);
		containers.add(cc);
		return cc;
	}

	public ConfigArray getArray(String arrayName) throws EmptyNameException {
		for (ConfigArray cont : arrays) {
			if (cont.getName().trim().equalsIgnoreCase(arrayName.trim()))
				return cont;
		}
		return null;
	}

	// Gets array if existing
	public ConfigArray createArray(String arrayName, ConfigVariableType varType) throws EmptyNameException {
		ConfigArray ca = getArray(arrayName);
		if (ca != null)
			return ca;
		ConfigArray cc = new ConfigArray(arrayName, this, varType.getTypeClass());
		arrays.add(cc);
		return cc;
	}

	public void deleteArray(String arrayName) throws EmptyNameException {
		arrays.remove(getArray(arrayName));
	}

	public boolean doesValueExist(String name) {
		for (String varsKeys : vars.keySet()) {
			if (varsKeys.trim().equalsIgnoreCase(name.trim().replace("_", " ")))
				return true;
		}
		return false;
	}

	public void deleteValue(String name) {
		if (!doesValueExist(name))
			return;
		vars.remove(name);
	}

	private Object getObject(String name) {
		for (String varsKeys : vars.keySet()) {
			if (varsKeys.trim().equalsIgnoreCase(name.trim().replace("_", " ")))
				return vars.get(varsKeys);
		}
		return null;
	}

	// DO NOT MAKE THIS PUBLIC, ONLY INTEGERS, STRINGS, FLOATS, DOUBLES, BOOLEANS, LONGS, BYTES
	// ALLOWED
	private void setObject(String name, Object obj) throws EmptyNameException {
		if (name.equals("") || name == null)
			throw new EmptyNameException();
		vars.put(name.replace("_", " "), obj);
	}


	//
	public int getInteger(String name) throws ClassCastException, NullPointerException {
		return (int) getObject(name);
	}

	public void setInteger(String name, int value) throws EmptyNameException {
		setObject(name, value);
	}

	public String getString(String name) throws ClassCastException, NullPointerException {
		return (String) getObject(name);
	}

	public void setString(String name, String value) throws EmptyNameException {
		setObject(name, value);
	}

	public float getFloat(String name) throws ClassCastException, NullPointerException {
		return (float) getObject(name);
	}

	public void setFloat(String name, float value) throws EmptyNameException {
		setObject(name, value);
	}

	public double getDouble(String name) throws ClassCastException, NullPointerException {
		return (double) getObject(name);
	}

	public void setDouble(String name, double value) throws EmptyNameException {
		setObject(name, value);
	}

	public boolean getBool(String name) throws ClassCastException, NullPointerException {
		return (boolean) getObject(name);
	}

	public void setBool(String name, boolean value) throws EmptyNameException {
		setObject(name, value);
	}

	public long getLong(String name) throws ClassCastException, NullPointerException {
		return (long) getObject(name);
	}

	public void setLong(String name, long value) throws EmptyNameException {
		setObject(name, value);
	}

	public byte getByte(String name) throws ClassCastException, NullPointerException {
		return (byte) getObject(name);
	}

	public void setByte(String name, byte value) throws EmptyNameException {
		setObject(name, value);
	}

}
