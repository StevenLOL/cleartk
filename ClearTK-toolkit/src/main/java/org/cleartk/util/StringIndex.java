 /** 
 * Copyright (c) 2007-2008, Regents of the University of Colorado 
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer. 
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. 
 * Neither the name of the University of Colorado at Boulder nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE. 
*/
package org.cleartk.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * <br>Copyright (c) 2007-2008, Regents of the University of Colorado 
 * <br>All rights reserved.

 *
 * @author Philipp Wetzler
 *
 */

public class StringIndex implements Iterable<String>, Serializable {
	
	private static final long serialVersionUID = 8794282308665576153L;

	public static StringIndex fromFile(File inputFile) throws IOException {
		StringIndex index = new StringIndex();
		index.read(inputFile);
		return index;
	}
	
	public static StringIndex fromInputStream(InputStream inputStream) throws IOException {
		StringIndex index = new StringIndex();
		index.read(inputStream);
		return index;
	}
	
	
	Map<String, Integer> indexMap;
	int nextIndex;
	
	public StringIndex() {
		this(1);
	}
	
	public StringIndex(int firstIndex) {
		if( firstIndex < 0 )
			throw new IllegalArgumentException();
		
		this.indexMap = new HashMap<String, Integer>();
		this.nextIndex = firstIndex;
	}
	
	public void setCapacity(int capacity) {
		if( capacity * .75 < this.indexMap.size() )
			return;
		
		HashMap<String,Integer> map = new HashMap<String,Integer>(capacity);
		map.putAll(this.indexMap);
		this.indexMap = map;
	}
	
	public boolean contains(String string) {
		return indexMap.containsKey(string);
	}

	public int find(String string) {
		return indexMap.get(string);
	}
	
	public void insert(String string) {
		if( !indexMap.containsKey(string) ) {
			indexMap.put(string, nextIndex);
			nextIndex += 1;
		}
	}
	
	public void write(File outputFile) throws IOException {
		OutputStream output = new FileOutputStream(outputFile);
		this.write(output);
		output.close();
	}
	
	public void write(OutputStream outputStream) throws IOException {
		PrintWriter output = new PrintWriter(outputStream);
		
		for( String key : this ) {
			output.format("%s %d\n", key, this.find(key));
		}
		
		output.flush();
	}
	
	public void read(File inputFile) throws IOException {
		InputStream input = new FileInputStream(inputFile);
		this.read(input);
		input.close();
	}
	
	public void read(InputStream inputStream) throws IOException {
		BufferedReader input = new BufferedReader(new InputStreamReader(inputStream));

		this.indexMap = new TreeMap<String, Integer>();
		this.nextIndex = 0;
		
		String line;
		while( (line = input.readLine()) != null ) {
			int split = line.lastIndexOf(" ");
			String key = line.substring(0, split);
			int index = Integer.valueOf(line.substring(split+1));
//			String[] fields = line.split(" ");
//			String key = fields[0];
//			int index = Integer.valueOf(fields[1]);
			
			this.indexMap.put(key, index);
			if( index >= this.nextIndex )
				this.nextIndex = index + 1;
		}
	}
	
	public Iterator<String> iterator() {
		return indexMap.keySet().iterator();
	}
	
	public Map<Integer, String> reverseMap() {
		Map<Integer, String> rMap = new TreeMap<Integer, String>();
		
		for( String key : this ) {
			rMap.put(this.find(key), key);
		}
		
		return rMap;
	}
	
}
