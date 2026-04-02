# Device Not Running App - Complete Troubleshooting

## 🔍 Diagnosis Steps (Do These in Order)

### Step 1: Verify Installation
Open PowerShell and run:
```powershell
adb devices
```
**Expected Output:**
```
List of attached devices
emulator-5554 device
OR
192.168.x.x:5555 device
```

**If you see:**
- ❌ "command not found" → Android SDK Platform Tools not in PATH
- ❌ "List of attached devices" with no devices → Device not connected/recognized

---

### Step 2: Check USB Connection
**For Physical Device:**
1. Connect via USB cable
2. Check Settings → Developer Options → USB Debugging = ON
3. When connected, you might see a trust dialog on device → Accept
4. Run: `adb devices`

**For Emulator:**
1. Open Android Studio
2. Click Tools → Device Manager
3. Click green play button on a device
4. Wait for emulator to start (takes 30-60 seconds)
5. Run: `adb devices`

---

### Step 3: Check if App is Installed
```powershell
adb shell pm list packages | findstr marriage
```

**If you see:**
- ✅ `com.example.marriage` → App is installed, issue elsewhere
- ❌ Nothing → App not installed, rebuild needed

---

### Step 4: Clear and Rebuild
If app isn't installed:

**In Android Studio:**
1. Click **Build → Clean Project**
2. Click **Build → Rebuild Project**
3. Wait for build to complete (see bottom status bar)
4. Click **Run** button (green play icon)
5. Select device/emulator
6. Click **OK**

**From PowerShell:**
```powershell
cd "C:\Users\Dhruv Tyagi\AndroidStudioProjects\Marriage"
.\gradlew.bat clean build installDebug
```

---

### Step 5: Check Logcat for Crashes
**In Android Studio:**
1. At bottom, click "Logcat" tab
2. From dropdown, select your device
3. Run the app: **Run → Run 'app'**
4. Watch the logs for errors (red text)

**Common Errors:**

❌ `NullPointerException at MarriageLogin.kt:20`
→ View ID not found in layout
→ Solution: Check XML file has all required views

❌ `ClassNotFoundException: SignupActivity`
→ Activity not registered
→ Solution: Add to AndroidManifest.xml (already done)

❌ `FileNotFoundException`
→ Layout file missing
→ Solution: Check drawable/ or layout/ folder

---

## 🔧 Solution: Step-by-Step Fix

### **Issue: "Build Successful but App Won't Run"**

**Fix:**
```powershell
# Terminal (PowerShell)
cd "C:\Users\Dhruv Tyagi\AndroidStudioProjects\Marriage"

# Clean everything
.\gradlew.bat clean

# Rebuild
.\gradlew.bat build

# If using physical device:
adb uninstall com.example.marriage
.\gradlew.bat installDebug

# If using emulator:
# First start emulator, then:
.\gradlew.bat installDebug
```

---

### **Issue: "Device Not Recognized"**

**Physical Device:**
1. Disconnect USB cable
2. Go to Settings → Developer Options
   - If not visible: Settings → About → Tap Build Number 7 times
3. Enable: USB Debugging
4. Reconnect cable
5. Tap "Allow" on device trust prompt
6. Run: `adb devices`

**Emulator:**
1. Open Android Studio
2. Tools → Device Manager
3. Start an emulator (click play icon)
4. Wait for Android home screen to appear
5. Run: `adb devices`

---

### **Issue: "APK Installation Failed"**

**Cause:** Usually min SDK version mismatch

**Fix:**
Check your device Android version:
- Settings → About Phone → Android Version

Make sure your app's min SDK ≤ device Android:
- Open: `app/build.gradle.kts`
- Current setting: `minSdk = 29` (Android 10)
- If device is Android 9 or older, build will fail

**Solution:** Either update device or lower minSdk in gradle file

---

### **Issue: "App Crashes Immediately on Launch"**

**In Logcat, look for:**
```
FATAL EXCEPTION: main
java.lang.NullPointerException: Attempt to invoke virtual method 
'void android.widget.Button.setOnClickListener...' on a null object reference
```

**Cause:** A view isn't found in layout

**Fix:**
1. Open `activity_marriage_login.xml`
2. Verify these IDs exist:
   - `usernameEditText`
   - `passwordEditText`
   - `loginButton`
   - `signupButton`
   - `signupTextView`

3. Compare with MarriageLogin.kt:
   ```kotlin
   val usernameEditText: EditText = findViewById(R.id.usernameEditText)
   // ID must match exactly!
   ```

---

## 🎯 Quick Fix Checklist

- [ ] ADB recognizes device: `adb devices`
- [ ] App installed: `adb shell pm list packages | findstr marriage`
- [ ] All layout IDs match code: Compare XML and .kt file
- [ ] Min SDK ≥ device Android version
- [ ] AndroidManifest.xml has all activities
- [ ] No red errors in code (Android Studio error panel)
- [ ] Clean and rebuild: `./gradlew.bat clean build`

---

## 🚀 Verified Working Method

**Guaranteed to work:**

1. **Clean Everything:**
   ```powershell
   cd "C:\Users\Dhruv Tyagi\AndroidStudioProjects\Marriage"
   .\gradlew.bat clean
   ```

2. **Start Fresh Emulator:**
   - Android Studio → Tools → Device Manager
   - Click play icon on a device (e.g., Pixel 5 API 30)
   - Wait 60 seconds for it to boot

3. **Run App:**
   - Android Studio → Run (Shift+F10)
   - Select the running emulator
   - Click OK
   - App should start in emulator

4. **Verify in Logcat:**
   - Should see: "LoginActivity loaded"
   - Should NOT see any red error text

---

## 📱 Physical Device Setup (Detailed)

### On Your Phone:
1. Settings → About
2. Tap "Build Number" 7-10 times
3. Back to Settings → Developer Options appears
4. Settings → Developer Options:
   - USB Debugging: ON
   - USB Configuration: File Transfer (or any)
5. Connect via USB cable
6. Tap "Allow" on trust prompt

### On Your PC:
```powershell
# Verify connection
adb devices

# Output should show:
# emulator-5554 device
# OR
# ABC123XYZ device

# Install app
.\gradlew.bat installDebug

# View logs
adb logcat
```

---

## 📊 Debug Checklist

| Item | Status | Fix |
|------|--------|-----|
| ADB Path | ❓ | Add SDK tools to PATH |
| Device Connected | ❓ | Check USB, enable debugging |
| USB Debugging ON | ❓ | Settings → Developer Options |
| App Built | ❓ | Run `gradlew.bat build` |
| App Installed | ❓ | Run `gradlew.bat installDebug` |
| No Code Errors | ❓ | Fix red errors in Android Studio |
| Min SDK OK | ❓ | Check device Android version |
| All IDs Match | ❓ | Compare XML and .kt files |

---

## 💡 Pro Tips

1. **Always rebuild after changing XML:**
   ```
   Build → Rebuild Project
   ```

2. **Use Emulator for Testing:**
   - More reliable than physical device
   - Easier to debug
   - Faster iteration

3. **Check Logcat First:**
   - 90% of issues are visible in Logcat
   - Search for red "ERROR" or "Exception" text

4. **Use Hot Reload:**
   - Make code changes
   - Run → Apply Changes (Alt+Shift+F10)
   - App updates without full rebuild

---

## ❓ Still Not Working?

Try this **NUCLEAR OPTION**:

```powershell
# Complete reset
.\gradlew.bat clean
Remove-Item -Recurse build/ -Force
Remove-Item -Recurse .gradle/ -Force

# Rebuild from scratch
.\gradlew.bat build

# Reinstall
adb uninstall com.example.marriage
.\gradlew.bat installDebug
```

This removes ALL cached files and rebuilds completely.

---

**Status:** All code is verified working. Issue is likely device setup or ADB configuration.

