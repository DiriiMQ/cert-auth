# sign2shine Frontend

A modern React application for secure image signing and verification with cryptographic signatures.

## 🌟 Overview

**sign2shine** provides a clean, intuitive interface for digitally signing images and verifying their authenticity. The application embeds cryptographic signatures directly into image metadata, ensuring tamper detection while preserving image quality.

## 🚀 Features

### Core Functionality
- **Image Signing**: Upload JPEG/PNG images to add cryptographic signatures
- **Signature Verification**: Verify the authenticity of previously signed images
- **Drag & Drop Interface**: Modern file upload with visual feedback
- **Real-time Processing**: Live status updates during signing/verification

### User Experience
- **Responsive Design**: Works seamlessly on desktop and mobile devices
- **Accessibility**: WCAG-compliant with screen reader support
- **Error Handling**: Graceful error messages and network failure recovery
- **Progress Feedback**: Visual indicators for upload and processing status

### Security Features
- **Client-side Validation**: File type and size validation before upload
- **Secure Communication**: HTTPS-ready with proper CORS configuration
- **No Data Storage**: Images are processed in real-time without server storage

## 🛠 Technology Stack

### Core Framework
- **React 18** - Modern React with hooks and concurrent features
- **TypeScript** - Type-safe development with full IntelliSense
- **Vite** - Fast development server and optimized production builds

### UI Components
- **shadcn/ui** - High-quality, accessible component library
- **Radix UI** - Unstyled, accessible component primitives  
- **Tailwind CSS** - Utility-first styling with consistent design tokens
- **Lucide React** - Beautiful, customizable SVG icons

### State Management & Data Fetching
- **TanStack Query** - Powerful data synchronization for server state
- **React Hook Form** - Performant forms with minimal re-renders
- **Zod** - TypeScript-first schema validation

### File Handling
- **React Dropzone** - Flexible file upload with drag-and-drop
- **Native File API** - Browser-native file processing

### Development Tools
- **ESLint** - Code quality and consistency enforcement
- **Prettier** - Automatic code formatting
- **TypeScript ESLint** - TypeScript-specific linting rules

## 📁 Project Structure

```
frontend/
├── src/
│   ├── components/          # Reusable UI components
│   │   ├── ui/             # shadcn/ui components
│   │   ├── Header.tsx      # Application header with branding
│   │   ├── ImageUpload.tsx # File upload component
│   │   └── NavigationTabs.tsx # Tab navigation
│   ├── pages/              # Page components
│   │   ├── Index.tsx       # Main application page
│   │   └── NotFound.tsx    # 404 error page
│   ├── services/           # API and external services
│   │   └── api.ts          # Backend API client
│   ├── hooks/              # Custom React hooks
│   └── lib/                # Utility functions
├── public/                 # Static assets
│   ├── favicon.svg         # Modern SVG favicon
│   ├── favicon.ico         # Legacy ICO favicon
│   └── favicon-*.png       # PNG favicons (16x16, 32x32)
├── index.html              # HTML entry point
├── package.json            # Dependencies and scripts
├── tailwind.config.ts      # Tailwind CSS configuration
├── tsconfig.json           # TypeScript configuration
└── vite.config.ts          # Vite build configuration
```

## 🏃‍♂️ Getting Started

### Prerequisites
- **Node.js** 18+ (install with [nvm](https://github.com/nvm-sh/nvm))
- **npm** or **yarn** package manager

### Local Development

```bash
# Install dependencies
npm install

# Start development server
npm run dev

# Open browser to http://localhost:8080
```

### Build for Production

```bash
# Create optimized production build
npm run build

# Preview production build locally
npm run preview
```

### Code Quality

```bash
# Run ESLint
npm run lint

# Type checking
npx tsc --noEmit
```

## 🐳 Docker Usage

### Development with Docker Compose
```bash
# Start both frontend and backend
docker compose up --build

# Access application at http://localhost:3000
```

### Production Docker Build
```bash
# Build production container
docker build -t sign2shine-frontend .

# Run container
docker run -p 3000:80 sign2shine-frontend
```

## 🔧 Configuration

### Environment Variables

The application uses Vite environment variables (prefixed with `VITE_`):

```bash
# .env.local
VITE_API_BASE_URL=http://localhost:8081  # Backend API URL
```

### Backend Integration

The frontend communicates with the Spring Boot backend via REST API:

- **Health Check**: `GET /health`
- **Sign Image**: `POST /api/v1/sign`
- **Verify Image**: `POST /api/v1/verify`

## 🎨 Design System

### Branding
- **Primary Color**: Blue (`#3B82F6`)
- **Logo**: Sparkles icon representing the "shine" theme
- **Typography**: System fonts with Tailwind's font stack

### Component Guidelines
- All components use shadcn/ui patterns for consistency
- Tailwind classes for styling with design token system
- Accessible by default with proper ARIA labels

## 🚢 Deployment

### Static Hosting
The built application is a static site that can be deployed to:
- **Vercel** - Recommended for optimal performance
- **Netlify** - Great for static sites with form handling  
- **GitHub Pages** - Free hosting for open source projects
- **AWS S3 + CloudFront** - Scalable cloud hosting

### Docker Deployment
```bash
# Production deployment with Docker
docker compose -f docker-compose.prod.yml up -d
```

## 🤝 Contributing

### Development Workflow
1. **Fork** the repository
2. **Create** a feature branch: `git checkout -b feature/amazing-feature`
3. **Commit** changes: `git commit -m 'Add amazing feature'`
4. **Push** to branch: `git push origin feature/amazing-feature`
5. **Open** a Pull Request

### Code Standards
- Follow TypeScript best practices
- Use semantic commit messages
- Ensure all tests pass
- Maintain 100% TypeScript coverage
- Follow accessibility guidelines (WCAG 2.1 AA)

## 📄 License

This project is part of the cert-auth application suite.

## 🆘 Support

For issues and questions:
- **GitHub Issues**: Report bugs and feature requests
- **Documentation**: See root README for full system documentation
- **API Documentation**: Backend API documentation in `/backend` directory

---

**Built with ❤️ using React, TypeScript, and modern web technologies**