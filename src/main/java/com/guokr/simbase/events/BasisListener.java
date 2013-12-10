package com.guokr.simbase.events;

public interface BasisListener {

    public void onBasisRevised(String bkey, String[] oldSchema, String[] newSchema);

}
