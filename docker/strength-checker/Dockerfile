# Image legere : Python 3.12 en variante "slim" suffit largement pour
# Flask + zxcvbn (pas besoin de toolchain de compilation lourde).
FROM python:3.12-slim

WORKDIR /app

# On copie d'abord uniquement requirements.txt pour profiter du cache
# Docker : si seul app.py change, l'etape pip install n'est pas refaite.
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY app.py .

EXPOSE 5000

# Healthcheck Docker natif : permet a "docker ps" / docker-compose de
# signaler clairement si le service est reellement pret a repondre,
# et pas seulement si le processus a demarre.
HEALTHCHECK --interval=10s --timeout=3s --start-period=5s --retries=3 \
    CMD python -c "import urllib.request; urllib.request.urlopen('http://localhost:5000/health')" || exit 1

CMD ["python", "app.py"]
