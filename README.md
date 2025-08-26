# sign2shine - Secure Image Signing & Verification

A modern full-stack application for cryptographic image signing and verification using detached JWS signatures embedded in image metadata. Features a React frontend with intuitive drag-and-drop interface and a Spring Boot backend API.

## ✨ Features

### Core Functionality
- **Digital Image Signing**: Signs JPEG/PNG images by embedding detached JWS signatures in image metadata
- **Signature Verification**: Verifies signed images by extracting and validating JWS signatures
- **Drag & Drop Interface**: Modern React frontend with intuitive file upload
- **Real-time Processing**: Live feedback during signing and verification operations

### Security & Architecture
- **Secure Key Management**: Multi-tier RSA key loading with environment variables, external files, and dynamic generation
- **Metadata Preservation**: Maintains image integrity while adding cryptographic signatures
- **CORS Security**: Properly configured cross-origin resource sharing
- **RESTful API**: Clean HTTP endpoints for all operations

## 🚀 Quick Start

### Using Docker Compose (Recommended)

```bash
# Start both frontend and backend services
docker compose up --build

# Access the application
open http://localhost:3000
```

**Service URLs:**
- **Frontend**: http://localhost:3000 - Modern React web interface
- **Backend**: http://localhost:8081 - REST API endpoints
- **Health Check**: http://localhost:8081/health

### Manual Development

```bash
# Backend only
make build
make run

# Frontend only  
cd frontend
npm install
npm run dev
```

### API Usage

```bash
# Sign an image
curl -X POST "http://localhost:8081/api/v1/sign" -F "file=@image.png" --output signed.png

# Verify a signed image  
curl -X POST "http://localhost:8081/api/v1/verify" -F "file=@signed.png"
# Returns: {"valid": true}
```

## 🏗 Architecture

### Full-Stack Application
- **frontend/** – Modern React + TypeScript web interface with shadcn/ui components
- **backend/** – Spring Boot service with REST API endpoints and comprehensive CORS support
- **docker-compose.yml** – Orchestrates both services with proper networking

### Technology Stack
- **Frontend**: React 18, TypeScript, Vite, shadcn/ui, Tailwind CSS
- **Backend**: Spring Boot 3, Java 17, Nimbus JOSE+JWT
- **Infrastructure**: Docker, Docker Compose, Nginx

## Security

- Uses RS256 (RSA-SHA256) algorithm for signing
- Calculates SHA-256 digest of image pixel data for signature validation
- Secure key management with environment variables or dynamic generation
- No private keys stored in repository

## Documentation

For detailed development guidance, key management options, and deployment instructions, see the individual README files in each service directory:
- **Frontend**: [frontend/README.md](frontend/README.md) - React application setup and development
- **Backend**: Backend development commands are available via the root `Makefile`
