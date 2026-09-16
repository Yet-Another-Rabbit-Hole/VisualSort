package net.YaRh.VisualSort;

import net.YaRh.ConvConf.Attribute;
import net.YaRh.ConvConf.Switch;

import java.awt.*;

public class Config {
	
	public static final Switch stepByStep = new Switch(true, VisualList::setStepByStep);
	
	public static final Attribute<Double> stepDelay = new Attribute<Double>(null).nullable();
	
	public static final Attribute<Color> backgroundColor = new Attribute<>(Color.GRAY, VisualList::setBGColor);
	public static final Attribute<Color> defaultColumnColor = new Attribute<>(Color.BLUE);
	public static final Attribute<Color> removedColumnColor = new Attribute<>(Color.RED);
	public static final Attribute<Color> addedColumnColor = new Attribute<>(Color.GREEN);
	public static final Attribute<Color> swappedColumnColor = new Attribute<>(Color.CYAN);
	public static final Attribute<Color> alteredColumnColor = new Attribute<>(Color.ORANGE);
	public static final Attribute<Color> sortedColumnColor = new Attribute<>(Color.GREEN);
	
	public static final Attribute<Integer> columnSpacing = new Attribute<>(5);
	public static final Attribute<Integer> columnWidth = new Attribute<>(10);
	public static final Attribute<Integer> minColumnHeight = new Attribute<>(5);
	public static final Attribute<Integer> minWindowHeight = new Attribute<>(80);
	public static final Attribute<Integer> minWindowWidth = new Attribute<>(120);
	
}