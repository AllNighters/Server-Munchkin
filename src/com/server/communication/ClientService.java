package com.server.communication;

import java.io.IOException;

import com.common.Message;
import com.server.main.Client;

public class ClientService implements Runnable {

	int playerNum; // Signifies which player this client service represents in
					// the game

	MessageListener listener;

	Client client;
	public boolean isActive;

	public ClientService(Client client, int playerNum) {
		isActive = true;
		this.client = client;
		this.playerNum = playerNum;
	}

	/**
	 * This should only be called when a player is removed from the game. This
	 * will be call to shift all player numbers down, following the one that has
	 * been removed.
	 * 
	 * @param num
	 *            The new player number the client service will represent
	 */
	public void setPlayerNum(int num) {
		playerNum = num;
	}

	public void setOnMessageReceived(MessageListener a) {
		listener = a;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		doService();
	}

	public void doService() {

		while (isActive) {

			getInput();

			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		try {
			client.socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private synchronized void getInput() {
		try {

			Message message = (Message) client.in.readObject();
			System.out.println(isActive);
			if (isActive) {
				System.out.println(message.type);
				listener.onMessageReceived(playerNum, message);
			}

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			isActive = false;
			//System.out
				//	.println("client Thread has run into an exception. will stop now.");
			System.out.println("java.io.EOFException: ignore it for now. YOU DUN GOOFED!");
			System.out.println("Removing player " + playerNum + " from game");
		}

	}

}
