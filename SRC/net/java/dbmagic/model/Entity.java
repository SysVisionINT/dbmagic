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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.java.sjtools.db.sql.SQLUtil;

public class Entity {
	private String name = null;
	private boolean readOnly = false;
	private String sql = null;
	private String description = null;
	private Model model = null;
	private PrimaryKey primaryKey = null;
	private List columnList = new ArrayList();
	private List foreignKeyList = new ArrayList();
	private List indexList = new ArrayList();
	private List finderList = new ArrayList();
	private List operationList = new ArrayList();
	private VersionControlColumn versionControlColumn = null;

	public VersionControlColumn getVersionControlColumn() {
		return versionControlColumn;
	}

	public void setVersionControlColumn(VersionControlColumn versionControlColumn) {
		this.versionControlColumn = versionControlColumn;
	}

	public String getName() {
		return name;
	}

	public boolean isReadOnly() {
		if (readOnly) {
			return true;
		}
		
		return sql != null;
	}

	public void setName(String string) {
		name = string;
	}

	public void setReadOnly(boolean b) {
		readOnly = b;
	}

	public String getDescription() {
		return description;
	}
	
	public List getDescriptionLines() throws IOException {
		if (getDescription() == null) {
			return null;
		}
		
		List list = new ArrayList();
		
		BufferedReader br = new BufferedReader(new StringReader(getDescription()));
		String line = null;
		
		while ((line = br.readLine()) != null) {
			list.add(line.trim());
		}
		
		return list;
	}

	public void setDescription(String string) {
		description = string;
	}

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}

	public PrimaryKey getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(PrimaryKey key) {
		key.setEntity(this);
		primaryKey = key;
	}

	public void addColumn(Column column) {
		column.setEntity(this);
		columnList.add(column);
	}
	
	public void removeColumn(Column column) {
		columnList.remove(column);
	}

	public List getColumnList() {
		return columnList;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = SQLUtil.cleanSQL(sql);
		setReadOnly(true);
	}

	public Column getColumn(String name) {
		Column ret = null;

		for (Iterator i = getAllColumnList().iterator(); i.hasNext();) {
			ret = (Column) i.next();
			
			if (ret.getName().equals(name)) {
				return ret;
			}
		}

		return null;
	}
	
	public Column getEntityColumn(String name) {
		Column ret = null;

		for (Iterator i = columnList.iterator(); i.hasNext();) {
			ret = (Column) i.next();
			
			if (ret.getName().equals(name)) {
				return ret;
			}
		}

		return null;
	}	

	public void addForeignKey(ForeignKey reference) {
		reference.setEntity(this);
		foreignKeyList.add(reference);
	}

	public List getForeignKeyList() {
		return foreignKeyList;
	}

	public ForeignKey getForeignKey(String name) {
		ForeignKey ret = null;

		for (Iterator i = foreignKeyList.iterator(); i.hasNext();) {
			ret = (ForeignKey) i.next();

			if (ret.getName() != null && ret.getName().equals(name)) {
				return ret;
			}
		}

		return null;
	}

	public void addIndex(Index index) {
		index.setEntity(this);
		indexList.add(index);
	}

	public List getIndexList() {
		return indexList;
	}

	public Index getIndex(String name) {
		Index ret = null;

		for (Iterator i = indexList.iterator(); i.hasNext();) {
			ret = (Index) i.next();

			if (ret.getName().equals(name)) {
				return ret;
			}
		}

		return null;
	}

	public void addFinder(Finder finder) {
		finder.setEntity(this);
		finderList.add(finder);
	}

	public List getFinderList() {
		return finderList;
	}

	public Finder getFinder(String name) {
		Finder ret = null;

		for (Iterator i = finderList.iterator(); i.hasNext();) {
			ret = (Finder) i.next();

			if (ret.getName().equals(name)) {
				return ret;
			}
		}

		return null;
	}
	
	public void addOperation(Operation operation) {
		operation.setEntity(this);
		operationList.add(operation);
	}

	public List getOperationList() {
		return operationList;
	}

	public Operation getOperation(String name) {
		Operation ret = null;

		for (Iterator i = operationList.iterator(); i.hasNext();) {
			ret = (Operation) i.next();

			if (ret.getName().equals(name)) {
				return ret;
			}
		}

		return null;
	}	
	
	public List getImportedKeys() {
		return getForeignKeyList();
	}
	
	public List getExportedKeys() {
		List ret = new ArrayList();
		
		Entity table = null;
		ForeignKey fk = null;
		
		for (Iterator i = getModel().getEntityList().iterator(); i.hasNext();) {
			table = (Entity)i.next();
			
			for (Iterator iFK = table.getForeignKeyList().iterator(); iFK.hasNext();) {
				fk = (ForeignKey)iFK.next();
				
				if (fk.getReferencedEntity().equals(getName())) {
					ret.add(fk);	
				}
			}
		}
		
		return ret;
	}	
	
	public List getAllColumnList() {
		List list = new ArrayList();
		
		if (primaryKey != null) {
			list.addAll(getPrimaryKey().getColumnList());
		}
		
		list.addAll(getColumnList());
		
		if (versionControlColumn != null) {
			list.add(versionControlColumn.getColumn());
		}
		
		return list;
	}
	
	public boolean equals (Object obj) {
		if (obj == null) {
			return false;
		}
		
		if (!(obj instanceof Entity)) {
			return false;
		}
		
		return ((Entity)obj).getName().equals(getName());
	}

	public void validate() throws Exception {
		if (primaryKey != null && sql != null) {
			throw new Exception("PrimaryKey and SQL are exclusive"); 
		}
		
		if (versionControlColumn != null && sql != null) {
			throw new Exception("VersionControlColumn and SQL are exclusive"); 
		}
		
		if (primaryKey != null) {
			primaryKey.validate();
		}
		
		if (!foreignKeyList.isEmpty()) {
			ForeignKey foreignKey = null;
			
			for (Iterator i = foreignKeyList.iterator(); i.hasNext();){
				foreignKey = (ForeignKey) i.next();
				
				foreignKey.validate();
			}
		}
		
		if (!indexList.isEmpty()) {
			Index index = null;
			
			for (Iterator i = indexList.iterator(); i.hasNext();){
				index = (Index) i.next();
				
				index.validate();
			}
		}	
		
		if (!finderList.isEmpty()) {
			Finder finder = null;
			
			for (Iterator i = finderList.iterator(); i.hasNext();){
				finder = (Finder) i.next();
				
				finder.validate();
			}
		}		
		
		if (!operationList.isEmpty()) {
			Operation operation = null;
			
			for (Iterator i = operationList.iterator(); i.hasNext();){
				operation = (Operation) i.next();
				
				operation.validate();
			}
		}			
		
		if (versionControlColumn != null) {
			versionControlColumn.validate();
		}
	}
}
