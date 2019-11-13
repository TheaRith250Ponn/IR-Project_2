//Name: Teekavu Sucharitakul
//Section:  2
//ID: 5988169
//Name: Thearith Ponn
//Section: 1
//ID: 5988250

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;


public class TFIDFSearcher extends Searcher
{	
	// declare Number of documents
	public Double dn;
	// declare map (BagOfWords x iDF) of Term and inverted document frequency
	public Map <String, Double> idf = new HashMap<String,Double>();
	
	public Map <Document, Map<String, Double>> tfidf_doc = new HashMap<Document,Map<String, Double>>();
	public List<SearchResult> nan = new ArrayList<>();
	public TFIDFSearcher(String docFilename) {
		super(docFilename);
		/************* YOUR CODE HERE ******************/
		dn = (double) this.documents.size();
		// find DF
		// for each document
		for (Document doc : documents) 
		{ 
			// for each token
			for (String token : new HashSet<>(doc.getTokens()) )
			{ 
				// if map contain token
				if(!idf.containsKey(token)) {
					// update value
					idf.put(token, 1.0);
					
				}
				// else 
				else {
					// put a new token to map
					idf.replace(token, idf.get(token) + 1.0);
				}
			}
		}
		for (String token: idf.keySet()) {
			double  idf_score =  Math.log10(1.0 + (dn / idf.get(token)) );
			idf.replace(token, idf_score);
		}
		for (Document doc : documents) 
		{ 

			
			Map<String, Double> tfidf_dic = new HashMap<String, Double>();
			for (String token : new HashSet<>(doc.getTokens()) )
			{ 
				double tf = TF(token,doc.getTokens());
				
				// calculate TF score = 1+log( tf )
				// Math.log()
				double tf_score = 1.0+Math.log10((double) tf);
				double  idf_score = idf.get (token);
				
				
				// TFIDF score = TF x IDF
				double tfidf_score = tf_score * idf_score;
				
				tfidf_dic.put(token, tfidf_score);
				//tfidf_doc.get(doc).put(token, tfidf_score);
			}
			tfidf_doc.put(doc, new HashMap<String, Double>(tfidf_dic));
			//System.out.println(tfidf_doc.get(doc));
		}
		/***********************************************/
	}
	
	@Override
	public List<SearchResult> search(String queryString, int k) {
		/************* YOUR CODE HERE ******************/
		Map <Document, Map<String, Double>> tfidf_query = new HashMap<Document,Map<String, Double>>();
		List<String> query = new ArrayList<>(TFIDFSearcher.tokenize(queryString));
		// declare list of search result 
		List<SearchResult> sl = new ArrayList<>();
		// for each document
		for (Document doc : this.documents) {
			//tfidf_query.put(doc, new HashMap<String, Double>());
			Map<String, Double> tfidf_dic = new HashMap<String, Double>();
			// for each token in query
			for (String token : new HashSet<>(query) ) {
				
				if(idf.containsKey(token)) {
					// find TF
					double tf = TF(token,query);
					double tf_score = 1.0+Math.log10( tf);
					
					
					// calculate IDf score = log( dn / df_score )
					double idf_score = idf.get (token);

					
					// TFIDF score = TF x IDF
					double tfidf_score = tf_score * idf_score;
					
					
					//tfidf_query.get(doc).put(token, tfidf_score);
					tfidf_dic.put(token, tfidf_score);
				}
				
			}
			//tfidf_query.put(doc, new HashMap<String, Double>(tfidf_dic));
			double cos = cosineSimilarity(tfidf_dic, tfidf_doc.get(doc));
			//System.out.println(tfidf_query.get(doc));
			if(Double.isNaN(cos)){
            	nan.add(new SearchResult(doc, cos));
            } else {
            	sl.add(new SearchResult(doc, cos));
            }
		}
		
		
		
		// put to search result (document, score)
		//sl = new ArrayList<>(resultbycosineSimilarity(tfidf_doc, tfidf_query));
		if(sl.isEmpty()) {
			return nan.subList(0, k);
		}
		// list all document with top k TFIDF score
		Collections.sort(sl);
		return sl.subList(0, k);
		/***********************************************/
	}
	
	public double TF(String term, List<String> tokens) {
		//if(!tokens.contains(term)) return 0.0;
		// declare 
		int f=0;
		// for each tokens in document
		for (String token : tokens )
		{ 
			// if term == token
			if (term.equals(token)) {
				// count
				f++;
			}
		}
		return (double) f;
		
	}
	
	public double cosineSimilarity(Map<String, Double> d1, Map<String, Double> d2) {
		double dot = 0.0;
		double mag1 = 0.0;
		double mag2 = 0.0;
		double temp1 = 0.0, temp2 = 0.0;
		Set<String> tokenset = new HashSet<String>(d1.keySet());
		tokenset.addAll(d2.keySet());
		for(String token : tokenset) {
			temp1 = 0.0;
			temp2 = 0.0;
			if(d1.get(token) != null) {
				temp1 = d1.get(token);
			}
			if(d2.get(token) != null) {
				temp2 = d2.get(token);
			}
			dot += temp1 * temp2;
			mag1 += Math.pow(temp1, 2);
			mag2 += Math.pow(temp2, 2);
		}
		
		if(mag1 == 0 || mag2 == 0){
			return Double.NaN;
		}
		
		mag1 = Math.sqrt(mag1);
		mag2 = Math.sqrt(mag2);
		return dot/ (mag1 * mag2);
	}
}
