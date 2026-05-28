/**
 * DTOs mobile del backend d'EasyTraza.
 *
 * <p>
 * Aquest package conté els objectes de transferència de dades utilitzats
 * exclusivament per la comunicació entre el backend i l'aplicació Android.
 * Aquests DTOs adapten les dades del sistema a les necessitats de la part
 * mobile sense exposar directament les entitats persistents del domini.
 * </p>
 *
 * <p>
 * S'utilitzen principalment en funcionalitats com la gestió de lots des del
 * dispositiu mòbil, la identificació d'usuaris, la recepció assistida
 * d'albarans, el processament OCR i les proves de connexió amb el servidor.
 * </p>
 *
 * <p>
 * Aquesta separació facilita mantenir una API més clara, segura i estable per a
 * l'app mobile, evitant que els canvis interns del backend afectin directament
 * les pantalles Android.
 * </p>
 */
package cat.copernic.easytraza_backend.dto.mobile;
