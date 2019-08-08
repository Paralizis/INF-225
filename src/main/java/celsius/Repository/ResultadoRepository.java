package celsius.Repository;

import celsius.Model.Comentario;
import celsius.Model.Proyecto;
import celsius.Model.Resultado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ResultadoRepository extends JpaRepository<Resultado,Long> {

	@Query("SELECT r FROM Resultado r WHERE r.proyecto = :proyecto")
	public List<Resultado> findResultados(@Param("proyecto") Proyecto proyecto);

	public Resultado findByProyecto(Proyecto proyecto);
}
