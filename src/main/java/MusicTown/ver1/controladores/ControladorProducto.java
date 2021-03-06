package MusicTown.ver1.controladores;

import MusicTown.ver1.Entidades.Producto;
import MusicTown.ver1.errores.ErrorServicio;
import MusicTown.ver1.servicio.ProductoServicio;
import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/producto")
public class ControladorProducto {
    /* Coneccion con Servis Producto*/
    @Autowired
    private ProductoServicio producServis;

	    @GetMapping("/catalogo")
	    public String mostrarCatalogo(ModelMap modelo ,
	            RedirectAttributes redirectAtt){
	        try {
	            modelo.addAttribute("listaProductos", producServis.buscarTodosProdutos());
	            return "pages/catalogo.html";
	        } catch (Exception e) {
	            redirectAtt.addFlashAttribute("error" , e);
	            return "pages/catalogo.html";
	        }
	    }
    
    
    @GetMapping("/{id}") /*Metodo Para mostra 1 Solo producto*/
    public String mostrarUnSoloProducto(){
        return "VistaProducto.html";
    }
    
   @PostMapping("/guardarProducto")
   public String guardarProducto(@RequestParam("nombreProducto") String nombreProducto , 
                                 @RequestParam("cantidadProducto") int cantidadProducto,                                                       
                                 @RequestParam("precioProducto") float precioProducto,
                                 @RequestParam("marcaProducto") String marcaProducto,
                                 @RequestParam("fotoProducto") Collection<MultipartFile> fotoProducto,
                                 ModelMap modelo
           ){
       try {
           producServis.crearNuevoProducto(nombreProducto, fotoProducto, cantidadProducto, precioProducto, marcaProducto , null);
       } catch (Exception e) {
           System.out.println(e);
           modelo.addAttribute("Erorr",e);
       }
       return "redirect:/catalogo";
    }
   
   @GetMapping("/descripcionproducto/{IdProducto}")
   public String verdetalleproducto(ModelMap modelo, @PathVariable("IdProducto") String IdProducto) throws ErrorServicio{
       try {
            Producto producto = producServis.buscarUnProductoXID(IdProducto);
            modelo.put("productos", producto);            
        } catch (ErrorServicio e) {           
            throw new ErrorServicio("Producto no econtrado");
        }        
      
       
        return "pages/descripcionproducto.html";
       
   }
   
   @PostMapping("/descripcionproducto")
    public String verproducto(
            @PathVariable("IdProducto") String IdProducto,
            @RequestParam("nombreProducto") String nombreProducto , 
            @RequestParam("cantidadProducto") int cantidadProducto,                                                       
            @RequestParam("precioProducto") float precioProducto,
            @RequestParam("marcaProducto") String marcaProducto,
            @RequestParam("fotoProducto") Collection<MultipartFile> fotoProducto,
                                 ModelMap modelo) throws ErrorServicio, InterruptedException {
        try {
                    
        producServis.buscarUnProductoXID(IdProducto);
            //model.put("exito", "Compra realizada con ??xito");
        } catch (Exception e) {
            e.printStackTrace();
            throw new ErrorServicio("Error de Sistema");
        }


        return "redirect:/";
    }
}
				