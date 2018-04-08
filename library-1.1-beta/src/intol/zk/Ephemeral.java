package zk;

import bftmap.BFTMap;

public class Ephemeral extends Thread {
	
	private String name;
	private BFTMap<String, byte[]> map;

	public Ephemeral(String name, BFTMap<String, byte[]> map) {
		this.name = name;
		this.map = map;
	}

	public void run() {
		
		NodeInfo<byte[]> node = new NodeInfo<byte[]>(map.get(name));
		//heartbeat
		map.put(name, node.toByteArray());
		
	}

}
