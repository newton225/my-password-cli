package com.devultraapp.cli;

/**
 * Objet immuable representant les options de generation demandees par
 * l'utilisateur (longueur, jeux de caracteres, nombre de mots de passe a
 * produire en mode "rafale").
 *
 * On utilise un "record" Java 21 plutot qu'une classe classique : les options
 * sont figees une fois le parsing termine, ce qui evite des effets de bord
 * si elles sont relues plus tard (ex: lors de l'affichage du recapitulatif).
 */
public record CliOptions(
        int length,
        boolean useUpper,
        boolean useLower,
        boolean useDigits,
        boolean useSymbols,
        int count,
        boolean noServer
) {

    /**
     * Valide la coherence des options. On le fait ici (et pas dans le parser)
     * pour que toute construction de CliOptions, quelle que soit son origine,
     * garantisse un etat utilisable.
     */
    public CliOptions {
        if (length < 4 || length > 128) {
            throw new IllegalArgumentException(
                    "La longueur doit etre comprise entre 4 et 128 caracteres (recu : " + length + ")");
        }
        if (!useUpper && !useLower && !useDigits && !useSymbols) {
            throw new IllegalArgumentException(
                    "Au moins un type de caractere doit etre selectionne (majuscules/minuscules/chiffres/symboles)");
        }
        if (count < 1 || count > 1000) {
            throw new IllegalArgumentException(
                    "Le nombre de mots de passe a generer doit etre compris entre 1 et 1000 (recu : " + count + ")");
        }
    }
}
