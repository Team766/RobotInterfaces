package com.team766.tests;

import java.util.Arrays;

import junit.framework.TestCase;
import lib.CircularBuffer;

import org.junit.Test;

public class CircularBufferTest extends TestCase{
	@Test
	public void testAddingToBuffer() throws Exception {
		CircularBuffer buffer = new CircularBuffer(10);
		
		//Fill with [0,1,2...9]
		for(int i = 0; i < 10; i++){
			buffer.add(""+i);
		}
		
		System.out.println(buffer);
		
		//Check it looks like expected
		for(int i = 0; i < 10; i++){
			assertTrue(buffer.remove().equals(""+i));
		}
	}
	
	@Test
	public void testOverwritingBuffer() throws Exception {
		CircularBuffer buffer = new CircularBuffer(10);
		
		//Fill with [0,1,2,...14]
		for(int i = 0; i < 15; i++){
			buffer.add(""+i);
		}
		
		System.out.println(buffer);
		
		//Should only get [5,6,7,8,9,10,11,12,13,14]
		for(int i = 5; i < 15; i++){
			assertTrue(buffer.remove().equals(""+i));
		}
	}
	
	@Test
	public void testAddRemove() throws Exception {
		CircularBuffer buffer = new CircularBuffer(10);
	
		for(int i = 0; i < 100; i++){
			buffer.add(""+i);
			assertTrue(buffer.remove().equals(""+i));
		}
	}
	
	@Test
	public void testCheckOrderedArray() throws Exception {
		CircularBuffer buffer = new CircularBuffer(10);
		
		//Fill with [0,1,2,...14]
		for(int i = 0; i < 766; i++){
			buffer.add(""+i);
		}
		
		//Check outputed array is in order, not weird stored order
//		System.out.println("Actual: " + buffer);
		String[] output = buffer.orderedArray();
//		System.out.println("Sorted: " + Arrays.toString(output));
		int last = -1;
		for(String num : output){
			int curr = Integer.parseInt(num);
			assertTrue(curr > last);
			last = curr;
		}
	}
	
	@Test
	public void testEmptyList() throws Exception {
		CircularBuffer buffer = new CircularBuffer(3);
		buffer.add("0");
		buffer.add("1");
		
		//Check outputed array is in order, not weird stored order
//		System.out.println("Actual: " + buffer);
		String[] output = buffer.orderedArray();
//		System.out.println("Sorted: " + Arrays.toString(output));
		int last = -1;
		for(String num : output){
			if(num == null)
				continue;
			
			int curr = Integer.parseInt(num);
			assertTrue(curr > last);
			last = curr;
		}
	}
	
	@Test
	public void testEmptyHeadPastTail() throws Exception {
		CircularBuffer buffer = new CircularBuffer(5);
		buffer.add("0");
		buffer.add("1");
		buffer.add("2");
		buffer.add("3");
		buffer.add("4");
		buffer.add("5");
		buffer.add("6");
		
		buffer.remove();
		
		//Should have:
		//[5,6, ,3,4]
		//     t h
		
		//Check outputed array is in order, not weird stored order
//		System.out.println("Actual: " + buffer);
		String[] output = buffer.orderedArray();
//		System.out.println("Sorted: " + Arrays.toString(output));
		int last = -1;
		for(String num : output){
			if(num == null)
				continue;
			
			int curr = Integer.parseInt(num);
			assertTrue(curr > last);
			last = curr;
		}
	}
}
