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

package it.cnr.iasi.saks.semrel.method.asrmp;

import java.util.Vector;

import org.apache.jena.graph.Node;

import it.cnr.iasi.saks.semrel.Constants;
import it.cnr.iasi.saks.semrel.KnowledgeBase;
import it.cnr.iasi.saks.semrel.Path;
import it.cnr.iasi.saks.semrel.method.asrmp.aggregation.AggregationStrategy_Interface;

public class ASRMP_a extends ASRMP_Abstract {
	KnowledgeBase kb = null;
	int length = 0;
	String mode = Constants.DIRECTED_PATH;
	boolean acyclyc = true;
	AggregationStrategy_Interface ag = null;

	public ASRMP_a(KnowledgeBase kb, int length, String mode, boolean acyclyc, AggregationStrategy_Interface ag) {
		super();
		this.setKb(kb);
		this.length = length;
		this.mode = mode;
		this.acyclyc = acyclyc;
		this.ag = ag;
	}
	
	public KnowledgeBase getKb() {
		return kb;
	}

	public void setKb(KnowledgeBase kb) {
		this.kb = kb;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public boolean isAcyclyc() {
		return acyclyc;
	}

	public void setAcyclyc(boolean acyclyc) {
		this.acyclyc = acyclyc;
	}

	public AggregationStrategy_Interface getAg() {
		return ag;
	}

	public void setAg(AggregationStrategy_Interface ag) {
		this.ag = ag;
	}
	
	public double semrel(Node n1, Node n2) {
		double result = 0.0d;
				
		Vector<Path> paths = kb.paths(n1, n2, this.getLength(), this.getMode(), this.isAcyclyc());		
		if(paths.size() > 0)
			result = this.pathsAggregation(paths, kb, this.getAg());
		
		paths = kb.paths(n2, n1, this.getLength(), this.getMode(), this.isAcyclyc());		
		if(paths.size() > 0)
			result = result + this.pathsAggregation(paths, kb, this.getAg());
		
		result = result / 2.0d;
		
		return result;
	}
	
	public double pathsAggregation(Vector<Path> paths, KnowledgeBase kb, AggregationStrategy_Interface ag) {
		double result = 0.0d;
		double temp = pathAggregation(paths.get(0), kb, ag);
		for(int i=1; i<paths.size(); i++) {
			temp = ag.t_conorm(temp, pathAggregation(paths.get(i), kb, ag));
		}
		result = temp;
		return result;
	}

}
