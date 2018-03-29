/**
 * BFT Map implementation (interactive client).
 *
 */
package bftmap;

import java.io.IOException;
import java.util.Scanner;
import java.util.Set;
import java.io.Console;

public class BFTMapInteractiveClient {

    public static void main(String[] args) throws IOException {

        int clientId = (args.length > 0) ? Integer.parseInt(args[0]) : 1001;
        BFTMap<Integer, String> bftMap = new BFTMap<>(clientId);

        Console console = System.console();
        Scanner sc = new Scanner(System.in);

        System.out.println("\nCommands:\n");
        System.out.println("\tPUT: Insert value into the map");
        System.out.println("\tGET: Retrieve value from the map");
        System.out.println("\tSIZE: Retrieve the size of the map");
        System.out.println("\tREMOVE: Removes the value associated with the supplied key");
        System.out.println("\tKEYSET: List all keys available in the table");
        System.out.println("\tEXIT: Terminate this client\n");

        while (true) {
            String cmd = console.readLine("\n  > ");

            if (cmd.equalsIgnoreCase("PUT")) {

                int key = 0;
                try {
                    key = Integer.parseInt(console.readLine("Enter a numeric key: "));
                } catch (NumberFormatException e) {
                    System.out.println("\tThe key is supposed to be an integer!\n");
                    continue;
                }
                String value = console.readLine("Enter an alpha-numeric value: ");

                //invokes the op on the servers
                bftMap.put(key, value);

                System.out.println("\nkey-value pair added to the map\n");
            } else if (cmd.equalsIgnoreCase("GET")) {

                int key = 0;
                try {
                    key = Integer.parseInt(console.readLine("Enter a numeric key: "));
                } catch (NumberFormatException e) {
                    System.out.println("\tThe key is supposed to be an integer!\n");
                    continue;
                }

                //invokes the op on the servers
                String value = bftMap.get(key);

                System.out.println("\nValue associated with " + key + ": " + value + "\n");

            } else if (cmd.equalsIgnoreCase("KEYSET")) {

                 //invokes the op on the servers
                 Set<Integer> keyset = bftMap.keySet();
                 
                 System.out.println("Set of keys:\n");
                 keyset.forEach(key -> System.out.println(key + "\n"));

            } else if (cmd.equalsIgnoreCase("REMOVE")) {

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


            } else if (cmd.equalsIgnoreCase("EXIT")) {

                System.out.println("\tEXIT: Bye bye!\n");
                System.exit(0);

            } else {
                System.out.println("\tInvalid command :P\n");
            }
        }
    }

}
