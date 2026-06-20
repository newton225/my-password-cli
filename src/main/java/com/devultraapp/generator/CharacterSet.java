package com.devultraapp.generator;

/**
 * Regroupe les jeux de caracteres utilisables pour la generation.
 *
 * Les symboles sont volontairement limites a un sous-ensemble courant
 * (compatible avec la plupart des formulaires de saisie) plutot que
 * l'ensemble ASCII complet, pour eviter des caracteres qui posent souvent
 * probleme (espace, guillemets, antislash...).
 */
public final class CharacterSet {

    public static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    public static final String DIGITS = "0123456789";
    public static final String SYMBOLS = "!@#$%^&*()-_=+[]{};:,.?";

    private CharacterSet() {
    }
}
