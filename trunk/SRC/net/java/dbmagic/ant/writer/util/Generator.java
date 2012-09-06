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
package net.java.dbmagic.ant.writer.util;

import java.io.StringWriter;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

public class Generator {
	private VelocityEngine ve = new VelocityEngine();
	
	public Generator() throws Exception {		
		ve.setProperty("resource.loader" , "class");
		ve.setProperty("class.resource.loader.class" , "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		ve.setProperty("runtime.log.logsystem.class" , "net.java.dbmagic.ant.writer.util.VelocityLog");
		
		ve.init();
	}

	public String getSource(String template, VelocityContext context) throws Exception {
		StringWriter sw = new StringWriter();

		ve.mergeTemplate(template + ".vm", "ISO-8859-1", context, sw);

		return sw.toString();
	}

}
