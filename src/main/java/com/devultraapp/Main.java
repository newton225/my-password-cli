package com.devultraapp;

import com.devultraapp.cli.CliOptions;
import com.devultraapp.generator.PasswordGenerator;
import com.devultraapp.strenght.StrengthClient;
import com.devultraapp.strenght.StrengthResult;

import java.util.List;

public class Main {
    public static void main(String[] args) {

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
            }
            System.out.println();
        }
    }
}
