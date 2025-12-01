# SkillSwap Frontend Applications

This project includes two frontend applications for the SkillSwap platform:

1. **React Web App** (`frontend-web/`) - Desktop/web optimized
2. **React Native/Expo Mobile App** (`frontend-mobile/`) - Mobile app optimized

Both applications feature:
- Dark theme with light highlights
- Animated discover cards
- Complete profile editing features
- Mobile keyboard optimization
- Modern, responsive UI

## Quick Start

### Web App (Desktop)

```bash
cd frontend-web
npm install
npm start
```

The web app will run on http://localhost:3000

### Mobile App

```bash
cd frontend-mobile
npm install
npx expo start
```

Scan the QR code with Expo Go app on your phone to test.

## Features

### Web App Features
- Responsive design optimized for desktop
- Drag-to-swipe discover cards with Framer Motion animations
- Full profile editing with all fields
- Desktop-optimized layouts

### Mobile App Features
- Native mobile app experience
- Gesture-based swipe interactions
- Mobile keyboard optimization (prevents iOS zoom)
- Easy testing on physical devices via Expo Go
- Native animations with Reanimated

## Configuration

### Backend API URL

**Web App**: Update `REACT_APP_API_URL` in `.env` file or use proxy in `package.json`

**Mobile App**: Update `API_BASE_URL` in `frontend-mobile/src/services/api.js`
- For physical device testing, use your computer's IP address: `http://YOUR_IP:8080`
- Make sure your phone and computer are on the same WiFi network

## Testing on Phone

### Option 1: Expo Go (Recommended)
1. Install Expo Go from App Store (iOS) or Play Store (Android)
2. Run `npx expo start` in `frontend-mobile/`
3. Scan QR code with Expo Go app

### Option 2: Development Build
```bash
cd frontend-mobile
npx expo run:ios    # For iOS
npx expo run:android # For Android
```

## Tech Stack

### Web App
- React 18.2.0
- React Router 6.21.1
- Framer Motion 10.16.16
- Axios 1.6.2

### Mobile App
- Expo 54
- React Native 0.76.5
- React Navigation
- React Native Reanimated
- React Native Gesture Handler

## Mobile Keyboard Optimization

Both apps include keyboard optimizations:
- Input fields use `fontSize: 16px` to prevent iOS zoom
- KeyboardAvoidingView for proper keyboard handling
- Touch-optimized button sizes
- Proper input types and autocomplete attributes

## Profile Editing Features

Both apps support all profile editing features:
- Basic information (name, bio, major, year)
- Location with geocoding
- Career information (goals, experience, publications, awards)
- Skills (with offering/seeking flags)
- Interests
- Organizations
- Languages
- Social links (LinkedIn, GitHub, Portfolio)
- Profile photos

## Discover Card Animations

### Web App
- Drag-to-swipe with Framer Motion
- Smooth animations and transitions
- Visual feedback on swipe

### Mobile App
- Native gesture-based swiping
- PanResponder for touch interactions
- Smooth native animations

## Dark Theme

Both apps use a consistent dark theme:
- Primary background: `#0f0f1e`
- Secondary background: `#1a1a2e`
- Card background: `#1e2746`
- Accent colors: `#667eea` (primary), `#764ba2` (secondary)
- Light text on dark backgrounds

## Development

### Web App Development
```bash
cd frontend-web
npm start
# App runs on http://localhost:3000
# Hot reload enabled
```

### Mobile App Development
```bash
cd frontend-mobile
npx expo start
# Scan QR code or press 'i' for iOS simulator, 'a' for Android
```

## Building for Production

### Web App
```bash
cd frontend-web
npm run build
# Output in `build/` directory
```

### Mobile App
```bash
cd frontend-mobile
# For iOS
npx expo build:ios

# For Android
npx expo build:android

# Or use EAS Build
eas build --platform ios
eas build --platform android
```

## Notes

- The backend API should be running on `http://localhost:8080`
- For mobile testing on physical devices, ensure backend is accessible from your network
- Both apps use the same API endpoints and data structures
- Authentication is handled via localStorage (web) and AsyncStorage (mobile)

