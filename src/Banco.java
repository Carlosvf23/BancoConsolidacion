import java.util.*;

public class Banco {

    private Map<String, CuentaBancaria> cuentas;

    public Banco() {
        cuentas = new HashMap<>();
    }

    public boolean agregarCuenta(CuentaBancaria cuenta) {
        if (cuentas.containsKey(cuenta.getNumeroCuenta())){
            return false;
        }
        cuentas.put(cuenta.getNumeroCuenta(),cuenta);
        return true;
    }

    public Optional<CuentaBancaria> buscarCuenta(String numeroCuenta) {

        return Optional.ofNullable(cuentas.get(numeroCuenta));
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


    public Collection<CuentaBancaria> getCuentas() {
        return cuentas.values();
    }

    public interface Transferible {
        boolean transferir(CuentaBancaria destino, double monto);
    }
    public ArrayList<CuentaBancaria> obtenerCuentasPorEstado(EstadoCuenta estado) {

        ArrayList<CuentaBancaria> cuentasFiltradas = new ArrayList<>();
        for (CuentaBancaria cuenta : cuentas.values()) {
            if (cuenta.getEstado()== estado){
                cuentasFiltradas.add(cuenta);
            }

        }return cuentasFiltradas;
    }
    public List<CuentaBancaria> obtenerCuentasPorEstadoStream(EstadoCuenta estado){
        return cuentas.values().stream()
                .filter(cuenta ->cuenta.getEstado()==estado)
                .toList();
    }
    public List<CuentaBancaria> obtenerCuentasConSaldoMayorA(double monto) {

        return cuentas.values().stream()
                .filter(cuenta -> cuenta.getSaldo() > monto)
                .toList();
    }
    public long contarCuentasPorEstado(EstadoCuenta estado) {

        return cuentas.values().stream()
                .filter(cuenta -> cuenta.getEstado() == estado)
                .count();
    }
    public boolean existeCuentaConSaldoMayorA(double monto) {

        return cuentas.values().stream()
                .anyMatch(cuenta -> cuenta.getSaldo() > monto);
    }
    public List<String> obtenerNumerosDeCuenta() {

        return cuentas.values().stream()
                .map(cuenta -> cuenta.getNumeroCuenta())
                .toList();
    }
    public List<String> obtenerNumerosCuentasActivas() {

        return cuentas.values().stream()
                .filter(cuenta->cuenta.getEstado()==EstadoCuenta.ACTIVA).map(CuentaBancaria::getNumeroCuenta).toList();

    }
    public Optional<CuentaBancaria> obtenerPrimeraCuentaActiva() {

        return cuentas.values().stream()
                .filter(cuenta -> cuenta.getEstado() == EstadoCuenta.ACTIVA)
                .findFirst();
    }



}
