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
package it.cnr.iasi.saks.semsim;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;

import cern.colt.matrix.DoubleFactory2D;
import cern.colt.matrix.DoubleMatrix2D;
import it.cnr.iasi.saks.ic.IC_ExtrinsicMethod;
import it.cnr.iasi.saks.ic.IC_IntrinsicMethod;
import it.cnr.iasi.saks.semrel.Constants;
import it.cnr.iasi.saks.semsim.ofv.OFVMgmt;
import it.cnr.iasi.saks.semsim.taxonomy.WeightedTaxonomy;

/**
 * 
 * @author francesco
 *
 */
public class SemsimEngine {

	private Map<Node, Double> ics = new HashMap<Node, Double>(); 
	private WeightedTaxonomy wt;
	private DoubleMatrix2D consim_matrix;
	// maps classes'ids to consim matrix indexes
	private Map<Node, Integer> id_index = new HashMap<Node, Integer>();
	private String annotationMode;
	private OFVMgmt ofvmgmt;
	
	
	public OFVMgmt getOfvmgmt() {
		return ofvmgmt;
	}

	public void setOfvmgmt(OFVMgmt ofvmgmt) {
		this.ofvmgmt = ofvmgmt;
	}

	public SemsimEngine(WeightedTaxonomy wt, String avsFile, String annotationMode, String ic_file, String ic_mode, boolean coeff_option) {
		this.init(wt, avsFile, annotationMode, coeff_option);
		this.loadICs(ic_file, ic_mode);
	}
	
	public SemsimEngine(WeightedTaxonomy wt, String annotationMode, String avsFile) {
		this.wt = wt;
		this.init_idIndexMap();
		this.ofvmgmt.loadAnnotatedResources(avsFile, annotationMode, false);
	}
	
	public SemsimEngine(WeightedTaxonomy wt, boolean coeff_option) {
		this.wt = wt;
		this.init_idIndexMap();
		this.init_consimMatrix();
	}
	
	public SemsimEngine(WeightedTaxonomy wt, String avsFile, String annotationMode, boolean coeff_option) {
		this.init(wt, avsFile, annotationMode, coeff_option);
	}
	
	public SemsimEngine(WeightedTaxonomy wt, String avsFile, String annotationMode, String rvsFile, boolean coeff_option) {
		this.init(wt, avsFile, annotationMode, coeff_option);
		this.ofvmgmt.setRvsFile(rvsFile);
		this.ofvmgmt.loadRequests(rvsFile, annotationMode, coeff_option);
	}

	public SemsimEngine(WeightedTaxonomy wt, IC_ExtrinsicMethod icMethod, String avsFile, String annotationMode, boolean coeff_option) {
		this.init(wt, avsFile, annotationMode, coeff_option);
		this.setIcs(icMethod.computeIcs(wt, ofvmgmt));
	}
	
	public SemsimEngine(WeightedTaxonomy wt, IC_IntrinsicMethod icMethod, String avsFile, String annotationMode, boolean coeff_option) {
		this.init(wt, avsFile, annotationMode, coeff_option);
		this.setIcs(icMethod.computeIcs(wt));
	}
	
	private void init(WeightedTaxonomy wt, String avsFile, String annotationMode, boolean coeff_option) {
		this.wt = wt;
		this.setAnnotationMode(annotationMode);
		this.ofvmgmt = new OFVMgmt();
		this.ofvmgmt.setAvsFile(avsFile);
		this.ofvmgmt.loadAnnotatedResources(avsFile, annotationMode, coeff_option);
		this.init_idIndexMap();
		this.init_consimMatrix();
	}
	
	public WeightedTaxonomy getWt() {
		return wt;
	}

	public void setWt(WeightedTaxonomy wt) {
		this.wt = wt;
	}
	
	public Map<Node, Double> getIcs() {
		return ics;
	}

	public void setIcs(Map<Node, Double> ics) {
		this.ics = ics;
	}

	public DoubleMatrix2D getConsim_matrix() {
		return consim_matrix;
	}

	public void setConsim_matrix(DoubleMatrix2D consim_matrix) {
		this.consim_matrix = consim_matrix;
	}

	public Map<Node, Integer> getId_index() {
		return id_index;
	}

	public void setId_index(Map<Node, Integer> id_index) {
		this.id_index = id_index;
	}

	public String getAnnotationMode() {
		return annotationMode;
	}

	public void setAnnotationMode(String annotationMode) {
		this.annotationMode = annotationMode;
	}
	
	
	
	private void init_consimMatrix() {
		this.setConsim_matrix(DoubleFactory2D.sparse.make(this.getWt().size(), this.getWt().size()));
		this.getConsim_matrix().assign(Constants.CONSIM_MATRIX_INIT);
	}
	
	public double consim(Node n1, Node n2) {
		double result = 0.0d;
		
		double factor = computeFactor(n1, n2);
		
		if(this.getId_index().get(n1)==null) {
			n1 = this.getWt().getEquivalentClass(n1);
		}

		
		
		if(this.getId_index().get(n2)==null) {
			n2 = this.getWt().getEquivalentClass(n2);
		}
		
		if((n1==null) || (n2==null))
			result = 0.0d;
		else {
			int index_first = this.getId_index().get(n1);
			int index_second = this.getId_index().get(n2);
			
			boolean checkSiblings = checkConsimBetweenOrderedSiblings(n1, n2);
		
	/*		if(elem>=0) {
				result = elem;
				System.out.print("inCache"+" ["+result+"]");
			}
			else */{
				
				if(checkSiblings) {
					result = consimBetweenOrderedSiblings(n1, n2);
					this.getConsim_matrix().set(index_first, index_second, result);
				}
				else {
					Vector<Node> lubs = this.getWt().lub(n1, n2);
					Map<Node, Double> lubs_ic = new HashMap<Node, Double>();
					for(Node n:lubs) {
						lubs_ic.put(n, this.ic(n));
					}
					Node lub = this.getWt().bestLubId(lubs_ic);
	
					if(lub == null)
						lub = NodeFactory.createURI(Constants.OWL_THING);
					double lub_ic = this.ic(lub);
					double first_ic = this.ic(n1);
					double second_ic = this.ic(n2);
					
					result = 2 * lub_ic / (first_ic + second_ic);
					this.getConsim_matrix().set(index_first, index_second, result);
					//System.out.println(lub.substring(lub.indexOf("#")+1)+" ["+result+"]");
				}
			}
		}
		
		return result;
	}
	
	public double semsim_WithSpecialConcepts_FOR_AXED(Vector<OFVElem> orig_rv, Vector<OFVElem> av, boolean coeff_option) {
		double result = 0;
				
		Set<String> specialConcepts_label = new HashSet<String>();
		specialConcepts_label.add("InStock_Giacenza");
		specialConcepts_label.add("CostoAcquisto");
		specialConcepts_label.add("Interesse");
		
		double percentage_dealer = 0.6d;
		double percentage_client = 1.0d - percentage_dealer;
		double[] percs = {percentage_dealer, percentage_client};
		
		double[] semsim_with_percs = {0.0d, 0.0d};
		
		Vector<Vector<OFVElem>> requests = new Vector<Vector<OFVElem>>();
		Vector<OFVElem> rv_dealer = new Vector<OFVElem>();
		Vector<OFVElem> rv_client = new Vector<OFVElem>();
				
		for(OFVElem elem:orig_rv) {
			boolean special = false;
			for(String sc:specialConcepts_label) {
				if(this.getWt().getLabelById(elem.getConc_id()).startsWith(sc)) {
					special = true;
				}
			}
			if(special)
				rv_dealer.add(elem);
			else 
				rv_client.add(elem);

		}
				
		requests.add(rv_dealer);
		requests.add(rv_client);
		
		
		for(int z=0; z<2; z++) {
		Vector<OFVElem> rv = requests.get(z); 
		int rvSize = rv.size();
		int avSize = av.size();
		int diff = avSize - rvSize;
		double[][] semsimMatrix = null;
		double[][] semsimValues = null;

		if (diff <= 0) {
			double[][] m = new double[avSize - diff][rvSize];
			double[][] m2 = new double[avSize - diff][rvSize];
			semsimMatrix = m;
			semsimValues = m2;
			// Fill the semsim matrix with |ofv|<|rv|
			for (int i = 0; i < avSize; i++) {
				for (int j = 0; j < rvSize; j++) {
					semsimValues[i][j] = this.consim(av.get(i).getConc_id(), rv.get(j).getConc_id());
					semsimMatrix[i][j] = 1 - semsimValues[i][j];
				}
			}
			for (int i = avSize; i < rvSize; i++) {
				for (int j = 0; j < rvSize; j++) {
					semsimMatrix[i][j] = 1;
					semsimValues[i][j] = 0;
				}

			}
		} else {
			double[][] m = new double[avSize][rvSize + diff];
			double[][] m2 = new double[avSize][rvSize + diff];
			semsimMatrix = m;
			semsimValues = m2;
			// Fill the semsim matrix with |ofv|>=|rv|
			for (int i = 0; i < avSize; i++) {
				for (int j = 0; j < rvSize; j++) {
					semsimValues[i][j] = this.consim(av.get(i).getConc_id(), rv.get(j).getConc_id());
					semsimMatrix[i][j] = 1 - semsimValues[i][j];
				}
//				for (int k = rvSize; k < (rvSize - diff); k++) {
//					semsimMatrix[i][k] = 1;
//					semsimValues[i][k] = 0;
//				}
			}
			
			for (int j = 0; j < avSize; j++) {
				for (int i = rvSize; i < avSize; i++) {
					semsimMatrix[j][i] = 1;
					semsimValues[j][i] = 0;
				}

			}
		}

		HungarianAlgorithm ha = new HungarianAlgorithm();
		int assign[][] = ha.computeAssignments(semsimMatrix);

//		for(int k=0; k<assign.length; k++)
//			System.out.println(assign[k][0] + "\t" + assign[k][1]);
		
		double semsim = 0;
		
		if(coeff_option){
			for (int i = 0; i < assign.length; i++) {
				String c_ofv_elem = "";
				String c_rv_elem = "";
				if(semsimValues[assign[i][0]][assign[i][1]]!=0) {
					if(i<this.ofvmgmt.getAvs().size()) {
						c_ofv_elem = av.elementAt(assign[i][0]).getCoeff();
					}
					if(i<rv.size()) {
						c_rv_elem = rv.elementAt(assign[i][1]).getCoeff();
					}
				}

				semsim = semsim + semsimValues[assign[i][0]][assign[i][1]]*getCoeffContribution(c_ofv_elem,c_rv_elem);
			}

		}
		else{
			for (int i = 0; i < assign.length; i++) {
				semsim = semsim + semsimValues[assign[i][0]][assign[i][1]];
			}
		}
		
/*		for (int i = 0; i < assign.length; i++) {
			semsim = semsim + semsimValues[assign[i][0]][assign[i][1]];
		}
*/		
		//result = semsim / assign.length;
		double min = Math.min(avSize, rvSize);
		double max = Math.max(avSize, rvSize);
		double mean = (min + max)/2;
		double m_3 = max - (max - min)/3;
		double m_4 = max - (max - min)/4;
		double m_root_2 = max - Math.sqrt(max - min);
		double m_root_3 = max - Math.pow(max - min, 1.0d/3.0d);
		double geom_mean = Math.sqrt(max * min);
		
		double fact = rv.size();
		//double fact = max;
		semsim_with_percs[z] = semsim * percs[z] / fact;
		}
		result = semsim_with_percs[0] + semsim_with_percs[1];  
		return result;
	}
	
	
	public double semsim(Vector<OFVElem> rv, Vector<OFVElem> av, boolean coeff_option) {
		double result = 0;
//		System.out.println("semsim 1");		
		
		if(rv.size()==0 || av.size()==0) {
//			System.out.println("semsim 2");
			result = 0.0d;
		}
		else {
//			System.out.println("semsim 3");
			int rvSize = rv.size();
			int avSize = av.size();
			int diff = avSize - rvSize;
			double[][] semsimMatrix = null;
			double[][] semsimValues = null;
	
			if (diff <= 0) {
//				System.out.println("semsim 4");
				double[][] m = new double[avSize - diff][rvSize];
				double[][] m2 = new double[avSize - diff][rvSize];
				semsimMatrix = m;
				semsimValues = m2;
				// Fill the semsim matrix with |ofv|<|rv|
				for (int i = 0; i < avSize; i++) {
//					System.out.println("semsim 5");
					for (int j = 0; j < rvSize; j++) {
//						System.out.println("semsim 6");
	//					System.out.println(i + "\t" + j + "\t" + pair);
						semsimValues[i][j] = this.consim(av.get(i).getConc_id(), rv.get(j).getConc_id());
						semsimMatrix[i][j] = 1 - semsimValues[i][j];
					}
				}
				for (int i = avSize; i < rvSize; i++) {
//					System.out.println("semsim 7");
					for (int j = 0; j < rvSize; j++) {
//						System.out.println("semsim 8");
						semsimMatrix[i][j] = 1;
						semsimValues[i][j] = 0;
					}
	
				}
			} else {
//				System.out.println("semsim 9");
				double[][] m = new double[avSize][rvSize + diff];
				double[][] m2 = new double[avSize][rvSize + diff];
				semsimMatrix = m;
				semsimValues = m2;
				// Fill the semsim matrix with |ofv|>=|rv|
				for (int i = 0; i < avSize; i++) {
//					System.out.println("semsim 10");
					for (int j = 0; j < rvSize; j++) {
//						System.out.println("semsim 11");
						semsimValues[i][j] = this.consim(av.get(i).getConc_id(), rv.get(j).getConc_id());
						semsimMatrix[i][j] = 1 - semsimValues[i][j];
					}
	//				for (int k = rvSize; k < (rvSize - diff); k++) {
	//					semsimMatrix[i][k] = 1;
	//					semsimValues[i][k] = 0;
	//				}
				}
				
				for (int j = 0; j < avSize; j++) {
//					System.out.println("semsim 12");
					for (int i = rvSize; i < avSize; i++) {
//						System.out.println("semsim 13");
						semsimMatrix[j][i] = 1;
						semsimValues[j][i] = 0;
					}
	
				}
			}
	
//			System.out.println("semsim 14");
			HungarianAlgorithm ha = new HungarianAlgorithm();
//			System.out.println("semsim 14a");
			int assign[][] = ha.computeAssignments(semsimMatrix);
//			System.out.println("semsim 14b");
	
	//		for(int k=0; k<assign.length; k++)
	//			System.out.println(assign[k][0] + "\t" + assign[k][1]);
			
			double semsim = 0;
			
//			System.out.println("semsim 15");
			if(coeff_option){
//				System.out.println("semsim 16");
				for (int i = 0; i < assign.length; i++) {
//					System.out.println("semsim 17");
					String c_ofv_elem = "";
					String c_rv_elem = "";
					if(semsimValues[assign[i][0]][assign[i][1]]!=0) {
//						System.out.println("semsim 18");
						if(i<this.ofvmgmt.getAvs().size()) {
//							System.out.println("semsim 19");
							c_ofv_elem = av.elementAt(assign[i][0]).getCoeff();
						}
						if(i<rv.size()) {
//							System.out.println("semsim 20");
							c_rv_elem = rv.elementAt(assign[i][1]).getCoeff();
						}
					}
	
					semsim = semsim + semsimValues[assign[i][0]][assign[i][1]]*getCoeffContribution(c_ofv_elem,c_rv_elem);
				}
	
			}
			else{
//				System.out.println("semsim 21");
				for (int i = 0; i < assign.length; i++) {
//					System.out.println("semsim 22");
					semsim = semsim + semsimValues[assign[i][0]][assign[i][1]];
				}
			}
			
	/*		for (int i = 0; i < assign.length; i++) {
				semsim = semsim + semsimValues[assign[i][0]][assign[i][1]];
			}
	*/		
//			System.out.println("semsim 23");
			//result = semsim / assign.length;
			double min = Math.min(avSize, rvSize);
			double max = Math.max(avSize, rvSize);
			double mean = (min + max)/2;
			double m_3 = max - (max - min)/3;
			double m_4 = max - (max - min)/4;
			double m_root_2 = max - Math.sqrt(max - min);
			double m_root_3 = max - Math.pow(max - min, 1.0d/3.0d);
			double geom_mean = Math.sqrt(max * min);
			
			double fact = mean;
			//double fact = max;
			result = semsim / fact;
		}
//		System.out.println(rv+" versus "+av+": "+result);
		return result;
	}
	
	public double _semsim(Set<Node> avSet, Set<Node> rvSet) {
		double result = 0;
		
		Vector<Node> av = new Vector<Node>(avSet);
		Vector<Node> rv = new Vector<Node>(rvSet);
		
		int rvSize = rv.size();
		int avSize = av.size();
		int diff = avSize - rvSize;
		double[][] semsimMatrix = null;
		double[][] semsimValues = null;

		if (diff <= 0) {
			double[][] m = new double[avSize - diff][rvSize];
			double[][] m2 = new double[avSize - diff][rvSize];
			semsimMatrix = m;
			semsimValues = m2;
			// Fill the semsim matrix with |ofv|<|rv|
			for (int i = 0; i < avSize; i++) {
				for (int j = 0; j < rvSize; j++) {
					semsimValues[i][j] = this.consim(av.get(i), rv.get(j));
					semsimMatrix[i][j] = 1 - semsimValues[i][j];
				}
			}
			for (int i = avSize; i < rvSize; i++) {
				for (int j = 0; j < rvSize; j++) {
					semsimMatrix[i][j] = 1;
					semsimValues[i][j] = 0;
				}

			}
		} else {
			double[][] m = new double[avSize][rvSize + diff];
			double[][] m2 = new double[avSize][rvSize + diff];
			semsimMatrix = m;
			semsimValues = m2;
			// Fill the semsim matrix with |ofv|>=|rv|
			for (int i = 0; i < avSize; i++) {
				for (int j = 0; j < rvSize; j++) {
					semsimValues[i][j] = this.consim(av.get(i), rv.get(j));
					semsimMatrix[i][j] = 1 - semsimValues[i][j];
				}
				for (int k = rvSize; k < (rvSize - diff); k++) {
					semsimMatrix[i][k] = 1;
					semsimValues[i][k] = 0;
				}
			}
		}

		HungarianAlgorithm ha = new HungarianAlgorithm();
		int assign[][] = ha.computeAssignments(semsimMatrix);

		double semsim = 0;
		for (int i = 0; i < assign.length; i++) {
			semsim = semsim + semsimValues[assign[i][0]][assign[i][1]];
		}
		
		//result = semsim / assign.length;
		double min = Math.min(avSize, rvSize);
		double max = Math.max(avSize, rvSize);
		double mean = (min + max)/2;
		double m_3 = max - (max - min)/3;
		double m_4 = max - (max - min)/4;
		double m_root_2 = max - Math.sqrt(max - min);
		double m_root_3 = max - Math.pow(max - min, 1.0d/3.0d);
		double geom_mean = Math.sqrt(max * min);
		
		double fact = min;
		result = semsim / fact;
		return result;
	}
	
	
	public double __semsim(Set<OFVElem> avSet, Set<OFVElem> rvSet, boolean coeff_option) {
		double result = 0;
		
		Vector<OFVElem> av = new Vector<OFVElem>(avSet);
		Vector<OFVElem> rv = new Vector<OFVElem>(rvSet);
		
		int rvSize = rv.size();
		int avSize = av.size();
		int diff = avSize - rvSize;
		double[][] semsimMatrix = null;
		double[][] semsimValues = null;

		if (diff <= 0) {
			double[][] m = new double[avSize - diff][rvSize];
			double[][] m2 = new double[avSize - diff][rvSize];
			semsimMatrix = m;
			semsimValues = m2;
			// Fill the semsim matrix with |ofv|<|rv|
			for (int i = 0; i < avSize; i++) {
				for (int j = 0; j < rvSize; j++) {
					semsimValues[i][j] = this.consim(av.get(i).getConc_id(), rv.get(j).getConc_id());
//					System.out.println(pair+" "+
//					semsimValues[i][j]);
//					semsimMatrix[i][j] = 1 - semsimValues[i][j];
				}
			}
			for (int i = avSize; i < rvSize; i++) {
				for (int j = 0; j < rvSize; j++) {
					semsimMatrix[i][j] = 1;
					semsimValues[i][j] = 0;
				}

			}
		} else {
			double[][] m = new double[avSize][rvSize + diff];
			double[][] m2 = new double[avSize][rvSize + diff];
			semsimMatrix = m;
			semsimValues = m2;
			// Fill the semsim matrix with |ofv|>=|rv|
			for (int i = 0; i < avSize; i++) {
				for (int j = 0; j < rvSize; j++) {
					semsimValues[i][j] = this.consim(av.get(i).getConc_id(), rv.get(j).getConc_id());
					semsimMatrix[i][j] = 1 - semsimValues[i][j];
				}
				for (int k = rvSize; k < (rvSize - diff); k++) {
					semsimMatrix[i][k] = 1;
					semsimValues[i][k] = 0;
				}
			}
		}

		HungarianAlgorithm ha = new HungarianAlgorithm();
		for(int i=0; i<semsimMatrix.length; i++) {
			for(int j=0; j<semsimMatrix.length; j++) {
//				System.out.print(semsimMatrix[i][j]+"\t");
			}
//			System.out.print("\n");
		}
		int assign[][] = ha.computeAssignments(semsimMatrix);

		double semsim = 0;
		
		
	if(coeff_option){
		for (int i = 0; i < assign.length; i++) {
			String c_ofv_elem = "";
			String c_rv_elem = "";
			if(semsimValues[assign[i][0]][assign[i][1]]!=0) {
				if(i<this.ofvmgmt.getAvs().size()) {
					c_ofv_elem = av.elementAt(assign[i][0]).getCoeff();
				}
				if(i<rv.size()) {
					c_rv_elem = rv.elementAt(assign[i][1]).getCoeff();
				}
			}

			semsim = semsim + semsimValues[assign[i][0]][assign[i][1]]*getCoeffContribution(c_ofv_elem,c_rv_elem);
			semsim = semsim + semsimValues[assign[i][0]][assign[i][1]];
		}

	}
	else{
		for (int i = 0; i < assign.length; i++) {
			semsim = semsim + semsimValues[assign[i][0]][assign[i][1]];
		}
	}

		
		
		
		
		
/*		
		for (int i = 0; i < assign.length; i++) {
			semsim = semsim + semsimValues[assign[i][0]][assign[i][1]];
		}
*/		
		//result = semsim / assign.length;
		double min = Math.min(avSize, rvSize);
		double max = Math.max(avSize, rvSize);
		double mean = (min + max)/2;
		double m_3 = max - (max - min)/3;
		double m_4 = max - (max - min)/4;
		double m_root_2 = max - Math.sqrt(max - min);
		double m_root_3 = max - Math.pow(max - min, 1.0d/3.0d);
		double geom_mean = Math.sqrt(max * min);
		
		double fact = max;
		result = semsim / fact;
		return result;
	}
	
	private void init_idIndexMap() {
		int index = 0;
		Iterator iter = this.getWt().getIds().iterator();
		while(iter.hasNext()) {
			this.getId_index().put(((Node)(iter.next())), index);
			index++;
		}
	}
	
	/**
	 * @param n
	 * @return
	 */
	public double ic(Node n) {
//		System.out.println("ic.n="+n.getURI().toString());
		double result = 0;
		if(n.getURI().toString().equalsIgnoreCase(Constants.OWL_THING))
			result = 0;
		else {
			if(this.getIcs().get(n) == null) {
				if(this.getWt().getWeights().get(n)==null) {
					n = this.getWt().getEquivalentClass(n);
				}
				result = -Math.log(this.getWt().getWeights().get(n));
				this.getIcs().put(n, result);
			}
			else 
				result = this.getIcs().get(n);
	/*		
			Relatedness ft = new Relatedness();
			Node n = NodeFactory.createURI(id);
			result = ft.ic(n, this.wt);
	*/		
		}
		return result;
	}
		
	private void loadICs(String input_file, String mode) {
		this.getIcs().put(NodeFactory.createURI(Constants.OWL_THING), 0.0d);
        try {
        	BufferedReader b = new BufferedReader(new FileReader(input_file));
            //BufferedReader b = new BufferedReader(new FileReader(this.getClass().getResource(input_file).getFile()));
            String readLine = "";
            while ((readLine = b.readLine()) != null) {
				String s = readLine;
				
				String class_ref = s.substring(0, s.indexOf(Constants.SEMSIM_EQUAL)).trim();
				Node id = null;
				if(mode.equals(Constants.SEMSIM_BY_LABEL))
					id = this.wt.getIdByLabel(class_ref);
				else if(mode.equals(Constants.SEMSIM_BY_ID))
					id = NodeFactory.createURI(class_ref);
				else if(mode.equals(Constants.SEMSIM_BY_NAME))
					id = this.wt.getIdByName(class_ref);

				s = s.substring(s.indexOf(Constants.SEMSIM_WEIGHT_DELIM_START)+1, s.indexOf(Constants.SEMSIM_ANNOTATION_DELIM_END)).trim();
				double weight = Double.valueOf(s);
				this.getIcs().put(id, weight);
            }
            b.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	private double _getCoeffContribution(String coeff1, String coeff2) {
		if(coeff1.equalsIgnoreCase(coeff2)) {
			return 1.0d;
		}
		else if(coeff1.equalsIgnoreCase(Constants.COEFF_HIGH)) {
			if(coeff2.equalsIgnoreCase(Constants.COEFF_MEDIUM))
				return 0.8d;
			else if(coeff2.equalsIgnoreCase(Constants.COEFF_LOW))
				return 0.5d;
		}
		else if(coeff1.equalsIgnoreCase(Constants.COEFF_MEDIUM)) {
			if(coeff2.equalsIgnoreCase(Constants.COEFF_HIGH))
				return 0.8d;
			else if(coeff2.equalsIgnoreCase(Constants.COEFF_LOW))
				return 0.8d;
		}
		else if(coeff1.equalsIgnoreCase(Constants.COEFF_LOW)) {
			if(coeff2.equalsIgnoreCase(Constants.COEFF_HIGH))
				return 0.5d;
			else if(coeff2.equalsIgnoreCase(Constants.COEFF_MEDIUM))
				return 0.8d;
		}
		return 0.0d;
	}
	
	private double getCoeffContribution(String coeff1, String coeff2) {
		double result = 0.0d;
		
		double c1 = Double.valueOf(coeff1); 
		double c2 = Double.valueOf(coeff2); 
		
		result = c1*c2;
		
		return result;
	}

	public Vector<Result> sortingResult(Vector<Result> r) {
		Vector<Result> result = new Vector<Result>();
		result.add(r.elementAt(0));
		for(int i=1; i<r.size(); i++) {
			//			System.out.println("i="+i);
			Result elem = (Result)r.get(i);
			boolean inserted = false;
			int resultCurrentSize = result.size(); 
			for(int j=0; (j<resultCurrentSize)&&(inserted==false); j++) {
				//				System.out.println("j="+j);
				Result elem_r = (Result)result.get(j);
				if(elem.getValue()>elem_r.getValue()) {
					//					System.out.println(elem.getValue()+" "+elem_r.getValue());
					result.add(j, elem);
					inserted = true;
				}
			}
			if(inserted==false)
				result.add(elem);
		}
		return result;
	}
	
	private boolean checkConsimBetweenOrderedSiblings(Node n1, Node n2) {
		boolean result = false;
		if(this.getWt().ifSibling(n1, n2)) {
			if(this.getWt().getOrderById(n1)>0 && this.getWt().getOrderById(n2)>0)
				result = true;
		}
		return result;
	}

	private double _checkConsimBetweenOrderedSiblings(Node n1, Node n2) {
		double result = 0.0d;
		String first = n1.getURI().toString();
		String second = n2.getURI().toString();
		String start1 = "";
		String end1 = "";
		String start2 = "";
		String end2 = "";
		if(first.endsWith(Constants.ENDS_IN_HIGH)) {
			start1 = first.substring(0, first.indexOf(Constants.ENDS_IN_HIGH));
			end1 = Constants.ENDS_IN_HIGH;
		}
		else  
			if(first.endsWith(Constants.ENDS_IN_MEDIUM)) {
				start1 = first.substring(0, first.indexOf(Constants.ENDS_IN_MEDIUM));
				end1 = Constants.ENDS_IN_MEDIUM;
			}
			else  
				if(first.endsWith(Constants.ENDS_IN_LOW)) {
					start1 = first.substring(0, first.indexOf(Constants.ENDS_IN_LOW));
					end1 = Constants.ENDS_IN_LOW;
				}
		if(second.endsWith(Constants.ENDS_IN_HIGH)) {
			start2 = second.substring(0, second.indexOf(Constants.ENDS_IN_HIGH));
			end2 = Constants.ENDS_IN_HIGH;
		}
		else  
			if(second.endsWith(Constants.ENDS_IN_MEDIUM)) {
				start2 = second.substring(0, second.indexOf(Constants.ENDS_IN_MEDIUM));
				end2 = Constants.ENDS_IN_MEDIUM;
			}
			else  
				if(second.endsWith(Constants.ENDS_IN_LOW)) {
					start2 = second.substring(0, second.indexOf(Constants.ENDS_IN_LOW));
					end2 = Constants.ENDS_IN_LOW;
				}
		
		if(start1.equalsIgnoreCase(start2)) {
			if(!(start1.equalsIgnoreCase(""))) {
				if(!(end1.equalsIgnoreCase("") || end2.equalsIgnoreCase(""))) {
					result = _consimBetweenOrderedSiblings(end1, end2);
				}
			}
		}
		return result;
	}
	
	private double consimBetweenOrderedSiblings(Node n1, Node n2) {
		double result = 0.0d;
		String lub = "";
		double children_count = 0.0d;
		int orderingFirst = 0; 
		int orderingSecond = 0;
		
		if(n1.getURI().toString().equals(n2.getURI().toString()))
			result = 1.0d;
		else {
			orderingFirst = this.getWt().getOrderById(n1);
			orderingSecond = this.getWt().getOrderById(n2);
			
			//lub = this.bestLubId(this.getWt().lub(pair));
			lub = this.getWt().lub(n1, n2).elementAt(0).getURI().toString();
			children_count = this.getWt().children(NodeFactory.createURI(lub)).size();
			
			result = (1/children_count)*(children_count-Math.abs(orderingFirst-orderingSecond));  
		}
		return result;
	}
	
	private double _consimBetweenOrderedSiblings(String coeff1, String coeff2) {
		if(coeff1.equalsIgnoreCase(coeff2)) {
			return 1.0d;
		}
		else if(coeff1.equalsIgnoreCase(Constants.ENDS_IN_HIGH)) {
			if(coeff2.equalsIgnoreCase(Constants.ENDS_IN_MEDIUM))
				return 0.8d;
			else if(coeff2.equalsIgnoreCase(Constants.ENDS_IN_LOW))
				return 0.5d;
		}
		else if(coeff1.equalsIgnoreCase(Constants.ENDS_IN_MEDIUM)) {
			if(coeff2.equalsIgnoreCase(Constants.ENDS_IN_HIGH))
				return 0.8d;
			else if(coeff2.equalsIgnoreCase(Constants.ENDS_IN_LOW))
				return 0.8d;
		}
		else if(coeff1.equalsIgnoreCase(Constants.ENDS_IN_LOW)) {
			if(coeff2.equalsIgnoreCase(Constants.ENDS_IN_HIGH))
				return 0.5d;
			else if(coeff2.equalsIgnoreCase(Constants.ENDS_IN_MEDIUM))
				return 0.8d;
		}
		return 0.0d;
	}
	
	public String toStringRV(Vector<OFVElem> ofv, WeightedTaxonomy wt) {
		String result = "[";
		for(OFVElem elem:ofv) {
			if (elem.getCoeff().equals(""))
				result = result + wt.getLabelById(elem.getConc_id()) +"; ";
			else
				result = result + wt.getLabelById(elem.getConc_id())+"("+elem.getCoeff()+"); ";
		}
		result = result.substring(0, result.length()-2)+"]";
		return result;
	}
	
	public String toStringAV(Vector<OFVElem> ofv, WeightedTaxonomy wt) {
		String result = "[";
		for(OFVElem elem:ofv) {
			result = result + wt.getLabelById(elem.getConc_id()) +"; ";
		}
		result = result.substring(0, result.length()-2)+"]";
		return result;
	}
	
	public double computeFactor(Node n1, Node n2) {
		double result = 0.0d;
		
		Set<String> specialConcepts_label = new HashSet<String>();
		specialConcepts_label.add("InStock_Giacenza");
		
		for(String sc:specialConcepts_label) {
			Node id = this.getWt().getIdByName(sc);
			if(n1.getURI().toString().startsWith(id.getURI().toString()) && n2.getURI().toString().startsWith(id.getURI().toString()));
				result = 2.0d;
		}
		
		return result;
	}	
	
}

