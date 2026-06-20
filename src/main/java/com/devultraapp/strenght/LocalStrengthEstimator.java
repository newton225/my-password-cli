package com.devultraapp.strenght;

/**
 * Estimation de secours, utilisee UNIQUEMENT si le conteneur Docker est
 * injoignable. Elle repose sur une heuristique simple (longueur + diversite
 * de caracteres), volontairement plus simpliste que zxcvbn : il s'agit d'un
 * filet de securite, pas d'un substitut a l'audit externe qui reste la
 * source de verite demandee par le cahier des charges.
 */
final class LocalStrengthEstimator {

    private LocalStrengthEstimator() {
    }

    static StrengthResult estimate(String password) {
        int variety = 0;
        if (password.chars().anyMatch(Character::isUpperCase)) variety++;
        if (password.chars().anyMatch(Character::isLowerCase)) variety++;
        if (password.chars().anyMatch(Character::isDigit)) variety++;
        if (password.chars().anyMatch(c -> !Character.isLetterOrDigit(c))) variety++;

        int lengthScore;
        if (password.length() >= 16) {
            lengthScore = 2;
        } else if (password.length() >= 12) {
            lengthScore = 1;
        } else {
            lengthScore = 0;
        }

        int rawScore = Math.min(4, (variety - 1) + lengthScore);
        rawScore = Math.max(0, rawScore);

        return new StrengthResult(StrengthLevel.fromScore(rawScore), "estimation locale (conteneur indisponible)", true);
    }
}
