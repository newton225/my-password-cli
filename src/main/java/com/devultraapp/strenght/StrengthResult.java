package com.devultraapp.strenght;

/**
 * Resultat de l'analyse de robustesse d'un mot de passe.
 *
 * @param level     niveau de robustesse (Tres faible ... Tres fort)
 * @param crackTime estimation humaine du temps necessaire pour le casser
 *                  (fournie par zxcvbn, ex: "3 hours", "centuries")
 * @param fromLocal true si le score provient du calcul local de secours
 *                  (le conteneur Docker etait inaccessible), false si le
 *                  score provient bien du conteneur de validation externe.
 */
public record StrengthResult(StrengthLevel level, String crackTime, boolean fromLocal) {
}
