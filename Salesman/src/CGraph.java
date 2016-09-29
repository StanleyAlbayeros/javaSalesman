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

	// PrintDistances ----------------------------------------------------------
	public void PrintDistances() throws Exception {
		System.out.print("DISTANCES ");
		for (int i = 0; i < m_Vertices.size(); ++i) {
			CVertex v = m_Vertices.get(i);
			CVertex previousVertex = v.m_DijkstraPrevious;
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
		
		double maxValue = 99999999; // lo utilizo para inicializar las
									// dijkstraDistance de los vertex a un valor
									// elevado
									// y para inicializar la distancia minima de
									// la lista de hijos de cada vertice

		LinkedList<CVertex> vertexQ = new LinkedList<CVertex>();
		// Inicializamos todos los vertices del grafo a una distancia de
		// dijkstra elevada
		for (CVertex vert : this.m_Vertices) {
			vert.m_DijkstraDistance = maxValue;
			vert.m_DijkstraVisit = false;
		}

		// el nodo de inicio siempre tiene distancia 0 y ponemos start a
		// visitado
		start.m_DijkstraDistance = 0;
		start.m_DijkstraVisit = true;
		CVertex currentVertex = start;
		vertexQ.add(currentVertex);
		
		/// comenzara bucle con cola/pila

		while ( currentVertex != null ) {
			
			double distToCurrent;
			//// comienza bucle, falta iterador entre todos los vertex
			for (CVertex lookupVertex : currentVertex.m_Neighbords) {
				distToCurrent = lookupVertex.m_Point.Distance(currentVertex.m_Point) + currentVertex.m_DijkstraDistance;

				// Si la distancia calculada es menor que la distancia guardada
				// en distancia de dijkstra del punto, se pone la calculada

				if (distToCurrent < lookupVertex.m_DijkstraDistance) {					
					System.out.print("lookupVertex old DIST: " + lookupVertex.m_DijkstraDistance);
					lookupVertex.m_DijkstraDistance = distToCurrent;
					lookupVertex.m_DijkstraPrevious = currentVertex;
					System.out.print(" lookupVertex new DIST: " + lookupVertex.m_DijkstraDistance + "\n");
				}

				if (!lookupVertex.m_DijkstraVisit ) {			
					vertexQ.offer(lookupVertex);					
				}

			}
			
			currentVertex.m_DijkstraVisit = true;
			currentVertex = vertexQ.pollLast();
		}

	}

		//// termina bucle, falta iterador entre todos los vertex

		// en minDist esta la distancia desde current a verTemp, la minima en el
		// grupo de neighbors. Este vertice
		// es el siguiente "current".

		// throw new Exception("Dijkstra no implementado");


	// DijkstraQueue
	// -------------------------------------------------------------------
	public void DijkstraQueue(CVertex start) throws Exception {
		double maxValue = 99999999; // lo utilizo para inicializar las
		// dijkstraDistance de los vertex a un valor
		// elevado
		// y para inicializar la distancia minima de
		// la lista de hijos de cada vertice

		CVertex bestVertex; // para el mejor vertex por vuelta del bucle mas
							// interno
		LinkedList<CVertex> vertexQ = new LinkedList<CVertex>();
		double minDist = maxValue;
		int numGrafVertexes = this.nVertices(); // numero de vertices del grafo
		int numVisitedVertexes = 0; // numero de vertices que he visitado desde
									// inicio de dijkstra

		// Inicializamos todos los vertices del grafo a una distancia de
		// dijkstra elevada
		for (CVertex vert : this.m_Vertices) {
			vert.m_DijkstraDistance = maxValue;
			vert.m_DijkstraVisit = false;
		}

		// el nodo de inicio siempre tiene distancia 0 y ponemos start a
		// visitado
		start.m_DijkstraDistance = 0;
		start.m_DijkstraVisit = true;

		CVertex currentVertex = start;
		bestVertex = currentVertex;
		//
		//
		//
		//
		//
		//
		vertexQ.add(currentVertex);
		//
		//
		// we can't instantiate a queue instance because it's an interface
		//
		//
		//
		//

		/// comenzara bucle con cola/pila

		while (currentVertex != null /* numVisitedVertexes <= numGrafVertexes */ ) {

			double distToCurrent;

			//// com ienza bucle, falta iterador entre todos los vertex
			for (CVertex lookupVertex : currentVertex.m_Neighbords) {

				distToCurrent = lookupVertex.m_Point.Distance(currentVertex.m_Point) + currentVertex.m_DijkstraDistance;

				// Si la distancia calculada es menor que la distancia guardada
				// en distancia de dijkstra del punto, se pone la calculada

				if (distToCurrent < lookupVertex.m_DijkstraDistance) {

					System.out.print("lookupVertex old DIST: " + lookupVertex.m_DijkstraDistance);
					lookupVertex.m_DijkstraDistance = distToCurrent;
					lookupVertex.m_DijkstraPrevious = currentVertex;
					System.out.print(" lookupVertex new DIST: " + lookupVertex.m_DijkstraDistance + "\n");

				}

				// Si la distancia es menor que la minima en el grupo de
				// neighbors, se asigna la min dist
				if (/* (distToCurrent < minDist) && */!lookupVertex.m_DijkstraVisit) {

					/*
					 * minDist = lookupVertex.m_DijkstraDistance; bestVertex =
					 * lookupVertex;
					 */

					vertexQ.offer(lookupVertex);

				}

			}

			currentVertex.m_DijkstraVisit = true;
			currentVertex = vertexQ.pollLast();

			// minDist = maxValue;

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
