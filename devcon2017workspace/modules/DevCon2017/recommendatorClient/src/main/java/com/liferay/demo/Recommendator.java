package com.liferay.demo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

public class Recommendator {

	static Double dist(Map<String, Integer> a, Map<String, Integer> b) {
		Set<String> inters = new HashSet<String>();
		for (String k : a.keySet())
			if (b.containsKey(k))
				inters.add(k);
		if (inters.size() > 0)
			return 1 / (1 + inters.stream().map(k -> Math.pow(a.get(k) - b.get(k), 2)).mapToDouble(i -> i).sum());
		else
			return 0.0;
	}

	/***
	 * The recommendation function itself
	 * 
	 * for each of the other users get the distance with my user for each item
	 * that other user rated (that my user hasn't), accumulate: ---rating x
	 * distance ---distance
	 * 
	 * the estimated rating for each item = (accumulated rating x
	 * distance)/(acumulated distance)
	 * 
	 */
	public static Map<String, Double> recommend(Long user, Map<Long, Map<String, Integer>> r) {
		Map<String, Double[]> totals = new HashMap<String, Double[]>();
		Map<String, Integer> userRatings = r.remove(user);
		for (Entry<Long, Map<String, Integer>> m : r.entrySet()) {
			Double d = dist(userRatings, m.getValue());
			if (d > 0)
				for (Entry<String, Integer> rat : m.getValue().entrySet()) {
					if (!userRatings.containsKey(rat.getKey())) {
						if (!totals.containsKey(rat.getKey()))
							totals.put(rat.getKey(), new Double[] { 0.0, 0.0 });
						Double t = totals.get(rat.getKey())[0];
						Double td = totals.get(rat.getKey())[1];
						totals.put(rat.getKey(), new Double[] { t + d * rat.getValue(), td + d });
					}
				}
		}
		Map<String, Double> r0 = totals.entrySet().stream()
				.map((e) -> (Map.Entry<String, Double>) new Map.Entry<String, Double>() {
					Double value = 0.0;

					public String getKey() {
						return e.getKey();
					}

					public Double getValue() {
						return value;
					}

					public Double setValue(Double value) {
						value = e.getValue()[1] / e.getValue()[0];
						return value;
					}
				}).collect(Collectors.toMap(Map.Entry::getKey, i -> i.getValue()));
		return r0;
		/*
		 * TODO: sort the results and return a sorted list instead of a map Not
		 * really necessary if we want to show them all
		 */
	}

}
