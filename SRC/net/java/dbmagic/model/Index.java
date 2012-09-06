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

public class Index {
	private String name = null;
	private boolean unique = false;
	private Entity entity = null;
	private List indexColumnList = new ArrayList();

	public String getName() {
		return name;
	}

	public boolean isUnique() {
		return unique;
	}

	public void setName(String string) {
		name = string;
	}

	public void setUnique(boolean b) {
		unique = b;
	}

	public Entity getEntity() {
		return entity;
	}

	public void setEntity(Entity entity) {
		this.entity = entity;
	}

	public void addIndexColumn(IndexColumn indexColumn) {
		indexColumn.setIndex(this);
		indexColumnList.add(indexColumn);
	}

	public List getIndexColumnList() {
		return indexColumnList;
	}

	public IndexColumn getIndexColumn(String name) {
		IndexColumn ret = null;

		for (Iterator i = indexColumnList.iterator(); i.hasNext();) {
			ret = (IndexColumn) i.next();

			if (ret.getColumnName().equals(name)) {
				return ret;
			}
		}

		return null;
	}

	public void validate() throws Exception {
		IndexColumn indexColumn = null;
		
		for (Iterator i = indexColumnList.iterator(); i.hasNext();) {
			indexColumn = (IndexColumn) i.next();

			if (entity.getColumn(indexColumn.getColumnName()) == null) {
				throw new Exception("Index column " + indexColumn.getColumnName() + " does not exists on entity");
			}
		}
		
	}
}
