# Visual Setup Guide - Step by Step

## 🎯 How to Add Images (Step-by-Step with Screenshots)

### Step 1: Prepare Your Image
- Use a square image (e.g., 512x512 or 1024x1024 pixels)
- Format: PNG or JPG
- File size: Less than 2MB
- Recommended: App logo or wedding-themed image

### Step 2: Add Image to Project
**Method A: Using Android Studio UI**
1. In Android Studio, right-click on `app/src/main/res/drawable`
2. Select **New** → **Image Asset**
3. Choose **Source File** and browse to your image
4. Click **Next** → **Finish**
5. Image is now in drawable folder

**Method B: Manual Copy**
1. Open File Explorer
2. Navigate to `C:\Users\Dhruv Tyagi\AndroidStudioProjects\Marriage\app\src\main\res\drawable`
3. Copy your image here
4. Image name should be lowercase with underscores (e.g., `wedding_logo.png`)

### Step 3: Update Layout XML
Open `app/src/main/res/layout/activity_marriage_login.xml`

Find this line:
```xml
<ImageView
    android:id="@+id/logoImageView"
    ...
    android:src="@drawable/ic_launcher_foreground"
```

Change to:
```xml
<ImageView
    android:id="@+id/logoImageView"
    ...
    android:src="@drawable/wedding_logo"
```

**Important:** Use your image name WITHOUT the file extension!

### Step 4: Rebuild and Test
1. Click **Build** → **Rebuild Project**
2. Run the app
3. Your image should appear at the top of login screen

---

## 🔘 How Sign Up Button Works

### Visual Flow
```
User Opens App
      ↓
[Login Screen] (MarriageLogin.kt)
      ↓
User clicks "Sign Up" button
      ↓
→ SignupActivity.kt onCreate() runs
  → Starts SignupActivity intent
  → Layout activity_signup.xml loads
[Sign Up Screen] 
      ↓
User fills form and clicks "Sign Up"
      ↓
→ Form validated in SignupActivity.kt
→ If valid: "Account created" toast
→ Intent back to MarriageLogin
[Back to Login Screen]
```

### Code: Sign Up Button Click
**File: MarriageLogin.kt**
```kotlin
signupButton.setOnClickListener {
    // Create intent to open SignupActivity
    val intent = Intent(this, SignupActivity::class.java)
    
    // Start the activity
    startActivity(intent)
}
```

### Code: Text Link Click
**File: MarriageLogin.kt**
```kotlin
signupTextView.setOnClickListener {
    val intent = Intent(this, SignupActivity::class.java)
    startActivity(intent)
}
```

Both buttons do the same thing - they open SignupActivity

---

## 🚀 How to Run App on Device

### Step 1: Connect Device
**Physical Phone:**
1. Connect via USB cable
2. Settings → Developer Options → USB Debugging ON
3. Tap "Allow" on device (trust prompt)
4. Type in PowerShell: `adb devices`
5. Should see your phone listed

**Virtual Emulator:**
1. Android Studio → Tools → Device Manager
2. Click play icon next to a device
3. Wait 60 seconds for Android to boot
4. Device appears in Device Manager as "running"

### Step 2: Run App
**Method 1: Android Studio UI**
1. Click green **Run** button (or press Shift + F10)
2. In "Select Deployment Target" popup
3. Select your device from list
4. Click **OK**
5. App installs and launches

**Method 2: Command Line**
```powershell
cd "C:\Users\Dhruv Tyagi\AndroidStudioProjects\Marriage"
.\gradlew.bat installDebug
adb shell am start -n com.example.marriage/.MarriageLogin
```

### Step 3: Verify App Launched
- Device shows Marriage app icon
- Login screen appears
- You can enter username and password
- Sign Up button is clickable

---

## 🎨 How to Change App Colors

### Step 1: Edit Colors File
Open: `app/src/main/res/values/colors.xml`

Example current file:
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="black">#FF000000</color>
    <color name="white">#FFFFFFFF</color>
</resources>
```

### Step 2: Add Custom Colors
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="black">#FF000000</color>
    <color name="white">#FFFFFFFF</color>
    <color name="primary_red">#FF6B35</color>
    <color name="primary_blue">#004E89</color>
    <color name="accent_gold">#FFB700</color>
</resources>
```

### Step 3: Use in Layout
In `activity_marriage_login.xml`:
```xml
<Button
    android:id="@+id/loginButton"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="Login"
    android:backgroundTint="@color/primary_red"
/>
```

### Step 4: Rebuild
Click **Build** → **Rebuild Project**

---

## 📝 How to Customize Text

### Change Button Text
In XML layout files, look for:
```xml
<Button
    android:text="Login"
/>
```

Change to:
```xml
<Button
    android:text="Sign In"
/>
```

### Change Label Text
```xml
<TextView
    android:text="Don't have an account? Sign up here"
/>
```

Change to:
```xml
<TextView
    android:text="Create new account here"
/>
```

### Change App Name
In `app/src/main/res/values/strings.xml`:
```xml
<string name="app_name">Marriage</string>
```

Change to:
```xml
<string name="app_name">Perfect Match</string>
```

Then rebuild: **Build** → **Rebuild Project**

---

## 🔍 How to Debug Issues

### Check if App Installed
```powershell
adb devices
# Should show your device

adb shell pm list packages | findstr marriage
# Should show: com.example.marriage
```

### View App Logs
**In Android Studio:**
1. Bottom panel → Click "Logcat"
2. Run your app
3. Scroll through logs looking for errors (red text)

**From Command Line:**
```powershell
adb logcat | findstr marriage
```

### Clear App Data
```powershell
adb shell pm clear com.example.marriage
```

### Uninstall and Reinstall
```powershell
adb uninstall com.example.marriage
.\gradlew.bat installDebug
```

---

## 📊 UI Preview

### Login Screen Layout
```
╔═══════════════════════════╗
║                           ║
║    [Wedding Logo]         ║  ← ImageView
║   (324x326 dp)            ║
║                           ║
║     - Login -             ║  ← Title (24sp)
║                           ║
║ ┌─────────────────────┐  ║
║ │ Username input      │  ║  ← EditText
║ └─────────────────────┘  ║
║                           ║
║ ┌─────────────────────┐  ║
║ │ Password input      │  ║  ← EditText (masked)
║ └─────────────────────┘  ║
║                           ║
║      [Login Button]       ║  ← Primary button
║                           ║
║     [Sign Up Button]      ║  ← Gray button
║                           ║
║  Don't have account?      ║  ← Blue clickable text
║   Sign up here            ║
║                           ║
╚═══════════════════════════╝
```

### Sign Up Screen Layout
```
╔═══════════════════════════╗
║   Create Account          ║  ← Title (28sp)
║                           ║
║ ┌─────────────────────┐  ║
║ │ Full Name input     │  ║
║ └─────────────────────┘  ║
║                           ║
║ ┌─────────────────────┐  ║
║ │ Email input         │  ║
║ └─────────────────────┘  ║
║                           ║
║ ┌─────────────────────┐  ║
║ │ Password input      │  ║
║ └─────────────────────┘  ║
║                           ║
║ ┌─────────────────────┐  ║
║ │ Confirm Password    │  ║
║ └─────────────────────┘  ║
║                           ║
║    [Sign Up Button]       ║
║                           ║
║  Already have account?    ║  ← Blue clickable text
║   Login here              ║
║                           ║
╚═══════════════════════════╝
```

---

## ⚙️ App Configuration

### Target Device Specs
- **Min Android Version:** Android 10 (API 29)
- **Target Android Version:** Android 15 (API 36)
- **Recommended Device:** Android 12+ (API 31+)

### Project Structure
```
Marriage/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/example/marriage/
│   │   │   ├── res/
│   │   │   │   ├── drawable/    ← Add images here
│   │   │   │   ├── layout/      ← XML layouts
│   │   │   │   └── values/      ← Colors, strings
│   │   │   └── AndroidManifest.xml
│   │   └── test/
│   └── build.gradle.kts
├── gradle/
└── settings.gradle.kts
```

---

## 🎓 Learning Resources

### Android Documentation
- Official Docs: https://developer.android.com/
- Kotlin Guide: https://kotlinlang.org/docs/
- Material Design: https://material.io/design/

### Key Components Used
- **Activity:** Screen/Page
- **Intent:** Navigation between screens
- **EditText:** Text input field
- **Button:** Clickable button
- **TextView:** Text display
- **ImageView:** Image display
- **ConstraintLayout:** Layout positioning

---

## 🆘 Quick Problem Solver

| Problem | Solution |
|---------|----------|
| "Cannot find symbol: R" | Build → Rebuild Project |
| "Activity not registered" | Add to AndroidManifest.xml |
| "View not found" | Check ID matches XML layout |
| "App crashes on launch" | Check Logcat for errors |
| "Device not recognized" | Enable USB Debugging |
| "Image not showing" | Check drawable folder exists |
| "Build fails" | ./gradlew.bat clean build |

---

**All guides complete! Start with SETUP_GUIDE.md for comprehensive instructions.**

