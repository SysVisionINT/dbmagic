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
package net.java.dbmagic;

import java.io.File;
import java.util.Properties;

import net.java.dbmagic.integration.DB2XMLUtil;
import net.java.sjtools.util.PropertyReader;
import net.java.sjtools.util.TextUtil;

public class Generator {
	private static final String DRIVER = "driver.name";
	private static final String URL = "db.url";
	private static final String USER = "db.user";
	private static final String PASSWORD = "db.user.password";
	private static final String SCHEMA = "db.schema";
	private static final String TABLE = "table.";

	public static void main(String[] args) {
		if (args.length != 2 && args.length != 3) {
			help();
			System.exit(1);
		}

		Properties props = null;

		try {
			props = PropertyReader.getProperties(args[0]);
		} catch (Exception e) {
			System.err.println("ERROR: Reading config file " + args[0] + ":");
			e.printStackTrace(System.err);
			System.exit(2);
		}

		validate(props);

		DB2XMLUtil util = new DB2XMLUtil();
		util.setDriver(props.getProperty(DRIVER));
		util.setUrl(props.getProperty(URL));
		util.setUser(props.getProperty(USER));
		util.setPassword(props.getProperty(PASSWORD));
		util.setDbSchema(props.getProperty(SCHEMA));

		int index = 1;
		String tableName = null;

		while ((tableName = props.getProperty(TABLE.concat(String.valueOf(index)))) != null) {
			util.addTable(tableName);
			index++;
		}

		try {
			util.readDB();
			
			if (args.length == 3) {
				util.merge(new File(args[2]));
			}
				
			util.createXML(new File(args[1]));
		} catch (Exception e) {
			System.err.println("ERROR: Creating XML model file " + args[1] + ":");
			e.printStackTrace(System.err);
			System.exit(4);
		}
	}

	private static void validate(Properties props) {
		testProperty(props, DRIVER);
		testProperty(props, URL);
		testProperty(props, USER);
	}

	private static void testProperty(Properties props, String property) {
		if (TextUtil.isEmptyString(props.getProperty(property))) {
			System.err.println("ERROR: Property " + property + " not defined!");
			System.exit(3);
		}
	}

	private static void help() {
		System.err.println("USE: java " + Generator.class.getName() + " config.properties output.dbi [merge.dbi]");
	}

}
