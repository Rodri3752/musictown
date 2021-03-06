package MusicTown.ver1.controladores;

import java.util.Collection;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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

import MusicTown.ver1.Entidades.Producto;
import MusicTown.ver1.Entidades.Usuario;
import MusicTown.ver1.errores.ErrorServicio;
import MusicTown.ver1.repositorio.ProductoRepositorio;
import MusicTown.ver1.servicio.ProductoServicio;
import MusicTown.ver1.servicio.UsuarioServicios;

@Controller
@RequestMapping("/admin")
public class ControladorAdmin {

	@Autowired
	private UsuarioServicios usuarioServicios;

	@Autowired
	private ProductoServicio productoServicio;

	@Autowired
	private ProductoRepositorio productoRepositorio;

	// administrar Instrumentos

	@GetMapping("/crearInstrumento")
	public String crearinstrumentostatic() {
		return "/admin/crearInstrumento.html";
	}

	@PostMapping("/guardarProducto")
	public String guardarProducto(@RequestParam("nombreProducto") String nombreProducto,
			@RequestParam("cantidadProducto") int cantidadProducto,
			@RequestParam("precioProducto") float precioProducto, @RequestParam("marcaProducto") String marcaProducto,
			@RequestParam("fotoProducto") Collection<MultipartFile> fotoProducto, ModelMap modelo) {
		try {
			productoServicio.crearNuevoProducto(nombreProducto, fotoProducto, cantidadProducto, precioProducto,
					marcaProducto, null);
		} catch (Exception e) {
			System.out.println(e);
			modelo.addAttribute("Erorr", e);
		}
		return "redirect:/admin";
	}
	@GetMapping("/actualizar/{IdProducto}")
	public String editarProductos(@PathVariable("IdProducto") String IdProducto,
			ModelMap modelo) {
		
		try {
			Producto productoEditar = productoServicio.buscarUnProductoXID(IdProducto);
			modelo.addAttribute("producto",productoEditar);
		} catch (ErrorServicio e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "/admin/actualizarInstrumento.html";
	}

	@PostMapping("/editarProducto/{IdProducto}")
	public String editarProducto(@RequestParam("nombreProducto") String nombreProducto,
			@RequestParam("cantidadProducto") int cantidadProducto,
			@RequestParam("precioProducto") float precioProducto, 
			@PathVariable("IdProducto") String IdProducto,
			@RequestParam("marcaProducto") String marcaProducto,
			@RequestParam("fotoProducto") Collection<MultipartFile> fotoProducto, ModelMap modelo) {
		try {
			
			
			productoServicio.editarProducto(nombreProducto, fotoProducto, cantidadProducto, precioProducto, marcaProducto, IdProducto);
                        modelo.addAttribute("ActExito", "Exito");
                } catch (Exception e) {
			System.out.println(e);
			modelo.addAttribute("Erorr", e);
		}
		return "redirect:/admin";
	}
	

	@GetMapping("")
	public String mostrarCatalogo(ModelMap modelo, RedirectAttributes redirectAtt) {
		try {
			modelo.addAttribute("listarProductos", productoServicio.buscarTodosProdutos());
			return "/admin/instrumentos.html";
		} catch (Exception e) {
			redirectAtt.addFlashAttribute("error", e);
			return "/admin/instrumentos.html";
		}
	}

}
