package com.guokr.simbase;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.guokr.simbase.errors.SimContextException;
import com.guokr.simbase.errors.engine.SimEngineException;

public class SimContext extends HashMap<String, Object> {

    private static final long serialVersionUID = -8288998975274604087L;

    protected String          type;
    protected SimContext      defaults;

    public SimContext() {
        super();
    }

    public SimContext(Map<String, Object> raw) {
        super(raw);
    }

    public SimContext(String type, SimContext defaults, Map<String, Object> raw) {
        super(raw);
        this.type = type;
        this.defaults = defaults;
    }

    @SuppressWarnings("unchecked")
    public int getInt(String... keys) {
        Integer result = null;
        Map<String, Object> intermedia = this;
        int idx = keys.length;
        for (String key : keys) {
            if (idx > 1) {
                try {
                    intermedia = (Map<String, Object>) intermedia.get(key);
                } catch (ClassCastException e) {
                    intermedia = null;
                }
            } else {
                try {
                    result = (Integer) intermedia.get(key);
                } catch (ClassCastException e) {
                    result = null;
                }
            }
            idx--;
        }
        if (result != null) {
            return result.intValue();
        } else if (defaults != null) {
            return defaults.getSub(type, type).getInt(keys);
        } else {
            throw new SimContextException("no default int value found for keys" + Arrays.asList(keys) + " in type[" + type + "]");
        }
    }

    @SuppressWarnings("unchecked")
    public int[] getIntArray(String... keys) {
        int[] result = null;
        Map<String, Object> intermedia = this;
        int idx = keys.length;
        for (String key : keys) {
            if (idx > 1) {
                try {
                    intermedia = (Map<String, Object>) intermedia.get(key);
                } catch (ClassCastException e) {
                    intermedia = null;
                }
            } else {
                try {
                    result = (int[]) intermedia.get(key);
                } catch (ClassCastException e) {
                    result = null;
                }
            }
            idx--;
        }
        if (result == null) {
            result = defaults.getSub(type, type).getIntArray(keys);
        }
        if (result == null) {
            throw new SimContextException("no default int array found for keys" + Arrays.asList(keys) + " in type[" + type + "]");
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public float getFloat(String... keys) {
        Float result = null;
        Map<String, Object> intermedia = this;
        int idx = keys.length;
        for (String key : keys) {
            if (idx > 1) {
                try {
                    intermedia = (Map<String, Object>) intermedia.get(key);
                } catch (ClassCastException e) {
                    intermedia = null;
                }
            } else {
                try {
                    result = (Float) intermedia.get(key);
                } catch (ClassCastException e) {
                    result = null;
                }
            }
            idx--;
        }
        if (result != null) {
            return result.floatValue();
        } else if (defaults != null) {
            System.out.println(defaults);
            System.out.println(defaults.getSub(type, type));
            return defaults.getSub(type, type).getFloat(keys);
        } else {
            throw new SimContextException("no default float value found for keys" + Arrays.asList(keys) + " in type[" + type + "]");
        }
    }

    @SuppressWarnings("unchecked")
    public float[] getFloatArray(String... keys) {
        float[] result = null;
        Map<String, Object> intermedia = this;
        int idx = keys.length;
        for (String key : keys) {
            if (idx > 1) {
                try {
                    intermedia = (Map<String, Object>) intermedia.get(key);
                } catch (ClassCastException e) {
                    intermedia = null;
                }
            } else {
                try {
                    result = (float[]) intermedia.get(key);
                } catch (ClassCastException e) {
                    result = null;
                }
            }
            idx--;
        }
        if (result == null) {
            result = defaults.getSub(type, type).getFloatArray(keys);
        }
        if (result == null) {
            throw new SimContextException("no default float value found for keys" + Arrays.asList(keys) + " in type[" + type + "]");
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public String getString(String... keys) {
        String result = null;
        Map<String, Object> intermedia = this;
        int idx = keys.length;
        for (String key : keys) {
            if (idx > 1) {
                try {
                    System.out.println(intermedia);
                    intermedia = (Map<String, Object>) intermedia.get(key);
                } catch (ClassCastException e) {
                    intermedia = null;
                }
                if (intermedia == null) {
                    throw new SimEngineException("configruation[" + key + "] is missing");
                }
            } else {
                try {
                    result = (String) intermedia.get(key);
                } catch (ClassCastException e) {
                    result = null;
                }
            }
            idx--;
        }
        if (result != null) {
            return result;
        } else if (defaults != null) {
            return defaults.getSub(type, type).getString(keys);
        } else {
            throw new SimContextException("no default string value found for keys" + Arrays.asList(keys) + " in type[" + type + "]");
        }
    }

    @SuppressWarnings("unchecked")
    public String[] getStringArray(String... keys) {
        String[] result = null;
        Map<String, Object> intermedia = this;
        int idx = keys.length;
        for (String key : keys) {
            if (idx > 1) {
                try {
                    intermedia = (Map<String, Object>) intermedia.get(key);
                } catch (ClassCastException e) {
                    intermedia = null;
                }
            } else {
                try {
                    result = (String[]) intermedia.get(key);
                } catch (ClassCastException e) {
                    result = null;
                }
            }
            idx--;
        }
        if (result == null) {
            result = defaults.getSub(type, type).getStringArray(keys);
        }
        if (result == null) {
            throw new SimContextException("no default string value found for keys" + Arrays.asList(keys) + " in type[" + type + "]");
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public SimContext getSub(String type, String... keys) {
        Map<String, Object> intermedia = this;
        if (keys == null) {
            intermedia = new HashMap<String, Object>();
        } else {
            for (String key : keys) {
                if (intermedia != null) {
                    try {
                        intermedia = (Map<String, Object>) intermedia.get(key);
                    } catch (ClassCastException e) {
                        intermedia = null;
                    }
                }
            }
            if (defaults != null) {
                intermedia = defaults.getSub(type, keys);
            }
            if (intermedia == null) {
                intermedia = new HashMap<String, Object>();
            }
        }
        return new SimContext(type, defaults, intermedia);
    }
}
