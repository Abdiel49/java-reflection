import java.util.Date;

public class User {

  private final String name;
  private final String dni;
  private final Date date;
  
  private int age;
  private boolean married;
  private Address address;

  public User(String name, String dni, Address address) {
    this.name = name;
    this.dni = dni;
    date = new Date();
    age = getRandomInt(17, 35);
    married = isMarried();
    this.address = address;
  }

  private int getRandomInt(int min, int max) {
    return (int) (Math.random() * (max-min)) + min;
  }

  private boolean isMarried(){
    return age>=25;
  }

  @Override
  public String toString(){
    return  "Name:\t"+name+"\t"+
            "DNI:\t"+dni+"\t"+
            "Age:\t"+age+"\t"+
            "Married:\t"+married+"\t"+
            "Address:\t"+address+"\t"+
            "Date:\t"+date;
  }
}