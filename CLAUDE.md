# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

### Backend Commands
All backend development commands are managed via the root `Makefile`:

- `make build` - Compile the Spring Boot backend using Maven
- `make test` - Run tests with JaCoCo code coverage (minimum 80% required)
- `make run` - Run the backend application locally on port 8080
- `make docker-build` - Build Docker image for the backend
- `make docker-run` - Run the backend in Docker container

### Frontend Commands
Frontend development commands are managed via npm scripts in the `frontend/` directory:

- `cd frontend && npm run dev` - Start development server on port 8080
- `cd frontend && npm run build` - Build for production
- `cd frontend && npm run build:dev` - Build for development
- `cd frontend && npm run lint` - Run ESLint
- `cd frontend && npm run preview` - Preview production build

## Architecture

### Backend (Spring Boot)
The backend is a defensive security application that provides image signature verification capabilities using detached JWS signatures embedded in image metadata.

**Core Components:**
- `SignatureController` (`/api/v1`) - REST endpoints for signing and verification
- `SignatureService` - Core business logic for JWS operations and image metadata handling
- `KeyConfig` - Secure RSA key pair configuration with multiple loading strategies
- `HealthController` - Health check endpoint at `/health`

**Key Features:**
- Signs JPEG/PNG images by embedding detached JWS signatures in image metadata
- Verifies signed images by extracting and validating JWS signatures
- Uses RS256 (RSA-SHA256) algorithm for signing
- Embeds signatures in XMP metadata for JPEG, iTXt metadata for PNG
- Calculates SHA-256 digest of image pixel data for signature validation

**Dependencies:**
- Nimbus JOSE + JWT library for JWS operations
- Apache Commons Imaging for JPEG XMP handling
- Drew Noakes metadata-extractor for reading image metadata
- TwelveMonkeys ImageIO extensions for enhanced format support

### Frontend (React + TypeScript)
The frontend is a modern React application built with Vite, TypeScript, and shadcn/ui components for the image signing and verification interface.

**Core Components:**
- `App.tsx` - Main application with routing setup using React Router
- `ImageUpload.tsx` - Drag-and-drop image upload component with sign/verify modes
- `ApiService` - HTTP client for backend API communication
- `pages/Index.tsx` - Main page with tabbed interface for signing and verification
- `components/ui/` - shadcn/ui component library for consistent UI

**Key Features:**
- Responsive drag-and-drop image upload interface
- Tabbed interface for signing and verification workflows
- Real-time upload progress and result feedback
- Download functionality for signed images
- File type validation (JPEG/PNG only)
- Integration with backend REST API endpoints

**Technology Stack:**
- **React 18** with TypeScript for type-safe development
- **Vite** for fast development and optimized builds
- **shadcn/ui** + **Radix UI** for accessible component primitives
- **Tailwind CSS** for utility-first styling
- **React Router** for client-side routing
- **TanStack Query** for API state management
- **React Dropzone** for file upload functionality
- **Lucide React** for icons
- **React Hook Form** + **Zod** for form validation

**API Integration:**
- `POST /api/v1/sign` - Upload image for signing, returns signed image blob
- `POST /api/v1/verify` - Upload signed image for verification, returns validation result
- `GET /health` - Backend health check endpoint

**Development:**
- ESLint configuration with React and TypeScript rules
- Path aliases configured (`@/` maps to `./src/`)
- Development server runs on port 8080 (same as backend for simplicity)

## Key Management

The application uses secure RSA key management with multiple loading strategies (in priority order):

### 1. Environment Variables (Production)
Set these environment variables for production deployment:
```bash
export RSA_PRIVATE_KEY="-----BEGIN PRIVATE KEY-----
MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQ...
-----END PRIVATE KEY-----"

export RSA_PUBLIC_KEY="-----BEGIN PUBLIC KEY-----
MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA...
-----END PUBLIC KEY-----"
```

### 2. External File Paths (Staging)
Configure file paths for key loading:
```bash
export RSA_PRIVATE_KEY_FILE="/secure/path/to/private.pem"
export RSA_PUBLIC_KEY_FILE="/secure/path/to/public.pem"
```
- Files must be outside the application resources directory
- Files must exist and be readable by the application

### 3. Dynamic Generation (Development)
If no keys are provided, the application automatically generates a new RSA key pair using the Nimbus JOSE+JWT library.

**Security Notes:**
- Never commit private keys to version control
- Use environment variables or secure key management systems in production
- Dynamically generated keys are ephemeral and not persistent across restarts

## Testing
- JaCoCo coverage enforced at 80% minimum during verify phase
- Integration tests cover REST endpoints
- Unit tests cover service layer logic