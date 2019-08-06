package celsius.Controller;

import celsius.Model.Proyecto;
import celsius.Model.Comentario;
import celsius.Model.Job;
import celsius.Model.Resultado;
import celsius.Model.Usuario;
import celsius.Model.UsuarioProyecto;
import celsius.Repository.ProyectoRepository;
import celsius.Repository.ComentarioRepository;
import celsius.Repository.JobRepository;
import celsius.Repository.ResultadoRepository;
import celsius.Repository.UsuarioProyectoRepository;
import celsius.Repository.UsuarioRepository;
import celsius.Helper.MessageHelper;
import celsius.Helper.UpdateHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.validation.BindingResult;
import org.springframework.ui.Model;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping(value={"/proyecto", "/proyectos"})
public class ProyectoController {

    @Autowired
    ProyectoRepository proyectoRepository;

    @Autowired
    ComentarioRepository comentarioRepository;

    @Autowired
    ResultadoRepository resultadoRepository;
    
    @Autowired
    UsuarioRepository usuarioRepository;
    
    @Autowired
    JobRepository jobRepository;
    
    @Autowired
    UsuarioProyectoRepository usuarioProyectoRepository;

    @ModelAttribute(value = "comentario")
    public Comentario getComentario() {
      return new Comentario();
    }

    @ModelAttribute(value = "resultado")
    public Resultado getResultado() {
      return new Resultado();
    }

    @ModelAttribute(value = "usuarioProyecto")
    public UsuarioProyecto getUsuarioProyecto() {
      return new UsuarioProyecto();
    }
    
    @ModelAttribute
    public void addAttributes(Model model) {
    	Usuario usuario = usuarioRepository.getEnabled();
    	model.addAttribute("admin", (usuario.getRol().name() == "ADMINISTRADOR" ? true : false));
    	model.addAttribute("anonimo", (usuario.getRol().name() == "ANONIMO" ? true : false));
    	model.addAttribute("miembro", (usuario.getRol().name() == "MIEMBRO" ? true : false));
    	model.addAttribute("ayudante", (usuario.getRol().name() == "AYUDANTE" ? true : false));
    	model.addAttribute("usuario", usuario);
    }
    
    private String getPermiso() {
      Usuario usuario = usuarioRepository.getEnabled();
      UsuarioProyecto usuarioProyecto = usuarioProyectoRepository.findByUsuario(usuario);
      if (usuarioProyecto != null) {
      	return usuarioProyecto.getTipo().name();
      }
      return null;
    }

    @GetMapping(value={"/{id}", "/index/{id}"})
    public String getProyecto(@PathVariable("id") Long id, Model model, RedirectAttributes ra) {
      Optional<Proyecto> optProyecto = proyectoRepository.findById(id);
      if (!optProyecto.isPresent()) {
        //ERROR
        MessageHelper.addErrorAttribute(ra, "Error al ir a la página del proyecto. Proyecto no encontrado.");
        return "redirect:/proyecto/";
      }
      List<Comentario> comentarios = comentarioRepository.findComentariosProyecto(optProyecto.get());
      List<Resultado> resultados = resultadoRepository.findResultados(optProyecto.get());
      List<Usuario> usuarios = usuarioRepository.findAll();
      List<Job> jobs = jobRepository.findJobs(usuarioRepository.getEnabled());
      List<List<Comentario>> resultados_comentarios;
      int contador_resultados_comentarios;
      if (!resultados.isEmpty()) {
        resultados_comentarios = comentarioRepository.findComentariosResultados(resultados);
        contador_resultados_comentarios = resultados_comentarios.size();
      } else {
      	resultados_comentarios = null;
      	contador_resultados_comentarios = 0;
      }
      model.addAttribute("proyecto", optProyecto.get());
      model.addAttribute("usuarios", usuarios);
      model.addAttribute("comentarios", comentarios);
      model.addAttribute("resultados", resultados);
      model.addAttribute("jobs", jobs);
      model.addAttribute("resultados_comentarios", resultados_comentarios);
      model.addAttribute("contador_comentarios", comentarios.size());
      model.addAttribute("contador_resultados_comentarios", contador_resultados_comentarios);
      model.addAttribute("contador_resultados", resultados.size());
      
      //Permiso de miembro en proyecto
      model.addAttribute("miembro_jefe", (getPermiso() == "JEFE" ? true : false));
      model.addAttribute("miembro_normal", (getPermiso() == "MIEMBRO" ? true : false));
      model.addAttribute("miembro_mentor", (getPermiso() == "MENTOR" ? true : false));
      return "proyecto/index";
    }

    // GET crear nuevo proyecto
    @GetMapping(value = {"/new", "/nuevo"})
    public String newProyecto(Model model) {
        model.addAttribute("proyecto", new Proyecto());
        model.addAttribute("estado_inicial", Proyecto.Estado.EN_ESPERA);
        return "proyecto/new";
    }

    // POST guardar nuevo proyecto
    @PostMapping(value={"/create", "/crear"})
    public String saveProyecto(@ModelAttribute Proyecto proyecto, BindingResult errors, Model model, RedirectAttributes ra) {
      if (!errors.hasErrors()) {
        proyectoRepository.save(proyecto);
        model.addAttribute("proyectos", proyectoRepository.findAll());
        MessageHelper.addSuccessAttribute(ra, "Proyecto creado con éxito.");
      } else {
        model.addAttribute("proyecto", new Proyecto());
        model.addAttribute("estado_inicial", Proyecto.Estado.EN_ESPERA);
        MessageHelper.addErrorAttribute(ra, "Error al crear proyecto.");
      }
      return (errors.hasErrors() ? "redirect:/proyecto/new" : "redirect:/proyecto/list");
    }

    // GET editar proyecto
    @GetMapping(value = {"/edit/{id}", "/editar/{id}"})
    public String editProyecto(@PathVariable("id") Long id, Model model, RedirectAttributes ra) {
      Optional<Proyecto> optProyecto = proyectoRepository.findById(id);
      if (!optProyecto.isPresent()) {
        //ERROR
        MessageHelper.addErrorAttribute(ra, "Error al ir al formulario de edición del proyecto. Proyecto no encontrado.");
        return "";
      }
      model.addAttribute("proyecto", optProyecto.get());
      return "proyecto/edit";
    }

    // POST actualizar proyecto
    @PostMapping(value={"/update", "/actualizar"})
    public String updateProyecto(@Valid @ModelAttribute Proyecto proyecto, BindingResult errors, Model model, RedirectAttributes ra) {
      if (!errors.hasErrors()) {
      	Optional<Proyecto> existing = proyectoRepository.findById(proyecto.getId());
      	if (existing.isPresent()) {
	        UpdateHelper.copyNonNullProperties(proyecto, existing.get());
	        proyectoRepository.save(existing.get());
      	}
        model.addAttribute("proyectos", proyectoRepository.findAll());
        MessageHelper.addSuccessAttribute(ra, "Proyecto actualizado con éxito.");
      } else {
        MessageHelper.addErrorAttribute(ra, "Error al actualizar proyecto.");
        System.out.println(errors);
      }
      return (errors.hasErrors() ? "redirect:/proyecto/edit/"+proyecto.getId() : "redirect:/proyecto/list");
    }

    // GET listado de proyectos
    @GetMapping(value = {"/listado", "/lista", "/list", ""})
    public String listProyecto(Model model) {
      model.addAttribute("proyectos", proyectoRepository.findAll());
      return "proyecto/list";
    }

    //POST eliminar proyecto
    @PostMapping(value={"/delete","/eliminar"})
    public String deleteProyecto(@RequestParam("id") long id, Model model, RedirectAttributes ra) {
      Optional<Proyecto> optProyecto = proyectoRepository.findById(id);
      if (!optProyecto.isPresent()) {
        MessageHelper.addErrorAttribute(ra, "Error al eliminar proyecto, proyecto no encontrado.");
      } else {
        MessageHelper.addErrorAttribute(ra, "Proyecto eliminado con éxito.");
        proyectoRepository.delete(optProyecto.get());
      }
      model.addAttribute("proyectos", proyectoRepository.findAll());
      return "redirect:/proyecto/list";
    }

    // POST guarda nuevo comentario
    @PostMapping(value = {"/comentario/create", "/comantario/crear"})
    public String saveComentario(@ModelAttribute("comentario") Comentario comentario, BindingResult errors, Model model, RedirectAttributes ra) {
      if (!errors.hasErrors()) {
        MessageHelper.addSuccessAttribute(ra, "Comentario creado con éxito.");
        comentarioRepository.save(comentario);
      } else {
        MessageHelper.addErrorAttribute(ra, "Error al crear comentario.");
      }
      return "redirect:/proyecto/index/"+comentario.getProyecto().getId();
    }
    
    // POST guarda nuevo resultado
    @PostMapping(value = {"/resultado/create", "/resultado/crear"})
    public String saveResultado(@ModelAttribute("resultado") Resultado resultado, BindingResult errors, Model model, RedirectAttributes ra) {
      if (!errors.hasErrors()) {
        MessageHelper.addSuccessAttribute(ra, "Resultado creado con éxito.");
        resultadoRepository.save(resultado);
      } else {
        MessageHelper.addErrorAttribute(ra, "Error al crear resultado.");
      }
      return "redirect:/proyecto/index/"+resultado.getProyecto().getId();
    }
    
    // POST agregar miembros al proyecto
    @PostMapping(value={"/add_members", "/agregar_miembros"})
    public String addMembers(@ModelAttribute("usuarioProyecto") UsuarioProyecto usuarioProyecto,@RequestParam("usuario") Usuario usuario, @RequestParam("proyecto") Long id, BindingResult errors, Model model, RedirectAttributes ra) {
    	Optional<Proyecto> proyecto = proyectoRepository.findById(id);
    	if (!errors.hasErrors() && proyecto.isPresent()) {
        MessageHelper.addSuccessAttribute(ra, "Miembro añadido con éxito.");
        proyecto.get().addUsuario(usuario);
//        usuarioProyectoRepository.save(usuarioProyecto);
      } else {
        MessageHelper.addErrorAttribute(ra, "Error al agregar miembro.");
      }
    	return "redirect:/proyecto/index/"+usuarioProyecto.getProyecto().getId();
    }
    
    // GET editar proyecto
    @GetMapping(value = {"/resolve/{id}", "/resolver/{id}"})
    public String resolveProyecto(@PathVariable("id") Long id, Model model, RedirectAttributes ra) {
      Optional<Proyecto> optProyecto = proyectoRepository.findById(id);
      if (!optProyecto.isPresent()) {
        //ERROR
        MessageHelper.addErrorAttribute(ra, "Error al ir al formulario de selección del proyecto. Proyecto no encontrado.");
        return "";
      }
      model.addAttribute("comentario", new Comentario());
      model.addAttribute("proyecto", optProyecto.get());
      return "proyecto/resolve";
    }

    // POST actualizar proyecto
    @PostMapping(value={"/update_status", "/actualizar_estado"})
    public String updateStatusProyecto(@Valid @ModelAttribute("proyecto") Proyecto proyecto, @Valid @ModelAttribute("comentario") Comentario comentario, BindingResult errors, Model model, RedirectAttributes ra) {
      if (!errors.hasErrors()) {
      	Optional<Proyecto> existing = proyectoRepository.findById(proyecto.getId());
      	if (existing.isPresent()) {
	        UpdateHelper.copyNonNullProperties(proyecto, existing.get());
	        proyectoRepository.save(existing.get());
      	}
      	comentario.setProyecto(proyecto);
        comentarioRepository.save(comentario);
        model.addAttribute("proyectos", proyectoRepository.findAll());
        MessageHelper.addSuccessAttribute(ra, "Proyecto actualizado con éxito.");
      } else {
        MessageHelper.addErrorAttribute(ra, "Error al actualizar proyecto.");
      }
      return (errors.hasErrors() ? "redirect:/proyecto/resolve/"+proyecto.getId() : "redirect:/proyecto/index/"+proyecto.getId());
    }
}
