package zk;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.List;

public class FolderInfo<V> {

	private boolean sequential;
	private int seq;
	private boolean ephemeral;
	private List<String> children;
	private V data;
	private boolean watch;

	public FolderInfo(boolean sequential, boolean ephemeral, boolean watch, V data) {
		this.setSequential(sequential);
		this.setEphemeral(ephemeral);
		children = new LinkedList<>();
		this.data = data;
		this.watch = watch;
	}
	
	@SuppressWarnings("unchecked")
	public FolderInfo(byte[] byteArray) {
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

	        byteIn.close();
	        objIn.close();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}

        
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

			objOut.flush();
			byteOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return byteOut.toByteArray();
	}
	
	@Override
	public String toString() {
		return "FolderInfo [sequential=" + sequential + ", seq=" + seq + ", ephemeral=" + ephemeral + ", children="
				+ children + ", data=" + data + ", watch=" + watch + "]";
	}
	

}
