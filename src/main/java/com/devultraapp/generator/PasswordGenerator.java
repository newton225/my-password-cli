package com.devultraapp.generator;

import com.devultraapp.cli.CliOptions;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Coeur de la generation de mots de passe.
 *
 * Choix techniques importants :
 *  - On utilise SecureRandom (et non java.util.Random) car Random n'est pas
 *    cryptographiquement sur : sa periode et son algorithme (LCG) le rendent
 *    predictible. Pour un outil de securite, c'est non negociable.
 *  - On garantit qu'au moins un caractere de CHAQUE categorie cochee est
 *    present (sinon un mot de passe "long" pourrait statistiquement ne
 *    contenir aucun symbole alors que l'utilisateur l'a explicitement demande).
 *  - Le melange final (Collections.shuffle) utilise egalement SecureRandom
 *    pour eviter que les caracteres "obligatoires" ne se retrouvent toujours
 *    en debut de chaine, ce qui faciliterait une attaque par dictionnaire
 *    cible sur les premiers caracteres.
 */
public final class PasswordGenerator {

    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * Genere une liste de {@code options.count()} mots de passe respectant
     * les contraintes de {@code options}.
     */
    public List<String> generate(CliOptions options) {
        List<String> passwords = new ArrayList<>(options.count());
        for (int i = 0; i < options.count(); i++) {
            passwords.add(generateOne(options));
        }
        return passwords;
    }

    private String generateOne(CliOptions options) {
        List<String> activePools = new ArrayList<>();
        if (options.useUpper()) activePools.add(CharacterSet.UPPER);
        if (options.useLower()) activePools.add(CharacterSet.LOWER);
        if (options.useDigits()) activePools.add(CharacterSet.DIGITS);
        if (options.useSymbols()) activePools.add(CharacterSet.SYMBOLS);

        StringBuilder fullAlphabet = new StringBuilder();
        activePools.forEach(fullAlphabet::append);

        List<Character> chars = new ArrayList<>(options.length());

        // Etape 1 : on impose un representant de chaque categorie active,
        // pour respecter la demande explicite de l'utilisateur.
        for (String pool : activePools) {
            chars.add(pool.charAt(secureRandom.nextInt(pool.length())));
        }

        // Etape 2 : on complete le reste de la longueur en tirant dans
        // l'alphabet combine de toutes les categories actives.
        while (chars.size() < options.length()) {
            chars.add(fullAlphabet.charAt(secureRandom.nextInt(fullAlphabet.length())));
        }

        StringBuilder sb = new StringBuilder(chars.size());
        chars.forEach(sb::append);
        return sb.toString();
    }
}
