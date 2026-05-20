package cat.copernic.easytraza_mobile.network.dto

/**
 * Component de xarxa `OcrLotRespostaDto` de l'aplicació mobile d'EasyTraza.
 */
data class OcrLotRespostaDto(
    val codiLot: String? = null,
    val codiMateriaPrimaOcr: String? = null,
    val materiaPrima: String? = null,
    val quantitat: Double? = null
)

/**
 * Component de xarxa `OcrAlbaraResponseDto` de l'aplicació mobile d'EasyTraza.
 */
data class OcrAlbaraResponseDto(
    val proveidorCif: String? = null,
    val numeroAlbara: String? = null,
    val dataAlbara: String? = null,
    val textDetectat: String? = null,
    val lots: List<OcrLotRespostaDto> = emptyList(),

    val documentOcrNomOriginal: String? = null,
    val documentOcrNomGuardat: String? = null,
    val documentOcrContentType: String? = null,
    val documentOcrRuta: String? = null
)

/**
 * Component de xarxa `MobileLotSaveRequestDto` de l'aplicació mobile d'EasyTraza.
 */
data class MobileLotSaveRequestDto(
    val codiLot: String,
    val codiMateriaPrimaOcr: String? = null,
    val quantitat: Double?,
    val materiaPrimaNom: String,
    val crearMateriaPrimaSiNoExisteix: Boolean
)

/**
 * Component de xarxa `MobileAlbaraSaveRequestDto` de l'aplicació mobile d'EasyTraza.
 */
data class MobileAlbaraSaveRequestDto(
    val dataRecepcio: String,
    val proveidorCif: String?,
    val proveidorNom: String,
    val crearProveidorSiNoExisteix: Boolean,
    val usuariReceptorId: Long?,
    val lots: List<MobileLotSaveRequestDto>,

    val documentOcrNomOriginal: String? = null,
    val documentOcrNomGuardat: String? = null,
    val documentOcrContentType: String? = null,
    val documentOcrRuta: String? = null
)

/**
 * Component de xarxa `MobileLotDto` de l'aplicació mobile d'EasyTraza.
 */
data class MobileLotDto(
    val id: Long,
    val codiLot: String? = null,
    val materiaPrimaNom: String? = null,
    val proveidorNom: String? = null,
    val quantitat: Double? = null,
    val estat: String? = null,
    val dataRecepcio: String? = null
)
