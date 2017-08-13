package test;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Generisanje JPA klasa na osnovu tabele
 * 
 * @author milan
 *
 */
public class JPA {

	public static void main(String[] args) {
		doIt();

	}

	// gde ce biti napravljena klasa fizicki
	private static String path = "C:/workspace-IPG/Master/src/rs/milanmitic/master/model/";
	private static boolean saveToFile = true;

	private static String cmsURL = "jdbc:mysql://localhost/master";
	private static String cmsUser = "root";
	private static String cmsPswd = "root";

	private static String sema = "";
	private static String table = "PASS_HISTORY";

	private static String className = StringUtils.capitalize(getColName(table));

	public static void doIt() {
		PreparedStatement s = null;
		ResultSet r = null;
		Connection conn = null;
		StringBuilder sbget = new StringBuilder();
		StringBuilder sbhash = new StringBuilder();
		StringBuilder sbeq = new StringBuilder();
		sbhash.append("/** ");
		sbhash.append("* {@inheritDoc} ");
		sbhash.append("*/");
		sbhash.append("@Override\n");
		sbhash.append("public int hashCode() {\n");
		sbhash.append("return new HashCodeBuilder()");

		sbeq.append("/** ");
		sbeq.append("* {@inheritDoc} ");
		sbeq.append("*/");
		sbeq.append("@Override\n");
		sbeq.append("public boolean equals(final Object other) {\n");
		sbeq.append("if (this == other)\n");
		sbeq.append("return true;\n");
		sbeq.append("if (other == null)\n");
		sbeq.append("return false;\n");
		sbeq.append("if (!(other instanceof " + className + "))\n");
		sbeq.append("return false;\n");
		sbeq.append(className + " castOther = (" + className + ") other;\n");
		sbeq.append("return new EqualsBuilder()");

		StringBuilder sb = new StringBuilder(addDefault());
		sb.append("public class " + className + " extends SearchInput implements Serializable, HiddenFieldsSecureInterface {\n\n");
		sb.append("private static final long serialVersionUID = 1L;\n\n");

		boolean hasparticipantFk = false;
		boolean hasversion = false;
		try {

			conn = getConnection();
			String komandaSelect = "select * FROM " + sema + table;
			s = conn.prepareStatement(komandaSelect);
			r = s.executeQuery();

			int n = r.getMetaData().getColumnCount();
			for (int i = 1; i <= n; i++) {
				String col = r.getMetaData().getColumnName(i);
				int precisiion = r.getMetaData().getPrecision(i);
				int colTypeI = r.getMetaData().getColumnType(i);
				int scale = r.getMetaData().getScale(i);
				String colType = getColType(colTypeI);
				String colN = getColName(col);
				String colnUpper = StringUtils.capitalize(colN);

				if (!hasparticipantFk) {
					if ("participantFk".equalsIgnoreCase(colN))
						hasparticipantFk = true;
				}
				if (!hasversion) {
					if ("version".equalsIgnoreCase(colN))
						hasversion = true;
				}

				boolean isNullable = 1 == r.getMetaData().isNullable(i);
				boolean isString = "string".equalsIgnoreCase(colType);

				System.out.println(col + " - " + precisiion + " - " + scale + " ---> " + r.getMetaData().isNullable(i));

				if (col.equalsIgnoreCase("id")) {
					sb.append("@Id\n");
					sb.append("@Column(name = \"ID\")\n");
					sb.append("@GeneratedValue(strategy=GenerationType.AUTO)\n");
					sb.append("private " + colType + " " + colN + ";\n\n");

				} else if (col.equalsIgnoreCase("version")) {
					sb.append("@Version\n");
					sb.append("private Long version;\n\n");

				} else {

					String toAddprec = "";
					if (isString)
						toAddprec = "length=" + precisiion + ", ";

					String toAddnull = "";
					toAddprec = "nullable=" + (isNullable ? "true" : "false") + ", ";

					sb.append("@Column(name = \"" + col + "\", " + toAddprec + " " + toAddnull + "unique = false)\n");
					if (isString)
						sb.append("@Length(max=" + precisiion + ") \n");
					if (!isNullable) {
						if (colType.equalsIgnoreCase("string"))
							sb.append("@NotEmpty\n");
						else
							sb.append("@NotNull\n");
					}
					sb.append("private " + colType + " " + colN + ";\n\n");

					if (col.toLowerCase().endsWith("fk")) {
						String coln1 = colN.substring(0, colN.length() - 2);

						sb.append("@ManyToOne(fetch = FetchType.LAZY, optional = false)\n");
						sb.append("@JoinColumn(name = \"" + col + "\", referencedColumnName = \"ID\", nullable = false, unique = false, insertable = false, updatable = false)\n");
						sb.append("private " + StringUtils.capitalize(coln1) + " " + coln1 + ";\n\n");
					}
				}

				sbget.append("public " + colType + " get" + colnUpper + "() {\n");
				sbget.append("return " + colN + ";\n");
				sbget.append("}\n");
				sbget.append("public void set" + colnUpper + "(" + colType + " " + colN + ") {\n");
				sbget.append("this." + colN + " = " + colN + ";\n");
				sbget.append("}\n");

				String hh = "get" + colnUpper + "()";

				sbhash.append(".append(" + hh + ")");
				sbeq.append(".append(" + hh + ",castOther." + hh + ")");

			}
			close(r);
			close(s);

			sb.append("public " + className + "() {super();}\n");

			sb.append("\n@Override\n");
			sb.append("public String toString() {\n");
			sb.append("return Utils.toStringGlobal(this);\n");
			sb.append("}\n");

			sbeq.append(".isEquals();\n}\n");
			sbhash.append(".toHashCode();\n}\n");

			sb.append(sbget);
			sb.append(sbeq);
			sb.append(sbhash);

			sb.append("/**\n");
			sb.append(" * @see HiddenFieldsSecureInterface#generateSecureFieldsHash()\n");
			sb.append(" */\n");
			sb.append("@Override\n");
			sb.append("public String getSecureFields() {\n");
			sb.append("HiddenFieldBuilder h = HiddenFieldBuilder.create(this.getClass().getName())");
			if (hasversion)
				sb.append(".add(this.version)");

			sb.append(";\n");
			sb.append("if (id != null) {\n");
			sb.append("h.add(id);\n");
			sb.append("}\n");
			if (hasparticipantFk) {
				sb.append("if (participantFk != null) {\n");
				sb.append("h.add(participantFk);\n");
				sb.append("}\n");
			}
			sb.append("return h.toString();\n");
			sb.append("	}\n");

			sb.append("/**\n");
			sb.append(" * @see HiddenFieldsSecureInterface#generateSecureFieldsHash()\n");
			sb.append(" */\n");
			sb.append("@Override\n");
			sb.append("public String generateSecureFieldsHash() {\n");
			sb.append("return Utils.generateSecureFieldsHash(this);\n");
			sb.append("}\n");

			sb.append("\n}\n");

			System.out.println(sb);
			if (saveToFile)
				FileUtils.writeStringToFile(new File(path + className + ".java"), sb.toString(), "UTF-8");

		} catch (Exception t) {
			t.printStackTrace();
		} finally {
			close(conn);
			close(r);
			close(s);
		}
	}

	private static String getColName(String col) {
		int n = col.length();
		String s = "";
		boolean underscore = false;
		for (int i = 0; i < n; i++) {
			char key = col.charAt(i);
			if (key == '_') {
				underscore = true;
			} else {
				if (underscore)
					s += ("" + key).toUpperCase();
				else
					s += ("" + key).toLowerCase();

				underscore = false;
			}
		}
		return s;
	}

	private static String getColType(int i) {
		if (Types.BIGINT == i || Types.INTEGER == i || 2 == i)
			return "Long";
		if (Types.SMALLINT == i)
			return "Integer";
		if (Types.DATE == i || Types.TIME == i || Types.TIMESTAMP == i)
			return "Timestamp";
		if (Types.DOUBLE == i || Types.DECIMAL == i)
			return "BigDecimal";
		if (Types.VARCHAR == i || Types.CHAR == i)
			return "String";
		if (2005 == i)// CLOB
			return "String";
		return "" + i;
	}

	private static String addDefault() {
		StringBuilder sb = new StringBuilder();
		sb.append("package rs.milanmitic.master.model;\n");
		sb.append("import javax.persistence.Entity;\n");
		sb.append("import javax.persistence.Table;\n");
		sb.append("import java.io.Serializable;\n");
		sb.append("import javax.persistence.Column;\n");
		sb.append("import javax.persistence.FetchType;\n");
		sb.append("import javax.persistence.GeneratedValue;\n");
		sb.append("import javax.persistence.GenerationType;\n");
		sb.append("import javax.persistence.Id;\n");
		sb.append("import javax.persistence.Version;\n");
		sb.append("import javax.persistence.JoinColumn;\n");
		sb.append("import javax.persistence.ManyToOne;\n");
		sb.append("import javax.persistence.SequenceGenerator;\n");
		sb.append("import org.hibernate.validator.constraints.NotEmpty;\n");
		sb.append("import rs.milanmitic.master.common.pagging.SearchInput;\n");
		sb.append("import rs.milanmitic.master.common.util.Utils;\n\n");
		sb.append("import rs.milanmitic.master.common.protector.HiddenFieldBuilder;\n");
		sb.append("import rs.milanmitic.master.common.protector.HiddenFieldsSecureInterface;\n");
		sb.append("import org.hibernate.validator.constraints.Length;\n");
		sb.append("import org.apache.commons.lang3.builder.HashCodeBuilder;\n");
		sb.append("import org.apache.commons.lang3.builder.EqualsBuilder;\n");
		sb.append("import javax.validation.constraints.NotNull;\n");
		sb.append("import java.sql.Timestamp;\n");

		sb.append(addDefault1());
		return sb.toString();
	}

	private static String addDefault1() {
		StringBuilder sb = new StringBuilder();

		sb.append("/**\n");
		sb.append("* <p>\n");
		sb.append("* Title: " + className + "\n");
		sb.append("* </p>\n");
		sb.append("*\n");
		sb.append("* <p>\n");
		sb.append("* Description: Domain Object describing a " + className + " entity\n");
		sb.append("* </p>\n");
		sb.append("*\n");
		sb.append("*/\n");
		sb.append("@Entity(name = \"" + className + "\")\n");
		sb.append("@Table(name = \"" + table + "\")\n");

		return sb.toString();
	}

	public static void close(ResultSet rs) {
		try {
			if (rs != null)
				rs.close();
			rs = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void close(Statement st) {
		try {
			if (st != null)
				st.close();
			st = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void close(Connection conn) {
		try {
			if (conn != null && !conn.isClosed()) {

				conn.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static Connection getConnection() throws SQLException, ClassNotFoundException {
		String cmsDBDriver = "com.mysql.jdbc.Driver"; //"oracle.jdbc.driver.OracleDriver";
		// Load class into memory
		Class.forName(cmsDBDriver);
		// Establish connection
		return DriverManager.getConnection(cmsURL, cmsUser, cmsPswd);
	}

}
