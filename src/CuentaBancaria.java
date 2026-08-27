public class CuentaBancaria {
    private String numeroCuenta;
    private Persona titular;
    private double saldo;


    public CuentaBancaria (String numeroCuenta,Persona titular){
        this.numeroCuenta = numeroCuenta;
        this.titular = titular;
        this.saldo = 0;
    }

    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public double getSaldo() {
        return saldo;

    }

    public boolean depositar (double monto){

        if (monto >0 ){
            this.saldo = this.saldo+ monto;
            return true;
        }else {return false;
        }
    }
    public boolean retirar (double monto){
        if (monto >0 && monto <= this.saldo){
            this.saldo = this.saldo - monto;
            return true;
        }else {return false;

        }
    }
}
