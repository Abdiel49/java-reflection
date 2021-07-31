# Accediendo a campos/atributos privados de un objeto JAVA

> Hace unos días tuve que acceder al valor que tenia un objeto el cual era `private` por lo cual "no se puede" acceder directamente, también no podía añadir un método `get()` que seria lo mas rápido, pero de no ser por esa restricción no habría este post. enlace del repositorio al final.

**Java JDK**

```sh
->  java -version
java version "15.0.2" 2021-01-19
Java(TM) SE Runtime Environment (build 15.0.2+7-27)
Java HotSpot(TM) 64-Bit Server VM (build 15.0.2+7-27, mixed mode, sharing)
```

La estructura de archivos es:

```sh
.
├── code
│   ├── Address.java
│   ├── Explorer.java
│   ├── Main.java
│   └── User.java
└── README.md
```

Las clases `User` y `Address` servirán de ejemplo ejemplo y pruebas. `Explorer` contiene la funcionalidad a exponer y que explicaremos en este post, la clase `Main` creara instancias de User, Address y Explorer [repositorio](#repositorio).


`code/Address.java`
```java
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
        ...
    }
}
```

`Address.java` esta formado por un tipo de dato primitivo `int` y dos cadenas de caracteres `String`, estos _'atributos'_, que a partir de ahora los llamaremos **'campos'** (field), tienen el modificador de acceso `private` y definen al objeto Address que no posee getters o setters.


`code/User.java`
```java
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
      ...
  }
}
```

`User.java` consta de **campos** primitivos, cadena de caracteres, `java.util.Date` y `Address` que son tipos que encierran mas complejidad.  

Ok como vemos todos los 'campos' de User y Address tienen el modificador de acceso: `private` y en las clases no tenemos métodos de acceso y menos modificar los valores de cada campo.

## Objetivo

El objetivo es mostrar como podemos acceder, conocer y modificar campos que tengan, en este caso, el modificador de acceso `private` usando métodos de la clase `Object` y clases del paquete `java.lang.reflect`.
Obtén mas información en la sección de [referencias](#referencias).

## Algunos conceptos necesarios

* **[Class<?>](https://docs.oracle.com/en/java/javase/15/docs/api/java.base/java/lang/Class.html)** - Representa Clases e Interfaces de una aplicación Java en ejecución. Los tipos primitivos de Java (boolean, byte, char, short, int, long, float y double) y la palabra clave void también se representan como objetos `Class`. La clase `Class` expone mas características de una clase o interfaz, la mayoría derivan del archivo de archivo `class` que el _class loader_ paso a la _Java Virtual Machine_ pero unas pocas características estas determinadas por el entorno _class loading_ en tiempo de ejecución.

* **[Object.getClass()](https://docs.oracle.com/en/java/javase/15/docs/api/java.base/java/lang/Object.html)** - Devuelve la clase en tiempo de ejecución del objeto
  
* **[java.lang.reflect](https://docs.oracle.com/en/java/javase/15/docs/api/java.base/java/lang/reflect/package-summary.html)** - Permite el acceso a la información sobre los campos (fields), métodos y constructores de las clases cargadas.

### Main 

En la clase main definimos un objeto User, Address y Explorer; iremos llamando a los métodos de Explorer según sea necesario.

`code/Main.java`
```java 
class Main {
	public static void main(String[] args) {
		Address address = new Address("Duck town", "Donal Ave", 204);
		User user = new User("Donald Juan", "5896224CBN", address);
		Explorer exp = new Explorer();
	}
}
```

## Fields - Campos

Lo primero es conocer los campos que tiene el objeto user, y ademas el tipo de dato al que pertenecen (int, String, etc.) asi que en la clase `Explorer` definiremos el método `lookFields(Object o)` para imprimir información de los campos del objeto.

Interactuando con la clase User en tiempo de ejecución usando `user.getClass()`, de ello llamaremos al método `getDeclaredFields()` y lo almacenamos un un `Field[]` del paquete `java.lang.reflect.Field`.

```java
Class<?> uClass = user.getClass();
Field[] uFields = uClass.getDeclaredFields(); //contiene un arreglo con los campos del objeto 'user'
```

Si conocemos el nombre de un campo, podemos acceder mediante el método `uClass.getDeclaredField("field_name")`, pero como asumimos que no conocemos cuales son, iteraremos cada uno de los objetos `Field` en el arreglo, y obtendremos información de cada campo del objeto `User`.

_Consulta la sección de [**Referencias**](#referencias)_

Podemos iterar el arreglo de campos, pero primero debemos hacer accesible cada campo llamando al método `setAccessible(true)`, ok ahora sobre el Objeto `Field` usaremos los métodos:

* **`getName()`** _String_ - para obtener el nombre del campo
* **`getType()`** _Class<?>_ - para conocer el `Class` del campo
* **`get(_Object_)`** _Object_ - para obtener el valor del campo, es decir el valor que contiene el campo en la clase `User`, recibe como argumento el objeto al que pertenece el objeto `Field`, en este caso `user`, se debe capturar las excepciones `IllegalArgumentException` e `IllegalAccessException`.
* **`getModifiers()`** _int_ - para obtener los modificadores de acceso del campo, consulta _refect.Modifier_ en las [Referencias](#referencias).

`Main.java`
```java
lookFields(user)
```

`Explores.java`
```java
import java.lang.reflect.Field;

public class Explorer {

    public void lookFields(Object o){
        Class<?> oClass = o.getClass();
        Field[] oFields = oClass.getDeclaredFields();
        for (Field f : oFields) {
            f.setAccessible(true); // hacemos accesible en campo
            System.out.print("Field name:\t" + f.getName() + "\t"); // nombre del campo/atributo
            try {
                Class<?> fclassType = f.getType(); // la clase del campo
                System.out.print("class type:\t" + fclassType + "\t");
                Object obj = f.get(o); // el valor del campo
                System.out.println("\tvalue:\t" + obj);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

}
```

Salida:

```sh
Field name:     name    class type:     class java.lang.String          value:  Donald Juan
Field name:     dni     class type:     class java.lang.String          value:  5896224CBN
Field name:     date    class type:     class java.util.Date            value:  Fri Jul 30 18:51:10 BOT 2021
Field name:     age     class type:     int             value:  20
Field name:     married class type:     boolean         value:  false
Field name:     address class type:     class Address           value:  City:   Duck town       Street: Donal Ave       Number: 204
```

Ok ya sabemos como obtener información acerca de los campos de `User`, resaltan las clases de los campos `date` y los de tipo _'String'_ `java.lang.String` y `java.util.Date` respectivamente pero de la clase Address es `Address`, también el valor devuelto por casa uno de estos es interesante en el caso del campo `date` y `address` que para retornar el valor llaman al método `toString()` que la clase _Date_ y _Address_ tienen implementado, de lo contrario la salida seria una dirección en la memoria, algo asi:
```sh
Field name:     address class type:     class Address           value:  Address@23fc625e
```

## Analizando el campo `address`

Como vimos el en la salida hace uso del método to string para conocer el valor, pero si necesitamos conocer mas acerca de este objeto como crear nuevas instancias de esta para poder cambiar el valor debemos hacer algunas cosas mas, si no puedes pasar a la [siguiente sección](#modificando-campos)

Nuestro punto de partida sera conocer el nombre del campo o Field name (`address`) con lo cual podremos trabajar, empecemos por mandarlo a otro método si el nombre del campo coincide con el de 'address'.

```java
public void lookFields(Object o){
    // ...
    for (Field f : oFields) {
        // ...
        String fieldName = f.getName();
        if(fieldName.equals("address")){
            instanceObject(f);
        }
        // ...
    }
}

private void instanceObject(Field f){ ... }
```

Con el método `f.getType()` sobre el objeto `Field` obtenemos el objeto `Class` que identifica el tipo declarado para el campo representado por este objeto `Field`. 

Podemos repetir en mismo proceso para obtener los campos  y valores del objeto `address` usando `getDeclaredFields()` : `Field[]`, es exactamente igual, lo interesante es crear un objeto de esa clase con valores que queramos cambiar y es lo que haremos.

Usando el la clase `Constructor` del paquete `java.lang.reflect` y el método `newInstance(Object... initargs) : T` podemos crear una nueva instancia de la clase declarada por el constructor, con los parámetros de inicialización especificados.

`Address` tiene los campos: `String : city`, `String : street` y `int : number`, el orden y tipos de datos son muy importante para el objeto `Constructor` con la clase con el método `getConstructor(Class<?>... parameterTypes)` que recibe como parámetro objetos Class que identifican los tipos de parámetros formales del constructor, en el orden declarado.

```java
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
```
Salida:

```sh
Object: City:   New City        Street: New Street      Number: 888
Clas Object:    class Address
```

Como observamos ya tenemos una instancia del objeto `Address` con los campos diferentes, esto servira cuando queramos modificar un campo.  

<!-- modificar los atributos de User -->
#### Modificando campos

La clase `Field` posee métodos para modificar campos de tipo primitivo, y `set​(Object obj, Object value)` donde `obj` es el objeto cuyo campo debe modificarse y `value` el nuevo valor para el campo de obj que se está modificando, si el argumento del objeto especificado no es una instancia de la clase o interfaz (`User`) que declara el campo del objeto, el método arroja un `IllegalArgumentException` `IllegalAccessException`

Añadiremos el método `changeFields(Object o)` y `changeFieldValue` para ayudarnos a cambiar el valor de los campos, hasta este punto ya conocemos en nombre de los campos podemos efectuar cambios, 

```java
import java.lang.reflect.Field;
import java.util.Date;
// ....
    public void changeFields(Object o){
        Class<?> oClass = o.getClass();
        Field[] oFields = oClass.getDeclaredFields();
        System.out.println(o);
        for(Field f : oFields){
            f.setAccessible(true);
            try {
                Object valueChanged = changeFieldValue(f.getName());
                f.set(o, valueChanged); // Set the field value
            } catch (IllegalArgumentException | IllegalAccessException e) {
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
```

Salida

```sh
Name:   Pedro Perez Pereira     DNI:    5896224CBN      Age:    25      Married:        true    Address:        City:   CBBA    Street: Av. Pando       Number: 204     Date:   Sat Jun 12 23:16:34 BOT 2021
Name:   Este es un nombre cambiado      DNI:    Un nuevo DNI    Age:    55      Married:        false   Address:        City:   Toronto Street: Queen ave       Number: 404     Date:  Sat Jun 12 23:16:35 BOT 2021
```

Puedes crear una instancia para el campo `address` como se vio en la sección [Analizando el campo address](#analizando-el-campo-address)


## Repositorio 
[click aquí](https://github.com/Abdiel49/java-relfection)


## Referencias

* [Java Object](https://docs.oracle.com/en/java/javase/15/docs/api/java.base/java/lang/Object.html)
* [Java Class](https://docs.oracle.com/en/java/javase/15/docs/api/java.base/java/lang/Class.html)
* [Java Reflect](https://docs.oracle.com/en/java/javase/15/docs/api/java.base/java/lang/reflect/package-summary.html)
* [Java Field](https://docs.oracle.com/en/java/javase/15/docs/api/java.base/java/lang/reflect/Field.html)
* [Java Method](https://docs.oracle.com/en/java/javase/15/docs/api/java.base/java/lang/reflect/Method.html)
* [Java Modifier](https://docs.oracle.com/en/java/javase/15/docs/api/java.base/java/lang/reflect/Modifier.html)
* [Constructor](https://docs.oracle.com/en/java/javase/15/docs/api/java.base/java/lang/reflect/Constructor.html)
