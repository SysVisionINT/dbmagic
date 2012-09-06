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

public class Model {
	private String description = null;
	private List entityList = new ArrayList();
	private List queryList = new ArrayList();

	public String getDescription() {
		return description;
	}

	public void setDescription(String string) {
		description = string;
	}

	public void addEntity(Entity table) {
		table.setModel(this);
		entityList.add(table);
	}

	public List getEntityList() {
		return entityList;
	}

	public Entity getEntity(String name) {
		Entity ret = null;

		for (Iterator i = entityList.iterator(); i.hasNext();) {
			ret = (Entity) i.next();

			if (ret.getName().equals(name)) {
				return ret;
			}
		}

		return null;
	}
	
	public void addQuery(Query query) {
		query.setModel(this);
		queryList.add(query);
	}

	public List getQueryList() {
		return queryList;
	}

	public Query getQuery(String name) {
		Query ret = null;

		for (Iterator i = queryList.iterator(); i.hasNext();) {
			ret = (Query) i.next();

			if (ret.getName().equals(name)) {
				return ret;
			}
		}

		return null;
	}

	public List getEntitiesOrderedByDependencies() {
		List all = new ArrayList();
		List ret = new ArrayList();

		all.addAll(getEntityList());

		Entity entity = null;
		ForeignKey fk = null;
		boolean none = true;

		while (!all.isEmpty()) {
			for (Iterator i = all.iterator(); i.hasNext();) {
				entity = (Entity) i.next();

				none = true;

				for (Iterator iFK = entity.getImportedKeys().iterator(); iFK.hasNext();) {
					fk = (ForeignKey) iFK.next();

					if (!(fk.getReferencedEntity().equals(entity.getName())
						|| ret.contains(getEntity(fk.getReferencedEntity())))) {
						none = false;
						break;
					}
				}

				if (none) {
					ret.add(entity);
					i.remove();
				}
			}
		}

		return ret;
	}

	public void validate() throws Exception {
		Entity entity = null;
		
		for (Iterator i = entityList.iterator(); i.hasNext();){
			entity = (Entity) i.next();
			
			entity.validate();
		}
		
		Query query = null;
		
		for (Iterator i = queryList.iterator(); i.hasNext();){
			query = (Query) i.next();
			
			query.validate();
		}		
	}
}
