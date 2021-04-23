package ru.yandex.incoming34.Server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ClientHandler  extends Thread{
	@Autowired
	private Server server;
	// исходящее сообщение
	private PrintWriter outMessage;
	// входящее собщение
	private Scanner inMessage;
	private static final String HOST = "localhost";
	private static final int PORT = 3443;
	// клиентский сокет
	private Socket clientSocket = null;
	// количество клиента в чате, статичное поле
	
	
	public void initialize(Socket socket, Server server) {
	    try {
	      server.clients_count.incrementAndGet();
	      this.server = server;
	      this.clientSocket = socket;
	      outMessage = new PrintWriter(socket.getOutputStream());
	      inMessage = new Scanner(socket.getInputStream());
	    } catch (IOException ex) {
	      ex.printStackTrace();
	    }
	    System.out.println("ClientHandler initialized: " + this + " for Server: " + server);
	    System.out.println("Канал входящих сообщений: " + inMessage + " Канал исходящих сообщений: " + outMessage);
	    this.start();
	  }
	
	
	public ClientHandler() {
		
	}
	

	public void run() {
		
			// сервер отправляет сообщение
			server.sendMessageToAllClients("MSGНовый участник вошёл в чат!");
			server.sendMessageToAllClients("CLNКоличество клиентов в чате: " + server.clients_count.get());
		
		while (true) {
			if (Objects.isNull(inMessage)) {
				continue;
			}
			// Если от клиента пришло сообщение
			if (inMessage.hasNext()) {
				String clientMessage = inMessage.nextLine();
				if (clientMessage.length() < 3) {
					continue;
				}
				String command = clientMessage.substring(0, 3);
				switch (command) {
				
				case "RPL": {
					server.sendMessageToAllClients("MSG" + clientMessage.substring(3));
					System.out.println("Server receives message: " + clientMessage.substring(3));
					break;
				}
					
				case "END": {
					server.sendMessageToAllClients("MSG" + clientMessage.substring(3));
					server.sendMessageToAllClients("CLNКоличество клиентов в чате: " + server.clients_count.decrementAndGet());
					System.out.println(clientMessage.substring(3));
					server.removeClient(this);
					this.interrupt();
					break;
				}
					default:
					{
						continue;
					}
				}
			}
		}
	}

	public void sendMessageToClient(String msg) {
		try {
			System.out.println("Sending: " + msg + " to " + outMessage);
			outMessage.println(msg);
			outMessage.flush();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
