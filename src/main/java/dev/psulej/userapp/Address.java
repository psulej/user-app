package dev.psulej.userapp;

public class Address {
    String country;
    String city;
    String street;
    String houseNumber;
    String zipCode;

    public Address(String country, String city, String street, String houseNumber, String zipCode) {
        this.country = country;
        this.city = city;
        this.street = street;
        this.houseNumber = houseNumber;
        this.zipCode = zipCode;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public String getStreet() {
        return street;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public String getZipCode() {
        return zipCode;
    }
}
