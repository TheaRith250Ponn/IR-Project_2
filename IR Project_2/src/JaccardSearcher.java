//Name: Teekavu Sucharitakul
//Section:  2
//ID: 5988169
//Name: Thearith Ponn
//Section: 1
//ID: 5988250

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class JaccardSearcher extends Searcher{

	public JaccardSearcher(String docFilename) {
		super(docFilename);
		/************* YOUR CODE HERE ******************/
		
		// list of documents
		// super.documents;
		// after run it then the result is correct so i don't do this part
		// Unnecessary 
					
		/***********************************************/
	}

	@Override
	public List<SearchResult> search(String queryString, int k) {
		/************* YOUR CODE HERE ******************/
		// map of document id and jaccard score
		List<SearchResult> map = new LinkedList<>();
		
		// set of query tokens
		List<String> qTokens = Searcher.tokenize(queryString);

		int uSize,iSize;
		
		// all document
		// set of document tokens
		for(Document docTokens: documents){
			
			// the intersection between doc and query
			Set<String> intersection = new HashSet<String>(qTokens);
			intersection.retainAll(docTokens.getTokens());
			iSize = intersection.size();
			
			// The union between doc and query
			Set<String> union = new HashSet<String>(qTokens);
			union.addAll(docTokens.getTokens());
			uSize = union.size();

			// calculate jaccard score for query and document
			// put to map
            double jScore = iSize/(double)uSize;
			map.add(new SearchResult(docTokens,jScore));
		}
		
		// list all document with top k jaccard score 
		Collections.sort(map);
		return map.subList(0, k);
		/***********************************************/
	}

}
