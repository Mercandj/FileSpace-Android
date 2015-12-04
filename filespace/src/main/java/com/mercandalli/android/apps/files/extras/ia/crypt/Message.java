package com.mercandalli.android.apps.files.extras.ia.crypt;

public class Message {
    private String contenu;

    public Message(String pContenu) {
        contenu = pContenu;
    }

    @Override
    public String toString() {
        return contenu;
    }

    public void setValue(String new_value) {
        contenu = new_value;
    }
}
