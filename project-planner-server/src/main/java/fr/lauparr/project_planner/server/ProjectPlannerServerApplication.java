package fr.lauparr.project_planner.server;

import fr.lauparr.project_planner.server.model.*;
import fr.lauparr.project_planner.server.repository.ProjetRepository;
import fr.lauparr.project_planner.server.repository.StatutTacheRepository;
import fr.lauparr.project_planner.server.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

@SpringBootApplication
public class ProjectPlannerServerApplication implements CommandLineRunner {

  @Autowired
  private PasswordEncoder passwordEncoder;
  @Autowired
  private ProjetRepository projetRepository;
  @Autowired
  private UtilisateurRepository utilisateurRepository;
  @Autowired
  private StatutTacheRepository statutTacheRepository;

  @Autowired
  public ProjectPlannerServerApplication() {
  }

  public static void main(String[] args) {
    SpringApplication.run(ProjectPlannerServerApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    if (utilisateurRepository.count() < 1) {
      creerDonneesReference();

      Utilisateur user1 = creerUtilisateur("lparrot", "Parrot", "Laurent", "kestounet@gmail.com", LocalDate.of(1983, 9, 5), "CDAD-R", "06 48 09 11 32", "06 48 09 11 32");
      Utilisateur user2 = creerUtilisateur("aparrot", "Parrot", "Anne", "patesdegeek@gmail.com", LocalDate.of(1982, 7, 13), "Maison Rambouillet", "01 23 45 67 89", "01 23 45 67 89");
      Utilisateur user3 = creerUtilisateur("ebauduin", "Bauduin", "Edith", "edith.bauduin@gmail.com", LocalDate.of(1960, 3, 17), "Maison Guesnain", "98 76 54 32 10", "98 76 54 32 10");

      byte[] logo1 = Base64.getDecoder().decode("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");
      byte[] logo2 = Base64.getDecoder().decode("iVBORw0KGgoAAAANSUhEUgAAAMgAAADICAYAAACtWK6eAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAABFpSURBVHhe7Z1bbB3FGcfnjUceoz7xUikPlYiam0lTcCmFVOpDCAkJhBZzaRW1IXZCQu5wYoMsqkoRlZBFVSl5aaOoLVGRWreiyK0qYVQk3EogU/XigGTv7oHECTic3Ei33zeZdeaMZ3dnd2eP7fD/SX9FPns59sn3P998c1sBAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAgPoJG+K28HnRPdUv+sJ+0SCdCgbESKJwQBzl1ycHxBY+j89XlwJw83H6OXHL1IBYLwN/QIyT4jIi8wT07xCbRt0agMVJsyGWqAwxbAa6DymzHJ1siGXqLQFY+LAxOHBJrSSY6xaZZYLNqH4FABYe82EMU9Io1JRTvxIA889CMIYpLvTR9ALzTtQQXaoWsAbqAtDQ6Ya4Vf26AHSOoF9sowBcMFkjQ+PoJgYdg7tsKeiGjCBc6JrmbKf+BADqgesNalKNWgJwMajFA4/qTwHAL9yW514iS+AtKk31i33qTwLAD9ysWsSZY44wZgK8wnOlbIFWRtGLy+Ozr/44nnnnjfjS5L/ja5cuxjrXrl6JL4UfxBfefTP+5K+/is/8Yr/1PpWF6SrAB9GAGLQGWEFNv/aSNEQZrp4/I83SPLrOeu8ykt3T6N0CVVBdudYAcxFnCw5sDnAfcHb57P2/eTMKNxu5+aj+XADc4ZFoW1C56uPjvfGVs5EKbb9ws+z8yC+t71tU9CVwTP3JALhTZRYuZw3+tq8brlU++tmT1t+hiDB/CxRCrd2wBlOeWv8aU+HbGa7OnPNhkjE0tYATaqS81KKmqua48vFUfOG9N2XziZtotvfg18+/fjye+cdf5PmMD5Og6xc4IRc5WQIoTxcoYMtw7fIlGezNkgHO1/H1l8kszZc3W89xEfdqIYuATNRUksKzc7nmKMq11gW67teyp8t2zzKafvUn1tddhSwCMuFpGLbAyRJ/axctyFsT73kd09AVvXi79XUXIYuATHhcwBY4Wbr44fsq7PPh5pSv7tlc9cuAtx/LEI/9qI8DgBvIrXgsAZOlc8OvqNDPh82RVnjXqaImofNH1EdSGTmW1JDbFkn5yk5TT4ml4Xa6p1LzR2KJOiQ5/ai4NXySjpnqMfTIDalLvXD6TnFLuI7uq+seQ93tOnfXAl/YxvtQ2QImS5c/mlThnw2bo7Y5Vf7V8hXIdK8hzmThEVKDzHdIrFOHKhH0UqbfQfd8irSdaicyjDokCbdR0P2Ajn1f6UnSE6THSY+RekiPkr5HTdLvkh4hbaWft4ogekicCB4S286tLx+wZIjbom/TPdeR7lO6l/Qt0j2kb5LuJn2D1E26i3Qnaa1okYbDtdTU71pg04DoP7NQ1y4HvAv/+/yqnJxou4er+L24I4AnOOri5tqZk0es1+jiLFIkk/gaOORFWm0Gea76qL3MHr10P2WQYLsYVYdmKWsQqYdJD5G2iOnwQdEoY5TSBvm60lrS10hrxLEFYZSpBn3olkDJEs+JcqFszcGBz+Mqrh0Arf++KydE2u4lxYFqe92uIfXRVIZqmonEIOGz1bNT2EeZXjfID+fWTBaDjAdPiJHgcdJjpEfFCJljLMcgcbSZ9CAZZWOxJphpkPBeykz3ihEyyAiZQyq8W4w6GCSOukQrWDXPdWHR3ivugXIJ3MvRB9brs8RB7tp0s8GDhlU7AijbTKiPpjKy6ZoY5DkRTx6utqqRmlcTmkFaZv3BzDHIE6JHHbJCNcst1KxaRwYZJINMGAaJo010jwfcF5pZMkhu5qRmVTepQQYZazPIHaQu0ipxgmsbdXpn4Ql7tkBJE3+758FNqyJFOY+HuGYlF3hqfZWBQ86q6uOphMzOmkEoiwyrQ4WJdlCTrY/uccMg1nsVNYgJmWQ9mWNcN0i0kbTBbZyojEF0yCBdZJDhNoOspi/mlf4yeyHoG5M3jLYGik0uA4Mz7/zJeq1NPE2k7JR4HnDkTJWIf07gWb9sZtt7OqhQUGURHKGiOjHIYfrWb8z91neBzDGkG2Ryuz0bVTUIw1mFzHHCMEgrXJ/f3KpqkIRwDWUUzSCUReJgxTw0tygYChXoed/0nD2aL2+xXmuKv+W5WeQKG0DO13r9eOr0FH7v6dd+Ks/je2fWJmmippH6eCpDWaRPyyBxcLD4fzLXLkGfCGYN8pSY5iBWh9vwYZCE5iZxatYgD5A2iDF1KBVfBmGoSG/oBolW0BfMynJfMKWhgCi0vxVPM8+CR8pt15niWsZ1zciV6aasLcpMTTn/x5+3dTNTxmw7niJv6VzuPqkb5PDcnqc8gl1inTSHMkiwIz3ofBpEZpJN1Ny6YZA4Wp/dy+fTIAwZZHjWICspblaIo+pQ/aitQ20Bkqo8g7h263LPUx6cjXjOlu36whr8inuXb784pT4iL5A5hhODUDMrDvcV676MdooTukF4gFAdmoNPgzDhJrGlzSD3Z2cR3wahjLFMN0i0XLTO3d6hgUXZV28LkAxlGeTqJ2et15hyGYXnrOF79D0xR5DT7UvneRtRZyiD9OgGmTrk3ivEzatwJ2V5ZZCgVwTqkBXfBuEsEj4gAs0gMdUiqQb3bRCGDDI8a5AVUp1Z5BY8T6nbEiBZyjKIS/OKm0mft2bUFXbYHHVNaHSRb4PIIH+WgjzJIIfEuDqUC5mihwxyPXuQoh1iUB2y4tsgDGWQY4ZBUu9Zk0H6dIM0v9qhZpZ8xJklQLKUZZBPR1+zXqOLa4ks5tscLN8GYXgkXTNIPHnQbQd6MsewbpDJvuzr6jAIZZAe3SCk9BqoBoPIZpaeQZbndxZ4oYxBslYOcu+R7RpdWQOB3EtVxhxnTh6JP3nzt3IKyrnXjzlPQbG9zqrFIIcoW2sGiQ5mZwKmuYdqxF10fmKQXodepBoM0nyQArTdIKmfTx0G4UFCwyDT6lC9lDEIB2Iaed27XFNkwd23tuts4ntxl3PaqD6Pg8htgjIGDNNMUodBmOCwCBKDBAezawlmapfo0w0y1Ztfu9SSQajmMAySatQ6DMKQQaY1g8Tq5Xops71P1kTFvKWzWYOMl6b+Y73Gppm3h53naXG9k9WzZjMJveZtuokOmeNoYpDwIGlf9sBb8LQY1Q1im1piUodBZHdvu0FSP5/aDLJSTOgG6ch4CG9KbQaHi9KCM88gWYOMrj1WWffgbmEeUeeRfNZn7789u7kDZz7b/WyqyyDUrOrSDRIcSA+eqafF0vBpOk8ZJOh1y2p1GIRZaAYJl3Vopi8FxLQZIHlKW0mYZ5C0+oOD2Ha+qbQCn02R1TzjuobHUz5963fW42YWqcsgDBlkfDaD7BfTaTN8w92ioRuEMohToNdUgyxZaAZRL9cPBcSYHhwu4ukbNvIMkjatxGUwkJt2ZubijOHSc5aIjaKPqieKjHGRWg1yiAI/McgBqisO2Pv0g91iQjNI63Sf2+BYh2qQjhbpDJmjNT8GKbGakGWbJpI3ip5mEJeeK9v7nfv9K9Zz82RmDFN0PLeALkvIy5s1g0T7xQl1aJZoFzXFdtNxZRAeSVeHcqnFIBtFt2GQ1FnJdRiER86NXqzavsDmUKYni2UruGfe/oP13EQ2g3x+4bz1XF22joEiM4atyhlNr7q4KYvgEBXfyiDUzGqZDxsN94gh3SA8F0sdyqUmgzR0g1BGSZ3MWYdBotVivTFQ6HUqUC4UEIXrEB4RN6epX/zwn9ZzE9lqkLxrWNxrpcM1h+28otKnnJhZpc5HSfOM3lmDsPa1B3Gwh5pXyiDBzmLbEdVhEDLHiGGQ9LlgNRikuUocNTJIZ/cwozb4CT04XHXm5JG2uoA3aLCdl4gfjGPikgnMTgHX6fS50gwS6a+T6nymoZwkekC0EoPozazJPWIZZZBYyyCFZhb7Nki4hQK+fbJiK22qPePbIHKXlNUiaMsgt3f4OfgUED16cBQRj17r2IrgRLZJii4G0ae3FBkvqSSPa0JsUAYZns0g+2+MDE/tEft0g3A9og454dsgzY307a0ZpHl/dvPGt0HkPKz22bydmWaiIwtHW5A4Sm86ZU1Y5NWDJkUNUqTXykVpBTu9XstoesLkAbFFM8jsoGHwjBiZNcgu90mNCT4NEm0WXWSOVptB1ufMBfNoEN7VhBToBgmXV28yloICovSDOrkXKullyltRaDaXePNp23m69PlfLvO9WPw78aAidwywgbmOsZ2XqRof0yZn+FLmSAwS7RWD3JUbPkM/3zBI4SzmyyDctAo3UXBqKwqbG/KLY18GkU2rO8SovqIwXEH12Hxt3lDluSAsDsjkOYRZQW82s1yaTPr8L9cFWbZnItpMkpZBWLzji/p4aoFH0mczyF4xPrmXsopukL7iBvVhEM4c4WYyh7YmPdwgAh4wVKek4sMgU2vE0ugOMda2Jp3HQZYXa256h4Ki8KChLu7Z4gzBxXpWFtF7v/IKexZPOExwMUjafDGel2U7X5exmKpwE6cI3KzSmlhxsJeaV8ogwe7iS3OZKgbhrNHcIobMbX8oe7SoeeUUnFUM0uwWS+T2P2vEtLmrSbhqnppWOmXHRExxlrj4wbj1GIuDXCersE+UNM1c9r0y75/APW628/XeLGkQvXerUe+3FmWRIDEIZZFYM0ipHTyKGESuFuT9eR+moNwqRiwbx7FBCm0eV9QgUTfVOXeKvuZacSq6vv1o+75YqxfA5nE6FBxenovOTa6zv3nReox1gZphCS6FNxf4HOC8W4ntuC5+b9uESq5FbOdHerZ74Uttx/jzUB9NLUT7xeAcg+wWLV4Lok4pxByDlNt69LpBNlGLgrKKurUTFoOU3Xo0phqEC/T5bVaZVH3C7RwNLrW+zs2xpPeLF0q57FjCi6G46HY9VzcJN69cZwybc7PqfNAnryy0ZJDSpvRkkDGqQUqNA3kyyARlj755K8jzKDtwaFNWEczf9IlJXOdVnTl5JP70nTesx6oq43cdr3PqCZljXDfI5O7yg5SlDPIwtfm3imHZ1KICXd2qFBUMMkIapMzRmQ0ZqqDWiZR6mGeqjG/lRJwNuMeJ16K7ZAZW8+h9cfTSd6zHykhmDKP2MFXnI9qmDoilZJBuqd3VniVCdYXz80GaPf4XHcnuWffng3RmTUcdqF3fC8/RylLWdjvcBTvz9z9bj6XqhSX210soMUlbFmn/fad97dkLbhKqjo2kKa0pw925Hx3faT1mlRnQDpLna4Ff5Ho6d8KceQu+4FAwlVovkqWsoJTHBr9sPWYTn+8c5MoYha4xRNeN1FmPgEUIBZaXrl9TaUEqX29v3uSLs4nlGvM9yhrDUOf2hwWLAwqKISNIvIqDOwleT0GcKXqPUTLVMKlB771NDpJaJHef5CxK59I1+nPk5+d5FWDhwj05WoB0RkUzSYqU6bjTYahKsa1mPvewwUgjqElAG6pwL/TIBB+SAV7QLHyNEs9U7vFdO7DR+Euj7ENxwE0Kj7ZT0OnNjY6LVwBqBpgVH0v+5QFPdM2CeUE9W6TWuqSCpuucHgKAMyqbFHrOYa3qF6fQ5AELDv7GJqNMWIO2EyJj1LkTCQBekF2mFRdeFVCL3u8YjAEWHdwdqrqFvZtFNel60MUKbgqUWfZxYJepV+ia0WhADHIzDqYAXxjkQ0SfF90y01wfrZaSYy30OoptAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAgIYQ/wcF6gQiQAVBBwAAAABJRU5ErkJggg==");
      byte[] logo3 = Base64.getDecoder().decode("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");

      List<Projet> projets = Arrays.asList(
              ajouterTaches(new Projet("CCS", "Carte de Circulation Sécurisée", logo1, user1)),
              ajouterTaches(new Projet("VDD", "Valise Diplomatique Défense", logo2, user1)),
              ajouterTaches(new Projet("PRDV", "Eureka Module Prise de Rendez-Vous", logo3, user2))
      );

      projetRepository.saveAll(projets);
    }
  }

  private void creerDonneesReference() {
    statutTacheRepository.save(new StatutTache("A réaliser", "Description inconnue", "grey-3", true));
    statutTacheRepository.save(new StatutTache("En cours", "Description inconnue", "orange-2", false));
    statutTacheRepository.save(new StatutTache("Réalisé", "Description inconnue", "blue-2", false));
    statutTacheRepository.save(new StatutTache("Testé", "Description inconnue", "yellow-3", false));
    statutTacheRepository.save(new StatutTache("Cloturé", "Description inconnue", "green-2", false));
    statutTacheRepository.save(new StatutTache("Annulé", "Description inconnue", "red-2", false));
  }

  private Utilisateur creerUtilisateur(String username, String nom, String prenom, String email, LocalDate dateNaissance, String compagnie, String fixe, String portable) {
    Utilisateur utilisateur = new Utilisateur();
    UtilisateurDetails details = new UtilisateurDetails();
    utilisateur.setUsername(username);
    utilisateur.setPassword(passwordEncoder.encode("123"));
    details.setNom(nom);
    details.setPrenom(prenom);
    details.setEmail(email);
    details.setDateNaissance(dateNaissance);
    details.setCompagnie(compagnie);
    details.setFixe(fixe);
    details.setPortable(portable);
    utilisateur.setDetails(details);

    return utilisateurRepository.save(utilisateur);
  }

  private Projet ajouterTaches(Projet projet) {
    GroupeTache groupe1 = new GroupeTache("Groupe 1");
    GroupeTache groupe2 = new GroupeTache("Groupe 2");
    GroupeTache groupe3 = new GroupeTache("Groupe 3");

    StatutTache statut = statutTacheRepository.findStatutTacheByInitialTrue();

    groupe1.addTache(new Tache("Tache 1-1", "Description tache 1-1", statut));
    groupe1.addTache(new Tache("Tache 1-2", "Description tache 1-2", statut));
    groupe1.addTache(new Tache("Tache 1-3", "Description tache 1-3", statut));
    groupe2.addTache(new Tache("Tache 2-1", "Description tache 2-1", statut));
    groupe2.addTache(new Tache("Tache 2-2", "Description tache 2-2", statut));
    groupe2.addTache(new Tache("Tache 2-3", "Description tache 2-3", statut));
    groupe3.addTache(new Tache("Tache 3-1", "Description tache 3-1", statut));
    groupe3.addTache(new Tache("Tache 3-2", "Description tache 3-2", statut));
    groupe3.addTache(new Tache("Tache 3-3", "Description tache 3-3", statut));

    projet.addGroupe(groupe1);
    projet.addGroupe(groupe2);
    projet.addGroupe(groupe3);

    return projet;
  }
}
