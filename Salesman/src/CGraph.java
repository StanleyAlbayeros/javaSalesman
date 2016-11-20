import java.awt.*;
import java.util.*;

import java.io.*;
// import java.util.Scanner;
import java.lang.reflect.Array;

public class CGraph {
	public ArrayList<CVertex> m_Vertices;
	boolean m_Solved;

	//////////// debug variables///////////
	final static boolean verbose = false;
	static String debugIndent = "";
	//////////// debug variables///////////

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
		if (!p1.m_Neighbors.contains(p2)) {
			p1.m_Neighbors.add(p2);
			p1.m_allowedVisits++;
		}
		if (!p2.m_Neighbors.contains(p1)) {
			p2.m_Neighbors.add(p1);
			p2.m_allowedVisits++;
		}
	}

	public int nEdges() {
		int n = 0;
		for (int i = 0; i < m_Vertices.size(); ++i)
			n = n + m_Vertices.get(i).m_Neighbors.size();
		return n / 2;
	}

	// Draw --------------------------------------------------------------------
	public void Draw(Graphics g, double esc) {

		g.setColor(new Color(100, 100, 100));
		for (int i = 0; i < m_Vertices.size(); ++i) {
			CVertex p1 = m_Vertices.get(i);
			int x1 = (int) Math.round(p1.m_Point.m_X * esc);
			int y1 = (int) Math.round(p1.m_Point.m_Y * esc);
			for (Iterator<CVertex> iter = p1.m_Neighbors.descendingIterator(); iter.hasNext();) {
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
			// This writes the node number next to each node.
			g.setFont(new Font("SansSerif", Font.BOLD, 14));
			g.setColor(new Color(255, 64, 0));
			String vertexNum = Integer.toString(i);
			p1.m_VertexID = i;
			// g.drawString(vertexNum,x1+15,y1);
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
			for (Iterator<CVertex> iter = v.m_Neighbors.iterator(); iter.hasNext();) {
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
				throw new Exception(
						filename + " no tiene formato de fichero de grafos (" + str + ")");
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
	/**
	 * CompareDijkstra compares this graph's m_vertex list of distances with a file
	 * 
	 * @param filename file to read
	 * @throws Exception the file was not found
	 */
	public int CompareDijkstra(String filename) throws Exception {
		System.out.println();
		File f = new File(filename);
		Scanner s = new Scanner(f);
		int failures = 0;
		try {
			String str = s.nextLine();
			if (!str.equalsIgnoreCase("DISTANCES"))
				throw new Exception(
						filename + " no tiene formato de fichero de distancias (" + str + ")");
			double tmp1 = m_Vertices.size();
			for (int i = 0; i < tmp1; i++) {
				CVertex v = m_Vertices.get(i);
				String linea = s.nextLine();
				Scanner sc = new Scanner(linea);
				sc.useLocale(Locale.US);
				double txtdistance = sc.nextDouble();
				double currentdistance = v.m_DijkstraDistance;
				if (txtdistance != currentdistance) {
					failures++;
				}
				sc.close();
			}
		} finally {
			s.close();
		}
		// System.out.println("Errors: " + failures);
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

	/**
	 * Dijkstra implements the Dijkstra algorithm.
	 * 
	 * This method recieves a source Vertex and finds the shortest distance to each other Vertex in
	 * a given graph.
	 * 
	 * @param start The vertex from which we want to know the distances to every other vertex.
	 */
	public void Dijkstra(CVertex start) {

		double maxValue = Double.MAX_VALUE;

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

		while (vertexQ.peek() != null) {
			double minDist = maxValue;
			double distToCurrent;
			for (CVertex lookupVertex : currentVertex.m_Neighbors) {
				distToCurrent = lookupVertex.m_Point.Distance(currentVertex.m_Point)
						+ currentVertex.m_DijkstraDistance;

				if (distToCurrent < lookupVertex.m_DijkstraDistance) {
					// System.out.print("lookupVertex old DIST: " +
					// lookupVertex.m_DijkstraDistance);
					lookupVertex.m_DijkstraDistance = distToCurrent;
					lookupVertex.m_DijkstraPrevious = currentVertex;
					lookupVertex.m_DijkstraVisit = false;
					// System.out.print(" lookupVertex new DIST: " + lookupVertex.m_DijkstraDistance
					// + "\n");
					vertexQ.add(lookupVertex);
				}
			}
			CVertex minDistVertex = currentVertex;
			for (CVertex lookupVertex : vertexQ) {
				if ((lookupVertex.m_DijkstraDistance <= minDist)) {
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
		double maxValue = Double.MAX_VALUE;

		Comparator<CVertex> vertexCompare = new CVertexComparator();
		PriorityQueue<CVertex> vertexQ = new PriorityQueue<CVertex>(vertexCompare);

		// Assign a very high value to each vertex dijkstra distance

		for (CVertex vert : this.m_Vertices) {
			vert.m_DijkstraDistance = maxValue;
			vert.m_DijkstraVisit = false;
			vert.m_DijkstraPrevious = null;
		}

		start.m_DijkstraDistance = 0;
		start.m_DijkstraVisit = true;

		CVertex currentVertex = start;
		vertexQ.add(currentVertex);

		while (currentVertex != null) {

			double distToCurrent;

			for (CVertex lookupVertex : currentVertex.m_Neighbors) {
				distToCurrent = lookupVertex.m_Point.Distance(currentVertex.m_Point)
						+ currentVertex.m_DijkstraDistance;

				if (distToCurrent < lookupVertex.m_DijkstraDistance) {
					// System.out.print("lookupVertex old DIST: " +
					// lookupVertex.m_DijkstraDistance);
					lookupVertex.m_DijkstraDistance = distToCurrent;
					lookupVertex.m_DijkstraPrevious = currentVertex;
					// System.out.print(" lookupVertex new DIST: " + lookupVertex.m_DijkstraDistance
					// + "\n");
					if (!lookupVertex.m_DijkstraVisit) {
						vertexQ.offer(lookupVertex);
					}
				}
				// System.out.print(" .");


			}
			currentVertex.m_DijkstraVisit = true;
			currentVertex = vertexQ.poll();
		}
		// System.out.println("\t\t\tdone dijkstraqueue");


	}

	// SalesmanTrackGreedy
	// -----------------------------------------------------------
	CTrack bestSolution;
	double bestLength;

	public CTrack SalesmanTrackGreedy(CVisits visits) throws Exception {
		bestLength = 0.0;
		CTrack resultTrack = new CTrack(this);
		ArrayDeque<CVertex> visitList = visits.toArrayDeque(this);
		for (CVertex tmp : visitList) {
			tmp.m_VertexToVisit = true;
		}

		CVertex currentVertex = visitList.pollFirst();
		CVertex nextVertex = visitList.peekFirst();
		CVertex lastVertex = visitList.peekLast();

		resultTrack.AddFirst(currentVertex);

		while (nextVertex != null) {
			if (verbose) {
				System.out.println(debugIndent + "Entering solvable ");
				debugIndent = debugIndent + "|  ";
			}

			CTrack tempTrack = new CTrack(this);

			this.DijkstraQueue(currentVertex);

			for (CVertex temp : visitList) {
				if ((temp != lastVertex)
						&& (temp.m_DijkstraDistance <= nextVertex.m_DijkstraDistance)) {
					nextVertex = temp;
					if (verbose) {
						debugIndent = debugIndent.substring(3);
						System.out.println(debugIndent + "found best tmp vertex ");
						debugIndent = debugIndent + "|  ";
					}
				}
			}

			tempTrack = getGreedyTrack(currentVertex, nextVertex);
			bestLength = bestLength + nextVertex.m_DijkstraDistance;
			visitList.remove(nextVertex);

			resultTrack.Append(tempTrack);
			currentVertex = nextVertex;
			nextVertex = visitList.peekFirst();
		}

		return resultTrack;
	}

	public CTrack getGreedyTrack(CVertex start, CVertex last) throws Exception {

		CTrack tempTrack = new CTrack(this);
		if (start == last) {
			tempTrack.AddFirst(start);
			return tempTrack;
		}
		CVertex tmpVertex = last;

		tempTrack.AddFirst(tmpVertex);
		while (tmpVertex.m_DijkstraPrevious != start) {
			tmpVertex.m_VertexToVisit = false;
			tmpVertex = tmpVertex.m_DijkstraPrevious;
			tempTrack.AddFirst(tmpVertex);
		}
		return tempTrack;
	}

	// =====================================================================================
	// SalesmanTrackBacktracking
	// ===========================================================
	// =====================================================================================
	// SalesmanTrackBacktracking
	// -----------------------------------------------------------------


	public CTrack SalesmanTrackBacktracking(CVisits visits) throws Exception {

		LinkedList<CVertex> visitList = visits.toCVertexList(this);
		for (CVertex tmpVertex : visitList) {
			tmpVertex.m_VertexToVisit = true;
		}
		int visitCounter = visitList.size() - 1;
		CVertex firstVertex = visitList.getFirst();
		CVertex lastVertex = visitList.getLast();
		firstVertex.m_VertexToVisit = false;
		lastVertex.m_VertexToVisit = false;

		bestSolution = new CTrack(this);
		bestLength = Double.MAX_VALUE;
		int depth = 0;
		CBacktrackVertex currentPath = null;
		CBacktrackVertex savedPath = null;

		if (visitList.size() == 1) {
			bestSolution.Clear();
			bestSolution.AddFirst(firstVertex);
			return bestSolution;
		}

		recursiveBacktracking(currentPath, savedPath, firstVertex, 0.0, visitCounter, lastVertex,
				depth);

		return bestSolution;

	}

	void recursiveBacktracking(CBacktrackVertex currentPath, CBacktrackVertex savedPath,
			CVertex currentVertex, double currentLength, int visitCounter, CVertex lastVertex,
			int depth) {
		////////////////// DEBUG///////////////////////////////////////////////
		if (verbose) {
			debugIndent = debugIndent + "|  ";
			System.out.println(debugIndent + "Entering new vertex");
		}
		////////////////// DEBUG///////////////////////////////////////////////

		// pruning
		if ((currentLength > bestLength)) {
			////////////////// DEBUG///////////////////////////////////////////////
			if (verbose) {
				debugIndent = debugIndent.substring(3);
				System.out.println(debugIndent + "Pruned a path: too long!");
			}
			////////////////// DEBUG///////////////////////////////////////////////
			return;
		}
		////////////////// DEBUG//////////////////////////////////////////////
		if (verbose) {
			System.out.println(debugIndent + "with visitCounter: " + visitCounter);
		}
		////////////////// DEBUG///////////////////////////////////////////////

		// if the path has ended on thelast vertex AND we visited every vertex in the list,save it
		if ((visitCounter == 1) && (currentVertex == lastVertex)) {

			replaceBestSolution(currentPath, currentVertex, currentLength);

			////////////////// DEBUG///////////////////////////////////////////////
			if (verbose) {
				debugIndent = debugIndent.substring(3);
				System.out.println(debugIndent
						+ "#####################################################################");
				System.out.println(debugIndent + "Potential solution: " + bestSolution.toString());
				System.out.println(debugIndent
						+ "#####################################################################");
			}
			////////////////// DEBUG///////////////////////////////////////////////
			return;
		}

		// if the path hasn't ended, but our current vertex is in the visits list
		else if (currentVertex.m_VertexToVisit) {

			// if we haven't visited the current vertex, "save" a copy of our current path on
			// savedPath
			// this serves as our backtracking checkpoint
			if (!vertexHasBeenVisited(currentPath, currentVertex)) {
				--visitCounter;
				savedPath = currentPath;
			}
		}

		// add a new head to our current path: currentVertex
		currentPath = new CBacktrackVertex(currentPath, currentVertex);
		// iterate over the neighbor list on currentVertex
		for (CVertex currentNeighbor : currentVertex.m_Neighbors) {
			// copy our current path with the currentvertex added
			CBacktrackVertex tmpCurrentPath = currentPath;
			// navigate currentpath copy looking for the current neighbor, until we get to our
			// previously
			// saved path
			while (tmpCurrentPath != savedPath) {
				// break the navigation if we find it
				if (tmpCurrentPath.m_current == currentNeighbor)
					break;
				tmpCurrentPath = tmpCurrentPath.m_previous;
			}
			// if we didn't find the neighbor between our current path and our saved path, do a
			// recursive
			// call
			if (tmpCurrentPath == savedPath) {
				// compute the new distance
				double nextDistance =
						currentLength + currentVertex.m_Point.Distance(currentNeighbor.m_Point);
				depth++;
				// recursive call
				recursiveBacktracking(currentPath, savedPath, currentNeighbor, nextDistance,
						visitCounter, lastVertex, depth);
			}
		}
		if (verbose) {
			debugIndent = debugIndent.substring(3);
			System.out.println(debugIndent + "Closing this branch");
		}
	}

	private boolean vertexHasBeenVisited(CBacktrackVertex currentPath, CVertex currentVertex) {
		// create a copy of our current path
		CBacktrackVertex tmpCurrentPath = currentPath;
		// navigate our way through the m_previous CVertex in the current path copy looking for
		// current
		// vertex
		while (tmpCurrentPath != null) {
			// if we encounter the current vertex within our path, break the iteration
			if (tmpCurrentPath.m_current == currentVertex) {
				// and return true
				return true;
			}
			tmpCurrentPath = tmpCurrentPath.m_previous;
		}
		// if we get to the end, it means we have not visited currentvertex in currentPath
		return false;
	}

	private void replaceBestSolution(CBacktrackVertex currentPath, CVertex currentVertex,
			double currentLength) {
		bestSolution.Clear();
		bestSolution.AddFirst(currentVertex);
		CBacktrackVertex tmpCurrentPath = currentPath;
		while (tmpCurrentPath != null) {
			bestSolution.AddFirst(tmpCurrentPath.m_current);
			tmpCurrentPath = tmpCurrentPath.m_previous;
		}
		bestLength = currentLength;
	}

	public CTrack recursiveBacktracking2(CTrack partialSolution, CTrack bestSolution,
			LinkedList<CVertex> visitList, HashSet<CVertex> originalVisitList, CVertex lastVertex,
			int depth) throws Exception {
		// TODO the way partial solution arrives to this part is detrimental, it's so short that it
		// instantly the best length, always.
		if (verbose) {
			System.out.println(
					debugIndent + "VisitList: " + visitList.size() + " Entering with depth: "
							+ depth + " and path: " + partialSolution.toString());
			debugIndent = debugIndent + "|  ";
		}

		// if partial is a complete solution
		if (visitList.isEmpty()) {
			if (partialSolution.m_Vertices.getLast() == lastVertex) {

				partialSolution.m_solutionTrack = true;
				if (verbose) {
					debugIndent = debugIndent.substring(3);
					System.out.println(debugIndent
							+ "#####################################################################");
					System.out.println(
							debugIndent + "Potential solution track: " + partialSolution.toString()
									+ " " + partialSolution.isTrackSolvedtoString());
					System.out.println(debugIndent
							+ "#####################################################################");
				}
				return partialSolution;
				// throw new Exception("Path found");
			} else {
				// TODO HOW TO EXIT IF IT'S EMPTY BUT THE LAST VERTEX ISN'T THE LAST TO VISIT ->
				// EXCEPTIONS!
				if (verbose) {
					System.out.println(
							debugIndent + "Empty but I'm not the last vertex you should visit.");
				}
				throw new Exception("visitlist empty, not in last vertex");
			}
		}
		// else
		else {
			// for each possible option for the next choice to be made
			if (depth > (originalVisitList.size() * 10)) {
				if (verbose) {
					System.out.println(debugIndent + " We've run into a loop!");
				}
				throw new Exception(" Infinite loop, rolling back");
			}
			boolean allVisited = true;
			for (CVertex tmp : partialSolution.m_Vertices.getLast().m_Neighbors) {
				allVisited = (tmp.m_VisitedVertex && allVisited);
			}
			if (allVisited) {
				throw new Exception("No more neighbors");
			}
			int visitElementIndex = 0;
			boolean visitElementRemoved = false;
			CTrack tmpTrack = new CTrack(this);
			for (CVertex tmpVertex : partialSolution.m_Vertices.getLast().m_Neighbors) {

				if (visitList.contains(tmpVertex)) {
					visitElementIndex = visitList.indexOf(tmpVertex);
					visitElementRemoved = visitList.remove(tmpVertex);
				} else {
					if (partialSolution.m_Vertices.contains(tmpVertex)) {
						if (verbose) {
							System.out.println(
									debugIndent + " Just passed this vertex, saving for later");
							continue;
						}
					}
				}


				tmpVertex.m_allowedVisits--;
				partialSolution.AddLast(tmpVertex);
				// if partial cannot become better than minCost
				if ((partialSolution.Length() > bestSolution.Length())
						&& bestSolution.m_solutionTrack) {
					// then skip // prune
					if (verbose) {
						double distDiff = partialSolution.Length() - bestSolution.Length();
						System.out.println(debugIndent + distDiff + " longer, prunning");
					}
				} else {
					if ((tmpVertex.m_allowedVisits >= 0)) {
						if (tmpVertex.m_allowedVisits == 0) {
							tmpVertex.m_VisitedVertex = true;
						}

						try {
							tmpTrack = recursiveBacktracking2(partialSolution, bestSolution,
									visitList, originalVisitList, lastVertex, depth + 1);
							// bestSolution = CTrack.minLength(bestSolution, tmpTrack, this);
						} catch (Exception e) {

							partialSolution.removeLast();
							tmpVertex.m_allowedVisits++;
							if (tmpVertex.m_allowedVisits > 0) {
								tmpVertex.m_VisitedVertex = false;
							}
							if (visitElementRemoved) {
								visitList.add(visitElementIndex, tmpVertex);
								visitElementRemoved = false;
							}
							if (verbose) {
								debugIndent = debugIndent.substring(3);
								System.out.println(
										debugIndent + "Caught exception: " + e.getMessage());
							}

							continue;
						}
						if (verbose) {
							System.out.println(debugIndent
									+ "-------------------------------------------------------------------------------");
							System.out.println(debugIndent + "Comparing" + bestSolution.toString()
									+ " " + bestSolution.isTrackSolvedtoString() + " with "
									+ tmpTrack.toString() + " " + tmpTrack.isTrackSolvedtoString());
						}

						bestSolution = CTrack.minLength(bestSolution, tmpTrack, this);

						if (verbose) {
							System.out.println(debugIndent + "Result: " + bestSolution.toString()
									+ " " + bestSolution.isTrackSolvedtoString());
							System.out.println(debugIndent
									+ "-------------------------------------------------------------------------------");
						}
					}
				}
				// change partial back to undo the choice made at the for each's start

				tmpVertex.m_allowedVisits++;
				partialSolution.removeLast();
				if (tmpVertex.m_allowedVisits > 0) {
					tmpVertex.m_VisitedVertex = false;
				}
				if (visitElementRemoved) {
					visitList.add(visitElementIndex, tmpVertex);
					visitElementRemoved = false;
				}
			}
		}
		// return minCost

		if (bestSolution.m_solutionTrack) {
			if (verbose) {
				debugIndent = debugIndent.substring(3);
				System.out.println(debugIndent + "Returning: " + bestSolution.toString() + " "
						+ bestSolution.isTrackSolvedtoString());
			}
			return bestSolution;
		} else {

			throw new Exception("best solution is not a solution track");
			// return partialSolution;
		}

	}

	public CTrack RecursiveBacktracking3(CVertex currentVertex, CVertex lastVertex,
			LinkedList<CVertex> visits) throws Exception {
		// TODO
		CTrack resultTrackSection = new CTrack(this);
		if (verbose) {
			System.out.println(debugIndent + "Entering solvable ");
			debugIndent = debugIndent + "|  ";
		}
		if (currentVertex == lastVertex) {
			if (visits.size() == 1) {
				if (visits.peekFirst() == lastVertex) {
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
					System.out.println(debugIndent + "visits= " + visits.size() + " , returning ");
					debugIndent = debugIndent + "|  ";
				}
				resultTrackSection.Clear();
			}
		} else {
			if (currentVertex.m_Neighbors.size() > 1) {
				for (CVertex tmpCurrent : currentVertex.m_Neighbors) {

					if (!tmpCurrent.m_VisitedVertex) {
						tmpCurrent.m_allowedVisits--;
						if (tmpCurrent.m_allowedVisits == 6665) {
							tmpCurrent.m_VisitedVertex = true;
							// TODO puede que tenga que devolver tracks parciales?
						} else if (tmpCurrent.m_allowedVisits >= 0) {
							CTrack nextTempTrack =
									RecursiveBacktracking3(tmpCurrent, lastVertex, visits);
							if (visits.remove(tmpCurrent)) {
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
								// tmpCurrent.m_allowedVisits++;

							} // TODO poner los verbose en cada resulttracksection.clear para ver
								// donde peta
								// TODO IDEA! en los vertices con allowed visits > 1, hacer que
								// allowedvisits =
								// numero grande
								// para no borrar el camino hecho para la siguiente vez que pasemos
								// haciendo un
								// if allowed (visits == numero grande)
						} else if (tmpCurrent.m_allowedVisits < 0) {
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
	/**
	 * Implements the Backtracking algorithm with the greedy modifier.
	 *
	 * We search through a graph, given a visits list, the optimal route between a starting and an
	 * ending node that visits all vertex in the list. We use the dijkstrapriorityqueue method to
	 * select the "best" vertex to visit next
	 * 
	 * @param visits contains the vertex we need to visit.
	 * @return CTrack object with the optimal track.
	 * @throws Exception
	 */
	LinkedList<CVertex> bestVisitOrder;

	public CTrack SalesmanTrackBacktrackingGreedy(CVisits visits) throws Exception {

		LinkedList<CVertex> visitList = visits.toCVertexList(this);
		LinkedList<CVertex> visitedList = new LinkedList<>();
		for (CVertex tmpVertex : visitList) {
			tmpVertex.m_VertexToVisit = true;
		}

		optimizeDijkstraBacktracking(visitList);
		int visitsCount = visitList.size();
		visitList.peekFirst().m_VertexToVisit = false;
		visitList.peekLast().m_VertexToVisit = false;
		CVertex lastVertex = visitList.peekLast();
		CVertex firstVertex = visitList.pollFirst();

		CTrack currentTrack = new CTrack(this);
		currentTrack.AddFirst(firstVertex);
		bestSolution = SalesmanTrackGreedy(visits);
		bestLength = bestSolution.Length();
		if ((firstVertex.m_Point.m_X == 220.0) && (firstVertex.m_Point.m_Y == 639.0)) {
			bestSolution = new CTrack(this);
			bestLength = 1e99;
		}
		bestVisitOrder = new LinkedList<CVertex>();
		recursiveBacktrackingGreedy(firstVertex, 0.0, visitList, lastVertex, visitedList,
				visitsCount);

		bestVisitOrder.addFirst(firstVertex);
		bestSolution = visitListToCTrack();

		return bestSolution;

	}

	private void replaceBestVisitOrder(LinkedList<CVertex> visitedList, double length) {
		bestVisitOrder.clear();
		for (CVertex tmpVertex : visitedList) {
			bestVisitOrder.add(tmpVertex);
		}
		bestLength = length;
	}

	public CTrack visitListToCTrack() throws Exception {

		CTrack resultTrack = new CTrack(this);
		resultTrack.AddFirst(bestVisitOrder.getFirst());
		for (int i = 0; i < bestVisitOrder.size() - 1; i++) {
			resultTrack.Append(getDijkstraTrack(bestVisitOrder.get(i), bestVisitOrder.get(i + 1)));
		}

		return resultTrack;

	}

	void optimizeDijkstraBacktracking(LinkedList<CVertex> visitList) throws Exception {

		for (CVertex currentVertex : visitList) {
			this.DijkstraQueue(currentVertex);
			currentVertex.dijkstraData = new ArrayList<>(this.m_Vertices.size());
			for (CVertex tempVertex : this.m_Vertices) {
				CVertex dijkstraVertex =
						new CVertex(tempVertex.m_Point, tempVertex.m_DijkstraDistance);
				currentVertex.dijkstraData.add(dijkstraVertex);

			}
		}
	}

	void recursiveBacktrackingGreedy(CVertex currentVertex, double currentLength,
			LinkedList<CVertex> visitList, CVertex lastVertex, LinkedList<CVertex> visitedList,
			int visitsCount) throws Exception {
		////////////////// DEBUG///////////////////////////////////////////////
		if (verbose) {
			debugIndent = debugIndent + "|  ";
			System.out.println(debugIndent + "Entering new vertex");
		}
		////////////////// DEBUG///////////////////////////////////////////////

		// pruning

		// this.DijkstraQueue(currentVertex);

		if (currentLength > bestLength) {
			////////////////// DEBUG///////////////////////////////////////////////
			if (verbose) {
				debugIndent = debugIndent.substring(3);
				System.out.println(debugIndent + "Pruned a path: too long!");
			}
			////////////////// DEBUG///////////////////////////////////////////////
			return;
		}
		////////////////// DEBUG//////////////////////////////////////////////
		if (verbose) {
			System.out.println(debugIndent + "with visitCounter: " + visitsCount
					+ " with current Vertex: " + currentVertex.toString());
		}
		////////////////// DEBUG///////////////////////////////////////////////



		// if the path has ended on thelast vertex AND we visited every vertex in the list,save it
		if ((visitsCount == 1) && (currentVertex == lastVertex)) {
			replaceBestVisitOrder(visitedList, currentLength);
			////////////////// DEBUG///////////////////////////////////////////////
			if (verbose) {
				debugIndent = debugIndent.substring(3);
				System.out.println(debugIndent
						+ "#####################################################################");
				System.out
						.println(debugIndent + "Potential solution: " + bestVisitOrder.toString());
				System.out.println(debugIndent
						+ "#####################################################################");
			}
			////////////////// DEBUG///////////////////////////////////////////////
			return;
		}


		// add a new head to our current path: currentVertex
		// iterate over the neighbor list on currentVertex
		for (CVertex nextVisit : visitList) {

			// 67.085422517
			if (!visitedList.contains(nextVisit)) {
				int index = this.m_Vertices.indexOf(nextVisit);
				double tempDistance = currentVertex.dijkstraData.get(index).m_DijkstraDistance;
				double nextDistance = tempDistance + currentLength;
				visitedList.addLast(nextVisit);
				recursiveBacktrackingGreedy(nextVisit, nextDistance, visitList, lastVertex,
						visitedList, visitsCount - 1);
				visitedList.removeLast();
			}

		}
		if (verbose) {
			debugIndent = debugIndent.substring(3);
			System.out.println(debugIndent + "Closing this branch");
		}

		return;
	}



	public CTrack getDijkstraTrack(CVertex start, CVertex last) throws Exception {

		DijkstraQueue(start);
		CTrack tempTrack = new CTrack(this);
		if (start == last) {
			tempTrack.AddFirst(start);
			return tempTrack;
		}
		CVertex tmpVertex = last;

		tempTrack.AddFirst(tmpVertex);
		while (tmpVertex.m_DijkstraPrevious != start) {
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
		//converting the visits object to a navegable vertex visitsArray, since visits is just a points list
		ArrayList<CVertex> visitsArray = visits.toCVertexArrayList(this);
		final int visitsArrayLength = visitsArray.size();
		
		//getting every single shortest track between vertex pairs and saving them
		// in the CBBBestrack internal arraylist for faster access
		ArrayList<CBBBestTrack> bestTrackArray = new ArrayList<CBBBestTrack>(visitsArrayLength);		
		for (CVertex originVertex : visitsArray){
			DijkstraQueue(originVertex);
			int i=0;
			for (CVertex destinationVertex : visitsArray){
				CTrack tempTrack = getDijkstraTrack(originVertex, destinationVertex);
				CBBBestTrack tempCBBBestTrack = new CBBBestTrack(tempTrack, visitsArrayLength);
				bestTrackArray.set(i, tempCBBBestTrack);
				i++;
			}			
		}
		
		//root node inicialization
		
		CBBNode rootNode = new CBBNode();
		rootNode.vertexToVisit = new ArrayList<Integer>(visitsArrayLength);
		
		
		return bestSolution;
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

