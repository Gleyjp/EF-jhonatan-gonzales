package cibertec.com.pe.examen.controller;

import cibertec.com.pe.examen.model.Boleto;
import cibertec.com.pe.examen.model.Ciudad;
import cibertec.com.pe.examen.model.Venta;
import cibertec.com.pe.examen.model.VentaDetalle;
import cibertec.com.pe.examen.repository.ICiudadRepository;
import cibertec.com.pe.examen.repository.IVentaDetalleRepository;
import cibertec.com.pe.examen.repository.IVentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Controller
@SessionAttributes({"boletosSesion"})
public class CarritoController {

    @Autowired
    private ICiudadRepository ciudadRepository;

    @Autowired
    private IVentaRepository ventaRepository;

    @Autowired
    private IVentaDetalleRepository ventaDetalleRepository;

    @GetMapping("/")
    public String inicioSlash(Model model) {
        List<Ciudad> ciudades = ciudadRepository.findAll();
        List<Boleto> boletos = (List<Boleto>) model.getAttribute("boletosSesion");

        if(boletos.size()>0){
            Boleto boletoEncontrado = boletos.get(boletos.size()-1);
            model.addAttribute("boleto", boletoEncontrado);
        }else{
            model.addAttribute("boleto", new Boleto());
        }

        model.addAttribute("ciudades", ciudades);


        return "index";
    }

    @GetMapping("/inicio")
    public String inicio(Model model) {
        List<Ciudad> ciudades = ciudadRepository.findAll();
        List<Boleto> boletos = (List<Boleto>) model.getAttribute("boletosSesion");

        if(boletos.size()>0){
            Boleto boletoEncontrado = boletos.get(boletos.size()-1);
            model.addAttribute("boleto", boletoEncontrado);
        }else{
            model.addAttribute("boleto", new Boleto());
        }

        model.addAttribute("ciudades", ciudades);

        return "index";
    }

    @PostMapping("/agregar-boleto")
    public String agregarBoleto(Model model, @ModelAttribute Boleto boleto) {
        List<Ciudad> ciudades = ciudadRepository.findAll();
        List<Boleto> boletos = (List<Boleto>) model.getAttribute("boletosSesion");
        Double precioBoleto = 50.00;

        boleto.setSubTotal(boleto.getCantidad() * precioBoleto);

        boletos.add(boleto);

        model.addAttribute("boletosSesion", boletos);
        model.addAttribute("ciudades", ciudades);
        model.addAttribute("boleto", new Boleto());

        return "redirect:/";
    }

    @GetMapping("/comprar")
    public String comprar(Model model) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd", Locale.ENGLISH);
        List<Boleto> boletos = (List<Boleto>) model.getAttribute("boletosSesion");
        Double montoTotal = 0.0;

        for (Boleto boleto : boletos) {
            montoTotal += boleto.getSubTotal();
        }

        Venta nuevaVenta = new Venta();
        nuevaVenta.setFechaVenta(new Date());
        nuevaVenta.setMontoTotal(montoTotal);
        nuevaVenta.setNombreComprador(boletos.get(0).getNombreComprador());

        Venta ventaGuardada = ventaRepository.save(nuevaVenta);

        for (Boleto boleto : boletos) {
            VentaDetalle ventaDetalle = new VentaDetalle();

            Ciudad ciudadDestino = ciudadRepository.findById(boleto.getCiudadDestino()).get();
            ventaDetalle.setCiudadDestino(ciudadDestino);
            Ciudad ciudadOrigen = ciudadRepository.findById(boleto.getCiudadOrigen()).get();
            ventaDetalle.setCiudadOrigen(ciudadOrigen);

            ventaDetalle.setCantidad(boleto.getCantidad());
            ventaDetalle.setSubTotal(boleto.getSubTotal());

            Date fechaRetorno = formatter.parse(boleto.getFechaRetorno());
            ventaDetalle.setFechaRetorno(fechaRetorno);

            Date fechaSalida = formatter.parse(boleto.getFechaSalida());
            ventaDetalle.setFechaViaje(fechaSalida);

            ventaDetalle.setVenta(ventaGuardada);

            ventaDetalleRepository.save(ventaDetalle);
        }

        return "redirect:/";
    }

    @GetMapping("/limpiar")
    public String limpiar(Model model){
        List<Ciudad> ciudades = ciudadRepository.findAll();

        model.addAttribute("boleto", new Boleto());
        model.addAttribute("ciudades", ciudades);

        return "index";
    }

    @ModelAttribute("boletosSesion")
    public List<Boleto> boletosComprados() {
        return new ArrayList<>();
    }
}
