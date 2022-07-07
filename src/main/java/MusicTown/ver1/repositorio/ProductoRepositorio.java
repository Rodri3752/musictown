
package MusicTown.ver1.repositorio;
/*Entidad*/

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import MusicTown.ver1.Entidades.Producto;

@Repository
public interface ProductoRepositorio extends JpaRepository<Producto, String> {
    
}
