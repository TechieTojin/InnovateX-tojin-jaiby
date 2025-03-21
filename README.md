# Trackyyy: AI-Powered Expense Tracker

<p align="center">
  <img src="app/src/main/res/mipmap-xxxhdpi/ic_launcher.png" alt="Trackyyy Logo" width="120"/>
</p>

<p align="center">
  <a href="#features">Features</a> •
  <a href="#screenshots">Screenshots</a> •
  <a href="#installation">Installation</a> •
  <a href="#technologies">Technologies</a> •
  <a href="#architecture">Architecture</a> •
  <a href="#contributing">Contributing</a> •
  <a href="#license">License</a>
</p>

## Overview

Trackyyy is a next-generation expense tracking application powered by artificial intelligence. The app eliminates the friction of manual expense tracking by automatically capturing, categorizing, and analyzing your financial transactions. With smart insights and personalized recommendations, Trackyyy helps you build better spending habits and achieve your financial goals.

Developed by **Tojin Varkey Simson** and **Jaiby Joepsh**, Trackyyy combines cutting-edge mobile design with AI technologies including computer vision, natural language processing, and machine learning to create a seamless financial management experience.

## Features

### 🤖 AI-Powered Functionality
- **Smart Receipt Scanning**: Instantly extract merchant names, amounts, dates, and itemized expenses from receipts
- **Intelligent Categorization**: Automatically categorize expenses with >90% accuracy
- **Spending Pattern Recognition**: Identify trends and recurring expenses automatically
- **Anomaly Detection**: Flag unusual transactions that may indicate fraud

### 📱 Modern User Experience
- **Beautiful Material Design 3 Interface**: Clean, intuitive UI with visually appealing components
- **Voice Input**: Add transactions using voice commands
- **Smart Suggestions**: Quick-add recent or recurring transactions with one tap
- **Multiple Input Methods**: Manual entry, receipt scanning, voice, bank sync
- **Dark Mode Support**: Easy on the eyes during nighttime use

### 💰 Financial Management
- **Customizable Categories**: Tailor expense categories to match your lifestyle
- **Budget Setting & Tracking**: Set monthly or category-specific budgets
- **Multi-Currency Support**: Track expenses in different currencies with automatic conversion
- **Financial Reports**: Visual breakdowns of spending with insightful analytics
- **Goal Setting**: Create and track progress towards savings and spending goals

### 🔒 Security & Privacy
- **On-Device Processing**: Sensitive financial data processed locally on your device
- **End-to-End Encryption**: All cloud data secured with industry-standard encryption
- **Privacy Controls**: Granular permissions for data sharing and analysis

## Screenshots

<p align="center">
  <img src="screenshots/home_screen.png" width="200" alt="Home Screen"/>
  <img src="screenshots/add_transaction.png" width="200" alt="Add Transaction"/>
  <img src="screenshots/receipt_scan.png" width="200" alt="Receipt Scanning"/>
  <img src="screenshots/insights.png" width="200" alt="Financial Insights"/>
</p>

## Installation

### Prerequisites
- Android 8.0 (API level 26) or higher
- 100MB of free storage space
- Camera access for receipt scanning


### From GitHub Releases
1. Download the latest APK from the [Releases](https://github.com/fiu-team/trackyyy/releases) page
2. Enable "Install from unknown sources" in your device settings
3. Open the downloaded APK and follow installation prompts
4. Launch the app and complete setup

### Building from Source
```bash
# Clone the repository
git clone https://github.com/TechieTojin/InnovateX-tojin-jaiby

# Navigate to the project directory
cd trackyyy

# Build debug version
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug
```

## Technologies

### Frontend
- **Language**: Kotlin
- **UI Framework**: Android Jetpack Compose with Material Design 3
- **Architecture**: MVVM with Clean Architecture principles
- **Navigation**: Jetpack Navigation Component
- **Animation**: Lottie for complex animations

### AI & Machine Learning
- **Computer Vision**: Google ML Kit for OCR and receipt parsing
- **NLP**: TensorFlow Lite for text classification and entity extraction
- **Machine Learning**: On-device models for categorization and prediction

### Backend & Data
- **Database**: Room for local storage
- **Remote API**: Retrofit for network requests
- **Authentication**: Firebase Authentication
- **Cloud Storage**: Firebase Cloud Storage
- **Analytics**: Firebase Analytics (opt-in)

## Architecture

Trackyyy follows a modular Clean Architecture approach with the following layers:

1. **Presentation Layer**: UI components, ViewModels, and state management
2. **Domain Layer**: Business logic, use cases, and domain models
3. **Data Layer**: Repositories, data sources, and data models

The app employs several architectural patterns:
- **MVVM**: For reactive UI updates
- **Repository Pattern**: For data abstraction
- **Use Case Pattern**: For encapsulating business logic
- **Dependency Injection**: Using Hilt for modular and testable code

## Contributing

We welcome contributions to make Trackyyy even better! Here's how you can help:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

Please read our [CONTRIBUTING.md](CONTRIBUTING.md) for details on our code of conduct and submission process.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- **Tojin Varkey Simson** and **Jaiby Joepsh** for development and design
- All open-source libraries and frameworks used in this project
- Beta testers who provided valuable feedback

---

<p align="center">
  Made  by Tojin Varkey Simson and Jaiby Joepsh
</p>
