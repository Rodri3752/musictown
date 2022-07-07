package MusicTown.ver1.servicio;

import MusicTown.ver1.Entidades.Usuario;
import MusicTown.ver1.Enum.Roles;
import MusicTown.ver1.MailSender.MailNotificaciones;
import MusicTown.ver1.errores.ErrorServicio;
import MusicTown.ver1.repositorio.UsuarioRepositorio;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


@Service
public class UsuarioServicios implements UserDetailsService {
    private boolean bool = true;
 @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    
    @Autowired
    private MailNotificaciones mailNotificaciones;

    @Transactional
    public void registrar(String nombre, String mail, String clave, String clave1) throws ErrorServicio {
        validar(nombre, mail, clave, clave1);
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setMail(mail);
        String encriptada = new BCryptPasswordEncoder().encode(clave);
        usuario.setClave(encriptada);
        usuario.setAlta(new Date());
        usuario.setRol(Roles.USUARIOREGISTRADO); //agregado para que todos sean usuarios registrados
        
        mailNotificaciones.mailsender(mail, "Bienvenido/a a Music Town "+ nombre + "!" , "Usted se ha registrado de manera exitosa");

        usuarioRepositorio.save(usuario);
    }

    @Transactional
    public void modificar(String id, String nombre, String mail, String clave, String clave1) throws ErrorServicio {
        Optional<Usuario> respuesta = usuarioRepositorio.findById(id);
        if (respuesta.isPresent()) {

            Usuario usuario = respuesta.get();
            if(nombre != null){
            usuario.setNombre(nombre);
            }
            if(mail != null){
            usuario.setMail(mail);
            }
            if(clave != null){
            validar1(clave,clave1);
            validarbool(clave, clave1);
            if(bool == false){
            String encriptada = new BCryptPasswordEncoder().encode(clave);
            usuario.setClave(encriptada);}}
            usuarioRepositorio.save(usuario);
        } else {
            throw new ErrorServicio("No se encontro el usuario solicitado.");
        }
    }

    @Transactional
    public void deshabilitar(String id) throws ErrorServicio {
        Optional<Usuario> respuesta = usuarioRepositorio.findById(id);
        if (respuesta.isPresent()) {
            Usuario usuario = respuesta.get();
            usuario.setBaja(new Date());
            usuarioRepositorio.save(usuario);
        } else {
            throw new ErrorServicio("No se encontro el usuario solicitado.");
        }
    }

    @Transactional
    public void habilitar(String id) throws ErrorServicio {
        Optional<Usuario> respuesta = usuarioRepositorio.findById(id);
        if (respuesta.isPresent()) {
            Usuario usuario = respuesta.get();
            usuario.setBaja(null);
            usuarioRepositorio.save(usuario);
        } else {
            throw new ErrorServicio("No se encontro el usuario solicitado.");
        }
    }

    private void validar(String nombre, String mail, String clave, String clave1) throws ErrorServicio {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ErrorServicio("El nombre del usuario no puede ser nulo");
        }
        if (mail == null || mail.trim().isEmpty()) {
            throw new ErrorServicio("El mail del usuario no puede ser nulo");
        }
        if (clave == null || clave.trim().isEmpty() || clave.length() < 6) {
            throw new ErrorServicio("El clave del usuario no puede ser nulo, ni tener menos de 6 digitos");
        }
        if (!(clave.equals(clave1))) {
            throw new ErrorServicio("La clave y la verificacion de la clave deben ser iguales");
        }
    }
    
    private void validar1(String clave, String clave1) throws ErrorServicio {
        
        if (clave == null || clave.trim().isEmpty() || clave.length() < 6) {
         throw new ErrorServicio("La clave no puede tener menos de 6 digitos");
        }
        
        if (!(clave.equals(clave1))) {
            throw new ErrorServicio("La clave y la verificacion de la clave deben ser iguales");
        }
    }

    private void validarbool(String clave, String clave1){
       boolean si= false;
        if (clave.length() < 6) {
         si = true;
        }
        
        if (!(clave.equals(clave1))) {
           si = true;
        }
        bool=si;
    }
    
    @Override
    public UserDetails loadUserByUsername(String mail) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepositorio.buscarPorMail(mail);
        if (usuario != null) {

            List<GrantedAuthority> permisos = new ArrayList<>();

    if(usuario.getRol().equals(Roles.ADMIN)){
                 GrantedAuthority p1 = new SimpleGrantedAuthority("ROLE_ADMIN");
                 permisos.add(p1);
                 GrantedAuthority p2 = new SimpleGrantedAuthority("ROLE_USUARIOREGISTRADO");
                 permisos.add(p2);
            }else{
                GrantedAuthority p1 = new SimpleGrantedAuthority("ROLE_USUARIOREGISTRADO");
                 permisos.add(p1);
            }
            
            ServletRequestAttributes attr = (ServletRequestAttributes)RequestContextHolder.currentRequestAttributes();
            HttpSession sesion = attr.getRequest().getSession(true);
            sesion.setAttribute("datosUsuario", usuario);
            
             User user = new User(usuario.getNombre(), usuario.getClave(), permisos);
            
            return user;
        } else {
            return null;
        }
    }
    
    public Usuario buscarPorId(String idUs) throws ErrorServicio{
        Optional<Usuario> usuario = usuarioRepositorio.findById(idUs);
        
        if(usuario.isPresent()){
           Usuario UsuarioReturn =  usuario.get();
           return UsuarioReturn;
        }else{
            throw new ErrorServicio("No se encontro el usuario");
        }

    }
}
