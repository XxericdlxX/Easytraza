package cat.copernic.easytraza_mobile.network.dto

data class OcrLotRespostaDto(
    val codiLot: String? = null,
    val materiaPrima: String? = null,
    val quantitat: Double? = null
)

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

data class MobileLotSaveRequestDto(
    val codiLot: String,
    val quantitat: Double?,
    val materiaPrimaNom: String,
    val crearMateriaPrimaSiNoExisteix: Boolean
)

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

data class MobileLotDto(
    val id: Long,
    val codiLot: String? = null,
    val materiaPrimaNom: String? = null,
    val proveidorNom: String? = null,
    val quantitat: Double? = null,
    val estat: String? = null,
    val dataRecepcio: String? = null
)