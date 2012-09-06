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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class KeyGenerator {
	public static final String MAX = "max";
	public static final String NATIVE = "native";
	public static final String TABLE = "table";
	public static final String SEQUENCE = "sequence";

	private String type = NATIVE;
	private PrimaryKey primaryKey = null;
	private List propertyList = new ArrayList();

	public String getType() {
		return type;
	}

	public void setType(String string) {
		type = string;
	}

	public PrimaryKey getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(PrimaryKey key) {
		primaryKey = key;
	}

	public void addProperty(Property property) {
		property.setKeyGenerator(this);
		propertyList.add(property);
	}

	public List getPropertyList() {
		return propertyList;
	}

	public Property getProperty(String name) {
		Property ret = null;

		for (Iterator i = propertyList.iterator(); i.hasNext();) {
			ret = (Property) i.next();

			if (ret.getName().equals(name)) {
				return ret;
			}
		}

		return null;
	}

	public static boolean isValidType(String type) {
		if (type.equals(MAX)) {
			return true;
		} else if (type.equals(NATIVE)) {
			return true;
		} else if (type.equals(TABLE)) {
			return true;
		} else if (type.equals(SEQUENCE)) {
			return true;
		}

		return false;
	}

	public Column getColumn() {
		Column column = (Column) getPrimaryKey().getColumnList().get(0);

		if (getType().equals(KeyGenerator.MAX)) {
			column = getPrimaryKey().getEntity().getColumn(getProperty(Property.COLUMN_NAME).getValue());
		}

		return column;
	}

	public List getMaxColumnList() {
		List ret = new ArrayList();

		if (!getType().equals(KeyGenerator.MAX)) {
			return ret;
		}

		Column column = null;
		String name = getProperty(Property.COLUMN_NAME).getValue();

		for (Iterator i = getPrimaryKey().getColumnList().iterator(); i.hasNext();) {
			column = (Column) i.next();

			if (!column.getName().equals(name)) {
				ret.add(column);
			}
		}

		return ret;
	}

	public boolean isValidProperty(Property property) {
		if (property.getName().equals(Property.COLUMN_NAME) && type.equals(KeyGenerator.MAX)) {
			return true;
		} 
		
		if (property.getName().equals(Property.NAME_COLUMN) && type.equals(KeyGenerator.TABLE)) {
			return true;
		} 
		
		if (property.getName().equals(Property.SEQUENCE_NAME) && type.equals(KeyGenerator.SEQUENCE)) {
			return true;
		} 
		
		if (property.getName().equals(Property.TABLE_NAME) && type.equals(KeyGenerator.TABLE)) {
			return true;
		} 
		
		if (property.getName().equals(Property.VALUE_COLUMN) && type.equals(KeyGenerator.TABLE)) {
			return true;
		} 
		
		return false;
	}

	public void validate() throws Exception {
		Property property = null;
		
		if (type.equals(MAX)) {
			property = getProperty(Property.COLUMN_NAME);
			
			if (property == null) {
				throw new Exception("Property " + Property.COLUMN_NAME + " not found");
			}
			
			if (primaryKey.getEntity().getColumn(property.getValue())== null) {
				throw new Exception("Column indicated by property " + Property.COLUMN_NAME + " not found");
			}
		} else if (type.equals(NATIVE)) {
			if (primaryKey.getColumnList().size() != 1) {
				throw new Exception("PrimaryKey must have only one column to use KeyGenerator " + NATIVE);
			}
		} else if (type.equals(TABLE)) {
			if (primaryKey.getColumnList().size() != 1) {
				throw new Exception("PrimaryKey must have only one column to use KeyGenerator " + TABLE);
			}
			
			property = getProperty(Property.TABLE_NAME);
			
			if (property == null) {
				throw new Exception("Property " + Property.TABLE_NAME + " not found");
			}
			
			property = getProperty(Property.NAME_COLUMN);
			
			if (property == null) {
				throw new Exception("Property " + Property.NAME_COLUMN + " not found");
			}
			
			property = getProperty(Property.VALUE_COLUMN);
			
			if (property == null) {
				throw new Exception("Property " + Property.VALUE_COLUMN + " not found");
			}
		} else if (type.equals(SEQUENCE)) {
			if (primaryKey.getColumnList().size() != 1) {
				throw new Exception("PrimaryKey must have only one column to use KeyGenerator " + SEQUENCE);
			}
			
			property = getProperty(Property.SEQUENCE_NAME);
			
			if (property == null) {
				throw new Exception("Property " + Property.SEQUENCE_NAME + " not found");
			}
		}
	}
}
