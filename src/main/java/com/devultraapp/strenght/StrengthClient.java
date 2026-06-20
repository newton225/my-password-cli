package com.devultraapp.strenght;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Client charge de la communication entre l'application Java et le
 * conteneur Docker de validation (micro-service Python/Flask exposant
 * l'algorithme zxcvbn sur le port 5000).
 *
 * Protocole utilise : HTTP REST simple (POST + JSON), choisi plutot que
 * gRPC ou l'execution de "docker exec" depuis Java car :
 *   - il ne necessite aucune dependance externe (java.net.http est dans
 *     le JDK depuis Java 11) ;
 *   - il decouple totalement le conteneur de l'application Java : celle-ci
 *     pourrait tourner sur une autre machine que le conteneur sans
 *     modification (architecture clients/serveur classique en DevOps) ;
 *   - le format JSON est trivial a parser sans librairie pour une reponse
 *     aussi simple ({"score":X,"crack_time_display":"..."}).
 *
 * En cas d'indisponibilite du conteneur (reseau coupe, image non demarree),
 * on bascule sur une estimation locale degradee (cf. LocalStrengthEstimator)
 * plutot que de faire planter tout le programme : la generation de mots de
 * passe reste la fonctionnalite principale, l'audit Docker est un service
 * complementaire.
 */
public final class StrengthClient {

    private final HttpClient httpClient;
    private final URI endpoint;

    public StrengthClient(String baseUrl) {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(2))
                .build();
        this.endpoint = URI.create(baseUrl + "/check");
    }

    public StrengthClient() {
        this("http://localhost:5000");
    }

    /**
     * Interroge le conteneur Docker pour evaluer un mot de passe.
     * Retourne le resultat distant si l'appel reussit, sinon une estimation
     * locale de secours (fromLocal=true).
     */
    public StrengthResult evaluate(String password) {
        try {
            String escaped = password.replace("\\", "\\\\").replace("\"", "\\\"");
            String body = "{\"password\":\"" + escaped + "\"}";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(endpoint)
                    .timeout(Duration.ofSeconds(3))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                return LocalStrengthEstimator.estimate(password);
            }

            // System.out.println(response.body());

            return parseJsonResponse(response.body());

        } catch (Exception e) {
            // Le conteneur n'est pas joignable (pas demarre, port different, etc.)
            // -> degradation gracieuse plutot que crash de l'application.
            return LocalStrengthEstimator.estimate(password);
        }
    }

    /**
     * Parsing JSON minimaliste fait a la main (le format de reponse attendu
     * du micro-service est volontairement tres simple et stable :
     * {"score": 0-4, "crack_time_display": "..."}). On evite ainsi
     * d'ajouter une dependance Jackson/Gson pour un seul champ a extraire.
     */
    private StrengthResult parseJsonResponse(String json) {
        int score = extractInt(json, "\"score\"");
        String crackTime = extractString(json, "\"crack_time_display\"");
        return new StrengthResult(StrengthLevel.fromScore(score), crackTime, false);
    }

    private int extractInt(String json, String key) {
        int idx = json.indexOf(key);
        if (idx == -1) return 0;
        int colon = json.indexOf(':', idx);
        int start = colon + 1;
        int end = start;
        while (end < json.length() && (Character.isDigit(json.charAt(end)))) {
            end++;
        }
        try {
            return Integer.parseInt(json.substring(start, end).trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private String extractString(String json, String key) {
        int idx = json.indexOf(key);
        if (idx == -1) return "inconnu";
        int firstQuote = json.indexOf('"', json.indexOf(':', idx) + 1);
        int secondQuote = json.indexOf('"', firstQuote + 1);
        if (firstQuote == -1 || secondQuote == -1) return "inconnu";
        return json.substring(firstQuote + 1, secondQuote);
    }
}
