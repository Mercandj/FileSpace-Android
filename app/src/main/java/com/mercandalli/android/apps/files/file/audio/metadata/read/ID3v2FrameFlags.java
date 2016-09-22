package com.mercandalli.android.apps.files.file.audio.metadata.read;

/* package */ class ID3v2FrameFlags {
    private boolean tagAlterPreservation = false;
    private boolean fileAlterPreservation = false;
    private boolean readOnly = false;
    private boolean groupingIdentity = false;
    private boolean compression = false;
    private boolean encryption = false;
    private boolean unsynchronisation = false;
    private boolean dataLengthIndicator = false;

    public boolean isTagAlterPreservation() {
        return this.tagAlterPreservation;
    }

    public boolean isFileAlterPreservation() {
        return this.fileAlterPreservation;
    }

    public boolean isReadOnly() {
        return this.readOnly;
    }

    public boolean isGroupingIdentity() {
        return this.groupingIdentity;
    }

    public boolean isCompression() {
        return this.compression;
    }

    public boolean isEncryption() {
        return this.encryption;
    }

    public boolean isUnsynchronisation() {
        return this.unsynchronisation;
    }

    public boolean isDataLengthIndicator() {
        return this.dataLengthIndicator;
    }

    public void setTagAlterPreservation(boolean value) {
        this.tagAlterPreservation = value;
    }

    public void setFileAlterPreservation(boolean value) {
        this.fileAlterPreservation = value;
    }

    public void setReadOnly(boolean value) {
        this.readOnly = value;
    }

    public void setGroupingIdentity(boolean value) {
        this.groupingIdentity = value;
    }

    public void setCompression(boolean value) {
        this.compression = value;
    }

    public void setEncryption(boolean value) {
        this.encryption = value;
    }

    public void setUnsynchronisation(boolean value) {
        this.unsynchronisation = value;
    }

    public void setDataLengthIndicator(boolean value) {
        this.dataLengthIndicator = value;
    }

    public boolean hasSetFlag() {
        return this.tagAlterPreservation || this.fileAlterPreservation || this.readOnly || this.groupingIdentity || this.compression || this.encryption || this.unsynchronisation || this.dataLengthIndicator;
    }

    public ID3v2FrameFlags() {
    }
}

