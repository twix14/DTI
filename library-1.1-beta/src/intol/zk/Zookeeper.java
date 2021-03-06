package zk;

import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import bftmap.BFTMap;

public class Zookeeper {

	private BFTMap<String, byte []> map;
	private BlockingQueue<NodeInfo<byte[]>> bq;

	public Zookeeper(BFTMap<String, byte []> map, BlockingQueue<String> msgs) {
		this.map = map;
		bq = new LinkedBlockingQueue<>();

		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		executor.scheduleAtFixedRate(new Watcher(map, bq, msgs), 0, 1, TimeUnit.SECONDS);
	}

	public boolean createNode(String path, byte [] data, boolean seq, boolean ephe) {

		if(map.get(path) != null)
			return false;

		NodeInfo<byte []> fi = new NodeInfo<>(path, seq, ephe, data);
		String pathSeq = null;
		NodeInfo<byte []> parentFI = null;

		if(path.indexOf("/") != -1) {
			int iparent = path.lastIndexOf("/"); 
			String parent = path.substring(0, iparent);
			byte[] p = map.get(parent);
			
			if(p == null)
				return false;

			parentFI = new NodeInfo<byte []>(p);

			if(parentFI.isSequential()) {
				parentFI.incSeq();
				parentFI.addChild(path + "_" + parentFI.getSeq());
				pathSeq = path + "_" + parentFI.getSeq();
				fi.setName(pathSeq);
				map.put(pathSeq, fi.toByteArray());
			} else {
				parentFI.addChild(path);
				map.put(path, fi.toByteArray());
			}

			updateNode(parent, parentFI.toByteArray());
		} else 
			map.put(path, fi.toByteArray());
		
		if(fi.isEphemeral()) {
			String pathN = parentFI != null && parentFI.isSequential()? pathSeq : path;
			
			Timer t = new Timer();
			TimerTask hearbeat = new TimerTask(){

				@Override
				public void run() {
					//heartbeat
					NodeInfo<byte[]> node = getNode(pathN);
					if(node != null) 
						map.put(pathN, getNode(pathN).toByteArray());
					else 
						t.cancel();
				}
			};
			
			t.schedule(hearbeat, 0, 1000);
		}

		return true;
	}

	public NodeInfo<byte[]> getNode(String path){
		byte[] info = map.get(path);
		return info != null ? new NodeInfo<byte[]>(map.get(path)) : null;
	}

	public boolean updateNode(String path, byte [] newData) {
		if(map.containsKey(path)) {
			map.put(path, newData);
			return true;
		}
		return false;
	}
	
	public boolean update(String path, byte[] newData) {
		byte[] data = map.get(path);
		
		if(data == null)
			return false;
		
		NodeInfo<byte[]> node = new NodeInfo<byte[]>(data);
		node.setData(newData);
		return updateNode(path, node.toByteArray());
	}

	public void addWatcher(String path) {
		NodeInfo<byte[]> node = getNode(path);
		node.setWatcher();
		updateNode(path, node.toByteArray());
		try {
			bq.put(node);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public boolean removeNode(String path) {
		//remove nó da lista do pai
		int iparent = path.indexOf("/");
		String parent = null;
		NodeInfo<byte []> parentFI = null;

		if(path.indexOf("/") != -1) {
			parent = path.substring(0, iparent);

			parentFI = new NodeInfo<byte []>(map.get(parent));
			parentFI.removeChild(path);
		}

		//vai apagar os filhos recursivamente
		NodeInfo<byte []> node = new NodeInfo<byte []>(map.get(path));
		removeChildren(node, path);

		return iparent != -1 ? updateNode(parent, parentFI.toByteArray()) : true;
	}

	private void removeChildren(NodeInfo<byte []> node, String path) {
		List<String> children = node.getChildren();
		if(!children.isEmpty()) {
			for (String string : children) {
				removeChildren(new NodeInfo<>(map.get(string)), string);
				map.remove(path);
			}
		}else {
			map.remove(path);
		}
	}

	public void printNameSpace() {
		System.out.println("---------- NAMESPACE ------------\n");
		List<NodeInfo<byte[]>> rootNodes = new LinkedList<>();
		List<String> rootNames = new LinkedList<>();
		map.keySet().forEach(node -> {
			if(!node.contains("/")) {
				rootNodes.add(getNode(node));
				rootNames.add(node);
			}
		});

		StringBuilder sb = new StringBuilder();

		for(int i = 0; i < rootNodes.size(); i++) {
			sb.append("Root Nº" + (i+1) + "\n");
			print(0, rootNames.get(i),rootNodes.get(i), sb);
			sb.append("\n\n");
		}

		System.out.print(sb.toString());

	}

	private void print(int indent, String parentN, NodeInfo<byte[]> parent,
			StringBuilder sb) {
		sb.append(getIndentString(indent));
		sb.append("+--");
		sb.append(parentN);
		sb.append("/");
		sb.append("\n");
		for (String child : parent.getChildren()) {
			NodeInfo<byte[]> childInfo = getNode(child);
			if(childInfo == null)
				return;
			if (!isLeaf(childInfo)) {
				print(indent + 1, child, childInfo, sb);
			} else {
				printLeaf(indent + 1, child, sb);
			}
		}
	}

	private void printLeaf(int indent, String parentN,
			StringBuilder sb) {
		sb.append(getIndentString(indent));
		sb.append("+--");
		sb.append(parentN);
		sb.append("\n");
	}

	private String getIndentString(int indent) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < indent; i++) {
			sb.append("|  ");
		}
		return sb.toString();
	}

	private boolean isLeaf(NodeInfo<byte[]> node) {
		return node.getChildren().isEmpty();
	}



}
