package rs.milanmitic.master.common.protector;

public class HiddenFieldBuilder {

	private StringBuilder sb = null;

	private HiddenFieldBuilder() {
		sb = new StringBuilder();
	}

	private HiddenFieldBuilder(Object s) {
		sb = new StringBuilder();
		add(s);
	}

	public HiddenFieldBuilder add(Object o) {
		sb.append(o != null ? o.toString() : "<null>");
		return this;
	}

	public HiddenFieldBuilder addDate(java.util.Date o) {
		return add(o != null ? new java.sql.Date(o.getTime()) : o);
	}

	public static HiddenFieldBuilder create() {
		return new HiddenFieldBuilder();
	}

	public static HiddenFieldBuilder create(Object s) {
		return new HiddenFieldBuilder(s);
	}

	@Override
	public String toString() {
		return sb.toString();
	}
}
