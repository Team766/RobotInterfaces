package lib;

import java.util.Arrays;
import java.util.Iterator;

public class CircularBuffer implements Iterable<String> {
	private String data[];
	private int head, tail, count;

	public CircularBuffer(int size) {
		data = new String[size];
		head = 0;
		tail = 0;
		count = 0;
	}

	public synchronized void add(String value) {
		data[tail] = value;
		count++;
		
		if(head == tail && count > 1)
			head = (head + 1) % data.length;
	
		tail = (tail + 1) % data.length;
	}

	public synchronized String remove() {
		String out = data[head];
		data[head] = null;
		count--;
		head = (head + 1) % data.length;
		
		return out;
	}
	
	public synchronized String peek(){
		return data[head];
	}

	public String toString() {
		return Arrays.toString(data);
	}
	
	public String[] orderedArray(){
		String[] out = new String[data.length];
		
		if(tail > head){
			int x = tail;
			tail = head;
			head = x;
		}
		
		for(int i = head; i < out.length; i++){
			out[i - head] = data[i];
		}
		for(int i = 0; i < tail; i++){
			out[out.length - head + i] = data[i];
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
		String[] sortedData = orderedArray();
		String out = "";
		for(int i = 0; i < sortedData.length - 1; i++) {
			if(sortedData[i] != null)
				out += sortedData[i] + "\n";
		}
		return out + sortedData[sortedData.length - 1];
	}
	
	public synchronized void removeOldMessages(double timeStampSecs){
		//Iterate over every message
		for(int i = 0; i < data.length; i++){
			if(peek() == null)
				continue;
						
			//Remove old messages
			if(timeInSecs(peek().split(" ")[1]) <= timeStampSecs){
				remove();
			}else{
				//In chronological order, so we can stop searching after we find the first new message
				return;
			}
		}
	}
	
	private long timeInSecs(String time){
		return Long.parseLong(time) / 1000l;
	}

	@Override
	public Iterator<String> iterator() {
		return new Iterator<String>() {
			int position = head;
			boolean done = count > 0;

			@Override
			public boolean hasNext() {
				return !done;
			}

			@Override
			public String next() {
				if (position == tail) {
					done = true;
				}
				return data[position++];
			}
		};
	}
}