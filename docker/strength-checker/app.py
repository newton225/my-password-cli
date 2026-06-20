"""
Micro-service d'audit de mots de passe.

Ce service est le coeur de la partie "DevOps / Docker" du projet : il tourne
dans un conteneur isole et expose l'algorithme zxcvbn (developpe par Dropbox)
via une API REST minimaliste. zxcvbn estime la robustesse d'un mot de passe
en se basant sur des heuristiques realistes (dictionnaires courants, motifs
de clavier, dates, repetitions, substitutions l33t...) plutot que sur de
simples regles de longueur/complexite, ce qui en fait une validation
nettement plus pertinente que ce qu'un programme Java seul calculerait.

Le score retourne va de 0 (Tres faible) a 4 (Tres fort), ce qui correspond
exactement aux 5 niveaux demandes dans le cahier des charges.
"""

from flask import Flask, request, jsonify
from zxcvbn import zxcvbn

app = Flask(__name__)



if __name__ == "__main__":
    # 0.0.0.0 est necessaire pour que le port soit accessible depuis
    # l'exterieur du conteneur (depuis l'hote ou un autre conteneur).
    app.run(host="0.0.0.0", port=5000)
