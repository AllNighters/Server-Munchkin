package com.common;

import java.io.Serializable;

public class Message implements Serializable{
	public String type;
	public String[] values;
	
	public Message(String type, String[] values){
		this.type = type;
		this.values = values;
	}
	
	public String toString(){
		String value = "Type: " + type + ": ";
		for(int i = 0; i < values.length; i++){
			value = value + values[i] + "\n";
		}
		
		return value;
	}
}
