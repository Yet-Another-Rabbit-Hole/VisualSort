package net.YaRh.VisualSort;

import net.YaRh.CheapLog.logging.Logger;
import sas.Rectangle;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static net.YaRh.CheapLog.Logging.info;
import static net.YaRh.VisualSort.Config.*;

public class VisualList extends ArrayList<Integer> {
	public static final Logger LOGGER = new Logger("VisualList");
	
	/**
	 * Calling this method on a method ensures that the values get swapped even if the list isn't a {@link VisualList}
	 * <p>
	 * But if it is, {@link VisualList#swap(int, int)} is called on the list, making it visual
	 */
	public static void swap(List<Integer> list, int i1, int i2) {
		if (list instanceof VisualList vList) {
			vList.swap(i1, i2);
		} else {
			int v1 = list.get(i1);
			int v2 = list.get(i2);
			list.set(i1, v2);
			list.set(i2, v1);
		}
	}
	
	public static List<Integer> subList(List<Integer> list, int i1, int i2) {
		if (list instanceof VisualList vList)
			return vList.subList(i1, i2);
		return new ArrayList<>(list.subList(i1, i2));
	}
	
	private final List<Rectangle> columns = new ArrayList<>();
	
	double baseHeight;
	
	@Visual
	public VisualList() {
		super();
		ListWindow.add(this);
	}
	@Visual
	public VisualList(List<Integer> list) {
		super(list);
		for (Integer i : list)
			columns.add(getRectangle(i));
		ListWindow.add(this);
	}
	
	@Visual
	@Override
	public boolean add(Integer e) {
		if (!super.add(e)) return false;
		
		LOGGER.log.println("Adding int %d", e);
		
		columns.add(getRectangle(e));
		
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
			ListWindow.step();
		}
		
		removeRectangle(index);
		
		updateWindow();
		
		return res;
	}
	
	@Visual
	@Override
	public void add(int index, Integer element) {
		super.add(index, element);
		
		LOGGER.log.println("Adding %d at %d", element, index);
		
		columns.add(index, getRectangle(element));
		
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
			l.add(getRectangle(i));
		}
		columns.addAll(l);
		
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
			l.add(getRectangle(i));
		}
		columns.addAll(index, l);
		
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
			ListWindow.step();
		}
		
		for (int i = 0; i < columns.size(); i++)
			removeRectangle(i);
		
		updateWindow();
	}
	
	@Visual
	@Override
	public VisualList subList(int fromIndex, int toIndex) {
		Rectangle r;
		for (int i = fromIndex; i < toIndex; i++) {
			r = columns.remove(i);
			r.setHidden(true);
			ListWindow.recycle(r);
		}
		return new VisualList(super.subList(fromIndex, toIndex));
	}
	
	private Rectangle getRectangle(int e) {
		int offset = columnSpacing.get() + (columnWidth.get() + columnSpacing.get()) * columns.size();
		int columnHeight = max(e, minColumnHeight.get());
		Color c = Config.stepByStep.get()
				? addedColumnColor.get()
				: defaultColumnColor.get();
		int y = listHeight.get() - columnSpacing.get() - columnHeight;
		int width = columnWidth.get();
		
		Rectangle r = ListWindow.recycleRectangle();
		if (r == null) r = new Rectangle(offset, y, width, columnHeight, c);
		
		r.moveTo(offset, y);
		r.scaleTo(width, columnHeight);
		r.setHidden(false);
		
		return r;
	}
	
	private void removeRectangle(int index) {
		Rectangle r = columns.remove(index);
		r.setHidden(true);
		ListWindow.recycle(r);
	}
	
	/**
	 * Swaps two ints in the list, marking them in the window in {@link Config#swappedColumnColor}
	 */
	public void swap(int i1, int i2) {
		int v1 = super.get(i1);
		int v2 = super.get(i2);
		super.set(i1, v2);
		super.set(i2, v1);
		
		LOGGER.log.println("Swapping ints %d and %d", i1, i2);
		
		columns.get(i1).setColor(swappedColumnColor.get());
		columns.get(i2).setColor(swappedColumnColor.get());
		
		updateWindow();
	}
	
	void updateWindow() {
		if (isEmpty()) return;
		
		if (size() != columns.size())
			throw new IllegalStateException("An internal error occurred");
		
		updateColumns();
		
		ListWindow.step();
		columns.forEach(c -> c.setColor(defaultColumnColor.get()));
	}
	
	private void updateColumns() {
		LOGGER.debug.println("Updating columns");
		
		int offset = columnSpacing.get();
		int listHeight = Config.listHeight.get();
		List<Integer> scaled = scaledList();
		for (int i = 0; i < size(); i++) {
			int value = scaled.get(i);
			Rectangle r = columns.get(i);
			r.scaleTo(columnWidth.get(), value);
			r.moveTo(offset, baseHeight - value + listHeight);
			
			LOGGER.debug.println("Setting column %d to height %d at %d",
			              i, value, offset);
			
			offset += columnWidth.get() + columnSpacing.get();
		}
	}
	
	List<Integer> scaledList() {
		int min = Collections.min(this);
		int max = Math.min(Collections.max(this), listHeight.get());
		int minHeight = Math.max(min, minColumnHeight.get());
		
		return stream()
				.map(value -> minHeight
						+ (value - min) * (listHeight.get() - minHeight)
						/ (max - min))
				.toList();
	}
	
	int calculateWidth() {
		int w = columnSpacing.get();
		return w + (columnSpacing.get() + columnWidth.get()) * columns.size();
	}
	
	public boolean isOrdered() {
		List<Integer> ordered = new ArrayList<>(this);
		Collections.sort(ordered);
		boolean r = equals(ordered);
		
		LOGGER.log.println("List is%s ordered", r ? "" : " not");
		
		if (!r) return false;
		
		columns.forEach(c -> c.setColor(sortedColumnColor.get()));
		
		ListWindow.step();
		updateWindow();
		
		return true;
	}
	
	public void sort() {
		LOGGER.log.println("Sorting list");
		
		info.println(this);
		Collections.sort(this);
		info.println(this);
		
		updateColumns();
		isOrdered();
	}
}