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

public class ReturnObject {
	private String columnName = null;
	private String columnPosition = null;	
	private String className = null;
	private List mapList = new ArrayList();
	
	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getColumnPosition() {
		return columnPosition;
	}

	public void setColumnPosition(String columnPosition) {
		this.columnPosition = columnPosition;
	}

	public String getClassName() {
		return className;
	}
	
	public void setClassName(String className) {
		this.className = className;
	}
	
	public void addFieldMap(FieldMap fieldMap) {
		mapList.add(fieldMap);
	}

	public List getFieldMapList() {
		return mapList;
	}

	public void validate() throws Exception {
		if (columnName != null && columnPosition != null) {
			throw new Exception("Must use only one of: column-name, column-position or field-map (this one can have more than one value)");
		}
		
		if (columnName != null && mapList.size() > 0) {
			throw new Exception("Must use only one of: column-name, column-position or field-map (this one can have more than one value)");
		}
		
		if (columnPosition != null && mapList.size() > 0) {
			throw new Exception("Must use only one of: column-name, column-position or field-map (this one can have more than one value)");
		}
		
		if (columnName == null && columnPosition == null && mapList.size() == 0) {
			throw new Exception("One of the tags must be use: column-name, column-position or field-map (this one can have more than one value)");
		}
		
		FieldMap fieldMap = null;
		
		for (Iterator i = mapList.iterator(); i.hasNext();) {
			fieldMap = (FieldMap) i.next();
			
			fieldMap.validate();
		}
	}
}
