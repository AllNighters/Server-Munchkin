package com.server.communication;

import com.common.Message;

public interface MessageListener {

	public void onMessageReceived(int playerNumber, Message message);
}
