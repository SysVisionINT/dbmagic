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

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Iterator;

import net.java.dbmagic.ant.BaseOutputTask;
import net.java.dbmagic.ant.util.StdOut;
import net.java.dbmagic.ant.writer.java.JavaHelper;
import net.java.dbmagic.ant.writer.java.ModelHelper;
import net.java.dbmagic.ant.writer.java.SQLHelper;
import net.java.dbmagic.ant.writer.util.Generator;
import net.java.dbmagic.model.Entity;
import net.java.dbmagic.model.Query;
import net.java.sjtools.util.TextUtil;

import org.apache.tools.ant.BuildException;
import org.apache.velocity.VelocityContext;


public class GenerateJava extends BaseOutputTask {
	private File outputDir = null;
	private String packageName = null;
	private boolean java6 = false;
	private boolean logDebug = false;
	private boolean useRLog = false;

	public GenerateJava() {
		super();
	}

	public void execute() throws BuildException {
		try {
			Generator gen = new Generator();

			VelocityContext context = new VelocityContext();

			JavaHelper java = new JavaHelper();
			context.put("java", java);
			context.put("model", new ModelHelper());
			context.put("sql", new SQLHelper());
			context.put("packageName", packageName);
			context.put("java6", new Boolean(java6));
			context.put("loggerLevel", (logDebug? "debug": "info"));
			context.put("loggerType", (useRLog? "RLog": "log"));
			context.put("loggerLevelEnable", (logDebug? "isDebugEnabled": "isInfoEnabled"));

			File directory = getDirectory();

			directory.mkdirs();

			StdOut.println("Directory " + directory.getAbsolutePath() + " created");

			Entity entity = null;
			Query query = null;
			PrintStream ps = null;

			for (Iterator i = getContainer().getModel().getEntityList().iterator(); i.hasNext();) {
				entity = (Entity) i.next();

				context.put("entity", entity);

				// A criar o ValueObject
				ps = new PrintStream(new FileOutputStream(getFile(directory, java.getName(entity) + ".java")));

				ps.print(gen.getSource("object", context));
				ps.close();

				StdOut.println(" - " + java.getName(entity) + ".java generated");

				// A criar a PersistenceInterface
				ps = new PrintStream(new FileOutputStream(getFile(directory, java.getName(entity) + "PI.java")));

				ps.print(gen.getSource("pi", context));
				ps.close();

				StdOut.println(" - " + java.getName(entity) + "PI.java generated");
			}

			context.remove("entity");
			
			for (Iterator i = getContainer().getModel().getQueryList().iterator(); i.hasNext();) {
				query = (Query) i.next();

				context.put("query", query);

				// A criar o ValueObject
				ps = new PrintStream(new FileOutputStream(getFile(directory, java.getName(query) + ".java")));

				ps.print(gen.getSource("query", context));
				ps.close();

				StdOut.println(" - " + java.getName(query) + ".java generated");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BuildException("Unable to generate the java files ", e);
		}
	}

	private File getDirectory() {
		String packagePath = TextUtil.replace(packageName, ".", "/");
		return new File(outputDir, packagePath);
	}

	private File getFile(File dir, String fileName) {
		return new File(dir, fileName);
	}

	public void setOutputDir(File outputDir) {
		this.outputDir = outputDir;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public void setJava6(boolean java6) {
		this.java6 = java6;
	}
	
	public void setLogDebug(boolean logDebug) {
		this.logDebug = logDebug;
	}

	public void setUseRLog(boolean useRLog) {
		this.useRLog = useRLog;
	}	
}
