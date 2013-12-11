package com.guokr.simbase.events;

import com.guokr.simbase.store.Basis;

public interface BasisListener {

    public void onBasisRevised(Basis source, String[] oldSchema, String[] newSchema);

}
