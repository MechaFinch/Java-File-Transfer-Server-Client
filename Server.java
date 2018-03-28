/*Alex Pickering
 *3/26/2018
 *
 *Simple Database Server
 */
 
 import java.io.*;
 import java.net.*;
import java.util.ArrayList;
 
 public class Server{
 	static int port;
 	static ArrayList<byte[]> databaseraw = new ArrayList<byte[]>();
 	static ArrayList<String[]> databasetext = new ArrayList<String[]>();
 	
 	public static void main(String[] args) throws IOException{
 		port = AR.readIntRange(1024, 49151, "Please input the server port", "Please input a number.", "Port must be from 1024-49151", "Port must be from 1024-49151");
 		
 		ServerSocket server = new ServerSocket(port);
 		
 		System.out.println("Server successfully initiated on port " + port);
 		
 		Thread ConnectionListener = new Thread(){
 			public void run(){
 				while(true){
 					try{
			 			Socket client = server.accept();
			 			
			 			Runnable connection = new ConnectionHandler(client);
			 			new Thread(connection).start();
			 		} catch(IOException e){
			 		}
		 		}
 			}
 		};
 		
 		Thread mon = new Thread(new ServerMonitor());
 		ConnectionListener.start();
 		mon.start();
 	}
 }
 
 class ConnectionHandler implements Runnable{
 	Socket client;
 	
 	ConnectionHandler(Socket s) throws IOException{
 		client = s;
 	}
 	
 	public void run() {
 		//Init IO
 		BufferedReader clientOutput = null;
 		PrintWriter toClient = null;
 		InetAddress clientAddress = client.getInetAddress();
 		int clientPort = client.getPort();
 		
 		System.out.println("Recieving connection from " + clientAddress + " on port " + clientPort);
 		
 		try {
 			toClient = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);
 			clientOutput = new BufferedReader(new InputStreamReader(client.getInputStream()));
 		} catch(IOException e) {
 			System.out.println("IO Exception occured while connecting to " + clientAddress + " on port " + clientPort);
 			return;
 		}
 		
 		//Verify connection handshake
 		try {
 			System.out.println("Verifying proper connection with client at " + clientAddress + " on port " + clientPort);
 			if(!clientOutput.readLine().equals("verifyConnection")) {
 	 			System.out.println("Client at " + clientAddress + " on port " + clientPort + " tried to connect with invalid handshake.");
 	 			return;
 	 		} else {
 	 			toClient.println("connectionVerify");
 	 			toClient.flush();
 	 		}
 			
 			System.out.println("Client at " + clientAddress + " on port " + clientPort + " verified.");
 			
 			//Running loop
 			while(true) {
 				//Check for closed client
 				if(clientOutput.read() == -1) {
 					System.out.println("Client at " + clientAddress + " on port " + clientPort + " disconnected.");
 	 				return;
 	 			}
 				
 				String inputRaw = "";
 				
 				inputRaw = clientOutput.readLine();
 				
 				//Parse input
 				String[] input = inputRaw.split(" ");
 				
 				if(input[0].equalsIgnoreCase("sendtmptxt")) {
 					String cont = "";
 					for(int i = 3; i < input.length; i++) {
 						cont += input[i] + ((i == input.length - 1) ? "" : " ");
 					}
 					String[] todata = {
 							"tmptxt",
 							input[1],
 							input[2],
 							cont
 					};
 					Server.databasetext.add(todata);
 					System.out.println("Temporary text recieved from client at " + clientAddress + " on port " + clientPort + " with id " + input[1] + ".");
 					toClient.println("ftmptextrecieved");
 					toClient.flush();
 				} else if(input[0].equalsIgnoreCase("sendtxt")) {
 					String cont = "";
 					for(int i = 3; i < input.length; i++) {
 						cont += input[i] + ((i == input.length - 1) ? "" : " ");
 					}
 					String[] todata = {
 							"txt",
 							input[1],
 							input[2],
 							cont
 					};
 					Server.databasetext.add(todata);
 					System.out.println("Text recieved from client at " + clientAddress + " on port " + clientPort + " with id " + input[1] + ".");
 					toClient.println("ftextrecieved");
 					toClient.flush();
 				} else if(input[0].equalsIgnoreCase("downtxt")) {
 					System.out.println("Client at " + clientAddress + " on port " + clientPort + " has sent a request for text id " + input[1] + ".");
 					String txt = "";
 					String type = "";
 					boolean p = true;
 					int id = 0;
 					String[] dat;
 					for(int i = 0; i < Server.databasetext.size(); i++) {
 						dat = Server.databasetext.get(i);
 						if(dat[1].equals(input[1])) {
 							if(dat[2].equals(input[2])) {
 								txt = dat[3];
 								type = dat[0];
 								id = i;
 								break;
 							} else {
 								toClient.println("fpasswrong");
 								toClient.flush();
 								System.out.println("The client at " + clientAddress + " on port " + clientPort + " didn't recieve the requested text because the password was incorrect.");
 								p = false;
 								break;
 							}
 						}
 					}
 					
 					if(!p) continue;
 					
 					if(txt.equals("")) {
 						toClient.println("fnotfound");
 						toClient.flush();
 						System.out.println("The text requested by the client at " + clientAddress + " on port " + clientPort + " was not found.");
 						continue;
 					}
 					
 					toClient.println("ffound");
 					toClient.println(txt);
 					toClient.flush();
 					System.out.println("The client at " + clientAddress + " on port " + clientPort + " has recieved their requested text.");
 					
 					if(type.equals("tmptxt")) {
 						Server.databasetext.remove(id);
 					}
 				} else if(input[0].equals("rmtxt")){
 					System.out.println("Client at " + clientAddress + " on port " + clientPort + " has sent a request to remove text id " + input[1] + ".");
 					String[] dat;
 					boolean p = true;
 					int id = -1;
 					for(int i = 0; i < Server.databasetext.size(); i++){
 						dat = Server.databasetext.get(i);
 						
 						if(dat[1].equals(input[1])){
 							if(dat[2].equals(input[2])){
 								id = i;
 								break;
 							} else {
 								toClient.println("fpasswrong");
 								toClient.flush();
 								System.out.println("The text requested for removal by the client at " + clientAddress + " on port " + clientPort + " could not be removed because the password was incorrect.");
 								p = false;
 								break;
 							}
 						}
 					}
 					
 					if(!p) continue;
 					
 					if(id == -1){
 						toClient.println("fnotfound");
 						toClient.flush();
 						System.out.println("The text requested for removal by the client at " + clientAddress + " on port " + clientPort + " could not be found.");
 						continue;
 					}
 					
 					Server.databasetext.remove(id);
 					
 					toClient.println("fdone");
 					toClient.flush();
 					System.out.println("The text requested for removal by the client at " + clientAddress + " on port " + clientPort + " had been removed.");
 				}
 			}
 		} catch(IOException e) {
 			String msg = e.getMessage();
 			
 			if(msg.equalsIgnoreCase("Connection reset")) {
 				System.out.println("Client at " + clientAddress + " on port " + clientPort + " disconnected.");
 			} else {
 				System.out.println("Unknown IOException occured with client at " + clientAddress + " on port " + clientPort);
 				e.printStackTrace();
 			}
 			
 			try {
 				client.close();
 			} catch(IOException a) {
 				
 			}
 			
 			return;
 		}
 	}
 }
 
 class ServerMonitor implements Runnable{
 	ServerMonitor(){
 	}
 	
 	public void run(){
 		while(true){
 			String input = AR.readString("", "");
 			String[] com = input.split(" ");
 			
 			if(com[0].equalsIgnoreCase("listtext")){
 				if(com.length != 2){
 					System.out.println("Invalid Use! Must be 'listtext <[all, tmp, per]>'!");
 					continue;
 				}
 				
 				if(com[1].equalsIgnoreCase("all")){
 					showAllText();
 				} else if(com[1].equalsIgnoreCase("tmp")){
 					showTmpText();
 				} else if(com[1].equalsIgnoreCase("per")){
 					showPerText();
 				} else {
 					System.out.println("Invalid section");
 				}
 			}
 		}
 	}
 	
 	void showAllText(){
 		ArrayList<String> tmp = new ArrayList<String>();
 		ArrayList<String> per = new ArrayList<String>();
 		
 		for(int i = 0; i < Server.databasetext.size(); i++){
 			String[] s = Server.databasetext.get(i);
 			
 			if(s[0].equals("tmptxt")){
 				tmp.add(s[1]);
 			} else {
 				per.add(s[1]);
 			}
 		}
 		
 		System.out.println("Temporary Text IDs:");
 		
 		for(int i = 0; i < tmp.size(); i++){
 			System.out.println(tmp.get(i));
 		}
 		
 		System.out.println("Permanant Text IDs:");
 		
 		for(int i = 0; i < per.size(); i++){
 			System.out.println(per.get(i));
 		}
 	}
 	
 	void showTmpText(){
 		System.out.println("Temporary Text IDs:");
 		
 		for(int i = 0; i < Server.databasetext.size(); i++){
 			String[] s = Server.databasetext.get(i);
 			if(s[0].equals("tmptxt")){
 				System.out.println(s[1]);
 			}
 		}
 	}
 	
 	void showPerText(){
 		System.out.println("Permanant Text IDs:");
 		
 		for(int i = 0; i < Server.databasetext.size(); i++){
 			String[] s = Server.databasetext.get(i);
 			if(s[0].equals("txt")){
 				System.out.println(s[1]);
 			}
 		}
 	}
 }