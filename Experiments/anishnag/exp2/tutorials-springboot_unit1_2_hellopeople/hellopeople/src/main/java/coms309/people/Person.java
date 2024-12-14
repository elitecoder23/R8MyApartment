package coms309.people;


/**
 * Provides the Definition/Structure for the people row
 *
 * @author Vivek Bengre
 */

public class Person {

    private String firstName;

    private String socialSecurity; //Anish added

    private String lastName;

    private String address;

    private String telephone;

    private String age; //Anish added

    public Person(){
        
    }

    public Person(String firstName, String lastName, String address, String telephone){
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.telephone = telephone;
        this.socialSecurity = socialSecurity; //Anish added
        this.age = age; //Anish added
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTelephone() {
        return this.telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getSocialSecurity() {
        return this.socialSecurity;
    } //Anish Added

    public void setSocialSecurity(String firstName) {
        this.firstName = socialSecurity;
    } //Anish added

    public String getAge() {
        return this.age;
    } //Anish added

    public void setAge(String firstName) {
        this.firstName = age;
    } //Anish Added

    @Override
    public String toString() {
        return firstName + " " 
               + lastName + " "
               + address + " "
                + socialSecurity + " " //Anish added
                + age + " " //Anish Added
               + telephone;
    }
}
