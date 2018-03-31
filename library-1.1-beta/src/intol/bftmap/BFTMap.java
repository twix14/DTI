/**
 * BFT Map implementation (client side).
 *
 */
package bftmap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.util.Map;

import bftsmart.tom.ServiceProxy;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class BFTMap<K, V> implements Map<K, V> {

    ServiceProxy serviceProxy;

    public BFTMap(int id) {
        serviceProxy = new ServiceProxy(id);
    }

    /**
     *
     * @param key The key associated to the value
     * @return value The value previously added to the map
     */
    @Override
    public V get(Object key) {
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream objOut = new ObjectOutputStream(byteOut);

            objOut.writeObject(BFTMapRequestType.GET);
            objOut.writeObject(key);

            objOut.flush();
            byteOut.flush();

            //invokes BFT-SMaRt
            byte[] rep = serviceProxy.invokeUnordered(byteOut.toByteArray());

            objOut.close();
            byteOut.close();

            if (rep.length == 0) {
                return null;
            }

            ByteArrayInputStream byteIn = new ByteArrayInputStream(rep);
            ObjectInputStream objIn = new ObjectInputStream(byteIn);

            @SuppressWarnings("unchecked")
			V val = (V) objIn.readObject();

            objIn.close();
            byteIn.close();

            return val;
        } catch (ClassNotFoundException | IOException ex) {
            return null;
        }
    }

    /**
     *
     * @param key The key associated to the value
     * @param value Value to be added to the map
     */
    @Override
    public V put(K key, V value) {
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
            objOut.writeObject(BFTMapRequestType.PUT);
            objOut.writeObject(key);
            objOut.writeObject(value);

            objOut.flush();
            byteOut.flush();

            byte[] rep = serviceProxy.invokeOrdered(byteOut.toByteArray());

            if (rep.length == 0) {
                return null;
            }

            objOut.close();
            byteOut.close();

            ByteArrayInputStream byteIn = new ByteArrayInputStream(rep);
            ObjectInputStream objIn = new ObjectInputStream(byteIn);
            @SuppressWarnings("unchecked")
			V val = (V) objIn.readObject();

            byteIn.close();
            objIn.close();

            return val;
        } catch (ClassNotFoundException | IOException ex) {
            return null;
        }
    }

    @Override
    public int size() {
    	try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream objOut = new ObjectOutputStream(byteOut);

            objOut.writeObject(BFTMapRequestType.SIZE);

            objOut.flush();
            byteOut.flush();

            //invokes BFT-SMaRt
            byte[] rep = serviceProxy.invokeUnordered(byteOut.toByteArray());

            objOut.close();
            byteOut.close();

            ByteArrayInputStream byteIn = new ByteArrayInputStream(rep);
            ObjectInputStream objIn = new ObjectInputStream(byteIn);

            int size = (int) objIn.readObject();

            objIn.close();
            byteIn.close();

            return size;
        } catch (ClassNotFoundException | IOException ex) {
            return -1;
        }
    }

    @Override
    public V remove(Object key) {
    	try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
            objOut.writeObject(BFTMapRequestType.REMOVE);
            objOut.writeObject(key);

            objOut.flush();
            byteOut.flush();

            byte[] rep = serviceProxy.invokeOrdered(byteOut.toByteArray());

            if (rep.length == 0) {
                return null;
            }

            objOut.close();
            byteOut.close();

            ByteArrayInputStream byteIn = new ByteArrayInputStream(rep);
            ObjectInputStream objIn = new ObjectInputStream(byteIn);
            @SuppressWarnings("unchecked")
			V val = (V) objIn.readObject();

            byteIn.close();
            objIn.close();

            return val;
        } catch (ClassNotFoundException | IOException ex) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
	@Override
    public Set<K> keySet() {
    	try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream objOut = new ObjectOutputStream(byteOut);

            objOut.writeObject(BFTMapRequestType.KEYSET);

            objOut.flush();
            byteOut.flush();

            //invokes BFT-SMaRt
            byte[] rep = serviceProxy.invokeUnordered(byteOut.toByteArray());

            objOut.close();
            byteOut.close();

            ByteArrayInputStream byteIn = new ByteArrayInputStream(rep);
            ObjectInputStream objIn = new ObjectInputStream(byteIn);

			int keysetSize = (int) objIn.readObject();
            
            Set<K> keySet = new HashSet<K>();
            for(int i = 0; i < keysetSize; i++) {
            	K key = (K) objIn.readObject();
            	keySet.add(key);
            }

            objIn.close();
            byteIn.close();

            return keySet;
            
        } catch (ClassNotFoundException | IOException ex) {
            ex.printStackTrace();
            return Collections.emptySet();
        }
    }

    @Override
    public boolean containsKey(Object key) {
        throw new UnsupportedOperationException("You are supposed to implement this method :)");
    }

    @Override
    public boolean isEmpty() {
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
            objOut.writeObject(BFTMapRequestType.ISEMPTY);

            objOut.flush();
            byteOut.flush();

            byte[] rep = serviceProxy.invokeUnordered(byteOut.toByteArray());

            if (rep.length == 0) {
                return false;
            }

            objOut.close();
            byteOut.close();

            ByteArrayInputStream byteIn = new ByteArrayInputStream(rep);
            ObjectInputStream objIn = new ObjectInputStream(byteIn);
            @SuppressWarnings("unchecked")
			boolean val = objIn.readBoolean();

            byteIn.close();
            objIn.close();

            return val;
        } catch (IOException ex) {
            return false;
        }
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("You are supposed to implement this method :)");
    }

    @Override
    public boolean containsValue(Object value) {
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
            objOut.writeObject(BFTMapRequestType.CONSTAINSV);
            objOut.writeObject(value);

            objOut.flush();
            byteOut.flush();

            byte[] rep = serviceProxy.invokeUnordered(byteOut.toByteArray());

            if (rep.length == 0) {
                return false;
            }

            objOut.close();
            byteOut.close();

            ByteArrayInputStream byteIn = new ByteArrayInputStream(rep);
            ObjectInputStream objIn = new ObjectInputStream(byteIn);
            @SuppressWarnings("unchecked")
            boolean val = objIn.readBoolean();

            byteIn.close();
            objIn.close();

            return val;
        } catch (IOException ex) {
            return false;
        }
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        throw new UnsupportedOperationException("You are supposed to implement this method :)");
    }

    @Override
    public Collection<V> values() {
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
            objOut.writeObject(BFTMapRequestType.VALUES);

            objOut.flush();
            byteOut.flush();

            byte[] rep = serviceProxy.invokeUnordered(byteOut.toByteArray());

            if (rep.length == 0) {
                return null;
            }

            objOut.close();
            byteOut.close();

            ByteArrayInputStream byteIn = new ByteArrayInputStream(rep);
            ObjectInputStream objIn = new ObjectInputStream(byteIn);
            @SuppressWarnings("unchecked")
            Collection<V> val = (Collection<V>) objIn.readObject();

            byteIn.close();
            objIn.close();

            return val;
        } catch (ClassNotFoundException | IOException ex) {
            return null;
        }
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        throw new UnsupportedOperationException("You are supposed to implement this method :)");
    }
}
