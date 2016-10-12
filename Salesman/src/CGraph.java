import java.awt.*;
import java.util.*;

import java.io.*;
//import java.util.Scanner;

public class CGraph {
	public ArrayList<CVertex> m_Vertices;
	boolean m_Solved;
	
	////////////debug variables///////////
	final static boolean verbose = true;
	static String debugIndent = "";
	////////////debug variables///////////
	
	public CGraph() {
		m_Vertices = new ArrayList<CVertex>();
		m_Solved = false;
	}

	public void Clear() {
		m_Vertices.clear();
	}

	// Vertices ----------------------------------------------------------------
	public CVertex FindVertex(double x, double y) {
		for (int i = 0; i < m_Vertices.size(); ++i) {
			CVertex v = m_Vertices.get(i);
			if (v.m_Point.m_X == x && v.m_Point.m_Y == y)
				return v;
		}
		return null;
	}

	public CVertex GetVertex(double x, double y) throws Exception {
		CVertex v = FindVertex(x, y);
		if (v == null)
			throw new Exception("El punto (" + x + "," + ") no pertenece al grafo");
		return v;
	}

	public CVertex GetVertex(CPoint p) throws Exception {
		return GetVertex(p.m_X, p.m_Y);
	}

	public boolean MemberP(CVertex v) {
		return m_Vertices.contains(v);
	}

	public int nVertices() {
		return m_Vertices.size();
	}

	// Edges -------------------------------------------------------------------
	public void Add(double x1, double y1, double x2, double y2) {
		CVertex p1 = FindVertex(x1, y1);
		if (p1 == null) {
			p1 = new CVertex(x1, y1);
			m_Vertices.add(p1);
		}
		CVertex p2 = FindVertex(x2, y2);
		if (p2 == null) {
			p2 = new CVertex(x2, y2);
			m_Vertices.add(p2);
		}
		if (!p1.m_Neighbords.contains(p2)){
			p1.m_Neighbords.add(p2);
			p1.m_allowedVisits++;
		}
		if (!p2.m_Neighbords.contains(p1)){
			p2.m_Neighbords.add(p1);
			p2.m_allowedVisits++;
		}
	}

	public int nEdges() {
		int n = 0;
		for (int i = 0; i < m_Vertices.size(); ++i)
			n = n + m_Vertices.get(i).m_Neighbords.size();
		return n / 2;
	}    
	// Draw --------------------------------------------------------------------
	public void Draw(Graphics g, double esc) {

		g.setColor(new Color(100, 100, 100));
		for (int i = 0; i < m_Vertices.size(); ++i) {
			CVertex p1 = m_Vertices.get(i);
			int x1 = (int) Math.round(p1.m_Point.m_X * esc);
			int y1 = (int) Math.round(p1.m_Point.m_Y * esc);
			for (Iterator<CVertex> iter = p1.m_Neighbords.descendingIterator(); iter.hasNext();) {
				CPoint p2 = iter.next().m_Point;
				g.drawLine(x1, y1, (int) Math.round(p2.m_X * esc), (int) Math.round(p2.m_Y * esc));
			}
		}
		g.setColor(new Color(0, 128, 255));
		for (int i = 0; i < m_Vertices.size(); ++i) {
			CVertex p1 = m_Vertices.get(i);
			int x1 = (int) Math.round(p1.m_Point.m_X * esc);
			int y1 = (int) Math.round(p1.m_Point.m_Y * esc);
			g.fillOval(x1 - 4, y1 - 4, 9, 9);
			//This writes the node number next to each node.
			g.setFont(new Font("SansSerif",Font.BOLD,14));
			g.setColor(new Color(255,64,0));
			String vertexNum = Integer.toString(i);
			p1.m_VertexID = i;
			//g.drawString(vertexNum,x1+15,y1);
		}
	}

	// AddRectHull -------------------------------------------------------------
	public void AddRectHull(CPoint min, CPoint max) {
		for (int i = 0; i < m_Vertices.size(); ++i) {
			CPoint p = m_Vertices.get(i).m_Point;
			if (p.m_X < min.m_X)
				min.m_X = p.m_X;
			if (p.m_Y < min.m_Y)
				min.m_Y = p.m_Y;
			if (p.m_X > max.m_X)
				max.m_X = p.m_X;
			if (p.m_Y > max.m_Y)
				max.m_Y = p.m_Y;
		}
	}

	// Write ----------------------------------------------------------------
	public void Write(String filename) throws Exception {
		File f = new File(filename);
		BufferedWriter bw = new BufferedWriter(new FileWriter(f));
		bw.write("GRAPH\n");
		for (int i = 0; i < m_Vertices.size(); ++i) {
			CVertex v = m_Vertices.get(i);
			CPoint p1 = v.m_Point;
			for (Iterator<CVertex> iter = v.m_Neighbords.iterator(); iter.hasNext();) {
				CPoint p2 = iter.next().m_Point;
				bw.write(p1.m_X + " " + p1.m_Y + " " + p2.m_X + " " + p2.m_Y + "\n");
			}
		}
		bw.close();
	}

	// Read --------------------------------------------------------------------
	public void Read(String filename) throws Exception {
		Clear();
		File f = new File(filename);
		Scanner s = new Scanner(f);
		try {
			String str = s.nextLine();
			if (!str.equalsIgnoreCase("GRAPH"))
				throw new Exception(filename + " no tiene formato de fichero de grafos (" + str + ")");
			// leer v�rtices
			while (s.hasNextLine()) {
				String linea = s.nextLine();
				// System.out.println(linea);
				Scanner sl = new Scanner(linea);
				sl.useLocale(Locale.US);
				double x1 = sl.nextDouble();
				double y1 = sl.nextDouble();
				double x2 = sl.nextDouble();
				double y2 = sl.nextDouble();
				sl.close();
				Add(x1, y1, x2, y2);
			}
		} finally {
			s.close();
		}
	}

	// Compare dijkstra --------------------------------------------------------------------
	/** CompareDijkstra compares this graph's m_vertex list of distances with a file
	 * 
	 * @param filename file to read
	 * @throws Exception the file was not found
	 */
	public int CompareDijkstra(String filename) throws Exception {
		System.out.println();
		File f = new File(filename);
		Scanner s = new Scanner(f);
		int failures=0;
		try {
			String str = s.nextLine();
			if (!str.equalsIgnoreCase("DISTANCES"))
				throw new Exception(filename + " no tiene formato de fichero de distancias (" + str + ")");			
			double tmp1 = m_Vertices.size();
			for (int i = 0; i < tmp1; i++){				
				CVertex v = m_Vertices.get(i);
				String linea = s.nextLine();
				Scanner sc = new Scanner(linea);
				sc.useLocale(Locale.US);
				double txtdistance = sc.nextDouble();
				double currentdistance = v.m_DijkstraDistance;
				if (txtdistance != currentdistance){
					failures++;
				}
				sc.close();	
			}		
		} finally {
			s.close();
		}
//		System.out.println("Errors: " + failures);
		return failures;
	}
	
	// PrintDistances ----------------------------------------------------------
	public void PrintDistances() throws Exception {
		System.out.print("DISTANCES ");
		for (int i = 0; i < m_Vertices.size(); ++i) {
			CVertex v = m_Vertices.get(i);
			System.out.print(i + ":" + v.m_DijkstraDistance + " ");
		}
		System.out.println();
	}
	
	// WriteDistances ----------------------------------------------------------
	public void WriteDistances(String filename) throws Exception {
		File f = new File(filename);
		BufferedWriter bw = new BufferedWriter(new FileWriter(f));
		bw.write("DISTANCES\n");
		for (int i = 0; i < m_Vertices.size(); ++i) {
			CVertex v = m_Vertices.get(i);
			bw.write(v.m_DijkstraDistance + "\n");
		}
		bw.close();
	}
	
	// Dijkstra
	// -------------------------------------------------------------------

	/** Dijkstra implements the Dijkstra algorithm.
	 * 
	 * This method recieves a source Vertex and finds the shortest distance
	 * to each other Vertex in a given graph.
	 * 
	 * @param start The vertex from which we want to know the distances to 
	 * every other vertex.
	 */
	public void Dijkstra(CVertex start){
		
		double maxValue = 99999999;

		LinkedList<CVertex> vertexQ = new LinkedList<CVertex>();
		
		// Assign a very high value to each vertex dijkstra distance
		
		for (CVertex vert : this.m_Vertices) {
			vert.m_DijkstraDistance = maxValue;
			vert.m_DijkstraVisit = false;
		}

		start.m_DijkstraDistance = 0;
		start.m_DijkstraVisit = true;
		CVertex currentVertex = start;
		vertexQ.add(currentVertex);
		
		while ( vertexQ.peek() != null ) {
			double minDist = maxValue;
			double distToCurrent;
			for (CVertex lookupVertex : currentVertex.m_Neighbords) {
				distToCurrent = lookupVertex.m_Point.Distance(currentVertex.m_Point) + currentVertex.m_DijkstraDistance;

				if (distToCurrent < lookupVertex.m_DijkstraDistance) {					
					//System.out.print("lookupVertex old DIST: " + lookupVertex.m_DijkstraDistance);
					lookupVertex.m_DijkstraDistance = distToCurrent;
					lookupVertex.m_DijkstraPrevious = currentVertex;
					lookupVertex.m_DijkstraVisit = false;
					//System.out.print(" lookupVertex new DIST: " + lookupVertex.m_DijkstraDistance + "\n");		
					vertexQ.add(lookupVertex);	
				}				
			}			
			CVertex minDistVertex = currentVertex;
			for (CVertex lookupVertex : vertexQ){
				if ( (lookupVertex.m_DijkstraDistance <= minDist) ){
					minDistVertex = lookupVertex;
					minDist = minDistVertex.m_DijkstraDistance;
				}
			}
			currentVertex.m_DijkstraVisit = true;
			currentVertex = minDistVertex;
			vertexQ.remove(minDistVertex);
		}
	}

	// DijkstraQueue
	// -------------------------------------------------------------------
	public void DijkstraQueue(CVertex start) throws Exception {
		double maxValue = 99999999; 

		Comparator<CVertex> vertexCompare = new VertexCompare();
		PriorityQueue<CVertex> vertexQ = new PriorityQueue<CVertex>(vertexCompare);
		
		// Assign a very high value to each vertex dijkstra distance

		for (CVertex vert : this.m_Vertices) {
			vert.m_DijkstraDistance = maxValue;
			vert.m_DijkstraVisit = false;
		}

		start.m_DijkstraDistance = 0;
		start.m_DijkstraVisit = true;

		CVertex currentVertex = start;
		vertexQ.add(currentVertex);

		while ( currentVertex != null ) {

			double distToCurrent;

			for (CVertex lookupVertex : currentVertex.m_Neighbords) {
				distToCurrent = lookupVertex.m_Point.Distance(currentVertex.m_Point) + currentVertex.m_DijkstraDistance;

				if (distToCurrent < lookupVertex.m_DijkstraDistance) {
//					System.out.print("lookupVertex old DIST: " + lookupVertex.m_DijkstraDistance);
					lookupVertex.m_DijkstraDistance = distToCurrent;
					lookupVertex.m_DijkstraPrevious = currentVertex;
//					System.out.print(" lookupVertex new DIST: " + lookupVertex.m_DijkstraDistance + "\n");
					if (!lookupVertex.m_DijkstraVisit) {
					vertexQ.offer(lookupVertex);
					}
				}
				//System.out.print(" .");

				
			}
			currentVertex.m_DijkstraVisit = true;
			currentVertex = vertexQ.poll();
		}
		//System.out.println("\t\t\tdone dijkstraqueue");

		
	}

	// SalesmanTrackGreedy
	// -----------------------------------------------------------
	public CTrack SalesmanTrackGreedy(CVisits visits) throws Exception {
		// IMPLEMENTAR LA FUNCION
		throw new Exception("SalesmanTrackGreedy no implementado");
	}

	// =====================================================================================
	// SalesmanTrackBacktracking
	// ===========================================================
	// =====================================================================================
	// SalesmanTrackBacktracking
	// -----------------------------------------------------------------
	public CTrack SalesmanTrackBacktracking(CVisits visits) throws Exception {
//TODO
		CTrack resultTrack = new CTrack(this);	
		CTrack tmpTrack = new CTrack(this);		
		CTrack worstSolution = new CTrack(this);		
		LinkedList<CVertex> visitList = visits.toCVertexList(this);
		CVertex firstVertex = visitList.getFirst();
		CVertex lastVertex = visitList.getLast();
		
		for (CVertex tmp : visitList){
			tmp.m_VertexToVisit = true;
		}		

		visitList.remove(firstVertex);
		
		//TODO foo call backtrack
		for (CVertex tmp : this.m_Vertices){
			worstSolution.AddLast(tmp);
		}
		tmpTrack.AddFirst(firstVertex);
		worstSolution.m_solutionTrack = false;
		
		resultTrack = recursiveBacktracking(tmpTrack, worstSolution, visitList, lastVertex);
		
		return resultTrack;
		
	}

	public CTrack recursiveBacktracking(CTrack partialSolution, CTrack bestSolution, LinkedList<CVertex> visitList, CVertex lastVertex) throws Exception{
//TODO the way partial solution arrives to this part is detrimental, it's so short that it¡s instantly the best length, always.
    if (verbose) {
      System.out.println(debugIndent + "VisitList: " + visitList.size() + " Entering with: " + partialSolution.toString());
      debugIndent = debugIndent + "|  ";
  }
    
    
		int visitElementIndex = 0;
		boolean visitElementRemoved = false;
		CTrack tmpTrack = new CTrack(this);
		
//	if partial is a complete solution
		if (visitList.isEmpty()){
			if(partialSolution.m_Vertices.getLast() == lastVertex){
//		then minCost = min(minCost,the cost of the solution represented by partial)
//			partialSolution.AddLast(lastVertex);
				partialSolution.m_solutionTrack = true;
				bestSolution = CTrack.minLength(bestSolution, partialSolution, this);
				if (verbose) {
					debugIndent = debugIndent.substring(3);
			        System.out.println(debugIndent + "-------------------------------------------------------------------------------");
			        System.out.println(debugIndent + "found last vertex path: " + bestSolution.toString() + " " + bestSolution.isTrackSolvedtoString());
			        System.out.println(debugIndent + "-------------------------------------------------------------------------------");
			    }
				this.m_Solved = true;
				return bestSolution;
				//throw new Exception("Path found");
			} else {
				//TODO HOW TO EXIT IF IT'S EMPTY BUT THE LAST VERTEX ISN'T THE LAST TO VISIT -> EXCEPTIONS!
				if (verbose) {
					System.out.println(debugIndent + "Empty but I'm not the last vertex you should visit.");
				}
				throw new Exception("visitlist empty, not in last vertex");
			}
		}
//	else 
		else {
//		for each possible option for the next choice to be made
			boolean allVisited = true;
			for (CVertex tmp : partialSolution.m_Vertices.getLast().m_Neighbords){
				allVisited = (tmp.m_VisitedVertex && allVisited);
			}
			if (allVisited){
				throw new Exception("No more neighbors");
			}
			partialSolution.m_solutionTrack = false;
			for (CVertex tmpVertex : partialSolution.m_Vertices.getLast().m_Neighbords){
//			do try that option, that is, change partial to incorporate this choice
				
//				if (tmpVertex == lastVertex){
//					if (visitList.contains(lastVertex)){
//						if (visitList.size()==1){
//							partialSolution.AddLast(tmpVertex);
//							bestSolution = CTrack.minLength(bestSolution, partialSolution, this);
//							if (verbose) {
//				        System.out.println(debugIndent + "found last vertex " + bestSolution.toString());
//							}
//						} continue;
//					}
//				}
				
				partialSolution.AddLast(tmpVertex);
				tmpVertex.m_allowedVisits --;
				if (visitList.contains(tmpVertex)){					
//					if (visitList.size()==1){
//						if(tmpVertex == lastVertex){
////					then minCost = min(minCost,the cost of the solution represented by partial)
////						partialSolution.AddLast(lastVertex);
//							bestSolution = CTrack.minLength(bestSolution, partialSolution, this);
//							if (verbose) {
//						        System.out.println(debugIndent + "found last vertex " + bestSolution.toString());
//						    }
//							continue;
//						} else {
//							//TODO HOW TO EXIT IF IT'S EMPTY BUT THE LAST VERTEX ISN'T THE LAST TO VISIT
//							if (verbose) {
//								System.out.println(debugIndent + "kkkkk Empty but I'm not the last vertex you should visit kkkkk");
//							} continue;
//						}
//					}
					
					visitElementIndex = visitList.indexOf(tmpVertex);
					visitElementRemoved = visitList.remove(tmpVertex);
					}
//			if partial cannot become better than minCost
				if (partialSolution.Length() >= bestSolution.Length()){
//				then skip // prune
					if (verbose) {
						double distDiff = partialSolution.Length() - bestSolution.Length();
						System.out.println(debugIndent + distDiff+" longer, prunning");
					}
				} else {
					if (tmpVertex.m_allowedVisits >= 0) {
						if (tmpVertex.m_allowedVisits == 0) {
							tmpVertex.m_VisitedVertex = true;
						}
						// else minCost ← min(minCost, Opt Backtrack(partial,best, visitlist))
						try {
							tmpTrack = recursiveBacktracking(partialSolution, bestSolution, visitList, lastVertex);
							bestSolution = CTrack.minLength(bestSolution, tmpTrack, this);
//							if (this.m_Solved) {
//								bestSolution = CTrack.minLength(bestSolution, tmpTrack, this);
//							} else {
//								bestSolution = tmpTrack;
//							}

						} catch (Exception e) {
							partialSolution.removeLast();
							tmpVertex.m_allowedVisits++;
							if (tmpVertex.m_allowedVisits > 0) {
								tmpVertex.m_VisitedVertex = true;
							}
							if (visitElementRemoved) {
								visitList.add(visitElementIndex, tmpVertex);
								visitElementRemoved = false;
							}
							if (verbose) {
								debugIndent = debugIndent.substring(3);
								System.out.println(debugIndent + "Caught exception: " + e.getMessage());
							}

							continue;
						}
						

						// bestSolution = recursiveBacktracking(partialSolution, bestSolution, visitList,
						// lastVertex);
					}
				}
//			change partial back to undo the choice made at the for each's start
				partialSolution.removeLast();
				tmpVertex.m_allowedVisits++;
				if (tmpVertex.m_allowedVisits>0) {
					tmpVertex.m_VisitedVertex = true;
				}
				if (visitElementRemoved){
					visitList.add(visitElementIndex, tmpVertex);
					visitElementRemoved = false;
				}
			}
		}
//	return minCost	
		
		if (bestSolution.m_solutionTrack) {
			if (verbose) {
				debugIndent = debugIndent.substring(3);
				System.out.println(debugIndent + "Returning: " + bestSolution.toString()+ " " + bestSolution.isTrackSolvedtoString());
			}
			return bestSolution;
		} else { 
			throw new Exception("best solution is not a solution track");
		}
		
	}
	
	
	public CTrack RecursiveBacktracking2(CVertex currentVertex , CVertex lastVertex, LinkedList<CVertex> visits) throws Exception{
//TODO
		CTrack resultTrackSection = new CTrack(this);
	    if (verbose) {
	        System.out.println(debugIndent + "Entering solvable ");
	        debugIndent = debugIndent + "|  ";
	    }
		if (currentVertex == lastVertex){
			if (visits.size()==1){
				if (visits.peekFirst() == lastVertex){
					if (verbose) {
						debugIndent = debugIndent.substring(3);
				        System.out.println(debugIndent + "found last vertex ");
				        debugIndent = debugIndent + "|  ";
				    }
					resultTrackSection.AddFirst(lastVertex);
				} else {
					if (verbose) {
						debugIndent = debugIndent.substring(3);
				        System.out.println(debugIndent + "peek != last, returning ");
				        debugIndent = debugIndent + "|  ";
				    }
					resultTrackSection.Clear();
				}
				
			} else {
				if (verbose) {
					debugIndent = debugIndent.substring(3);
			        System.out.println(debugIndent + "visits= "+visits.size()+" , returning ");
			        debugIndent = debugIndent + "|  ";
			    }
				resultTrackSection.Clear();
			}			
		} else {
			if (currentVertex.m_Neighbords.size() > 1) {
				for (CVertex tmpCurrent : currentVertex.m_Neighbords) {
					
					if (!tmpCurrent.m_VisitedVertex) {
						tmpCurrent.m_allowedVisits--;
						if (tmpCurrent.m_allowedVisits == 6665){
							tmpCurrent.m_VisitedVertex = true;
//TODO puede que tenga que devolver tracks parciales?
						} else if (tmpCurrent.m_allowedVisits >= 0) {
							CTrack nextTempTrack = RecursiveBacktracking2(tmpCurrent, lastVertex, visits);
							if(visits.remove(tmpCurrent)){
								if (verbose) {
									debugIndent = debugIndent.substring(3);
									System.out.println(debugIndent + "found node in visitList");
								}
								tmpCurrent.m_allowedVisits = 6666;
							}
							
							if (!nextTempTrack.m_Vertices.isEmpty()) {
								
								resultTrackSection.AddFirst(currentVertex);
								resultTrackSection.Append(nextTempTrack);
								if (verbose) {
									debugIndent = debugIndent.substring(3);
									System.out.println(debugIndent + "resultTrackSection= "
											+ resultTrackSection.toString() + " , returning ");
								}
								return resultTrackSection;
							} else {
								resultTrackSection.Clear();
//								tmpCurrent.m_allowedVisits++;
								
							}//TODO poner los verbose en cada resulttracksection.clear para ver donde peta
							//TODO IDEA! en los vertices con allowed visits > 1, hacer que allowedvisits = numero grande
							//para no borrar el camino hecho para la siguiente vez que pasemos haciendo un
							//if allowed (visits == numero grande)
						} else if (tmpCurrent.m_allowedVisits < 0 ){
							tmpCurrent.m_VisitedVertex = true;
						}

					}
				}
			}
			if (verbose) {
				debugIndent = debugIndent.substring(3);
		        System.out.println(debugIndent + "no viable neighbors, returning ");
		    }
		}			
		return resultTrackSection;

	}
	
	
	// =====================================================================================
	// SalesmanTrackBacktrackingGreedy
	// =====================================================
	// =====================================================================================
	// SalesmanTrackBacktrackingGreedy
	// -----------------------------------------------------
	/**Implements the Backtracking algorithm with the greedy modifier. 
	 *
	 *We search through a graph, given a visits list, the optimal route between a starting and
	 *an ending node that visits all vertex in the list. We use the dijkstrapriorityqueue method 
	 *to select the "best" vertex to visit next
	 * @param visits contains the vertex we need to visit.
	 * @return CTrack object with the optimal track.
	 * @throws Exception
	 */
	public CTrack SalesmanTrackBacktrackingGreedy(CVisits visits) throws Exception {
		
		CTrack resultTrack = new CTrack(this);		
		LinkedList<CVertex> visitList = visits.toCVertexList(this);
		
//		Ugly code, nevermind this. Keeping it for future reference.
//		for (CVertex currentStart : visitList) {
//			
//			CTrack tempResult = new CTrack(this);		
//			
//			System.out.print(currentStart.m_DijkstraDistance + " start distancia\n");
//			if (i<(visitList.size()-1)){
//				CVertex nextVertex = visitList.get(i);
//				this.DijkstraQueue(currentStart);							
//			
//				while (nextVertex.m_DijkstraPrevious != currentStart) {
//					CVertex step = nextVertex.m_DijkstraPrevious;
//					System.out.print(nextVertex.m_DijkstraDistance + " distancia\n");
//					tempResult.AddLast(step);
//					nextVertex = step.m_DijkstraPrevious;
//				}
//			 
//			}
//			else {
//				CVertex nextVertex = visitList.getLast();
//				this.DijkstraQueue(currentStart);
//				tempResult.AddLast(nextVertex.m_DijkstraPrevious);				
//			}
//				
//			i++;
//			result.Append(tempResult);
//		}
///////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////		
		
//TODO dijkstra implementation
		
		for (CVertex tmp : visitList){
			tmp.m_VertexToVisit = true;
		}
		
		CVertex currentVertex = visitList.pollFirst();
		CVertex nextVertex = visitList.peekFirst();
		CVertex lastVertex = visitList.peekLast();
		
		resultTrack.AddFirst(currentVertex);
		
		while(nextVertex != null){
			if (verbose) {
		        System.out.println(debugIndent + "Entering solvable ");
		        debugIndent = debugIndent + "|  ";
		    }

			visitList.remove(currentVertex);
			
			CTrack tempTrack = new CTrack(this);
			this.DijkstraQueue(currentVertex);

			for (CVertex temp : visitList){
				if ((temp!=lastVertex) && (temp.m_DijkstraDistance <= nextVertex.m_DijkstraDistance)){
					nextVertex = temp;
					if (verbose) {
						debugIndent = debugIndent.substring(3);
				        System.out.println(debugIndent + "found best tmp vertex ");
				        debugIndent = debugIndent + "|  ";
				    }
				}
			}
		
			tempTrack=getDijkstraTrack(currentVertex,nextVertex);
			
			visitList.remove(nextVertex);

//			for (CVertex tmp : visitList){
//				tempTrack = getDijkstraTrack(currentVertex,tmp);
//			}
			
//			for (CVertex tmp : tempTrack.m_Vertices){
//				if (tmp.m_VertexToVisit == false){
//					visitList.remove(tmp);
//				}
//			}
			

			resultTrack.Append(tempTrack);
			currentVertex = nextVertex;
			nextVertex = visitList.peekFirst();
		}
		
///////////////////////////////////////////////////////////////////////////////////////		
///////////////////////////////////////////////////////////////////////////////////////		
/////////////////////////more ugly code, don't look////////////////////////////////////		
///////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////		

		/*
		CVertex lastVertex = visitList.getLast();
		CVertex currentVertex = visitList.getFirst();
		//resultTrack.AddFirst( visitList.getFirst());
		boolean finish = false ;
		
		Comparator<CTrack> resultCompare = new TrackCompare();
		PriorityQueue<CTrack> resultTQ = new PriorityQueue<CTrack>(resultCompare);
		Comparator<CTrack> tmpTComparator = new TrackCompare();
		PriorityQueue<CTrack> tmpTQ = new PriorityQueue<CTrack>(tmpTComparator);
		
		while (currentVertex!=null){			
			


			ListIterator<CVertex> vertexIterator = visitList.listIterator();
			
			System.out.println("kek\n");
			

			if (currentVertex == lastVertex){
				finish = true;
			}
			
			for (CVertex nextVertex : visitList){
				CTrack tmpTrack = getDijkstraTrack(currentVertex, nextVertex);
				tmpTQ.offer(tmpTrack);
			}
			
			CTrack bestTrack = tmpTQ.poll();
			if (finish && bestTrack!=null){
				resultTrack.Append(bestTrack);
				return resultTrack;
				
			} else if (!finish){
				if (bestTrack != null){
					boolean badPath = false;
					
					for (CVertex iter : bestTrack.m_Vertices){
						if (resultTrack.m_Vertices.contains(iter)){
							badPath = true;
						}
					}
					
					if (!badPath){
//						resultTQ.offer(bestTrack.AppendBefore(t);
						currentVertex = bestTrack.m_Vertices.getLast();
						visitList.remove(currentVertex);
					}
					if (badPath){
						bestTrack = resultTQ.poll();
						currentVertex = bestTrack.m_Vertices.getLast();
						visitList.remove(currentVertex);
						tmpTQ.clear();
					}
				}
			}
			
			resultTrack.Append(bestTrack);
			//
			
			//start
				//getdijkstratrack desde current a todo visits excepto last
					//if newtrack.length <= mintrack.length
						
			//guarda el track
				//current.m_VertexToVisit = true
				//current = 	
					
			
		}
*/
		
		
		return resultTrack;		
	}
	
	public CTrack getDijkstraTrack(CVertex start, CVertex last) throws Exception{
		
		CTrack tempTrack = new CTrack(this);
		if (start == last){
			tempTrack.AddFirst(start);
			return tempTrack;
		}
		CVertex tmpVertex = last;
		
		tempTrack.AddFirst(tmpVertex);
		while (tmpVertex.m_DijkstraPrevious != start){
			tmpVertex.m_VertexToVisit = false;
			tmpVertex = tmpVertex.m_DijkstraPrevious;
			tempTrack.AddFirst(tmpVertex);
		}		
		return tempTrack;
	}

	// =====================================================================================
	// SalesmanTrackBranchAndBound
	// =========================================================
	// =====================================================================================
	// SalesmanTrackBranchAndBound1
	// ---------------------------------------------------------
	public CTrack SalesmanTrackBranchAndBound1(CVisits visits) throws Exception {
		// IMPLEMENTAR LA FUNCION
		throw new Exception("SalesmanTrackBranchAndBound1 no implementado");
	}

	// SalesmanTrackBranchAndBound2
	// -------------------------------------------------------------------
	public CTrack SalesmanTrackBranchAndBound2(CVisits visits) throws Exception {
		// IMPLEMENTAR LA FUNCION
		throw new Exception("SalesmanTrackBranchAndBound2 no implementado");
	}

	// SalesmanTrackBranchAndBound3
	// -------------------------------------------------------------------
	public CTrack SalesmanTrackBranchAndBound3(CVisits visits) throws Exception {
		// IMPLEMENTAR LA FUNCION
		throw new Exception("SalesmanTrackBranchAndBound3 no implementado");
	}
}
