/*Alex Pickering
 *3/26/2018
 *
 *Simple Database Client
 */
 
 import java.io.*;
 import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
 
 public class Client{
 	static boolean connected = false;
 	static String filePath = "";
 	static Socket server;
 	static String[] comm;
 	static BufferedReader serverOutput;
 	static PrintWriter toServer;
 	
 	public static void main(String[] args) throws IOException{
 		String input = "";
 		Thread connectTest = new Thread() {
 			public void run() {
 				while(true) {
 					if(connected) {
 						try {
							if(serverOutput.read() == -1) {
								System.out.println("The connection to the server has been lost.");
								connected = false;
							}
						} catch (IOException e) {
							System.out.println("The connection to the server has been lost.");
							connected = false;
						}
 					}
 					
 					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
					}
 				}
 			}
 		};
 		connectTest.start();
 		while(true){
 			input = AR.readString("Input a command or 'help' for a list of commands.", "Please input a command");
 			
 			//Parse
 			comm = input.split(" ");
 			
 			if(comm[0].equalsIgnoreCase("connect")){
 				if(comm.length != 3){
 					System.out.println("Invalid Use! Must be 'connect <ip> [<port>, default]'!");
 					continue;
 				}
 				try{
 					if(connected) {
 						System.out.println("Please disconnect from current server before connecting to another.");
 					} else {
 						connect();
 					}
 				} catch(UnknownHostException e){
 					System.out.println("Unknown Host");
 				}
 			} else if(comm[0].equalsIgnoreCase("disconnect")) {
 				if(comm.length != 1) {
 					System.out.println("Invalid Use! Must only be 'disconnect'!");
 					continue;
 				}
 				
				disconnect();
 			} else if(comm[0].equalsIgnoreCase("sendtext")) {
 				if(comm.length == 3) {
 					System.out.println("Cannot send text. No text to send.");
 					continue;
 				} else if(!connected) {
 					System.out.println("Cannot send text. Not connected to server.");
 					continue;
 				} else if(comm.length < 4) {
 					System.out.println("Invalid Use! Must be 'sendtext [-tmp] <id> <password> <text>'!");
 					continue;
 				}
 				
 				sendText();
 			} else if(comm[0].equalsIgnoreCase("downloadtext")) {
 				if(comm.length != 3) {
 					System.out.println("Invalid Use! Must be 'downloadtext <id> <password>'!");
 					continue;
 				}
 				
 				downloadText();
 			} else if(comm[0].equalsIgnoreCase("removetext")){
 				if(comm.length != 3){
 					System.out.println("Invalid Use! Must be 'removetext <id> <password>'!");
 					continue;
 				}
 				
 				removeText();
 			} else if(comm[0].equalsIgnoreCase("shutdown")) {
 				if(comm.length != 1) {
 					System.out.println("Invalid Use! Must be 'shutdown'!");
 					continue;
 				}
 				
 				System.exit(0);
 			} else if(comm[0].equalsIgnoreCase("findips")) {
 				if(comm.length != 2) {
 					System.out.println("Invalid Use! Must be 'findips [all, server]'!");
 					continue;
 				}
 				
 				if(comm[1].equalsIgnoreCase("all")) {
 					findAllIps();
 				} else if(comm[1].equalsIgnoreCase("server")) {
 					if(connected) {
 						System.out.println("Please disconnect before trying to find servers.");
 						continue;
 					}
 					
 					findServerIps();
 				} else {
 					System.out.println("Invalid Use! Must be 'findips [all, server]'!");
 				}
 			} else if(comm[0].equalsIgnoreCase("sendfile")) {
 				if(comm.length == 4) {
 					sendFile(false);
 				} else if(comm.length == 5 && comm[1].equalsIgnoreCase("-abs")) {
 					sendFile(true);
 				} else {
 					System.out.println("Invalid Use! Must be 'sendfile [-abs] <filename/path> <id> <password>'!");
 					continue;
 				}
 			} else if(comm[0].equalsIgnoreCase("selectdir")) {
 				if(comm.length == 2) {
 					selectDir(true);
 				} else if(comm.length == 3 && comm[1].equalsIgnoreCase("-prev")) {
 					selectDir(false);
 				} else {
 					System.out.println("Invalid Use! Must be 'selectdir [-prev] <directory>'!");
 					continue;
 				}
 			} else if(comm[0].equalsIgnoreCase("downloadfile")) {
 				if(comm.length == 4) {
 					downloadFile(false);
 				} else if(comm.length == 5 && comm[1].equalsIgnoreCase("-abs")) {
 					downloadFile(true);
 				} else {
 					System.out.println("Invalid Use! Must be 'downloadfile [-abs] <filename/path> <id> <password>'!");
 					continue;
 				}
 			}
 		}
 	}
 	
 	static void downloadFile(boolean absPath) {
 		String fp = "";
 		if(absPath) {
 			fp = comm[1];
 		} else {
 			fp = comm[2];
 		}
 	}
 	
 	static void selectDir(boolean absPath) {
 		if(absPath) {
 			filePath = comm[1];
 		} else {
 			filePath = filePath + comm[2];
 		}
 	}
 	
 	static void sendFile(boolean absPath) throws IOException {
 		String fp = "";
 		if(absPath) {
 			fp = comm[2];
 		} else {
 			fp = filePath + comm[1];
 		}
 		
 		File read = new File(fp);
 		byte[] data = readFile(read);
 		
 		toServer.println("fsendfile " + (!absPath ? comm[2] : comm[3]) + (!absPath ? comm[3] : comm[4]) + data.length);
 		
 		String responce = serverOutput.readLine();
 		if(!responce.equals("ready")) {
 			System.out.println("File sending failed. Server didn't recieve file sending request.");
 			return;
 		}
 		
 		DataOutputStream dos = new DataOutputStream(server.getOutputStream());
 		dos.write(data);
 		dos.flush();
 		
 		responce = serverOutput.readLine();
 		if(responce.equals("done")) {
 			System.out.println("File sent successfully");
 		}
 	}
 	
 	static void findServerIps() throws IOException {
 		System.out.println("Searching for open local ips with servers running on the default port. This may take up to ~5 minutes.");
 		InetAddress local = InetAddress.getLocalHost();
 		byte[] ip = local.getAddress();
 		for(int i = 1; i < 255; i++) {
 			ip[3] = (byte) i;
 			InetAddress testAdd = InetAddress.getByAddress(ip);
 			if(testAdd.isReachable(500)) {
 				try {
 					Socket testSocket = new Socket();
 					testSocket.connect(new InetSocketAddress(testAdd, 48293), 500);
 					serverOutput = new BufferedReader(new InputStreamReader(testSocket.getInputStream()));
 			 		toServer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(testSocket.getOutputStream())), true);
 			 		toServer.println("testfor");
 			 		toServer.flush();
 			 		testSocket.close();
 			 		
 			 		System.out.println("Server found at " + testAdd);
 				} catch(SocketTimeoutException e) {
 				}
 			}
 		}
 	}
 	
 	static void findAllIps() throws IOException {
 		System.out.println("Searching for open ips. This may take up to ~2.5 minutes.");
 		InetAddress local = InetAddress.getLocalHost();
 		byte[] ip = local.getAddress();
 		for(int i = 1; i < 255; i++) {
 			ip[3] = (byte) i;
 			InetAddress testAdd = InetAddress.getByAddress(ip);
 			
 			if(testAdd.isReachable(500)) {
 				System.out.println(testAdd + ((testAdd.equals(local)) ? " (Localhost)" : ""));
 			}
 		}
 		
 		System.out.println("All found ips listed.");
 	}
 	
 	static void removeText() throws IOException {
 		String responce = "";
 		toServer.println("frmtxt " + comm[1] + " " + comm[2]);
 		toServer.flush();
 		
 		responce = serverOutput.readLine();
 		
 		if(responce.equals("done")){
 			System.out.println("Text removed from server.");
 		} else if(responce.equals("notfound")){
 			System.out.println("Text id not found.");
 		} else if(responce.equals("passwrong")){
 			System.out.println("Text password invalid.");
 		} else {
 			System.out.println("Invalid server responce.");
 		}
 	}
 	
 	static void downloadText() throws IOException {
 		String text = "";
 		String responce = "";
 		
 		toServer.println("fdowntxt " + comm[1] + " " + comm[2]);
 		toServer.flush();
 		
 		responce = serverOutput.readLine();
 		
 		if(responce.equals("found")) {
 			System.out.println("Text requested successfully.");
 		} else if(responce.equals("notfound")) {
 			System.out.println("Text id not found.");
 			return;
 		} else if(responce.equals("passwrong")) {
 			System.out.println("Text password invalid.");
 			return;
 		} else {
 			System.out.println("Invalid server responce.");
 			return;
 		}
 		
 		text = serverOutput.readLine();
 		
 		System.out.println("Requested text:\n" + text);
 	}
 	
 	static void sendText() throws IOException {
 		int start = 3;
 		boolean tmp = false;
 		if(comm[1].equalsIgnoreCase("-tmp")) {
 			tmp = true;
 			start = 4;
 			
 			if(comm.length < 5) {
 				System.out.println("Cannot send text. No text to send (Flag applied: temporary)");
 				return;
 			}
 		}
 		
 		String txt = "";
		for(int i = start; i < comm.length; i++) {
			txt += comm[i] + ((i == comm.length - 1) ? "" : " ");
		}
 		
 		if(tmp) {
 			toServer.println("fsendtmptxt " + comm[2] + " " + comm[3] + " " + txt);
 		} else {
 			toServer.println("fsendtxt " + comm[1] + " " + comm[2] + " " + txt);
 		}
 		
 		toServer.flush();
 		
 		System.out.println("Text sent. Waiting for conformation from server.");
 		
 		String serverconf = serverOutput.readLine();
 		
 		if(tmp && serverconf.equals("tmptextrecieved")) {
 			System.out.println("Successfully sent temporary text to server.");
 		} else if(!tmp && serverconf.equals("textrecieved")) {
 			System.out.println("Successfully sent text to server.");
 		} else {
 			System.out.println("Server failed to recieve text.");
 		}
 	}
 	
 	static void disconnect() throws IOException{
 		server.close();
 		connected = false;
 		System.out.println("Disconnected from server.");
 	}
 	
 	static boolean testConnect() throws IOException {
		return !(serverOutput.read() == -1);
 	}
 	
 	static void connect() throws UnknownHostException, IOException{
 		if(comm[2].equalsIgnoreCase("default")) {
 			comm[2] = "48293";
 		}
 		try{
 			server = new Socket(InetAddress.getByName(comm[1]), Integer.parseInt(comm[2]));
 		} catch(NumberFormatException e){
 			System.out.println("Invalid use! Must be 'connect <ip> <port>'!");
 		} catch(ConnectException e) {
 			System.out.println("Connection refused to ip " + InetAddress.getByName(comm[1]) + " on port " + comm[2] + ". The server may not exist on this address.");
 			return;
 		}
 		
 		serverOutput = new BufferedReader(new InputStreamReader(server.getInputStream()));
 		toServer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(server.getOutputStream())), true);
 		
 		connected = true;
 		
 		//Confirm connection
 		toServer.println("verifyConnection");
 		toServer.flush();
 		if(!serverOutput.readLine().equals("connectionVerify")){
 			System.out.println("Connection to port " + server.getPort() + " failed. Invalid handshake recieved.");
 			connected = false;
 			return;
 		}
 		System.out.println("Server connected successfully");
 	}
 	
 	static byte[] readFile(File f) throws IOException{
        return Files.readAllBytes(Paths.get(f.getPath()));
    }

    static void writeFile(File f, byte[] d) throws IOException{
        FileOutputStream fos = new FileOutputStream(f);
        fos.write(d);
        fos.close();
    }
 }