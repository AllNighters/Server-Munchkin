package com.server.database.card;

import com.server.database.Card;
import com.server.main.player.CLASS;

public class ClassCard extends Card {
	CLASS type;
	
	public ClassCard(){
		type = null;
	}
	
	public void setType(String name){
		if(name.equals("cleric")){
			type = CLASS.Cleric;
		}else if(name.equals("thief")){
			type = CLASS.Thief;
		}else if(name.equals("warrior")){
			type = CLASS.Warrior;
		}
		else if(name.equals("wizard")){
			type = CLASS.Wizard;
		}
	}
	
	public CLASS getType(){
		return type;
	}
}
