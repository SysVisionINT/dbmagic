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
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;

import net.java.dbmagic.ant.util.StdOut;
import net.java.dbmagic.model.Column;
import net.java.dbmagic.model.Entity;
import net.java.dbmagic.model.ForeignColumn;
import net.java.dbmagic.model.ForeignKey;
import net.java.dbmagic.model.Index;
import net.java.dbmagic.model.IndexColumn;
import net.java.dbmagic.model.Model;
import net.java.dbmagic.model.PrimaryKey;


public class DBModelReader {
	private static final String[] TABLE_TYPES = { "TABLE" };

	public static Model read(String driver, String url, String user, String password, String dbSchema, List tableList)
			throws Exception {
		Model shm = new Model();
		shm.setDescription("Generated from " + url + " on " + new Timestamp(System.currentTimeMillis()));

		Class.forName(driver);

		Connection con = null;
		ResultSet rs = null;

		try {
			con = DriverManager.getConnection(url, user, password);

			StdOut.println("Connected to " + url);

			DatabaseMetaData dmd = con.getMetaData();

			rs = dmd.getTables(con.getCatalog(), dbSchema, null, TABLE_TYPES);

			String tableName = null;

			while (rs.next()) {
				tableName = rs.getString("TABLE_NAME");

				if (selectedTable(tableName, tableList)) {
					shm.addEntity(processTable(dmd, rs.getString("TABLE_CAT"), dbSchema, tableName,
							rs.getString("TABLE_TYPE")));
				}
			}
		} finally {
			if (rs != null) {
				rs.close();
			}

			if (con != null) {
				con.close();
			}
		}

		return shm;
	}

	private static boolean selectedTable(String tableName, List tableList) {
		if (tableList == null) {
			return true;
		}
		
		if (tableList.isEmpty()) {
			return true;
		}
		
		for (Iterator i = tableList.iterator(); i.hasNext();) {
			if (tableName.equalsIgnoreCase((String) i.next())){
				return true;
			}
		}
		
		return false;
	}

	private static Entity processTable(DatabaseMetaData dmd, String catalog, String schema, String tableName,
			String type) throws SQLException {
		Entity table = new Entity();
		table.setName(tableName);
		table.setReadOnly(isView(type));

		StdOut.println("Processing table " + table.getName());

		if (!isView(type)) {
			PrimaryKey key = processPK(dmd, catalog, schema, tableName);

			if (key != null) {
				table.setPrimaryKey(key);
			}
		}

		ResultSet rs = null;
		Column column = null;

		try {
			rs = dmd.getColumns(catalog, schema, tableName, null);

			while (rs.next()) {
				column = getColumn(dmd, catalog, schema, tableName, rs.getString("COLUMN_NAME"));

				if (table.getPrimaryKey() == null || table.getPrimaryKey().getColumn(column.getName()) == null) {
					table.addColumn(column);
				}
			}
		} finally {
			if (rs != null) {
				rs.close();
			}
		}

		if (!isView(type)) {
			ForeignKey fk = null;
			ForeignColumn fkColumn = null;

			try {				
				rs = dmd.getImportedKeys(catalog, schema, tableName);

				while (rs.next()) {
					if (fk == null || !fk.getConstraintName().equals(rs.getString("FK_NAME"))) {
						fk = new ForeignKey();
						fk.setConstraintName(rs.getString("FK_NAME"));
						fk.setReferencedEntity(rs.getString("PKTABLE_NAME"));

						table.addForeignKey(fk);
					}

					fkColumn = new ForeignColumn();
					fkColumn.setColumnName(rs.getString("FKCOLUMN_NAME"));
					fkColumn.setReferencedColumn(rs.getString("PKCOLUMN_NAME"));

					fk.addForeignColumn(fkColumn);
				}
			} finally {
				if (rs != null) {
					rs.close();
				}
			}

			Index index = null;
			IndexColumn iColumn = null;

			try {
				rs = dmd.getIndexInfo(catalog, schema, tableName, false, true);

				while (rs.next()) {
					if ((rs.getShort("TYPE") != DatabaseMetaData.tableIndexStatistic)
							&& (table.getPrimaryKey() == null || !table.getPrimaryKey().getName().equals(
									rs.getString("INDEX_NAME")))) {
						if (index == null || !index.getName().equals(rs.getString("INDEX_NAME"))) {
							index = new Index();
							index.setName(rs.getString("INDEX_NAME"));
							index.setUnique(!rs.getBoolean("NON_UNIQUE"));

							table.addIndex(index);
						}

						iColumn = new IndexColumn();
						iColumn.setColumnName(rs.getString("COLUMN_NAME"));

						index.addIndexColumn(iColumn);
					}
				}
			} finally {
				if (rs != null) {
					rs.close();
				}
			}
		}

		return table;
	}

	private static boolean isView(String type) {
		return type.equals("VIEW");
	}

	private static PrimaryKey processPK(DatabaseMetaData dmd, String catalog, String schema, String tableName)
			throws SQLException {
		PrimaryKey key = new PrimaryKey();

		ResultSet rs = null;

		try {
			rs = dmd.getPrimaryKeys(catalog, schema, tableName);

			while (rs.next()) {
				if (key.getName() == null) {
					key.setName(rs.getString("PK_NAME"));

					if (key.getName() == null) {
						key.setName("PK_" + tableName);
					}
				}

				key.addColumn(getColumn(dmd, catalog, schema, tableName, rs.getString("COLUMN_NAME")));
			}
		} finally {
			if (rs != null) {
				rs.close();
			}
		}

		if (key.getColumnList().size() > 0) {
			return key;
		} else {
			return null;
		}
	}

	private static Column getColumn(DatabaseMetaData dmd, String catalog, String schema, String tableName,
			String columnName) throws SQLException {
		Column column = new Column();

		ResultSet rs = null;

		try {
			rs = dmd.getColumns(catalog, schema, tableName, columnName);

			while (rs.next()) {
				column.setName(rs.getString("COLUMN_NAME"));
				column.setJavaType(JDBCUtil.getJavaType(rs.getShort("DATA_TYPE"), rs.getInt("COLUMN_SIZE"), rs
						.getInt("DECIMAL_DIGITS")));
				column.setMaxSize(JDBCUtil.getMaxSize(rs.getShort("DATA_TYPE"), rs.getInt("COLUMN_SIZE")));
				column.setNullable(getColumnNullable(rs.getString("IS_NULLABLE")));
			}
		} finally {
			if (rs != null) {
				rs.close();
			}
		}

		return column;
	}

	private static boolean getColumnNullable(String value) {
		if (value == null) {
			return false;
		} else {
			value = value.trim();
		}

		return !value.equals("NO");
	}
}
