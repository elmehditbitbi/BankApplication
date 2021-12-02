package ma.octo.assignement.exceptions;

public class UtilisateurNonExistantException extends Exception {
    private static final long serialVersionUID = 1L;

    public UtilisateurNonExistantException() {
    }

    public UtilisateurNonExistantException(String message) {
        super(message);
    }
}
