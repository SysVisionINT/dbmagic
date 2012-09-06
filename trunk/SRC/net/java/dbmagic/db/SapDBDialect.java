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

public class SapDBDialect extends SQLCreater {

	public String translateType(Column column) {
		if (column.getJavaType().equals("byte")) {
			return "SMALLINT";
		} else if (column.getJavaType().equals("short")) {
			return "SMALLINT";
		} else if (column.getJavaType().equals("int")) {
			return "INTEGER";
		} else if (column.getJavaType().equals("long")) {
			return "DECIMAL(20)";
		} else if (column.getJavaType().equals("float")) {
			return "FLOAT";
		} else if (column.getJavaType().equals("double")) {
			return "DOUBLE PRECISION";
		} else if (column.getJavaType().equals("java.math.BigDecimal")) {
			return "DECIMAL(" + column.getMaxSize() + "," + column.getDecimalDigits() + ")";
		} else if (column.getJavaType().equals("boolean")) {
			return "BOOLEAN";
		} else if (column.getJavaType().equals("String")) {
			if (column.getMaxSize() == 1 || column.isFixedSize()) {
				return "CHAR(" + column.getMaxSize() + ")";
			} else if (column.getMaxSize() > 1 && column.getMaxSize() < 4001) {
				return "VARCHAR(" + column.getMaxSize() + ")";
			} else {
				return "LONG";
			}
		} else if (column.getJavaType().equals("byte[]")) {
			return "LONG";
		} else if (column.getJavaType().equals("java.sql.Date")) {
			return "DATE";
		} else if (column.getJavaType().equals("java.sql.Time")) {
			return "TIME";
		} else if (column.getJavaType().equals("java.sql.Timestamp")) {
			return "TIMESTAMP";
		}

		return "UNKNOWN";
	}

	public String getSqlSeparater() {
		return "\n//";
	}
}
