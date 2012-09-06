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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Iterator;

import net.java.dbmagic.ant.BaseOutputTask;
import net.java.dbmagic.ant.util.StdOut;
import net.java.dbmagic.db.DBDialect;
import net.java.dbmagic.db.SGBDUtil;
import net.java.dbmagic.model.Entity;
import net.java.dbmagic.model.ForeignKey;
import net.java.dbmagic.model.Index;

import org.apache.tools.ant.BuildException;


public class DBSchema extends BaseOutputTask {
	private File file = null;
	private String dbms = null;

	public DBSchema() {
		super();
	}

	public void execute() throws BuildException {
		PrintWriter out = null;

		DBDialect script = SGBDUtil.getDialect(dbms);

		try {
			out = new PrintWriter(new BufferedOutputStream(new FileOutputStream(file)));

			Entity table = null;

			for (Iterator i = getContainer().getModel().getEntitiesOrderedByDependencies().iterator(); i.hasNext();) {
				table = (Entity) i.next();

				if (table.getSql() == null) {
					processTable(table, script, out);
				}
			}

			StdOut.println("DB Model file " + file + " created");
		} catch (Exception e) {
			e.printStackTrace();
			throw new BuildException("Unable to db create file " + file, e);
		} finally {
			out.close();
		}
	}

	private void processTable(Entity table, DBDialect script, PrintWriter out) {
		out.print(script.getCreateTable(table));
		out.println(script.getSqlSeparater());

		// INDEXES
		if (!table.getIndexList().isEmpty()) {
			out.println();

			for (Iterator i = table.getIndexList().iterator(); i.hasNext();) {
				out.print(script.getCreateIndex((Index) i.next()));
				out.println(script.getSqlSeparater());
			}
		}
		
		// INDEXES FOR FKs
		if (!table.getForeignKeyList().isEmpty() && script.generateFKIndexs()) {
			out.println();

			for (Iterator i = table.getForeignKeyList().iterator(); i.hasNext();) {
				out.print(script.getCreateIndex((ForeignKey) i.next()));
				out.println(script.getSqlSeparater());
			}
		}

		// FKs
		if (!table.getForeignKeyList().isEmpty()) {
			out.println();

			for (Iterator i = table.getForeignKeyList().iterator(); i.hasNext();) {
				out.print(script.getForeignKey((ForeignKey) i.next()));
				out.println(script.getSqlSeparater());
			}
		}

		out.println();
		StdOut.println("Table " + table.getName() + " exported");
	}

	public void setFile(File file) {
		this.file = file;
	}

	public void setDBMS(String dbms) {
		this.dbms = dbms.toUpperCase().trim();
	}

}
