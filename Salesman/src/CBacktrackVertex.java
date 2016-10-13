
/** CBacktrackVertex is used to represent the head of a path that contains a visited vertex 
 *
 */
public class CBacktrackVertex {

	public CVertex m_current;
	public CBacktrackVertex m_previous;
	
	/** CBacktrackVertex constructor
	 * 
	 * @param previous previous CBacktrackVertex head of a path containing a vertex we need to stop by
	 * @param current current CVertex head of a path containing a vertex we need to stop by
	 * 
	 */
	public CBacktrackVertex(CBacktrackVertex previous, CVertex current){
		m_current = current;
		m_previous = previous;
	}
}