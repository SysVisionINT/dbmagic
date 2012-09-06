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
package net.java.dbmagic.ant.writer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.java.dbmagic.ant.BaseOutputTask;
import net.java.dbmagic.ant.util.StdOut;
import net.java.dbmagic.db.DBDialect;
import net.java.dbmagic.db.SGBDUtil;
import net.java.dbmagic.model.Entity;
import net.java.dbmagic.model.ForeignKey;
import net.java.dbmagic.model.Index;

import org.apache.tools.ant.BuildException;


public class DBCreate extends BaseOutputTask {
	private String driver = null;
	private String url = null;
	private String user = null;
	private String password = null;

	public DBCreate() {
		super();
	}

	public void execute() throws BuildException {
		Connection con = null;

		try {
			con = SGBDUtil.getConnection(driver, url, user, password);
			DBDialect script = SGBDUtil.getDialect(con);

			dropTables(con, script);
			StdOut.println("Old tables drop from " + url);

			createTables(con, script);
			StdOut.println("Model tables created on " + url);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BuildException("Unable to create DB tables on " + url, e);
		} finally {
			SGBDUtil.close(con);
		}
	}

	private void createTables(Connection con, DBDialect script) {
		List list = getContainer().getModel().getEntitiesOrderedByDependencies();

		Entity table = null;
		PreparedStatement ps = null;

		for (Iterator i = list.iterator(); i.hasNext();) {
			table = (Entity) i.next();

			if (table.getSql() == null) {
				try {
					ps = con.prepareStatement(script.getCreateTable(table));
					ps.execute();

					for (Iterator ii = table.getIndexList().iterator(); ii.hasNext();) {
						ps = con.prepareStatement(script.getCreateIndex((Index) ii.next()));
						ps.execute();
					}

					for (Iterator ifk = table.getForeignKeyList().iterator(); ifk.hasNext();) {
						ps = con.prepareStatement(script.getForeignKey((ForeignKey) ifk.next()));
						ps.execute();
					}

					StdOut.println("Table " + table.getName() + " created");
				} catch (SQLException e) {
					StdOut.println("ERROR: Unable to create table " + table.getName() + " (" + e.getErrorCode() + " - "
							+ e.getMessage() + ")");
				} finally {
					SGBDUtil.close(ps);
				}
			}
		}
	}

	private void dropTables(Connection con, DBDialect script) {
		List list = getContainer().getModel().getEntitiesOrderedByDependencies();
		Collections.reverse(list);

		Entity table = null;
		PreparedStatement ps = null;

		for (Iterator i = list.iterator(); i.hasNext();) {
			table = (Entity) i.next();

			if (table.getSql() == null) {
				try {
					ps = con.prepareStatement(script.getDropTable(table));
					ps.execute();

					StdOut.println("Table " + table.getName() + " droped");
				} catch (SQLException e) {
				} finally {
					SGBDUtil.close(ps);
				}
			}
		}
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
}
