package it.cnr.iasi.saks.wikidata;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import it.cnr.iasi.saks.http.HttpClient;
import it.cnr.iasi.saks.semrel.Constants;

public class Wikidata {
	HttpClient client = new HttpClient();
	String remoteServer = "";
	String paramName = "";
	
	public Wikidata(String remoteServer, String paramName) {
		this.remoteServer = remoteServer;
		this.paramName = paramName;
	}
	
	
	public HttpClient getClient() {
		return client;
	}

	public void setClient(HttpClient client) {
		this.client = client;
	}	
	
	public String getRemoteServer() {
		return remoteServer;
	}

	public void setRemoteServer(String remoteServer) {
		this.remoteServer = remoteServer;
	}

	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}
	
	public Vector<String> altLabels(String id) {
		Vector<String> result = new Vector<String>();
    	//check if entry with term as name exists

		String paramValue = "SELECT distinct ?altLabel  WHERE{  " +
			  "<"+id+">" + " <http://www.w3.org/2004/02/skos/core#altLabel> ?altLabel ." +
			  "FILTER (lang(?altLabel) = \"en\")}";
//		System.out.println("paramValue = "+paramValue);

		try {
    		String res = client.sendGet(this.getRemoteServer(), this.getParamName(), paramValue);
    		Map<String, String> vars = new HashMap<String, String>();
    		Map<String, Vector<String>> temp = extractResults(res, vars);
    		if(temp.get("altLabel")!=null) {
	    		result = temp.get("altLabel");
	
				Vector<String> toRemove = new Vector<String>();
//				System.out.println("result="+result);
				for(String s:result)
					if(s.length()<3)
						toRemove.add(s);
				
				result.removeAll(toRemove);
    		}
    	}
    	catch(Exception ex) {
    		ex.printStackTrace();
    	}
    	return result;
	}
	
	
	public String label(String id) {
		String result = "";
//		System.out.println("id="+id);
    	//check if entry with term as name exists

		String paramValue = "SELECT distinct ?label  WHERE{  " +
			  "<"+id+"> <http://www.w3.org/2000/01/rdf-schema#label> ?label ."  +  				
					  "FILTER (lang(?label) = \"en\")}";		
//		System.out.println(paramValue);
		
    	try {
    		String res = client.sendGet(this.getRemoteServer(), this.getParamName(), paramValue);
    		Map<String, String> vars = new HashMap<String, String>();
    		vars.put("label", "literal");
    		Map<String, Vector<String>> temp = extractResults(res, vars);
    		if(!temp.get("label").isEmpty())
    			result = temp.get("label").get(0);
    	}
    	catch(Exception ex) {
    		ex.printStackTrace();
    	}
    	return result;
	}	
		
	private Vector<Vector<String>> extractResultsWithQualifier(String rawResult, Map<String, String> vars) {
		Vector<Vector<String>> result = new Vector<Vector<String>>();
		
		String beginDelim_1 = "<result>";
		String endDelim_1 = "</result>";
		while(rawResult.length() > 31) {
			String statement = "";
			String entity = "";
			String qualifier = "";
			String qvalue = "";
			
			int beginIndex_1 = rawResult.indexOf(beginDelim_1)+beginDelim_1.length();
			int endIndex_1 = rawResult.indexOf(endDelim_1);
			String raw_frag = rawResult.substring(beginIndex_1, endIndex_1);
			String beginDelim_2 = "<binding name='statement'>";
			String endDelim_2 = "</binding>";

			int beginIndex_2 = raw_frag.indexOf(beginDelim_2)+beginDelim_2.length();
			int endIndex_2 = raw_frag.indexOf(endDelim_2);
			String frag_1 = raw_frag.substring(beginIndex_2, endIndex_2);
			statement = frag_1.substring(10, frag_1.length()-10);
					
//			System.out.println("statement="+statement);
			raw_frag = raw_frag.substring(endIndex_2+1);
			
			beginDelim_2 = "<binding name='entity'>";
			endDelim_2 = "</binding>";

			beginIndex_2 = raw_frag.indexOf(beginDelim_2)+beginDelim_2.length();
			endIndex_2 = raw_frag.indexOf(endDelim_2);
			frag_1 = raw_frag.substring(beginIndex_2, endIndex_2);
			entity = frag_1.substring(10, frag_1.length()-10);
					
//			System.out.println("entity="+entity);
			raw_frag = raw_frag.substring(endIndex_2+1);
			
			beginDelim_2 = "<binding name='qualifier'>";
			endDelim_2 = "</binding>";

			beginIndex_2 = raw_frag.indexOf(beginDelim_2)+beginDelim_2.length();
			endIndex_2 = raw_frag.indexOf(endDelim_2);
			frag_1 = raw_frag.substring(beginIndex_2, endIndex_2);
			qualifier = frag_1.substring(10, frag_1.length()-10);
					
//			System.out.println("qualifier="+qualifier);
			raw_frag = raw_frag.substring(endIndex_2+1);
			
			beginDelim_2 = "<binding name='qvalue'>";
			endDelim_2 = "</binding>";

			beginIndex_2 = raw_frag.indexOf(beginDelim_2)+beginDelim_2.length();
			endIndex_2 = raw_frag.indexOf(endDelim_2);
			frag_1 = raw_frag.substring(beginIndex_2, endIndex_2);
			if(frag_1.contains("<uri>")) {
				qvalue = frag_1.substring(10, frag_1.length()-10);
//				System.out.println("qvalue="+qvalue);
			}
			else {
				int beginIndex_3 = frag_1.indexOf(">");
				int endIndex_3 = frag_1.indexOf("</literal>");
				qvalue = frag_1.substring(beginIndex_3+1, endIndex_3);
//				System.out.println("qvalue="+qvalue);
			}
			
			//System.exit(0);
			rawResult = rawResult.substring(endIndex_1 + 1);
			
			Vector<String> temp = new Vector<String>();
			temp.add(statement);
			temp.add(entity);
			temp.add(qualifier);
			temp.add(qvalue);
			result.add(temp);
		}
		
		
		return result;
	}
	
	
	private Map<String, Vector<String>> extractResults(String rawResult, Map<String, String> vars) {
		Map<String, Vector<String>> result = new HashMap<String, Vector<String>>();

		for(String v:vars.keySet()) {
			result.put(v, new Vector<String>());
			String beginDelim = "<binding name='"+v+"'>";
			String endDelim = "</binding>";
			int beginIndex = rawResult.indexOf(beginDelim)+beginDelim.length();
			int endIndex = rawResult.indexOf(endDelim);
			Vector<String> res_v = new Vector<String>();

			while(beginIndex>-1 && endIndex>-1) {				
				String temp = rawResult.substring(beginIndex, endIndex);
				rawResult = rawResult.substring(endIndex+endDelim.length());
							StringTokenizer st2 = new StringTokenizer(temp, "\n");
				while(st2.hasMoreTokens()) {
					String line = st2.nextToken().trim();
					if(line.trim().length()>0) {
						String y = "<"+vars.get(v)+">";
						beginIndex = line.indexOf("<"+vars.get(v)+">") + y.length();
						endIndex = line.indexOf("</"+vars.get(v)+">");
						String w = line.substring(beginIndex, endIndex); 
													
						if (vars.get(v).equalsIgnoreCase("literal")) {
							int lastIndex = w.indexOf(">") +1;
							w = w.substring(lastIndex);
						}
						res_v.add(w);
					}
				}
				result.put(v, res_v);
				beginIndex = rawResult.indexOf(beginDelim)+beginDelim.length();
				endIndex = rawResult.indexOf(endDelim);
			}
		}		
		return result;
	}
	
	private Map<String, Vector<String>> extractResults_all(String rawResult, Map<String, String> vars) {
		Map<String, Vector<String>> result = new HashMap<String, Vector<String>>();

//		System.out.println(vars);
		
		for(String v:vars.keySet()) {
//			System.out.println(rawResult);
			result.put(v, new Vector<String>());
			String beginDelim = "<binding name='"+v+"'>";
			String endDelim = "</binding>";
			int beginIndex = rawResult.indexOf(beginDelim)+beginDelim.length();
			int endIndex = rawResult.indexOf(endDelim);
			Vector<String> res_v = new Vector<String>();

			while(beginIndex>-1 && endIndex>-1) {				
//				System.out.println(beginIndex);
//				System.out.println(endIndex);
				String temp = rawResult.substring(beginIndex, endIndex);
				rawResult = rawResult.substring(endIndex+endDelim.length());
							StringTokenizer st2 = new StringTokenizer(temp, "\n");
				while(st2.hasMoreTokens()) {
					String line = st2.nextToken().trim();
					if(line.trim().length()>0) {
//						System.out.println("v="+v);
						String y = "<"+vars.get(v)+">";
						beginIndex = line.indexOf("<"+vars.get(v)+">") + y.length();
						endIndex = line.indexOf("</"+vars.get(v)+">");
//						System.out.println("line = "+line);
						String w = line.substring(beginIndex, endIndex); 
													
						if (vars.get(v).equalsIgnoreCase("literal")) {
							int lastIndex = w.indexOf(">") +1;
							w = w.substring(lastIndex);
						}
						res_v.add(w);
					}
				}
				result.put(v, res_v);
				beginIndex = rawResult.indexOf(beginDelim)+beginDelim.length();
				endIndex = rawResult.indexOf(endDelim);
			}
		}		
		return result;
	}

	
	
	// *** METHODS FOR PERSON

	public String family_name(String id) {
		String result = "";
    	//check if entry with term as name exists

		String paramValue = "SELECT ?fnLabel WHERE {" +
				"<"+id+"> <"+Constants.WIKIDATA_PROP_FAMILYNAME+"> ?fn ." +
				"?fn <"+ Constants.RDFS_LABEL+"> ?fnLabel ." +
				"FILTER ( lang(?fnLabel) = \"en\" ) "+
				"}";
			
//		System.out.println("paramValue = "+paramValue);
		
    	try {
    		String res = client.sendGet(this.getRemoteServer(), this.getParamName(), paramValue);
    		Map<String, String> vars = new HashMap<String, String>();
    		vars.put("fnLabel", "literal");
    		Map<String, Vector<String>> temp = extractResults(res, vars);
//    		System.out.println(temp.get("fnLabel"));
    		if(temp.get("fnLabel").size()>0)
    			result = temp.get("fnLabel").get(0);
    	}
    	catch(Exception ex) {
    		ex.printStackTrace();
    	}
 //   	System.out.println("family_name = -"+result+"-");
    	return result;
	}	
	
	
	
	public Vector<Vector<String>> structuredStatements(String id) {
		Vector<Vector<String>> result = new Vector<Vector<String>>();
		
		String paramValue = "SELECT ?statement ?entity ?qualifier ?qvalue  WHERE { " +
				"<"+id+"> ?p [?statement ?entity; ?qualifier ?qvalue] . " +
			    "FILTER(STRSTARTS(STR(?statement), \"http://www.wikidata.org/prop/statement\")) . " +
			    "FILTER(STRSTARTS(STR(?entity), \"http://www.wikidata.org/entity\")) . " + 
			    "FILTER(STRSTARTS(STR(?qualifier), \"http://www.wikidata.org/prop/qualifier/P\")) . " +
			"}";		

		
//		System.out.println(paramValue);
		
    	try {
    		String res = client.sendGet(this.getRemoteServer(), this.getParamName(), paramValue);
//    		System.out.println(res);
    		result = extractResultsWithQualifier(res, null);
    	}
    	catch(Exception ex) {
    		ex.printStackTrace();
    	}
    	return result;
	}

	
	public Vector<Vector<String>> all(String id) {
		Vector<Vector<String>> result = new Vector<Vector<String>>();
//		System.out.println("id="+id);
    	//check if entry with term as name exists

		String paramValue = "SELECT distinct ?pred ?obj  WHERE{  " +
			  "<"+id+"> ?pred ?obj ."  +  				
					  "FILTER (lang(?obj) = \"en\")}";		
//		System.out.println(paramValue);
		
    	try {
    		String res = client.sendGet(this.getRemoteServer(), this.getParamName(), paramValue);
    		Map<String, String> vars = new HashMap<String, String>();
    		vars.put("pred", "uri");
    		vars.put("obj", "literal");
    		Map<String, Vector<String>> temp = extractResults_literal(res, vars);
    		Vector<String> v = new Vector<String>();
    		if(!temp.get("pred").isEmpty()) {
	    			v.add(temp.get("pred").get(0));
	    		if(!temp.get("obj").isEmpty())
	    			v.add(temp.get("obj").get(1));
	    		result.add(v);
    		}
    		
    	}
    	catch(Exception ex) {
    		ex.printStackTrace();
    	}
    	return result;
	}	
	
	private Map<String, Vector<String>> extractResults_literal(String rawResult, Map<String, String> vars) {
		Map<String, Vector<String>> result = new HashMap<String, Vector<String>>();
//		System.out.println(rawResult);
		for(String v:vars.keySet()) {
			result.put(v, new Vector<String>());
			String beginDelim = "<binding name='"+v+"'>";
			String endDelim = "</binding>";
			int beginIndex = rawResult.indexOf(beginDelim)+beginDelim.length();
			int endIndex = rawResult.indexOf(endDelim);
			Vector<String> res_v = new Vector<String>();

			while(beginIndex>-1 && endIndex>-1) {
//				System.out.println(beginIndex);
//				System.out.println(endIndex);
//				System.out.println(rawResult);
				String temp = rawResult.substring(beginIndex, endIndex);
				rawResult = rawResult.substring(endIndex+endDelim.length());
							StringTokenizer st2 = new StringTokenizer(temp, "\n");
				while(st2.hasMoreTokens()) {
					String line = st2.nextToken().trim();
					if(line.trim().length()>0) {
//						System.out.println("v="+v);
						String y = "<"+vars.get(v)+">";
						beginIndex = line.indexOf("<"+vars.get(v)+">") + y.length();
						endIndex = line.indexOf("</"+vars.get(v)+">");
//						System.out.println("line = "+line);
						String w = line.substring(beginIndex, endIndex); 
													
						if (vars.get(v).equalsIgnoreCase("literal")) {
							int lastIndex = w.indexOf(">") +1;
							w = w.substring(lastIndex);
						}
						res_v.add(w);
					}
				}
				result.put(v, res_v);
				beginIndex = rawResult.indexOf(beginDelim)+beginDelim.length();
				endIndex = rawResult.indexOf(endDelim);
			}
		}		
		return result;
	}
}