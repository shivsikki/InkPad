package com.example.inkpad.p_info;

public class P_info {

    private String firstName;
    private String middleName;
    private String lastName;
    private String dob;
    private byte[] docImage;
    private byte[] certImage;

    public P_info(String firstName, String middleName, String lastName, String dob, byte[] docImage, byte[] certImage) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.dob = dob;
        this.docImage = docImage;
        this.certImage = certImage;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getDob() {
        return dob;
    }

    public byte[] getDocImage() {
        return docImage;
    }

    public byte[] getCertImage() {
        return certImage;
    }
}
