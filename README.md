# 🛍️ Uniclub E-Commerce System
# Test ci/cd
[![CI/CD](https://github.com/huytran19-dot/Uniclub_Testing/actions/workflows/cd.yml/badge.svg)](https://github.com/huytran19-dot/Uniclub_Testing/actions)
[![Docker](https://img.shields.io/badge/docker-ready-blue.svg)](https://github.com/huytran19-dot/Uniclub_Testing/pkgs/container/uniclub-frontend)

Modern full-stack e-commerce platform with Spring Boot backend and React frontend, featuring comprehensive admin dashboard and customer shopping experience.

## 🌐 Live Demo

Experience the platform in action:

- 🛍️ **Customer Website:** https://uniclub-testing.vercel.app/
- 👨‍💼 **Admin Dashboard:** https://uniclub-testing-caeg.vercel.app/dashboard
- 🔧 **Backend API:** https://uniclubtesting-production.up.railway.app/api

### Demo Credentials
```
Admin: admin@uniclub.com / huytran123
Customer: buyer@uniclub.com / huytran123
```

> **Note:** Backend is hosted on Railway's free tier, so initial requests may take a few seconds to wake up.

## 🚀 Quick Start

### Development Mode (Hot Reload)
Perfect for active development with instant code updates:

```bash
# Clone repository
git clone https://github.com/huytran19-dot/Uniclub_Testing.git
cd Uniclub_Testing

# Start everything (MySQL Docker + Backend + Frontend)
start-all.bat
```

**Access points:**
- 🌐 Customer Website: http://localhost:5173
- 👨‍💼 Admin Dashboard: http://localhost:5174
- 🔧 Backend API: http://localhost:8080
- 💾 phpMyAdmin: http://localhost:8081

### Production Mode (Docker)
Production-ready deployment with Docker containers:

```bash
# Start with Docker Compose
docker-compose up -d
```

**Access points:**
- 🌐 Main Website: http://localhost
- 👨‍💼 Admin Panel: http://localhost/admin
- 🔧 Backend API: http://localhost/api
- 💾 phpMyAdmin: http://localhost:8081

### Stop Services

```bash
# Stop all services
stop-all.bat

# Or stop Docker only
docker-compose down
```

## ✨ Features

### Customer Features
- 🔐 **Authentication** - JWT-based login with email OTP verification
- 🛒 **Shopping Cart** - Real-time cart management with quantity control
- 💳 **Payment** - Multiple payment methods (COD & VNPay)
- 📦 **Order Tracking** - Real-time order status updates
- 🔍 **Product Search** - Advanced filtering by category, brand, size, color
- 👤 **User Profile** - Account management and order history

### Admin Features
- 📊 **Dashboard** - Sales analytics and business insights
- 👔 **Product Management** - CRUD with variants (colors, sizes)
- 📦 **Order Management** - Order processing and status updates
- 📥 **Inventory** - Goods Receipt Note (GRN) system with anti-spam protection
- 👥 **User Management** - Customer and staff administration
- 🏢 **Supplier Management** - Vendor relationship management
- 📧 **Email Notifications** - Automated order confirmations (SendGrid)

### Technical Features
- 🚀 **CI/CD Pipeline** - Automated builds and deployments
- 🐳 **Docker Support** - Containerized deployment
- 🔒 **Security** - Spring Security + JWT authentication
- 📱 **Responsive Design** - Mobile-friendly interface
- ⚡ **Performance** - Optimized loading with code splitting

## 🔑 Default Login Credentials

### Development Environment
```
Admin: admin@uniclub.com / huytran123
Customer: buyer@uniclub.com / huytran123
```

### Database Access
```
phpMyAdmin: http://localhost:8081
Username: root
Password: (no password)
```

## 🛠️ Tech Stack

### Backend
- **Framework:** Spring Boot 3.x
- **Security:** Spring Security + JWT
- **Database:** MySQL 8.0
- **Email:** SendGrid API
- **Payment:** VNPay Gateway
- **Build:** Maven

### Frontend
- **Framework:** React 19.2.0
- **Routing:** React Router v7
- **Styling:** Tailwind CSS
- **Icons:** Lucide React
- **� Project Structure

```
uniclub/
├── .github/
│   └── workflows/         # CI/CD pipelines
│       ├── ci.yml        # Continuous Integration
│       └── cd.yml        # Continuous Deployment
├── uniclub-be/           # Spring Boot Backend
│   ├── src/
│   ├── Dockerfile
│   └── pom.xml
├── uniclub-fe/           # React Frontend (Monorepo)
│   ├── admin/           # Admin Dashboard (Port 5174)
│   ├── web/             # Customer Website (Port 5173)
│   ├── Dockerfile       # Multi-stage build
│   └── nginx.conf       # Production config
├── mysql-init/          # Database initialization
│   └── init-database.sql
├── selenium-tests/      # E2E testing
├── docker-compose.yml   # Local development
└── *.bat               # Windows helper scripts
```

## 📝 Prerequisites

Before running the project, ensure you have:

### Required
- **Docker Desktop** - For MySQL and production deployment
- *🐛 Common Issues & Solutions

### Port Already in Use
```bash
# Check and kill process on port
netstat -ano | findstr :8080
taskkill /PID <process_id> /F
```

### Frontend Build Errors
```bash
# Clear cache and reinstall
cd uniclub-fe/admin
rm -rf node_modules .vite
pnpm install
```

### Database Connection Issues
- Ensure MySQL container is running: `docker ps`
- Check connection in `application.properties`
- Verify port 3306 is not blocked

### Docker Build ARM64 Error
- Project only supports `linux/amd64` platform
- ARM64 builds disabled due to QEMU emulation issues
- Works on most cloud providers (AWS, GCP, Azure)

## 🧪 Testing

### Automated Testing Suite

The project includes comprehensive testing at multiple levels:

#### Unit Tests
```bash
# Backend tests
cd uniclub-be
mvn test

# Frontend tests
cd uniclub-fe/admin
pnpm test
```

#### End-to-End Tests (Selenium)
We use Selenium WebDriver with TestNG for automated browser testing:

```bash
# Run all E2E tests
cd selenium-tests
mvn test

# Run with Chrome in visible mode
./run-visible-chrome-with-allure.bat

# Generate Allure report
mvn allure:serve
```

**Test Coverage:**
- ✅ User Registration & Login
- ✅ Product Search & Filtering
- ✅ Shopping Cart Operations
- ✅ Checkout & Payment Flow
- ✅ Order Management
- ✅ Admin Dashboard Functions

**Features:**
- 🔍 Page Object Model (POM) design pattern
- 📊 Allure reporting with screenshots
- 🧹 Automatic cleanup of test data
- 🌐 Cross-browser testing support
- ⚡ Parallel test execution

See [selenium-tests/README.md](selenium-tests/README.md) and [TESTING_GUIDE.md](TESTING_GUIDE.md) for detailed testing documentation.

## 🚀 Deployment

### Production Deployments

The application is deployed on multiple platforms:

**Frontend:**
- **Customer Website:** Vercel - https://uniclub-testing.vercel.app/
- **Admin Dashboard:** Vercel - https://uniclub-testing-caeg.vercel.app/

**Backend:**
- **API Server:** Railway - https://uniclubtesting-production.up.railway.app/api
- **Database:** Railway MySQL instance

### Docker Hub / GHCR
Images are automatically built and pushed via GitHub Actions:
- `ghcr.io/huytran19-dot/uniclub-backend:latest`
- `ghcr.io/huytran19-dot/uniclub-frontend:latest`

### Deploy Your Own

#### Deploy to Vercel (Frontend)
[![Deploy with Vercel](https://vercel.com/button)](https://vercel.com/new/clone?repository-url=https://github.com/huytran19-dot/Uniclub_Testing)

#### Deploy to Railway (Backend)
[![Deploy on Railway](https://railway.app/button.svg)](https://railway.app/template)

#### Manual Deployment
```bash
# Build and run with Docker Compose
docker-compose -f docker-compose.production.yml up -d
```

See [CICD.md](CICD.md) for CI/CD pipeline details.

## 🤝 Contributing

We welcome contributions! Please follow these steps:

1. Fork the repository
2. Create feature branch: `git checkout -b feature/amazing-feature`
3. Commit changes: `git commit -m 'Add amazing feature'`
4. Push to branch: `git push origin feature/amazing-feature`
5. Open Pull Request

### Code Style
- Backend: Follow Spring Boot best practices
- Frontend: Use ESLint and Prettier configurations
- Commit messages: Use conventional commits format

## 📄 License

This project is for educational purposes only.

## 👥 Team

- **Frontend:** React + Tailwind CSS
- **Backend:** Spring Boot + MySQL
- **DevOps:** Docker + GitHub Actions

---

📚 **Documentation:**
- [Setup Guide](SETUP_GUIDE.md) - Detailed installation instructions
- [Docker Guide](DOCKER_SETUP_GUIDE.md) - Docker-specific documentation
- [Testing Guide](TESTING_GUIDE.md) - Testing procedures
- [CI/CD Guide](CICD.md) - Deployment pipeline

🐛 **Found a bug?** [Open an issue](https://github.com/huytran19-dot/Uniclub_Testing/issues)

⭐ **Like this project?** Give it a star!

# Verify installations
java -version
node -version
pnpm -version
docker --version
```
Uniclub_Testing/
├── uniclub-be/           # Spring Boot Backend
├── uniclub-fe/
│   ├── admin/           # Admin Dashboard
│   └── web/             # Customer Website
├── mysql-init/          # Database Schema
├── docker-compose.yml   # MySQL + phpMyAdmin
└── SETUP_GUIDE.md       # Detailed Setup Guide
```

## 🤝 Contributing

1. Fork the repository
2. Create feature branch: `git checkout -b feature/new-feature`
3. Commit changes: `git commit -m 'Add new feature'`
4. Push to branch: `git push origin feature/new-feature`
5. Submit pull request

## 📄 License

This project is for educational purposes.

---

For detailed setup instructions, see **[SETUP_GUIDE.md](SETUP_GUIDE.md)**
# Uniclub_DevOps
