import java.awt.*;
import java.util.*;

import java.io.*;
// import java.util.Scanner;

public class CGraph {
  public ArrayList<CVertex> m_listaVertex;
  boolean solvedGraph;

  //////////// debug variables///////////
  static final boolean verbose = false;
  static String debugIndent = "";
  //////////// debug variables///////////

  public CGraph() {
    m_listaVertex = new ArrayList<CVertex>();
    solvedGraph = false;
  }

  public void clear() {
    m_listaVertex.clear();
  }

  // Vertices ----------------------------------------------------------------
  public CVertex findVertex(double x, double y) {
    for (int i = 0; i < m_listaVertex.size(); ++i) {
      CVertex tempVertex = m_listaVertex.get(i);
      if (tempVertex.m_Point.m_X == x && tempVertex.m_Point.m_Y == y)
        return tempVertex;
    }
    return null;
  }

  public CVertex GetVertex(double x, double y) throws Exception {
    CVertex v = findVertex(x, y);
    if (v == null)
      throw new Exception("El punto (" + x + "," + ") no pertenece al grafo");
    return v;
  }

  public CVertex GetVertex(CPoint p) throws Exception {
    return GetVertex(p.m_X, p.m_Y);
  }

  public boolean MemberP(CVertex v) {
    return m_listaVertex.contains(v);
  }

  public int nVertices() {
    return m_listaVertex.size();
  }

  // Edges -------------------------------------------------------------------
  public void Add(double x1, double y1, double x2, double y2) {
    CVertex p1 = findVertex(x1, y1);
    if (p1 == null) {
      p1 = new CVertex(x1, y1);
      m_listaVertex.add(p1);
    }
    CVertex p2 = findVertex(x2, y2);
    if (p2 == null) {
      p2 = new CVertex(x2, y2);
      m_listaVertex.add(p2);
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
    for (int i = 0; i < m_listaVertex.size(); ++i)
      n = n + m_listaVertex.get(i).m_Neighbors.size();
    return n / 2;
  }

  // Draw --------------------------------------------------------------------
  public void Draw(Graphics g, double esc) {

    g.setColor(new Color(100, 100, 100));
    for (int i = 0; i < m_listaVertex.size(); ++i) {
      CVertex p1 = m_listaVertex.get(i);
      int x1 = (int) Math.round(p1.m_Point.m_X * esc);
      int y1 = (int) Math.round(p1.m_Point.m_Y * esc);
      for (Iterator<CVertex> iter = p1.m_Neighbors.descendingIterator(); iter.hasNext();) {
        CPoint p2 = iter.next().m_Point;
        g.drawLine(x1, y1, (int) Math.round(p2.m_X * esc), (int) Math.round(p2.m_Y * esc));
      }
    }
    g.setColor(new Color(0, 128, 255));
    for (int i = 0; i < m_listaVertex.size(); ++i) {
      CVertex p1 = m_listaVertex.get(i);
      int x1 = (int) Math.round(p1.m_Point.m_X * esc);
      int y1 = (int) Math.round(p1.m_Point.m_Y * esc);
      g.fillOval(x1 - 4, y1 - 4, 9, 9);
      // This writes the node number next to each node.
      g.setFont(new Font("SansSerif", Font.BOLD, 14));
      g.setColor(new Color(255, 64, 0));
      p1.m_VertexID = i;
      // g.drawString(vertexNum,x1+15,y1);
    }
  }

  // AddRectHull -------------------------------------------------------------
  public void AddRectHull(CPoint min, CPoint max) {
    for (int i = 0; i < m_listaVertex.size(); ++i) {
      CPoint p = m_listaVertex.get(i).m_Point;
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
    for (int i = 0; i < m_listaVertex.size(); ++i) {
      CVertex v = m_listaVertex.get(i);
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
    clear();
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
        throw new Exception(filename + " no tiene formato de fichero de distancias (" + str + ")");
      double tmp1 = m_listaVertex.size();
      for (int i = 0; i < tmp1; i++) {
        CVertex v = m_listaVertex.get(i);
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
    for (int i = 0; i < m_listaVertex.size(); ++i) {
      CVertex v = m_listaVertex.get(i);
      System.out.print(i + ":" + v.m_DijkstraDistance + " ");
    }
    System.out.println();
  }

  // WriteDistances ----------------------------------------------------------
  public void WriteDistances(String filename) throws Exception {
    File f = new File(filename);
    BufferedWriter bw = new BufferedWriter(new FileWriter(f));
    bw.write("DISTANCES\n");
    for (int i = 0; i < m_listaVertex.size(); ++i) {
      CVertex v = m_listaVertex.get(i);
      bw.write(v.m_DijkstraDistance + "\n");
    }
    bw.close();
  }

  // Dijkstra
  // -------------------------------------------------------------------

  /**
   * Dijkstra implements the Dijkstra algorithm.
   * 
   * This method recieves a source Vertex and finds the shortest distance to each other Vertex in a
   * given graph.
   * 
   * @param start The vertex from which we want to know the distances to every other vertex.
   */
  public void Dijkstra(CVertex start) {

    double maxValue = Double.MAX_VALUE;

    // Assign a very high value to each vertex dijkstra distance
    for (CVertex vert : this.m_listaVertex) {
      vert.m_DijkstraDistance = maxValue;
      vert.m_DijkstraVisit = false;
    }

    start.m_DijkstraDistance = 0;
    start.m_DijkstraVisit = true;
    CVertex currentVertex = start;
    LinkedList<CVertex> vertexQ = new LinkedList<CVertex>();
    vertexQ.add(currentVertex);

    while (vertexQ.peek() != null) {
      double minDist = maxValue;
      double distToCurrent;
      for (CVertex lookupVertex : currentVertex.m_Neighbors) {
        distToCurrent =
            lookupVertex.m_Point.Distance(currentVertex.m_Point) + currentVertex.m_DijkstraDistance;

        if (distToCurrent < lookupVertex.m_DijkstraDistance) {
          // System.out.print("lookupVertex old DIST: " + lookupVertex.m_DijkstraDistance);
          lookupVertex.m_DijkstraDistance = distToCurrent;
          lookupVertex.m_DijkstraPrevious = currentVertex;
          lookupVertex.m_DijkstraVisit = false;
          // System.out.print(" lookupVertex new DIST: " + lookupVertex.m_DijkstraDistance + "\n");
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

    Comparator<CVertex> vertexCompare = new VertexCompare();

    // Assign a very high value to each vertex dijkstra distance

    for (CVertex vert : this.m_listaVertex) {
      vert.m_DijkstraDistance = maxValue;
      vert.m_DijkstraVisit = false;
      vert.m_DijkstraPrevious = null;
    }

    start.m_DijkstraDistance = 0;
    start.m_DijkstraVisit = true;

    CVertex currentVertex = start;
    PriorityQueue<CVertex> vertexQ = new PriorityQueue<CVertex>(vertexCompare);
    vertexQ.add(currentVertex);

    while (currentVertex != null) {

      double distToCurrent;

      for (CVertex lookupVertex : currentVertex.m_Neighbors) {
        distToCurrent =
            lookupVertex.m_Point.Distance(currentVertex.m_Point) + currentVertex.m_DijkstraDistance;

        if (distToCurrent < lookupVertex.m_DijkstraDistance) {
          lookupVertex.m_DijkstraDistance = distToCurrent;
          lookupVertex.m_DijkstraPrevious = currentVertex;
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

      visitList.remove(currentVertex);

      CTrack tempTrack = new CTrack(this);
      this.DijkstraQueue(currentVertex);

      for (CVertex temp : visitList) {
        if ((temp != lastVertex) && (temp.m_DijkstraDistance <= nextVertex.m_DijkstraDistance)) {
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
  CTrack bestSolution;
  double bestLength;

  public CTrack SalesmanTrackBacktracking(CVisits visits) throws Exception {

    LinkedList<CVertex> visitList = visits.toCVertexList(this);
    for (CVertex tmpVertex : visitList) {
      tmpVertex.m_VertexToVisit = true;
    }
    CVertex firstVertex = visitList.getFirst();
    CVertex lastVertex = visitList.getLast();
    firstVertex.m_VertexToVisit = false;
    lastVertex.m_VertexToVisit = false;

    bestSolution = new CTrack(this);
    bestLength = Double.MAX_VALUE;
    int depth = 0;
    CBacktrackVertex currentPath = null;
    CBacktrackVertex savedPath = null;
    int visitCounter = visitList.size() - 1;

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
        System.out.println(
            debugIndent + "#####################################################################");
        System.out.println(debugIndent + "Potential solution: " + bestSolution.toString());
        System.out.println(
            debugIndent + "#####################################################################");
      }
      ////////////////// DEBUG///////////////////////////////////////////////
      return;
    }

    // if the path hasn't ended, but our current vertex is in the visits list
    else if (currentVertex.m_VertexToVisit) {

      // if we haven't visited the current vertex, "save" a copy of our current path on savedPath
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
      // navigate currentpath copy looking for the current neighbor,
      //  until we get to our previously saved path
      while (tmpCurrentPath != savedPath) {
        // break the navigation if we find it
        if (tmpCurrentPath.m_current == currentNeighbor)
          break;
        tmpCurrentPath = tmpCurrentPath.m_previous;
      }
      // if we didn't find the neighbor between our current path and our saved path,
      //  do a recursive call
      if (tmpCurrentPath == savedPath) {
        // compute the new distance
        double nextDistance =
            currentLength + currentVertex.m_Point.Distance(currentNeighbor.m_Point);
        depth++;
        // recursive call
        recursiveBacktracking(currentPath, savedPath, currentNeighbor, nextDistance, visitCounter,
            lastVertex, depth);
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
    // navigate our way through the m_previous CVertex in the current path copy looking for current
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


  // =====================================================================================
  // SalesmanTrackBacktrackingGreedy
  // =====================================================
  // =====================================================================================
  // SalesmanTrackBacktrackingGreedy
  // -----------------------------------------------------
  /**
   * Implements the Backtracking algorithm with the greedy modifier.
   *
   * We search through a graph, given a visits list, for the optimal route between a starting and an
   * ending node that visits all vertex in the list. We use the dijkstrapriorityqueue method to
   * select the "best" vertex to visit next
   * 
   * @param visits contains the vertex we need to visit.
   * @return CTrack object with the optimal track.
   * @throws Exception
   */
  public CTrack SalesmanTrackBacktrackingGreedy(CVisits visits) throws Exception {

    bestSolution = new CTrack(this);
    bestSolution = SalesmanTrackGreedy(visits);

    LinkedList<CVertex> visitList = visits.toCVertexList(this);
    LinkedList<CVertex> visitedList = new LinkedList<>();
    for (CVertex tmpVertex : visitList) {
      tmpVertex.m_VertexToVisit = true;
    }

    int visitsCount = visitList.size();
    visitList.peekFirst().m_VertexToVisit = false;
    visitList.peekLast().m_VertexToVisit = false;
    CVertex lastVertex = visitList.pollLast();
    CVertex firstVertex = visitList.pollFirst();
    visitedList.add(firstVertex);

    CTrack currentTrack = new CTrack(this);
    currentTrack.AddFirst(firstVertex);
    visitsCount--;

    recursiveBacktrackingGreedy(currentTrack, firstVertex, 0.0, visitList, lastVertex, visitedList,
        visitsCount);

    return bestSolution;

  }

  void recursiveBacktrackingGreedy(CTrack currentTrack, CVertex currentVertex, double currentLength,
      LinkedList<CVertex> visitList, CVertex lastVertex, LinkedList<CVertex> visitedList,
      int visitsCount) throws Exception {
    ////////////////// DEBUG///////////////////////////////////////////////
    if (verbose) {
      debugIndent = debugIndent + "|  ";
      System.out.println(debugIndent + "Entering new vertex");
    }
    ////////////////// DEBUG///////////////////////////////////////////////

    // pruning
    
    this.DijkstraQueue(currentVertex);
    
    if (((currentLength + lastVertex.m_DijkstraDistance) > bestLength)) {
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
      System.out.println(debugIndent + "with visitCounter: " + visitsCount);
    }
    ////////////////// DEBUG///////////////////////////////////////////////


    // if the path has ended on thelast vertex AND we visited every vertex in the list,save it
    if ((visitsCount == 1)) {

      currentTrack.Append(getDijkstraTrack(currentVertex, lastVertex));
      bestSolution = currentTrack;
      bestLength = currentLength + lastVertex.m_DijkstraDistance;
      ////////////////// DEBUG///////////////////////////////////////////////
      if (verbose) {
        debugIndent = debugIndent.substring(3);
        System.out.println(
            debugIndent + "#####################################################################");
        System.out.println(debugIndent + "Potential solution: " + bestSolution.toString());
        System.out.println(
            debugIndent + "#####################################################################");
      }
      ////////////////// DEBUG///////////////////////////////////////////////
      return;
    }

    // add a new head to our current path: currentVertex
    // iterate over the neighbor list on currentVertex
    for (CVertex nextVisit : visitList) {

      if (!visitedList.contains(nextVisit)) {
        this.DijkstraQueue(currentVertex);
        double nextDistance = nextVisit.m_DijkstraDistance + currentLength;
        if (nextDistance > bestLength) {
          continue;
        }
        visitedList.addLast(nextVisit);
        CTrack tempTrack = new CTrack(this);
        tempTrack.Append(currentTrack);
        tempTrack.Append(getDijkstraTrack(currentVertex, nextVisit));
        recursiveBacktrackingGreedy(tempTrack, nextVisit, nextDistance, visitList, lastVertex,
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
