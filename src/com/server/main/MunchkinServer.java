package com.server.main;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.common.Message;
import com.server.main.player.Player;

public class MunchkinServer {

	public static void main(String[] args) throws IOException {
		int PORT = 8888;

		String username = "noname";
		String id = "-1";

		GameRoom room1 = new GameRoom(0);
		Thread game1 = new Thread(room1);
		game1.start();

		ServerSocket ss = new ServerSocket(PORT);
		System.out.println("Waiting for Clients to connect");

		// Infinite Game Loop
		while(true){
		//while (room1.getGame().getState().equals(GameState.Lobby)) {
			System.out.println("accept()");
			Socket s = ss.accept();
			System.out.println("Socket connected");
			Client newClient = new Client(s);

			// Gets the player info from the client right after connection
			Message msg;
			try {
				msg = ((Message) newClient.in.readObject());
				username = msg.values[0];
				id = msg.values[1];
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}

			System.out.println("Player name is " + username);
			Player a = new Player(username, id, newClient);

			room1.addPlayer(a);
			System.out.println("Client Connected");

		}

	}

}
