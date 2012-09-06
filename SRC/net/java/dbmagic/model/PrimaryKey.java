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

public class PrimaryKey {
	private Entity entity = null;

	private String name = null;

	private KeyGenerator keyGenerator = null;

	private List columnList = new ArrayList();

	public Entity getEntity() {
		return entity;
	}

	public void setEntity(Entity entity) {
		this.entity = entity;
	}

	public void addColumn(Column column) {
		column.setPrimaryKey(this);
		column.setNullable(false);

		int pos = columnList.indexOf(column);

		if (pos == -1) {
			columnList.add(column);
		} else {
			columnList.set(pos, column);
		}
	}

	public List getColumnList() {
		return columnList;
	}

	public Column getColumn(String name) {
		Column ret = null;

		for (Iterator i = columnList.iterator(); i.hasNext();) {
			ret = (Column) i.next();

			if (ret.getName().equals(name)) {
				return ret;
			}
		}

		return null;
	}

	public KeyGenerator getKeyGenerator() {
		return keyGenerator;
	}

	public void setKeyGenerator(KeyGenerator generator) {
		if (generator != null) {
			generator.setPrimaryKey(this);
		}
		
		keyGenerator = generator;
	}

	public String getName() {
		return name;
	}

	public void setName(String string) {
		name = string;
	}

	public void validate() throws Exception {
		if (columnList.isEmpty()) {
			throw new Exception("No column on PrimaryKey");
		}

		if (keyGenerator != null) {
			keyGenerator.validate();
		}
	}

}
