# Marriage App - Setup & Troubleshooting Guide

## ✅ What's Fixed

### 1. **Removed Unused Imports in MainActivity**
   - Cleaned up `ViewCompat` and `WindowInsetsCompat` warnings
   - Code is now lint-free

### 2. **Login Page Features**
   - ✅ Login button with validation
   - ✅ Sign Up button (gray color)
   - ✅ Clickable "Sign up here" text link
   - ✅ App logo/image at the top
   - ✅ Username and Password fields

### 3. **Sign Up Page Features**
   - ✅ Full Name field
   - ✅ Email field
   - ✅ Password field
   - ✅ Confirm Password field
   - ✅ Password validation (min 6 characters)
   - ✅ Password match validation
   - ✅ Clickable "Login here" text link

## 🖼️ Adding Custom Images

### Step 1: Add Images to Project
1. Open Android Studio
2. Go to **File → New → Image Asset** OR
3. Right-click on `app/src/main/res/drawable` → **New → Image Asset**
4. Select your image file
5. Click **Next** → **Import** → **Finish**

### Step 2: Use Image in Login Page
The login page already has an `ImageView` with ID `logoImageView`. 

To use a custom image:
1. Place your image in `app/src/main/res/drawable/` folder
2. In `activity_marriage_login.xml`, change the `android:src` attribute:
   ```xml
   android:src="@drawable/your_image_name"
   ```
   Example:
   ```xml
   android:src="@drawable/ic_wedding_logo"
   ```

## 🔧 How Click Listeners Work

### Sign Up Button Click
```kotlin
signupButton.setOnClickListener {
    val intent = Intent(this, SignupActivity::class.java)
    startActivity(intent)
}
```
- When user clicks "Sign Up" button, app navigates to SignupActivity

### Clickable Text Link
```kotlin
signupTextView.setOnClickListener {
    val intent = Intent(this, SignupActivity::class.java)
    startActivity(intent)
}
```
- The blue text "Don't have an account? Sign up here" is also clickable

## 🚀 Running the App

### Method 1: Using Android Studio
1. Click **Run** button (or press `Shift + F10`)
2. Select a device or emulator
3. Wait for app to build and deploy

### Method 2: Using Gradle (Command Line)
```bash
cd C:\Users\Dhruv Tyagi\AndroidStudioProjects\Marriage
gradlew.bat clean build
gradlew.bat installDebug
```

## ❌ Troubleshooting

### Issue: "App not showing on device"
**Solution:**
1. Check if the app is installed:
   ```bash
   adb devices
   adb shell pm list packages | findstr marriage
   ```
2. Clear cache and reinstall:
   ```bash
   adb uninstall com.example.marriage
   ```
3. Rebuild the project in Android Studio

### Issue: "Build fails"
**Solution:**
1. Click **Build → Clean Project**
2. Click **Build → Rebuild Project**
3. Check for SDK version compatibility (app uses SDK 36)

### Issue: "Crash on launch"
**Solution:**
1. Check Android Studio's Logcat for error messages
2. Look for `NullPointerException` - usually means a view ID doesn't match
3. Verify all `findViewById()` IDs exist in the layout XML

### Issue: "Device doesn't recognize app"
**Solution:**
1. Enable USB Debugging on device (Settings → Developer Options)
2. Authorize computer when prompted on device
3. Restart ADB: `adb kill-server && adb start-server`

## 📁 Project Structure

```
Marriage/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/marriage/
│   │   │   │   ├── MarriageLogin.kt (Login screen)
│   │   │   │   ├── SignupActivity.kt (Sign up screen)
│   │   │   │   └── MainActivity.kt (Home screen)
│   │   │   └── res/
│   │   │       ├── layout/
│   │   │       │   ├── activity_marriage_login.xml
│   │   │       │   ├── activity_signup.xml
│   │   │       │   └── activity_main.xml
│   │   │       ├── drawable/ (Add images here)
│   │   │       └── values/
│   │   │           ├── strings.xml
│   │   │           ├── colors.xml
│   │   │           └── themes.xml
│   └── build.gradle.kts
├── build.gradle.kts
└── gradle.properties
```

## ✨ Next Steps

1. **Add custom branding:**
   - Edit `strings.xml` to change app name
   - Add logo in `drawable/` folder
   - Customize colors in `colors.xml`

2. **Add backend authentication:**
   - Replace validation in MarriageLogin.kt with real server calls
   - Consider using Firebase or REST API

3. **Enhance UI:**
   - Add Material Design components
   - Add animations/transitions
   - Improve color scheme

## 📝 Key Files Reference

- **MarriageLogin.kt** - Login logic
- **SignupActivity.kt** - Sign up logic  
- **MainActivity.kt** - Home page after login
- **activity_marriage_login.xml** - Login layout
- **activity_signup.xml** - Sign up layout
- **AndroidManifest.xml** - App configuration

---

**Status:** ✅ All basic issues fixed. App is ready to run!

