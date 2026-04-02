# Quick Reference Guide

## 🎯 Your Questions Answered

### Q1: How to add pictures in login page?
**A:** 
1. Go to `app/src/main/res/drawable/` folder in Android Studio
2. Right-click → **New → Image Asset**
3. Select and import your image
4. In `activity_marriage_login.xml`, change:
   ```xml
   android:src="@drawable/ic_launcher_foreground"
   ```
   To:
   ```xml
   android:src="@drawable/your_image_name"
   ```

---

### Q2: How to add signup button to create account?
**A:**
✅ **Already done!** You have:
- A gray "Sign Up" button
- A blue clickable text link "Don't have an account? Sign up here"

Both navigate to SignupActivity when clicked.

---

### Q3: How to make "Sign up" text clickable?
**A:**
In the code (MarriageLogin.kt):
```kotlin
signupTextView.setOnClickListener {
    val intent = Intent(this, SignupActivity::class.java)
    startActivity(intent)
}
```

In the layout (activity_marriage_login.xml):
```xml
<TextView
    android:id="@+id/signupTextView"
    android:clickable="true"
    android:focusable="true"
    ... />
```

The `clickable="true"` and `focusable="true"` attributes make it clickable.

---

### Q4: Why doesn't device run the app?
**A:** Check these in order:

1. **USB Debugging Enabled?**
   - Settings → Developer Options → USB Debugging (ON)

2. **Device Connected?**
   - Check: `adb devices`
   - Should show your device

3. **App Installed?**
   ```bash
   adb shell pm list packages | findstr marriage
   ```
   - If not found, rebuild: **Build → Rebuild Project**

4. **Crash on Launch?**
   - Check Logcat for errors (Android Studio)
   - Look for `NullPointerException`

---

### Q5: How to test without physical device?
**A:**
1. **Open AVD Manager** in Android Studio
2. Click **Create Virtual Device**
3. Select a device (e.g., Pixel 5)
4. Select API Level 29+ (min is 29)
5. Click **Finish**
6. Run the app - it will auto-launch on emulator

---

### Q6: How to change app branding?
**A:**

**Change App Name:**
Open `app/src/main/res/values/strings.xml`:
```xml
<string name="app_name">My Marriage App</string>
```

**Change Colors:**
Edit `app/src/main/res/values/colors.xml`

**Change Logos:**
Replace images in `drawable/` folder

---

## 🔑 All Click Listeners Explained

### Login Button
```kotlin
loginButton.setOnClickListener {
    // Validates username and password
    // Navigates to MainActivity
}
```

### Sign Up Button
```kotlin
signupButton.setOnClickListener {
    // Navigates to SignupActivity
}
```

### Sign Up Text Link
```kotlin
signupTextView.setOnClickListener {
    // Navigates to SignupActivity
}
```

### Create Account Button (in SignupActivity)
```kotlin
signupButton.setOnClickListener {
    // Validates all fields
    // Shows success message
    // Returns to MarriageLogin
}
```

### Login Text Link (in SignupActivity)
```kotlin
loginTextView.setOnClickListener {
    // Returns to MarriageLogin
}
```

---

## 🛠️ Common Commands

```bash
# Clear project
./gradlew.bat clean

# Build project
./gradlew.bat build

# Check connected devices
adb devices

# Clear app cache
adb shell pm clear com.example.marriage

# Uninstall app
adb uninstall com.example.marriage

# View app logs
adb logcat | findstr marriage
```

---

## 📊 Current UI Layout

### Login Page
```
┌─────────────────────────┐
│   [Wedding Logo/Image]  │ (ImageView)
├─────────────────────────┤
│        Login            │ (Title)
├─────────────────────────┤
│  [Username Input]       │
│  [Password Input]       │
├─────────────────────────┤
│  [Login Button]         │
│  [Sign Up Button]       │
│  Sign up here (link)    │
└─────────────────────────┘
```

### Sign Up Page
```
┌─────────────────────────┐
│   Create Account        │ (Title)
├─────────────────────────┤
│  [Full Name Input]      │
│  [Email Input]          │
│  [Password Input]       │
│  [Confirm Password]     │
├─────────────────────────┤
│  [Sign Up Button]       │
│  Login here (link)      │
└─────────────────────────┘
```

---

## ✅ Fixed Issues

✓ Removed unused imports (ViewCompat, WindowInsetsCompat)
✓ Improved password validation
✓ Added better error handling
✓ All button listeners working
✓ Sign up navigation working
✓ Login navigation working

---

## 🚀 Next Steps

1. Add custom wedding/marriage themed images
2. Connect to backend Firebase or REST API
3. Add user authentication
4. Add profile page
5. Add messaging feature
6. Add notifications

---

**Need more help?** Refer to `SETUP_GUIDE.md` for detailed instructions.

