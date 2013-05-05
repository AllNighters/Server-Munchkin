package com.server.main;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
	public Socket socket;
	
//	public Scanner in;
//	public PrintWriter out;
	
	public ObjectInputStream in;
	public ObjectOutputStream out;

	public Client(Socket theSocket) throws IOException {
		socket = theSocket;
		
		out = new ObjectOutputStream(socket.getOutputStream());
		//out.flush();
		in = new ObjectInputStream(socket.getInputStream());
		
		
		
		
	}
	


	
}
