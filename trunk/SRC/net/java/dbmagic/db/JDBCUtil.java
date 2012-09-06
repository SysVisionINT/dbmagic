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

import java.sql.Types;

public class JDBCUtil {

	public static String getJavaType(short dataType, int columnSize, int decimalDigits) {
		String ret = "String";

		switch (dataType) {
		case Types.TINYINT:
			ret = "byte";
			break;
		case Types.SMALLINT:
			ret = "short";
			break;
		case Types.INTEGER:
			ret = "int";
			break;
		case Types.BIGINT:
			ret = "long";
			break;
		case Types.REAL:
			ret = "float";
			break;
		case Types.FLOAT:
		case Types.DOUBLE:
			ret = "double";
			break;
		case Types.DECIMAL:
		case Types.NUMERIC:
			if (decimalDigits == 0) {
				if (columnSize <= 3) {
					ret = "byte";
				} else if (columnSize <= 5) {
					ret = "short";
				} else if (columnSize <= 10) {
					ret = "int";
				} else {
					ret = "long";
				}
			} else {
				ret = "java.math.BigDecimal";
			}
			break;
		case Types.BIT:
			ret = "boolean";
			break;
		case Types.CHAR:
		case Types.VARCHAR:
		case Types.LONGVARCHAR:
			ret = "String";
			break;
		case Types.BINARY:
		case Types.VARBINARY:
		case Types.LONGVARBINARY:
			ret = "byte[]";
			break;
		case Types.DATE:
			ret = "java.sql.Date";
			break;
		case Types.TIME:
			ret = "java.sql.Time";
			break;
		case Types.TIMESTAMP:
			ret = "java.sql.Timestamp";
			break;
		}

		return ret;
	}
	
	public static int getMaxSize(short dataType, int columnSize) {
		if (dataType == Types.CHAR || dataType == Types.LONGVARCHAR || dataType == Types.VARCHAR) {
			return columnSize;
		} else {
			return 0;
		}
	}	
}
