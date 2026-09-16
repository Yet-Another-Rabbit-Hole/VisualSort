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
		Config.columnWidth.set(3);
		
		//List<Integer> l = new VisualList();
		VisualList l = new VisualList();
		
		l.add(5);
		l.add(10);
		l.add(20);
		
		l.remove(2);
		
		l.addAll(List.of(1, 3, 7, 8, 2, 3, 7, 4));
		
		//l.isOrdered();
		
		l.sort();
		
		log.println(l.scaledList().get(2));
		log.println(l.get(2));
		log.println(VisualList.columns.get(2).getShapeHeight());
		
		log.println(l.scaledList().get(1));
		log.println(l.get(1));
		log.println(VisualList.columns.get(1).getShapeHeight());
		
		log.println(l.scaledList().get(3));
		log.println(l.get(3));
		log.println(VisualList.columns.get(3).getShapeHeight());
	}
}