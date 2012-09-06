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

import net.java.dbmagic.model.Column;
import net.java.dbmagic.model.Entity;
import net.java.dbmagic.model.KeyGenerator;
import net.java.dbmagic.model.Property;

public class OracleDialect extends SQLCreater {
	
	public String getCreateTable(Entity table) {
		StringBuffer out = new StringBuffer();
		
		if (table.getPrimaryKey() != null) {
			if (table.getPrimaryKey().getKeyGenerator() != null) {
				if (table.getPrimaryKey().getKeyGenerator().getType().equals(KeyGenerator.SEQUENCE)) {
					out.append("CREATE SEQUENCE ");
					out.append(table.getPrimaryKey().getKeyGenerator().getProperty(Property.SEQUENCE_NAME).getValue());
					out.append(" START WITH 1 INCREMENT BY 1 MAXVALUE 9223372036854775806 CYCLE;");
					
					out.append(LINE);
					out.append(LINE);
				}
			}
		}
		
		out.append(super.getCreateTable(table));
		
		return out.toString();
	}

	public String translateType(Column column) {
		if (column.getJavaType().equals("byte")) {
			return "NUMBER(3)";
		} else if (column.getJavaType().equals("short")) {
			return "NUMBER(5)";
		} else if (column.getJavaType().equals("int")) {
			return "NUMBER(10)";
		} else if (column.getJavaType().equals("long")) {
			return "NUMBER(19)";
		} else if (column.getJavaType().equals("float")) {
			return "NUMBER(38,7)";
		} else if (column.getJavaType().equals("double")) {
			return "NUMBER(38,15)";
		} else if (column.getJavaType().equals("java.math.BigDecimal")) {
			return "NUMBER(" + column.getMaxSize() + "," + column.getDecimalDigits() + ")";
		} else if (column.getJavaType().equals("boolean")) {
			return "NUMBER(1)";
		} else if (column.getJavaType().equals("String")) {
			if (column.getMaxSize() == 1 || column.isFixedSize()) {
				return "CHAR(" + column.getMaxSize() + ")";
			} else if (column.getMaxSize() > 1 && column.getMaxSize() < 4001) {
				return "VARCHAR2(" + column.getMaxSize() + ")";
			} else {
				return "LONG";
			}
		} else if (column.getJavaType().equals("byte[]")) {
			return "LONG RAW";
		} else if (column.getJavaType().equals("java.sql.Date")) {
			return "DATE";
		} else if (column.getJavaType().equals("java.sql.Time")) {
			return "DATE";
		} else if (column.getJavaType().equals("java.sql.Timestamp")) {
			return "DATE";
		}

		return "UNKNOWN";
	}

	public String getSqlSeparater() {
		return ";";
	}
	
	public boolean generateFKIndexs() {
		return true;
	}	
}
