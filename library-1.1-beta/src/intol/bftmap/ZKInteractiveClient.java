package bftmap;

import java.io.Console;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import zk.NodeInfo;
import zk.Zookeeper;

public class ZKInteractiveClient {

	public static void main(String[] args) {
		int clientId = (args.length > 0) ? Integer.parseInt(args[0]) : 1001;
		BlockingQueue<String> msgs = new LinkedBlockingQueue<>();
		Zookeeper zk = new Zookeeper(new BFTMap<>(clientId), msgs);

		Console console = System.console();

		/*ExecutorService ex = Executors.newSingleThreadExecutor();

		Runnable task = () -> {
			while(true) {
				while(!msgs.isEmpty())
					try {
						System.out.println(msgs.take() + "\n");
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
			}
		};

		ex.execute(task);*/

		System.out.println("\nCommands:\n");
		System.out.println("\tCREATE: CREATES A NODE IN THE ZOOKEEPER NAMESPACE");
		System.out.println("\tTO CREATE A NODE USE THE '/' SLASH TO DEFINE THE PATH\n");
		System.out.println("\tGET: SHOWS THE NODEINFORMATION PRESENT IN ZOOKEEPER");
		System.out.println("\tREMOVE: REMOVES THE NODE FROM ZOOKEEPER");
		System.out.println("\tPRINT: PRINTS ALL THE NAMESPACE");
		System.out.println("\tWATCHER: PLACES A WATCHER ON THE NODE");
		System.out.println("\tUPDATE: UPDATES THE DATA OF A NODE");
		System.out.println("\tEXIT: TERMINATE THIS CLIENT\n");


		while (true) {
			while(!msgs.isEmpty()) {
				try {
					System.out.println(msgs.take() + "\n");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			String cmd = console.readLine("\n  > ");

			if (cmd.equalsIgnoreCase("create")) {
				String path = console.readLine("Path: ");
				String data = console.readLine("Data: ");
				boolean eph = console.readLine("Ephemeral (Y/N): ").equalsIgnoreCase("y");
				boolean seq = console.readLine("Sequential (Y/N): ").equalsIgnoreCase("y");

				System.out.println(zk.createNode(path, data.getBytes(), seq, eph)? "Node added to Zookeeper" :
						"The node already exists or there was a problem");
			} else if (cmd.equalsIgnoreCase("remove")) {

				String path = console.readLine("Path: ");
				System.out.println(zk.removeNode(path) ? "Node was removed successfully" : 
						"The node doesn't exist or there was a problem");

			} else if (cmd.equalsIgnoreCase("get")) {

				String path = console.readLine("Path: ");
				NodeInfo<byte[]> node = zk.getNode(path);
				System.out.println(node != null ? node : "The node doesn't exist or there was a problem");

			} else if (cmd.equalsIgnoreCase("print")) {

				zk.printNameSpace();

			} else if (cmd.equalsIgnoreCase("watcher")) {

				String path = console.readLine("Path: ");
				zk.addWatcher(path);
			} else if (cmd.equalsIgnoreCase("update")) {

				String path = console.readLine("Path: ");
				String data = console.readLine("Data: ");
				System.out.println(zk.update(path, data.getBytes())? "The data was updated successfully" :
					"The node doesn't exist or there was a problem");
				
			} else  if (cmd.equalsIgnoreCase("EXIT")) {

        		System.out.println("\tEXIT: Bye bye!\n");
        		System.exit(0);

        	} else {

        		System.out.println("\tInvalid command :P\n");
        	}

		}

	}

}
