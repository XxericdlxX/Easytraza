/**
 * Aplicació mobile d'EasyTraza.
 *
 * Aquest package és l'arrel de la part Android del projecte EasyTraza.
 * Conté la classe principal de l'aplicació i agrupa la resta de packages
 * organitzats per requisits funcionals.
 *
 * L'estructura mobile està separada en diferents blocs segons els RF
 * implementats:
 *
 * - `comu`: components comuns reutilitzats per diferents funcionalitats.
 * - `rf07_albarans_ocr`: recepció d'albarans de proveïdor amb OCR.
 * - `rf10_rf11_rf21_rf22_lots`: gestió, inici, finalització, filtratge i ordenació de lots.
 * - `rf15_configuracio_ip`: configuració persistent de la IP del servidor.
 * - `rf25_rf28_rf29_auth`: identificació, login i logout d'usuari.
 *
 * Aquesta organització facilita relacionar el codi mobile amb els requisits
 * funcionals del projecte i manté separada la responsabilitat de cada part
 * de l'aplicació.
 */
package cat.copernic.easytraza_mobile