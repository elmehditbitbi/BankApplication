package ma.octo.assignement.mapper;

import ma.octo.assignement.domain.Versement;
import ma.octo.assignement.dto.VersementDto;
import ma.octo.assignement.repository.CompteRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class VersementMapper {
    private static VersementDto versementDto;

    public static VersementDto map(Versement versement) {
        versementDto = new VersementDto();
        versementDto.setNrnom_prenom_emetteur(versement.getNom_prenom_emetteur());
        versementDto.setDateExecution(versement.getDateExecution());
        versementDto.setMotifVersement(versement.getMotifVersement());
        versementDto.setNrcompteBeneficiaire(versement.getCompteBeneficiaire().getRib());

        return versementDto;

    }
}
