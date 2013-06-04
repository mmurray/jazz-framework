package org.jazzframework.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

public class ServerDaemon {

	public static void main(String[] args) throws Exception {
		WebAppContext webAppContext = new WebAppContext();
		webAppContext.setContextPath("/");
		webAppContext.setResourceBase("webapp");
		webAppContext.setDescriptor("webapp/WEB-INF/web.xml");

		int port = 80;
		String envPort = System.getenv("PORT");
		if(envPort != null && !envPort.isEmpty()) {
			port = Integer.parseInt(envPort);
		}
		Server server = new Server(port);
		server.setHandler(webAppContext);

		server.start();
		server.join();
	}

}
