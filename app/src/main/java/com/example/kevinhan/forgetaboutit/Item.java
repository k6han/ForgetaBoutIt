package com.example.kevinhan.forgetaboutit;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This class represents an Item that can be detected by
 * the device. It stores the RF-id and name of the item.
 */
public class Item implements Parcelable {

	//the id that the rf-id sensors detect
	private String id;

	//user provided name for the item corresponding the id
	private String name;

	/**
	 * Constructor that only takes the id
	 * Sets name to null
	 * @param  id Rf-id that the sensors identify
	 * @return    newly created object
	 */
	public Item(String id) {
		//set name to null if not provided
		this(id,null);
	}

	/**
	 * Constructor that takes id and name
	 * @param  id   Rf-id that the sensors identify
	 * @param  name what the user wants the object to be called
	 * @return      newly created object
	 */
	public Item(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public Item(Parcel source){
		id = source.readString();
		name = source.readString();
	}

	/**
	 * retrieves the id of this item
	 * @return the id attribute of this item
	 */
	public String getId() {
		return id;
	}

	/**
	 * retrieves the name of this item
	 * @return the name attribute of this item
	 */
	public String getName() {
		return name;
	}

	/**
	 * changes the id of this item
	 * @param id the new id that this item should have
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * renames this item
	 * @param name the new name for this item
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * overriden equals method from Object class
	 * true if same id, false otherwise
	 * @param  other object being compared to
	 * @return       true if id matches; false otherwise
	 */
	@Override
	public boolean equals(Object other) {
		if (other == null)
			return false;
		if (!(other instanceof Item))
			return false;
		//there should not be two Item objects with the same id
		if(id != null)
		return (this.id.equals(((Item)other).id));

		else return false;
	}

	//needed for hashing
	/**
	 * hash function for this class
	 * @return hash code for the String id, using String class
	 */
	@Override
	public int hashCode() {
		if(id != null)
		return id.hashCode();

		else return -1;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(name);
	}

	public static final Parcelable.Creator<Item>CREATOR
			= new Parcelable.Creator<Item>() {
		@Override
		public Item createFromParcel(Parcel source) {
			return new Item(source);
		}

		@Override
		public Item[] newArray(int size) {
			return new Item[size];
		}
	};
}