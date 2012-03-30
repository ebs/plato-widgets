package ses.driver;

public class ResultFormat {

	public String name;

	public String type;

	public String id;

	public String code;

	public ResultFormat(String name, String type, String id, String code) {
		this.name = name;
		this.type = type;
		this.id = id;
		this.code = code;
	}

	/* public String toString() {
	     String s = "";

	     s += "Name : " + name;
	     s += "\tType : " + type;
	     s += "\tValue : " + value;

	     return s;
	 }*/
}
