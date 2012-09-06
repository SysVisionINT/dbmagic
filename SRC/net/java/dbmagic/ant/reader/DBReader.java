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
package net.java.dbmagic.ant.reader;

import net.java.dbmagic.ant.Container;
import net.java.dbmagic.ant.util.StdOut;
import net.java.dbmagic.db.DBModelReader;

import org.apache.tools.ant.BuildException;


public class DBReader extends Container {
	private String driver = null;
	private String url = null;
	private String user = null;
	private String password = null;
	private String dbSchema = null;

	public DBReader() {
		super();
	}

	public void execute() throws BuildException {
		try {
			setModel(DBModelReader.read(driver, url, user, password, dbSchema, null));
			StdOut.println("Model loaded from database " + url);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BuildException("Unable to import DB tables " + url, e);
		}

		super.execute();
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public void setDbSchema(String string) {
		dbSchema = string;
	}

}
