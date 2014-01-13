package com.guokr.simbase.events;

public interface SimBasisListener {

    public void onVecSetAdded(String bkeySrc, String vkey);

    public void onVecSetDeleted(String bkeySrc, String vkey);

    public void onRecAdded(String bkeySrc, String vkeyFrom, String vkeyTo);

    public void onRecDeleted(String bkeySrc, String vkeyFrom, String vkeyTo);

}
