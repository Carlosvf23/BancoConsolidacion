INSERT INTO personas (rut, nombre, fecha_nacimiento)
VALUES
    ('12345678-9', 'Carlos', '1995-03-07'),
    ('12345678-4', 'Felipe', '1995-03-07');

INSERT INTO cuentas (
    numero_cuenta,
    titular_rut,
    saldo,
    estado,
    tipo_cuenta,
    linea_credito
)
VALUES
    ('001', '12345678-9', 70000, 'ACTIVA', 'CORRIENTE', 500000),
    ('003', '12345678-9', 30000, 'ACTIVA', 'VISTA', NULL),
    ('004', '12345678-4', 0, 'ACTIVA', 'VISTA', NULL);