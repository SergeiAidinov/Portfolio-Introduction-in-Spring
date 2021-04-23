package ru.yandex.incoming34.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import ru.yandex.incoming34.Server.ClientHandler;

@Component
public class Server {
	// порт, который будет прослушивать наш сервер
	static final int PORT = 3443;
	// список клиентов, которые будут подключаться к серверу
	private static ArrayList<ClientHandler> clients = new ArrayList<ClientHandler>();
	static AtomicInteger clients_count =  new AtomicInteger(0);
	@Autowired
	ClientHandler clientHandler;
	ApplicationContext context;
	

	public ClientHandler getClientHandler() {
		return clientHandler;
	}
	
	public void setContext(ApplicationContext ctx) {
		context = ctx;
	}
	
	public void report() {
		System.out.println("Server: " + this);
		System.out.println("ClientHandler: " + clientHandler);
		System.out.println("Context: " + context);
	}
	
	

	public void procedure() {
		// сокет клиента, это некий поток, который будет подключаться к серверу
		// по адресу и порту
		Socket clientSocket = null;
		// серверный сокет
		ServerSocket serverSocket = null;
		try {
			// создаём серверный сокет на определенном порту
			serverSocket = new ServerSocket(PORT);
			System.out.println("Сервер запущен!");
			// запускаем бесконечный цикл
			while (true) {
				// таким образом ждём подключений от сервера
				clientSocket = serverSocket.accept();
				// создаём обработчик клиента, который подключился к серверу
				// this - это наш сервер
				
				ClientHandler client = (ClientHandler) context.getBean("clientHandler");
				client.initialize(clientSocket, this);
				System.out.println("Новое подключение: " + client);
				clients.add(client);
				System.out.println("Total clients connected to server: " + clients.size());
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				// закрываем подключение
				clientSocket.close();
				System.out.println("Сервер остановлен");
				serverSocket.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public void sendMessageToAllClients(String msg) {
		clients.trimToSize();
		System.out.println("Total clients connected to server: " + clients.size());
		for (ClientHandler oneClient : clients) {
			oneClient.sendMessageToClient(msg);
			System.out.println("Server sent message " + msg + " to client " + oneClient);
		}

	}

	// удаляем клиента из коллекции при выходе из чата
	public void removeClient(ClientHandler client) {
		clients.remove(client);
	}

}
