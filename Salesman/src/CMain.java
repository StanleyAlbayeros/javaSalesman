import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Random;

//import java.awt.*;
//import java.io.*;

public class CMain {
	static CGraphView m_View;
	// Rellenar con los datos de los dos alumnos que presentan la pr�ctica
	static String NombreAlumno1="Vernon";
	static String ApellidosAlumno1="Albayeros Duarte";
	static String NIAAlumno1="1272977"; // NIA alumno1
	static String NombreAlumno2="Eric";
	static String ApellidosAlumno2="Millan Rodriguez";
	static String NIAAlumno2="1311996"; // NIA alumno2 o "" si grupo de un alumno

	static String[] NIAS={
	    "1397750", "1365445", "1390451", "1359074", "1391791", "1332764", "1390203",
	    "1391402", "1395239", "1341199", "1264719", "1307766", "1391540", "1390932",
	    "1362857", "1390770", "1341247", "1390934", "1391725", "1390127", "1281175",
	    "1397833", "1311996", "1337854", "1395062", "1304626", "1331167", "1390687",
	    "1395359", "1391649", "1390450", "1391488", "1391713", "1391106", "1391664",
	    "1358228", "1412759", "1362679", "1366706", "1390282", "1391971", "1417251",
	    "1390536", "1272977"
	};
	static boolean NIACorrecto(String nia) {
		for (int i=0;i<NIAS.length; ++i) if (nia.equals(NIAS[i])) return true;
		return false;
	}
	// RandomGraph -------------------------------------------------------------
	public static CGraph RandomGraph(int nVertices,int nEdges) throws Exception 
	{
		CGraph g=new CGraph();
		CPoint[] vertices=new CPoint[nVertices];
		for (int i=0; i<nVertices; ++i) {
			double x,y;
			boolean encontrado;
			double minDist=100*100;
			do {
				x=(int) Math.round(Math.random()*1000);
				y=(int) Math.round(Math.random()*1000);
				encontrado=false;
				for (int j=0; j<i;++j) {
					double dx=vertices[j].m_X-x;
					double dy=vertices[j].m_Y-y;
					if (dx*dx+dy*dy<minDist) {
						encontrado=true;
						break;
					}
					minDist*=0.75;
				}
			} while (encontrado);
			vertices[i]=new CPoint(x,y);
			if (i!=0) {
				int j=(int) (Math.random()*i);
				g.Add(vertices[j].m_X,vertices[j].m_Y, x,y);
				--nEdges;
			}
			//if (i%100==0) System.out.println("Vertice " + i + " de " + nVertices);
		}
		while (nEdges>0) {
			int i=(int) (Math.random()*nVertices);
			int j=(int) (Math.random()*nVertices);
			if (i!=j && !g.GetVertex(vertices[i]).m_Neighbords.contains(g.GetVertex(vertices[j]))) {
				g.Add(vertices[i].m_X,vertices[i].m_Y, vertices[j].m_X,vertices[j].m_Y);
				--nEdges;				
			}
			//if (nEdges%100==0) System.out.println("Edges por generar " + nEdges);
		}
		return g;
	}
	
	// RamdomVisits ------------------------------------------------------------
	public static CVisits RandomVisits(CGraph g,int nVisits) throws Exception 
	{
		assert nVisits<g.m_Vertices.size();
		CVisits visits=new CVisits();
		while (nVisits>0) {
			int i=(int) (Math.random()*g.m_Vertices.size());
			CPoint p=g.m_Vertices.get(i).m_Point;
			if (!visits.MemberP(p.m_X,p.m_Y)) {
				visits.Add(p.m_X, p.m_Y);
				--nVisits;
			}
		}
		return visits;
	}
	// CiclicRamdomVisits ------------------------------------------------------------
	public static CVisits CiclicRandomVisits(CGraph g,int nVisits) throws Exception 
	{
		assert nVisits<g.m_Vertices.size();
		CVisits visits=new CVisits();
		while (nVisits>1) {
			int i=(int) (Math.random()*g.m_Vertices.size());
			CPoint p=g.m_Vertices.get(i).m_Point;
			if (!visits.MemberP(p.m_X,p.m_Y)) {
				visits.Add(p.m_X, p.m_Y);
				--nVisits;
			}
		}
		CPoint p=visits.m_Points.getFirst();
		visits.Add(p.m_X, p.m_Y);
		return visits;
	}
	// SaveRandomProblem -----------------------------------------------------------
	static void SaveRandomProblem(int n,int nVertices, int nEdges, int nVisits) 
	{
		try {
			System.out.println("SaveRandomProblem " + n + " Vertices=" + nVertices + " Edges=" + nEdges + " Visits=" + nVisits);
			CGraph graph=RandomGraph(nVertices,nEdges);
			CVisits visits=RandomVisits(graph,nVisits);
			graph.Write("Grafo" + n  + ".txt");
			visits.Write("Visitas" + n +".txt");
		}
		catch (Exception ex) {
			System.out.println("***EXCEPCION***");
			System.out.println(ex.toString());
			System.out.println(ex.getMessage());
			ex.printStackTrace(System.out);
		}
	}
	
	// SaveCiclicRandomProblem -----------------------------------------------------------
	static void SaveCiclicRandomProblem(int n,int nVertices, int nEdges, int nVisits) 
	{
		try {
			System.out.println("SaveCiclicRandomProblem " + n + " Vertices=" + nVertices + " Edges=" + nEdges + " Visits=" + nVisits);
			CGraph graph=RandomGraph(nVertices,nEdges);
			CVisits visits=CiclicRandomVisits(graph,nVisits);
			graph.Write("Grafo" + n  + ".txt");
			visits.Write("Visitas" + n +".txt");
		}
		catch (Exception ex) {
			System.out.println("***EXCEPCION***");
			System.out.println(ex.toString());
			System.out.println(ex.getMessage());
			ex.printStackTrace(System.out);
		}
	}
	
	static void PrintRandomAnalysis(String filename, int numGrafs) throws Exception
	{
		
	    String COMMA_DELIMITER = ",";	    
	    String FILE_HEADER = "Vertex,Edge,Dijkstra,DijkstraPriority";
	    
	    PrintWriter writer;
		try {
			writer = new PrintWriter(filename, "UTF-8");
			writer.println(FILE_HEADER);
			
			for (int i = 1; i <= numGrafs; i++) {
				int j = i;
				long t0, t1, t2, t3;
				int numVertex, numEdges;
				
				numVertex = i*5;
				numEdges = (numVertex*(numVertex-2))/4;
				System.out.println("Iteration: " + j + " Vertex number: " + numVertex + " Edge number: " + numEdges);

				CGraph graph = RandomGraph(numVertex,numEdges);
				System.out.println("\t Graph " + j + " Created");
			    java.util.Date date = new java.util.Date();
			    System.out.println(date);

				
				// dijkstra	
				System.out.println("\t\tStarting dijkstra on randomgraph " + j );
				System.gc();
				t0 = System.nanoTime();
				graph.Dijkstra(graph.m_Vertices.get(0));
				t1 = System.nanoTime();
				double tempD1 = (t1 - t0) / 1e9;

				// dijkstraqueue
				System.out.println("\t\tStarting dijkstraqueue on randomgraph " + j );
				System.gc();
				t2 = System.nanoTime();
				graph.DijkstraQueue(graph.m_Vertices.get(0));
				t3 = System.nanoTime();
				double tempD2 = (t3 - t2) / 1e9;

				// print
				//System.out.println("Dijkstra " + j + " time: " + tempD1 + " DijkstraQueue " + j + " time: " + tempD2 + " Time difference: " + tempDiff);
				writer.println(numVertex + COMMA_DELIMITER + numEdges + COMMA_DELIMITER + tempD1 + COMMA_DELIMITER + tempD2);
				System.out.println();
			}

			writer.close();
			System.out.println("Random analysis done, file created");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	// main --------------------------------------------------------------------
	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception
    {		
		System.out.println(NIAAlumno1);		
		System.out.println(NombreAlumno1);
		System.out.println(ApellidosAlumno1);
		System.out.println(NIAAlumno2);
		System.out.println(NombreAlumno2);
		System.out.println(ApellidosAlumno2);

		boolean testSeries = false;
		
		if (!NIACorrecto(NIAAlumno1)) throw new Exception("El NIA " + NIAAlumno1 + " no es de alumno matriculado");
		if (!NIAAlumno2.isEmpty() && !NIACorrecto(NIAAlumno2)) throw new Exception("El NIA " + NIAAlumno2 + " no es de alumno matriculado");
		
		if (args.length<2) {
			System.out.println("Uso: fichero algoritmo grafo [visitas] [salir]");
			return;
		}
		
		boolean salir=false;
		try {			
			String algorihtm=args[0];
			if (algorihtm.toLowerCase().equals("dijkstra") || algorihtm.toLowerCase().equals("dijkstraqueue")) {
				String graphFilename=args[1];
				
				System.out.println("Fichero de grafo: " + graphFilename);
				System.out.println("Algoritmo: " + algorihtm);
				if (args.length==4) {
					
					
					if (args[3].toLowerCase().equals("salir")) salir=true;
					else {
						System.out.println("Uso: fichero algoritmo grafo [visitas] [salir]");
						//return;
					}
				}
				CGraph graph=new CGraph();
				graph.Read(graphFilename);
				String graphDistanceFilename=args[2];
				
				//
				//graph = RandomGraph(10,19);
				//
				
				if (!salir) {
					m_View = new CGraphView();
					m_View.ShowGraph(graph);					
				}
				long t0,t1;
				
				if (!testSeries){
					if (algorihtm.toLowerCase().equals("dijkstra")) {
						System.gc();
						t0=System.nanoTime();
						graph.Dijkstra(graph.m_Vertices.get(0));
						t1=System.nanoTime();
					}
					else if (algorihtm.toLowerCase().equals("dijkstraqueue")) {
						System.gc();
						t0=System.nanoTime();
						graph.DijkstraQueue(graph.m_Vertices.get(0));
						t1=System.nanoTime();
					}
					else throw new Exception(algorihtm + " no es un algoritmo v�lido");
					
					graph.PrintDistances();
					System.out.println("Time: " + (t1-t0)/1e9);
					int errors = graph.CompareDijkstra(graphDistanceFilename);

					//System.out.println("Errors: " + errors);
					
					if (salir) {
						System.exit(0);
					}
				}
				if (testSeries){
					
					CGraph graphTest=new CGraph();
					int errD1 = 0;
					int errD2 = 0;
					boolean testProf = false;
					boolean generateAnalysis = true;
					
					if (testProf) {
						for (int i = 0; i < 25; i++) {
							int j = i + 1;
							StringBuilder filename = new StringBuilder(0);
							filename.append("Tests/Grafo");
							filename.append(j);
							filename.append(".txt");
							graphTest.Read(filename.toString());

							StringBuilder filenameDist = new StringBuilder(0);
							filenameDist.append("Tests/Distancias");
							filenameDist.append(j);
							filenameDist.append(".txt");

							long t2, t3;
							// dijkstra
							System.gc();
							t0 = System.nanoTime();
							graphTest.Dijkstra(graphTest.m_Vertices.get(0));
							t1 = System.nanoTime();
							errD1 = graphTest.CompareDijkstra(filenameDist.toString());
							double tempD1 = (t1 - t0) / 1e9;
							// dijkstraqueue
							System.gc();
							t2 = System.nanoTime();
							graphTest.DijkstraQueue(graphTest.m_Vertices.get(0));
							t3 = System.nanoTime();
							errD1 = graphTest.CompareDijkstra(filenameDist.toString());
							double tempD2 = (t3 - t2) / 1e9;
							double tempDiff = tempD2 - tempD1;

							// print
							System.out.println("Dijkstra " + j + " time: " + tempD1 + " Errors: " + errD1
									+ " DijkstraQueue " + j + " time: " + tempD2 + " Errors: " + errD2
									+ " Time difference: " + tempDiff);
						} 
					}
					if (generateAnalysis){
						int numRuns = 100;
						String analysisName = "analisis3";
						StringBuilder analysisN = new StringBuilder(0);
						analysisN.append(analysisName);
						analysisN.append(".txt");
						
						PrintRandomAnalysis(analysisN.toString(), numRuns);
						
						
					}

					
				}
				
			}
			else {
				String graphFilename=args[1];
				String visitsFilename=args[2];
				if (args.length==4 && args[3].toLowerCase().equals("salir")) salir=true;
				System.out.println("Fichero de grafo: " + graphFilename);
				System.out.println("Fichero de visitas: " + visitsFilename);
				System.out.println("Algoritmo: " + algorihtm);
				CGraph graph=new CGraph();
				graph.Read(graphFilename);
				CVisits visits=new CVisits();
				visits.Read(visitsFilename);
				if (!salir) {
					m_View = new CGraphView();
					m_View.ShowGraph(graph);
					m_View.ShowVisits(visits);
				}
				CTrack track;
				long t0,t1;
				if (algorihtm.toLowerCase().equals("greedy")) {
					System.gc();
					t0=System.nanoTime();
					track=graph.SalesmanTrackGreedy(visits);
					t1=System.nanoTime();
				}
				else if (algorihtm.toLowerCase().equals("backtracking")) {
					System.gc();
					t0=System.nanoTime();
					track=graph.SalesmanTrackBacktracking(visits);
					t1=System.nanoTime();
				}
				else if (algorihtm.toLowerCase().equals("backtrackinggreedy")) {
					System.gc();
					t0=System.nanoTime();
					track=graph.SalesmanTrackBacktrackingGreedy(visits);
					t1=System.nanoTime();
				}
				else if (algorihtm.toLowerCase().equals("branchandbound1")) {
					System.gc();
					t0=System.nanoTime();
					track=graph.SalesmanTrackBranchAndBound1(visits);
					t1=System.nanoTime();
				}
				else if (algorihtm.toLowerCase().equals("branchandbound2")) {
					System.gc();
					t0=System.nanoTime();
					track=graph.SalesmanTrackBranchAndBound2(visits);
					t1=System.nanoTime();
				}
				else if (algorihtm.toLowerCase().equals("branchandbound3")) {
					System.gc();
					t0=System.nanoTime();
					track=graph.SalesmanTrackBranchAndBound3(visits);
					t1=System.nanoTime();
				}
				else throw new Exception(algorihtm + " no es un algoritmo v�lido");
				if (!salir) {
					m_View.ShowTrack(track);
					m_View.ShowVisits(visits);
				}
				System.out.println("Track: " + track);
				System.out.println("Longitud: " + track.Length());
				System.out.println("Time: " + (t1-t0)/1e9);
			}
		}
		catch (Exception ex) {			
			System.out.println("***EXCEPCION***");
			System.out.println(ex.toString());
			System.out.println(ex.getMessage());
			ex.printStackTrace(System.out);
		}
		if (args.length>3) System.exit(0);
    }
}
