import java.util.ArrayList;

public class CBBBestTrack {
  public double pathLength;
  public CTrack track;
  
  public CBBBestTrack(CTrack aTrack){
    this.track = aTrack;
    if (aTrack.m_Vertices.peekLast()!=null){
      pathLength = aTrack.m_Vertices.peekLast().m_DijkstraDistance;
    } else {
      pathLength = 0;
    }
  }

}
