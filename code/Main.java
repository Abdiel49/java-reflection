
class Main {

	public static void main(String[] args) {
		Address address = new Address("Duck town", "Donal Ave", 204);
		User user = new User("Donald Juan", "5896224CBN", address);
		Explorer exp = new Explorer();
		System.out.println("\n*** look fields: ***\n");
		exp.lookFields(user);
		System.out.println("\n*** Change user valur fields: ***\n");
		exp.changeFields(user);
	}
}

/**
 * Usuario
 * 	ID
 * 	Nombre
 * 	DNI
 * 	FechaNac
 * 		
 * 
 * 
 * CuentaBancaria
 * 		UID
 * 		Money
 * 		Transacciones
 * 		
 */