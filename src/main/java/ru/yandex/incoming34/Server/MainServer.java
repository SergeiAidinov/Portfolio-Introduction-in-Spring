package ru.yandex.incoming34.Server;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MainServer {

	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("configServer.xml");
		Server server = (Server) context.getBean("server");
		server.setContext(context);
		server.report();
		System.out.println("Server: " + server + " Clienthandler: " + server.getClientHandler());
		server.procedure();

	}

}
