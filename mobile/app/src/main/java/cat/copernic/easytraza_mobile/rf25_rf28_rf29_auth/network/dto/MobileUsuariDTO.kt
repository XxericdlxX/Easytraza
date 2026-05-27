package cat.copernic.easytraza_mobile.rf25_rf28_rf29_auth.network.dto

/**
 * Component de xarxa `MobileUsuariDto` de l'aplicació mobile d'EasyTraza.
 */
data class MobileUsuariDto(
    val id: Long,
    val nom: String? = null,
    val cognoms: String? = null,
    val rol: String? = null,

    val fotoPerfilUrl: String? = null
)
