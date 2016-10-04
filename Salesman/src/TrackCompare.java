
import java.util.Comparator;

public class TrackCompare implements Comparator<CTrack> {

	public int compare(CTrack a, CTrack b) {
		if (a.Length()<b.Length()){
			return -1;
		}
		else if (a.Length()>b.Length()){
			return 1;
		}
		else{
			return 0;
		}
	}	
}
