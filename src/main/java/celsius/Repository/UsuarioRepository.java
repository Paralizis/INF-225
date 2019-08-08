package celsius.Repository;

import celsius.Model.Proyecto;
import celsius.Model.Usuario;
import celsius.Model.UsuarioProyecto;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UsuarioRepository extends JpaRepository<Usuario,Long> {

    Usuario findByCorreo(String correo);

    Usuario findByRol(Long rol);

		Usuario findByNombre(String nombre);

		@Query("SELECT u FROM Usuario u WHERE u.enabled = True")
		public Usuario getEnabled();

		@Query("SELECT u FROM Usuario u, UsuarioProyecto up WHERE u = up.usuario AND up.proyecto = :proyecto")
		List<Usuario> findUsuariosProyecto(@Param("proyecto") Proyecto proyecto);

		@Query("SELECT u FROM Usuario u, UsuarioProyecto up WHERE NOT(u = up.usuario AND up.proyecto = :proyecto)")
		List<Usuario> findUsuarioNoProyecto(@Param("proyecto") Proyecto proyecto);
}
