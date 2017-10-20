package org.tdl.vireo.model;

public class DefaultConfiguration implements Configuration {
	private String name;
	private String value;
	private String type;
	
	public DefaultConfiguration(String name, String value, String type) {
		this.name = name;
		this.value = value;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	public String getType() {
		return type;
	}

}
