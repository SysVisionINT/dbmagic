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
import java.util.List;

import net.java.dbmagic.model.Column;
import net.java.dbmagic.model.Entity;
import net.java.dbmagic.model.FieldMap;
import net.java.dbmagic.model.ForeignColumn;
import net.java.dbmagic.model.ForeignKey;
import net.java.dbmagic.model.Parameter;
import net.java.dbmagic.model.Query;
import net.java.sjtools.util.TextUtil;

public class JavaHelper {
	public long getSerialVersionUID(Entity entity) {
		StringBuffer buffer = new StringBuffer();

		buffer.append(entity.getName());
		buffer.append("{");

		Column column = null;

		for (Iterator i = entity.getAllColumnList().iterator(); i.hasNext();) {
			column = (Column) i.next();

			buffer.append(column.getJavaType());
			buffer.append(" ");
			buffer.append(column.getName());
			buffer.append(";");
		}

		buffer.append("}");

		String text = buffer.toString();
		long ret = 0;
		int power = text.length();

		for (int i = 0; i < text.length(); i++) {
			ret += text.charAt(i) * 31 ^ power;
			power--;
		}

		return ret;
	}

	public String getName(Entity entity) {
		return getJavaName(entity.getName());
	}

	public String getName(Column column) {
		return getJavaName(column.getName());
	}

	public String getInnerName(Column column) {
		StringBuffer buffer = new StringBuffer();

		String tmp = getJavaName(column.getName());

		buffer.append(tmp.substring(0, 1).toLowerCase());

		if (tmp.length() > 1) {
			buffer.append(tmp.substring(1));
		}

		return buffer.toString();
	}

	public String getName(ForeignKey fk) {
		StringBuffer buffer = new StringBuffer();

		buffer.append(getJavaName(fk.getReferencedEntityObject().getName()));

		if (fkCount(fk.getEntity(), fk.getReferencedEntityObject()) > 1) {
			buffer.append("Using");

			boolean first = true;
			ForeignColumn column = null;

			for (Iterator i = fk.getForeignColumnList().iterator(); i.hasNext();) {
				column = (ForeignColumn) i.next();

				if (first) {
					first = false;
				} else {
					buffer.append("And");
				}

				buffer.append(getName(column.getColumnObject()));
			}
		}

		return buffer.toString();
	}

	private int fkCount(Entity entity, Entity referencedEntity) {
		int count = 0;

		ForeignKey foreignKey = null;

		for (Iterator i = entity.getForeignKeyList().iterator(); i.hasNext();) {
			foreignKey = (ForeignKey) i.next();

			if (foreignKey.getReferencedEntity().equals(referencedEntity.getName())) {
				count++;
			}
		}

		return count;
	}

	public String getSetter(Column column) {
		StringBuffer buffer = new StringBuffer();

		String name = getJavaName(column.getName());

		buffer.append("set");
		buffer.append(name);

		return buffer.toString();
	}
	
	public String getSetter(FieldMap field) {
		StringBuffer buffer = new StringBuffer();

		buffer.append("set");
		buffer.append(field.getFieldName().substring(0, 1).toUpperCase());
		
		if (field.getFieldName().length() > 1) {
			buffer.append(field.getFieldName().substring(1));
		}

		return buffer.toString();
	}	

	public String getGetter(Column column) {
		StringBuffer buffer = new StringBuffer();

		String name = getJavaName(column.getName());

		if (column.getJavaType().equals("boolean")) {
			buffer.append("is");
		} else {
			buffer.append("get");
		}

		buffer.append(name);

		return buffer.toString();
	}

	private String getJavaName(String name) {
		StringBuffer buffer = new StringBuffer();
		List nameParts = TextUtil.split(name.toLowerCase(), "_");

		String tmp = null;

		for (Iterator i = nameParts.iterator(); i.hasNext();) {
			tmp = (String) i.next();

			buffer.append(tmp.substring(0, 1).toUpperCase());

			if (tmp.length() > 1) {
				buffer.append(tmp.substring(1));
			}
		}

		return buffer.toString();
	}

	public String getJavaTypeGetter(Column column) {
		if (column.getJavaType().equals("byte")) {
			return "getByte";
		} else if (column.getJavaType().equals("short")) {
			return "getShort";
		} else if (column.getJavaType().equals("int")) {
			return "getInt";
		} else if (column.getJavaType().equals("long")) {
			return "getLong";
		} else if (column.getJavaType().equals("float")) {
			return "getFloat";
		} else if (column.getJavaType().equals("double")) {
			return "getDouble";
		} else if (column.getJavaType().equals("java.math.BigDecimal")) {
			return "getBigDecimal";
		} else if (column.getJavaType().equals("boolean")) {
			return "getBoolean";
		} else if (column.getJavaType().equals("String")) {
			return "getString";
		} else if (column.getJavaType().equals("byte[]")) {
			return "getBytes";
		} else if (column.getJavaType().equals("java.sql.Date")) {
			return "getDate";
		} else if (column.getJavaType().equals("java.sql.Time")) {
			return "getTime";
		} else if (column.getJavaType().equals("java.sql.Timestamp")) {
			return "getTimestamp";
		}

		return "getObject";
	}
	
	public String getJavaTypeGetter(FieldMap field) {
		if (field.getJavaType().equals("byte") || field.getJavaType().equals("Byte")) {
			return "getByte";
		} else if (field.getJavaType().equals("short") || field.getJavaType().equals("Short")) {
			return "getShort";
		} else if (field.getJavaType().equals("int") || field.getJavaType().equals("Integer")) {
			return "getInt";
		} else if (field.getJavaType().equals("long") || field.getJavaType().equals("Long")) {
			return "getLong";
		} else if (field.getJavaType().equals("float") || field.getJavaType().equals("Float")) {
			return "getFloat";
		} else if (field.getJavaType().equals("double") || field.getJavaType().equals("Double")) {
			return "getDouble";
		} else if (field.getJavaType().equals("java.math.BigDecimal")) {
			return "getBigDecimal";
		} else if (field.getJavaType().equals("boolean") || field.getJavaType().equals("Boolean")) {
			return "getBoolean";
		} else if (field.getJavaType().equals("String")) {
			return "getString";
		} else if (field.getJavaType().equals("byte[]")) {
			return "getBytes";
		} else if (field.getJavaType().equals("java.sql.Date")) {
			return "getDate";
		} else if (field.getJavaType().equals("java.sql.Time")) {
			return "getTime";
		} else if (field.getJavaType().equals("java.sql.Timestamp")) {
			return "getTimestamp";
		}

		return "getObject";
	}	
	
	public boolean isUseName(FieldMap field) {
		return TextUtil.isEmptyString(field.getColumnPosition());
	}

	public String getJavaTypeSetter(Parameter parameter) {
		if (parameter.getJavaType().equals("byte") || parameter.getJavaType().equals("Byte")) {
			return "setByte";
		} else if (parameter.getJavaType().equals("short") || parameter.getJavaType().equals("Short")) {
			return "setShort";
		} else if (parameter.getJavaType().equals("int") || parameter.getJavaType().equals("Integer")) {
			return "setInt";
		} else if (parameter.getJavaType().equals("long") || parameter.getJavaType().equals("Long")) {
			return "setLong";
		} else if (parameter.getJavaType().equals("float") || parameter.getJavaType().equals("Float")) {
			return "setFloat";
		} else if (parameter.getJavaType().equals("double") || parameter.getJavaType().equals("Double")) {
			return "setDouble";
		} else if (parameter.getJavaType().equals("java.math.BigDecimal")) {
			return "setBigDecimal";
		} else if (parameter.getJavaType().equals("boolean") || parameter.getJavaType().equals("Boolean")) {
			return "setBoolean";
		} else if (parameter.getJavaType().equals("String")) {
			return "setString";
		} else if (parameter.getJavaType().equals("byte[]")) {
			return "setBytes";
		} else if (parameter.getJavaType().equals("java.sql.Date")) {
			return "setDate";
		} else if (parameter.getJavaType().equals("java.sql.Time")) {
			return "setTime";
		} else if (parameter.getJavaType().equals("java.sql.Timestamp")) {
			return "setTimestamp";
		}

		return "setObject";
	}

	public String getJavaTypeSetter(Column column) {
		if (column.getJavaType().equals("byte")) {
			return "setByte";
		} else if (column.getJavaType().equals("short")) {
			return "setShort";
		} else if (column.getJavaType().equals("int")) {
			return "setInt";
		} else if (column.getJavaType().equals("long")) {
			return "setLong";
		} else if (column.getJavaType().equals("float")) {
			return "setFloat";
		} else if (column.getJavaType().equals("double")) {
			return "setDouble";
		} else if (column.getJavaType().equals("java.math.BigDecimal")) {
			return "setBigDecimal";
		} else if (column.getJavaType().equals("boolean")) {
			return "setBoolean";
		} else if (column.getJavaType().equals("String")) {
			return "setString";
		} else if (column.getJavaType().equals("byte[]")) {
			return "setBytes";
		} else if (column.getJavaType().equals("java.sql.Date")) {
			return "setDate";
		} else if (column.getJavaType().equals("java.sql.Time")) {
			return "setTime";
		} else if (column.getJavaType().equals("java.sql.Timestamp")) {
			return "setTimestamp";
		}

		return "setObject";
	}

	public boolean isJavaClass(Column column) {
		if (column.isNullable()) {
			return true;
		}

		if (column.getJavaType().equals("byte")) {
			return false;
		} else if (column.getJavaType().equals("short")) {
			return false;
		} else if (column.getJavaType().equals("int")) {
			return false;
		} else if (column.getJavaType().equals("long")) {
			return false;
		} else if (column.getJavaType().equals("float")) {
			return false;
		} else if (column.getJavaType().equals("double")) {
			return false;
		} else if (column.getJavaType().equals("java.math.BigDecimal")) {
			return true;
		} else if (column.getJavaType().equals("boolean")) {
			return false;
		} else if (column.getJavaType().equals("String")) {
			return true;
		} else if (column.getJavaType().equals("byte[]")) {
			return true;
		} else if (column.getJavaType().equals("java.sql.Date")) {
			return true;
		} else if (column.getJavaType().equals("java.sql.Time")) {
			return true;
		} else if (column.getJavaType().equals("java.sql.Timestamp")) {
			return true;
		}

		return false;
	}

	public boolean canCopy(Column column) {
		if (column.isNullable() && !column.getJavaType().equals("String")) {
			return false;
		}

		if (column.getJavaType().equals("byte")) {
			return true;
		} else if (column.getJavaType().equals("short")) {
			return true;
		} else if (column.getJavaType().equals("int")) {
			return true;
		} else if (column.getJavaType().equals("long")) {
			return true;
		} else if (column.getJavaType().equals("float")) {
			return true;
		} else if (column.getJavaType().equals("double")) {
			return true;
		} else if (column.getJavaType().equals("java.math.BigDecimal")) {
			return false;
		} else if (column.getJavaType().equals("boolean")) {
			return true;
		} else if (column.getJavaType().equals("String")) {
			return true;
		} else if (column.getJavaType().equals("byte[]")) {
			return false;
		} else if (column.getJavaType().equals("java.sql.Date")) {
			return false;
		} else if (column.getJavaType().equals("java.sql.Time")) {
			return false;
		} else if (column.getJavaType().equals("java.sql.Timestamp")) {
			return false;
		}

		return false;
	}

	public boolean isArray(Column column) {
		return column.getJavaType().equals("byte[]");
	}

	public String getArrayJavaType(Column column) {
		if (column.getJavaType().equals("byte[]")) {
			return "byte";
		}

		return "";
	}

	public String getCopyOf(Column column) {
		StringBuffer buffer = new StringBuffer();

		if (column.getJavaType().equals("java.math.BigDecimal")) {
			buffer.append("new java.math.BigDecimal(");
			buffer.append(getInnerName(column));
			buffer.append(".toString())");
		} else if (column.getJavaType().equals("java.sql.Date")) {
			buffer.append("new java.sql.Date(");
			buffer.append(getInnerName(column));
			buffer.append(".getTime())");
		} else if (column.getJavaType().equals("java.sql.Time")) {
			buffer.append("new java.sql.Time(");
			buffer.append(getInnerName(column));
			buffer.append(".getTime())");
		} else if (column.getJavaType().equals("java.sql.Timestamp")) {
			buffer.append("new java.sql.Timestamp(");
			buffer.append(getInnerName(column));
			buffer.append(".getTime())");
		} else {
			if (column.isNullable()) {
				if (column.getJavaType().equals("byte")) {
					buffer.append("new Byte(");
					buffer.append(getInnerName(column));
					buffer.append(primitiveGetter(column));
					buffer.append(")");
				} else if (column.getJavaType().equals("short")) {
					buffer.append("new Short(");
					buffer.append(getInnerName(column));
					buffer.append(primitiveGetter(column));
					buffer.append(")");
				} else if (column.getJavaType().equals("int")) {
					buffer.append("new Integer(");
					buffer.append(getInnerName(column));
					buffer.append(primitiveGetter(column));
					buffer.append(")");
				} else if (column.getJavaType().equals("long")) {
					buffer.append("new Long(");
					buffer.append(getInnerName(column));
					buffer.append(primitiveGetter(column));
					buffer.append(")");
				} else if (column.getJavaType().equals("float")) {
					buffer.append("new Float(");
					buffer.append(getInnerName(column));
					buffer.append(primitiveGetter(column));
					buffer.append(")");
				} else if (column.getJavaType().equals("double")) {
					buffer.append("new Double(");
					buffer.append(getInnerName(column));
					buffer.append(primitiveGetter(column));
					buffer.append(")");
				} else if (column.getJavaType().equals("boolean")) {
					buffer.append("new Boolean(");
					buffer.append(getInnerName(column));
					buffer.append(primitiveGetter(column));
					buffer.append(")");
				}
			} else {
				buffer.append(getInnerName(column));
			}
		}

		return buffer.toString();
	}

	public boolean isValidGeneratorNativeColumn(Column column) {
		if (column.getJavaType().equals("byte")) {
			return true;
		} else if (column.getJavaType().equals("short")) {
			return true;
		} else if (column.getJavaType().equals("int")) {
			return true;
		} else if (column.getJavaType().equals("long")) {
			return true;
		} else if (column.getJavaType().equals("float")) {
			return true;
		} else if (column.getJavaType().equals("double")) {
			return true;
		} else if (column.getJavaType().equals("java.math.BigDecimal")) {
			return true;
		} else if (column.getJavaType().equals("boolean")) {
			return true;
		} else if (column.getJavaType().equals("String")) {
			return true;
		} else if (column.getJavaType().equals("byte[]")) {
			return false;
		} else if (column.getJavaType().equals("java.sql.Date")) {
			return true;
		} else if (column.getJavaType().equals("java.sql.Time")) {
			return true;
		} else if (column.getJavaType().equals("java.sql.Timestamp")) {
			return true;
		}

		return false;
	}

	public String getJavaType(Column column) {
		String ret = column.getJavaType();

		if (column.isNullable()) {
			if (column.getJavaType().equals("byte")) {
				ret = "Byte";
			} else if (column.getJavaType().equals("short")) {
				ret = "Short";
			} else if (column.getJavaType().equals("int")) {
				ret = "Integer";
			} else if (column.getJavaType().equals("long")) {
				ret = "Long";
			} else if (column.getJavaType().equals("float")) {
				ret = "Float";
			} else if (column.getJavaType().equals("double")) {
				ret = "Double";
			} else if (column.getJavaType().equals("boolean")) {
				ret = "Boolean";
			}
		}

		return ret;
	}

	public boolean isWrapperClass(Column column) {

		if (column.isNullable()) {
			if (column.getJavaType().equals("byte")) {
				return true;
			} else if (column.getJavaType().equals("short")) {
				return true;
			} else if (column.getJavaType().equals("int")) {
				return true;
			} else if (column.getJavaType().equals("long")) {
				return true;
			} else if (column.getJavaType().equals("float")) {
				return true;
			} else if (column.getJavaType().equals("double")) {
				return true;
			} else if (column.getJavaType().equals("boolean")) {
				return true;
			}
		}

		return false;
	}
	
	public boolean isWrapperClass(FieldMap field) {

		if (field.getJavaType().equals("Byte")) {
			return true;
		} else if (field.getJavaType().equals("Short")) {
			return true;
		} else if (field.getJavaType().equals("Integer")) {
			return true;
		} else if (field.getJavaType().equals("Long")) {
			return true;
		} else if (field.getJavaType().equals("Float")) {
			return true;
		} else if (field.getJavaType().equals("Double")) {
			return true;
		} else if (field.getJavaType().equals("Boolean")) {
			return true;
		}

		return false;
	}	

	public boolean isWrapperClass(Parameter parameter) {

		if (parameter.getJavaType().equals("Byte")) {
			return true;
		} else if (parameter.getJavaType().equals("Short")) {
			return true;
		} else if (parameter.getJavaType().equals("Integer")) {
			return true;
		} else if (parameter.getJavaType().equals("Long")) {
			return true;
		} else if (parameter.getJavaType().equals("Float")) {
			return true;
		} else if (parameter.getJavaType().equals("Double")) {
			return true;
		} else if (parameter.getJavaType().equals("Boolean")) {
			return true;
		}

		return false;
	}

	public String primitiveGetter(Column column) {

		if (column.isNullable()) {
			if (column.getJavaType().equals("byte")) {
				return ".byteValue()";
			} else if (column.getJavaType().equals("short")) {
				return ".shortValue()";
			} else if (column.getJavaType().equals("int")) {
				return ".intValue()";
			} else if (column.getJavaType().equals("long")) {
				return ".longValue()";
			} else if (column.getJavaType().equals("float")) {
				return ".floatValue()";
			} else if (column.getJavaType().equals("double")) {
				return ".doubleValue()";
			} else if (column.getJavaType().equals("boolean")) {
				return ".booleanValue()";
			}
		}

		return "";
	}

	public String primitiveGetter(Parameter parameter) {

		if (parameter.getJavaType().equals("Byte")) {
			return ".byteValue()";
		} else if (parameter.getJavaType().equals("Short")) {
			return ".shortValue()";
		} else if (parameter.getJavaType().equals("Integer")) {
			return ".intValue()";
		} else if (parameter.getJavaType().equals("Long")) {
			return ".longValue()";
		} else if (parameter.getJavaType().equals("Float")) {
			return ".floatValue()";
		} else if (parameter.getJavaType().equals("Double")) {
			return ".doubleValue()";
		} else if (parameter.getJavaType().equals("Boolean")) {
			return ".booleanValue()";
		}

		return "";
	}

	public String getSQLType(Parameter parameter) {

		if (parameter.getJavaType().equals("byte") || parameter.getJavaType().equals("Byte")) {
			return "TINYINT";
		} else if (parameter.getJavaType().equals("short") || parameter.getJavaType().equals("Short")) {
			return "SMALLINT";
		} else if (parameter.getJavaType().equals("int") || parameter.getJavaType().equals("Integer")) {
			return "INTEGER";
		} else if (parameter.getJavaType().equals("long") || parameter.getJavaType().equals("Long")) {
			return "BIGINT";
		} else if (parameter.getJavaType().equals("float") || parameter.getJavaType().equals("Float")) {
			return "REAL";
		} else if (parameter.getJavaType().equals("double") || parameter.getJavaType().equals("Double")) {
			return "DOUBLE";
		} else if (parameter.getJavaType().equals("boolean") || parameter.getJavaType().equals("Boolean")) {
			return "BOOLEAN";
		}

		return "VARCHAR";
	}

	public String getSQLType(Column column) {

		if (column.isNullable()) {
			if (column.getJavaType().equals("byte")) {
				return "TINYINT";
			} else if (column.getJavaType().equals("short")) {
				return "SMALLINT";
			} else if (column.getJavaType().equals("int")) {
				return "INTEGER";
			} else if (column.getJavaType().equals("long")) {
				return "BIGINT";
			} else if (column.getJavaType().equals("float")) {
				return "REAL";
			} else if (column.getJavaType().equals("double")) {
				return "DOUBLE";
			} else if (column.getJavaType().equals("boolean")) {
				return "BOOLEAN";
			}
		}

		return "VARCHAR";
	}

	public String getName(Query query) {
		StringBuffer buffer = new StringBuffer();
		
		buffer.append(query.getName().substring(0, 1).toUpperCase());
		
		if (query.getName().length() > 1) {
			buffer.append(query.getName().substring(1));
		}
		
		return buffer.toString();
	}
	
	public String getReturnType(Query query) {
		return query.getReturnObject().getClassName();
	}
	

}
