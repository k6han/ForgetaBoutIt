package com.example.kevinhan.forgetaboutit;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;
import java.util.List;

/**
 * represents a single event or class
 * and the corresponding required items
 */
public class Event implements Parcelable {

	//set of all items needed for this event
	private Set<Item> items;

	//time of day when this event starts
	//represented as 24 hour clock
	private int time;
	private String name;

	/**
	 * Constructor for Event class
	 * @param  time the time of the day this event occurs
	 * @return      newly created Event object
	 */
	public Event(int time, String name) {
		items = new HashSet<Item>();
		this.time = time;
		this.name = name;
	}

	public Event(Parcel source){
		items = new HashSet<Item>(Arrays.asList(source.createTypedArray(com.example.kevinhan.forgetaboutit.Item.CREATOR)));
		time = source.readInt();
		name = source.readString();
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

	public String getName() { return name; }

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

	public String getHeader() {
		String result = "";
		int hours = time / 100;
		boolean am = true;
		if (hours == 0) {
			result += "12";
			am = true;
		}
		else if (1 <= hours && hours <= 11) {
			result += hours;
			am = true;
		}
		else if (hours == 12) {
			result += "12";
			am = false;
		}
		else {
			result += (hours - 12);
			am = false;
		}
		result += ":" + time % 100;
		if (am) {
			result += " am";
		}
		else {
			result += " pm";
		}
		result += " - ";
		result += name;
		return result;
	}

	public String getItemsFormat() {
		String result = "Items: ";
		if (items.isEmpty()) {
			result += "none";
			return result;
		}
		result += items.toString().substring(1);
		result = result.substring(0,result.length()-1);
		return result;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		Item[] itemList = new Item[items.size()];
		items.toArray(itemList);
		dest.writeTypedArray(itemList, 0);
		dest.writeInt(time);
		dest.writeString(name);
	}

	public static final Parcelable.Creator<Event>CREATOR
			= new Parcelable.Creator<Event>() {
		@Override
		public Event createFromParcel(Parcel source) {
			return new Event(source);
		}

		@Override
		public Event[] newArray(int size) {
			return new Event[size];
		}
	};
}