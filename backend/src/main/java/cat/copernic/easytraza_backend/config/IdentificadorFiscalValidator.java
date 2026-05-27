package cat.copernic.easytraza_backend.config;

/**
 * Utilitat per validar identificadors fiscals espanyols. Permet validar DNI,
 * NIE i CIF comprovant el format i el caràcter de control.
 */
public final class IdentificadorFiscalValidator {

    private static final String LLETRES_DNI = "TRWAGMYFPDXBNJZSQVHLCKE";
    private static final String LLETRES_CONTROL_CIF = "JABCDEFGHI";

    /**
     * Crea una nova instància del component.
     */
    private IdentificadorFiscalValidator() {
    }

    /**
     * Valida si el document rebut és un DNI, NIE o CIF correcte.
     *
     * @param document document fiscal introduït per l'usuari
     * @return true si el document és vàlid
     */
    public static boolean esDocumentFiscalValid(String document) {
        String valor = normalitzar(document);

        if (valor == null) {
            return false;
        }

        return esDniValid(valor) || esNieValid(valor) || esCifValid(valor);
    }

    /**
     * Valida un DNI amb 8 números i lletra de control.
     *
     * @param document DNI introduït
     * @return true si el DNI és vàlid
     */
    public static boolean esDniValid(String document) {
        String valor = normalitzar(document);

        if (valor == null || !valor.matches("^[0-9]{8}[A-Z]$")) {
            return false;
        }

        int numero = Integer.parseInt(valor.substring(0, 8));
        char lletraEsperada = LLETRES_DNI.charAt(numero % 23);
        char lletraRebuda = valor.charAt(8);

        return lletraEsperada == lletraRebuda;
    }

    /**
     * Valida un NIE transformant X, Y o Z al prefix numèric corresponent.
     *
     * @param document NIE introduït
     * @return true si el NIE és vàlid
     */
    public static boolean esNieValid(String document) {
        String valor = normalitzar(document);

        if (valor == null || !valor.matches("^[XYZ][0-9]{7}[A-Z]$")) {
            return false;
        }

        String prefixNumeric = switch (valor.charAt(0)) {
            case 'X' ->
                "0";
            case 'Y' ->
                "1";
            case 'Z' ->
                "2";
            default ->
                null;
        };

        if (prefixNumeric == null) {
            return false;
        }

        String dniEquivalent = prefixNumeric + valor.substring(1);
        return esDniValid(dniEquivalent);
    }

    /**
     * Valida un CIF comprovant el dígit o lletra de control.
     *
     * @param document CIF introduït
     * @return true si el CIF és vàlid
     */
    public static boolean esCifValid(String document) {
        String valor = normalitzar(document);

        if (valor == null || !valor.matches("^[ABCDEFGHJKLMNPQRSUVW][0-9]{7}[0-9A-J]$")) {
            return false;
        }

        char lletraInicial = valor.charAt(0);
        String digits = valor.substring(1, 8);
        char controlRebut = valor.charAt(8);

        int sumaParells = 0;
        int sumaSenars = 0;

        for (int i = 0; i < digits.length(); i++) {
            int digit = Character.getNumericValue(digits.charAt(i));

            if ((i + 1) % 2 == 0) {
                sumaParells += digit;
            } else {
                int doble = digit * 2;
                sumaSenars += (doble / 10) + (doble % 10);
            }
        }

        int sumaTotal = sumaParells + sumaSenars;
        int digitControl = (10 - (sumaTotal % 10)) % 10;
        char lletraControl = LLETRES_CONTROL_CIF.charAt(digitControl);
        char digitControlChar = Character.forDigit(digitControl, 10);

        if ("ABEH".indexOf(lletraInicial) >= 0) {
            return controlRebut == digitControlChar;
        }

        if ("KPQS".indexOf(lletraInicial) >= 0) {
            return controlRebut == lletraControl;
        }

        return controlRebut == digitControlChar || controlRebut == lletraControl;
    }

    /**
     * Normalitza el document eliminant espais i guions, i passant-lo a
     * majúscules.
     *
     * @param document document introduït per l'usuari
     * @return document normalitzat o null si és buit
     */
    public static String normalitzar(String document) {
        if (document == null) {
            return null;
        }

        String valor = document.trim()
                .toUpperCase()
                .replace(" ", "")
                .replace("-", "");

        return valor.isBlank() ? null : valor;
    }
}
