CREATE TABLE personas (
                          rut VARCHAR(12) PRIMARY KEY,
                          nombre VARCHAR(100) NOT NULL,
                          fecha_nacimiento DATE NOT NULL
);

CREATE TABLE cuentas (
                         numero_cuenta VARCHAR(20) PRIMARY KEY,
                         titular_rut VARCHAR(12) NOT NULL,
                         saldo NUMERIC(15,2) NOT NULL DEFAULT 0,
                         estado VARCHAR(20) NOT NULL,
                         tipo_cuenta VARCHAR(30) NOT NULL,
                         linea_credito NUMERIC(15,2),

                         CONSTRAINT fk_cuentas_personas
                             FOREIGN KEY (titular_rut)
                                 REFERENCES personas(rut)
);