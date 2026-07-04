# Kirigen Platform Android App

A professional networking and job search Android application similar to LinkedIn and Naukri, built with Java.

## Features

- **User Authentication**: Login and registration with JWT token management
- **Job Search**: Browse and search for jobs from the backend API
- **Professional Network**: Connect with other users and build your network
- **Profile Management**: View and edit your professional profile
- **Notifications**: Stay updated with connection requests and job alerts
- **Modern UI**: Clean, professional interface with blue, white, and navy blue color scheme

## Tech Stack

- **Language**: Java
- **Minimum SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)
- **Architecture**: MVVM with Fragments
- **Networking**: Retrofit 2 + OkHttp
- **Image Loading**: Glide
- **UI Components**: Material Design Components
- **Navigation**: Bottom Navigation with Fragments

## Color Scheme

The app uses a professional color scheme with:
- **Primary Blue**: #0A66C2 (LinkedIn-style blue)
- **Navy Blue**: #1E3A5F (20% usage throughout the app)
- **White**: #FFFFFF (Backgrounds and cards)
- **Light Gray**: #F5F5F5 (Secondary backgrounds)

## API Integration

The app connects to the existing backend at:
- **Base URL**: https://asgapp.onrender.com/api

### Available Endpoints

- **Auth**: `/api/auth/register`, `/api/auth/login`, `/api/auth/refresh`
- **Users**: `/api/users/profile/:userId`, `/api/users/verified`
- **Jobs**: `/api/jobs/`
- **Connections**: `/api/connections/`
- **Notifications**: `/api/notifications/`
- **Projects**: `/api/projects/`

## Project Structure

```
app/
├── src/main/
│   ├── java/com/kirigenplatform/
│   │   ├── adapter/           # RecyclerView adapters
│   │   ├── data/
│   │   │   ├── api/          # Retrofit API client
│   │   │   └── model/        # Data models
│   │   ├── ui/
│   │   │   ├── auth/         # Login/Register activities
│   │   │   ├── fragments/    # Main screen fragments
│   │   │   └── main/         # Main activity
│   │   └── utils/            # SharedPreferences helper
│   ├── res/
│   │   ├── drawable/         # Drawables and icons
│   │   ├── layout/           # XML layouts
│   │   ├── menu/             # Menu resources
│   │   ├── values/           # Strings, colors, themes
│   └── AndroidManifest.xml
```

## Building the Project

1. Open the project in Android Studio
2. Sync Gradle files
3. Build and run on an emulator or physical device

## Key Features Implementation

### Authentication
- JWT token storage using SharedPreferences
- Automatic token refresh
- Session persistence

### Navigation
- Bottom navigation with 5 tabs
- Fragment-based architecture
- Smooth transitions between screens

### Data Loading
- Retrofit for API calls
- Swipe-to-refresh functionality
- Loading states and error handling

## Screens

1. **Login/Register**: Clean authentication screens with navy blue headers
2. **Home**: Job feed with personalized recommendations
3. **Jobs**: Browse all available jobs with filters
4. **Network**: View and connect with other professionals
5. **Notifications**: Stay updated with alerts
6. **Profile**: View and edit your professional profile

## Dependencies

- Material Components
- Retrofit 2
- Gson
- Glide
- SwipeRefreshLayout
- RecyclerView
- CardView

## Notes

- The app uses the existing backend without any modifications
- All API calls are made to https://asgapp.onrender.com
- The UI follows LinkedIn/Naukri design patterns
- Navy blue is used strategically (20% of the UI) for headers and important elements
