/*  -----------------
 *  SysVision DBMagic
 *  -----------------
 *
 *  Copyright 2006-2012 SysVision - Consultadoria e Desenvolvimento em Sistemas de Informática, Lda.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package net.java.dbmagic.xml;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;

import net.java.dbmagic.model.*;
import net.java.sjtools.db.sql.SQLUtil;
import net.java.sjtools.util.TextUtil;

public class XMLModelWriter {
	private static final String TAB = "\t";

	public static void write(Model shm, File file) throws FileNotFoundException {
		PrintWriter out = new PrintWriter(new BufferedOutputStream(new FileOutputStream(file)));

		writeXML(shm, out);
	}

	public static String getXML(Model shm) {
		StringWriter stringWriter = new StringWriter();

		writeXML(shm, new PrintWriter(stringWriter));

		return stringWriter.toString();
	}

	public static void writeXML(Model shm, PrintWriter out) {
		try {
			out.println("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
			out.println();
			out.println("<!DOCTYPE model PUBLIC \"" + XMLConstants.DTD_NAME + "\" \"" + XMLConstants.DTD_URL + "\">");
			out.println();

			proccessSchema(out, shm);
		} finally {
			out.close();
		}
	}

	private static void proccessSchema(PrintWriter out, Model shm) {
		out.println("<model>");

		if (shm.getDescription() != null) {
			proccessDescription(out, shm.getDescription(), 1);
			out.println();
		}

		for (Iterator i = shm.getEntityList().iterator(); i.hasNext();) {
			proccessEntity(out, (Entity) i.next(), 1);
			out.println();
		}

		for (Iterator i = shm.getQueryList().iterator(); i.hasNext();) {
			proccessQuery(out, (Query) i.next(), 1);
			out.println();
		}

		out.println("</model>");
	}

	private static void proccessQuery(PrintWriter out, Query query, int level) {
		write(out, "<query", level);

		if (query.getName() != null) {
			out.print(" name=\"" + query.getName() + "\"");
		}

		if (!query.getReturnType().equals(Query.COLLECTION)) {
			out.print(" return-type=\"" + query.getReturnType() + "\"");
		}

		out.println(">");

		if (query.getDescription() != null) {
			proccessDescription(out, query.getDescription(), level + 1);
		}

		if (query.getReturnObject() != null) {
			proccessReturnObject(out, query.getReturnObject(), level + 1);
		}

		if (query.getSql() != null) {
			proccessSQl(out, query.getSql(), level + 1);
		}

		if (query.isUseFilter()) {
			proccessFilter(out, level + 1);
		}

		for (Iterator i = query.getParameterList().iterator(); i.hasNext();) {
			proccessParameter(out, (Parameter) i.next(), level + 1);
		}

		writeln(out, "</query>", level);
	}

	private static void proccessFilter(PrintWriter out, int level) {
		writeln(out, "<filter/>", level);
	}

	private static void proccessReturnObject(PrintWriter out, ReturnObject returnObject, int level) {
		write(out, "<return-object", level);

		if (returnObject.getClassName() != null) {
			out.print(" class-name=\"" + returnObject.getClassName() + "\"");
		}

		out.println(">");

		if (returnObject.getFieldMapList().size() > 0) {
			for (Iterator i = returnObject.getFieldMapList().iterator(); i.hasNext();) {
				proccessFieldMap(out, (FieldMap) i.next(), level + 1);
			}
		} else {
			proccessColumnMap(out, returnObject.getColumnName(), returnObject.getColumnPosition(), level + 1);
		}

		writeln(out, "</return-object>", level);
	}

	private static void proccessColumnMap(PrintWriter out, String columnName, String columnPosition, int level) {
		if (columnName != null) {
			write(out, "<column-name>", level);
			out.print(columnName);
			out.println("</column-name>");
		} else {
			write(out, "<column-position>", level);
			out.print(columnPosition);
			out.println("</column-position>");
		}
	}

	private static void proccessFieldMap(PrintWriter out, FieldMap map, int level) {
		write(out, "<field-map", level);

		if (map.getFieldName() != null) {
			out.print(" field-name=\"" + map.getFieldName() + "\"");
		}

		if (map.getJavaType() != null) {
			out.print(" java-type=\"" + map.getJavaType() + "\"");
		}

		out.println(">");

		proccessColumnMap(out, map.getColumnName(), map.getColumnPosition(), level + 1);

		writeln(out, "</field-map>", level);
	}

	private static void proccessEntity(PrintWriter out, Entity entity, int level) {
		write(out, "<entity", level);

		if (entity.getName() != null) {
			out.print(" name=\"" + entity.getName() + "\"");
		}

		if (entity.isReadOnly()) {
			out.print(" read-only=\"true\"");
		}

		out.println(">");

		if (entity.getDescription() != null) {
			proccessDescription(out, entity.getDescription(), level + 1);
		}

		if (entity.getSql() != null) {
			proccessSQl(out, entity.getSql(), level + 1);
		}

		if (entity.getPrimaryKey() != null) {
			proccessPrimaryKey(out, entity.getPrimaryKey(), level + 1);
		}

		for (Iterator i = entity.getColumnList().iterator(); i.hasNext();) {
			proccessColumn(out, (Column) i.next(), level + 1);
		}

		if (entity.getVersionControlColumn() != null) {
			proccessVersionControlColumn(out, entity.getVersionControlColumn(), level + 1);
		}

		for (Iterator i = entity.getForeignKeyList().iterator(); i.hasNext();) {
			proccessForeignKey(out, (ForeignKey) i.next(), level + 1);
		}

		for (Iterator i = entity.getIndexList().iterator(); i.hasNext();) {
			proccessIndex(out, (Index) i.next(), level + 1);
		}

		for (Iterator i = entity.getFinderList().iterator(); i.hasNext();) {
			proccessFinder(out, (Finder) i.next(), level + 1);
		}

		for (Iterator i = entity.getOperationList().iterator(); i.hasNext();) {
			proccessOperation(out, (Operation) i.next(), level + 1);
		}

		writeln(out, "</entity>", level);
	}

	private static void proccessVersionControlColumn(PrintWriter out, VersionControlColumn versionControlColumn,
			int level) {
		writeln(out, "<version-control-column>", level);

		if (versionControlColumn.getColumn() != null) {
			proccessColumn(out, versionControlColumn.getColumn(), level + 1);
		}

		writeln(out, "</version-control-column>", level);
	}

	private static void proccessSQl(PrintWriter out, String sql, int level) {
		writeln(out, "<sql>", level);
		writelnCDATA(out, SQLUtil.cleanSQL(sql), level + 1);
		writeln(out, "</sql>", level);
	}

	private static void proccessOperation(PrintWriter out, Operation operation, int level) {
		write(out, "<operation", level);

		if (operation.getName() != null) {
			out.print(" name=\"" + operation.getName() + "\"");
		}

		out.println(">");

		if (operation.getSql() != null) {
			proccessSQl(out, operation.getSql(), level + 1);
		}

		for (Iterator i = operation.getParameterList().iterator(); i.hasNext();) {
			proccessParameter(out, (Parameter) i.next(), level + 1);
		}

		writeln(out, "</operation>", level);
	}

	private static void proccessFinder(PrintWriter out, Finder finder, int level) {
		write(out, "<finder", level);

		if (finder.getName() != null) {
			out.print(" name=\"" + finder.getName() + "\"");
		}

		if (!finder.getReturnType().equals(Finder.COLLECTION)) {
			out.print(" return-type=\"" + finder.getReturnType() + "\"");
		}

		if (finder.isDistinct()) {
			out.print(" distinct-rows=\"true\"");
		}

		if (finder.isLimited()) {
			out.print(" with-limits=\"true\"");
		}

		out.println(">");

		if (finder.getWhere() != null) {
			proccessWhere(out, finder.getWhere(), level + 1);
		}

		for (Iterator i = finder.getParameterList().iterator(); i.hasNext();) {
			proccessParameter(out, (Parameter) i.next(), level + 1);
		}

		writeln(out, "</finder>", level);
	}

	private static void proccessWhere(PrintWriter out, String text, int level) {
		writeln(out, "<where>", level);
		writelnCDATA(out, SQLUtil.cleanSQL(text), level + 1);
		writeln(out, "</where>", level);
	}

	private static void proccessParameter(PrintWriter out, Parameter parameter, int level) {
		write(out, "<parameter", level);

		if (parameter.getName() != null) {
			out.print(" name=\"" + parameter.getName() + "\"");
		}

		if (parameter.getJavaType() != null) {
			out.print(" java-type=\"" + parameter.getJavaType() + "\"");
		}

		out.println("/>");
	}

	private static void proccessIndex(PrintWriter out, Index index, int level) {
		write(out, "<index", level);

		if (index.getName() != null) {
			out.print(" name=\"" + index.getName() + "\"");
		}

		if (index.isUnique()) {
			out.print(" unique=\"true\"");
		}

		out.println(">");

		for (Iterator i = index.getIndexColumnList().iterator(); i.hasNext();) {
			proccessIndexColumn(out, (IndexColumn) i.next(), level + 1);
		}

		writeln(out, "</index>", level);
	}

	private static void proccessIndexColumn(PrintWriter out, IndexColumn column, int level) {
		write(out, "<index-column", level);

		if (column.getColumnName() != null) {
			out.print(" column-name=\"" + column.getColumnName() + "\"");
		}

		out.println("/>");
	}

	private static void proccessForeignKey(PrintWriter out, ForeignKey reference, int level) {
		write(out, "<foreign-key", level);

		if (reference.getReferencedEntity() != null) {
			out.print(" referenced-entity=\"" + reference.getReferencedEntity() + "\"");
		}

		if (reference.isOneToOne()) {
			out.print(" one-to-one=\"true\"");
		}

		out.println(">");

		for (Iterator i = reference.getForeignColumnList().iterator(); i.hasNext();) {
			proccessForeignColumn(out, (ForeignColumn) i.next(), level + 1);
		}

		writeln(out, "</foreign-key>", level);
	}

	private static void proccessForeignColumn(PrintWriter out, ForeignColumn column, int level) {
		write(out, "<foreign-column", level);

		if (column.getColumnName() != null) {
			out.print(" column-name=\"" + column.getColumnName() + "\"");
		}

		if (column.getReferencedColumn() != null) {
			out.print(" referenced-column=\"" + column.getReferencedColumn() + "\"");
		}

		out.println("/>");
	}

	private static void proccessColumn(PrintWriter out, Column column, int level) {
		write(out, "<column", level);

		if (column.getName() != null) {
			out.print(" name=\"" + column.getName() + "\"");
		}

		if (column.getJavaType() != null) {
			out.print(" java-type=\"" + column.getJavaType() + "\"");
		}

		if (column.getMaxSize() > 0) {
			out.print(" max-size=\"" + column.getMaxSize() + "\"");
		}
		
		if (column.isFixedSize()) {
			out.print(" fixed-size=\"true\"");
		}

		if (column.getMaxSize() > 0 && column.getDecimalDigits() > 0) {
			out.print(" decimal-digits=\"" + column.getDecimalDigits() + "\"");
		}

		if (!column.isNullable()) {
			out.print(" nullable=\"false\"");
		}

		out.println("/>");
	}

	private static void proccessPrimaryKey(PrintWriter out, PrimaryKey key, int level) {
		writeln(out, "<primary-key>", level);

		if (key.getKeyGenerator() != null) {
			proccessKeyGenerator(out, key.getKeyGenerator(), level + 1);
		}

		for (Iterator i = key.getColumnList().iterator(); i.hasNext();) {
			proccessColumn(out, (Column) i.next(), level + 1);
		}

		writeln(out, "</primary-key>", level);
	}

	private static void proccessKeyGenerator(PrintWriter out, KeyGenerator generator, int level) {
		write(out, "<key-generator", level);

		if (generator.getType() != null) {
			out.print(" type=\"" + generator.getType() + "\"");
		}

		if (generator.getPropertyList().size() > 0) {
			out.println(">");

			for (Iterator i = generator.getPropertyList().iterator(); i.hasNext();) {
				proccessProperty(out, (Property) i.next(), level + 1);
			}

			writeln(out, "</key-generator>", level);
		} else {
			out.println("/>");
			;
		}
	}

	private static void proccessProperty(PrintWriter out, Property property, int level) {
		write(out, "<property", level);

		if (property.getName() != null) {
			out.print(" name=\"" + property.getName() + "\"");
		}

		if (property.getValue() != null) {
			out.print(" value=\"" + property.getValue() + "\"");
		}

		out.println("/>");
	}

	private static void proccessDescription(PrintWriter out, String text, int level) {
		writeln(out, "<description>", level);
		writelnCDATA(out, text, level);
		writeln(out, "</description>", level);
	}

	private static void writeCDATA(PrintWriter out, String text) {
		out.print("<![CDATA[");
		out.print(text);
		out.print("]]>");
	}

	private static void writelnCDATA(PrintWriter out, String text, int level) {
		write(out, null, level);
		writeCDATA(out, text);
		out.println();
	}

	private static void write(PrintWriter out, String text, int level) {
		for (int i = 0; i < level; i++) {
			out.print(TAB);
		}

		if (!TextUtil.isEmptyString(text)) {
			out.print(text);
		}
	}

	private static void writeln(PrintWriter out, String text, int level) {
		write(out, text, level);
		out.println();
	}
}
