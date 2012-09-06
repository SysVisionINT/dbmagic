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
package net.java.dbmagic.db;

import java.util.Iterator;

import net.java.dbmagic.model.Column;
import net.java.dbmagic.model.Entity;
import net.java.dbmagic.model.ForeignColumn;
import net.java.dbmagic.model.ForeignKey;
import net.java.dbmagic.model.KeyGenerator;


public class InformixDialect extends SQLCreater {
	
	public String getForeignKey(ForeignKey fk) {
		StringBuffer out = new StringBuffer();

		ForeignColumn fkC = null;

		out.append("ALTER TABLE ");
		out.append(fk.getEntity().getName());
		out.append(" ADD CONSTRAINT FOREIGN KEY (");

		for (Iterator iC = fk.getForeignColumnList().iterator(); iC.hasNext();) {
			fkC = (ForeignColumn) iC.next();

			out.append(fkC.getColumnName());

			if (iC.hasNext()) {
				out.append(", ");
			}
		}

		out.append(") REFERENCES ");
		out.append(fk.getReferencedEntity());
		out.append(" (");

		for (Iterator iC = fk.getForeignColumnList().iterator(); iC.hasNext();) {
			fkC = (ForeignColumn) iC.next();

			out.append(fkC.getReferencedColumn());

			if (iC.hasNext()) {
				out.append(", ");
			}
		}

		out.append(")");

		return out.toString();
	}
	

	public String translateType(Column column) {
		if (column.getPrimaryKey() != null) {
			if (column.getPrimaryKey().getKeyGenerator() != null && column.getPrimaryKey().getKeyGenerator().getType().equals(KeyGenerator.NATIVE)){
				return "SERIAL";
			}
		}
		
		if (column.getJavaType().equals("byte")) {
			return "SMALLINT";
		} else if (column.getJavaType().equals("short")) {
			return "SMALLINT";
		} else if (column.getJavaType().equals("int")) {
			return "INTEGER";
		} else if (column.getJavaType().equals("long")) {
			return "INT8";
		} else if (column.getJavaType().equals("float")) {
			return "SMALLFLOAT";
		} else if (column.getJavaType().equals("double")) {
			return "FLOAT";
		} else if (column.getJavaType().equals("java.math.BigDecimal")) {
			return "DECIMAL(" + column.getMaxSize() + "," + column.getDecimalDigits() + ")";
		} else if (column.getJavaType().equals("boolean")) {
			return "SMALLINT";
		} else if (column.getJavaType().equals("String")) {
			if (column.getMaxSize() == 1 || column.isFixedSize()) {
				return "CHAR(" + column.getMaxSize() + ")";
			} else if (column.getMaxSize() > 1 && column.getMaxSize() < 256) {
				return "VARCHAR(" + column.getMaxSize() + ")";
			} else if (column.getMaxSize() > 255 && column.getMaxSize() < 32739) {
				return "LVARCHAR(" + column.getMaxSize() + ")";
			} else {
				return "TEXT";
			}
		} else if (column.getJavaType().equals("byte[]")) {
			return "BYTE";
		} else if (column.getJavaType().equals("java.sql.Date")) {
			return "DATE";
		} else if (column.getJavaType().equals("java.sql.Time")) {
			return "DATETIME hour TO second";
		} else if (column.getJavaType().equals("java.sql.Timestamp")) {
			return "DATETIME year TO fraction(3)";
		}

		return "UNKNOWN";
	}

	public String getSqlSeparater() {
		return ";";
	}

	public boolean validName(String name) {
		return name.length() <= 18;
	}
	
	public String getCreateTableSufix(Entity table) {
		return " LOCK MODE ROW";
	}	
}
