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
package net.java.dbmagic.xml;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import net.java.dbmagic.model.Model;
import net.java.sjtools.xml.SimpleParser;

public class XMLModelReader {
	public static Model read(File file) throws Exception {
		SimpleParser parser = getSimpleParser();

		return (Model) parser.parse(file.getAbsolutePath());
	}
	
	public static Model read(InputStream is) throws Exception {
		SimpleParser parser = getSimpleParser();

		return (Model) parser.parse(is);
	}
	
	public static Model read(String xml) throws Exception {
		return read(new ByteArrayInputStream(xml.getBytes()));
	}	
	
	private static SimpleParser getSimpleParser() throws Exception {
		ModelHandler handler = new ModelHandler();

		SimpleParser parser = new SimpleParser(handler, true);

		parser.addDTD(XMLConstants.DTD_NAME, parser.getClass().getResource(XMLConstants.DTD_FILE).toString());
		
		return parser;
	}	
}
