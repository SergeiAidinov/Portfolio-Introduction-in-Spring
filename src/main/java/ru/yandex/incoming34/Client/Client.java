package ru.yandex.incoming34.Client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import javax.swing.JLabel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Client {
	// графический интерфейс
	ClientWindow clientWindow;
	// адрес сервера
	private static final String SERVER_HOST = "localhost";
	// порт
	private static final int SERVER_PORT = 3443;
	// клиентский сокет
	private Socket clientSocket;
	// входящее сообщение
	private Scanner inMessage;
	// исходящее сообщение
	private PrintWriter outMessage;
	private JLabel jlNumberOfClients;
	private String clientName = "Incognito";

	public String getClientName() {
		return clientName;
	}

	@Autowired
	public Client(ClientWindow clientWindow) {
		try {
			// подключаемся к серверу
			clientSocket = new Socket(SERVER_HOST, SERVER_PORT);
			inMessage = new Scanner(clientSocket.getInputStream());
			outMessage = new PrintWriter(clientSocket.getOutputStream());
			this.clientWindow = clientWindow;

		} catch (IOException e) {
			e.printStackTrace();
		}
		clientWindow.setClient(this);
		performWork();
	}

	public void performWork() {

		// бесконечный цикл
		while (true) {
			// если есть входящее сообщение
			if (inMessage.hasNext()) {
				// считываем его
				String inMes = inMessage.nextLine();
				if (inMes.length() < 3) {
					continue;
				} else {
					String command = inMes.substring(0, 3);
					switch (command) {
					case "CLN": {
						clientWindow.dislpayQuantityOfClientsInChat(inMes.substring(3, inMes.length()));
						break;
					}
					case "MSG": {
						clientWindow.displayMessage(inMes.substring(3) + "\n");
						break;
					}
					default:
						break;
					}
				}
			}
		}

	}

	public void performSendingMessage(String messageStr) {
		outMessage.println(messageStr);
		System.out.println("Client sent message: " + messageStr);
		outMessage.flush();

	}

	public void endSession() {

		System.out.println("Session ended.");
		outMessage.flush();
		outMessage.close();
		inMessage.close();

		try {
			clientSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void defineClientName(String text) {
		System.out.println("In defineClientName(String text)");
		System.out.println("Text: " + text + text.length());
		if (!(text.length() == 0 || text.equals("Введите ваше имя: "))) {
			clientName = text;
		}
		clientWindow.freezeNameOfClient(clientName);
	}

	public ClientWindow getClientWindow() {
		return clientWindow;
	}

}
