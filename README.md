# projet-description-image

L'application sert la page d'analyse à la racine. Elle envoie une image JPEG,
PNG ou WEBP (10 Mo maximum) et une question facultative à
`POST /api/description` en `multipart/form-data` (`image`, `question`).
La réponse contient le champ `description`; les erreurs contiennent un message
français dans le champ `erreur`.

Lancer les tests avec `.\gradlew.bat test` (Java 17 ou supérieur requis).