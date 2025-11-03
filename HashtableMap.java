import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Implementation of a hash table that uses chaining for collision resolution
 */
public class HashtableMap<KeyType, ValueType> implements MapADT<KeyType, ValueType> {
    
    // Inner class to store key-value pairs
    protected class Pair {
        public KeyType key;
        public ValueType value;
        
        public Pair(KeyType key, ValueType value) {
            this.key = key;
            this.value = value;
        }
    }
    
    // Instance field to store our key-value pairs
    protected LinkedList<Pair>[] table;
    private int size; // Number of key-value pairs stored
    private static final double LOAD_FACTOR_THRESHOLD = 0.8;
    
    /**
     * Constructor with specified capacity
     * @param capacity the initial capacity of the hash table
     */
    @SuppressWarnings("unchecked")
    public HashtableMap(int capacity) {
        // Create array of raw type and cast to generic type
        table = (LinkedList<Pair>[]) new LinkedList[capacity];
        size = 0;
        
        // Initialize all LinkedList slots to empty lists
        for (int i = 0; i < capacity; i++) {
            table[i] = new LinkedList<>();
        }
    }
    
    /**
     * Default constructor with capacity of 64
     */
    public HashtableMap() {
        this(64);
    }
    
    /**
     * Adds a new key-value pair to the hash table
     * @param key the key to add
     * @param value the value associated with the key
     * @throws IllegalArgumentException if the key already exists
     * @throws NullPointerException if the key is null
     */
    @Override
    public void put(KeyType key, ValueType value) throws IllegalArgumentException {
        if (key == null) {
            throw new NullPointerException("Key cannot be null");
        }
        
        if (containsKey(key)) {
            throw new IllegalArgumentException("Key already exists in the hash table");
        }
        
        // Check if we need to resize before adding the new element
        if ((double) (size + 1) / table.length >= LOAD_FACTOR_THRESHOLD) {
            resize();
        }
        
        int index = getIndexForKey(key);
        table[index].add(new Pair(key, value));
        size++;
    }

    /**
     * Check if the hash table contains the specified key
     * @param key the key to check
     * @return true if the key exists, false otherwise
     */
    @Override
    public boolean containsKey(KeyType key) {
        if (key == null) {
            return false;
        }
        
        int index = getIndexForKey(key);
        for (Pair pair : table[index]) {
            if (pair.key.equals(key)) {
                return true;
            }
        }
        return false;
    }
    
    public List<KeyType> getKeys() {
        List<KeyType> keys = new LinkedList<>();
        for (LinkedList<Pair> bucket : table) {
            for (Pair pair : bucket) {
                keys.add(pair.key);
            }
        }
        return keys;
    }

    /**
     * Retrieve the value associated with the specified key
     * @param key the key to look up
     * @return the value associated with the key
     * @throws NoSuchElementException if the key doesn't exist
     */
    @Override
    public ValueType get(KeyType key) throws NoSuchElementException {
        if (key == null) {
            throw new NoSuchElementException("Key cannot be null");
        }
        
        int index = getIndexForKey(key);
        for (Pair pair : table[index]) {
            if (pair.key.equals(key)) {
                return pair.value;
            }
        }
        
        throw new NoSuchElementException("Key not found: " + key);
    }

    /**
     * Remove the key-value pair with the specified key
     * @param key the key to remove
     * @return the value associated with the removed key
     * @throws NoSuchElementException if the key doesn't exist
     */
    @Override
    public ValueType remove(KeyType key) throws NoSuchElementException {
        if (key == null) {
            throw new NoSuchElementException("Key cannot be null");
        }
        
        int index = getIndexForKey(key);
        LinkedList<Pair> list = table[index];
        
        for (int i = 0; i < list.size(); i++) {
            Pair pair = list.get(i);
            if (pair.key.equals(key)) {
                ValueType value = pair.value;
                list.remove(i);
                size--;
                return value;
            }
        }
        
        throw new NoSuchElementException("Key not found: " + key);
    }

    /**
     * Remove all key-value pairs from the hash table
     */
    @Override
    public void clear() {
        for (int i = 0; i < table.length; i++) {
            table[i].clear();
        }
        size = 0;
    }

    /**
     * Get the number of key-value pairs in the hash table
     * @return the number of key-value pairs
     */
    @Override
    public int getSize() {
        return size;
    }

    /**
     * Get the capacity of the hash table
     * @return the capacity of the hash table
     */
    @Override
    public int getCapacity() {
        return table.length;
    }
    
    /**
     * Helper method to calculate the index for a key
     * @param key the key to hash
     * @return the index in the table array
     */
    private int getIndexForKey(KeyType key) {
        // Calculate index using absolute value to handle negative hash codes
        return Math.abs(key.hashCode()) % table.length;
    }
    
    /**
     * Resize the hash table by doubling its capacity
     */
    @SuppressWarnings("unchecked")
    private void resize() {
        int newCapacity = table.length * 2;
        LinkedList<Pair>[] oldTable = table;
        
        // Create new table with doubled capacity
        table = (LinkedList<Pair>[]) new LinkedList[newCapacity];
        for (int i = 0; i < newCapacity; i++) {
            table[i] = new LinkedList<>();
        }
        
        // Reset size as we'll re-add all elements
        size = 0;
        
        // Rehash all elements from old table to new table
        for (LinkedList<Pair> list : oldTable) {
            for (Pair pair : list) {
                // Use put without checking load factor to avoid recursive resizing
                int newIndex = getIndexForKey(pair.key);
                table[newIndex].add(pair);
                size++;
            }
        }
    }
    
    
    // JUnit tests
    
    /**
     * Tests adding a key-value pair to the hash table and retrieving it
     */
    @Test
    public void testPutAndGet() {
        HashtableMap<String, Integer> map = new HashtableMap<>();
        map.put("test", 123);
        
        // Check if the value is correctly stored
        assertEquals(123, map.get("test"));
        assertEquals(1, map.getSize());
    }
    
    /**
     * Tests checking if a key exists in the hash table
     */
    @Test
    public void testContainsKey() {
        HashtableMap<String, Integer> map = new HashtableMap<>();
        map.put("exists", 1);
        
        // Check if containsKey correctly identifies existing and non-existing keys
        assertTrue(map.containsKey("exists"));
        assertFalse(map.containsKey("notExists"));
    }
    
    /**
     * Tests removing a key-value pair from the hash table
     */
    @Test
    public void testRemove() {
        HashtableMap<String, Integer> map = new HashtableMap<>();
        map.put("remove", 456);
        
        // Check if the removed value is correct and the key no longer exists
        assertEquals(456, map.remove("remove"));
        assertEquals(0, map.getSize());
        assertFalse(map.containsKey("remove"));
    }
    
    /**
     * Tests handling collisions by adding multiple keys that hash to the same index
     */
    @Test
    public void testCollisionHandling() {
        // Create a map with a very small capacity to force collisions
        HashtableMap<Integer, String> map = new HashtableMap<>(2);
        
        // These keys will likely hash to the same index
        map.put(0, "zero");
        map.put(2, "two");
        map.put(4, "four");
        
        // Check if all values are correctly stored despite collisions
        assertEquals(3, map.getSize());
        assertEquals("zero", map.get(0));
        assertEquals("two", map.get(2));
        assertEquals("four", map.get(4));
    }
    
    /**
     * Tests clearing all key-value pairs from the hash table
     */
    @Test
    public void testClear() {
        HashtableMap<String, Integer> map = new HashtableMap<>();
        map.put("key1", 1);
        map.put("key2", 2);
        
        // Check if clear removes all entries
        map.clear();
        assertEquals(0, map.getSize());
        assertFalse(map.containsKey("key1"));
        assertFalse(map.containsKey("key2"));
    }
    
    
}