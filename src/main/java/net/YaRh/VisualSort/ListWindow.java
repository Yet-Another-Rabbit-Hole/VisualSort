package net.YaRh.VisualSort;

import net.YaRh.CheapLog.logging.Logger;
import sas.Rectangle;
import sas.View;
import sasio.Button;

import java.awt.*;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
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
	
	public static List<WeakReference<VisualList>> lists = new ArrayList<>();
	public static final List<Rectangle> deleted = new ArrayList<>();
	private static final ReferenceQueue<VisualList> queue = new ReferenceQueue<>();
	
	static {
		Thread cleaner = new Thread(() -> {
			try {
				while (!Thread.currentThread().isInterrupted()) {
					Reference<? extends VisualList> ref = queue.remove();
					lists.remove(ref);
				}
			} catch (InterruptedException e) {
				illegalState(new IllegalThreadStateException("Cleaner encountered an error"));
				Thread.currentThread().interrupt();
			}
		});
		
		cleaner.start();
		
		window.setBackgroundColor(backgroundColor.get());
	}
	
	static void recycle(Rectangle r) {
		r.setHidden(true);
		r.moveTo(0, 0);
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
		VisualList list;
		int lw;
		for (WeakReference<VisualList> ref : lists) {
			list = ref.get();
			if (list == null) continue;
			lw = list.calculateWidth();
			if (width < lw) width = lw;
		}
		
		window.setSize(
				width,
				height
		);
		
		updateLists();
		
		lists.forEach(ref -> {
			VisualList l = ref.get();
			if (l == null) return;
			l.windowUpdate();
		});
	}
	
	static void add(VisualList list) {
		lists.add(new WeakReference<>(list, queue));
		updateWindow();
		step();
	}
	
	static void remove(VisualList list) {
		for (int i = 0; i < lists.size(); i++)
			if (lists.get(i).refersTo(list)) {
				lists.remove(i);
				break;
			}
		
		updateWindow();
		step();
	}
	
	static void setBGColor(Color color) {
		window.setBackgroundColor(color);
	}
	
	static void setStepByStep(boolean sbs) {
		step.setHidden(!sbs);
		step.setActivated(sbs);
	}
	
	private static void updateLists() {
		int height = stepByStep.get() ? 30 : 0;
		int stepHeight = listHeight.get() + listSpacing.get();
		VisualList list;
		for (WeakReference<VisualList> ref : lists) {
			list = ref.get();
			if (list == null) continue;
			LOGGER.debug("%s".formatted(list));
			list.baseHeight = height;
			height += stepHeight;
		}
	}
	
	public static void step() {
		cleanup();
		if (stepDelay.get() != null) {
			window.wait((int) (stepDelay.get() * 100));
			return;
		}
		if (!stepByStep.get()) return;
		do {
			window.wait(100);
		} while (!step.clicked());
	}
	
	private static void cleanup() {
		lists.removeIf(ref -> ref.get() == null);
	}
	
	static void illegalState(Throwable reason) {
		throw new IllegalStateException("An internal error occurred", reason);
	}
	
	private ListWindow() {}
}