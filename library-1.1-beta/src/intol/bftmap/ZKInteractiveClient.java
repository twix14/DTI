package bftmap;

import java.io.Console;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import zk.NodeInfo;
import zk.Zookeeper;

public class ZKInteractiveClient {

	public static void main(String[] args) {
		int clientId = (args.length > 0) ? Integer.parseInt(args[0]) : 1001;
		Zookeeper zk = new Zookeeper(new BFTMap<>(clientId));
		
		Console console = System.console();
        Scanner sc = new Scanner(System.in);

        System.out.println("\nCommands:\n");
        System.out.println("\tCREATE: CREATES A NODE IN THE ZOOKEEPER NAMESPACE");
        System.out.println("\tTO CREATE A NODE USE THE '/' SLASH TO DEFINE THE PATH\n");
        System.out.println("\tGET: SHOWS THE NODEINFORMATION PRESENT IN ZOOKEEPER");
        System.out.println("\tREMOVE: REMOVES THE NODE FROM ZOOKEEPER");
        System.out.println("\tPRINT: PRINTS ALL THE NAMESPACE");
        System.out.println("\tEXIT: TERMINATE THIS CLIENT\n");

        while (true) {
            String cmd = console.readLine("\n  > ");

            if (cmd.equalsIgnoreCase("create")) {
                String path = console.readLine("Path: ");
                String data = console.readLine("Data: ");
                boolean eph = console.readLine("Ephemeral (Y/N): ").equalsIgnoreCase("y");
                boolean seq = console.readLine("Sequential (Y/N): ").equalsIgnoreCase("y");
                boolean watch = console.readLine("Watcher (Y/N): ").equalsIgnoreCase("y");

                System.out.println(zk.createNode(path, data.getBytes(), seq, eph, watch)? "Node added to Zookeeper" :
                	"The node already exists or there was a problem");
            } else if (cmd.equalsIgnoreCase("remove")) {

            	 String path = console.readLine("Path: ");
            	 System.out.println(zk.removeNode(path) ? "Node was removed successfully" : 
            		 "The node doesn't exist or there was a problem");

            } else if (cmd.equalsIgnoreCase("get")) {

            	String path = console.readLine("Ponha o path: ");
            	NodeInfo<byte[]> node = zk.getNode(path);
            	System.out.println(node != null ? node : "The node doesn't exist or there was a problem");

            } else if (cmd.equalsIgnoreCase("print")) {
            	
            	zk.printNameSpace();
            	
            }	/*} else if (cmd.equalsIgnoreCase("REMOVE")) {
            

            	int key = 0;
                try {
                    key = Integer.parseInt(console.readLine("Enter a numeric key: "));
                } catch (NumberFormatException e) {
                    System.out.println("\tThe key is supposed to be an integer!\n");
                    continue;
                }
                
                //invokes the op on the servers
                String value = bftMap.remove(key);

                System.out.println("\nValue ("+ value + ") associated with " + key 
                		+ " has been removed" + "\n");

            } else if (cmd.equalsIgnoreCase("SIZE")) {

                //invokes the op on the servers
                int size = bftMap.size();

                System.out.println("\nThe size of the key value store is " + size + "\n");


            } else if (cmd.equalsIgnoreCase("CONTAINSKEY")) {
            	
            	int key = 0;
                try {
                    key = Integer.parseInt(console.readLine("Enter a numeric key: "));
                } catch (NumberFormatException e) {
                    System.out.println("\tThe key is supposed to be an integer!\n");
                    continue;
                }
                
                //invokes the op on the servers
                System.out.println("The key " + key + (bftMap.containsKey(key)? "exists" : "does"
                		+ "not exist")); 
            	
            } else if (cmd.equalsIgnoreCase("CLEAR")) {
            	
            	bftMap.clear();
            	System.out.println("The keystore is now clear, do a keyset or another search"
            			+ " operation to see if the clean operation was done successfully");
            	
            } else if (cmd.equalsIgnoreCase("PUTALL")) {
            	
            	int entries = 0;
                try {
                    entries = Integer.parseInt(console.readLine("Number of entries to add: "));
                } catch (NumberFormatException e) {
                    System.out.println("\tThe number of entries is supposed to be an integer!\n");
                    continue;
                }
                
                Map<Integer, String> m = new TreeMap<Integer, String>();
                for(int i = 0; i < entries; i++) {
                	int key = 0;
                    try {
                        key = Integer.parseInt(console.readLine("Enter a numeric key: "));
                    } catch (NumberFormatException e) {
                        System.out.println("\tThe key is supposed to be an integer!\n");
                        continue;
                    }
                    String value = console.readLine("Enter an alpha-numeric value: ");

                    //invokes the op on the servers
                    m.put(key, value);
                }
                
                bftMap.putAll(m);
            	
            	
        	} else if (cmd.equalsIgnoreCase("ENTRYSET")) {
        		
        		//invokes the op on the servers
                Set<Entry<Integer, String>> entrySet = bftMap.entrySet();
                
                System.out.println("Set of entries:\n");
                entrySet.forEach(entry -> System.out.println(entry + "\n"));
        		
        	}*/ else  if (cmd.equalsIgnoreCase("EXIT")) {
        		
                System.out.println("\tEXIT: Bye bye!\n");
                System.exit(0);

            } else {
                System.out.println("\tInvalid command :P\n");
            }
        }

	}

}
