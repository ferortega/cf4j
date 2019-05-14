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
    public Boolean setBoolean(String key, Boolean value){
        Object obj = map.put(key,value);
        return obj instanceof Boolean ? (Boolean)obj : null;
    }

    /**
     * Set or store a boolean array inside this databank.
     * @param key key where the element will be stored.
     * @param value boolean array to be stored.
     * @return If an existing key is passed then the previous value gets returned. Null if it was not found.
     */
    public Boolean[] setBooleanArray(String key, Boolean[] value){
        Object obj = map.put(key,value);
        return obj instanceof Boolean ? (Boolean[])obj : null;
    }

    /**
     * Set or store a int inside this databank.
     * @param key key where the element will be stored.
     * @param value int value to be stored.
     * @return If an existing key is passed then the previous value gets returned. Null if it was not found.
     */
    public Integer setInteger(String key, Integer value){
        Object obj = map.put(key,value);
        return obj instanceof Integer ? (Integer)obj : null;
    }

    /**
     * Set or store a int array inside this databank.
     * @param key key where the element will be stored.
     * @param value int array to be stored.
     * @return If an existing key is passed then the previous value gets returned. Null if it was not found.
     */
    public Integer [] setIntegerArray(String key, Integer [] value){
        Object obj = map.put(key,value);
        return obj instanceof Integer ? (Integer [])obj : null;
    }

    /**
     * Set or store a double inside this databank.
     * @param key key where the element will be stored.
     * @param value double value to be stored.
     * @return If an existing key is passed then the previous value gets returned. Null if it was not found.
     */
    public Double setDouble(String key, Double value){
        Object obj = map.put(key,value);
        return obj instanceof Double ? (Double)obj : null;
    }

    /**
     * Set or store a double array inside this databank.
     * @param key key where the element will be stored.
     * @param value double array to be stored.
     * @return If an existing key is passed then the previous value gets returned. Null if it was not found.
     */
    public Double[] setDoubleArray(String key, Double[] value){
        Object obj = map.put(key,value);
        return obj instanceof Double ? (Double[])obj : null;
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
     * Get an stored boolean array inside this databank.
     * @param key key where the element should be stored.
     * @return If an existing key is passed then the previous value is returned. Null if it was not found.
     */
    public Boolean [] getBooleanArray(String key){
        Object obj = map.get(key);
        return obj instanceof Boolean ? (Boolean [])obj : null;
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
     * Get an stored int array inside this databank.
     * @param key key where the element should be stored.
     * @return If an existing key is passed then the previous value is returned. Null if it was not found.
     */
    public Integer [] getIntegerArray(String key){
        Object obj = map.get(key);
        return obj instanceof Integer ? (Integer [])obj : null;
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

    /**
     * Get an stored double array inside this databank.
     * @param key key where the element should be stored.
     * @return If an existing key is passed then the previous value is returned. Null if it was not found.
     */
    public Double [] getDoubleArray(String key){
        Object obj = map.get(key);
        return obj instanceof Double ? (Double [])obj : null;
    }
}
