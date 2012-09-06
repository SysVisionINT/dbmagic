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

public class Parameter {
	private String name = null;
	private String javaType = null;
	private AceptParameters father = null;

	public String getJavaType() {
		return javaType;
	}

	public String getName() {
		return name;
	}

	public void setJavaType(String string) {
		javaType = string;
	}

	public void setName(String string) {
		name = string;
	}

	public AceptParameters getAceptParameters() {
		return father;
	}

	public void setAceptParameters(AceptParameters father) {
		this.father = father;
	}

	public static boolean isValidJavaType(String value) {
		if (Column.isValidJavaType(value)) {
			return true;
		} else {
			if (value.equals("Byte")) {
				return true;
			} else if (value.equals("Short")) {
				return true;
			} else if (value.equals("Integer")) {
				return true;
			} else if (value.equals("Long")) {
				return true;
			} else if (value.equals("Float")) {
				return true;
			} else if (value.equals("Double")) {
				return true;
			} else if (value.equals("Boolean")) {
				return true;
			}

			return false;
		}
	}

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (!(obj instanceof Parameter)) {
			return false;
		}

		Parameter theOther = (Parameter) obj;

		return name.equals(theOther.getName());
	}
}
