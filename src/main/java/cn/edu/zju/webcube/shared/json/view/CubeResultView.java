package cn.edu.zju.webcube.shared.json.view;

import java.util.ArrayList;

import cn.edu.zju.webcube.shared.db.Column;
import cn.edu.zju.webcube.shared.db.CubeResult;

public class CubeResultView {
	private ArrayList<ArrayList<String>> results;
	
	public ArrayList<ArrayList<String>> getResults() {
		return results;
	}

	public CubeResultView(ArrayList<CubeResult> cubeResults, ArrayList<Column> groups, ArrayList<Column> quantities) {
		results = new ArrayList<ArrayList<String>>();
		
		for(CubeResult cs : cubeResults) {
			ArrayList<String> oneRes = new ArrayList<String>();
			
			for(Column g : groups) {
				for(Column c : cs.getAllGroupNames()) {
					if(g.compare(c)) {						
						oneRes.add(cs.getGroupValue(c));
						break;
					}
				}
			}
		
			for(Column q : quantities) {
				for(Column c : cs.getAllAggregationNames()) {
					if(q.compare(c)) {
						oneRes.add(cs.getAggValue(c));
						break;
					}
				}
			}
			
			//oneRes.add(String.valueOf(cs.getCount()));
			results.add(oneRes);
		}
	}
	
	
}
