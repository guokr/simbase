package com.guokr.simbase;

import java.util.HashMap;
import java.util.Map;

public class SimContext extends HashMap<String, Object> {

    private static final long serialVersionUID = -8288998975274604087L;

    public SimContext() {
        super();
    }

    public SimContext(Map<String, Object> raw) {
        super(raw);
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
        } else {
            return 0;
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
            result = new int[0];
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
        } else {
            return 0.0f;
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
            result = new float[0];
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
                    intermedia = (Map<String, Object>) intermedia.get(key);
                } catch (ClassCastException e) {
                    intermedia = null;
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
        } else {
            return "";
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
            result = new String[0];
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public SimContext getSub(String... keys) {
        Map<String, Object> intermedia = this;
        for (String key : keys) {
            try {
                intermedia = (Map<String, Object>) intermedia.get(key);
            } catch (ClassCastException e) {
                intermedia = null;
            }
        }
        if (intermedia == null) {
            intermedia = new HashMap<String, Object>();
        }
        return new SimContext(intermedia);
    }

}
