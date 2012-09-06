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
package net.java.dbmagic.model;


public class FieldMap {
	private String columnName = null;
	private String columnPosition = null;	
	private String javaType = null;
	private String fieldName = null;
	
	public String getColumnPosition() {
		return columnPosition;
	}

	public void setColumnPosition(String position) {
		this.columnPosition = position;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getColumnName() {
		return columnName;
	}

	public String getJavaType() {
		return javaType;
	}

	public void setColumnName(String name) {
		columnName = name;
	}

	public void setJavaType(String string) {
		javaType = string;
	}

	public static boolean isValidJavaType(String value) {
		if (Column.isValidJavaType(value)) {
			return true;
		} else {
			if (value.equals("Byte")) {
				return true;
			} else if (value.equals("Short")) {
				return true;
			} else if (value.equals("Integer")) {
				return true;
			} else if (value.equals("Long")) {
				return true;
			} else if (value.equals("Float")) {
				return true;
			} else if (value.equals("Double")) {
				return true;
			} else if (value.equals("Boolean")) {
				return true;
			}

			return false;
		}
	}

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (!(obj instanceof FieldMap)) {
			return false;
		}

		FieldMap other = (FieldMap) obj;

		return getFieldName().equals(other.getFieldName());
	}

	public void validate() throws Exception {
		if (columnName != null && columnPosition != null) {
			throw new Exception("Must use only one of: column-name or column-position");
		}
				
		if (columnName == null && columnPosition == null) {
			throw new Exception("One of the tags must be use: column-name or column-position");
		}
	}
}
