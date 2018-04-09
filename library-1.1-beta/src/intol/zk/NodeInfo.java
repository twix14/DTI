package zk;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.List;

public class NodeInfo<V> {

	@Override
	public String toString() {
		return "NodeInfo [sequential=" + sequential + ", seq=" + seq + ", ephemeral=" + ephemeral + ", children="
				+ children + ", data=" + new String((byte[])data) + "], watch=" + watch + ", name=" + name + ", timestamp=" + timestamp
				+ "]";
	}

	private boolean sequential;
	private int seq;
	private boolean ephemeral;
	private List<String> children;
	private V data;
	private boolean watch;
	private String name;
	private long timestamp;

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getName() {
		return name;
	}

	public NodeInfo(String name, boolean sequential, boolean ephemeral, V data) {
		this.setSequential(sequential);
		this.setEphemeral(ephemeral);
		children = new LinkedList<>();
		this.data = data;
		this.name = name;
	}
	
	@SuppressWarnings("unchecked")
	public NodeInfo(byte[] byteArray) {
		
		ByteArrayInputStream byteIn = new ByteArrayInputStream(byteArray);
        ObjectInputStream objIn;
		try {
			objIn = new ObjectInputStream(byteIn);

	        this.sequential = objIn.readBoolean();
	        this.seq = objIn.readInt();
	        this.ephemeral = objIn.readBoolean();
	        int size = objIn.readInt();
	        children = new LinkedList<>();
	        for(int i = 0; i < size; i++) {
	        	children.add((String) objIn.readObject());
	        }
	        this.watch = objIn.readBoolean();
	        data = (V) objIn.readObject();
	        this.name = (String) objIn.readObject();
	        this.timestamp = objIn.readLong();

	        byteIn.close();
	        objIn.close();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}

        
	}
	
	public void setWatcher() {
		watch = true;
	}
	
	public void removeWatcher() {
		watch = false;
	}
	
	public boolean getWatcher() {
		return watch;
	}
	
	public List<String> getChildren(){
		return this.children;
	}

	public void addChild(String child) {
		children.add(child);
	}

	public void removeChild(String child) {
		children.remove(child);
	}

	public boolean isSequential() {
		return sequential;
	}

	public void setSequential(boolean sequential) {
		this.sequential = sequential;
	}

	public boolean isEphemeral() {
		return ephemeral;
	}

	public void setEphemeral(boolean ephemeral) {
		this.ephemeral = ephemeral;
	}

	public int getSeq() {
		return seq;
	}

	public void incSeq() {
		seq++;
	}

	public byte[] toByteArray() {
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		try {
			
			ObjectOutputStream objOut = new ObjectOutputStream(byteOut);

			objOut.writeBoolean(sequential);
			objOut.writeInt(seq);
			objOut.writeBoolean(ephemeral);
			objOut.writeInt(children.size());
			children.forEach(child -> {
				try {
					objOut.writeObject(child);
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			objOut.writeBoolean(watch);
			objOut.writeObject(data);
			objOut.writeObject(name);
			objOut.writeLong(timestamp);

			objOut.flush();
			byteOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return byteOut.toByteArray();
	}

	public boolean equalsN(NodeInfo<V> other) {
		if (children == null) {
			if (other.children != null)
				return false;
		} else if (children.size() != other.children.size())
			return false;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!new String((byte[])data).equals(new String((byte[])other.data)))
			return false;
		return true;
	}
	
	public V getData() {
		return data;
	}

	public void setData(V data) {
		this.data = data;
	}
	
	public void setName(String name) {
		this.name = name;
	}

}
