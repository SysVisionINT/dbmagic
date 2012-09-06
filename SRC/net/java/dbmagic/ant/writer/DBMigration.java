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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import net.java.dbmagic.ant.BaseOutputTask;
import net.java.dbmagic.ant.util.StdOut;
import net.java.dbmagic.db.SGBDUtil;
import net.java.dbmagic.model.Column;
import net.java.dbmagic.model.Entity;
import net.java.sjtools.util.TextUtil;

import org.apache.tools.ant.BuildException;


public class DBMigration extends BaseOutputTask {

	private String inputDriver = null;
	private String inputUrl = null;
	private String inputUser = null;
	private String inputPassword = null;

	private String outputDriver = null;
	private String outputUrl = null;
	private String outputUser = null;
	private String outputPassword = null;


	public DBMigration() {
		super();
	}

	public void execute() throws BuildException {
		Connection outputCon = null;
		Connection inputCon = null;

		try {
			inputCon = SGBDUtil.getConnection(inputDriver, inputUrl, inputUser, inputPassword);
			StdOut.println("Connected to: " + inputUrl);
			outputCon = SGBDUtil.getConnection(outputDriver, outputUrl, outputUser, outputPassword);
			StdOut.println("Connected to: " + outputUrl);

			transferData(inputCon, outputCon);
			StdOut.println("Data transfered from " + inputCon + " to " + outputUrl);

		} catch (Exception e) {
			e.printStackTrace();
			throw new BuildException("Unable to tranfer data to " + outputUrl, e);
		} finally {
			SGBDUtil.close(inputCon);
			SGBDUtil.close(outputCon);
		}
	}

	private void transferData(Connection inputCon, Connection outputCon) {
		List list = getContainer().getModel().getEntitiesOrderedByDependencies();

		String GET = "SELECT * FROM [TABLE_NAME]";
		String INSERT = "INSERT INTO [TABLE_NAME] VALUES ([PARAMS])";
		StringBuffer params = null;

		Entity table = null;
		PreparedStatement psGet = null;
		PreparedStatement psInsert = null;

		ResultSet rs = null;

		for (Iterator i = list.iterator(); i.hasNext();) {
			table = (Entity) i.next();

			try {
				params = new StringBuffer();
				int size = table.getAllColumnList().size();

				for (int j = 0; j < size; j++) {
					params.append("?");
					if (j < size - 1) {
						params.append(",");
					}
				}

				TextUtil.replace(INSERT, "[TABLE_NAME]", table.getName());

				psGet = inputCon.prepareStatement(TextUtil.replace(GET, "[TABLE_NAME]", table.getName()));
				psInsert = outputCon.prepareStatement(TextUtil.replace(TextUtil.replace(INSERT, "[TABLE_NAME]", table.getName()), "[PARAMS]", params.toString()));

				rs = psGet.executeQuery();

				while (rs.next()) {

					int index = 1;
					for (Iterator iterator = table.getAllColumnList().iterator(); iterator.hasNext();) {
						Column coluna = (Column) iterator.next();
						setColumnValue(index, coluna, psInsert, rs);
						index++;
					}
					psInsert.executeUpdate();
				}

				StdOut.println("Data from table " + table.getName() + " transfered");
			} catch (SQLException e) {
				StdOut.println("ERROR: Unable to tranfer data from table " + table.getName() + " (" + e.getErrorCode() + " - " + e.getMessage() + ")");
			} finally {
				SGBDUtil.close(psGet);
				SGBDUtil.close(psInsert);
				SGBDUtil.close(rs);
			}
		}
	}

	public void setColumnValue(int index, Column column, PreparedStatement ps, ResultSet rs) throws SQLException {

		if (column.getJavaType().equals("byte")) {
			ps.setByte(index, rs.getByte(column.getName()));
		} else if (column.getJavaType().equals("short")) {
			ps.setShort(index, rs.getShort(column.getName()));
		} else if (column.getJavaType().equals("int")) {
			ps.setInt(index, rs.getInt(column.getName()));
		} else if (column.getJavaType().equals("long")) {
			ps.setLong(index, rs.getLong(column.getName()));
		} else if (column.getJavaType().equals("float")) {
			ps.setFloat(index, rs.getFloat(column.getName()));
		} else if (column.getJavaType().equals("double")) {
			ps.setDouble(index, rs.getDouble(column.getName()));
		} else if (column.getJavaType().equals("java.math.BigDecimal")) {
			ps.setBigDecimal(index, rs.getBigDecimal(column.getName()));
		} else if (column.getJavaType().equals("boolean")) {
			ps.setBoolean(index, rs.getBoolean(column.getName()));
		} else if (column.getJavaType().equals("String")) {
			ps.setString(index, rs.getString(column.getName()));
		} else if (column.getJavaType().equals("byte[]")) {
			ps.setBytes(index, rs.getBytes(column.getName()));
		} else if (column.getJavaType().equals("java.sql.Date")) {
			ps.setDate(index, rs.getDate(column.getName()));
		} else if (column.getJavaType().equals("java.sql.Time")) {
			ps.setTime(index, rs.getTime(column.getName()));
		} else if (column.getJavaType().equals("java.sql.Timestamp")) {
			ps.setTimestamp(index, rs.getTimestamp(column.getName()));
		} else {
			ps.setObject(index, rs.getObject(column.getName()));
		}

		if (rs.wasNull()) {
			ps.setNull(index, getSQLType(column));
		}
	}

	private int getSQLType(Column column) {

		if (column.isNullable()) {
			if (column.getJavaType().equals("byte")) {
				return java.sql.Types.TINYINT;
			} else if (column.getJavaType().equals("short")) {
				return java.sql.Types.SMALLINT;
			} else if (column.getJavaType().equals("int")) {
				return java.sql.Types.INTEGER;
			} else if (column.getJavaType().equals("long")) {
				return java.sql.Types.BIGINT;
			} else if (column.getJavaType().equals("float")) {
				return java.sql.Types.REAL;
			} else if (column.getJavaType().equals("double")) {
				return java.sql.Types.DOUBLE;
			} else if (column.getJavaType().equals("boolean")) {
				return java.sql.Types.BOOLEAN;
			}
		}

		return java.sql.Types.VARCHAR;
	}

	public String getInputDriver() {
		return inputDriver;
	}


	public void setInputDriver(String inputDriver) {
		this.inputDriver = inputDriver;
	}


	public String getInputUrl() {
		return inputUrl;
	}


	public void setInputUrl(String inputUrl) {
		this.inputUrl = inputUrl;
	}


	public String getInputUser() {
		return inputUser;
	}


	public void setInputUser(String inputUser) {
		this.inputUser = inputUser;
	}


	public String getInputPassword() {
		return inputPassword;
	}


	public void setInputPassword(String inputPassword) {
		this.inputPassword = inputPassword;
	}


	public String getOutputDriver() {
		return outputDriver;
	}


	public void setOutputDriver(String outputDriver) {
		this.outputDriver = outputDriver;
	}


	public String getOutputUrl() {
		return outputUrl;
	}


	public void setOutputUrl(String outputUrl) {
		this.outputUrl = outputUrl;
	}


	public String getOutputUser() {
		return outputUser;
	}


	public void setOutputUser(String outputUser) {
		this.outputUser = outputUser;
	}


	public String getOutputPassword() {
		return outputPassword;
	}


	public void setOutputPassword(String outputPassword) {
		this.outputPassword = outputPassword;
	}
}
