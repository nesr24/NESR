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

import java.util.Vector;

import org.apache.jena.graph.Triple;

/**
 * 
 * @author francesco
 *
 */
public class Path {
	private Vector<Triple> triples = new Vector<Triple>();
	private Vector<String> propertyPathAdornments = new Vector<String>();

	public Path() {
		Vector<String> temp = new Vector<String>();
		temp.add(" ");
		temp.add(" ");
		temp.add(" ");
		this.setPropertyPathAdornments(temp);
	}
	
	public Vector<Triple> getTriples() {
		return triples;
	}
	public void setTriples(Vector<Triple> triples) {
		this.triples = triples;
	}
	
	public Vector<String> getPropertyPathAdornments() {
		return propertyPathAdornments;
	}
	public void setPropertyPathAdornments(Vector<String> propertyPathAdornments) {
		this.propertyPathAdornments = propertyPathAdornments;
	}
	public int size() {
		return this.getTriples().size();
	}
	
	public boolean equals(Path pp)  {
		boolean result = false;
		// Check if the triples are the same for both the patterns
		if(this.getTriples().size() == pp.getTriples().size()) {
			for(int i=0; i<this.getTriples().size(); i++) {
				if(this.getTriples().get(i).getSubject().toString().equals(pp.getTriples().get(i).getSubject().toString()) &&
				   this.getTriples().get(i).getObject().toString().equals(pp.getTriples().get(i).getObject().toString()) &&
				   this.getTriples().get(i).getPredicate().toString().equals(pp.getTriples().get(i).getPredicate().toString()));
				else
					return false;
			}		
			// Check if the propertyPathAdornments of the two patterns are equal
			if(this.getPropertyPathAdornments().size() == pp.getPropertyPathAdornments().size()) {
				for(int i=0; i<this.getPropertyPathAdornments().size(); i++) {
					if(this.getPropertyPathAdornments().get(i).toString().equals(pp.getPropertyPathAdornments().get(i).toString()));
					else 
						return false;
				}
			}
			result = true;
		}

		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Path) {
			Path p = (Path) o;
			return this.equals(p);
		}	
		return false;			
	}
	
	/**
	 * Check if the path is acyclic.
	 * This function is valid only for paths whose length is not greater than 3
	 * @return
	 */
	public boolean isAcyclic() {
		boolean result = true;
		for(Triple t:this.getTriples())
			// whatever the size of the path is,
			// for each triple the subject must be different from the object
			if(t.getSubject().toString().equals(t.getObject().toString()))
					return false;
		if(this.getTriples().size()==2) {
			
		}
		// if the path's size is equal to 3, subject and object of the first triple 
		// must be different from subject and object of the last triple. 
		if(this.getTriples().size()==3) {
			if((this.getTriples().get(0).getSubject().toString().equals(this.getTriples().get(2).getSubject().toString())))
				return false;
			if((this.getTriples().get(0).getSubject().toString().equals(this.getTriples().get(2).getObject().toString())))
				return false;
			if((this.getTriples().get(0).getObject().toString().equals(this.getTriples().get(2).getSubject().toString())))
				return false;
			if((this.getTriples().get(0).getObject().toString().equals(this.getTriples().get(2).getObject().toString())))
				return false;					
		}			
		return result;
	}
	
	@Override
	public int hashCode(){
		int result = 0;
		result = this.toString().hashCode();
		return result;
	}

	@Override
	public String toString() {
		String result = "";
		for(int i=0; i<this.getTriples().size(); i++) {
			Triple t = this.getTriples().get(i);
			String a = this.getPropertyPathAdornments().get(i);
			
			result = result 
					+ t.getSubject() + " "
					+ "@"+t.getPredicate() + a + " "
					+ t.getObject();
			if(i<this.getTriples().size()-1)
				result = result+"\n\t";
		}
		return result;
	}
	
	public String triplesToString() {
		String result = "";
		for(Triple t:this.getTriples())
			result = result + " \n . " + t.toString() + " ";
		return result;
	}
	
	public String propertyPathAdornmentsToString() {
		String result = "";
		for(String t:this.getPropertyPathAdornments())
			result = result + " \n . " + t.toString() + " ";
		return result;
	}
	
}
