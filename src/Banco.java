import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Banco {

    private ArrayList<CuentaBancaria> cuentas;

    public Banco() {
        cuentas = new ArrayList<>();
    }

    public boolean agregarCuenta(CuentaBancaria cuenta) {
        if (buscarCuenta(cuenta.getNumeroCuenta()).isPresent()) {
            return false;
        }

        cuentas.add(cuenta);
        return true;
    }

    public Optional<CuentaBancaria> buscarCuenta(String numeroCuenta) {

        return cuentas.stream()
                .filter(cuenta -> cuenta.getNumeroCuenta().equals(numeroCuenta))
                .findFirst();
    }

    public boolean depositar(String numeroCuenta, double monto) {

        CuentaBancaria cuenta = buscarCuenta(numeroCuenta)
                .orElseThrow(() ->
                        new CuentaNoEncontradaException("Cuenta no encontrada")
                );

        return cuenta.depositar(monto);
    }

    public boolean retirar(String numeroCuenta, double monto) {

        CuentaBancaria cuenta = buscarCuenta(numeroCuenta)
                .orElseThrow(()->
                         new CuentaNoEncontradaException("Cuenta No encontrada"+ numeroCuenta));

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
    public long contarCuentasPorEstado(EstadoCuenta estado) {

        return cuentas.stream()
                .filter(cuenta -> cuenta.getEstado() == estado)
                .count();
    }
    public boolean existeCuentaConSaldoMayorA(double monto) {

        return cuentas.stream()
                .anyMatch(cuenta -> cuenta.getSaldo() > monto);
    }
    public List<String> obtenerNumerosDeCuenta() {

        return cuentas.stream()
                .map(cuenta -> cuenta.getNumeroCuenta())
                .toList();
    }
    public List<String> obtenerNumerosCuentasActivas() {

        return cuentas.stream()
                .filter(cuenta->cuenta.getEstado()==EstadoCuenta.ACTIVA).map(CuentaBancaria::getNumeroCuenta).toList();

    }
    public Optional<CuentaBancaria> obtenerPrimeraCuentaActiva() {

        return cuentas.stream()
                .filter(cuenta -> cuenta.getEstado() == EstadoCuenta.ACTIVA)
                .findFirst();
    }



}
