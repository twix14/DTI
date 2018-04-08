/**
 * BFT Map implementation (server side).
 *
 */
package bftmap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.List;
import java.util.TreeMap;

import bftsmart.tom.MessageContext;
import bftsmart.tom.ServiceReplica;
import bftsmart.tom.server.defaultservices.DefaultSingleRecoverable;
import zk.NodeInfo;

public class BFTMapServer<K, V> extends DefaultSingleRecoverable {

	TreeMap<K, V> replicaMap = null;
	ServiceReplica replica = null;

	//The constructor passes the id of the server to the super class
	public BFTMapServer(int id) {

		replicaMap = new TreeMap<>();
		replica = new ServiceReplica(id, this, this);
	}

	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("Use: java BFTMapServer <server id>");
			System.exit(-1);
		}
		new BFTMapServer<Integer, String>(Integer.parseInt(args[0]));
	}

	@SuppressWarnings("unchecked")
	@Override
	public byte[] appExecuteOrdered(byte[] command, MessageContext msgCtx) {
		try {

			ByteArrayInputStream byteIn = new ByteArrayInputStream(command);
			ObjectInputStream objIn = new ObjectInputStream(byteIn);

			ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			ObjectOutputStream objOut = new ObjectOutputStream(byteOut);

			byte[] reply = new byte[0];
			BFTMapRequestType cmd = (BFTMapRequestType) objIn.readObject();

			System.out.println("Ordered execution of a "+cmd+" from "+msgCtx.getSender());
			NodeInfo<byte[]> node;
			switch (cmd) {
			//write operations on the map   

			case PUT:

				K key = (K) objIn.readObject();
				V value = (V) objIn.readObject();
				node = new NodeInfo<byte[]>((byte[])value);
				if(node.isEphemeral()) {
					node.setTimestamp(msgCtx.getTimestamp());
					System.out.println("Heartbeat received in node " + node.getName());
				}

				V ret = replicaMap.put(key, (V) node.toByteArray());
				
				if (ret != null) {
					objOut.writeObject(ret);
					reply = byteOut.toByteArray();
				}
				break;

			case REMOVE:

				K keyR = (K) objIn.readObject();

				node = new NodeInfo<byte[]>((byte[])replicaMap.get(keyR));
				V retR = null;
				if(node.isEphemeral()) {
					long current = msgCtx.getTimestamp();
					long last = node.getTimestamp();
					if(current - last < 2000) 
						retR = replicaMap.remove(keyR);
					else {
						removeNode((String)keyR);
						System.out.println("Node and children removed due "
								+ "to inactivity of the client");
					}
				}



				if (retR != null) {
					objOut.writeObject(retR);
					reply = byteOut.toByteArray();
				}
				break;

			case CLEAR:

				replicaMap.clear();

			case PUTALL:

				int size = objIn.readInt();
				for(int i = 0; i < size; i++) {
					K keyP = (K) objIn.readObject();
					V valueP = (V) objIn.readObject();
					replicaMap.put(keyP, valueP);
				}
				break;
			}

			objOut.flush();
			byteOut.flush();

			byteIn.close();
			objIn.close();
			objOut.close();
			byteOut.close();

			return reply;
		} catch (IOException | ClassNotFoundException ex) {
			Logger.getLogger(BFTMapServer.class.getName()).log(Level.SEVERE, null, ex);
			return new byte[0];
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public byte[] appExecuteUnordered(byte[] command, MessageContext msgCtx) {
		try {
			ByteArrayInputStream byteIn = new ByteArrayInputStream(command);
			ObjectInputStream objIn = new ObjectInputStream(byteIn);

			ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			ObjectOutputStream objOut = new ObjectOutputStream(byteOut);

			byte[] reply = new byte[0];
			BFTMapRequestType cmd = (BFTMapRequestType) objIn.readObject();

			System.out.println("Ordered execution of a "+cmd+" from "+msgCtx.getSender());
			K key;
			NodeInfo<byte[]> node;

			switch (cmd) {
			//read operations on the map

			case GET:
				key = (K) objIn.readObject();

				V ret = replicaMap.get(key);
				if(ret != null) {
					node = new NodeInfo<byte[]>((byte[])ret);
					if(node.isEphemeral()) {
						long current = msgCtx.getTimestamp();
						long last = node.getTimestamp();
						if(current - last < 2000) 
							ret = replicaMap.get(key);
						else {
							removeNode((String)key);
							System.out.println("Node and children removed due "
									+ "to inactivity of the client");
							ret = null;
						}
					}
					objOut.writeObject(ret);
					reply = byteOut.toByteArray();
				}

				break;

			case SIZE:
				objOut.writeObject(replicaMap.size());
				reply = byteOut.toByteArray();
				break;

			case KEYSET:
				objOut.writeObject(replicaMap.size());
				replicaMap.keySet().forEach(keyS -> {
					try {
						objOut.writeObject(keyS);
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
				reply = byteOut.toByteArray();
				break;
			case CONTAINSKEY:
				key = (K) objIn.readObject();
				objOut.writeObject(replicaMap.containsKey(key));
				reply = byteOut.toByteArray();
				break;

			case ENTRYSET:
				objOut.writeObject(replicaMap.size());
				replicaMap.entrySet().forEach(entry -> {
					try {
						objOut.writeObject(entry.getKey());
						objOut.writeObject(entry.getValue());

					} catch (IOException e) {
						e.printStackTrace();
					}
				});
				reply = byteOut.toByteArray();
				break;

			case ISEMPTY:
				objOut.writeBoolean(replicaMap.isEmpty());
				reply = byteOut.toByteArray();
				break;

			case CONSTAINSV:
				V val = (V) objIn.readObject();
				objOut.writeBoolean(replicaMap.containsValue(val));
				reply = byteOut.toByteArray();
				break;

			case VALUES:
				objOut.writeObject(replicaMap.size());
				replicaMap.values().forEach(val2 -> {
					try {
						objOut.writeObject(val2);
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
				reply = byteOut.toByteArray();
				break;
			}



			objOut.flush();
			byteOut.flush();

			byteIn.close();
			objIn.close();
			objOut.close();
			byteOut.close();

			return reply;
		} catch (IOException | ClassNotFoundException ex) {
			Logger.getLogger(BFTMapServer.class.getName()).log(Level.SEVERE, null, ex);
			return new byte[0];
		}
	}

	@Override
	public byte[] getSnapshot() {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutput out = new ObjectOutputStream(bos);
			out.writeObject(replicaMap);
			out.flush();
			bos.flush();
			out.close();
			bos.close();
			return bos.toByteArray();
		} catch (IOException ex) {
			ex.printStackTrace(); //debug instruction
			return new byte[0];
		}
	}

	@Override
	public void installSnapshot(byte[] state) {
		try {
			// serialize to byte array and return
			ByteArrayInputStream bis = new ByteArrayInputStream(state);
			ObjectInput in = new ObjectInputStream(bis);
			replicaMap = (TreeMap<K, V>) in.readObject();
			in.close();
			bis.close();
		} catch (ClassNotFoundException | IOException ex) {
			ex.printStackTrace(); //debug instruction
		}
	}
	
	private boolean removeNode(String path) {
		//remove nó da lista do pai
		int iparent = path.indexOf("/");
		String parent = null;
		NodeInfo<byte []> parentFI = null;

		if(path.indexOf("/") != -1) {
			parent = path.substring(0, iparent);

			parentFI = new NodeInfo<byte []>((byte[])replicaMap.get(parent));
			parentFI.removeChild(path);
		}

		//vai apagar os filhos recursivamente
		NodeInfo<byte []> node = new NodeInfo<byte []>((byte[])replicaMap.get(path));
		removeChildren(node, path);

		return iparent != -1 ? updateNode(parent, parentFI.toByteArray()) : true;
	}

	private void removeChildren(NodeInfo<byte []> node, String path) {
		List<String> children = node.getChildren();
		if(!children.isEmpty()) {
			for (String string : children) {
				removeChildren(new NodeInfo<byte[]>((byte[])replicaMap.get(string)), string);
				replicaMap.remove(path);
			}
		}else {
			replicaMap.remove(path);
		}
	}
	
	private boolean updateNode(String path, byte [] newData) {
		if(replicaMap.containsKey(path)) {
			replicaMap.remove(path);
			replicaMap.put((K)path, (V) newData);
			return true;
		}
		return false;
	}

}
