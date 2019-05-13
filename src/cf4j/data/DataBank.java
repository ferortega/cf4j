package cf4j.data;

import java.io.Serializable;
import java.util.HashMap;

public class DataBank implements Serializable {

    private HashMap<String, Object> map = new HashMap<String, Object>();

    /**
     * Deletes all content of this DataBank.
     */
    public void deleteAll (){ map.clear(); }

    /**
     * Delete a value associated to a single key.
     * @param key Key where the data is stored.
     * @return True, if the key was found.
     */
    public boolean deleteKey(String key){ return map.remove(key) != null; }

    /**
     * Find if an element exist inside the databank
     * @param key key to be searched.
     * @return True, if the element exist inside the databank.
     */
    public boolean hasKey(String key){
        return map.get(key) != null;
    }

    /**
     * Set or store a boolean inside this databank.
     * @param key key where the element will be stored.
     * @param value boolean value to be stored.
     * @return If an existing key is passed then the previous value gets returned. Null if it was not found.
     */
    public Boolean setBoolean(String key, boolean value){
        Object obj = map.put(key,value);
        return obj instanceof Boolean ? (Boolean)obj : null;
    }

    /**
     * Set or store a byte inside this databank.
     * @param key key where the element will be stored.
     * @param value byte value to be stored.
     * @return If an existing key is passed then the previous value gets returned. Null if it was not found.
     */
    public Byte setByte(String key, byte value){
        Object obj = map.put(key,value);
        return obj instanceof Byte ? (Byte)obj : null;
    }

    /**
     * Set or store a char inside this databank.
     * @param key key where the element will be stored.
     * @param value char value to be stored
     * @return If an existing key is passed then the previous value gets returned. Null if it was not found.
     */
    public Character setCharacter(String key, char value){
        Object obj = map.put(key,value);
        return obj instanceof Character ? (Character)obj : null;
    }

    /**
     * Set or store a byte inside this databank.
     * @param key key where the element will be stored.
     * @param value byte value to be stored.
     * @return If an existing key is passed then the previous value gets returned. Null if it was not found.
     */
    public Short setShort(String key, short value){
        Object obj = map.put(key,value);
        return obj instanceof Short ? (Short)obj : null;
    }

    /**
     * Set or store a int inside this databank.
     * @param key key where the element will be stored.
     * @param value int value to be stored.
     * @return If an existing key is passed then the previous value gets returned. Null if it was not found.
     */
    public Integer setInteger(String key, int value){
        Object obj = map.put(key,value);
        return obj instanceof Integer ? (Integer)obj : null;
    }

    /**
     * Set or store a long inside this databank.
     * @param key key where the element will be stored.
     * @param value long value to be stored.
     * @return If an existing key is passed then the previous value gets returned. Null if it was not found.
     */
    public Long setLong(String key, long value){
        Object obj = map.put(key,value);
        return obj instanceof Long ? (Long)obj : null;
    }

    /**
     * Set or store a float inside this databank.
     * @param key key where the element will be stored.
     * @param value float value to be stored.
     * @return If an existing key is passed then the previous value gets returned. Null if it was not found.
     */
    public Float setFloat(String key, float value){
        Object obj = map.put(key,value);
        return obj instanceof Float ? (Float)obj : null;
    }

    /**
     * Set or store a double inside this databank.
     * @param key key where the element will be stored.
     * @param value double value to be stored.
     * @return If an existing key is passed then the previous value gets returned. Null if it was not found.
     */
    public Double setDouble(String key, double value){
        Object obj = map.put(key,value);
        return obj instanceof Double ? (Double)obj : null;
    }

    /**
     * Get an stored boolean inside this databank.
     * @param key key where the element should be stored.
     * @return If an existing key is passed then the previous value is returned. Null if it was not found.
     */
    public Boolean getBoolean(String key){
        Object obj = map.get(key);
        return obj instanceof Boolean ? (Boolean)obj : null;
    }

    /**
     * Get an stored byte inside this databank.
     * @param key key where the element should be stored.
     * @return If an existing key is passed then the previous value is returned. Null if it was not found.
     */
    public Byte getByte(String key){
        Object obj = map.get(key);
        return obj instanceof Byte ? (Byte)obj : null;
    }

    /**
     * Get an stored char inside this databank.
     * @param key key where the element should be stored.
     * @return If an existing key is passed then the previous value is returned. Null if it was not found.
     */
    public Character getCharacter(String key){
        Object obj = map.get(key);
        return obj instanceof Character ? (Character)obj : null;
    }

    /**
     * Get an stored short inside this databank.
     * @param key key where the element should be stored.
     * @return If an existing key is passed then the previous value is returned. Null if it was not found.
     */
    public Short getShort(String key){
        Object obj = map.get(key);
        return obj instanceof Short ? (Short)obj : null;
    }

    /**
     * Get an stored int inside this databank.
     * @param key key where the element should be stored.
     * @return If an existing key is passed then the previous value is returned. Null if it was not found.
     */
    public Integer getInteger(String key){
        Object obj = map.get(key);
        return obj instanceof Integer ? (Integer)obj : null;
    }

    /**
     * Get an stored long inside this databank.
     * @param key key where the element should be stored.
     * @return If an existing key is passed then the previous value is returned. Null if it was not found.
     */
    public Long getLong(String key){
        Object obj = map.get(key);
        return obj instanceof Long ? (Long)obj : null;
    }

    /**
     * Get an stored float inside this databank.
     * @param key key where the element should be stored.
     * @return If an existing key is passed then the previous value is returned. Null if it was not found.
     */
    public Float getFloat(String key){
        Object obj = map.get(key);
        return obj instanceof Float ? (Float)obj : null;
    }

    /**
     * Get an stored double inside this databank.
     * @param key key where the element should be stored.
     * @return If an existing key is passed then the previous value is returned. Null if it was not found.
     */
    public Double getDouble(String key){
        Object obj = map.get(key);
        return obj instanceof Double ? (Double)obj : null;
    }
}
