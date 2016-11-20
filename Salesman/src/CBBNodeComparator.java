import java.util.Comparator;

public class CBBNodeComparator implements Comparator<CBBNode>{

  @Override
  public int compare(CBBNode node1, CBBNode node2) {
    if (node1.nodeWeight > node2.nodeWeight){
      return 1;
    } else if (node1.nodeWeight == node2.nodeWeight){
      return 0;
    } else if (node1.nodeWeight < node2.nodeWeight) {
      return -1;
    } else return 0;
  }
  
}
