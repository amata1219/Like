package amata1219.like.command.library;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class ArgumentQueue {
	
	private final Queue<String> args;
	
	public ArgumentQueue(String[] args){
		this.args = new LinkedList<>(Arrays.asList(args));
	}
	
	public String poll(){
		return args.poll();
	}
	
	/*
	 * nextLong().flatMap(
	 *   id -> 
	 * )
	 * 
	 */

}
