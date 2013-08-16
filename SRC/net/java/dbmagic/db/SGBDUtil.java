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
package net.java.dbmagic.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import net.java.sjtools.db.DBMS;
import net.java.sjtools.db.DBMSUtil;
import net.java.sjtools.db.DBUtil;

import org.apache.tools.ant.BuildException;

public class SGBDUtil extends DBUtil {
	public static final String SAPDB = "SAPDB";
	public static final String MCKOI = "MCKOI";
	public static final String ORACLE = "ORACLE";
	public static final String INFORMIX = "INFORMIX";
	public static final String MYSQL = "MYSQL";
	public static final String DERBY = "DERBY";
	public static final String HSQL = "HSQL";
	public static final String H2 = "H2";
	public static final String POSTGRESQL = "POSTGRESQL";

	public static DBDialect getDialect(String dbmsName) {
		String dbms = dbmsName.toUpperCase();
		
		if (dbms.equals(POSTGRESQL)) {
			return new PostgreSQLDialect();
		} else if (dbms.equals(INFORMIX)) {
			return new InformixDialect();
		} else if (dbms.equals(ORACLE)) {
			return new OracleDialect();
		} else if (dbms.equals(MCKOI)) {
			return new MckoiDialect();
		} else if (dbms.equals(SAPDB)) {
			return new SapDBDialect();
		} else if (dbms.equals(MYSQL)) {
			return new MySQLDialect();		
		} else if (dbms.equals(DERBY)) {
			return new DerbyDialect();		
		} else if (dbms.equals(HSQL)) {
			return new HSQLDialect();
		} else if (dbms.equals(H2)) {
			return new H2Dialect();				
		} else {
			throw new BuildException(dbmsName + " DB Schema not implemented");
		}
	}

	public static DBDialect getDialect(Connection con) throws SQLException {
		DBMS dbms = DBMSUtil.getDBMS(con);

		if (dbms.equals(DBMS.DBMS_POSTGRESQL)) {
			return new PostgreSQLDialect();
		} else if (dbms.equals(DBMS.DBMS_INFORMIX)) {
			return new InformixDialect();
		} else if (dbms.equals(DBMS.DBMS_ORACLE)) {
			return new OracleDialect();
		} else if (dbms.equals(DBMS.DBMS_MCKOI)) {
			return new MckoiDialect();
		} else if (dbms.equals(DBMS.DBMS_SAPDB)) {
			return new SapDBDialect();
		} else if (dbms.equals(DBMS.DBMS_MYSQL)) {
			return new MySQLDialect();		
		} else if (dbms.equals(DBMS.DBMS_DERBY)) {
			return new DerbyDialect();				
		} else if (dbms.equals(DBMS.DBMS_HSQL)) {
			return new HSQLDialect();
		} else if (dbms.equals(DBMS.DBMS_H2)) {
			return new H2Dialect();				
		} else {
			throw new BuildException(dbms.toString());
		}
	}
	
	public static Connection getConnection(String driver, String url,
			String user, String password) throws SQLException {

		try {
			Class.forName(driver).newInstance();
		} catch (Exception e) {
			throw new SQLException(e);
		}

		return DriverManager.getConnection(url, user, password);
	}	
	
}
