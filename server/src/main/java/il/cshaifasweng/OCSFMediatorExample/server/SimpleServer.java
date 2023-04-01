package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.SubscribedClient;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SimpleServer extends AbstractServer {
	private static ArrayList<SubscribedClient> SubscribersList = new ArrayList<>();

	public SimpleServer(int port) {
		super(port);
		
	}

	@Override
	protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
		Message message = (Message) msg;
		String request = message.getMessage();
		try {
			//we got an empty message, so we will send back an error message with the error details.
			if (request.isBlank()){
				message.setMessage("Error! we got an empty message");
				client.sendToClient(message);
			}
			//we got a request to change submitters IDs with the updated IDs at the end of the string, so we save
			// the IDs at data field in Message entity and send back to all subscribed clients a request to update
			//their IDs text fields. An example of use of observer design pattern.
			//message format: "change submitters IDs: 123456789, 987654321"
			else if(request.startsWith("change submitters IDs:")){
				message.setData(request.substring(23));
				message.setMessage("update submitters IDs");
				sendToAllClients(message);
			}
			//we got a request to add a new client as a subscriber.
			else if (request.equals("add client")){
				SubscribedClient connection = new SubscribedClient(client);
				SubscribersList.add(connection);
				message.setMessage("client added successfully");
				client.sendToClient(message);
			}
			//we got a message from client requesting to echo Hello, so we will send back to client Hello world!
			else if(request.startsWith("echo Hello")){
				message.setMessage("Hello World!");
				client.sendToClient(message);
			}
			else if(request.startsWith("send Submitters IDs")){
				//we got a message from client requesting the submitters' ID, so we will send back to client the submitters IDs'
				message.setMessage("318155447, 302576269");
				client.sendToClient(message);
			}
			else if (request.startsWith("send Submitters")){
				//we got a message from client requesting the submitters' names, so we will send back to client the submitters names'
				message.setMessage("Omer Artzi, Edan Peled Donenfeld");
				client.sendToClient(message);
			}
			else if (request.equals("what’s the time?")) {
				//we got a message from client requesting the time in HH:mm:ss format, so we will send back to client the time in that format
				SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
				Date date = new Date();
				message.setMessage(sdf.format(date.getTime()));
				client.sendToClient(message);
			}
			else if (request.startsWith("multiply")){
				//we got a message from client requesting to multiply 2 numbers, so we will send back to client the product
				String[] numbers = request.substring(8).split("(?<=\\*)|(?=\\*)");
				try {
					if(!(numbers[1].equals("*")))
					{
						throw new Exception();
					}
					message.setMessage(Integer.toString((Integer.parseInt(numbers[0].trim()) * Integer.parseInt(numbers[2].trim()))));
				}
				catch (Exception e)
				{
					message.setMessage("Illegal Input");
				}
				client.sendToClient(message);
							}else{
				//we got a message from client we couldn't identify, so we will send back to all clients the message
				message.setMessage(request);
				sendToAllClients(message);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public void sendToAllClients(Message message) {
		try {
			for (SubscribedClient SubscribedClient : SubscribersList) {
				SubscribedClient.getClient().sendToClient(message);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

}
