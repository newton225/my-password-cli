package com.devultraapp.cli;

import java.util.Scanner;

/**
 * Analyse les arguments passes en ligne de commande. Si aucun argument
 * n'est fourni, bascule en mode interactif (questions/reponses au clavier),
 * conformement au cahier des charges qui autorise les deux modes
 * d'interaction.
 *
 * Arguments supportes :
 *   --length=16          longueur du mot de passe
 *   --upper / --no-upper
 *   --lower / --no-lower
 *   --digits / --no-digits
 *   --symbols / --no-symbols
 *   --count=5            nombre de mots de passe a generer (mode rafale)
 *   --no-server          desactive l'appel au conteneur Docker (score local uniquement)
 *   --help
 */
public final class ArgumentParser {

    private ArgumentParser() {
        // Classe utilitaire : pas d'instanciation
    }

    public static CliOptions parse(String[] args) {
        if (args.length == 0) {
            return parseInteractive();
        }
        if (containsHelp(args)) {
            printHelp();
            System.exit(0);
        }

        int length = 16;
        boolean upper = true, lower = true, digits = true, symbols = true;
        int count = 1;
        boolean noServer = false;

        for (String arg : args) {
            if (arg.startsWith("--length=")) {
                length = parseIntArg(arg, "--length=");
            } else if (arg.startsWith("--count=")) {
                count = parseIntArg(arg, "--count=");
            } else if (arg.equals("--upper")) {
                upper = true;
            } else if (arg.equals("--no-upper")) {
                upper = false;
            } else if (arg.equals("--lower")) {
                lower = true;
            } else if (arg.equals("--no-lower")) {
                lower = false;
            } else if (arg.equals("--digits")) {
                digits = true;
            } else if (arg.equals("--no-digits")) {
                digits = false;
            } else if (arg.equals("--symbols")) {
                symbols = true;
            } else if (arg.equals("--no-symbols")) {
                symbols = false;
            } else if (arg.equals("--no-server")) {
                noServer = true;
            } else {
                throw new IllegalArgumentException("Argument inconnu : " + arg + " (utilisez --help)");
            }
        }

        return new CliOptions(length, upper, lower, digits, symbols, count, noServer);
    }

    /**
     * Mode interactif : pose les questions une a une. Utilise quand
     * l'utilisateur lance simplement "java -jar password-cli.jar" sans
     * argument, pour rester accessible meme sans connaitre la syntaxe CLI.
     */
    private static CliOptions parseInteractive() {
        Scanner sc = new Scanner(System.in);

        System.out.println("=== Generateur de mots de passe - mode interactif ===");

        int length = askInt(sc, "Longueur du mot de passe [16] : ", 16);
        boolean upper = askBool(sc, "Inclure des majuscules ? (o/n) [o] : ", true);
        boolean lower = askBool(sc, "Inclure des minuscules ? (o/n) [o] : ", true);
        boolean digits = askBool(sc, "Inclure des chiffres ? (o/n) [o] : ", true);
        boolean symbols = askBool(sc, "Inclure des symboles ? (o/n) [o] : ", true);
        int count = askInt(sc, "Combien de mots de passe generer (mode rafale) ? [1] : ", 1);
        boolean noServer = !askBool(sc, "Valider la force via le conteneur Docker ? (o/n) [o] : ", true);

        return new CliOptions(length, upper, lower, digits, symbols, count, noServer);
    }

    private static int askInt(Scanner sc, String prompt, int defaultValue) {
        System.out.print(prompt);
        String line = sc.nextLine().trim();
        if (line.isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(line);
        } catch (NumberFormatException e) {
            System.out.println("Valeur invalide, utilisation de la valeur par defaut : " + defaultValue);
            return defaultValue;
        }
    }

    private static boolean askBool(Scanner sc, String prompt, boolean defaultValue) {
        System.out.print(prompt);
        String line = sc.nextLine().trim().toLowerCase();
        if (line.isEmpty()) {
            return defaultValue;
        }
        return line.startsWith("o") || line.equals("y") || line.equals("yes");
    }

    private static int parseIntArg(String arg, String prefix) {
        try {
            return Integer.parseInt(arg.substring(prefix.length()));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Valeur numerique attendue pour " + prefix + " : " + arg);
        }
    }

    private static boolean containsHelp(String[] args) {
        for (String a : args) {
            if (a.equals("--help") || a.equals("-h")) {
                return true;
            }
        }
        return false;
    }

    private static void printHelp() {
        System.out.println("""
                Usage: java -jar password-cli.jar [options]

                Options:
                  --length=N        Longueur du mot de passe (defaut: 16)
                  --upper/--no-upper    Inclure/exclure les majuscules (defaut: inclus)
                  --lower/--no-lower    Inclure/exclure les minuscules (defaut: inclus)
                  --digits/--no-digits  Inclure/exclure les chiffres (defaut: inclus)
                  --symbols/--no-symbols Inclure/exclure les symboles (defaut: inclus)
                  --count=N         Nombre de mots de passe a generer (mode rafale, defaut: 1)
                  --no-server       Ne pas interroger le conteneur Docker de validation
                  --help            Affiche cette aide

                Sans argument, le programme passe en mode interactif.
                """);
    }
}
