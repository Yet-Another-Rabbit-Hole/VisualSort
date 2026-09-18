package net.YaRh.VisualSort;

import net.YaRh.ConvConf.Attribute;
import net.YaRh.ConvConf.Switch;

import java.awt.*;

/**
 * Holds configuration values that influence how the lists look and how they behave
 */
public class Config {
	
	/**
	 * Enables step-by-step walking through actions, highlighted by color
	 */
	public static final Switch stepByStep = new Switch(true, ListWindow::setStepByStep);
	
	/**
	 * The delay between automatic steps, overrides manual stepping
	 * <p>
	 * When set to {@code null}, there will be no delay
	 * <p>
	 * Even a delay of 0 will introduce delay
	 */
	public static final Attribute<Double> stepDelay = new Attribute<>();
	
	public static final Attribute<Color> backgroundColor = new Attribute<>(Color.GRAY, ListWindow::setBGColor);
	public static final Attribute<Color> defaultColumnColor = new Attribute<>(Color.BLUE);
	public static final Attribute<Color> removedColumnColor = new Attribute<>(Color.RED);
	public static final Attribute<Color> addedColumnColor = new Attribute<>(Color.GREEN);
	public static final Attribute<Color> swappedColumnColor = new Attribute<>(Color.CYAN);
	public static final Attribute<Color> alteredColumnColor = new Attribute<>(Color.ORANGE);
	public static final Attribute<Color> sortedColumnColor = new Attribute<>(Color.GREEN);
	
	public static final Attribute<Integer> columnSpacing = new Attribute<>(5);
	public static final Attribute<Integer> columnWidth = new Attribute<>(10);
	public static final Attribute<Integer> minColumnHeight = new Attribute<>(5);
	public static final Attribute<Integer> minWindowWidth = new Attribute<>(120);
	/**
	 * The height all the lists have, values are scaled accordingly
	 */
	public static final Attribute<Integer> listHeight = new Attribute<>(90);
	/**
	 * The vertical spacing between lists
	 */
	public static final Attribute<Integer> listSpacing = new Attribute<>(5);
	
}