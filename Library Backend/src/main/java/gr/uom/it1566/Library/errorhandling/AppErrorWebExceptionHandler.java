package gr.uom.it1566.Library.errorhandling;

import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

// Δηλώνει ότι αυτή η κλάση διαχειρίζεται τις εξαιρέσεις που σχετίζονται με τα σφάλματα στην εφαρμογή
@Component
public class AppErrorWebExceptionHandler extends AbstractErrorWebExceptionHandler {
    // Constructor που δέχεται τα απαιτούμενα στοιχεία για την παραμετροποίηση του exception handler
    public AppErrorWebExceptionHandler(AppErrorAttributes g, ApplicationContext applicationContext, ServerCodecConfigurer serverCodecConfigurer) {
        super(g, new WebProperties.Resources(), applicationContext); // Καλεί τον constructor της γονικής κλάσης
        super.setMessageWriters(serverCodecConfigurer.getWriters()); // Ρυθμίζει τους writers μηνυμάτων
        super.setMessageReaders(serverCodecConfigurer.getReaders()); // Ρυθμίζει τους readers μηνυμάτων
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(final ErrorAttributes errorAttributes) {
        // Δημιουργεί μια routing function για την επεξεργασία των σφαλμάτων
        return RouterFunctions.route(RequestPredicates.all(), request -> {
            var props = getErrorAttributes(request, ErrorAttributeOptions.defaults()); // Λαμβάνει τα attributes του σφάλματος

            // Δημιουργεί και επιστρέφει την ServerResponse με τον κατάλληλο κωδικό κατάστασης και περιεχόμενο JSON
            return ServerResponse.status(Integer.parseInt(props.getOrDefault("status", 500).toString())) // Καθορίζει την κατάσταση
                    .contentType(MediaType.APPLICATION_JSON) // Καθορίζει το τύπο περιεχομένου σε JSON
                    .body(BodyInserters.fromValue(props.get("errors"))); // Εισάγει το σώμα της απάντησης με τα σφάλματα
        });
    }
}
