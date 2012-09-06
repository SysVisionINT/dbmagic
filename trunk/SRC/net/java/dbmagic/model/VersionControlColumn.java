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

public class VersionControlColumn {
	private Column column = null;

	public Column getColumn() {
		return column;
	}

	public void setColumn(Column column) {
		this.column = column;
		column.setNullable(false);
	}

	public void validate() throws Exception {
		if (column == null) {
			throw new Exception("No column on VersionControlColumn");
		}
		
		if (column.getJavaType().equals("byte")) {
			return;
		} else if (column.getJavaType().equals("short")) {
			return;
		} else if (column.getJavaType().equals("int")) {
			return;
		} else if (column.getJavaType().equals("long")) {
			return;
		} else if (column.getJavaType().equals("java.math.BigDecimal")) {
			return;
		}
		
		throw new Exception("The VersionControlColumn must be an integer");
	}
}
