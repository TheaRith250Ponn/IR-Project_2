//Name: Teekavu Sucharitakul
//Section: 2
//ID: 5988169
//Name: Thearith Ponn
//Section: 1
//ID: 5988250

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;

public class SearcherEvaluator {
	private List<Document> queries = null;				//List of test queries. Each query can be treated as a Document object.
	private  Map<Integer, Set<Integer>> answers = null;	//Mapping between query ID and a set of relevant document IDs
	
	public List<Document> getQueries() {
		return queries;
	}

	public Map<Integer, Set<Integer>> getAnswers() {
		return answers;
	}

	/**
	 * Load queries into "queries"
	 * Load corresponding documents into "answers"
	 * Other initialization, depending on your design.
	 * @param corpus
	 */
	public SearcherEvaluator(String corpus)
	{
		String queryFilename = corpus+"/queries.txt";
		String answerFilename = corpus+"/relevance.txt";
		
		//load queries. Treat each query as a document. 
		this.queries = Searcher.parseDocumentFromFile(queryFilename);
		this.answers = new HashMap<Integer, Set<Integer>>();
		//load answers
		try {
			List<String> lines = FileUtils.readLines(new File(answerFilename), "UTF-8");
			for(String line: lines)
			{
				line = line.trim();
				if(line.isEmpty()) continue;
				String[] parts = line.split("\\t");
				Integer qid = Integer.parseInt(parts[0]);
				String[] docIDs = parts[1].trim().split("\\s+");
				Set<Integer> relDocIDs = new HashSet<Integer>();
				for(String docID: docIDs)
				{
					relDocIDs.add(Integer.parseInt(docID));
				}
				this.answers.put(qid, relDocIDs);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Returns an array of 3 numbers: precision, recall, F1, computed from the top *k* search results 
	 * returned from *searcher* for *query*
	 * @param query
	 * @param searcher
	 * @param k
	 * @return
	 */
	public double[] getQueryPRF(Document query, Searcher searcher, int k)
	{
		/*********************** YOUR CODE HERE *************************/
		
		double[] P_R_F1 = new double[3];
		//double Precision, Recall, F1;
		
        // R the set of relevant documents retrieved by the searcher //Retrieve doc
	    List<SearchResult> R = searcher.search(query.getRawText(),k); 

        
        // G the set of ground-truth relevant documents (justified by human experts)
		Set<Integer> G = answers.get(query.getId());
		
        Set<Integer> intersection = new HashSet<>();
        
        // get id from R then store in set of intersection   
		for(SearchResult result : R){
			if(G.contains(result.getDocument().getId())){
				intersection.add(result.getDocument().getId());
			}
		}
		
		// calculate precision
		if(R.size() > 0) {
			P_R_F1[0] = intersection.size() / (double) R.size();
		}
		
		// calculate recall
		if(G.size() > 0) {
			P_R_F1[1] = intersection.size() / (double) G.size();
		}
		
		//calculate F1
		if(P_R_F1[0] > 0 && P_R_F1[1] > 0 ) {
			P_R_F1[2] = (2 * P_R_F1[0] * P_R_F1[1]) / (P_R_F1[0] + P_R_F1[1]);
		}
		
		return P_R_F1;
		/****************************************************************/
	}
	
	/**
	 * Test all the queries in *queries*, from the top *k* search results returned by *searcher*
	 * and take the average of the precision, recall, and F1. 
	 * @param searcher
	 * @param k
	 * @return
	 */
	public double[] getAveragePRF(Searcher searcher, int k)
	{
		/*********************** YOUR CODE HERE *************************/
		double[] avgP_R_F1 = new double[3];
		double Precision = 0.0;
		double Recall = 0.0; 
		double F1 = 0.0;
		
		int Q = queries.size();
		
	    //The summation for each Precision, Recall and F1
		for(Document doc : queries) {
			// Explain 
			double[] temp = getQueryPRF(doc, searcher, k);
			Precision += temp[0];
			Recall += temp[1];
			F1 += temp[2];
		}
		
		// Calculate the average of the Precision
		avgP_R_F1[0] =  Precision / Q;
		
		//Calculate the average of the Recall
		avgP_R_F1[1] = Recall / Q;
		
		//Calculate the average of the F1
		avgP_R_F1[2] = F1 / Q;
		
		return avgP_R_F1;
		/****************************************************************/
	}
}
