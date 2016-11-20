import java.util.ArrayList;

public class CBBBestTrack {
  public double pathLength;
  public CTrack track;
  ArrayList<CBBBestTrack> bestTracks;
  
  public CBBBestTrack(CTrack aTrack, int sublistCapacity){
    this.track = aTrack;
    if (aTrack.m_Vertices.peekLast()!=null){
      pathLength = aTrack.m_Vertices.peekLast().m_DijkstraDistance;
    } else {
      pathLength = 0;
    }
    bestTracks = new ArrayList<CBBBestTrack>(sublistCapacity);
  }

}
