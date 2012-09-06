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
package net.java.dbmagic.ant.reader;

import java.io.File;

import net.java.dbmagic.ant.Container;
import net.java.dbmagic.ant.util.StdOut;
import net.java.dbmagic.clay.ClayXMLModelReader;

import org.apache.tools.ant.BuildException;


public class ClayXMLReader extends Container {
	private File file = null;
	
	public ClayXMLReader() {
		super();
	}

	public void execute() throws BuildException {
		try {
			setModel(ClayXMLModelReader.read(file));
			StdOut.println("Model loaded from Clay file " + file);
			getModel().validate();
			StdOut.println("Model validated");
		} catch (Exception e) {
			e.printStackTrace();
			throw new BuildException("Unable to read Clay file " + file, e);
		}
		
		super.execute();
	}

	public void setFile(File file) {
		this.file = file;
	}
}
