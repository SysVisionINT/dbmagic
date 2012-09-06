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

import net.java.dbmagic.model.Column;
import net.java.dbmagic.model.Entity;
import net.java.dbmagic.model.ForeignKey;
import net.java.dbmagic.model.Index;

public interface DBDialect {
	public String translateType(Column column);

	public String getCreateTable(Entity table);

	public String getCreateIndex(Index index);

	public String getDropTable(Entity table);

	public String getDropIndex(Index index);

	public String getForeignKey(ForeignKey fk);

	public String getSqlSeparater();
	
	public boolean generateFKIndexs();

	public String getCreateIndex(ForeignKey key);
	
	public boolean validName(String name);
	
	public String getCreateTableSufix(Entity table);
}
