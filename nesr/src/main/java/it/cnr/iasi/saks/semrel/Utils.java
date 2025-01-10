/*
 * 	 This file is part of SemRel, originally promoted and
 *	 developed at CNR-IASI. For more information visit:
 *	 http://saks.iasi.cnr.it/tools/semrel
 *	     
 *	 This is free software: you can redistribute it and/or modify
 *	 it under the terms of the GNU General Public License as 
 *	 published by the Free Software Foundation, either version 3 of the 
 *	 License, or (at your option) any later version.
 *	 
 *	 This software is distributed in the hope that it will be useful,
 *	 but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	 GNU General Public License for more details.
 * 
 *	 You should have received a copy of the GNU General Public License
 *	 along with this source.  If not, see <http://www.gnu.org/licenses/>.
 */

package it.cnr.iasi.saks.semrel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Vector;

import org.apache.commons.math.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math.stat.correlation.SpearmansCorrelation;
import org.apache.commons.math3.stat.correlation.KendallsCorrelation;

import java.util.Map.Entry;

/**
 * 
 * @author francesco
 *
 */
public class Utils {
    public static <K, V extends Comparable<? super V>> List<Entry<K, V>> findGreatest_n(Map<K, V> map, int n) {
    	List<Entry<K, V>> result = new ArrayList<Map.Entry<K,V>>();
    	if(n>0) {
		    Comparator<? super Entry<K, V>> comparator = 
		        new Comparator<Entry<K, V>>()
		    {
		        @Override
		        public int compare(Entry<K, V> e0, Entry<K, V> e1)
		        {
		            V v0 = e0.getValue();
		            V v1 = e1.getValue();
		            return v0.compareTo(v1);
		        }
		    };
		    PriorityQueue<Entry<K, V>> highest = 
		        new PriorityQueue<Entry<K,V>>(n, comparator);
		    for (Entry<K, V> entry : map.entrySet())
		    {
		        highest.offer(entry);
		        while (highest.size() > n)
		        {
		            highest.poll();
		        }
		    }
		
		    while (highest.size() > 0)
		    {
		        result.add(highest.poll());
		    }
    	}
	    return result;
	}
    
    public static <K, V extends Comparable<? super V>> List<Entry<K, V>> findGreatest(Map<K, V> map) {
    	return findGreatest_n(map, 1);
    }
    
    public class MyEntry<K, V> implements Map.Entry<K, V> {
        private K key;
        private V value;

        public MyEntry() {
        }
        
        public MyEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            V old = this.value;
            this.value = value;
            return old;
        }
    }
    
	public static void println(String filename, String content, boolean append) {
		filename = content.getClass().getResource(filename).getFile();
		try {
			PrintWriter writer = new PrintWriter(new FileOutputStream(
				    new File(filename), 
				    append /* append = true */));
			writer.println(content);
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void print(String filename, String content, boolean append) {
		System.out.println(filename);
		filename = content.getClass().getResource(filename).getFile();
		try {
			PrintWriter writer = new PrintWriter(new FileOutputStream(
				    new File(filename), 
				    append /* append = true */));
			writer.print(content);
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Vector<String> loadIds(String in_file) {
		Vector<String> result = new Vector<String>();	
        try {
            BufferedReader b = new BufferedReader(new FileReader(this.getClass().getResource(in_file).getFile()));
            String readLine = "";
            while ((readLine = b.readLine()) != null) {
				String name = readLine.replace(' ', '_');
				result.add(Constants.DBPEDIA_DBR_NS+name);
            }
            b.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
	}
	
	public static void writeOnFile(String filename, String content, boolean append) {
	    try {
	      FileWriter myWriter = new FileWriter(filename, append);
	      myWriter.write(content);
	      myWriter.close();
	    } catch (IOException e) {
	      System.out.println("An error occurred.");
	      e.printStackTrace();
	    }
	  }
	
	public static void createFile(String filename) {
	    try {
	      File myObj = new File(filename);
	      if (myObj.createNewFile()) {
	        System.out.println("File created: " + myObj.getName());
	      } else {
	        System.out.println("File already exists.");
	      }
	    } catch (IOException e) {
	      System.out.println("An error occurred.");
	      e.printStackTrace();
	    }
	  }
	
	public static double mean(Vector<Double> values) {
		double result = 0.0d;
		if(values.size()>0) {
			for(Double d:values) 
				result = result+d;
			result = result/values.size();
		}
		return result;
	}
	
	
	public static String currentFolder() {
		String result = "";
		try {
			File f = new File("test.txt");
			String canonical = f.getCanonicalPath();
			result = canonical.substring(0, canonical.lastIndexOf("/")+1);	        
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}
}
