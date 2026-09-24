import net.YaRh.CheapLog.Logging;
import net.YaRh.VisualSort.Config;
import net.YaRh.VisualSort.VisualList;

import java.util.List;

import static net.YaRh.CheapLog.Logging.*;

public class Main {
	public static void main(String[] args) {
		net.YaRh.CheapLog.Config.enableAll();
		net.YaRh.CheapLog.Config.debugging.disable();
		net.YaRh.CheapLog.Config.thread.disable();
		net.YaRh.CheapLog.Config.location.disable();
		
		Config.stepByStep.disable();
		
		//List<Integer> l = new VisualList();
		
		VisualList l1 = new VisualList(List.of(1,2,3));
		
		VisualList l2 = new VisualList(List.of(3,2,1));
		
		l1.add(6);
		
		l2.remove(1);
		
		l1.close();
		
		l2.clear();
		
		checkpoint();
		
		l2.addAll(List.of(1,3,6,8,4,2,3,6));
		
		l2.add(3);
		
		l2.remove(5);
	}
	
	static void checkpoint() {
		Logging.info.println("Checkpoint reached");
		
		Config.stepByStep.enable();
		net.YaRh.CheapLog.Config.location.enable();
		//VisualList.LOGGER.debugging.enable();
	}
}