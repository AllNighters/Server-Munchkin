package com.server.database.card;

import com.server.database.Card;
import com.server.main.player.RACE;

public class RaceCard extends Card {
	RACE type;
	
	public RaceCard(){
		type = null;
	}
	
	public void setType(String name){
		if(name.equals("dwarf")){
			type = RACE.Dwarf;
		}else if(name.equals("elf")){
			type = RACE.Elf;
		}else if(name.equals("halfling")){
			type = RACE.Halfling;
		}
	}
	
	
	public RACE getType(){
		return type;
	}
}
