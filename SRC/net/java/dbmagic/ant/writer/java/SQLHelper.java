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
package net.java.dbmagic.ant.writer.java;

import java.util.Iterator;

import net.java.dbmagic.model.Column;
import net.java.dbmagic.model.Entity;
import net.java.dbmagic.model.Finder;
import net.java.dbmagic.model.ForeignColumn;
import net.java.dbmagic.model.ForeignKey;
import net.java.dbmagic.model.KeyGenerator;
import net.java.dbmagic.model.Operation;
import net.java.dbmagic.model.Property;
import net.java.dbmagic.model.Query;
import net.java.sjtools.db.sql.SQLUtil;
import net.java.sjtools.util.TextUtil;

public class SQLHelper {
	public String getDelete(Entity entity) {
		StringBuffer buffer = new StringBuffer();

		buffer.append("DELETE FROM ");
		buffer.append(entity.getName());
		buffer.append(" WHERE ");

		buffer.append(getPrimaryKeyRestriction(entity));

		if (entity.getVersionControlColumn() != null) {
			buffer.append(" AND ");
			buffer.append(entity.getVersionControlColumn().getColumn().getName());
			buffer.append("=?");
		}

		return buffer.toString();
	}

	private String getPrimaryKeyRestriction(Entity entity) {
		StringBuffer buffer = new StringBuffer();
		Column column = null;
		int count = 0;

		for (Iterator i = entity.getPrimaryKey().getColumnList().iterator(); i.hasNext();) {
			column = (Column) i.next();

			if (count != 0) {
				buffer.append(" AND ");
			}

			buffer.append(column.getName());
			buffer.append("=?");

			count++;
		}

		return buffer.toString();
	}

	public String getSelectByPK(Entity entity) {
		StringBuffer buffer = new StringBuffer();

		buffer.append(getSelect(entity, false));
		buffer.append(" WHERE ");

		buffer.append(getPrimaryKeyRestriction(entity));

		return buffer.toString();
	}

	public String getExists(Entity entity) {
		StringBuffer buffer = new StringBuffer();

		buffer.append(getSelectCount(entity));
		buffer.append(" WHERE ");

		buffer.append(getPrimaryKeyRestriction(entity));

		return buffer.toString();
	}

	public String getSelect(Entity entity, boolean distinct) {
		StringBuffer buffer = new StringBuffer();

		if (!TextUtil.isEmptyString(entity.getSql())) {
			buffer.append(entity.getSql());
		} else {
			buffer.append("SELECT");

			if (distinct) {
				buffer.append(" DISTINCT");
			}

			buffer.append(" * FROM ");
			buffer.append(entity.getName());
		}

		return buffer.toString();
	}

	public String getSelectAll(Entity entity) {
		return getSelect(entity, false);
	}

	public String getSelectCount(Entity entity) {
		StringBuffer buffer = new StringBuffer();

		buffer.append("SELECT count(*) FROM ");

		if (!TextUtil.isEmptyString(entity.getSql())) {
			int pos = entity.getSql().toUpperCase().indexOf("FROM");

			buffer.append(entity.getSql().substring(pos + 4));
		} else {
			buffer.append(entity.getName());
		}

		return buffer.toString();
	}

	public String getSelectByFK(ForeignKey fk) {
		StringBuffer buffer = new StringBuffer();

		buffer.append(getSelect(fk.getEntity(), false));
		buffer.append(" WHERE ");

		ForeignColumn foreignColumn = null;
		int count = 0;

		for (Iterator i = fk.getForeignColumnList().iterator(); i.hasNext();) {
			foreignColumn = (ForeignColumn) i.next();

			if (count != 0) {
				buffer.append(" AND ");
			}

			buffer.append(foreignColumn.getColumnName());
			buffer.append("=?");

			count++;
		}

		return buffer.toString();
	}

	public String getSelectForQuery(Query query) {
		return SQLUtil.cleanSQL(query.getSql().trim());
	}

	public String getSelectCountForQuery(Query query) {
		return SQLUtil.getSelectCount(getSelectForQuery(query), null);
	}

	public String getSelectForFinder(Finder finder) {
		if (finder.getWhere().trim().toUpperCase().startsWith("SELECT")) {
			return SQLUtil.cleanSQL(finder.getWhere().trim());
		}

		StringBuffer buffer = new StringBuffer();

		String sql = getSelect(finder.getEntity(), finder.isDistinct());

		buffer.append(sql);

		if (!finder.getWhere().trim().toUpperCase().startsWith("WHERE") && !finder.getWhere().trim().toUpperCase().startsWith("AND") && !finder.getWhere().trim().toUpperCase().startsWith("ORDER")) {
			if (hasWhere(sql)) {
				buffer.append(" AND");
			} else {
				buffer.append(" WHERE");
			}
		}

		buffer.append(" ");
		buffer.append(finder.getWhere());

		return SQLUtil.cleanSQL(buffer.toString());
	}

	public String getTableGeneratorInsert(Entity entity) {
		StringBuffer buffer = new StringBuffer();

		KeyGenerator generator = entity.getPrimaryKey().getKeyGenerator();

		buffer.append("INSERT INTO ");
		buffer.append(generator.getProperty(Property.TABLE_NAME).getValue());
		buffer.append(" (");
		buffer.append(generator.getProperty(Property.NAME_COLUMN).getValue());
		buffer.append(",");
		buffer.append(generator.getProperty(Property.VALUE_COLUMN).getValue());
		buffer.append(") VALUES ('");
		buffer.append(entity.getName());
		buffer.append("',1)");

		return buffer.toString();
	}

	public String getSelectKeyGenerator(Entity entity) {
		StringBuffer buffer = new StringBuffer();

		KeyGenerator generator = entity.getPrimaryKey().getKeyGenerator();

		buffer.append("SELECT ");

		if (generator.getType().equals(KeyGenerator.NATIVE)) {
			buffer.append("MAX(");
			buffer.append(generator.getColumn().getName());
			buffer.append(") FROM ");
			buffer.append(entity.getName());
			buffer.append(" WHERE 1=1 ");
		} else if (generator.getType().equals(KeyGenerator.MAX)) {
			buffer.append("MAX(");
			buffer.append(generator.getColumn().getName());
			buffer.append(") FROM ");
			buffer.append(entity.getName());

			if (entity.getPrimaryKey().getColumnList().size() > 1) {
				buffer.append(" WHERE ");

				Column column = null;
				int count = 0;

				for (Iterator i = generator.getMaxColumnList().iterator(); i.hasNext();) {
					column = (Column) i.next();

					if (count != 0) {
						buffer.append(" AND ");
					}

					buffer.append(column.getName());
					buffer.append("=?");

					count++;
				}
			}
		} else if (generator.getType().equals(KeyGenerator.SEQUENCE)) {
			buffer.append(generator.getProperty(Property.SEQUENCE_NAME).getValue());
			buffer.append(".nextval FROM dual");
		} else if (generator.getType().equals(KeyGenerator.TABLE)) {
			buffer.append(generator.getProperty(Property.VALUE_COLUMN).getValue());
			buffer.append(" FROM ");
			buffer.append(generator.getProperty(Property.TABLE_NAME).getValue());
			buffer.append(" WHERE ");
			buffer.append(generator.getProperty(Property.NAME_COLUMN).getValue());
			buffer.append(" = '");
			buffer.append(entity.getName());
			buffer.append("'");
		}

		return buffer.toString();
	}

	private boolean hasWhere(String sql) {
		String tmp = sql.toUpperCase();
		tmp.replaceAll("\n\r", "  ");

		int pos = -1;
		int sPos = 0;
		int ePos = 0;
		int count = 0;

		while ((pos = tmp.indexOf(" WHERE ", pos + 1)) != -1) {
			sPos = -1;
			ePos = -1;

			count = 0;

			for (int i = pos; i >= 0; i--) {
				if (tmp.charAt(i) == '(') {
					if (count == 0) {
						sPos = i;
						break;
					} else {
						count--;
					}
				} else if (tmp.charAt(i) == ')') {
					count++;
				}
			}

			for (int i = pos; i < tmp.length(); i++) {
				if (tmp.charAt(i) == '(') {
					count++;
				} else if (tmp.charAt(i) == ')') {
					if (count == 0) {
						ePos = i;
						break;
					} else {
						count--;
					}
				}
			}

			if (sPos == -1 && ePos == -1) {
				return true;
			}
		}

		return false;
	}

	public String getOperationSQL(Operation operation) {
		return SQLUtil.cleanSQL(operation.getSql().trim());
	}
}
