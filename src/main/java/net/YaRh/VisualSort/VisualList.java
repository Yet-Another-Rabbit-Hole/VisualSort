package net.YaRh.VisualSort;

import net.YaRh.CheapLog.logging.Logger;
import sas.Rectangle;
import sas.View;
import sasio.Button;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static java.lang.Math.max;
import static net.YaRh.CheapLog.Logging.*;
import static net.YaRh.VisualSort.Config.*;

public class VisualList extends ArrayList<Integer> {
	public static final Logger LOGGER = new Logger("VisualList");
	
	private static Rectangle newRectangle(int e) {
		int offset = columnSpacing.get();
		offset += (columnWidth.get() + columnSpacing.get()) * columns.size();
		
		return new Rectangle(
				offset,
				window.getHeight() - columnSpacing.get(),
				columnWidth.get(),
				max(e, minColumnHeight.get()),
				Config.stepByStep.get()
						? addedColumnColor.get()
						: defaultColumnColor.get()
		);
	}
	
	static void setBGColor(Color color) {
		window.setBackgroundColor(color);
	}
	
	static void setStepByStep(boolean sbs) {
		step.setHidden(!sbs);
		step.setActivated(!sbs);
	}
	
	private static final View window = new View(
			minWindowWidth.get(),
			minWindowHeight.get(),
			"Visual List"
	);
	private static final Button step = new Button(
			1, 1,
			30, 20,
			"Step", Color.GRAY
	);
	public static final List<Rectangle> columns = new ArrayList<>();
	
	private static boolean inUse = false;
	
	@Visual
	public VisualList() {
		super();
		
		if (inUse)
			throw new RuntimeException("Currently not more than one list is supported");
		
		inUse = true;
	}
	@Visual
	public VisualList(List<Integer> list) {
		super(list);
		
		if (inUse)
			throw new RuntimeException("Currently not more than one list is supported");
		
		inUse = true;
	}
	
	@Visual
	@Override
	public boolean add(Integer e) {
		if (!super.add(e)) return false;
		
		LOGGER.log.println("Adding int %d", e);
		
		columns.add(newRectangle(e));
		
		step();
		updateWindow();
		
		return true;
	}
	
	@Visual
	@Override
	public Integer remove(int index) {
		int res = super.remove(index);
		
		LOGGER.log.println("Removing int at %d", index);
		
		if (stepByStep.get()) {
			columns.get(index).setColor(removedColumnColor.get());
			step();
		}
		
		columns.remove(index);
		
		updateWindow();
		
		return res;
	}
	
	@Visual
	@Override
	public void add(int index, Integer element) {
		super.add(index, element);
		
		LOGGER.log.println("Adding %d at %d", element, index);
		
		columns.add(index, newRectangle(element));
		
		step();
		updateWindow();
	}
	
	@Visual
	@Override
	public Integer set(int index, Integer element) {
		int res = super.set(index, element);
		
		LOGGER.log.println("Setting value at %d to %d", index, element);
		
		columns.get(index).setColor(alteredColumnColor.get());
		updateWindow();
		
		return res;
	}
	
	@Visual
	@Override
	public boolean addAll(Collection<? extends Integer> c) {
		if (!super.addAll(c)) return false;
		
		LOGGER.log.println("Adding list of %d items", c.size());
		
		List<Rectangle> l = new ArrayList<>();
		for (Integer i : c) {
			l.add(newRectangle(i));
		}
		columns.addAll(l);
		
		step();
		updateWindow();
		
		return true;
	}
	
	@Visual
	@Override
	public boolean addAll(int index, Collection<? extends Integer> c) {
		if (!super.addAll(index, c)) return false;
		
		LOGGER.log.println("Adding list of %d items at index %d", c.size(), index);
		
		List<Rectangle> l = new ArrayList<>();
		for (Integer i : c) {
			l.add(newRectangle(i));
		}
		columns.addAll(index, l);
		
		step();
		updateWindow();
		
		return true;
	}
	
	@Visual
	@Override
	public void clear() {
		super.clear();
		
		LOGGER.log.println("Clearing list");
		
		if (Config.stepByStep.get()) {
			columns.forEach(c -> c.setColor(removedColumnColor.get()));
			step();
		}
		
		columns.clear();
		
		updateWindow();
	}
	
	/**
	 * Swaps two ints in the list
	 */
	public void swap(int i1, int i2) {
		super.add(super.remove(i1), i2);
		
		LOGGER.log.println("Swapping ints %d and %d", i1, i2);
		
		columns.get(i1).setColor(swappedColumnColor.get());
		columns.get(i2).setColor(swappedColumnColor.get());
		
		step();
		updateWindow();
	}
	
	private void updateWindow() {
		if (isEmpty()) {
			window.setSize(
					minWindowWidth.get(),
					minWindowHeight.get()
			);
			return;
		}
		
		int maxVal = Collections.max(this);
		
		LOGGER.debug.println("Max value: %d", maxVal);
		
		int height = maxVal + (columnSpacing.get() * 2);
		height = max(height, minWindowHeight.get());
		int width = ((columnWidth.get() + columnSpacing.get()) * size()) + columnSpacing.get();
		width = max(width, minWindowWidth.get());
		
		LOGGER.debug.println("Window size: W%dxH%d", width, height);
		
		window.setSize(
				width,
				height + (Config.stepByStep.get() ? 30 : 0)
		);
		
		LOGGER.debug.println("size: %d, columns.size: %d", size(), columns.size());
		
		if (size() != columns.size())
			throw new IllegalStateException("An internal error occurred");
		
		updateColumns();
		
		step();
		columns.forEach(c -> c.setColor(defaultColumnColor.get()));
	}
	
	private void updateColumns() {
		LOGGER.debug.println("Updating columns");
		LOGGER.debug.println("s %d, c %d", size(), columns.size());
		
		int offset = columnSpacing.get();
		List<Integer> scaled = scaledList();
		for (int i = 0; i < size(); i++) {
			int value = scaled.get(i);
			int columnHeight = max(value, minColumnHeight.get());
			Rectangle r = columns.get(i);
			r.scaleTo(columnWidth.get(), columnHeight);
			r.moveTo(offset, window.getHeight() - columnSpacing.get() - columnHeight);
			
			LOGGER.debug.println("Setting column %d to height %d at %d with value %d",
			              i, columnHeight, offset, value);
			
			offset += columnWidth.get() + columnSpacing.get();
		}
	}
	
	public void step() {
		if (stepDelay.get() != null) window.wait((int) (stepDelay.get() * 100));
		if (!stepByStep.get()) return;
		do {
			window.wait(100);
		} while (!step.clicked());
	}
	
	/*private List<Integer> scaledList() {
		int max = max(Collections.max(this), minWindowHeight.get());
		return stream().map(
				value -> (int) Math.round(
						minColumnHeight.get() + (double) value * (max - minColumnHeight.get()) / max
				)
		).toList();
	}*/
	
	public List<Integer> scaledList() {
		int min = Collections.min(this);
		int max = max(Collections.max(this), minWindowHeight.get());
		int minHeight = minColumnHeight.get();
		
		return stream()
				.map(value -> minHeight + (value - min) * (max - minHeight) / (max - min))
				.toList();
	}
	
	public boolean isOrdered() {
		List<Integer> ordered = new ArrayList<>(this);
		Collections.sort(ordered);
		boolean r = equals(ordered);
		
		LOGGER.log.println("List is%s ordered", r ? "" : " not");
		
		if (!r) return false;
		
		columns.forEach(c -> c.setColor(sortedColumnColor.get()));
		
		step();
		updateWindow();
		
		return true;
	}
	
	public void sort() {
		LOGGER.log.println("Ordering list");
		
		info.println(this);
		Collections.sort(this);
		info.println(this);
		
		updateColumns();
		isOrdered();
	}
}