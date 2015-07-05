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
        if (key instanceof RestJSONCacheObject)
            this.key = ((RestJSONCacheObject)key).getFields();
        else
            this.key = key;

        if (val instanceof RestJSONCacheObject)
            this.val = ((RestJSONCacheObject)val).getFields();
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
    public Object getValue() {
        return val;
    }

    /**
     * @param val Value.
     */
    public void setValue(Object val) {
        this.val = val;
    }
}
