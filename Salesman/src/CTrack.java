import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Scanner;
import java.awt.Font;

public class CTrack {
	public LinkedList<CVertex> m_Vertices;
	public CGraph m_Graph;
	public CTrack(CGraph graph) {
		m_Graph=graph;
		m_Vertices= new LinkedList<CVertex>();
	}
	public void AddLast(double x, double y) throws Exception {
		CVertex v=m_Graph.GetVertex(x, y);
		m_Vertices.addLast(v);
	}
	public void AddLast(CPoint p) throws Exception {
		CVertex v=m_Graph.GetVertex(p);
		m_Vertices.addLast(v);
	}
	public void AddLast(CVertex v) throws Exception {
		assert m_Graph.MemberP(v);
		m_Vertices.addLast(v);
	}
	public void AddFirst(CVertex v) {
		assert m_Graph.MemberP(v);
		m_Vertices.addFirst(v);
	}
	public void Clear() {
		m_Vertices.clear();
	}
	public void Append(CTrack t) {
		assert m_Graph==t.m_Graph;
		for (Iterator<CVertex> iter=t.m_Vertices.iterator(); iter.hasNext();) m_Vertices.addLast(iter.next());
	}
	public void AppendBefore(CTrack t) {
		assert m_Graph==t.m_Graph;
		for (Iterator<CVertex> iter=t.m_Vertices.descendingIterator(); iter.hasNext();) m_Vertices.addFirst(iter.next());
	}

	// Draw --------------------------------------------------------------------
	public void Draw(Graphics _g,double esc) {
		Graphics2D g = (Graphics2D) _g;		
		int x0=-9999;
		int y0=-9999;
		CVertex v0=null;
		for (Iterator<CVertex> iter=m_Vertices.iterator(); iter.hasNext();) {
			CVertex v1=iter.next();
			CPoint p=v1.m_Point;
			int x1=(int) Math.round(p.m_X*esc);
			int y1=(int) Math.round(p.m_Y*esc);
			if (x0!=-9999) {
				if (v0.m_Neighbords.contains(v1)) g.setColor(new Color(0,200,0));
				else g.setColor(new Color(200,0,0));
				Stroke stroke=g.getStroke();
				g.setStroke(new BasicStroke(3));
				g.drawLine(x0,y0,x1,y1);
				int x=(x0*2+x1)/3;
				int y=(y0*2+y1)/3;
				double dx=x1-x0;
				double dy=y1-y0;
				double d=Math.sqrt(dx*dx+dy*dy);
				dx=dx/d;
				dy=dy/d;
				double nx=dy;
				double ny=-dx;
				g.drawLine((int) (x+nx*7-dx*7+0.5),(int) (y+ny*7-dy*7+0.5),x,y);
				g.drawLine((int) (x-nx*7-dx*7+0.5),(int) (y-ny*7-dy*7+0.5),x,y);
				g.setStroke(stroke);
			}
			x0=x1;
			y0=y1;
			v0=v1;
		}
		boolean first=true;
		for (Iterator<CVertex> iter=m_Vertices.iterator(); iter.hasNext();) {
			CPoint p=iter.next().m_Point;
			int x1=(int) Math.round(p.m_X*esc);
			int y1=(int) Math.round(p.m_Y*esc);
			
			if (first) {
				g.setColor(new Color(255,0,0));
				g.fillRect(x1-8, y1-8, 17, 17);
				first=false;
			}
			else if (iter.hasNext()){
				g.setColor(new Color(0,255,0));
				g.fillOval(x1-8, y1-8, 17, 17);
			}
			else {
				g.setColor(new Color(0,255,0));
				g.fillRect(x1-8, y1-8, 17, 17);
			}
		}
		int i2=0;
		LinkedList<CPoint> apariciones=new LinkedList<CPoint>(); 
		g.setFont(new Font("SansSerif",Font.BOLD,14));
		for (Iterator<CVertex> iter=m_Vertices.iterator(); iter.hasNext(); ++i2) {
			CPoint p=iter.next().m_Point;
			int x1=(int) Math.round(p.m_X*esc);
			int y1=(int) Math.round(p.m_Y*esc);
			int ap=0;
			for (Iterator<CPoint> iterAp=apariciones.iterator(); iterAp.hasNext();) {
				if (iterAp.next()==p) ++ap;
			}
			g.setColor(new Color(255,64,0));
			g.drawString("" + i2,x1+15,y1+15*ap);
			apariciones.add(p);
		}
	}
	public void AddRectHull(CPoint min, CPoint max) {
		for (Iterator<CVertex> iter=m_Vertices.iterator(); iter.hasNext();) {
			CPoint p=iter.next().m_Point;
			if (p.m_X<min.m_X) min.m_X=p.m_X;
			if (p.m_Y<min.m_Y) min.m_Y=p.m_Y;
			if (p.m_X>max.m_X) max.m_X=p.m_X;
			if (p.m_Y>max.m_Y) max.m_Y=p.m_Y;			
		}
		max.m_X+=15;
		max.m_Y+=15*3;
	}
	
	// Files ----------------------------------------------------------------	
    public void Write(String filename) throws Exception {
    	File f=new File(filename);
    	BufferedWriter bw = new BufferedWriter(new FileWriter(f));
    	bw.write("TRACK\n");
		for (Iterator<CVertex> iter=m_Vertices.iterator(); iter.hasNext();) {
    		CVertex v=iter.next();
			CPoint p=v.m_Point;
   			bw.write(p.m_X + " " + p.m_Y + "\n");
    	}
    	bw.close();
    }
    public void Read(String filename) throws Exception {
    	Clear();
		File f=new File(filename);
		Scanner s;
		s = new Scanner(f);
		s.useLocale(Locale.US);
		try {
			if (!s.nextLine().equalsIgnoreCase("TRACK")) throw new Exception(filename + " no tiene formato de fichero de camino");
			// leer v�rtices
			while (s.hasNextLine()) {
				String linea = s.nextLine();
				//System.out.println(linea);
				Scanner sl = new Scanner(linea);
				sl.useLocale(Locale.US);
				double x=sl.nextDouble();
				double y=sl.nextDouble();
				sl.close();
				AddLast(x,y);
				
			}
		} 
		finally {
			s.close();
		}
    }
    
    public boolean Compare(CTrack track1, CTrack track2){
    	
    	
    	return false;
    }
    // Print -------------------------------------------------------------------
    public String toString() {
    	String str="[";
		for (Iterator<CVertex> iter=m_Vertices.iterator(); iter.hasNext();) {
    		CVertex v=iter.next();
			CPoint p=v.m_Point;
   			str=str  + "(" + p.m_X + "," + p.m_Y + ")";
   			if (iter.hasNext()) str=str + ",";
		}		
		return str+ "]";
    }
    // Length -------------------------------------------------------------------
    public double Length() {
    	double l=0.0;
    	Iterator<CVertex> iter=m_Vertices.iterator();
    	if (!m_Vertices.isEmpty()) {
	    	CVertex v0=iter.next();
			while (iter.hasNext()) {
	    		CVertex v1=iter.next();
	    		l=l+v1.m_Point.Distance(v0.m_Point);
	    		v0=v1;
			}	
    	}
		return l;
    }
}
