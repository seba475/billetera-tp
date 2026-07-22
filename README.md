# billete.ar — Sistema de Billetera Virtual

Sistema en Java que modela una billetera virtual: administra usuarios y
empresas, distintos tipos de cuenta, transferencias entre cuentas e
inversiones financieras. Desarrollado como trabajo práctico integrador de
Programación II (UNGS), respetando una interfaz provista por la cátedra y
verificado con tests unitarios en JUnit.

## Características

- **Tres tipos de cuenta**, cada uno con sus reglas: *Regular* (tope de saldo
  de 5.000.000), *Premium* (depósito inicial mínimo de 500.000) y
  *Corporativa* (asociada a una empresa; solo la operan usuarios autorizados).
- **Transferencias** entre cuentas identificadas por CVU, con validación de
  saldo y de autorización según el tipo de cuenta.
- **Tres tipos de inversión**, cada uno con su propio cálculo de rendimiento y
  de precancelación: *Renta Fija* (tasa 20%), *Vinculada a Divisa* (atada a una
  cotización) y *Fondo de Liquidez* (mínimo de 20.000.000, no precancelable).
- **Historial de actividad** unificado de transferencias e inversiones,
  consultable de forma global o por cuenta.

## Tecnologías

- Java
- JUnit (tests unitarios)

## Cómo ejecutar los tests

Los tests están en `BilleteraTest.java` y ejercitan el sistema completo:
cuentas, transferencias, inversiones y las reglas de cada tipo. En Eclipse,
click derecho sobre el archivo -> `Run As -> JUnit Test`.

## Diseño

El sistema se apoya en tres jerarquías de herencia, cada una con una clase
abstracta en la raíz:

- **`Cuenta`** -> `CuentaRegular`, `CuentaPremium`, `CuentaCorporativa`.
- **`Actividad`** -> `Transferencia`, `RegistroInversion`.
- **`Inversion`** -> `RentaFija`, `VinculadaDivisa`, `FondoLiquidez`.

**Polimorfismo en lugar de chequeo de tipos.** El historial es una lista de
`Actividad` con transferencias y registros de inversión mezclados; al recorrerlo
y llamar a `toString()`, cada objeto responde con su propia versión sin
preguntar de qué tipo es. Lo mismo al precancelar: `Billetera` llama a
`calcularDevolucionPorPrecancelacion()` sobre una `Inversion` y cada subtipo
resuelve su cálculo. Esto evita cadenas de `instanceof` y deja el código abierto
a agregar nuevos tipos sin tocar el existente.

**Sobrescritura para las reglas de cada subtipo.** `acreditar()` en
`CuentaRegular` impone el tope de 5 millones; `precancelar()` en `FondoLiquidez`
lanza excepción porque ese fondo no admite precancelación; `validarOperacion()`
se redefine en cada tipo de cuenta.

**Búsquedas en tiempo constante.** Usuarios, empresas y cuentas se indexan en
`HashMap` por su clave natural (DNI, CUIT, CVU), y las personas autorizadas de
una empresa en un `HashSet`. Gracias a esto `realizarTransferencia` es **O(1)**:
se descompone en dos búsquedas de cuenta por CVU, una validación que en el peor
caso consulta el `HashSet` de autorizados, la creación del registro, su
inserción al final del historial y el débito/acreditación sobre los saldos --
todas O(1). No depende de cuántas cuentas, usuarios o autorizados haya en el
sistema.

Cada clase mantiene además sus invariantes de representación (saldos nunca
negativos, unicidad de DNI/CUIT/CVU/alias, `totalInvertido` siempre igual a la
suma de inversiones activas del usuario, entre otras).

## Diagrama de clases

![Diagrama de clases](diagrama-clases.png)

## Autores

- Sebastián Pérez
- Ariel Andrada

Trabajo práctico integrador -- Programación II -- Universidad Nacional de
General Sarmiento (UNGS).
