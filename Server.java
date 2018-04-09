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
 	static ArrayList<FileEntry> databaseraw = new ArrayList<FileEntry>();
 	static ArrayList<String[]> databasetext = new ArrayList<String[]>();
 	static ArrayList<Thread> connections = new ArrayList<Thread>();
 	static ArrayList<InetAddress> ips = new ArrayList<InetAddress>();
 	static ArrayList<Integer> ports = new ArrayList<Integer>();
 	static ServerSocket server;
 	
 	static Thread ConnectionListen;
	
	static Thread ConnectionEnd;
 	
 	public static void main(String[] args) throws IOException{
 		initServer();
 		
 		Thread mon = new Thread(new ServerMonitor());
 		mon.start();
 	}
 	
 	static void initServer() throws IOException{
 		while(true) {
 			String initin = AR.readString("Please input the port, or 'default'.", "Please input a port.");
 			
 			try {
 				port = Integer.parseInt(initin);
 				break;
 			} catch(NumberFormatException e) {
 				if(initin.equalsIgnoreCase("default")) {
 					port = 48293;
 					break;
 				} else {
 					System.out.println("Please input a port.");
 				}
 			}
 		}
 		
 		server = new ServerSocket(port);
 		
 		ConnectionListen = new Thread(new ConnectionListener());
 		ConnectionEnd = new Thread(new ConnectionEnder());
 		
 		ConnectionListen.start();
 		ConnectionEnd.start();
 		
 		System.out.println("Server successfully initiated on port " + port);
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
 		
 		Server.ips.add(clientAddress);
 		Server.ports.add(clientPort);
 		
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
 			System.out.println("Verifying proper connection with the client at " + clientAddress + " on port " + clientPort);
 			String verf = clientOutput.readLine();
 			if(!verf.equals("verifyConnection")) {
 	 			if(verf.equals("testfor")) {
 	 				System.out.println("The client at " + clientAddress + " on port " + clientPort + " was a server search.");
 	 			} else {
 	 				System.out.println("The client at " + clientAddress + " on port " + clientPort + " tried to connect with invalid handshake.");
 	 			}
 	 			
 	 			return;
 	 		} else {
 	 			toClient.println("connectionVerify");
 	 			toClient.flush();
 	 		}
 			
 			System.out.println("The client at " + clientAddress + " on port " + clientPort + " verified.");
 			
 			//Running loop
 			while(true) {
 				//Check for closed client
 				if(clientOutput.read() == -1) {
 					System.out.println("The client at " + clientAddress + " on port " + clientPort + " disconnected.");
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
 					System.out.println("Temporary text recieved from the client at " + clientAddress + " on port " + clientPort + " with id " + input[1] + ".");
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
 					System.out.println("Text recieved from the client at " + clientAddress + " on port " + clientPort + " with id " + input[1] + ".");
 					toClient.println("ftextrecieved");
 					toClient.flush();
 				} else if(input[0].equalsIgnoreCase("downtxt")) {
 					System.out.println("The client at " + clientAddress + " on port " + clientPort + " has requested text id " + input[1] + ".");
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
 					System.out.println("The client at " + clientAddress + " on port " + clientPort + " has requested to remove text id " + input[1] + ".");
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
 				} else if(input[0].equals("sendfile") || input[0].equals("sendtmpfile")) {
 					System.out.println("Recieving file from the client at " + clientAddress + " on port " + clientPort + ".");
 					int datalength = Integer.parseInt(input[3]);
 					
 					toClient.println("fready");
 					toClient.flush();
 					
 					DataInputStream dis = new DataInputStream(client.getInputStream());
 					byte[] data = new byte[datalength];
 					dis.readFully(data, 0, datalength);
 					
 					FileEntry fe = new FileEntry(input[1], input[2], (input[0].equals("sendtmpfile")), data);
 					
 					Server.databaseraw.add(fe);
 					
 					toClient.println("done");
 					toClient.flush();
 					clientOutput.readLine();
 					System.out.println("File recieved from the client at " +  clientAddress + " on port " + clientPort + " successfully.");
 				} else if(input[0].equals("downfile")) {
 					System.out.println("The client at " + clientAddress + " on port " + clientPort + " has requested file id " + input[1] + ".");
 					boolean p = true;
 					int id = -1;
 					FileEntry fe = new FileEntry("", "", false, new byte[1]);
 					for(int i = 0; i < Server.databaseraw.size(); i++) {
 						fe = Server.databaseraw.get(i);
 						
 						if(fe.id.equals(input[1])) {
 							if(fe.pass.equals(input[2])) {
 								id = i;
 								break;
 							} else {
 								toClient.println("fpasswrong");
 								toClient.flush();
 								System.out.println("The client at " + clientAddress + " on port " + clientPort + " didn't recieve the requested file because the password was incorrect.");
 								p = false;
 							}
 							break;
 						}
 					}
 					
 					if(!p) continue;
 					
 					if(id == -1) {
 						toClient.println("fnotfound");
 						toClient.flush();
 						System.out.println("The file requested by the client at " + clientAddress + " on port " + clientPort + " could not be found.");
 						continue;
 					}
 					
 					toClient.println("fsending " + fe.data.length);
 					toClient.flush();
 					
 					DataOutputStream dos = new DataOutputStream(client.getOutputStream());
 			 		dos.write(fe.data);
 			 		dos.flush();
 			 		
 			 		if(fe.tmp) Server.databaseraw.remove(id);
 					
 					clientOutput.readLine();
 					System.out.println("The client at " + clientAddress + " on port " + clientPort + " has recieved their requested file.");
 				} else if(input[0].equals("rmfile")) {
 					System.out.println("The client at " + clientAddress + " on port " + clientPort + " has requested to remove file ID " + input[1]);
 					FileEntry fe;
 					boolean p = true;
 					int id = -1;
 					
 					for(int i = 0; i < Server.databaseraw.size(); i++) {
 						fe = Server.databaseraw.get(i);
 						
 						if(fe.id.equals(input[1])) {
 							if(fe.pass.equals(input[2])) {
 								id = i;
 								break;
 							} else {
 								toClient.println("fpasswrong");
 								toClient.flush();
 								System.out.println("The file requested for removal by the client at " + clientAddress + " on port " + clientPort + " could not be removed because the password was incorrect.");
 								p = false;
 							}
 							break;
 						}
 					}
 					
 					if(!p) continue;
 					
 					if(id == -1) {
 						toClient.println("fnotfound");
 						toClient.flush();
 						System.out.println("The file requested for removal by the client at " + clientAddress + " on port " + clientPort + " could not be found.");
 						continue;
 					}
 					
 					Server.databaseraw.remove(id);
 					
 					toClient.println("fdone");
 					toClient.flush();
 					System.out.println("The file requested for removal by the client at " + clientAddress + " on port " + clientPort + " had been removed.");
 				} else if(input[0].equals("lsttxt")){
 					if(input[1].equals("all")){
 						System.out.println("The client at " + clientAddress + " on port " + clientPort + " has requested all text IDs.");
 						
 						ArrayList<String> tmpIDs = new ArrayList<String>();
 						ArrayList<String> permIDs = new ArrayList<String>();
 						
 						for(int i = 0; i < Server.databasetext.size(); i++){
 							String[] s = Server.databasetext.get(i);
 							
 							if(s[0].equals("tmptxt")){
 								tmpIDs.add(s[1]);
 							} else {
 								permIDs.add(s[1]);
 							}
 						}
 						
 						toClient.println("ftmpl " + tmpIDs.size());
 						toClient.flush();
 						
 						for(int i = 0; i < tmpIDs.size(); i++){
 							toClient.println("f" + tmpIDs.get(i));
 							toClient.flush();
 						}
 						
 						toClient.println("fperml " + permIDs.size());
 						toClient.flush();
 						
 						for(int i = 0; i < permIDs.size(); i++){
 							toClient.println("f" + permIDs.get(i));
 							toClient.flush();
 						}
 						
 						toClient.println("fdone");
 						toClient.flush();
 					} else if(input[1].equals("tmp")){
 						System.out.println("The client at " + clientAddress + " on port " + clientPort + " has requested temporary text IDs.");
 						
 					} else if(input[1].equals("per")){
 						System.out.println("The client at " + clientAddress + " on port " + clientPort + " has requested permanant text IDs.");
 						
 					} else {
 						System.out.println("The client at " + clientAddress + " on port " + clientPort + " has requested text ids ivalidly.");
 					}
 				} else {
 					System.out.println(inputRaw);
 				}
 			}
 		} catch(IOException e) {
 			String msg = e.getMessage();
 			
 			if(msg.equalsIgnoreCase("Connection reset") || msg.equalsIgnoreCase("socket closed")) {
 				System.out.println("The client at " + clientAddress + " on port " + clientPort + " disconnected.");
 			} else {
 				System.out.println("Unknown IOException occured with the client at " + clientAddress + " on port " + clientPort);
 				e.printStackTrace();
 			}
 			
 			try {
 				client.close();
 			} catch(IOException a) {
 				
 			}
 			
 			return;
 		} catch(Exception e) {
 			System.out.println("The connection with the client at " + clientAddress + " on port " + clientPort + " has crashed.");
 			try {
 				client.close();
 			} catch(IOException a) {
 				
 			}
 			throw e;
 		}
 	}
 }
 
 class FileEntry {
	 String id;
	 String pass;
	 boolean tmp;
	 byte[] data;
	 
	 FileEntry(String id, String pass, boolean tmp, byte[] data){
		 this.id = id;
		 this.pass = pass;
		 this.tmp = tmp;
		 this.data = data;
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
 					System.out.println("Invalid Use! Must be 'listtext (all | tmp | per)'!");
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
 			} else if(com[0].equalsIgnoreCase("listconnections")) {
 				if(com.length != 1) {
 					System.out.println("Invalid Use! Must be 'listconnections'!");
 					continue;
 				}
 				
 				listConnections();
 			} else if(com[0].equalsIgnoreCase("shutdown")) {
 				if(com.length != 1) {
 					System.out.println("Invalid Use! Must be 'shutdown'!");
 					continue;
 				}
 				
 				System.exit(0);
 			} else if(com[0].equalsIgnoreCase("restart")) {
 				if(com.length != 1) {
 					System.out.println("Invalid Use! Must be 'restart'!");
 					continue;
 				}
 				
 				restartServer();
 			}
 		}
 	}
 	
 	void listConnections() {
 		if(Server.connections.size() == 0) {
 			System.out.println("No connections.");
 		} else {
 			System.out.println("Connections:");
 	 		for(int i = 0; i < Server.connections.size(); i++) {
 	 			System.out.println("IP " + Server.ips.get(i) + " on port " + Server.ports.get(i));
 	 		}
 		}
 	}
 	
 	void restartServer() {
 		for(Thread t : Server.connections) {
 			t.interrupt();
 		}
 		
 		Server.ConnectionListen.interrupt();
 		Server.ConnectionEnd.interrupt();
 		
 		Server.connections.clear();
 		Server.databaseraw.clear();
 		Server.databasetext.clear();
 		
 		try {
 			Server.server.close();
			Server.initServer();
		} catch (IOException e) {
		}
 	}
 	
 	void showAllText(){
 		showTmpText();
 		showPerText();
 	}
 	
 	void showTmpText(){
 		ArrayList<String> ids = new ArrayList<String>();
 		String[] s;
 		
 		for(int i = 0; i < Server.databasetext.size(); i++) {
 			s = Server.databasetext.get(i);
 			
 			if(s[0].equals("tmptxt")) {
 				ids.add(s[1]);
 			}
 		}
 		
 		if(ids.size() == 0) {
 			System.out.println("No temporary text found.");
 		} else {
 			System.out.println("Temporary text ids:");
 			for(int i = 0; i < ids.size(); i++) {
 				System.out.println(ids.get(i));
 			}
 		}
 	}
 	
 	void showPerText(){
 		ArrayList<String> ids = new ArrayList<String>();
 		String[] s;
 		
 		for(int i = 0; i < Server.databasetext.size(); i++) {
 			s = Server.databasetext.get(i);
 			
 			if(s[0].equals("txt")) {
 				ids.add(s[1]);
 			}
 		}
 		
 		if(ids.size() == 0) {
 			System.out.println("No permanant text found.");
 		} else {
 			System.out.println("Permanant text ids:");
 			for(int i = 0; i < ids.size(); i++) {
 				System.out.println(ids.get(i));
 			}
 		}
 	}
 }
 
 class ConnectionListener implements Runnable{
	 public void run(){
		while(true){
			try{
	 			Socket client = Server.server.accept();
	 			
	 			Runnable connection = new ConnectionHandler(client);
	 			Server.connections.add(new Thread(connection));
	 			Server.connections.get(Server.connections.size() - 1).start();
	 		} catch(IOException e){
	 		}
 		}
	}
 }
 
 class ConnectionEnder implements Runnable{
	 public void run() {
		while(true) {
			for(int i = Server.connections.size() - 1; i >= 0; i--) {
				if(!Server.connections.get(i).isAlive()) {
					Server.connections.remove(i);
					Server.ips.remove(i);
					Server.ports.remove(i);
				}
			}
			
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
			}
		}
	}
 }