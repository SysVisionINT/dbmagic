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
package net.java.dbmagic.integration;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.java.dbmagic.ant.util.StdOut;
import net.java.dbmagic.db.DBModelReader;
import net.java.dbmagic.model.Column;
import net.java.dbmagic.model.Entity;
import net.java.dbmagic.model.Finder;
import net.java.dbmagic.model.Model;
import net.java.dbmagic.model.Operation;
import net.java.dbmagic.model.Query;
import net.java.dbmagic.xml.XMLModelReader;
import net.java.dbmagic.xml.XMLModelWriter;


public class DB2XMLUtil {
	private String driver = null;
	private String url = null;
	private String user = null;
	private String password = null;
	private String dbSchema = null;
	private List tableList = null;

	private Model model = null;

	public DB2XMLUtil() {
	}

	public String getDbSchema() {
		return dbSchema;
	}

	public void setDbSchema(String dbSchema) {
		this.dbSchema = dbSchema;
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public List getTableList() {
		return tableList;
	}

	public void addTable(String name) {
		if (tableList == null) {
			tableList = new ArrayList();
		}

		tableList.add(name);
	}

	public void readDB() throws Exception {
		model = DBModelReader.read(driver, url, user, password, dbSchema, tableList);
	}

	public void createXML(File outputFile) throws Exception {
		XMLModelWriter.write(model, outputFile);
	}

	public void merge(File mergeFile) throws Exception {
		Model oldModel = XMLModelReader.read(mergeFile);

		if (model == null) {
			model = oldModel;
			return;
		}

		Entity eo = null;
		Entity en = null;

		Finder fo = null;
		Operation oo = null;

		Column co = null;
		Column cn = null;

		for (Iterator i = oldModel.getEntityList().iterator(); i.hasNext();) {
			eo = (Entity) i.next();
			en = model.getEntity(eo.getName());

			if (en == null) {
				StdOut.println("Entity " + eo.getName() + " added to model");
				model.addEntity(eo);
			} else {
				if (en.getPrimaryKey() == null) {
					if (eo.getPrimaryKey() != null) {
						boolean addPK = true;

						for (Iterator ic = eo.getPrimaryKey().getColumnList().iterator(); ic.hasNext();) {
							co = (Column) ic.next();

							cn = en.getColumn(co.getName());

							if (cn == null) {
								addPK = false;
								break;
							} else {
								eo.getPrimaryKey().addColumn(cn);
								en.removeColumn(cn);
							}
						}

						if (addPK) {
							en.setPrimaryKey(eo.getPrimaryKey());
							StdOut.println("Primary key added to entity " + eo.getName());
						} else {
							StdOut.println("[WARN] Primary key of entity " + eo.getName() + " not added, columns changed");
						}
					}
				} else {
					if (eo.getPrimaryKey() != null) {
						boolean addPK = true;

						for (Iterator ic = eo.getPrimaryKey().getColumnList().iterator(); ic.hasNext();) {
							co = (Column) ic.next();

							cn = en.getPrimaryKey().getColumn(co.getName());

							if (cn == null) {
								addPK = false;
								break;
							} else {
								eo.getPrimaryKey().addColumn(cn);
							}
						}

						if (addPK) {
							en.setPrimaryKey(eo.getPrimaryKey());
							StdOut.println("Primary key updated on entity " + eo.getName());
						} else {
							StdOut.println("[WARN] Primary key of entity " + eo.getName() + " not updated, columns changed");
						}
					}
				}

				for (Iterator ifd = eo.getFinderList().iterator(); ifd.hasNext();) {
					fo = (Finder) ifd.next();

					if (en.getFinder(fo.getName()) == null) {
						StdOut.println("Finder " + fo.getName() + " added to entity " + eo.getName());
						en.addFinder(fo);
					}
				}

				for (Iterator io = eo.getOperationList().iterator(); io.hasNext();) {
					oo = (Operation) io.next();

					if (en.getOperation(oo.getName()) == null) {
						StdOut.println("Operation " + oo.getName() + " added to entity " + eo.getName());
						en.addOperation(oo);
					}
				}
			}
		}
		
		for (Iterator i = oldModel.getQueryList().iterator(); i.hasNext();) {
			model.addQuery((Query) i.next());
		}
	}
}
