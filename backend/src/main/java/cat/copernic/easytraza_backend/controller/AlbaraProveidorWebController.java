package cat.copernic.easytraza_backend.controller;

import cat.copernic.easytraza_backend.dto.AlbaraProveidorDto;
import cat.copernic.easytraza_backend.dto.LotProveidorDto;
import cat.copernic.easytraza_backend.dto.OcrAlbaraResponseDto;
import cat.copernic.easytraza_backend.dto.OcrLotRespostaDto;
import cat.copernic.easytraza_backend.model.AlbaraProveidor;
import cat.copernic.easytraza_backend.model.MateriaPrima;
import cat.copernic.easytraza_backend.model.Proveidor;
import cat.copernic.easytraza_backend.repository.MateriaPrimaRepository;
import cat.copernic.easytraza_backend.repository.ProveidorRepository;
import cat.copernic.easytraza_backend.service.AlbaraProveidorService;
import cat.copernic.easytraza_backend.service.OcrAlbaraService;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/web/albarans-proveidor")
public class AlbaraProveidorWebController {

    @Autowired
    private AlbaraProveidorService albaraProveidorService;

    @Autowired
    private OcrAlbaraService ocrAlbaraService;

    @Autowired
    private ProveidorRepository proveidorRepository;

    @Autowired
    private MateriaPrimaRepository materiaPrimaRepository;

    @Autowired
    private MessageSource messageSource;

    @GetMapping
    public String llistar(@RequestParam(required = false) String proveidorCif,
            @RequestParam(required = false) LocalDate dataRecepcio,
            Model model) {

        model.addAttribute("albarans", albaraProveidorService.buscar(proveidorCif, dataRecepcio));
        model.addAttribute("proveidorCif", proveidorCif);
        model.addAttribute("dataRecepcio", dataRecepcio);
        model.addAttribute("currentPath", "/web/albarans-proveidor");

        return "albarans_proveidor/llistar-albarans-proveidor";
    }

    @GetMapping("/veure/{id}")
    public String veureDetall(@PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        Optional<AlbaraProveidor> albara = albaraProveidorService.findById(id);

        if (albara.isEmpty()) {
            redirectAttributes.addFlashAttribute(
                    "missatgeError",
                    messageSource.getMessage("albara.proveidor.flash.no.trobat", null, locale)
            );
            return "redirect:/web/albarans-proveidor";
        }

        model.addAttribute("albaraDetall", albara.get());
        model.addAttribute("currentPath", "/web/albarans-proveidor");

        return "albarans_proveidor/veure-albara-proveidor";
    }

    @GetMapping("/crear")
    public String mostrarFormulariCrear(Model model) {
        AlbaraProveidorDto dto = new AlbaraProveidorDto();
        dto.setDataRecepcio(LocalDate.now());
        dto.getLots().add(new LotProveidorDto());

        carregarDadesFormulari(model);
        model.addAttribute("albara", dto);
        model.addAttribute("currentPath", "/web/albarans-proveidor");

        return "albarans_proveidor/crear-albara-proveidor";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute("albara") AlbaraProveidorDto dto,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        String errorNegoci = albaraProveidorService.validarAlbara(dto, null);

        if (result.hasErrors() || errorNegoci != null) {
            carregarDadesFormulari(model);
            albaraProveidorService.assegurarMinimUnLot(dto);

            if (errorNegoci != null) {
                model.addAttribute("errorNegoci", messageSource.getMessage(errorNegoci, null, locale));
            }

            model.addAttribute("currentPath", "/web/albarans-proveidor");
            return "albarans_proveidor/crear-albara-proveidor";
        }

        AlbaraProveidor entity = albaraProveidorService.convertirDtoAEntity(dto);
        albaraProveidorService.save(entity);

        redirectAttributes.addFlashAttribute(
                "missatgeExit",
                messageSource.getMessage("albara.proveidor.flash.creat", null, locale)
        );

        return "redirect:/web/albarans-proveidor";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormulariEditar(@PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        Optional<AlbaraProveidor> albara = albaraProveidorService.findById(id);

        if (albara.isEmpty()) {
            redirectAttributes.addFlashAttribute(
                    "missatgeError",
                    messageSource.getMessage("albara.proveidor.flash.no.trobat", null, locale)
            );
            return "redirect:/web/albarans-proveidor";
        }

        if (!albaraProveidorService.esAlbaraEditable(albara.get())) {
            redirectAttributes.addFlashAttribute(
                    "missatgeError",
                    messageSource.getMessage("albara.proveidor.error.modificar.lots.no.estoc", null, locale)
            );
            return "redirect:/web/albarans-proveidor/veure/" + id;
        }

        carregarDadesFormulari(model);
        model.addAttribute("albara", albaraProveidorService.convertirEntityADto(albara.get()));
        model.addAttribute("currentPath", "/web/albarans-proveidor");

        return "albarans_proveidor/editar-albara-proveidor";
    }

    @PostMapping("/actualitzar/{id}")
    public String actualitzar(@PathVariable Long id,
            @ModelAttribute("albara") AlbaraProveidorDto dto,
            Model model,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        String errorNegoci = albaraProveidorService.validarAlbara(dto, id);

        if (errorNegoci != null) {
            carregarDadesFormulari(model);
            albaraProveidorService.assegurarMinimUnLot(dto);
            model.addAttribute("errorNegoci", messageSource.getMessage(errorNegoci, null, locale));
            model.addAttribute("currentPath", "/web/albarans-proveidor");

            return "albarans_proveidor/editar-albara-proveidor";
        }

        try {
            AlbaraProveidor entity = albaraProveidorService.convertirDtoAEntity(dto);
            albaraProveidorService.update(id, entity);

            redirectAttributes.addFlashAttribute(
                    "missatgeExit",
                    messageSource.getMessage("albara.proveidor.flash.actualitzat", null, locale)
            );

            return "redirect:/web/albarans-proveidor";

        } catch (IllegalStateException ex) {
            carregarDadesFormulari(model);
            albaraProveidorService.assegurarMinimUnLot(dto);
            model.addAttribute("errorNegoci", messageSource.getMessage(ex.getMessage(), null, locale));
            model.addAttribute("currentPath", "/web/albarans-proveidor");

            return "albarans_proveidor/editar-albara-proveidor";
        }
    }

    @GetMapping("/ocr")
    public String mostrarFormulariOcr(Model model) {
        model.addAttribute("currentPath", "/web/albarans-proveidor");
        return "albarans_proveidor/ocr-albara-proveidor";
    }

    @PostMapping("/ocr/processar")
    public String processarOcr(@RequestParam("fitxer") MultipartFile fitxer,
            Model model,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        try {
            if (fitxer == null || fitxer.isEmpty()) {
                redirectAttributes.addFlashAttribute(
                        "missatgeError",
                        messageSource.getMessage("albara.proveidor.ocr.fitxer.obligatori", null, locale)
                );
                return "redirect:/web/albarans-proveidor/ocr";
            }

            OcrAlbaraResponseDto resposta = ocrAlbaraService.processarImatgeAlbara(fitxer);
            AlbaraProveidorDto albara = convertirOcrAAlbaraDto(resposta);

            carregarDadesFormulari(model);
            model.addAttribute("ocrResposta", resposta);
            model.addAttribute("albara", albara);
            model.addAttribute("currentPath", "/web/albarans-proveidor");

            return "albarans_proveidor/revisar-ocr-albara-proveidor";

        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute(
                    "missatgeError",
                    messageSource.getMessage("albara.proveidor.ocr.error.processar", null, locale)
            );
            return "redirect:/web/albarans-proveidor/ocr";
        }
    }

    @PostMapping("/ocr/guardar")
    public String guardarDesDeOcr(@ModelAttribute("albara") AlbaraProveidorDto dto,
            Model model,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        String errorNegoci = albaraProveidorService.validarAlbara(dto, null);

        if (errorNegoci != null) {
            carregarDadesFormulari(model);
            albaraProveidorService.assegurarMinimUnLot(dto);
            model.addAttribute("errorNegoci", messageSource.getMessage(errorNegoci, null, locale));
            model.addAttribute("currentPath", "/web/albarans-proveidor");

            return "albarans_proveidor/revisar-ocr-albara-proveidor";
        }

        AlbaraProveidor entity = albaraProveidorService.convertirDtoAEntity(dto);
        albaraProveidorService.save(entity);

        redirectAttributes.addFlashAttribute(
                "missatgeExit",
                messageSource.getMessage("albara.proveidor.flash.creat", null, locale)
        );

        return "redirect:/web/albarans-proveidor";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        try {
            albaraProveidorService.deleteById(id);

            redirectAttributes.addFlashAttribute(
                    "missatgeExit",
                    messageSource.getMessage("albara.proveidor.flash.eliminat", null, locale)
            );

        } catch (IllegalStateException ex) {
            redirectAttributes.addFlashAttribute(
                    "missatgeError",
                    messageSource.getMessage(ex.getMessage(), null, locale)
            );

        } catch (DataIntegrityViolationException ex) {
            redirectAttributes.addFlashAttribute(
                    "missatgeError",
                    messageSource.getMessage("albara.proveidor.error.eliminar.relacions", null, locale)
            );

        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute(
                    "missatgeError",
                    messageSource.getMessage("albara.proveidor.error.eliminar.generic", null, locale)
            );
        }

        return "redirect:/web/albarans-proveidor";
    }

    private void carregarDadesFormulari(Model model) {
        model.addAttribute("proveidors", proveidorRepository.findAll());
        model.addAttribute("materiesPrimeres", materiaPrimaRepository.findAll());
    }

    private AlbaraProveidorDto convertirOcrAAlbaraDto(OcrAlbaraResponseDto resposta) {
        AlbaraProveidorDto dto = new AlbaraProveidorDto();

        dto.setDataRecepcio(parseDataOcr(resposta.getDataAlbara()));
        dto.setProveidorCif(resoldreProveidorExistent(resposta));

        List<LotProveidorDto> lots = new ArrayList<>();

        if (resposta.getLots() != null) {
            for (OcrLotRespostaDto lotOcr : resposta.getLots()) {
                LotProveidorDto lotDto = new LotProveidorDto();
                lotDto.setCodiLot(lotOcr.getCodiLot());
                lotDto.setQuantitat(lotOcr.getQuantitat() != null ? lotOcr.getQuantitat().intValue() : null);
                lotDto.setMateriaPrimaId(resoldreMateriaPrimaExistent(lotOcr.getMateriaPrima()));
                lots.add(lotDto);
            }
        }

        if (lots.isEmpty()) {
            lots.add(new LotProveidorDto());
        }

        dto.setLots(lots);
        return dto;
    }

    private LocalDate parseDataOcr(String valor) {
        if (valor == null || valor.isBlank()) {
            return LocalDate.now();
        }

        List<DateTimeFormatter> formatters = List.of(
                DateTimeFormatter.ofPattern("dd/MM/yyyy"),
                DateTimeFormatter.ofPattern("dd-MM-yyyy"),
                DateTimeFormatter.ofPattern("yyyy/MM/dd"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd")
        );

        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDate.parse(valor.trim(), formatter);
            } catch (DateTimeParseException ignored) {
            }
        }

        return LocalDate.now();
    }

    private String resoldreProveidorExistent(OcrAlbaraResponseDto resposta) {
        String cifDetectat = normalitzarDocument(resposta.getProveidorCif());

        if (cifDetectat != null && !cifDetectat.isBlank()) {
            Optional<Proveidor> existentPerCif = proveidorRepository.findById(cifDetectat);

            if (existentPerCif.isPresent()) {
                return existentPerCif.get().getCif();
            }
        }

        if (resposta.getTextDetectat() != null) {
            String text = resposta.getTextDetectat().toLowerCase();

            for (Proveidor proveidor : proveidorRepository.findAll()) {
                if (proveidor.getNom() != null
                        && !proveidor.getNom().isBlank()
                        && text.contains(proveidor.getNom().toLowerCase())) {
                    return proveidor.getCif();
                }

                if (proveidor.getCif() != null
                        && !proveidor.getCif().isBlank()
                        && text.contains(proveidor.getCif().toLowerCase())) {
                    return proveidor.getCif();
                }
            }
        }

        return null;
    }

    private Long resoldreMateriaPrimaExistent(String nomMateria) {
        String nomNet = netejarNomMateria(nomMateria);

        if (nomNet == null || nomNet.isBlank()) {
            return null;
        }

        Optional<MateriaPrima> exactaIgnoreCase = materiaPrimaRepository.findByNomIgnoreCase(nomNet);

        if (exactaIgnoreCase.isPresent()) {
            return exactaIgnoreCase.get().getId();
        }

        String nomNormalitzat = nomNet.trim().toLowerCase();

        for (MateriaPrima materia : materiaPrimaRepository.findAll()) {
            if (materia.getNom() == null || materia.getNom().isBlank()) {
                continue;
            }

            String nomMateriaSistema = materia.getNom().trim().toLowerCase();

            if (nomMateriaSistema.equals(nomNormalitzat)) {
                return materia.getId();
            }

            if (nomMateriaSistema.contains(nomNormalitzat)) {
                return materia.getId();
            }

            if (nomNormalitzat.contains(nomMateriaSistema)) {
                return materia.getId();
            }
        }

        return null;
    }

    private String netejarNomMateria(String nomMateria) {
        if (nomMateria == null) {
            return null;
        }

        String valor = nomMateria
                .replace("\n", " ")
                .replace("\r", " ")
                .trim()
                .replaceAll("\\s{2,}", " ");

        valor = valor.replaceAll("(?i)\\b\\d+(?:[\\.,]\\d+)?\\s*(kg|g|gr|grams|grams\\.|uds|ud|unitats|unitats\\.)\\b", "")
                .trim()
                .replaceAll("\\s{2,}", " ");

        if (valor.length() < 2) {
            return null;
        }

        return valor;
    }

    private String normalitzarDocument(String document) {
        if (document == null) {
            return null;
        }

        return document.trim().toUpperCase().replace(" ", "");
    }
}
