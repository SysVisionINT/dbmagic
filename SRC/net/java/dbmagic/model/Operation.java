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

public class Operation implements AceptParameters {
	private String name = null;
	private String sql = null;
	private Entity entity = null;
	private List parameterList = new ArrayList();

	public String getName() {
		return name;
	}

	public void setName(String string) {
		name = string;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String string) {
		sql = string;
	}

	public Entity getEntity() {
		return entity;
	}

	public void setEntity(Entity entity) {
		this.entity = entity;
	}

	public void addParameter(Parameter parameter) {
		parameter.setAceptParameters(this);
		parameterList.add(parameter);
	}

	public List getParameterList() {
		return parameterList;
	}
	
	public List getUniqueParameterList() {
		List list = new ArrayList();
		
		Parameter parameter = null;
		
		for (Iterator i = parameterList.iterator(); i.hasNext();) {
			parameter = (Parameter) i.next();
			
			if (!list.contains(parameter)) {
				list.add(parameter);
			}
		}
		
		return list;
	}	

	public Parameter getParameter(String name) {
		Parameter ret = null;

		for (Iterator i = parameterList.iterator(); i.hasNext();) {
			ret = (Parameter) i.next();

			if (ret.getName().equals(name)) {
				return ret;
			}
		}

		return null;
	}

	public void validate() throws Exception {
		if (sql == null) {
			throw new Exception("No sql on operation");
		}
	}
}
