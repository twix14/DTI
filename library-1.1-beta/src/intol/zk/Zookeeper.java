package zk;

import java.util.List;

import bftmap.BFTMap;

public class Zookeeper {
	
	private BFTMap<String, byte []> map;

	public Zookeeper(BFTMap<String, byte []> map) {
		this.map = map;
	}
	
	public boolean createNode(String path, byte [] data, boolean seq, boolean ephe, boolean watch) {
		
		FolderInfo<byte []> fi = new FolderInfo<>(seq, ephe, watch, data);
		
		if(path.indexOf("/") != -1) {
			int iparent = path.lastIndexOf("/"); 
			String parent = path.substring(0, iparent);
			
			FolderInfo<byte []> parentFI = new FolderInfo<byte []>(map.get(parent));
			
			if(parentFI.isSequential()) {
				parentFI.incSeq();
				parentFI.addChild(path + "_" + parentFI.getSeq());
				map.put(path + "_" + parentFI.getSeq(), fi.toByteArray());
			} else {
				parentFI.addChild(path);
			}
			updateNode(parent, parentFI.toByteArray());
			System.out.println(getNode(path));
			System.out.println(getNode(parent));
			//TODO APAGAR ISTO
			return true;
		} else {
			map.put(path, fi.toByteArray());
		}
			
				
		return false;
	}
	
	public FolderInfo<byte[]> getNode(String path){
		byte[] info = map.get(path);
		return info != null ? new FolderInfo<byte[]>(map.get(path)) : null;
	}
	
	public boolean updateNode(String path, byte [] newData) {
		if(map.containsKey(path)) {
			map.remove(path);
			map.put(path, newData);
			//TODO MUDAR PARA UMA OPERACAO, REPLACE
			return true;
		}
		return false;
	}
	
	public boolean removeNode(String path) {
		//remove nó da lista do pai
		int iparent = path.indexOf("/");
		String parent = null;
		FolderInfo<byte []> parentFI = null;
		
		if(path.indexOf("/") != -1) {
			parent = path.substring(0, iparent);
			
			parentFI = new FolderInfo<byte []>(map.get(parent));
			parentFI.removeChild(path);
		}
		
		//vai apagar os filhos recursivamente
		FolderInfo<byte []> node = new FolderInfo<byte []>(map.get(path));
		removeChildren(node, path);
		
		return iparent != -1 ? updateNode(parent, parentFI.toByteArray()) : true;
	}
	
	private void removeChildren(FolderInfo<byte []> node, String path) {
		List<String> children = node.getChildren();
		if(!children.isEmpty()) {
			for (String string : children) {
				removeChildren(new FolderInfo<>(map.get(string)), string);
				map.remove(path);
			}
		}else {
			map.remove(path);
		}
	}

}
