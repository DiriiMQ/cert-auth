# Signature Backend

Spring Boot application providing REST endpoints to sign and verify JPEG/PNG images using detached JWS signatures embedded in metadata.

## Endpoints
- `POST /api/v1/sign` – upload an image and receive signed image with embedded JWS.
- `POST /api/v1/verify` – upload signed image to verify; returns `{ "valid": true|false }`.
- `GET /health` – health check.

## Build & Run
Use the Makefile in repository root:

```bash
make build   # compile
make test    # run tests with coverage
make run     # run locally
make docker-build
make docker-run
```
