package ses.main.logic;

public class ValueFormat {

	public String name;

	public String type;

	public String value;

	public ValueFormat(String name, String type, String value) {
		this.name = name;
		this.type = type;
		this.value = value;
	}

	public String toString() {
		String s = "";

		s += "Name : " + name;
		s += "\tType : " + type;
		s += "\tValue : " + value;

		return s;
	}
}
