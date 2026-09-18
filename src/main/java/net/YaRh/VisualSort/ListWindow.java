package net.YaRh.VisualSort;

import net.YaRh.CheapLog.logging.Logger;
import sas.Rectangle;
import sas.View;
import sasio.Button;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static net.YaRh.VisualSort.Config.*;

/**
 * Handles the window and placement of single lists
 *
 * @since 2.0.0
 */
public final class ListWindow {
	public static final Logger LOGGER = new Logger("ListWindow");
	
	private static final View window = new View(
			minWindowWidth.get(),
			listHeight.get(),
			"Visual List"
	);
	private static final Button step = new Button(
			1, 1,
			30, 20,
			"Step", Color.GRAY
	);
	
	public static List<VisualList> lists = new ArrayList<>();
	
	public static final List<Rectangle> deleted = new ArrayList<>();
	
	static void recycle(Rectangle r) {
		deleted.add(r);
	}
	
	static Rectangle recycleRectangle() {
		if (deleted.isEmpty()) return null;
		
		return deleted.remove(0);
	}
	
	static void updateWindow() {
		int height = stepByStep.get() ? 30 : 0;
		height += (listHeight.get() + listSpacing.get()) * lists.size();
		
		int width = minWindowWidth.get();
		for (VisualList list : lists) {
			int lw = list.calculateWidth();
			if (width < lw) width = lw;
		}
		
		window.setSize(
				width,
				height
		);
		
		updateLists();
		
		lists.forEach(VisualList::updateWindow);
	}
	
	static void add(VisualList list) {
		lists.add(list);
		updateWindow();
	}
	
	static void setBGColor(Color color) {
		window.setBackgroundColor(color);
	}
	
	static void setStepByStep(boolean sbs) {
		step.setHidden(!sbs);
		step.setActivated(!sbs);
	}
	
	private static void updateLists() {
		int height = stepByStep.get() ? 30 : 0;
		int stepHeight = listHeight.get() + listSpacing.get();
		for (VisualList list : lists) {
			list.baseHeight = height;
			height += stepHeight;
		}
	}
	
	public static void step() {
		if (stepDelay.get() != null) {
			window.wait((int) (stepDelay.get() * 100));
			return;
		}
		if (!stepByStep.get()) return;
		do {
			window.wait(100);
		} while (!step.clicked());
	}
	
	private ListWindow() {}
}