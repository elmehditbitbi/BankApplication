package ma.octo.assignement.web;

import ma.octo.assignement.domain.Compte;
import ma.octo.assignement.domain.Utilisateur;
import ma.octo.assignement.domain.Versement;
import ma.octo.assignement.domain.Virement;
import ma.octo.assignement.dto.VersementDto;
import ma.octo.assignement.dto.VirementDto;
import ma.octo.assignement.exceptions.*;
import ma.octo.assignement.repository.CompteRepository;
import ma.octo.assignement.repository.UtilisateurRepository;
import ma.octo.assignement.repository.VersementRepository;
import ma.octo.assignement.repository.VirementRepository;
import ma.octo.assignement.service.AutiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController()
//J'ai ajoutee cette anotation pour diferencier path
@RequestMapping(value = "/versement")

public class VersementController {

    // j'ai changee le type de donnee des montants en bigdecimal et j'ai ajoutee montant minimal
    public static final BigDecimal MONTANT_MAXIMAL = new BigDecimal(10000);

    public static final BigDecimal MONTANT_MINIMAL = new BigDecimal(10);

    Logger LOGGER = LoggerFactory.getLogger(VersementController.class);
    @Autowired
    private CompteRepository rep1;
    @Autowired
    private VersementRepository re2;
    @Autowired
    private AutiService monservice;
    @Autowired
    private UtilisateurRepository re3;

    @GetMapping("lister_versement")
    List<Versement> loadAll() {
        List<Versement> all = re2.findAll();

        if (CollectionUtils.isEmpty(all)) {
            return null;
        } else {
            return all;
        }
    }



    @PostMapping("/executerVersement")
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional(rollbackFor = Exception.class)
    public String createTransaction(@RequestBody VersementDto versementDto)
            throws SoldeDisponibleInsuffisantException, CompteNonExistantException, TransactionException, UtilisateurNonExistantException, RIBNonExistantException {
        //l'emmeteur est un utilisateur !
        Utilisateur u1 = re3.findByUsername(versementDto.getNrnom_prenom_emetteur());
        System.out.println(versementDto.getNrcompteBeneficiaire());
        //find beneficiaire by rib
        Compte c = rep1
                .findByRib(versementDto.getNrcompteBeneficiaire());

        //j'ai ajoutee l'exception du utilasateur non existant
        if (u1 == null) {
            System.out.println("Utilisateur Non existant");
            throw new UtilisateurNonExistantException("Utilisateur Non existant");
        }

        //j'ai ajoutee l'exception du rib non existant
        if (c == null) {
            System.out.println("RIB Non existant");
            throw new RIBNonExistantException("RIB Non existant");
        }

        //traitement de cas du motif est nullou vide
        if (versementDto.getMotifVersement()==null||versementDto.getMotifVersement().trim().equals("")) {
            System.out.println("Montant vide");
            throw new TransactionException("Montant vide");

            //traitement montant du versement egale 0 ou null
        } else if (versementDto.getMontantVersement().equals(0)||versementDto.getMontantVersement().equals(null)) {
            //afficher que le montant vide
            System.out.println("Montant vide");
            throw new TransactionException("Montant vide");
            //traiter le cas que le montant n'atteint pas montant minmal
        } else if (versementDto.getMontantVersement().compareTo(MONTANT_MINIMAL)<0) {
            System.out.println("Montant minimal de versement non atteint");
            throw new TransactionException("Montant minimal de versement non atteint");
            //traiter le cas que le montant deppase le montant maximal
        } else if (versementDto.getMontantVersement().compareTo(MONTANT_MAXIMAL)>0) {
            System.out.println("Montant maximal de versement dépassé");
            throw new TransactionException("Montant maximal de versement dépassé");
        }

        //supprimer la methode intValue pour le solde c'est une anomalie !
        c
                .setSolde(c.getSolde().add(versementDto.getMontantVersement()));
        //traiter le cas si un ereur produit lors d'enregistrement dans le systeme du banque
        if(rep1.save(c)==null)
            throw new TransactionException("Probleme se produit");

        Versement versement = new Versement();
        versement.setDateExecution(versementDto.getDateExecution());
        versement.setCompteBeneficiaire(c);
        versement.setMontantVirement(versementDto.getMontantVersement());

        //traiter le cas si un ereur produit lors d'enregistrement dans le systeme du banque
        if(re2.save(versement)==null)
            throw new TransactionException("Probleme se produit");

        monservice.auditVersement("Versement depuis " + versementDto.getNrnom_prenom_emetteur() + " vers " + versementDto
                .getNrcompteBeneficiaire() + " d'un montant de " + versementDto.getMontantVersement()
                .toString());

        // changer le type retour de la fonction create transaction pour afficher au client que le versement bien effectuee
        return "Versement octo effectuee";
    }
    private void save(Versement Versement) {
        re2.save(Versement);
    }

}
