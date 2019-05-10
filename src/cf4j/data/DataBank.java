package cf4j.data;

import java.util.HashMap;

public class DataBank {

    private HashMap<String, Object> map = new HashMap<String, Object>();

    public void deleteAll (){ map.clear(); }

    public boolean deleteKey(String key){ return map.remove(key) != null; }

    public boolean hasKey(String key){
        return map.get(key) != null;
    }

    public boolean setBoolean(String key, boolean value){
        Object obj = map.put(key,value);
        return (obj instanceof Boolean ? (Boolean)obj : null);
    }

    public byte setByte(String key, byte value){
        Object obj = map.put(key,value);
        return (obj instanceof Byte ? (Byte)obj : null);
    }

    public char setCharacter(String key, char value){
        Object obj = map.put(key,value);
        return (obj instanceof Character ? (Character)obj : null);
    }

    public short setShort(String key, short value){
        Object obj = map.put(key,value);
        return (obj instanceof Short ? (Short)obj : null);
    }

    public int setInteger(String key, int value){
        Object obj = map.put(key,value);
        return (obj instanceof Integer ? (Integer)obj : null);
    }

    public long setLong(String key, long value){
        Object obj = map.put(key,value);
        return (obj instanceof Long ? (Long)obj : null);
    }

    public float setFloat(String key, float value){
        Object obj = map.put(key,value);
        return (obj instanceof Float ? (Float)obj : null);
    }

    public double setDouble(String key, double value){
        Object obj = map.put(key,value);
        return (obj instanceof Double ? (Double)obj : null);
    }

    public Boolean getBoolean(String key){
        Object obj = map.get(key);
        return (obj instanceof Boolean ? (Boolean)obj : null);
    }

    public Byte getByte(String key){
        Object obj = map.get(key);
        return (obj instanceof Byte ? (Byte)obj : null);
    }

    public Character getCharacter(String key){
        Object obj = map.get(key);
        return (obj instanceof Character ? (Character)obj : null);
    }

    public Short getShort(String key){
        Object obj = map.get(key);
        return (obj instanceof Short ? (Short)obj : null);
    }

    public Integer getInteger(String key){
        Object obj = map.get(key);
        return (obj instanceof Integer ? (Integer)obj : null);
    }

    public Long getLong(String key){
        Object obj = map.get(key);
        return (obj instanceof Long ? (Long)obj : null);
    }

    public Float getFloat(String key){
        Object obj = map.get(key);
        return (obj instanceof Float ? (Float)obj : null);
    }

    public Double getDouble(String key){
        Object obj = map.get(key);
        return (obj instanceof Double ? (Double)obj : null);
    }
}
