package gr.uom.it1566.Library.errorhandling;

import gr.uom.it1566.Library.exception.ApiException;
import gr.uom.it1566.Library.exception.AuthException;
import gr.uom.it1566.Library.exception.UnauthorizedException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

// Δηλώνει ότι αυτή η κλάση διαχειρίζεται τα attributes σφαλμάτων για τις αντιδράσεις της εφαρμογής
@Component
public class AppErrorAttributes extends DefaultErrorAttributes {
    private HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR; // Προκαθορισμένη κατάσταση σφάλματος

    public AppErrorAttributes() {
        super(); // Καλεί τον constructor της γονικής κλάσης
    }

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        var errorAttributes = super.getErrorAttributes(request, ErrorAttributeOptions.defaults()); // Λαμβάνει τα βασικά attributes σφάλματος
        var error = getError(request); // Λαμβάνει το σφάλμα από την αίτηση

        var errorList = new ArrayList<Map<String, Object>>(); // Λίστα για αποθήκευση λεπτομερειών σφάλματος

        // Ελέγχει αν το σφάλμα είναι τύπου AuthException ή UnauthorizedException ή σχετικό με JWT
        if (error instanceof AuthException || error instanceof UnauthorizedException
                || error instanceof ExpiredJwtException || error instanceof SignatureException || error instanceof MalformedJwtException) {
            status = HttpStatus.UNAUTHORIZED; // Ορίζει την κατάσταση σε UNAUTHORIZED
            var errorMap = new LinkedHashMap<String, Object>(); // Χάρτης για την αποθήκευση λεπτομερειών σφάλματος
            errorMap.put("code", ((ApiException) error).getErrorCode()); // Προσθέτει τον κωδικό σφάλματος
            errorMap.put("message", error.getMessage()); // Προσθέτει το μήνυμα σφάλματος
            errorList.add(errorMap); // Προσθέτει το errorMap στη λίστα
        }
        // Ελέγχει αν το σφάλμα είναι τύπου ApiException
        else if (error instanceof ApiException) {
            status = HttpStatus.BAD_REQUEST; // Ορίζει την κατάσταση σε BAD_REQUEST
            var errorMap = new LinkedHashMap<String, Object>();
            errorMap.put("code", ((ApiException) error).getErrorCode());
            errorMap.put("message", error.getMessage());
            errorList.add(errorMap);
        }
        // Διαχείριση άλλων σφαλμάτων
        else {
            status = HttpStatus.INTERNAL_SERVER_ERROR; // Ορίζει την κατάσταση σε INTERNAL_SERVER_ERROR
            var message = error.getMessage();
            if (message == null) // Αν δεν υπάρχει μήνυμα, χρησιμοποιεί το όνομα της κλάσης
                message = error.getClass().getName();

            var errorMap = new LinkedHashMap<String, Object>();
            errorMap.put("code", "INTERNAL_ERROR"); // Ορίζει γενικό κωδικό σφάλματος
            errorMap.put("message", message);
            errorList.add(errorMap);
        }

        var errors = new HashMap<String, Object>(); // Χάρτης για την αποθήκευση όλων των σφαλμάτων
        errors.put("errors", errorList); // Προσθέτει τη λίστα σφαλμάτων στον χάρτη
        errorAttributes.put("status", status.value()); // Ορίζει την κατάσταση στο attributes
        errorAttributes.put("errors", errors); // Προσθέτει τα errors στο attributes

        return errorAttributes; // Επιστρέφει τα attributes σφάλματος
    }
}
