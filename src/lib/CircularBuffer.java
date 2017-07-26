package lib;

import java.util.Arrays;

public class CircularBuffer {
	private final String[] data;
	private int head, tail, count;

	public CircularBuffer(int size) {
		data = new String[size];
		head = 0;
		tail = 0;
		count = 0;
	}

	public synchronized void add(String value) {
		data[tail] = value;
		
		if (head == tail && count > 0)
			head = (head + 1) % data.length;
		else
			count++;
		
		tail = (tail + 1) % data.length;
	}

	public synchronized String remove() {
		String out = data[head];
		data[head] = null;
		count--;
		head = (head + 1) % data.length;
		
		return out;
	}
	
	public synchronized int getCount() {
		return count;
	}
	
	public synchronized String peek(){
		return data[head];
	}

	public synchronized String toString() {
		return Arrays.toString(data);
	}
	
	public synchronized String[] orderedArray(){
		String[] out = new String[count];
		if (count == 0) return out;
		if (tail > head) {
			System.arraycopy(data, head, out, 0, count);
		} else {
			System.arraycopy(data, head, out, 0, out.length - head);
			System.arraycopy(data, 0, out, out.length - head, tail);
		}
		return out;
	}
	
	public synchronized void printTimes(){
		for(String mess : data){
			System.out.print(mess.split(" ")[1] + "\t");
		}
		System.out.println();
	}
	
	public synchronized String stackArrayElements() {
		if (count == 0) return "";
		String[] sortedData = orderedArray();
		
		// use a StringBuilder (with an estimated initial capacity to avoid reallocations)
		// String.join should be equivalent except it uses the default initial capacity of 16
		// (needs to be profiled)
		StringBuilder out = new StringBuilder(count * sortedData[0].length() * 2);
		out.append(sortedData[0]);
		for (int i = 1; i < sortedData.length; i++) {
			out.append('\n').append(sortedData[i]);
		}
		return out.toString();
	}
	
	public synchronized void removeOldMessages(double timeStampSecs){
		//Iterate over every message
		for(int i = 0; i < count; i++){
			if(peek() == null)
				return;
			
			//Remove old messages
			if(timeInSecs(peek().split(" ")[1]) <= timeStampSecs){
				remove();
			}else{
				//In chronological order, so we can stop searching after we find the first new message
				return;
			}
		}
	}
	
	private static double timeInSecs(String time) {
		return Long.parseLong(time) / 1000.0;
	}
}