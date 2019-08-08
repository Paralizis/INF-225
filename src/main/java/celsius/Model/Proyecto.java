package celsius.Model;

import lombok.AccessLevel;

import lombok.Getter;
import lombok.Setter;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

import org.hibernate.annotations.NaturalIdCache;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Setter(AccessLevel.PUBLIC)
@Getter(AccessLevel.PUBLIC)
@Entity
public class Proyecto extends Auditable<String> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.ORDINAL)
    private Estado estado;

    @Column(columnDefinition = "TEXT")
    private String titulo;
    
    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @OneToMany(mappedBy = "proyecto", cascade = CascadeType.ALL)
    private Set<Comentario> comentarios;
    
    @OneToMany(mappedBy = "proyecto", cascade = CascadeType.ALL)
    private Set<Resultado> resultados;

//		@OneToMany(mappedBy = "proyecto", cascade = CascadeType.ALL, orphanRemoval = true)
//		private List<UsuarioProyecto> usuarios = new ArrayList<>();

    @OneToMany(mappedBy = "proyecto")
    private Set<UsuarioProyecto> usuarioProyectos = new HashSet<UsuarioProyecto>();
    
    
		public Set<UsuarioProyecto> getUsuarioProyectos() {
			return usuarioProyectos;
		}

		public void setUsuarioProyectos(Set<UsuarioProyecto> usuarioProyectos) {
			this.usuarioProyectos = usuarioProyectos;
		}

		public Long getId() {
			return id;
		}

//		public List<UsuarioProyecto> getUsuarios() {
//			return usuarios;
//		}
//
//		public void setUsuarios(List<UsuarioProyecto> usuarios) {
//			this.usuarios = usuarios;
//		}

		public void setId(Long id) {
			this.id = id;
		}


		public Estado getEstado() {
			return estado;
		}


		public void setEstado(Estado estado) {
			this.estado = estado;
		}


		public String getTitulo() {
			return titulo;
		}


		public void setTitulo(String titulo) {
			this.titulo = titulo;
		}


		public String getDescripcion() {
			return descripcion;
		}


		public void setDescripcion(String descripcion) {
			this.descripcion = descripcion;
		}


		public Set<Comentario> getComentarios() {
			return comentarios;
		}


		public void setComentarios(Set<Comentario> comentarios) {
			this.comentarios = comentarios;
		}

		public Set<Resultado> getResultados() {
			return resultados;
		}


		public void setResultados(Set<Resultado> resultados) {
			this.resultados = resultados;
		}


		public static enum Estado {
        EN_ESPERA("En espera", "warning"),
        ARPOBADO("Aprobado", "success"),
        RECHAZADO("Rechazado", "danger");

        private final String nombre;
        private final String etiqueta;

        Estado(String nombre, String etiqueta) {
          this.nombre = nombre;
          this.etiqueta = etiqueta;
        }

        public String getNombre() {
          return nombre;
        }

        public String getEtiqueta() {
          return etiqueta;
        }
        
        
        public String toString() {
          return this.nombre;
        }
    }
		
//		public void addUsuario(Usuario usuario) {
//			UsuarioProyecto usuarioProyecto = new UsuarioProyecto(usuario, this);
//			usuarios.add(usuarioProyecto);
//			usuario.getProyectos().add(usuarioProyecto);
//		}
//		
//		public void removeUsuario(Usuario usuario) {
//			for (Iterator<UsuarioProyecto> iterator = usuarios.iterator(); iterator.hasNext();) {
//				UsuarioProyecto usuarioProyecto = iterator.next();
//				if (usuarioProyecto.getProyecto().equals(this) && usuarioProyecto.getUsuario().equals(usuarios)) {
//					iterator.remove();
//					usuarioProyecto.getUsuario().getProyectos().remove(usuarioProyecto);
//					usuarioProyecto.setProyecto(null);
//					usuarioProyecto.setUsuario(null);
//				}
//			}
//		}
		
		@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Proyecto proyecto = (Proyecto) o;
        return Objects.equals(titulo, proyecto.titulo);
    }
 
    @Override
    public int hashCode() {
        return Objects.hash(titulo);
    }
    
}
