public class Address {
    
    private String city;
    private String street;
    private int number;

    public Address(String city, String street, int num){
        this.city = city;
        this.street = street;
        number = num;
    }

    @Override
    public String toString(){
        return  "City:\t"+city+"\t"+
                "Street:\t"+street+"\t"+
                "Number:\t"+number;
    }
}
