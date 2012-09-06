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
package net.java.dbmagic.clay;

import net.java.dbmagic.db.JDBCUtil;
import net.java.dbmagic.model.Column;
import net.java.dbmagic.model.Entity;
import net.java.dbmagic.model.ForeignColumn;
import net.java.dbmagic.model.ForeignKey;
import net.java.dbmagic.model.Index;
import net.java.dbmagic.model.IndexColumn;
import net.java.dbmagic.model.KeyGenerator;
import net.java.dbmagic.model.Model;
import net.java.dbmagic.model.PrimaryKey;
import net.java.sjtools.util.TextUtil;
import net.java.sjtools.xml.SimpleHandler;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


public class ClayModelHandler extends SimpleHandler {

	public Object proccessElement(String elementType, Object currentObject, Attributes attributes) throws SAXException {
		if (elementType.equals("database-model")) {
			return new Model();
		} else if (elementType.equals("table")) {
			Entity ret = new Entity();

			ret.setName(attributes.getValue("name"));

			((Model) currentObject).addEntity(ret);

			return ret;
		} else if (elementType.equals("column")) {
			ClayColumn column = new ClayColumn();
			column.setName(attributes.getValue("name"));
			column.setDecimalDigits(Integer.parseInt(attributes.getValue("decimal-digits")));
			column.setMaxSize(Integer.parseInt(attributes.getValue("column-size")));
			column.setNullable(!Boolean.valueOf(attributes.getValue("mandatory")).booleanValue());
			column.setAutoIncrement(Boolean.valueOf(attributes.getValue("auto-increment")).booleanValue());
			((Entity) currentObject).addColumn(column);

			return column;
		} else if (elementType.equals("data-type")) {
			Column column = (Column) currentObject;

			short jdbcType = Short.parseShort(attributes.getValue("jdbc-type"));

			column.setJavaType(JDBCUtil.getJavaType(jdbcType, column.getMaxSize(), column.getDecimalDigits()));
			column.setMaxSize(JDBCUtil.getMaxSize(jdbcType, column.getMaxSize()));
		} else if (elementType.equals("primary-key")) {
			PrimaryKey primaryKey = new PrimaryKey();

			primaryKey.setName(attributes.getValue("name"));
			primaryKey.setEntity((Entity) currentObject);

			return primaryKey;
		} else if (elementType.equals("primary-key-column")) {
			PrimaryKey primaryKey = (PrimaryKey) currentObject;

			Entity entity = primaryKey.getEntity();

			entity.setPrimaryKey(primaryKey);
			String columnName = attributes.getValue("name");

			ClayColumn column = (ClayColumn) entity.getColumn(columnName);

			if (column.isAutoIncrement()) {
				KeyGenerator keyGenerator = new KeyGenerator();
				keyGenerator.setType(KeyGenerator.NATIVE);
				primaryKey.setKeyGenerator(keyGenerator);
			}

			primaryKey.addColumn(column);

			if (primaryKey.getColumnList() != null) {
				if (primaryKey.getColumnList().size() > 1) {
					primaryKey.setKeyGenerator(null);
				}
			}

			entity.removeColumn(column);
		} else if (elementType.equals("foreign-key")) {
			ForeignKey foreign = new ForeignKey();

			foreign.setConstraintName(attributes.getValue("name"));
			foreign.setReferencedEntity(attributes.getValue("referenced-table"));

			((Entity) currentObject).addForeignKey(foreign);

			return foreign;
		} else if (elementType.equals("foreign-key-column")) {
			ForeignColumn refColumn = new ForeignColumn();

			refColumn.setColumnName(attributes.getValue("column-name"));
			refColumn.setReferencedColumn(attributes.getValue("referenced-key-column-name"));

			((ForeignKey) currentObject).addForeignColumn(refColumn);
		} else if (elementType.equals("index")) {
			Index index = new Index();

			index.setName(attributes.getValue("name"));
			index.setUnique(Boolean.valueOf(attributes.getValue("unique")).booleanValue());

			((Entity) currentObject).addIndex(index);

			return index;
		} else if (elementType.equals("index-column")) {
			IndexColumn indexColumn = new IndexColumn();

			indexColumn.setColumnName(attributes.getValue("name"));

			((Index) currentObject).addIndexColumn(indexColumn);
		}

		return null;
	}

	public void processPCDATA(String elementType, Object currentObject, String value) {
		if (elementType.equals("database-model-description")) {
			if (!TextUtil.isEmptyString(value)) {
				Model obj = (Model) currentObject;

				obj.setDescription(value);
			}
		} else if (elementType.equals("table-description")) {
			if (!TextUtil.isEmptyString(value)) {
				Entity obj = (Entity) currentObject;

				obj.setDescription(value);
			}
		}
	}

	public void error(SAXParseException error) throws SAXException {
		throw new SAXException(error);
	}
}
