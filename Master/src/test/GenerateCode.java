package test;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import rs.milanmitic.master.model.PassPolicy;

public class GenerateCode {

	private static boolean writeViewsDefinition = true;
	private static boolean writeMenu = true;
	private static boolean writeRepositoryImpl = true;
	private static boolean writeServiceImpl = true;
	private static boolean writeRepository = true;
	private static boolean writeService = true;
	private static boolean writeController = true;
	private static boolean writeSaveJsp = true;
	private static boolean writeListJsp = true;
	private static boolean writeProperies = true;

	private static final String PATH = "C:/workspace-IPG/Master/";

	private static String jspSubFolder = "nomenclature";
	private static String serviceDaoName = "Nomenclature";
	private static String CONTROLLER_FOLDER = "src/rs/milanmitic/master/controller/";
	private static String REPOSITORY_FOLDER = "src/rs/milanmitic/master/repository/";
	private static String SERVICE_FOLER = "src/rs/milanmitic/master/service/";

	private GenerateCode() {
		super();
	}

	public static void main(String[] args) {
		Object entity = new PassPolicy();
		String entityName = entity.getClass().getSimpleName();
		String entityNameTitle = makeLabelTitle(entityName);
		String entitySmallFirstLetter = ("" + entityName.charAt(0)).toLowerCase() + entityName.substring(1);
		String templateListFile = PATH + "src/test/listTemplate.jsp";
		String templateSaveFile = PATH + "src/test/saveTemplate.jsp";
		String templateControllerFile = PATH + "src/test/EntityController.txt";

		String entityListFile = PATH + "WebContent/WEB-INF/views/" + jspSubFolder + "/" + entitySmallFirstLetter + "List.jsp";
		String entitySaveFile = PATH + "WebContent/WEB-INF/views/" + jspSubFolder + "/" + entitySmallFirstLetter + "Save.jsp";
		String entityControllerSaveFile = PATH + CONTROLLER_FOLDER + entityName + "Controller.java";

		String docCommentService = serviceDaoName + "Service";
		String docCommentDAO = serviceDaoName + "Dao";
		String docCommentDAOSmallFirstLetter = StringUtils.uncapitalize(serviceDaoName + "Dao");
		String nomenclatureServiceAppendFile = PATH + SERVICE_FOLER + serviceDaoName + "Service.java";
		String nomenclatureServiceImplAppendFile = PATH + SERVICE_FOLER + serviceDaoName + "ServiceImpl.java";

		String nomenclatureRepositoryAppendFile = PATH + REPOSITORY_FOLDER + serviceDaoName + "Dao.java";
		String nomenclatureRepositoryImplAppendFile = PATH + REPOSITORY_FOLDER + serviceDaoName + "HibernateDao.java";

		String propertiesLabelAppendFile = PATH + "WebContent/WEB-INF/messages/label.properties";
		String propertiesMenuAppendFile = PATH + "WebContent/WEB-INF/messages/menu_title.properties";
		String propertiesErrorAppendFile = PATH + "WebContent/WEB-INF/messages/error.properties";

		String menuXMLFile = PATH + "WebContent/WEB-INF/menu-config.xml";
		String viewsFile = PATH + "WebContent/WEB-INF/views/views.xml";

		try {
			String listData = FileUtils.readFileToString(new File(templateListFile));
			String saveData = FileUtils.readFileToString(new File(templateSaveFile));
			String controllerData = FileUtils.readFileToString(new File(templateControllerFile));

			String nomenclatureServiceData = FileUtils.readFileToString(new File(nomenclatureServiceAppendFile));
			String nomenclatureServiceImplData = FileUtils.readFileToString(new File(nomenclatureServiceImplAppendFile));
			String nomenclatureRepositoryData = FileUtils.readFileToString(new File(nomenclatureRepositoryAppendFile));
			String nomenclatureRepositoryImplData = FileUtils.readFileToString(new File(nomenclatureRepositoryImplAppendFile));

			String propertiesMenuData = FileUtils.readFileToString(new File(propertiesMenuAppendFile));
			String propertiesLabelData = FileUtils.readFileToString(new File(propertiesLabelAppendFile));

			String propertiesErrorData = FileUtils.readFileToString(new File(propertiesErrorAppendFile));
			String menuXMLData = FileUtils.readFileToString(new File(menuXMLFile));

			String viewsData = FileUtils.readFileToString(new File(viewsFile));

			listData = StringUtils.replace(listData, "|entitySmallFirstLetter|", entitySmallFirstLetter);
			listData = StringUtils.replace(listData, "|entity|", entityName);

			saveData = StringUtils.replace(saveData, "|entitySmallFirstLetter|", entitySmallFirstLetter);
			saveData = StringUtils.replace(saveData, "|entity|", entityName);

			controllerData = StringUtils.replace(controllerData, "|entitySmallFirstLetter|", entitySmallFirstLetter);
			controllerData = StringUtils.replace(controllerData, "|entity|", entityName);

			// |searchFields| |tableHeader| |tableColumn|
			// |inputFields|
			String searchFieldTemplate = "\t\t\t\t\t<masterInput:searchInputField label=\"label.entitySmallFirstLetter.fieldName\" name=\"fieldName\" value=\"${entitySmallFirstLetter.fieldName }\" />  ";
			String searchFieldTemplateDate = "\t\t\t\t\t<masterInput:searchDateTimeField label=\"label.entitySmallFirstLetter.fieldName\" name=\"fieldName\" required=\"false\" type=\"date\" />";
			String searchFieldTemplateSb = "\t\t\t\t\t<masterInput:searchSelectField label=\"label.entitySmallFirstLetter.fieldName\" name=\"fieldName\" required=\"false\" items=\"${ fieldNameList }\"   size=\"1\" />";

			String tableHeadertemplate = "\t\t\t<th _col=\"counter\" onclick=\"return applySearchOrder(this)\"><spring:message code=\"label.entitySmallFirstLetter.fieldName\" /></th>";

			String tableColumntemplate = "\t\t\t\t<td><c:out value=\"${item.fieldName}\" /></td>";
			String tableColumntemplateId = "\t\t\t\t<td><a class=\"activeLink\" onclick=\"return showLoadingDiv()\" href=\"<c:url value=\"/app/entitySmallFirstLetter/view/${item.id}\"/>\"><c:out  value=\"${item.id}\" /></a></td>";

			String inputFieldTemplate = "\t\t<masterInput:inputField label=\"label.entitySmallFirstLetter.fieldName\" name=\"fieldName\"  />";
			String inputFieldTemplateDate = "\t\t<masterInput:dateTimeField label=\"label.entitySmallFirstLetter.fieldName\" name=\"fieldName\" required=\"false\" type=\"date\" />";
			String inputFieldTemplateSb = "\t\t<masterInput:selectField label=\"label.entitySmallFirstLetter.fieldName\" name=\"fieldName\" required=\"false\" items=\"${ fieldNameList }\"   size=\"1\" fieldClass=\"form-control _selectBox2Default\" />";

			Field[] fieldList = entity.getClass().getDeclaredFields();
			StringBuilder searchFields = new StringBuilder();
			StringBuilder tableHeader = new StringBuilder();
			StringBuilder tableColumn = new StringBuilder();
			StringBuilder inputFields = new StringBuilder();

			StringBuilder constructorOrderBy = new StringBuilder();
			StringBuilder labels = new StringBuilder("# " + entityName + " labels\r\n");
			StringBuilder searchData = new StringBuilder();
			int counter = 0;

			Set<String> primitiveList = new HashSet<String>();
			primitiveList.add("String");
			primitiveList.add("Long");
			primitiveList.add("Integer");
			primitiveList.add("Date");
			primitiveList.add("Timestamp");
			primitiveList.add("BigDecimal");
			primitiveList.add("Double");

			boolean hasParticipantFk = false;
			for (Field f : fieldList) {
				if (f.getType() == null || java.lang.reflect.Modifier.isStatic(f.getModifiers()))
					continue;

				if (!primitiveList.contains(f.getType().getSimpleName()))
					continue;

				counter++;
				String fieldName = f.getName();
				String fieldCapitalize = StringUtils.capitalize(fieldName);
				String l = makeLabelTitle(fieldName);

				if (!"version".equals(fieldName) && !"dateFrom".equals(fieldName) && !"dateTo".equals(fieldName) && !"timeFrom".equals(fieldName) && !"timeTo".equals(fieldName))
					labels.append("label." + entitySmallFirstLetter + "." + fieldName + " = " + l).append("\r\n");

				if ("ParticipantFk".equalsIgnoreCase(fieldName))
					hasParticipantFk = true;
				String x = null;
				boolean isDate = f.getType().getSimpleName().equals("Date") || f.getType().getSimpleName().equals("Timestamp");
				boolean isLong = f.getType().getSimpleName().equals("Long");
				boolean isInteger = f.getType().getSimpleName().equals("Integer");
				boolean isNumber = isInteger || isLong;
				boolean isString = f.getType().getSimpleName().equals("String");
				boolean isFk = fieldName.indexOf("Fk") != -1;

				if (!"version".equals(fieldName)) {
					if (isString) {
						// searchData.append("if (StringUtils.isNotBlank(bean.get" + fieldCapitalize + "())) { \r\n");
						searchData.append("sql += addSQLString(\"a." + fieldName + "\", bean.get" + fieldCapitalize + "(), bean, map);\r\n");
						// searchData.append("}\r\n");

					} else if (isInteger) {
						// searchData.append("if (bean.get" + fieldCapitalize + "()!=null) { \r\n");
						searchData.append("sql += addSQLInt(\"a." + fieldName + "\", bean.get" + fieldCapitalize + "() , bean, map);\r\n");
						// searchData.append("}\r\n");

					} else if (isLong) {
						// searchData.append("if (bean.get" + fieldCapitalize + "()!=null) { \r\n");
						searchData.append("sql += addSQLLong(\"a." + fieldName + "\", bean.get" + fieldCapitalize + "() , bean, map);\r\n");
						// searchData.append("}\r\n");

					} else if (isDate) {
						// searchData.append("if (bean.get" + fieldCapitalize + "()!=null) { \r\n");
						searchData.append("//TODO datume ove posebno ubaciti u search kada se vidi da li ima from i to  i da li je sa vremenom ili ne - a trebalo bi da ih ima \r\n");
						searchData.append("sql += addSQLDate(\"a." + fieldName + "\", bean.get" + fieldCapitalize + "(), timeFrom, dateTo, timeTo,  bean, map);\r\n");
						// searchData.append("}\r\n");

					} else {
						searchData.append("if (bean.get" + fieldCapitalize + "()!=null) { \r\n");
						searchData.append("sql += \" AND a." + fieldName + " = :" + fieldName + " \";\r\n");
						searchData.append("map.put(\"" + fieldName + "\", bean.get" + fieldCapitalize + "() );\r\n");
						searchData.append("}\r\n");
					}

				}

				if (isDate)
					x = StringUtils.replace(searchFieldTemplateDate, "fieldName", fieldName);
				else if (isFk)
					x = StringUtils.replace(searchFieldTemplateSb, "fieldName", fieldName);

				else
					x = StringUtils.replace(searchFieldTemplate, "fieldName", fieldName);
				x = StringUtils.replace(x, "entitySmallFirstLetter", entitySmallFirstLetter);

				if (isNumber)
					x = StringUtils.replace(x, "fieldClass=\"", "fieldClass=\"numberAlign ");

				if (!"id".equals(fieldName) && !"version".equals(fieldName))
					searchFields.append(x).append("\r\n");

				x = StringUtils.replace(tableHeadertemplate, "fieldName", fieldName);
				x = StringUtils.replace(x, "counter", "" + counter);
				x = StringUtils.replace(x, "entitySmallFirstLetter", entitySmallFirstLetter);

				if (!"version".equals(fieldName))
					if (!"dateFrom".equals(fieldName) && !"dateTo".equals(fieldName) && !"timeFrom".equals(fieldName) && !"timeTo".equals(fieldName)) {
						tableHeader.append(x).append("\r\n");
						if (isString)
							constructorOrderBy.append("addColumnMapping(\"" + counter + "\", \"lower(a." + fieldName + ")\");\r\n");
						else
							constructorOrderBy.append("addColumnMapping(\"" + counter + "\", \"a." + fieldName + "\");\r\n");
					}

				if ("id".equals(fieldName))
					x = StringUtils.replace(tableColumntemplateId, "fieldName", fieldName);
				else
					x = StringUtils.replace(tableColumntemplate, "fieldName", fieldName);

				if (isNumber)
					x = StringUtils.replace(x, "<td", "<td class=\"numberAlign\"");

				if (isDate) {
					x = StringUtils.replace(x, "<c:out", "<util:formatDate type=\"both\" ");
				}

				x = StringUtils.replace(x, "entitySmallFirstLetter", entitySmallFirstLetter);
				if (!"version".equals(fieldName))
					if (!"dateFrom".equals(fieldName) && !"dateTo".equals(fieldName) && !"timeFrom".equals(fieldName) && !"timeTo".equals(fieldName))
						tableColumn.append(x).append("\r\n");

				if (isDate)
					x = StringUtils.replace(inputFieldTemplateDate, "fieldName", fieldName);
				else if (isFk)
					x = StringUtils.replace(inputFieldTemplateSb, "fieldName", fieldName);
				else
					x = StringUtils.replace(inputFieldTemplate, "fieldName", fieldName);
				x = StringUtils.replace(x, "entitySmallFirstLetter", entitySmallFirstLetter);

				if (!"id".equals(fieldName) && !"version".equals(fieldName))
					if (!"dateFrom".equals(fieldName) && !"dateTo".equals(fieldName) && !"timeFrom".equals(fieldName) && !"timeTo".equals(fieldName))
						inputFields.append(x).append("\r\n");

			}

			saveData = StringUtils.replace(saveData, "|searchFields|", searchFields.toString());
			saveData = StringUtils.replace(saveData, "|tableHeader|", tableHeader.toString());
			saveData = StringUtils.replace(saveData, "|tableColumn|", tableColumn.toString());
			saveData = StringUtils.replace(saveData, "|inputFields|", inputFields.toString());

			listData = StringUtils.replace(listData, "|searchFields|", searchFields.toString());
			listData = StringUtils.replace(listData, "|tableHeader|", tableHeader.toString());
			listData = StringUtils.replace(listData, "|tableColumn|", tableColumn.toString());
			listData = StringUtils.replace(listData, "|inputFields|", inputFields.toString());

			if (writeListJsp)
				FileUtils.writeStringToFile(new File(entityListFile), listData, false);
			else
				System.out.println("\n\n**************\n LIST \n************\n" + listData);
			if (writeSaveJsp)
				FileUtils.writeStringToFile(new File(entitySaveFile), saveData, false);
			else
				System.out.println("\n\n**************\n SAVE \n************\n" + saveData);
			if (writeController)
				FileUtils.writeStringToFile(new File(entityControllerSaveFile), controllerData, false);
			else
				System.out.println("\n\n**************\n CONROLLER \n************\n" + controllerData);

			// to append to service interface and repository interface
			StringBuilder sb = new StringBuilder();
			sb.append("\r\n");
			sb.append("/**").append("\r\n");
			sb.append("* Get record by PK").append("\r\n");
			sb.append("* @param id").append("\r\n");
			sb.append("* @return record or null").append("\r\n");
			sb.append("*/").append("\r\n");
			sb.append(entityName + " get" + entityName + "ById(Long id);").append("\r\n");

			sb.append("/**").append("\r\n");
			sb.append("* Update record in DB").append("\r\n");
			sb.append("* @param bean").append("\r\n");
			sb.append("*/").append("\r\n");
			sb.append("void update" + entityName + "(" + entityName + " bean);").append("\r\n");

			sb.append("/**").append("\r\n");
			sb.append("* Add new record in DB").append("\r\n");
			sb.append("* @param bean").append("\r\n");
			sb.append("*/").append("\r\n");
			sb.append("void add" + entityName + "(" + entityName + " bean);").append("\r\n");

			sb.append("/**").append("\r\n");
			sb.append("* Search database for " + entityName + "s ").append("\r\n");
			sb.append("* @param bean").append("\r\n");
			sb.append("* @return list of results").append("\r\n");
			sb.append("*/").append("\r\n");
			sb.append("SearchResults get" + entityName + "List(" + entityName + " bean);").append("\r\n");

			sb.append("/**").append("\r\n");
			sb.append("* Delete record from DB").append("\r\n");
			sb.append("* @param bean").append("\r\n");
			sb.append("*/").append("\r\n");
			sb.append("void delete" + entityName + "(" + entityName + " bean);").append("\r\n");

			nomenclatureServiceData = StringUtils.replace(nomenclatureServiceData, "}", sb + "\r\n}");

			sb = new StringBuilder();
			sb.append("/**").append("\r\n");
			sb.append("* Check is record can be deleted from DB").append("\r\n");
			sb.append("* @param b").append("\r\n");
			sb.append("*/").append("\r\n");
			sb.append("public void preDelete" + entityName + "Check(" + entityName + " b);").append("\r\n");
			sb.append("/**").append("\r\n");
			sb.append("* Search database for " + entityName + "s ").append("\r\n");
			sb.append("* @param bean").append("\r\n");
			sb.append("* @return list of results").append("\r\n");
			sb.append("*/").append("\r\n");
			sb.append("SearchResults get" + entityName + "List(" + entityName + " bean);").append("\r\n");

			nomenclatureRepositoryData = StringUtils.replace(nomenclatureRepositoryData, "}", sb + "\r\n}");

			if (writeService)
				FileUtils.writeStringToFile(new File(nomenclatureServiceAppendFile), nomenclatureServiceData, false);
			else
				System.out.println("\n\n**************\n SERVICE \n************\n" + nomenclatureServiceData);
			if (writeRepository)
				FileUtils.writeStringToFile(new File(nomenclatureRepositoryAppendFile), nomenclatureRepositoryData, false);
			else
				System.out.println("\n\n**************\n REPOSITORY \n************\n" + nomenclatureRepositoryData);

			sb = new StringBuilder();
			sb.append("/**").append("\r\n");
			sb.append("* @see " + docCommentService + "#get" + entityName + "ById(Long)").append("\r\n");
			sb.append("*/").append("\r\n");
			sb.append("@Override").append("\r\n");
			sb.append("@MasterLogAnnotation").append("\r\n");
			sb.append("public " + entityName + " get" + entityName + "ById(Long id) {").append("\r\n");
			if (hasParticipantFk) {
				sb.append("" + entityName + " b = " + docCommentDAOSmallFirstLetter + ".getByPk(id, " + entityName + ".class);").append("\r\n");
				sb.append("if (b != null && ContextHolder.isLoggedUserParticipant() && !ContextHolder.getLoggedParticipantData().getParticipantId().equals(b.getParticipantFk()))").append("\r\n");
				sb.append("b = null;").append("\r\n");
				sb.append("return b;").append("\r\n");
			} else {
				sb.append(" return " + docCommentDAOSmallFirstLetter + ".getByPk(id, " + entityName + ".class);").append("\r\n");
				sb.append("// if (b != null && ContextHolder.isLoggedUserParticipant() && !ContextHolder.getLoggedParticipantData().getParticipantId().equals(b.getParticipantFk()))").append("\r\n");
				sb.append("// b = null;").append("\r\n");
				sb.append("// return b;").append("\r\n");
			}
			sb.append("}").append("\r\n");

			sb.append("/**").append("\r\n");
			sb.append("* @see " + docCommentService + "#update" + entityName + "(Object)").append("\r\n");
			sb.append("*/").append("\r\n");
			sb.append("@Override").append("\r\n");
			sb.append("@MasterLogAnnotation").append("\r\n");
			sb.append("public void update" + entityName + "(" + entityName + " bean) {").append("\r\n");
			sb.append("" + docCommentDAOSmallFirstLetter + ".update(bean);").append("\r\n");
			sb.append("}").append("\r\n");

			sb.append("/**").append("\r\n");
			sb.append("* @see " + docCommentService + "#save" + entityName + "(Object)").append("\r\n");
			sb.append("*/").append("\r\n");
			sb.append("@Override").append("\r\n");
			sb.append("@MasterLogAnnotation").append("\r\n");
			sb.append("public void add" + entityName + "(" + entityName + " bean) {").append("\r\n");
			sb.append("" + docCommentDAOSmallFirstLetter + ".save(bean);").append("\r\n");
			sb.append("}").append("\r\n");

			sb.append("/**").append("\r\n");
			sb.append("* @see " + docCommentService + "#get" + entityName + "List(" + entityName + ") {").append("\r\n");
			sb.append("*/").append("\r\n");
			sb.append("@Override").append("\r\n");
			sb.append("@MasterLogAnnotation").append("\r\n");
			sb.append("public SearchResults get" + entityName + "List(" + entityName + " bean) {").append("\r\n");
			sb.append("return " + docCommentDAOSmallFirstLetter + ".get" + entityName + "List(bean);").append("\r\n");
			sb.append("}").append("\r\n");

			sb.append("/**").append("\r\n");
			sb.append("* @see " + docCommentService + "#delete" + entityName + "(Object)").append("\r\n");
			sb.append("*/").append("\r\n");
			sb.append("@Override").append("\r\n");
			sb.append("@MasterLogAnnotation").append("\r\n");
			sb.append("public void delete" + entityName + "(" + entityName + " bean) {").append("\r\n");
			sb.append("" + entityName + " b = get" + entityName + "ById(bean.getId());").append("\r\n");
			sb.append("if (b == null)").append("\r\n");
			sb.append("throw new ValidateException(\"error." + entitySmallFirstLetter + ".notFound\", \"\" + bean.getId());").append("\r\n");
			sb.append("if (!b.getVersion().equals(bean.getVersion()))").append("\r\n");
			sb.append("throw new ValidateException(\"error.fieldUpdatedOrDeleted\");").append("\r\n");
			sb.append("" + docCommentDAOSmallFirstLetter + ".preDelete" + entityName + "Check(b);").append("\r\n");
			sb.append("" + docCommentDAOSmallFirstLetter + ".delete(b);").append("\r\n");
			sb.append("}").append("\r\n");

			int l = nomenclatureServiceImplData.lastIndexOf("}");
			nomenclatureServiceImplData = nomenclatureServiceImplData.substring(0, l);
			nomenclatureServiceImplData = nomenclatureServiceImplData + sb + "}";

			if (writeServiceImpl)
				FileUtils.writeStringToFile(new File(nomenclatureServiceImplAppendFile), nomenclatureServiceImplData, false);
			else
				System.out.println("\n\n**************\n SERVICEIMPL \n************\n" + nomenclatureServiceImplData);

			sb = new StringBuilder();
			sb.append("/**").append("\r\n");
			sb.append("* @see " + docCommentDAO + "#preDelete" + entityName + "Check(" + entityName + ") {").append("\r\n");
			sb.append("*/").append("\r\n");
			sb.append("@Override").append("\r\n");
			sb.append("@MasterLogAnnotation").append("\r\n");
			sb.append("public void preDelete" + entityName + "Check(" + entityName + " b) {").append("\r\n");
			sb.append("// for future use \r\n");
			sb.append("}").append("\r\n");

			sb.append("/**").append("\r\n");
			sb.append("* @see " + docCommentDAO + "#get" + entityName + "List(" + entityName + ") {").append("\r\n");
			sb.append("*/").append("\r\n");
			sb.append("@Override").append("\r\n");
			sb.append("@MasterLogAnnotation").append("\r\n");
			sb.append("public SearchResults get" + entityName + "List(" + entityName + " bean) {").append("\r\n");
			sb.append("if (bean == null) {").append("\r\n");
			sb.append("bean = new " + entityName + "();").append("\r\n");
			sb.append("bean.setItemsPerPage(Integer.MAX_VALUE);").append("\r\n");
			sb.append("}").append("\r\n");
			sb.append("String sql = SQL_DEFAULT_WHERE_1_1;").append("\r\n");
			sb.append("Map<String, Object> map = new HashMap<String, Object>();").append("\r\n");

			// sb.append("if (bean!=null) {\r\n");
			sb.append(searchData);
			// sb.append("}\r\n");

			sb.append("if (ContextHolder.isLoggedUserParticipant()) {").append("\r\n");
			sb.append("//sql += addSQLLong(\"a.participantFk\", ContextHolder.getLoggedParticipantData().getParticipantId(), bean, map);").append("\r\n");
			sb.append("}").append("\r\n");

			sb.append("SearchResults sd = new SearchResults(bean);").append("\r\n");
			sb.append("List<SearchInput> l = executePagging(\"SELECT a from " + entityName + " a \" + sql, bean, map);").append("\r\n");
			sb.append("sd.setResults(l);").append("\r\n");
			sb.append("if (sd.isExecuteCount())").append("\r\n");
			sb.append("sd.setResultsCount(executeCount(\"SELECT count(*) from " + entityName + " a \" + sql, bean, map));").append("\r\n");

			sb.append("return sd;").append("\r\n");
			sb.append("}").append("\r\n");

			l = nomenclatureRepositoryImplData.lastIndexOf("}");
			nomenclatureRepositoryImplData = nomenclatureRepositoryImplData.substring(0, l);
			nomenclatureRepositoryImplData = nomenclatureRepositoryImplData + sb + "}";

			if (writeRepositoryImpl)
				FileUtils.writeStringToFile(new File(nomenclatureRepositoryImplAppendFile), nomenclatureRepositoryImplData, false);
			else
				System.out.println("\n\n**************\n REPOSITORY IMPL \n************\n" + nomenclatureRepositoryImplData);

			// PROPERTIES
			// menu
			StringBuilder menuLabels = new StringBuilder();
			menuLabels.append("title." + entitySmallFirstLetter + ".list=List of " + entityNameTitle + "s\r\n");
			menuLabels.append("title." + entitySmallFirstLetter + ".add=Add new " + entityNameTitle + "\r\n");
			menuLabels.append("title." + entitySmallFirstLetter + ".update=" + entityNameTitle + "\r\n");
			menuLabels.append("title." + entitySmallFirstLetter + ".view=View " + entityNameTitle + "\r\n");

			menuLabels.append("menu." + entitySmallFirstLetter + "=" + entityNameTitle + "\r\n");
			menuLabels.append("menu.add" + entityName + "=Add new " + entityNameTitle + "\r\n");
			menuLabels.append("menu.list" + entityName + "=" + entityNameTitle + " List\r\n");

			if (writeProperies) {
				FileUtils.writeStringToFile(new File(propertiesMenuAppendFile), propertiesMenuData + "\r\n" + menuLabels, false);
				FileUtils.writeStringToFile(new File(propertiesLabelAppendFile), propertiesLabelData + "\r\n" + labels, false);
			} else
				System.out.println("\n\n**************\n PROPERTIES \n************\n" + menuLabels + "\n" + labels);

			String x = "error." + entitySmallFirstLetter + ".notFound=" + entityName + " with ID={0} not exist";
			if (writeProperies)
				FileUtils.writeStringToFile(new File(propertiesErrorAppendFile), propertiesErrorData + "\r\n" + x, false);
			else
				System.out.println("\n\n**************\n PROPERTIES \n************\n" + x);

			//
			StringBuilder menuLabel = new StringBuilder();
			menuLabel.append("\t\t<Item   name=\"menu." + entitySmallFirstLetter + "\"   title=\"menu." + entitySmallFirstLetter + "\" >\r\n");
			menuLabel.append("\t\t\t<Item   name=\"menu.add" + entityName + "\"   title=\"menu.add" + entityName + "\" page=\"/app/" + entitySmallFirstLetter + "/add\"/>\r\n");
			menuLabel.append("\t\t\t<Item   name=\"menu.list" + entityName + "\"   title=\"menu.list" + entityName + "\" page=\"/app/" + entitySmallFirstLetter + "/list\"/>\r\n");
			menuLabel.append("\t\t</Item>\r\n\r\n");

			String s = StringUtils.replace(menuXMLData, "<!-- addMenuItems -->", "<!-- addMenuItems -->\r\n" + menuLabel);

			if (writeMenu)
				FileUtils.writeStringToFile(new File(menuXMLFile), s, false);
			else
				System.out.println("\n\n**************\n MENU \n************\n" + s);

			// VIEWS
			StringBuilder views = new StringBuilder();
			views.append("\t<definition name=\"" + entitySmallFirstLetter + "List\" extends=\"homeLayout\">\r\n");
			views.append("\t\t<put-attribute name=\"body\" value=\"/WEB-INF/views/" + jspSubFolder + "/" + entitySmallFirstLetter + "List.jsp\" />\r\n");
			views.append("\t</definition>\r\n\r\n");

			views.append("\t<definition name=\"" + entitySmallFirstLetter + "Save\" extends=\"homeLayout\">\r\n");
			views.append("\t\t<put-attribute name=\"body\" value=\"/WEB-INF/views/" + jspSubFolder + "/" + entitySmallFirstLetter + "Save.jsp\" />\r\n");
			views.append("\t</definition>\r\n");

			s = StringUtils.replace(viewsData, "</tiles-definitions>", views.toString() + "</tiles-definitions>");

			if (writeViewsDefinition)
				FileUtils.writeStringToFile(new File(viewsFile), s, false);
			else
				System.out.println("\n\n**************\n VIEWS \n************\n" + s);

			System.out.println("constructorOrderBy:\n" + constructorOrderBy);
			String role = "INSERT INTO APP_FUNCTION(ID, FUNCTION_NAME, URLS)  VALUES (1, '" + entitySmallFirstLetter.toUpperCase() + "_VIEW', '/app/" + entitySmallFirstLetter + "/view*|/app/" + entitySmallFirstLetter
					+ "/list');\r\n";
			role += "INSERT INTO APP_FUNCTION(ID, FUNCTION_NAME, URLS)  VALUES (2, '" + entitySmallFirstLetter.toUpperCase() + "_UPDATE', '/app/" + entitySmallFirstLetter + "/view*|/app/" + entitySmallFirstLetter
					+ "/add*|/app/" + entitySmallFirstLetter + "/list|/app/" + entitySmallFirstLetter + "/save*|/app/" + entitySmallFirstLetter + "/delete*');\r\n";
			System.out.println("\nROLE:\n" + role);

		} catch (Exception t) {
			t.printStackTrace();
		}

	}

	private static String makeLabelTitle(String fieldName) {
		StringBuilder sb = new StringBuilder();
		for (char c : fieldName.toCharArray()) {
			if (Character.isUpperCase(c)) {
				sb.append(" ").append(c);
			} else {
				sb.append(c);
			}

		}
		String s = StringUtils.capitalize(sb.toString());
		s = StringUtils.replace(s, "Participant Fk", "Participant");
		s = StringUtils.replace(s, "App User Fk", "User");
		return s;
	}

}
