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
        return null;
    }



}
