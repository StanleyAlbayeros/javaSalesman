
import java.util.Comparator;

public class CVertexComparator implements Comparator<CVertex> {
  public int compare(CVertex a, CVertex b) {
    if (a.m_DijkstraDistance < b.m_DijkstraDistance)
      return -1;
    else if (a.m_DijkstraDistance > b.m_DijkstraDistance)
      return 1;
    else
      return 0;
  }
}
