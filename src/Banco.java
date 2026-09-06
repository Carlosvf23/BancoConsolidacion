import java.util.ArrayList;

public class Banco {

    private ArrayList<CuentaBancaria> cuentas;

    public Banco() {
        cuentas = new ArrayList<>();
    }

    public boolean agregarCuenta(CuentaBancaria cuenta) {
        if (buscarCuenta(cuenta.getNumeroCuenta()) != null) {
            return false;
        }

        cuentas.add(cuenta);
        return true;
    }

    public CuentaBancaria buscarCuenta(String numeroCuenta) {

        for (CuentaBancaria cuenta : cuentas) {

            if (cuenta.getNumeroCuenta().equals(numeroCuenta)) {
                return cuenta;
            }
        }

        return null;
    }

    public boolean depositar(String numeroCuenta, double monto) {

        CuentaBancaria cuenta = buscarCuenta(numeroCuenta);

        if (cuenta == null) {
            return false;
        }

        return cuenta.depositar(monto);
    }

    public boolean retirar(String numeroCuenta, double monto) {

        CuentaBancaria cuenta = buscarCuenta(numeroCuenta);

        if (cuenta == null) {
            return false;
        }

        return cuenta.retirar(monto);
    }

    public ArrayList<CuentaBancaria> getCuentas() {
        return cuentas;
    }
    public interface Transferible {
        boolean transferir(CuentaBancaria destino, double monto);
    }
}
