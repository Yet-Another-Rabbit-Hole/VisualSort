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
		
		Config.stepByStep.enable();
		
		//List<Integer> l = new VisualList();
		
		VisualList l1 = new VisualList(List.of(1,2,3));
		
		VisualList l2 = new VisualList(List.of(3,2,1));
		
		l1.add(6);
		
		l2.remove(1);
	}
}