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
package net.java.dbmagic.ant.writer.java;

import net.java.dbmagic.model.Entity;
import net.java.dbmagic.model.Finder;
import net.java.dbmagic.model.ForeignKey;
import net.java.dbmagic.model.KeyGenerator;
import net.java.dbmagic.model.Model;
import net.java.dbmagic.model.Property;
import net.java.dbmagic.model.Query;

public class ModelHelper {
	public boolean hasPrimaryKey(Entity entity) {
		return entity.getPrimaryKey() != null;
	}
	
	public boolean hasKeyGenerator(Entity entity) {
		if (!hasPrimaryKey(entity)) {
			return false;
		}
		
		return entity.getPrimaryKey().getKeyGenerator() != null;
	}	
	
	public boolean isKeyGeneratorNative(Entity entity) {
		return isKeyGeneratorType(entity, KeyGenerator.NATIVE);
	}	
	
	public boolean isKeyGeneratorTable(Entity entity) {
		return isKeyGeneratorType(entity, KeyGenerator.TABLE);
	}
	
	public boolean isKeyGeneratorMax(Entity entity) {
		return isKeyGeneratorType(entity, KeyGenerator.MAX);
	}
	
	public boolean isKeyGeneratorSequence(Entity entity) {
		return isKeyGeneratorType(entity, KeyGenerator.SEQUENCE);
	}	

	private boolean isKeyGeneratorType(Entity entity, String type) {
		if (!hasKeyGenerator(entity)) {
			return false;
		}
		
		return entity.getPrimaryKey().getKeyGenerator().getType().equals(type);
	}		
	
	public boolean hasForeignKey(Entity entity) {
		return !entity.getForeignKeyList().isEmpty();
	}
	
	public Entity getFKEntity(ForeignKey fk) {
		Model model = fk.getEntity().getModel();
		
		return model.getEntity(fk.getReferencedEntity());
	}

	public boolean hasFinders(Entity entity) {
		return !entity.getFinderList().isEmpty();
	}
	
	public boolean hasOperations(Entity entity) {
		return !entity.getOperationList().isEmpty();
	}	
	
	public boolean isFinderReturnCollection(Finder finder) {
		return finder.getReturnType().equals(Finder.COLLECTION);
	}
	
	public boolean isFinderReturnVoid(Finder finder) {
		return finder.getReturnType().equals(Finder.VOID);
	}
	
	public boolean hasLimits(Finder finder) {
		return finder.isLimited();
	}	
	
	public boolean hasVersionControl(Entity entity) {
		return entity.getVersionControlColumn() != null;
	}		
	
	public boolean isUpdateable(Entity entity) {
		return entity.getColumnList().size() > 0;
	}		
	
	public String getKeyGeneratorTableValueColumn(Entity entity) {
		KeyGenerator generator = entity.getPrimaryKey().getKeyGenerator();
		
		return generator.getProperty(Property.VALUE_COLUMN).getValue();
	}
	
	public boolean isTable (Entity entity) {
		return entity.getSql() == null;
	}
	
	public boolean isCollectionQuery(Query query) {
		return query.getReturnType().equals(Query.COLLECTION);
	}
}
