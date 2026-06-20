package com.devultraapp;

import com.devultraapp.cli.ArgumentParser;
import com.devultraapp.cli.CliOptions;
import com.devultraapp.generator.PasswordGenerator;
import com.devultraapp.strenght.StrengthClient;
import com.devultraapp.strenght.StrengthResult;

import java.util.List;

/**
 * Point d'entree de l'application.
 *
 * Orchestre les trois etapes du programme :
 *   1. Lecture/validation des options (CLI ou interactif)
 *   2. Generation des mots de passe (mode "rafale" si count > 1)
 *   3. Audit de robustesse de chaque mot de passe via le conteneur Docker
 */
public class Main {
    public static void main(String[] args) {
        try {
            CliOptions options = ArgumentParser.parse(args);
            run(options);
        } catch (IllegalArgumentException e) {
            System.err.println("Erreur : " + e.getMessage());
            System.exit(1);
        }
    }

    private static void run(CliOptions options) {
        PasswordGenerator generator = new PasswordGenerator();
        List<String> passwords = generator.generate(options);

        StrengthClient strengthClient = options.noServer() ? null : new StrengthClient();

        System.out.println();
        System.out.println("=== Resultats (" + passwords.size() + " mot(s) de passe) ===");
        System.out.println();

        for (int i = 0; i < passwords.size(); i++) {
            String password = passwords.get(i);
            System.out.printf("[%d] %s%n", i + 1, password);

            if (strengthClient != null) {
                StrengthResult result = strengthClient.evaluate(password);
                printStrength(result);
            }
            System.out.println();
        }
    }

    private static void printStrength(StrengthResult result) {
        String source = result.fromLocal() ? "(estimation locale - conteneur Docker indisponible)" : "(audit conteneur Docker)";
        System.out.printf("    Robustesse : %s %s%n", result.level().label(), source);
        System.out.printf("    Temps de cassage estime : %s%n", result.crackTime());
    }
}
