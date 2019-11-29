package com.example.kevinhan.forgetaboutit;

import java.util.Set;
import java.util.HashSet;

/**
 * represents a single event or class
 * and the corresponding required items
 */
public class Event {

	//set of all items needed for this event
	private Set<Item> items;

	//time of day when this event starts
	//represented as 24 hour clock
	private int time;

	/**
	 * Constructor for Event class
	 * @param  time the time of the day this event occurs
	 * @return      newly created Event object
	 */
	public Event(int time) {
		items = new HashSet<Item>();
		this.time = time;
	}

	/**
	 * add an item to this event
	 * @param  it item to add
	 * @return    whether the item was successfully added
	 */
	public boolean addItem(Item it) {
		return items.add(it);
	}

	/**
	 * removes an item from this event
	 * @param  it the item to remove
	 * @return    whether the item was successfully removed
	 */
	public boolean remove(Item it) {
		return items.remove(it);
	}

	/**
	 * get the set of items
	 * @return set of items for this events
	 */
	public Set<Item> getItems() {
		return items;
	}

	/**
	 * get the time of this event
	 * @return time of this event
	 */
	public int getTime() {
		return time;
	}

	/**
	 * change the time of this event
	 * @param time new time of this event
	 */
	public void setTime(int time) {
		this.time = time;
	}

	/**
	 * get a set of items that are missing
	 * @param  items items already present
	 * @return       items that are missing
	 */
	public Set<Item> getMissingItems(Set<Item> items) {
		Set<Item> result = new HashSet<Item>();
		for (Item item : this.items) {
			if (!items.contains(item)) {
				result.add(item);
			}
		}
		return result;
	}
}