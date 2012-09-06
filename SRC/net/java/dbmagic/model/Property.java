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

public class Property {
	public static final String COLUMN_NAME = "column-name";
	public static final String TABLE_NAME = "table-name";
	public static final String NAME_COLUMN = "name-column";
	public static final String VALUE_COLUMN = "value-column";
	public static final String SEQUENCE_NAME = "sequence-name";

	private String name = null;
	private String value = null;
	private KeyGenerator keyGenerator = null;

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	public void setName(String string) {
		name = string;
	}

	public void setValue(String string) {
		value = string;
	}

	public KeyGenerator getKeyGenerator() {
		return keyGenerator;
	}

	public void setKeyGenerator(KeyGenerator generator) {
		keyGenerator = generator;
	}

}
