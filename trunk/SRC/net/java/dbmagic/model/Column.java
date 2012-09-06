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


public class Column {
	private String name = null;
	private String javaType = null;
	private int maxSize = 0;
	private int decimalDigits = 0;
	private boolean nullable = true;
	private boolean fixedSize = false;
	private Entity entity = null;
	private PrimaryKey primaryKey = null;

	public int getMaxSize() {
		return maxSize;
	}

	public String getName() {
		return name;
	}

	public String getJavaType() {
		return javaType;
	}

	public void setMaxSize(int i) {
		maxSize = i;
	}

	public void setName(String string) {
		name = string;
	}

	public void setJavaType(String string) {
		javaType = string;
	}

	public boolean isNullable() {
		return nullable;
	}

	public void setNullable(boolean b) {
		nullable = b;
	}

	public Entity getEntity() {
		return entity;
	}

	public void setEntity(Entity entity) {
		this.entity = entity;
	}

	public PrimaryKey getPrimaryKey() {
		return primaryKey;
	}

	public boolean isFixedSize() {
		return fixedSize;
	}
	
	public void setFixedSize(boolean fixedSize) {
		this.fixedSize = fixedSize;
	}

	public void setPrimaryKey(PrimaryKey key) {
		primaryKey = key;
	}

	public int getDecimalDigits() {
		return decimalDigits;
	}

	public void setDecimalDigits(int i) {
		decimalDigits = i;
	}

	public static boolean isValidJavaType(String value) {
		if (value.equals("byte")) {
			return true;
		} else if (value.equals("short")) {
			return true;
		} else if (value.equals("int")) {
			return true;
		} else if (value.equals("long")) {
			return true;
		} else if (value.equals("float")) {
			return true;
		} else if (value.equals("double")) {
			return true;
		} else if (value.equals("java.math.BigDecimal")) {
			return true;
		} else if (value.equals("boolean")) {
			return true;
		} else if (value.equals("String")) {
			return true;
		} else if (value.equals("byte[]")) {
			return true;
		} else if (value.equals("java.sql.Date")) {
			return true;
		} else if (value.equals("java.sql.Time")) {
			return true;
		} else if (value.equals("java.sql.Timestamp")) {
			return true;
		}
		
		return false;
	}

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		
		if (!(obj instanceof Column)) {
			return false;
		}
		
		Column other = (Column) obj;
		
		return getName().equals(other.getName());
	}
}
