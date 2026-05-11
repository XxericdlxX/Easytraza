package cat.copernic.easytraza_backend.controller;

import cat.copernic.easytraza_backend.dto.AlbaraProveidorDto;
import cat.copernic.easytraza_backend.dto.LotProveidorDto;
import cat.copernic.easytraza_backend.dto.OcrAlbaraResponseDto;
import cat.copernic.easytraza_backend.dto.OcrLotRespostaDto;
import cat.copernic.easytraza_backend.model.AlbaraProveidor;
import cat.copernic.easytraza_backend.service.AlbaraProveidorService;
import cat.copernic.easytraza_backend.service.MateriaPrimaService;
import cat.copernic.easytraza_backend.service.OcrAlbaraService;
import cat.copernic.easytraza_backend.service.ProveidorService;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/web/albarans-proveidor")
public class AlbaraProveidorWebController {

    @Autowired
    private AlbaraProveidorService albaraProveidorService;

    @Autowired
    private ProveidorService proveidorService;

    @Autowired
    private MateriaPrimaService materiaPrimaService;

    @Autowired
    private OcrAlbaraService ocrAlbaraService;

    @Autowired
    private MessageSource messageSource;

    @GetMapping
    public String listar(
            @RequestParam(required = false) String proveidorCif,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataRecepcio,
            Model model) {

        model.addAttribute("albarans", albaraProveidorService.buscar(proveidorCif, dataRecepcio));
        model.addAttribute("proveidorCif", proveidorCif);
        model.addAttribute("dataRecepcio", dataRecepcio);

        return "albarans_proveidor/llistar-albarans-proveidor";
    }

    @GetMapping("/crear")
    public String crear(Model model) {
        AlbaraProveidorDto dto = new AlbaraProveidorDto();
        dto.setDataRecepcio(LocalDate.now());
        dto.setLots(new ArrayList<>());
        dto.getLots().add(new LotProveidorDto());

        carregarDadesFormulari(model, dto);
        return "albarans_proveidor/crear-albara-proveidor";
    }

    @PostMapping("/guardar")
    public String guardar(
            @ModelAttribute("albara") AlbaraProveidorDto dto,
            RedirectAttributes redirectAttributes,
            Locale locale,
            Model model) {

        albaraProveidorService.assegurarMinimUnLot(dto);

        String error = albaraProveidorService.validarAlbara(dto, null);

        if (error != null) {
            afegirErrorFormulari(model, error, locale);
            carregarDadesFormulari(model, dto);
            return "albarans_proveidor/crear-albara-proveidor";
        }

        AlbaraProveidor albara = albaraProveidorService.convertirDtoAEntity(dto);
        albaraProveidorService.save(albara);

        redirectAttributes.addFlashAttribute("missatgeExit", missatge("albara.proveidor.flash.creat", locale));
        return "redirect:/web/albarans-proveidor";
    }

    @GetMapping("/editar/{id}")
    public String editar(
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        AlbaraProveidor albara = albaraProveidorService.findById(id).orElse(null);

        if (albara == null) {
            redirectAttributes.addFlashAttribute("missatgeError", missatge("albara.proveidor.flash.no.trobat", locale));
            return "redirect:/web/albarans-proveidor";
        }

        AlbaraProveidorDto dto = albaraProveidorService.convertirEntityADto(albara);
        albaraProveidorService.assegurarMinimUnLot(dto);

        carregarDadesFormulari(model, dto);
        return "albarans_proveidor/editar-albara-proveidor";
    }

    @PostMapping({"/editar/{id}", "/actualitzar/{id}"})
    public String actualitzar(
            @PathVariable Long id,
            @ModelAttribute("albara") AlbaraProveidorDto dto,
            RedirectAttributes redirectAttributes,
            Locale locale,
            Model model) {

        dto.setId(id);
        albaraProveidorService.assegurarMinimUnLot(dto);

        String error = albaraProveidorService.validarAlbara(dto, id);

        if (error != null) {
            afegirErrorFormulari(model, error, locale);
            carregarDadesFormulari(model, dto);
            return "albarans_proveidor/editar-albara-proveidor";
        }

        AlbaraProveidor albara = albaraProveidorService.convertirDtoAEntity(dto);
        AlbaraProveidor actualitzat = albaraProveidorService.update(id, albara);

        if (actualitzat == null) {
            redirectAttributes.addFlashAttribute("missatgeError", missatge("albara.proveidor.flash.no.trobat", locale));
            return "redirect:/web/albarans-proveidor";
        }

        redirectAttributes.addFlashAttribute("missatgeExit", missatge("albara.proveidor.flash.actualitzat", locale));
        return "redirect:/web/albarans-proveidor";
    }

    @GetMapping("/veure/{id}")
    public String veure(
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        AlbaraProveidor albara = albaraProveidorService.findById(id).orElse(null);

        if (albara == null) {
            redirectAttributes.addFlashAttribute("missatgeError", missatge("albara.proveidor.flash.no.trobat", locale));
            return "redirect:/web/albarans-proveidor";
        }

        model.addAttribute("albara", albara);
        model.addAttribute("albaraDetall", albara);

        return "albarans_proveidor/veure-albara-proveidor";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        try {
            albaraProveidorService.deleteById(id);
            redirectAttributes.addFlashAttribute("missatgeExit", missatge("albara.proveidor.flash.eliminat", locale));

        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("missatgeError", missatge("albara.proveidor.flash.no.trobat", locale));

        } catch (IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("missatgeError", missatge(ex.getMessage(), locale));

        } catch (DataIntegrityViolationException ex) {
            redirectAttributes.addFlashAttribute("missatgeError", missatge("albara.proveidor.error.eliminar.relacions", locale));

        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("missatgeError", missatge("albara.proveidor.error.eliminar.generic", locale));
        }

        return "redirect:/web/albarans-proveidor";
    }

    @GetMapping("/ocr")
    public String mostrarOcr() {
        return "albarans_proveidor/ocr-albara-proveidor";
    }

    @PostMapping("/ocr/processar")
    public String processarOcr(
            @RequestParam("fitxer") MultipartFile fitxer,
            Model model,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        if (fitxer == null || fitxer.isEmpty()) {
            redirectAttributes.addFlashAttribute("missatgeError", missatge("albara.proveidor.ocr.error.fitxer.obligatori", locale));
            return "redirect:/web/albarans-proveidor/ocr";
        }

        try {
            OcrAlbaraResponseDto ocrResposta = ocrAlbaraService.processarImatgeAlbara(fitxer);
            AlbaraProveidorDto dto = convertirOcrAAlbaraDto(ocrResposta);

            albaraProveidorService.completarReferenciesOcr(dto);
            albaraProveidorService.assegurarMinimUnLot(dto);

            carregarDadesFormulari(model, dto);
            model.addAttribute("ocrResposta", ocrResposta);
            model.addAttribute("missatgeExit", missatge("albara.proveidor.ocr.exit", locale));

            return "albarans_proveidor/revisar-ocr-albara-proveidor";

        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("missatgeError", missatge("albara.proveidor.ocr.error.processar", locale));
            return "redirect:/web/albarans-proveidor/ocr";
        }
    }

    @PostMapping("/ocr/guardar")
    public String guardarOcr(
            @ModelAttribute("albara") AlbaraProveidorDto dto,
            RedirectAttributes redirectAttributes,
            Locale locale,
            Model model) {

        albaraProveidorService.completarReferenciesOcr(dto);
        albaraProveidorService.assegurarMinimUnLot(dto);

        String error = albaraProveidorService.validarAlbara(dto, null);

        if (error != null) {
            afegirErrorFormulari(model, error, locale);
            carregarDadesFormulari(model, dto);
            return "albarans_proveidor/revisar-ocr-albara-proveidor";
        }

        AlbaraProveidor albara = albaraProveidorService.convertirDtoAEntity(dto);
        albaraProveidorService.save(albara);

        redirectAttributes.addFlashAttribute("missatgeExit", missatge("albara.proveidor.flash.creat", locale));
        return "redirect:/web/albarans-proveidor";
    }

    private AlbaraProveidorDto convertirOcrAAlbaraDto(OcrAlbaraResponseDto ocrResposta) {
        AlbaraProveidorDto dto = new AlbaraProveidorDto();

        dto.setDataRecepcio(convertirDataOcr(ocrResposta.getDataAlbara()));
        dto.setProveidorCif(ocrResposta.getProveidorCif());
        dto.setProveidorNomDetectat(extreureNomProveidorDetectat(ocrResposta.getTextDetectat()));
        dto.setCrearProveidorSiNoExisteix(false);
        dto.setDocumentOcrNomOriginal(ocrResposta.getDocumentOcrNomOriginal());
        dto.setDocumentOcrNomGuardat(ocrResposta.getDocumentOcrNomGuardat());
        dto.setDocumentOcrContentType(ocrResposta.getDocumentOcrContentType());
        dto.setDocumentOcrRuta(ocrResposta.getDocumentOcrRuta());

        List<LotProveidorDto> lots = new ArrayList<>();

        if (ocrResposta.getLots() != null) {
            for (OcrLotRespostaDto lotOcr : ocrResposta.getLots()) {
                LotProveidorDto lotDto = new LotProveidorDto();

                lotDto.setCodiLot(lotOcr.getCodiLot());
                lotDto.setQuantitat(lotOcr.getQuantitat());
                lotDto.setMateriaPrimaNomDetectada(lotOcr.getMateriaPrima());
                lotDto.setCrearMateriaPrimaSiNoExisteix(false);

                lots.add(lotDto);
            }
        }

        if (lots.isEmpty()) {
            lots.add(new LotProveidorDto());
        }

        dto.setLots(lots);
        return dto;
    }

    private LocalDate convertirDataOcr(String dataOcr) {
        if (dataOcr == null || dataOcr.isBlank()) {
            return LocalDate.now();
        }

        List<DateTimeFormatter> formats = List.of(
                DateTimeFormatter.ofPattern("dd/MM/yyyy"),
                DateTimeFormatter.ofPattern("d/M/yyyy"),
                DateTimeFormatter.ofPattern("dd-MM-yyyy"),
                DateTimeFormatter.ofPattern("d-M-yyyy"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd"),
                DateTimeFormatter.ofPattern("yyyy/MM/dd")
        );

        for (DateTimeFormatter format : formats) {
            try {
                return LocalDate.parse(dataOcr.trim(), format);
            } catch (DateTimeParseException ignored) {
                // Prova el format següent.
            }
        }

        return LocalDate.now();
    }

    private String extreureNomProveidorDetectat(String textDetectat) {
        if (textDetectat == null || textDetectat.isBlank()) {
            return null;
        }

        String prefix = "PROVEIDOR DETECTAT OCR:";

        for (String linia : textDetectat.split("\\R")) {
            if (linia.toUpperCase(Locale.ROOT).startsWith(prefix)) {
                return linia.substring(prefix.length()).trim();
            }
        }

        return null;
    }

    private void carregarDadesFormulari(Model model, AlbaraProveidorDto dto) {
        albaraProveidorService.assegurarMinimUnLot(dto);

        model.addAttribute("albara", dto);
        model.addAttribute("proveidors", proveidorService.findAll());
        model.addAttribute("materiesPrimeres", materiaPrimaService.findAll());
    }

    private void afegirErrorFormulari(Model model, String codiError, Locale locale) {
        String text = missatge(codiError, locale);

        model.addAttribute("errorNegoci", text);
        model.addAttribute("missatgeError", text);
    }

    private String missatge(String codi, Locale locale) {
        return messageSource.getMessage(codi, null, codi, locale);
    }
}
