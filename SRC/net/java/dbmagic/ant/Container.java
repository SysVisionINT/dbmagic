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
package net.java.dbmagic.ant;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.java.dbmagic.ant.writer.DBCreate;
import net.java.dbmagic.ant.writer.DBMigration;
import net.java.dbmagic.ant.writer.DBSchema;
import net.java.dbmagic.ant.writer.GenerateJava;
import net.java.dbmagic.ant.writer.XMLWriter;
import net.java.dbmagic.model.Model;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;


public class Container extends Task {
	private List work = new ArrayList();
	private Model shm = null;

	public XMLWriter createXMLWriter() {
		XMLWriter writer = new XMLWriter();
		addOutput(writer);
		return writer;
	}

	public DBSchema createDBSchema() {
		DBSchema db = new DBSchema();
		addOutput(db);
		return db;
	}

	public DBMigration createDBMigration() {
		DBMigration db = new DBMigration();
		addOutput(db);
		return db;
	}

	public GenerateJava createGenerateJava() {
		GenerateJava db = new GenerateJava();
		addOutput(db);
		return db;
	}

	public DBCreate createDBCreate() {
		DBCreate db = new DBCreate();
		addOutput(db);
		return db;
	}

	protected void addOutput(OutputTask out) {
		out.setContainer(this);
		work.add(out);
	}

	public void setModel(Model shm) {
		this.shm = shm;
	}

	public Model getModel() {
		return shm;
	}

	public void execute() throws BuildException {
		for (Iterator i = work.iterator(); i.hasNext();) {
			((OutputTask)i.next()).execute();
		}
	}
}
