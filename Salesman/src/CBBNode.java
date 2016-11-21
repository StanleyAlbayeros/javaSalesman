import java.util.ArrayList;

public class CBBNode {
	// arraylist for the vertex we need to visit
	public ArrayList<Integer> notVisitedIndexList;
	// arraylist for the vertex we have visited
	public ArrayList<Integer> visitedIndexList;
	// the index of this node on the original visitslist
	// this spares us the get (object) queries which have O(n)
	// complexity and we can just do remove(originalIndex) in the
	// original visits list to remove an object
	public int originalIndex;
	// current node Lenght
	public double nodeLength;
	// used for the heuristics
	public double nodeWeight;
	
	String debugIndent = "|  ";

	/** Makes a node the problem's root node.
	 * @param visitsArray
	 * @param visitsArrayLength
	 * @param firstVertexIndex
	 * @param cGraph
	 */
	void initializeRootNode(ArrayList<CVertex> visitsArray, final int visitsArrayLength,
			final int firstVertexIndex, CGraph cGraph) {
		nodeLength = 0;
		nodeWeight = 0;
		originalIndex = firstVertexIndex;
		visitedIndexList = new ArrayList<Integer>(visitsArrayLength);
		visitedIndexList.add(firstVertexIndex);
		notVisitedIndexList = new ArrayList<Integer>(visitsArrayLength);
		int i = 0;
		for (CVertex currentVertex : visitsArray) {
			i++;
			notVisitedIndexList.add(i);
		}
		notVisitedIndexList.remove(visitsArrayLength-1);	
//		System.out.println("not visited index list = " + notVisitedIndexList.toString());
	}
	
	CTrack nodeToCTrack (CGraph graph, ArrayList<ArrayList<CBBBestTrack>> bestTrack2DArray, boolean debug, ArrayList<CVertex> visitsArray){
//		debug = true;
		CTrack result = new CTrack(graph);
		int i=0;
		if (debug) {
			System.out.println(" | visitedindexlist size= " + visitedIndexList.size());
			System.out.println(" | besttrack2d array length: " + bestTrack2DArray.size());
			System.out.println(" | this node's length = " + nodeLength);
			System.out.println(" | visited index list" + visitedIndexList.toString());
		}
		result.Append((bestTrack2DArray.get(0).get(visitedIndexList.get(0))).track);
		for (int j=1  ; j < visitedIndexList.size() ; j++){
			if (debug) {
				System.out.println(" i: " + visitedIndexList.get(i) + " j: " + visitedIndexList.get(j));
			}
			CBBBestTrack tempTrack = bestTrack2DArray.get(visitedIndexList.get(i)).get(visitedIndexList.get(j));
			result.Append(tempTrack.track);
			i++;
		}
		
		return result;
	}


}
