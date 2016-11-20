import java.awt.Color;
import java.awt.Graphics;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Scanner;


public class CVisits {
  public LinkedList<CPoint> m_Points;

  public CVisits() {
    m_Points = new LinkedList<CPoint>();
  }

  public boolean MemberP(double x, double y) {
    for (Iterator<CPoint> iter = m_Points.iterator(); iter.hasNext();) {
      CPoint p = iter.next();
      if (p.m_X == x && p.m_Y == y)
        return true;
    }
    return false;
  }

  public void Add(double x, double y) throws Exception {
    // if (MemberP(x,y)) throw new Exception("Visita repetida (" + x + "," + y +")");
    m_Points.addLast(new CPoint(x, y));
  }

  public void Clear() {
    m_Points.clear();
  }

  /**
   * Returns a linkedlist containing all the visits vertex
   * 
   * @param graph CGraph which contains the vertex, used to check if they are actually in it
   * @return a linked list with all the vertex
   */
  public LinkedList<CVertex> toCVertexList(CGraph graph) {

    LinkedList<CVertex> result = new LinkedList<CVertex>();

    for (CPoint currentPoint : m_Points) {

      try {
        result.add(graph.GetVertex(currentPoint));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    return result;
  }  /**
   * Returns an ArrayList containing all the visits vertex
   * 
   * @param graph CGraph which contains the vertex, used to check if they are actually in it
   * @return the resulting arraylist with all the vertex
   */
  public ArrayList<CVertex> toCVertexArrayList(CGraph graph) {

    ArrayList<CVertex> result = new ArrayList<CVertex>(this.m_Points.size());

    for (CPoint currentPoint : m_Points) {

      try {
        result.add(graph.GetVertex(currentPoint));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    return result;
  }

  // Draw --------------------------------------------------------------------
  public void Draw(Graphics g, double esc) {
    boolean start = true;
    for (Iterator<CPoint> iter = m_Points.iterator(); iter.hasNext();) {
      CPoint p = iter.next();
      int x1 = (int) Math.round(p.m_X * esc);
      int y1 = (int) Math.round(p.m_Y * esc);
      if (start) {
        g.setColor(new Color(0, 0, 0));
        g.fillRect(x1 - 5, y1 - 5, 11, 11);
        // g.fillRect(x1-7, y1-7, 15, 15);
        start = false;
      } else if (iter.hasNext()) {
        g.setColor(new Color(255, 128, 64));
        g.fillOval(x1 - 5, y1 - 5, 11, 11);
      } else {
        g.setColor(new Color(255, 255, 255));
        g.fillRect(x1 - 5, y1 - 5, 11, 11);
      }
    }
  }

  public void AddRectHull(CPoint min, CPoint max) {
    for (Iterator<CPoint> iter = m_Points.iterator(); iter.hasNext();) {
      CPoint p = iter.next();
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

  // Files ----------------------------------------------------------------
  public void Write(String filename) throws Exception {
    File f = new File(filename);
    BufferedWriter bw = new BufferedWriter(new FileWriter(f));
    bw.write("VISITS\n");
    for (Iterator<CPoint> iter = m_Points.iterator(); iter.hasNext();) {
      CPoint p = iter.next();
      bw.write(p.m_X + " " + p.m_Y + "\n");
    }
    bw.close();
  }

  public void Read(String filename) throws Exception {
    Clear();
    File f = new File(filename);
    Scanner s;
    s = new Scanner(f);
    s.useLocale(Locale.US);
    try {
      if (!s.nextLine().equalsIgnoreCase("VISITS"))
        throw new Exception(filename + " no tiene formato de fichero de visitas");
      // leer puntos
      while (s.hasNextLine()) {
        String linea = s.nextLine();
        // System.out.println(linea);
        Scanner sl = new Scanner(linea);
        sl.useLocale(Locale.US);
        double x = sl.nextDouble();
        double y = sl.nextDouble();
        sl.close();
        Add(x, y);
      }
    } finally {
      s.close();
    }
  }

  // Print -------------------------------------------------------------------
  public String toString() {
    String str = "[";
    for (Iterator<CPoint> iter = m_Points.iterator(); iter.hasNext();) {
      CPoint p = iter.next();
      str = str + "(" + p.m_X + "," + p.m_Y + ")";
      if (iter.hasNext())
        str = str + ",";
    }
    return str + "]";
  }

  public ArrayDeque<CVertex> toArrayDeque(CGraph graph) {

    ArrayDeque<CVertex> result = new ArrayDeque<CVertex>();

    for (CPoint currentPoint : m_Points) {

      try {
        result.add(graph.GetVertex(currentPoint));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    return result;
  }

}
