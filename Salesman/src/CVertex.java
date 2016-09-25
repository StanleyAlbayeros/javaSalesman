import java.util.LinkedList;

public class CVertex {
	public CPoint m_Point;
	public LinkedList<CVertex> m_Neighbords;
	// Campos para el algoritmo de Dijkstra
	public double m_DijkstraDistance;
	public boolean m_DijkstraVisit;	
	public CVertex m_DijkstraPrevious;
	
	// Campos para el algoritmo de backtracking
	public boolean m_VertexToVisit; // Vertice a visitar (en lista visits)
	public CVertex(double x, double y) {
		m_Point=new CPoint(x,y);
		m_Neighbords=new LinkedList<CVertex>(); 
	}
}
