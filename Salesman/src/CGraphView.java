import javax.swing.*;

import java.awt.*;
import java.util.*;

public class CGraphView extends JFrame {
	private static final long serialVersionUID = 1L;
    private static final int HEIGHT = 800;
    private static final int WIDTH = 800;
    private ArrayList<Object> m_Elements;
    public CGraphView() {
    	m_Elements=new ArrayList<Object>();
    	setTitle("Práctica Greedy");
        setSize(HEIGHT, WIDTH);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
    public void paint(Graphics g) {
    	super.paint(g);
    	Insets inset=getInsets();
    	CPoint mins=new CPoint(1e99,1e99);
    	CPoint maxs=new CPoint(-1e99,-1e99);
    	for (int i=0;i<m_Elements.size();++i) {
    		if (m_Elements.get(i) instanceof CGraph) {
    			CGraph gr=(CGraph) m_Elements.get(i);
    			gr.AddRectHull(mins, maxs);
    		}
    		else if (m_Elements.get(i) instanceof CTrack) {
    			CTrack track=(CTrack) m_Elements.get(i);
    			track.AddRectHull(mins, maxs);
    		}
    	}
    	Dimension dim=getSize();
    	double escX=(dim.getWidth()-inset.left-inset.right-40)/(maxs.m_X-mins.m_X);
    	double escY=(dim.getHeight()-inset.top-inset.bottom-40)/(maxs.m_Y-mins.m_Y);
    	double esc=escX;
    	if (escY<escX) esc=escY;
    	g.translate(inset.left-(int) (mins.m_X*esc)+20,inset.top- (int) (mins.m_Y*esc)+20);
    	for (int i=0;i<m_Elements.size();++i) {
    		if (m_Elements.get(i) instanceof CGraph) {
    			CGraph gr=(CGraph) m_Elements.get(i);
    			gr.Draw(g, esc);
    		}
    		else if (m_Elements.get(i) instanceof CTrack) {
    			CTrack track=(CTrack) m_Elements.get(i);
    			track.Draw(g, esc);
    		}
    		else if (m_Elements.get(i) instanceof CVisits) {
    			CVisits visits=(CVisits) m_Elements.get(i);
    			visits.Draw(g, esc);
    		}
    	}
    }
    public void ShowGraph(CGraph graph) {
    	m_Elements.add(graph);
    	repaint();
    }
    public void ShowVisits(CVisits visits) {
    	m_Elements.add(visits);
    	repaint();
    }
    public void ShowTrack(CTrack track) {
    	m_Elements.add(track);
    	repaint();
    }
    public void Clear() {
    	m_Elements.clear();
    	repaint();
    }
}
