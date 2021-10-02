package org.ceiridge.intelligentconfig;

public enum ConfigVariableType {
	STRING(String.class), INTEGER(Integer.class), BOOLEAN(Boolean.class), FLOAT(Float.class), DOUBLE(Double.class), LONG(Long.class), BYTE(
			Byte.class);

	private Class<?> typeClass;

	public Class<?> getTypeClass() {
		return typeClass;
	}

	ConfigVariableType(Class<?> typeClass) {
		this.typeClass = typeClass;
	}
}
