package zk;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import bftmap.BFTMap;

public class Watcher implements Runnable {

	private List<NodeInfo<byte[]>> nodes;
	private BFTMap<String, byte[]> map;
	private BlockingQueue<NodeInfo<byte[]>> bq;
	private BlockingQueue<String> msgs;

	public Watcher(BFTMap<String, byte[]> map, BlockingQueue<NodeInfo<byte[]>> bq,
			BlockingQueue<String> msgs) {
		nodes = new LinkedList<>();
		this.map = map;
		this.bq = bq;
		this.msgs = msgs;
	}

	/**
	 * Every second checks for modifications in the node
	 */
	public void run() {
		try {
			while(!bq.isEmpty()) {

				nodes.add(bq.take());

			}

			for(int i = 0; i < nodes.size(); i++) {
				byte[] newNode = map.get(nodes.get(i).getName());
				NodeInfo<byte[]> node = new NodeInfo<byte[]>(newNode);
				if(newNode == null) {
					msgs.put("\nThe node " + nodes.get(i).getName() + " was removed, and the watcher was removed");
					nodes.remove(i);
				}else if(!(node.equalsN(nodes.get(i)))) {
					msgs.put("\nThe node " + node.getName() 
						+ " information has changed, and the watcher was removed");
					node.removeWatcher();
					map.remove(node.getName());
					map.put(node.getName(), node.toByteArray());
					nodes.remove(i);
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}
