/*Alex Pickering
 *3/26/2018
 *
 *Simple Database Server
 */
 
 import java.io.*;
 import java.net.*;
 
 public class Server{
 	public static int port;
 	public static void main(String[] args) throws IOException{
 		port = AR.readIntRange(1024, 49151, "Please input the server port", "Please input a number.", "Port must be from 1024-49151", "Port must be from 1024-49151");
 		
 		ServerSocket server = new ServerSocket(port);
 		
 		while(true){
 			Socket client = server.accept();
 			
 			Runnable connection = new ConnectionHandler(client);
 			new Thread(connection).start();
 		}
 	}
 }
 
 class ConnectionHandler implements Runnable{
 	ServerSocket server;
 	Socket client;
 	
 	ConnectionHandler(Socket s) throws IOException{
 		client = s;
 		server = new ServerSocket(Server.port);
 	}
 	
 	public void run(){
 		
 	}
 }