package net.YaRh.VisualSort;

import net.YaRh.CheapLog.logging.Logger;
import sas.Rectangle;

import java.awt.*;
import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static net.YaRh.VisualSort.Config.*;

/**
 * Displays its contents as columns in a window
 */
public class VisualList extends ArrayList<Integer> implements AutoCloseable {
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
	
	public static List<Integer> newFromList(List<Integer> list, int i1, int i2) {
		if (list instanceof VisualList vList)
			return new VisualList(vList.subList(i1, i2));
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
		super(new ArrayList<>(list));
		for (Integer i : list)
			columns.add(getRectangle(i));
		
		ListWindow.add(this);
		resetColumnColors();
	}
	
	@Visual
	@Override
	public boolean add(Integer e) {
		boolean res = super.add(e);
		
		LOGGER.log.println("Adding int %d", e);
		
		columns.add(getRectangle(e));
		
		setColumnColor(columns.size() - 1, addedColumnColor.get());
		
		ListWindow.updateWindow();
		ListWindow.step();
		resetColumnColors();
		
		return res;
	}
	
	@Visual
	@Override
	public Integer remove(int index) {
		int res = super.remove(index);
		
		LOGGER.log.println("Removing int at %d", index);
		
		setColumnColor(index, removedColumnColor.get());
		ListWindow.step();
		
		removeRectangle(index);
		
		ListWindow.updateWindow();
		resetColumnColors();
		
		return res;
	}
	
	@Visual
	@Override
	public void add(int index, Integer element) {
		super.add(index, element);
		
		LOGGER.log.println("Adding %d at %d", element, index);
		
		columns.add(index, getRectangle(element));
		
		ListWindow.updateWindow();
		ListWindow.step();
		resetColumnColors();
	}
	
	@Visual
	@Override
	public Integer set(int index, Integer element) {
		int res = super.set(index, element);
		
		LOGGER.log.println("Setting value at %d to %d", index, element);
		
		setColumnColor(index, alteredColumnColor.get());
		
		updateColumns();
		ListWindow.step();
		resetColumnColors();
		
		return res;
	}
	
	@Visual
	@Override
	public boolean addAll(Collection<? extends Integer> c) {
		boolean res = super.addAll(c);
		
		LOGGER.log.println("Adding list of %d items", c.size());
		
		List<Rectangle> l = new ArrayList<>();
		for (Integer i : c)
			l.add(getRectangle(i));
		columns.addAll(l);
		
		ListWindow.updateWindow();
		ListWindow.step();
		resetColumnColors();
		
		return res;
	}
	
	@Visual
	@Override
	public boolean addAll(int index, Collection<? extends Integer> c) {
		boolean res = super.addAll(index, c);
		
		LOGGER.log.println("Adding list of %d items at index %d", c.size(), index);
		
		List<Rectangle> l = new ArrayList<>();
		for (Integer i : c) {
			l.add(getRectangle(i));
		}
		columns.addAll(index, l);
		
		ListWindow.updateWindow();
		ListWindow.step();
		resetColumnColors();
		
		return res;
	}
	
	@Visual
	@Override
	public void clear() {
		super.clear();
		
		LOGGER.log.println("Clearing list");
		
		setColumnColor(removedColumnColor.get());
		ListWindow.step();
		
		clearRectangles();
		
		ListWindow.updateWindow();
	}
	
	@Visual
	@Override
	protected void removeRange(int fromIndex, int toIndex) {
		Color color = removedColumnColor.get();
		for (int i = fromIndex; i <= toIndex; i++)
			setColumnColor(i, color);
		
		ListWindow.step();
		
		for (int i = fromIndex; i <= toIndex; i++)
			removeRectangle(i);
		
		super.removeRange(fromIndex, toIndex);
		
		ListWindow.updateWindow();
		resetColumnColors();
	}
	
	@Visual
	@Override
	public List<Integer> subList(int fromIndex, int toIndex) {
		return new VisualSubList(this, fromIndex, toIndex - 1);
	}
	
	@Override
	public void close() {
		LOGGER.log.println("Closing list");
		
		setColumnColor(removedColumnColor.get());
		ListWindow.step();
		
		clearRectangles();
		
		ListWindow.remove(this);
	}
	
	private Rectangle getRectangle(int e) {
		int offset = columnSpacing.get() + (columnWidth.get() + columnSpacing.get()) * columns.size();
		int columnHeight = Math.max(e, minColumnHeight.get());
		Color c = Config.stepByStep.get()
				? addedColumnColor.get()
				: defaultColumnColor.get();
		int y = listHeight.get() - columnSpacing.get() - columnHeight;
		int width = columnWidth.get();
		
		Rectangle r = ListWindow.recycleRectangle();
		if (r == null) r = new Rectangle(offset, y, width, columnHeight, c);
		else {
			r.moveTo(offset, y);
			r.scaleTo(width, columnHeight);
			r.setHidden(false);
			r.setColor(c);
		}
		
		return r;
	}
	
	private void removeRectangle(int index) {
		ListWindow.recycle(columns.remove(index));
	}
	
	private void clearRectangles() {
		columns.forEach(ListWindow::recycle);
		columns.clear();
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
		
		setColumnColor(i1, swappedColumnColor.get());
		setColumnColor(i2, swappedColumnColor.get());
		
		updateColumns();
		ListWindow.step();
		resetColumnColors();
	}
	
	void windowUpdate() {
		if (size() != columns.size())
			ListWindow.illegalState(new InvalidObjectException("List not synced with visuals"));
		
		updateColumns();
	}
	
	void resetColumnColors() {
		Color color = defaultColumnColor.get();
		columns.forEach(r -> r.setColor(color));
	}
	
	private void setColumnColor(Color color) {
		if (Config.stepByStep.get()) columns.forEach(r -> r.setColor(color));
	}
	
	private void setColumnColor(int index, Color color) {
		if (Config.stepByStep.get()) columns.get(index).setColor(color);
	}
	
	private void updateColumns() {
		if (isEmpty()) return;
		
		LOGGER.debug.println("Updating columns");
		
		int offset = columnSpacing.get();
		int listHeight = Config.listHeight.get();
		List<Integer> scaled = scaledList();
		
		int value;
		Rectangle r;
		for (int i = 0; i < size(); i++) {
			value = scaled.get(i);
			r = columns.get(i);
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
						/ (max - min)
				)
				.toList();
	}
	
	int calculateWidth() {
		int spacing = columnSpacing.get();
		return spacing + (spacing + columnWidth.get()) * columns.size();
	}
	
	public boolean isOrdered() {
		List<Integer> ordered = new ArrayList<>(this);
		Collections.sort(ordered);
		boolean r = equals(ordered);
		
		LOGGER.log.println("List is%s ordered", r ? "" : " not");
		
		if (!r) return false;
		
		setColumnColor(sortedColumnColor.get());
		
		ListWindow.step();
		resetColumnColors();
		
		return true;
	}
	
	public void sort() {
		LOGGER.log.println("Sorting list");
		
		Collections.sort(this);
		
		setColumnColor(sortedColumnColor.get());
		updateColumns();
		
		ListWindow.step();
		resetColumnColors();
	}
	
	private static class VisualSubList extends VisualList {
		
		private final VisualList root;
		private final VisualSubList parent;
		private final int offset;
		private final int length;
		
		public VisualSubList(VisualList root, int offset, int length) {
			this.root = root;
			this.parent = null;
			this.offset = offset;
			this.length = length;
		}
		public VisualSubList(VisualSubList parent, int offset, int length) {
			this.root = parent.root;
			this.parent = parent;
			this.offset = parent.offset + offset;
			this.length = length;
		}
		
		@Override
		public boolean add(Integer e) {
			root.add(offset + length, e);
			return true;
		}
		
		@Override
		public Integer remove(int index) {
			return root.remove(offset + index);
		}
		
		@Override
		public void add(int index, Integer element) {
			root.add(offset + index, element);
		}
		
		@Override
		public Integer set(int index, Integer element) {
			return root.set(offset + index, element);
		}
		
		@Override
		public boolean addAll(Collection<? extends Integer> c) {
			return root.addAll(offset + length, c);
		}
		
		@Override
		public boolean addAll(int index, Collection<? extends Integer> c) {
			return root.addAll(offset + index, c);
		}
		
		@Override
		public void clear() {
			root.removeRange(offset, offset + length);
		}
		
		@Override
		public void swap(int i1, int i2) {
			root.swap(offset + i1, offset + i2);
		}
	}
}