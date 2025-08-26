# Image Signature Verification Service

A defensive security application that provides cryptographic image signature verification using detached JWS signatures embedded in image metadata.

## Features

- **Digital Image Signing**: Signs JPEG/PNG images by embedding detached JWS signatures in image metadata
- **Signature Verification**: Verifies signed images by extracting and validating JWS signatures
- **Secure Key Management**: Multi-tier RSA key loading with environment variables, external files, and dynamic generation
- **Metadata Preservation**: Maintains image integrity while adding cryptographic signatures
- **RESTful API**: HTTP endpoints for signing and verification operations

## Quick Start

```bash
# Build and run the application
make build
make run

# Sign an image
curl -X POST "http://localhost:8080/api/v1/sign" -F "file=@image.png" --output signed.png

# Verify a signed image  
curl -X POST "http://localhost:8080/api/v1/verify" -F "file=@signed.png"
# Returns: {"valid": true}
```

## Architecture

- **backend/** – Spring Boot service with REST API endpoints
- **frontend/** – Placeholder for future web interface

## Security

- Uses RS256 (RSA-SHA256) algorithm for signing
- Calculates SHA-256 digest of image pixel data for signature validation
- Secure key management with environment variables or dynamic generation
- No private keys stored in repository

## Documentation

See [CLAUDE.md](CLAUDE.md) for detailed development guidance, key management options, and deployment instructions.
