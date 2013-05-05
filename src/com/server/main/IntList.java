package com.server.main;

import java.util.ArrayList;

public class IntList extends ArrayList<Integer>{

	@Override
	public boolean add(Integer a){
		
		if(this.size() == 0){
			return super.add(a);
		}
		
		for(int i = 0; i < this.size(); i++){
			
			if(this.get(i) == a.intValue()){
				return false;
			}
			
			if(this.get(i) > a.intValue()){
				super.add(i, a);
				return true;
			}else if(i == this.size() - 1){
				return super.add(a);
			}
		}
		return false;
	}
}
