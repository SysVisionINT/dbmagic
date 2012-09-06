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

public class ForeignKey {
	private int fkID = 0;
	private String referencedEntity = null;
	private boolean oneToOne = false;
	private Entity entity = null;
	private String constraintName = null;
	private List foreignColumnList = new ArrayList();

	public String getReferencedEntity() {
		return referencedEntity;
	}
	
	public String getName() {
		return entity.getName() + "_" + getReferencedEntity();
	}

	public void setReferencedEntity(String string) {
		referencedEntity = string;
	}
	
	public boolean isOneToOne() {
		return oneToOne;
	}

	public void setOneToOne(boolean oneToOne) {
		this.oneToOne = oneToOne;
	}

	public Entity getEntity() {
		return entity;
	}

	public void setEntity(Entity entity) {
		fkID = entity.getForeignKeyList().size() + 1;
		this.entity = entity;
	}
	
	public void addForeignColumn(ForeignColumn refColumn) {
		refColumn.setForeignKey(this);
		foreignColumnList.add(refColumn);
	}
	
	public List getForeignColumnList() {
		return foreignColumnList;
	}
	
	public ForeignColumn getForeignColumn(String name) {
		ForeignColumn ret = null;

		for (Iterator i = foreignColumnList.iterator(); i.hasNext();) {
			ret = (ForeignColumn) i.next();

			if (ret.getColumnName().equals(name)) {
				return ret;
			}
		}

		return null;
	}	

	public Entity getReferencedEntityObject() {
		return getEntity().getModel().getEntity(getReferencedEntity());
	}

	public Object getConstraintName() {
		return constraintName;
	}

	public void setConstraintName(String constraintName) {
		this.constraintName = constraintName;
	}
	
	public int getFkID() {
		return fkID;
	}

	public void validate() throws Exception {
		Entity entityFK = entity.getModel().getEntity(referencedEntity);
		
		if (entityFK == null) {
			throw new Exception("Referenced entity " + referencedEntity + " does not exists");
		}
		
		ForeignColumn foreignColumn = null;

		for (Iterator i = foreignColumnList.iterator(); i.hasNext();) {
			foreignColumn = (ForeignColumn) i.next();
			
			if (entity.getColumn(foreignColumn.getColumnName()) == null) {
				throw new Exception("ForeignKey column " + foreignColumn.getColumnName() + " does not exists on entity");
			}
			
			if (entityFK.getColumn(foreignColumn.getReferencedColumn()) == null) {
				throw new Exception("ForeignKey column " + foreignColumn.getColumnName() + " does not exists on referenced entity " + referencedEntity);
			}			
		}
		
	}
}
