package ses.main.logic;

import java.beans.ConstructorProperties;
import java.util.Date;

public class ValueFormat {

	private String name;

	private String type;

	public String value;

	public Date timestamp;

	@ConstructorProperties({"name", "type", "value", "timestamp"})
	public ValueFormat(String name, String type, String value, Date timestamp) {
		this.name = name;
		this.type = type;
		this.value = value;
		this.timestamp = timestamp;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String toString() {
		String s = "";

		s += "Name : " + name;
		s += "\tType : " + type;
		s += "\tValue : " + value;

		return s;
	}
}
