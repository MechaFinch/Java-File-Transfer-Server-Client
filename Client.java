/*Alex Pickering
 *3/26/2018
 *
 *Simple Database Client
 */
 
 import java.io.*;
 import java.net.*;
 
 public class Client{
 	static boolean connected = false;
 	static Socket server;
 	static String[] comm;
 	static BufferedReader serverOutput;
 	static PrintWriter toServer;
 	
 	public static void main(String[] args) throws IOException{
 		String input = "";
 		while(true){
 			input = AR.readString("Input a command or 'help' for a list of commands.", "Please input a command");
 			
 			//Parse
 			comm = input.split(" ");
 			
 			if(comm[0].equalsIgnoreCase("connect")){
 				if(comm.length != 3){
 					System.out.println("Invalid use! Must be 'connect <ip> <port>'!");
 				}
 				try{
 					connect();
 				} catch(UnknownHostException e){
 					System.out.println("Unknown Host");
 				}
 			}
 		}
 	}
 	
 	static void connect() throws UnknownHostException, IOException{
 		try{
 			server = new Socket(InetAddress.getByName(comm[1]), Integer.parseInt(comm[2]));
 		} catch(NumberFormatException e){
 			System.out.println("Invalid use! Must be 'connect <port>'!");
 		}
 		
 		serverOutput = new BufferedReader(new InputStreamReader(server.getInputStream()));
 		toServer = new PrintWriter(server.getOutputStream(), true);
 		
 		connected = true;
 		
 		//Confirm connection
 		toServer.println("verifyConnection");
 		if(!serverOutput.readLine().equals("connectionVerify")){
 			System.out.println("Connection to port " + server.getPort() + "failed!");
 			connected = false;
 		}
 	}
 }