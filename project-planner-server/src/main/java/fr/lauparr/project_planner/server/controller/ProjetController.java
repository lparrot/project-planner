package fr.lauparr.project_planner.server.controller;

import fr.lauparr.project_planner.server.exception.EntityNotFoundException;
import fr.lauparr.project_planner.server.model.*;
import fr.lauparr.project_planner.server.projections.MembreDTO;
import fr.lauparr.project_planner.server.projections.ProjetDTO;
import fr.lauparr.project_planner.server.projections.StatutTacheDTO;
import fr.lauparr.project_planner.server.repository.*;
import fr.lauparr.project_planner.server.service.LogService;
import fr.lauparr.project_planner.server.service.ProjectionService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/projets")
public class ProjetController {

  @Autowired
  private ProjetRepository projetRepository;
  @Autowired
  private UtilisateurRepository utilisateurRepository;
  @Autowired
  private GroupeTacheRepository groupeTacheRepository;
  @Autowired
  private TacheRepository tacheRepository;
  @Autowired
  private ProjectionService projectionService;
  @Autowired
  private StatutTacheRepository statutTacheRepository;
  @Autowired
  private LogService logService;

  @Autowired
  public ProjetController() {
  }

  @GetMapping
  public ResponseEntity getProjets() {
    return ResponseEntity.ok(projectionService.convertToDto(projetRepository.findAll(), ProjetDTO.class));
  }

  @GetMapping("/statuts")
  public ResponseEntity getStatuts() {
    return ResponseEntity.ok(projectionService.convertToDto(statutTacheRepository.findAll(), StatutTacheDTO.class));
  }

  @GetMapping("{id}")
  public ResponseEntity getProjet(@PathVariable Long id) {
    Projet projet = findProjet(id);
    return ResponseEntity.ok(projectionService.convertToDto(projet, ProjetDTO.class));
  }

  @GetMapping("{id}/membres")
  public ResponseEntity getMembres(@PathVariable Long id) {
    Projet projet = findProjet(id);
    return ResponseEntity.ok(projectionService.convertToDto(utilisateurRepository.findAllMembre(projet.getId()), MembreDTO.class));
  }

  @PutMapping("{id}")
  public ResponseEntity putProjet(@PathVariable Long id, @RequestBody PutProjetParam params) {
    Projet projet = findProjet(id);
    projet.setNom(params.getNom());
    projet.setDescription(params.getDescription());
    projet = projetRepository.save(projet);
    return ResponseEntity.ok(projectionService.convertToDto(projet, ProjetDTO.class));
  }

  @PutMapping("/{id}/membres")
  public ResponseEntity putMembres(@RequestBody List<Long> ids, @PathVariable Long id) {
    Projet projet = findProjet(id);
    projet.getUtilisateurs().addAll(utilisateurRepository.findAllById(ids));
    projet = projetRepository.save(projet);
    return ResponseEntity.ok(projectionService.convertToDto(projet, ProjetDTO.class));
  }

  @PutMapping("{id}/logo")
  public ResponseEntity putProjet(@RequestParam("file") MultipartFile file, @PathVariable Long id) throws IOException {
    Projet projet = findProjet(id);
    projet.setLogo(file.getBytes());
    projet = projetRepository.save(projet);
    return ResponseEntity.ok(projectionService.convertToDto(projet, ProjetDTO.class));
  }

  @DeleteMapping("/{idProjet}/membres/{idMembre}")
  public ResponseEntity deleteMembre(@PathVariable Long idProjet, @PathVariable Long idMembre) {
    Projet projet = findProjet(idProjet);
    boolean removed = projet.getUtilisateurs().removeIf(u -> u.getId().equals(idMembre));
    return checkIfRemoved(projet, removed);
  }

  @PostMapping("/{id}/groupes")
  public ResponseEntity postGroupeTache(@RequestBody PostGroupeTacheParam params, @PathVariable Long id) {
    Projet projet = findProjet(id);
    GroupeTache groupeTache = new GroupeTache();
    groupeTache.setNom(params.getNom());
    groupeTache = groupeTacheRepository.save(groupeTache);
    projet.addGroupe(groupeTache);
    projet = projetRepository.save(projet);
    return ResponseEntity.ok(projectionService.convertToDto(projet, ProjetDTO.class));
  }

  @DeleteMapping("/{idProjet}/groupes/{idGroupe}")
  public ResponseEntity deleteGroupe(@PathVariable Long idProjet, @PathVariable Long idGroupe) {
    Projet projet = findProjet(idProjet);
    boolean removed = projet.getGroupes().removeIf(g -> g.getId().equals(idGroupe));
    return checkIfRemoved(projet, removed);
  }

  @PostMapping("/{idProjet}/groupes/{idGroupe}")
  public ResponseEntity postTache(@PathVariable Long idProjet, @PathVariable Long idGroupe, @RequestBody PostTacheParams params) {
    Projet projet = findProjet(idProjet);
    GroupeTache groupe = projet.getGroupes().stream().filter(g -> g.getId().equals(idGroupe)).findFirst().orElse(null);
    if (groupe == null) {
      throw new EntityNotFoundException();
    }
    Tache tache = new Tache();
    tache.setTitre(params.getTitre());
    tache.setDescription(params.getDescription());
    tache.setTags(params.getTags());
    tache.setStatut(statutTacheRepository.findStatutTacheByInitialTrue());
    tache = tacheRepository.save(tache);
    groupe.addTache(tache);
    groupeTacheRepository.save(groupe);
    return ResponseEntity.ok(projectionService.convertToDto(projet, ProjetDTO.class));
  }

  @DeleteMapping("/{idProjet}/taches/{idTache}")
  public ResponseEntity deleteTache(@PathVariable Long idProjet, @PathVariable Long idTache) {
    tacheRepository.deleteById(idTache);
    Projet projet = findProjet(idProjet);
    return ResponseEntity.ok(projectionService.convertToDto(projet, ProjetDTO.class));
  }

  @PutMapping("/{idProjet}/taches/{idTache}/utilisateurs/{idUtilisateur}")
  public ResponseEntity affecterUtilisateur(@PathVariable Long idProjet, @PathVariable Long idTache, @PathVariable Long idUtilisateur) {
    Utilisateur utilisateur = utilisateurRepository.findById(idUtilisateur).orElse(null);
    Tache tache = updateTache(EnumUpdateTacheFactory.utilisateur, utilisateur, idTache);
    Projet projet = findProjet(idProjet);
    logService.creer(projet, "L'utilisateur %s a été affecté à la tache %s", utilisateur.getUsername(), tache.getTitre());
    return ResponseEntity.ok(projectionService.convertToDto(projet, ProjetDTO.class));
  }

  @PutMapping("/{idProjet}/taches/{idTache}/statuts/{idStatut}")
  public ResponseEntity changerStatut(@PathVariable Long idProjet, @PathVariable Long idTache, @PathVariable Long idStatut) {
    StatutTache statut = statutTacheRepository.findById(idStatut).orElse(null);
    Tache tache = updateTache(EnumUpdateTacheFactory.statut, statut, idTache);
    Projet projet = findProjet(idProjet);
    logService.creer(projet, "Le statut de la tache %s a été modifié en %s", tache.getTitre(), tache.getStatut().getNom());
    return ResponseEntity.ok(projectionService.convertToDto(projet, ProjetDTO.class));
  }

  @PutMapping("/{idProjet}/estimations")
  public ResponseEntity putEstimations(@PathVariable Long idProjet, @RequestBody List<PutEstimationsParams> params) {
    params.forEach(data -> {
      Tache tache = tacheRepository.findById(data.getId()).orElse(null);
      if (tache != null) {
        tache.setEstimation(data.getEstimation());
        tacheRepository.save(tache);
      }
    });
    Projet projet = findProjet(idProjet);
    return ResponseEntity.ok(projectionService.convertToDto(projet, ProjetDTO.class));
  }

  private Tache updateTache(EnumUpdateTacheFactory enumeration, Object data, Long idTache) {
    if (data == null) {
      throw new EntityNotFoundException();
    }
    Tache tache = tacheRepository.findById(idTache).orElse(null);
    if (tache == null) {
      throw new EntityNotFoundException();
    }
    switch (enumeration) {
      case statut:
        tache.setStatut((StatutTache) data);
        break;
      case utilisateur:
        tache.setUtilisateur((Utilisateur) data);
        break;
      default:
        break;
    }
    return tacheRepository.save(tache);
  }


  private Projet findProjet(Long id) {
    Projet projet = projetRepository.findById(id).orElse(null);
    if (projet == null) {
      throw new EntityNotFoundException();
    }
    return projet;
  }

  private ResponseEntity checkIfRemoved(Projet projet, boolean removed) {
    if (!removed) {
      return ResponseEntity.notFound().build();
    }
    projetRepository.save(projet);
    return ResponseEntity.ok(projectionService.convertToDto(projet, ProjetDTO.class));
  }

  @Data
  static class PutProjetParam {
    String nom;
    String description;
  }

  @Data
  static class PostGroupeTacheParam {
    String nom;
  }

  @Data
  static class PostTacheParams {
    String titre;
    String description;
    List<String> tags;
  }

  @Data
  static class PutEstimationsParams {
    Long id;
    Float estimation;
  }

  private enum EnumUpdateTacheFactory {
    statut, utilisateur
  }

}
