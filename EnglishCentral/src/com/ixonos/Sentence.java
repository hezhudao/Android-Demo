/**
 * 
 */
package com.ixonos;

/**
 * @author jashewe
 * 
 */
public class Sentence {

	private long fromTime;
	private long toTime;
	private String content;

	/**
	 * @return the fromTime
	 */
	public long getFromTime() {
		return fromTime;
	}

	/**
	 * @param fromTime
	 *            the fromTime to set
	 */
	public void setFromTime(long fromTime) {
		this.fromTime = fromTime;
	}

	/**
	 * @return the toTime
	 */
	public long getToTime() {
		return toTime;
	}

	/**
	 * @param toTime
	 *            the toTime to set
	 */
	public void setToTime(long toTime) {
		this.toTime = toTime;
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param content
	 *            the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		return "{" + fromTime + "(" + content + ")" + toTime + "}";
	}

	public boolean isInTime(long time) {
		return time >= fromTime && time < toTime;
	}
}
