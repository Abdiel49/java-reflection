import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
// import java.lang.reflect.Method;
import java.util.Date;

public class Explorer {

    public void lookFields(Object o){
        Class<?> oClass = o.getClass();
        Field[] oFields = oClass.getDeclaredFields();
        for (Field f : oFields) {
            f.setAccessible(true); // hacemos accesible en campo
            String fieldName = f.getName();
            if(fieldName.equals("address")){
                instanceObject(f);
            }
            System.out.print("Field name:\t" + fieldName + "\t"); // nombre del campo/atributo
            Class<?> fclassType = f.getType(); //f.getType(); // la clase del campo
        
            System.out.print("class type:\t" + fclassType + "\t");
            try {
                Object obj = f.get(o); // el valor del campo
                System.out.println("\tvalue:\t" + obj);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private Object instanceObject(Field f){
        Class<?> fclassType = f.getType();
        Object o = null;
        try{
            Constructor<?> cons = fclassType.getConstructor(String.class, String.class, int.class);
            String city = "New City";            
            String street = "New Street";
            int number = 888;
            o = cons.newInstance(city, street, number);
            System.out.println("Object:\t"+ o);
            System.out.println("Clas Object:\t"+ o.getClass());
        }catch (NoSuchMethodException |
        InvocationTargetException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return o;
    }

    public void changeFields(Object o){
        Class<?> oClass = o.getClass();
        Field[] oFields = oClass.getDeclaredFields();
        System.out.println(o);
        for(Field f : oFields){
            f.setAccessible(true);
            try {
                Object valueChanged = changeFieldValue(f.getName());
                f.set(o, valueChanged);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        System.out.println(o);
    }

    private Object changeFieldValue(String fieldName){
        Object obj = null;
        switch (fieldName) {
            case "name" -> obj = "Este es un nombre cambiado";
            case "dni" -> obj = "Un nuevo DNI";
            case "date" -> obj = new Date();
            case "age" -> obj = 55;
            case "address" -> obj = new Address("Toronto", "Queen ave", 404);
            case "married"  -> obj = false;
        }
        return obj;
    }

    // public void lookMethods(Object o){
    //     Class<?> oClass = o.getClass();
    //     Method[] oMethods = oClass.getDeclaredMethods();
    //     for(Method m : oMethods){
            
    //     }
    // }
}
