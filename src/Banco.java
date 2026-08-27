import java.util.ArrayList;

public class Banco {

    private ArrayList<CuentaBancaria> cuentas;

    public Banco() {
        this.cuentas = new ArrayList<CuentaBancaria>();
    }
    public boolean agregarCuenta(CuentaBancaria cuenta) {
        if (buscarCuenta(cuenta.getNumeroCuenta())==null){
            this.cuentas.add(cuenta);
            return true;

        }   else {return false;

        }

    }
    public CuentaBancaria buscarCuenta(String numeroCuenta){
        for (CuentaBancaria cuenta : this.cuentas){
            if (cuenta.getNumeroCuenta().equals(numeroCuenta)){
                return cuenta;
            }
        }return null;
    }

} // ← Banco termina AQUÍ

