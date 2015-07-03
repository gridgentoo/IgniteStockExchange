package org.apache.ignite.internal.processors.rest.handlers.scripting;


/**
 * Rest entry.
 */
public class RestEntry {
    /** Key. */
    private Object key;

    /** Value. */
    private Object val;

    /**
     * @param key Key.
     * @param val Value.
     */
    public RestEntry(Object key, Object val) {
        if (key instanceof JSONCacheObject)
            this.key = ((JSONCacheObject)key).getFields();
        else
            this.key = key;

        if (val instanceof JSONCacheObject)
            this.val = ((JSONCacheObject)val).getFields();
        else
            this.val = val;
    }

    /**
     * @return Key.
     */
    public Object getKey() {
        return key;
    }

    /**
     * @param key Key.
     */
    public void setKey(Object key) {
        this.key = key;
    }

    /**
     * @return Value.
     */
    public Object getVal() {
        return val;
    }

    /**
     * @param val Value.
     */
    public void setVal(Object val) {
        this.val = val;
    }
}
