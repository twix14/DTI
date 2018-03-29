/**
 * BFT Map implementation (client side).
 *
 */
package intol.bftmap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.util.Map;

import bftsmart.tom.ServiceProxy;
import java.util.Collection;
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
        throw new UnsupportedOperationException("You are supposed to call this method :)");
    }

    @Override
    public V remove(Object key) {
        throw new UnsupportedOperationException("You are supposed to call this method :)");
    }

    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException("You are supposed to call this method :)");
    }

    @Override
    public boolean containsKey(Object key) {
        throw new UnsupportedOperationException("You are supposed to implement this method :)");
    }

    @Override
    public boolean isEmpty() {
        throw new UnsupportedOperationException("You are supposed to implement this method :)");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("You are supposed to implement this method :)");
    }

    @Override
    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException("You are supposed to implement this method :)");
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        throw new UnsupportedOperationException("You are supposed to implement this method :)");
    }

    @Override
    public Collection<V> values() {
        throw new UnsupportedOperationException("You are supposed to implement this method :)");
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        throw new UnsupportedOperationException("You are supposed to implement this method :)");
    }
}
