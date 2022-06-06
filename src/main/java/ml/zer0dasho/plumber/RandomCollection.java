package ml.zer0dasho.plumber;

import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * This collection allows you to retrieve items from it randomly.
 * Every item in the collection has a probability associated with it that represents how likely it is to be returned next.
 * 
 * @author ronen
 * @see https://stackoverflow.com/questions/6409652/random-weighted-selection-in-java
 */
public class RandomCollection<E> {
	
  private final NavigableMap<Double, E> map = new TreeMap<Double, E>();
  private double total = 0;

  public void add(double weight, E result) {
    if (weight <= 0 || map.containsValue(result))
      return;
    
    total += weight;
    map.put(total, result);
  }

  public E next() {
    double value = ThreadLocalRandom.current().nextDouble() * total;
    return map.ceilingEntry(value).getValue();
  }
  
}