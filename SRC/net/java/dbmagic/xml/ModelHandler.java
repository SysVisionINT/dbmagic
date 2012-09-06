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
package net.java.dbmagic.xml;

import net.java.dbmagic.model.*;
import net.java.sjtools.xml.SimpleHandler;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


public class ModelHandler extends SimpleHandler {

	public Object proccessElement(String elementType, Object currentObject, Attributes attributes) throws SAXException {
		if (elementType.equals("model")) {
			return new Model();
		} else if (elementType.equals("entity")) {
			Entity ret = new Entity();

			ret.setName(attributes.getValue("name"));

			if (attributes.getValue("read-only") != null) {
				ret.setReadOnly(attributes.getValue("read-only").equals("true"));
			}

			((Model) currentObject).addEntity(ret);

			return ret;
		} else if (elementType.equals("primary-key")) {
			PrimaryKey ret = new PrimaryKey();

			((Entity) currentObject).setPrimaryKey(ret);

			return ret;
		} else if (elementType.equals("key-generator")) {
			KeyGenerator ret = new KeyGenerator();

			if (!KeyGenerator.isValidType(attributes.getValue("type"))) {
				throw new SAXException(attributes.getValue("type") + " is not a valid type for key-generator");
			}

			ret.setType(attributes.getValue("type"));

			((PrimaryKey) currentObject).setKeyGenerator(ret);

			return ret;
		} else if (elementType.equals("property")) {
			Property ret = new Property();

			ret.setName(attributes.getValue("name"));
			ret.setValue(attributes.getValue("value"));

			KeyGenerator keyGenerator = (KeyGenerator) currentObject;

			if (!keyGenerator.isValidProperty(ret)) {
				throw new SAXException("Property " + ret.getName() + " is not a valid for type "
						+ keyGenerator.getType() + " in key-generator");
			}

			keyGenerator.addProperty(ret);

			return ret;
		} else if (elementType.equals("column")) {
			Column ret = new Column();

			ret.setName(attributes.getValue("name"));

			if (!Column.isValidJavaType(attributes.getValue("java-type"))) {
				throw new SAXException(attributes.getValue("java-type") + " is not a valid java-type for column");
			}

			ret.setJavaType(attributes.getValue("java-type"));

			if (attributes.getValue("max-size") != null) {
				ret.setMaxSize(Integer.parseInt(attributes.getValue("max-size")));
			}
			
			if (attributes.getValue("fixed-size") != null) {
				ret.setFixedSize(attributes.getValue("fixed-size").equals("true"));
			}

			if (attributes.getValue("decimal-digits") != null) {
				ret.setDecimalDigits(Integer.parseInt(attributes.getValue("decimal-digits")));
			}

			if (attributes.getValue("nullable") != null) {
				ret.setNullable(attributes.getValue("nullable").equals("true"));
			}

			if (currentObject instanceof Entity) {
				((Entity) currentObject).addColumn(ret);
			} else if (currentObject instanceof PrimaryKey) {
				((PrimaryKey) currentObject).addColumn(ret);
			} else if (currentObject instanceof VersionControlColumn) {
				((VersionControlColumn) currentObject).setColumn(ret);
			}

			return ret;
		} else if (elementType.equals("version-control-column")) {
			VersionControlColumn ret = new VersionControlColumn();

			((Entity) currentObject).setVersionControlColumn(ret);

			return ret;
		} else if (elementType.equals("foreign-key")) {
			ForeignKey ret = new ForeignKey();

			ret.setReferencedEntity(attributes.getValue("referenced-entity"));

			if (attributes.getValue("one-to-one") != null) {
				ret.setOneToOne(attributes.getValue("one-to-one").equals("true"));
			}

			((Entity) currentObject).addForeignKey(ret);

			return ret;
		} else if (elementType.equals("foreign-column")) {
			ForeignColumn ret = new ForeignColumn();

			ret.setColumnName(attributes.getValue("column-name"));
			ret.setReferencedColumn(attributes.getValue("referenced-column"));

			((ForeignKey) currentObject).addForeignColumn(ret);

			return ret;
		} else if (elementType.equals("index")) {
			Index ret = new Index();

			ret.setName(attributes.getValue("name"));

			if (attributes.getValue("unique") != null) {
				ret.setUnique(attributes.getValue("unique").equals("true"));
			}

			((Entity) currentObject).addIndex(ret);

			return ret;
		} else if (elementType.equals("index-column")) {
			IndexColumn ret = new IndexColumn();

			ret.setColumnName(attributes.getValue("column-name"));

			((Index) currentObject).addIndexColumn(ret);

			return ret;
		} else if (elementType.equals("finder")) {
			Finder ret = new Finder();

			ret.setName(attributes.getValue("name"));

			if (attributes.getValue("with-limits") != null) {
				ret.setLimited(attributes.getValue("with-limits").equals("true"));
			}

			if (attributes.getValue("distinct-rows") != null) {
				ret.setDistinct(attributes.getValue("distinct-rows").equals("true"));
			}

			if (attributes.getValue("return-type") != null) {
				if (!Finder.isValidReturnType(attributes.getValue("return-type"))) {
					throw new SAXException(attributes.getValue("return-type")
							+ " is not a valid return-type for finder");
				}

				ret.setReturnType(attributes.getValue("return-type"));
			}

			((Entity) currentObject).addFinder(ret);

			return ret;
		} else if (elementType.equals("operation")) {
			Operation ret = new Operation();

			ret.setName(attributes.getValue("name"));

			((Entity) currentObject).addOperation(ret);

			return ret;
		} else if (elementType.equals("parameter")) {
			Parameter ret = new Parameter();

			ret.setName(attributes.getValue("name"));

			if (!Parameter.isValidJavaType(attributes.getValue("java-type"))) {
				throw new SAXException(attributes.getValue("java-type") + " is not a valid java-type for parameter");
			}

			ret.setJavaType(attributes.getValue("java-type"));

			((AceptParameters) currentObject).addParameter(ret);

			return ret;
		} else if (elementType.equals("query")) {
			Query ret = new Query();

			ret.setName(attributes.getValue("name"));

			if (attributes.getValue("return-type") != null) {
				if (!Query.isValidReturnType(attributes.getValue("return-type"))) {
					throw new SAXException(attributes.getValue("return-type") + " is not a valid return-type for Query");
				}

				ret.setReturnType(attributes.getValue("return-type"));
			}

			((Model) currentObject).addQuery(ret);

			return ret;
		} else if (elementType.equals("return-object")) {
			ReturnObject ret = new ReturnObject();

			ret.setClassName(attributes.getValue("class-name"));

			((Query) currentObject).setReturnObject(ret);

			return ret;
		} else if (elementType.equals("field-map")) {
			FieldMap ret = new FieldMap();

			ret.setFieldName(attributes.getValue("field-name"));

			if (!FieldMap.isValidJavaType(attributes.getValue("java-type"))) {
				throw new SAXException(attributes.getValue("java-type") + " is not a valid java-type for field-map");
			}

			ret.setJavaType(attributes.getValue("java-type"));

			((ReturnObject) currentObject).addFieldMap(ret);

			return ret;
		} else if (elementType.equals("filter")) {
			((Query) currentObject).setUseFilter(true);

			return null;			
		}

		return null;
	}

	public void processPCDATA(String elementType, Object currentObject, String value) {
		if (elementType.equals("description")) {
			if (currentObject instanceof Model) {
				Model obj = (Model) currentObject;

				obj.setDescription(value);
			} else if (currentObject instanceof Entity) {
				Entity obj = (Entity) currentObject;

				obj.setDescription(value);
			} else if (currentObject instanceof Query) {
				Query obj = (Query) currentObject;

				obj.setDescription(value);
			}
		} else if (elementType.equals("sql")) {
			if (currentObject instanceof Entity) {
				Entity obj = (Entity) currentObject;

				obj.setSql(value);
			} else if (currentObject instanceof Query) {
				Query obj = (Query) currentObject;

				obj.setSql(value);
			} else {
				Operation obj = (Operation) currentObject;

				obj.setSql(value);
			}
		} else if (elementType.equals("where")) {
			Finder obj = (Finder) currentObject;

			obj.setWhere(value);
		} else if (elementType.equals("column-name")) {
			if (currentObject instanceof ReturnObject) {
				((ReturnObject) currentObject).setColumnName(value);
			} else if (currentObject instanceof FieldMap) {
				((FieldMap) currentObject).setColumnName(value);
			}
		} else if (elementType.equals("column-position")) {
			if (currentObject instanceof ReturnObject) {
				((ReturnObject) currentObject).setColumnPosition(value);
			} else if (currentObject instanceof FieldMap) {
				((FieldMap) currentObject).setColumnPosition(value);
			}
		}
	}

	public void error(SAXParseException error) throws SAXException {
		throw new SAXException(error);
	}
}
