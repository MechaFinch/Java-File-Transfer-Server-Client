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
								if(connected) {
									System.out.println("The connection to the server has been lost.");
									connected = false;
								}
							}
						} catch (IOException e) {
							if(connected) {
								System.out.println("The connection to the server has been lost.");
								connected = false;
							}
						}
 					}
 					
 					try {
						Thread.sleep(500);
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
 			
 			try {
	 			if(comm[0].equalsIgnoreCase("connect")){
	 				if(comm.length != 3){
	 					System.out.println("Invalid Use! Must be 'connect <ip> (<port> | default)'!");
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
	 					System.out.println("Invalid Use! Must be 'disconnect'!");
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
	 					System.out.println("Invalid Use! Must be 'sendtext [-tmp] <id> <password> <text>...'!");
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
	 					System.out.println("Invalid Use! Must be 'findips (all | server)'!");
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
	 					System.out.println("Invalid Use! Must be 'findips (all | server)'!");
	 				}
	 			} else if(comm[0].equalsIgnoreCase("sendfile")) {
	 				if(comm.length < 4) {
	 					System.out.println("Invalid Use! Must be 'sendfile [-abs -tmp] <id> <password> <filename/path>'!");
	 					continue;
	 				}
	 				if(comm[1].equalsIgnoreCase("-abs")) {
	 					if(comm[2].equalsIgnoreCase("-tmp")) {
	 						sendFile(true, true);
	 					} else if(comm[2].charAt(0) == '-') {
	 						System.out.println("Invalid Use! Must be 'sendfile [-abs  -tmp] <id> <password> <filename/path>'!");
	 	 					continue;
	 					} else {
	 						sendFile(true, false);
	 					}
	 				} else if(comm[1].equalsIgnoreCase("-tmp")) {
	 					if(comm[2].equalsIgnoreCase("-abs")) {
	 						sendFile(true, true);
	 					} else if(comm[2].charAt(0) == '-') {
	 						System.out.println("Invalid Use! Must be 'sendfile [-abs -tmp] <id> <password> <filename/path>'!");
	 	 					continue;
	 					} else {
	 						sendFile(false, true);
	 					}
	 				} else if(comm[1].charAt(0) == '-') {
	 					System.out.println("Invalid Use! Must be 'sendfile [-abs -tmp] <id> <password> <filename/path>'!");
	 					continue;
	 				} else {
	 					sendFile(false, false);
	 				}
	 			} else if(comm[0].equalsIgnoreCase("selectdir")) {
	 				if(comm.length < 2) {
	 					System.out.println("Invalid Use! Must be 'selectdir [-prev] <directory>...'!");
	 					continue;
	 				}
	 				if(comm[1].equalsIgnoreCase("-prev")) {
	 					selectDir(false);
	 				} else if(comm[1].charAt(0) == '-') {
	 					System.out.println("Invalid Use! Must be 'selectdir [-prev] <directory>...'!");
	 					continue;
	 				} else {
	 					selectDir(true);
	 				}
	 			} else if(comm[0].equalsIgnoreCase("downloadfile")) {
	 				if(comm.length < 4) {
	 					System.out.println("Invalid Use! Must be 'downloadfile [-abs] <id> <password> <filename/path>'!");
	 					continue;
	 				}
	 				if(comm[1].equalsIgnoreCase("-abs")) {
	 					if(comm.length < 5) {
	 						System.out.println("Invalid Use! Must be 'downloadfile [-abs] <id> <password> <filename/path>'!");
	 						continue;
	 					}
	 					
	 					downloadFile(true);
	 				} else if(comm[1].charAt(0) == '-') {
	 					System.out.println("Invalid Use! Must be 'downloadfile [-abs] <id> <password> <filename/path>'!");
	 					continue;
	 				} else {
	 					downloadFile(false);
	 				}
	 			} else if(comm[0].equalsIgnoreCase("removefile")) {
	 				if(comm.length != 3) {
	 					System.out.println("Invalid Use! Must be 'removefile <id> <password>'!");
	 					continue;
	 				}
	 				
	 				removeFile();
	 			} else if(comm[0].equalsIgnoreCase("listtext")){
	 				if(comm.length != 2){
	 					System.out.println("Invalid Use! Must be 'listtext (all | tmp | per)'!");
	 					continue;
	 				}
	 				
	 				listtext();
	 			}
 			} catch(SocketException e) {
 				if(!e.getMessage().equalsIgnoreCase("Connection reset")) {
 					throw e;
 				}
 			}
 		}
 	}
 	
 	static void listtext() throws IOException {
 		if(comm[1].equalsIgnoreCase("all")){
 			toServer.println("lsttxt all");
 			toServer.flush();
 			
 			while(!serverOutput.ready());
 			String sr = serverOutput.readLine();
 			String[] s = sr.split(" ");
 			
 			if(!s[0].equals("tmpl")){
 				System.out.println("Could not get text ids - Invalid server responce.");
 				toServer.println("fail");
 				toServer.flush();
 				return;
 			}
 			
 			int a = 0;
 			
 			try{
 				a = Integer.parseInt(s[1]);
 			} catch(NumberFormatException e){
 				System.out.println("Could not get text ids - Invalid server responce.");
 				toServer.println("fail");
 				toServer.flush();
 				return;
 			}
 			
 			toServer.println("ready");
 			toServer.flush();
 			
 			System.out.println("Temporary text IDs:");
 			for(int i = 0; i < a; i++) {
 				while(!serverOutput.ready());
 				System.out.println(serverOutput.readLine());
 			}
 			
 			while(!serverOutput.ready());
 			sr = serverOutput.readLine();
 			s = sr.split(" ");
 			
 			if(!s[0].equals("perml")) {
 				System.out.println("Could not get text ids - Invalid server responce.");
 				toServer.println("fail");
 				toServer.flush();
 				return;
 			}
 			
 			try {
 				a = Integer.parseInt(s[1]);
 			} catch(NumberFormatException e) {
 				System.out.println("Could not get text ids - Invalid server responce.");
 				toServer.println("fail");
 				toServer.flush();
 				return;
 			}
 			
 			toServer.println("ready");
 			toServer.flush();
 			
 			System.out.println("Permanant text IDs:");
 			for(int i = 0; i < a; i++) {
 				while(!serverOutput.ready());
 				System.out.println(serverOutput.readLine());
 			}
 		} else if(comm[1].equalsIgnoreCase("tmp")){
 			
 		} else if(comm[1].equalsIgnoreCase("per")){
 			
 		} else {
 			System.out.println("Invalid Use! Must be 'listtext (all | tmp | per)'!");
 		}
 	}
 	
 	static void removeFile() throws IOException {
 		toServer.println("rmfile " + comm[1] + " " + comm[2]);
 		toServer.flush();
 		
 		while(!serverOutput.ready());
 		String responce = serverOutput.readLine();
 		
 		if(responce.equals("done")) {
 			System.out.println("File removed successfully.");
 		} else if(responce.equals("notfound")) {
 			System.out.println("File ID not found.");
 		} else if(responce.equals("passwrong")) {
 			System.out.println("File password incorrect.");
 		} else {
 			System.out.println("Invalid server responce.");
 		}
 	}
 	
 	static void downloadFile(boolean absPath) throws IOException {
 		String fp = "";
 		String path = "";
 		
 		for(int i = (absPath ? 4 : 3); i < comm.length; i++) {
 			path += comm[i] + (i == comm.length - 1 ? "" : " ");
 		}
 		
 		if(absPath) {
 			fp = path;
 		} else {
 			fp = filePath + path;
 		}
 		System.out.println(fp);
 		
 		File write = new File(fp);
 		
 		toServer.println("downfile " + (!absPath ? (comm[1] + " " + comm[2]) : (comm[2] + " " + comm[3])));
 		
 		while(!serverOutput.ready());
 		String responcer = serverOutput.readLine();
 		String[] responces = responcer.split(" ");
 		
 		if(!responces[0].equals("sending")) {
 			if(responces[0].equals("notfound")) {
 				System.out.println("File ID not found.");
 			} else if(responces[0].equals("passwrong")) {
 				System.out.println("File password incorrect.");
 			} else {
 				System.out.println("Invalid server responce.");
 			}
 			
 			return;
 		}
 		
 		System.out.println("Downloading file");
 		DataInputStream dis = new DataInputStream(server.getInputStream());
 		int length = Integer.parseInt(responces[1]);
 		byte[] data = new byte[length];
 		dis.readFully(data, 0, length);
 		
 		writeFile(write, data);
 		
 		System.out.println("File downloaded successfullly to " + fp + ".");
 		
 		toServer.println("reset");
 	}
 	
 	static void selectDir(boolean absPath) {
 		String path = "";
 		
 		for(int i = (absPath ? 1 : 2); i < comm.length; i++) {
 			path += comm[i] + (i == comm.length - 1 ? (comm[i].contains(".") || comm[i].charAt(comm[i].length() - 1) == '\\' || comm[i].charAt(comm[i].length() - 1) == '/' ? "" : "\\") : " ");
 		}
 		
 		if(absPath) {
 			filePath = path;
 		} else {
 			filePath = filePath + path;
 		}
 		
 		System.out.println("File selected: " + filePath);
 	}
 	
 	static void sendFile(boolean absPath, boolean tmp) throws IOException {
 		String fp = "";
 		String idpass = "";
 		
 		if(absPath && tmp) {
 			idpass = comm[3] + " " + comm[4];
 			fp = filePath;
 			for(int i = 5; i < comm.length; i++) {
 				fp += comm[i] + (i == comm.length - 1 ? "" : " ");
 			}
 		} else if(absPath) {
 			idpass = comm[2] + " " + comm[3];
 			for(int i = 4; i < comm.length; i++) {
 				fp += comm[i] + (i == comm.length - 1 ? "" : " ");
 			}
 		} else if(tmp) {
 			fp = filePath;
 			idpass = comm[2] + " " + comm[3];
 			for(int i = 4; i < comm.length; i++) {
 				fp += comm[i] + (i == comm.length - 1 ? "" : " ");
 			}
 		} else {
 			fp = filePath;
 			idpass = comm[1] + " " + comm[2];
 			for(int i = 3; i < comm.length; i++) {
 				fp += comm[i] + (i == comm.length - 1 ? "" : " ");
 			}
 		}
 		
 		File read = new File(fp);
 		if(!read.exists()) {
 			System.out.println("File to send does not exist.");
 			return;
 		}
 		byte[] data = readFile(read);
 		
 		toServer.println((tmp ? "sendtmpfile " : "sendfile ") + idpass + " " + data.length);
 		
 		while(!serverOutput.ready());
 		String responce = serverOutput.readLine();
 		if(!responce.equals("ready")) {
 			System.out.println("File sending failed - Server didn't recieve file sending request.");
 			return;
 		}
 		
 		DataOutputStream dos = new DataOutputStream(server.getOutputStream());
 		dos.write(data);
 		dos.flush();
 		
 		while(!serverOutput.ready());
 		responce = serverOutput.readLine();
 		if(responce.equals("done")) {
 			System.out.println("File sent successfully");
 		} else {
 			System.out.println("File sending failed - Invalid server responce.");
 		}
 		
 		toServer.println("reset");
 		toServer.flush();
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
 		toServer.println("rmtxt " + comm[1] + " " + comm[2]);
 		toServer.flush();
 		
 		while(!serverOutput.ready());
 		responce = serverOutput.readLine();
 		
 		if(responce.equals("done")){
 			System.out.println("Text removed from server.");
 		} else if(responce.equals("notfound")){
 			System.out.println("Text removal failed - Text id not found.");
 		} else if(responce.equals("passwrong")){
 			System.out.println("Text removal failed - Text password invalid.");
 		} else {
 			System.out.println("Text removal failed - Invalid server responce.");
 		}
 	}
 	
 	static void downloadText() throws IOException {
 		String text = "";
 		String responce = "";
 		
 		toServer.println("downtxt " + comm[1] + " " + comm[2]);
 		toServer.flush();
 		
 		while(!serverOutput.ready());
 		responce = serverOutput.readLine();
 		
 		if(responce.equals("found")) {
 			System.out.println("Text requested successfully.");
 		} else if(responce.equals("notfound")) {
 			System.out.println("Text download failed - Text id not found.");
 			return;
 		} else if(responce.equals("passwrong")) {
 			System.out.println("Text download failed - Text password incorrect.");
 			return;
 		} else {
 			System.out.println("Text download failed - Invalid server responce.");
 			return;
 		}
 		
 		while(!serverOutput.ready());
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
 			toServer.println("sendtmptxt " + comm[2] + " " + comm[3] + " " + txt);
 		} else {
 			toServer.println("sendtxt " + comm[1] + " " + comm[2] + " " + txt);
 		}
 		
 		toServer.flush();
 		
 		System.out.println("Text sent. Waiting for conformation from server.");
 		
 		while(!serverOutput.ready());
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
 		while(!serverOutput.ready());
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