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

import java.util.Iterator;

import net.java.dbmagic.model.Column;
import net.java.dbmagic.model.Entity;
import net.java.dbmagic.model.ForeignColumn;
import net.java.dbmagic.model.ForeignKey;
import net.java.dbmagic.model.Index;
import net.java.dbmagic.model.IndexColumn;
import net.java.sjtools.util.TextUtil;


public abstract class SQLCreater implements DBDialect {
	protected static final String TAB = "\t";
	protected static final String SPACE = " ";
	protected static final String LINE = "\n";

	public String getCreateTable(Entity table) {
		if (!validName(table.getName())) {
			throw new Error(table.getName() + " to big!");
		}

		StringBuffer out = new StringBuffer();

		out.append("CREATE TABLE ");
		out.append(table.getName());
		out.append(" (");
		out.append(LINE);

		Column column = null;

		// COLUNAS
		for (Iterator i = table.getAllColumnList().iterator(); i.hasNext();) {
			column = (Column) i.next();

			if (!validName(column.getName())) {
				throw new Error(table.getName() + "." + column.getName() + " to big!");
			}

			out.append(TAB);
			out.append(column.getName());
			out.append(SPACE);
			out.append(translateType(column));

			if (!column.isNullable()) {
				out.append(SPACE);
				out.append("NOT NULL");
			}

			if (table.getPrimaryKey() != null || i.hasNext()) {
				out.append(",");
				out.append(LINE);
			} else {
				out.append(LINE);
			}
		}

		// PK
		if (table.getPrimaryKey() != null) {
			out.append(TAB);
			out.append("PRIMARY KEY (");

			for (Iterator i = table.getPrimaryKey().getColumnList().iterator(); i.hasNext();) {
				column = (Column) i.next();

				out.append(column.getName());

				if (i.hasNext()) {
					out.append(", ");
				}
			}

			out.append(")");
			out.append(LINE);
		}

		// FIM DA TABELA
		out.append(")");
		
		String tableSufix = getCreateTableSufix(table);
		
		if (tableSufix != null && !TextUtil.isEmptyString(tableSufix)) {
			out.append(tableSufix);
		}

		return out.toString();
	}

	public String getCreateIndex(Index index) {
		StringBuffer out = new StringBuffer();
		
		if (!validName(index.getName())) {
			throw new Error(index.getName() + " to big!");
		}

		IndexColumn indexC;
		out.append("CREATE ");
		
		if (index.isUnique()) {
			out.append("UNIQUE ");
		}
		
		out.append("INDEX ");
		out.append(index.getName());
		out.append(" ON ");
		out.append(index.getEntity().getName());
		out.append(" (");

		for (Iterator iC = index.getIndexColumnList().iterator(); iC.hasNext();) {
			indexC = (IndexColumn) iC.next();

			out.append(indexC.getColumnName());

			if (iC.hasNext()) {
				out.append(", ");
			}
		}

		out.append(")");

		return out.toString();
	}

	public String getCreateIndex(ForeignKey fk) {
		StringBuffer out = new StringBuffer();
		
		String fkIndexName = getFkIndexName(fk);
		
		if (!validName(fkIndexName)) {
			throw new Error(fkIndexName + " to big!");
		}

		ForeignColumn column = null;
		out.append("CREATE INDEX ");
		out.append(fkIndexName);
		out.append(" ON ");
		out.append(fk.getEntity().getName());
		out.append(" (");

		for (Iterator iC = fk.getForeignColumnList().iterator(); iC.hasNext();) {
			column = (ForeignColumn) iC.next();

			out.append(column.getColumnName());

			if (iC.hasNext()) {
				out.append(", ");
			}
		}

		out.append(")");

		return out.toString();
	}

	private String getFkIndexName(ForeignKey fk) {
		StringBuffer out = new StringBuffer();
		
		out.append("FK_");
		out.append(fk.getEntity().getName());
		out.append("_");
		out.append(fk.getFkID());
		
		return out.toString();
	}

	public String getDropTable(Entity table) {
		StringBuffer out = new StringBuffer();

		out.append("DROP TABLE ");
		out.append(table.getName());

		return out.toString();
	}

	public String getDropIndex(Index index) {
		StringBuffer out = new StringBuffer();

		out.append("DROP INDEX ");
		out.append(index.getName());

		return out.toString();
	}

	public String getForeignKey(ForeignKey fk) {
		StringBuffer out = new StringBuffer();

		ForeignColumn fkC = null;

		out.append("ALTER TABLE ");
		out.append(fk.getEntity().getName());
		out.append(" ADD FOREIGN KEY (");

		for (Iterator iC = fk.getForeignColumnList().iterator(); iC.hasNext();) {
			fkC = (ForeignColumn) iC.next();

			out.append(fkC.getColumnName());

			if (iC.hasNext()) {
				out.append(", ");
			}
		}

		out.append(") REFERENCES ");
		out.append(fk.getReferencedEntity());
		out.append(" (");

		for (Iterator iC = fk.getForeignColumnList().iterator(); iC.hasNext();) {
			fkC = (ForeignColumn) iC.next();

			out.append(fkC.getReferencedColumn());

			if (iC.hasNext()) {
				out.append(", ");
			}
		}

		out.append(")");

		return out.toString();
	}

	public boolean generateFKIndexs() {
		return false;
	}
	
	public boolean validName(String name) {
		return true;
	}	
	
	public String getCreateTableSufix(Entity table) {
		return null;
	}
}
