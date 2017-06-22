package com.comze_instancelabs.colormatch.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.comze_instancelabs.colormatch.patterns.PatternBase;

public class WeightedPatternMap {
	private ArrayList<WeightedPattern> patterns;
	private int count;
	
	public WeightedPatternMap() {
		patterns = new ArrayList<WeightedPattern>();
		count = 0;
	}
	
	public void add(WeightedPattern pattern) {
		count += pattern.weight;
		pattern.total = count;
		patterns.add(pattern);
	}
	
	public void remove(WeightedPattern pattern) {
		patterns.remove(pattern);
	}
	
	public List<WeightedPattern> getPatterns() {
		return Collections.unmodifiableList(patterns);
	}
	
	public PatternBase getRandom(Random random) {
		int val = random.nextInt(count);
		
		int index = Collections.binarySearch(patterns, new WeightedPattern(val));
		
		if(index < 0)
			index = (index + 1) * -1;
		
		return patterns.get(index).getPattern();
	}
	
	public static class WeightedPattern implements Comparable<WeightedPattern> {
		private int total;
		private final int weight;
		private final PatternBase pattern;
		private final String name;
		
		public WeightedPattern (int weight, String name, PatternBase pattern) {
			this.weight = weight;
			this.name = name;
			this.pattern = pattern;
		}
		
		private WeightedPattern(int total) {
			this.total = total;
			weight = 0;
			pattern = null;
			name = null;
		}
		
		public int getWeight() {
			return weight;
		}
		
		public String getPatternName() {
			return name;
		}
		
		public PatternBase getPattern() {
			return pattern;
		}
		
		@Override
		public int compareTo(WeightedPattern o) {
			return Integer.valueOf(total).compareTo(o.total);
		}
	}
}
