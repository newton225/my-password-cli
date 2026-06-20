package com.devultraapp.strenght;

/**
 * Niveaux de robustesse exposes a l'utilisateur, conformes au cahier des
 * charges (Tres faible -> Tres fort). Ces niveaux correspondent directement
 * au score 0-4 retourne par zxcvbn dans le conteneur Docker.
 */
public enum StrengthLevel {
    TRES_FAIBLE(0, "Tres faible"),
    FAIBLE(1, "Faible"),
    MOYEN(2, "Moyen"),
    FORT(3, "Fort"),
    TRES_FORT(4, "Tres fort");

    private final int score;
    private final String label;

    StrengthLevel(int score, String label) {
        this.score = score;
        this.label = label;
    }

    public String label() {
        return label;
    }

    /**
     * Convertit un score zxcvbn brut (0 a 4) en niveau lisible.
     * En cas de score hors limites (defaut du service externe), on
     * retombe prudemment sur TRES_FAIBLE plutot que de planter le programme :
     * mieux vaut sous-estimer la force d'un mot de passe que la surestimer.
     */
    public static StrengthLevel fromScore(int score) {
        for (StrengthLevel level : values()) {
            if (level.score == score) {
                return level;
            }
        }
        return TRES_FAIBLE;
    }
}
