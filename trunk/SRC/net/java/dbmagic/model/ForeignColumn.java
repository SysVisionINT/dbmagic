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

public class ForeignColumn {
	private String columnName = null;
	private String referencedColumn = null;
	private ForeignKey foreignKey = null;

	public String getColumnName() {
		return columnName;
	}

	public String getReferencedColumn() {
		return referencedColumn;
	}

	public void setColumnName(String string) {
		columnName = string;
	}

	public void setReferencedColumn(String string) {
		referencedColumn = string;
	}

	public ForeignKey getForeignKey() {
		return foreignKey;
	}

	public void setForeignKey(ForeignKey reference) {
		this.foreignKey = reference;
	}

	public Column getColumnObject() {
		return getForeignKey().getEntity().getColumn(getColumnName());
	}

	public Column getReferencedColumnObject() {
		ForeignKey fk = getForeignKey();
		return fk.getEntity().getModel().getEntity(fk.getReferencedEntity()).getColumn(getReferencedColumn());
	}
}
