import java.awt.*;
import java.util.*;

import java.io.*;
//import java.util.Scanner;

public class CGraph {
	public ArrayList<CVertex> m_Vertices;
	boolean m_Solved;

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
		if (!p1.m_Neighbords.contains(p2))
			p1.m_Neighbords.add(p2);
		if (!p2.m_Neighbords.contains(p1))
			p2.m_Neighbords.add(p1);
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
			g.drawString(vertexNum,x1+15,y1);
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
			System.out.print(i + ":" + v.m_DijkstraDistance /*+ " Last vertex: " + previousVertex.m_VertexID */+ "\n");
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
					//System.out.print("lookupVertex old DIST: " + lookupVertex.m_DijkstraDistance);
					lookupVertex.m_DijkstraDistance = distToCurrent;
					lookupVertex.m_DijkstraPrevious = currentVertex;
					//System.out.print(" lookupVertex new DIST: " + lookupVertex.m_DijkstraDistance + "\n");
					if (!lookupVertex.m_DijkstraVisit) {
					vertexQ.offer(lookupVertex);
					}
				}

				
			}
			currentVertex.m_DijkstraVisit = true;
			currentVertex = vertexQ.poll();
		}
		
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
		// IMPLEMENTAR LA FUNCION
		throw new Exception("SalesmanTrackBacktracking no implementado");
	}

	// =====================================================================================
	// SalesmanTrackBacktrackingGreedy
	// =====================================================
	// =====================================================================================
	// SalesmanTrackBacktrackingGreedy
	// -----------------------------------------------------
	public CTrack SalesmanTrackBacktrackingGreedy(CVisits visits) throws Exception {
		// IMPLEMENTAR LA FUNCION
		throw new Exception("SalesmanTrackBacktrackingGreedy no implementado");
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
