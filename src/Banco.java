import java.util.ArrayList;
import java.util.List;

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
    public ArrayList<CuentaBancaria> obtenerCuentasPorEstado(EstadoCuenta estado) {

        ArrayList<CuentaBancaria> cuentasFiltradas = new ArrayList<>();
        for (CuentaBancaria cuenta : cuentas) {
            if (cuenta.getEstado()== estado){
                cuentasFiltradas.add(cuenta);
            }

        }return cuentasFiltradas;
    }
    public List<CuentaBancaria> obtenerCuentasPorEstadoStream(EstadoCuenta estado){
        return cuentas.stream()
                .filter(cuenta ->cuenta.getEstado()==estado)
                .toList();
    }
    public List<CuentaBancaria> obtenerCuentasConSaldoMayorA(double monto) {

        return cuentas.stream()
                .filter(cuenta -> cuenta.getSaldo() > monto)
                .toList();
    }



}
