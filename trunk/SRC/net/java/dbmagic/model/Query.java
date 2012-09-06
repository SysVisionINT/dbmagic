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

public class Query implements AceptParameters{

	public static final String OBJECT = "Object";
	public static final String COLLECTION = "Collection";;

	private String name = null;
	private String description = null;
	private String returnType = COLLECTION;
	private String sql = null;
	private Model model = null;
	private ReturnObject returnObject = null;
	private List parameterList = new ArrayList();
	private boolean useFilter = false;
	
	public boolean isUseFilter() {
		return useFilter;
	}
	
	public void setUseFilter(boolean useFilter) {
		this.useFilter = useFilter;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ReturnObject getReturnObject() {
		return returnObject;
	}

	public void setReturnObject(ReturnObject returnObject) {
		this.returnObject = returnObject;
	}

	public String getName() {
		return name;
	}

	public String getReturnType() {
		return returnType;
	}

	public void setName(String string) {
		name = string;
	}

	public static boolean isValidReturnType(String returnType) {
		if (returnType.equals(OBJECT)) {
			return true;
		}
		
		if (returnType.equals(COLLECTION)) {
			return true;
		}
		
		return false;
	}

	public void setReturnType(String string) {
		returnType = string;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String select) {
		sql = select;
	}

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
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

	public void validate() throws Exception {
		if (sql == null) {
			throw new Exception("No SQL on query");
		}
		
		if (returnObject == null) {
			throw new Exception("Return record not defined");
		}
		
		if (useFilter && parameterList.size() > 0) {
			throw new Exception("Can't use filter and parameters at the same time");
		}
		
		returnObject.validate();
	}
}
