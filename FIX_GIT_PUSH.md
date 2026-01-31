# Fix GitHub Push Error

## ❌ Error: "push declined due to repository rule violations"

This error usually happens due to:

1. **Large files** (APK files are ~9MB - GitHub has limits)
2. **Sensitive files** (google-services.json, service account keys)
3. **Branch protection** (can't push directly to main)
4. **Missing required checks**

## ✅ Solutions

### Solution 1: Remove Large/Sensitive Files from Git

**Check what's being pushed:**
```bash
git status
git ls-files | grep -E "\.apk$|google-services\.json|servicesAccountsKey\.json"
```

**Remove from Git (but keep locally):**
```bash
# Remove APK files
git rm --cached android-app/app/build/outputs/apk/debug/app-debug.apk
git rm --cached DeviceManager.zip
git rm --cached DeviceManager.apk

# Remove sensitive files
git rm --cached google-services.json
git rm --cached backend-server/servicesAccountsKey.json
git rm --cached android-app/app/google-services.json

# Update .gitignore
echo "*.apk" >> .gitignore
echo "*.zip" >> .gitignore
echo "google-services.json" >> .gitignore
echo "servicesAccountsKey.json" >> .gitignore
echo "backend-server/servicesAccountsKey.json" >> .gitignore
```

### Solution 2: Use Git LFS for Large Files

If you need to store APK files:

```bash
# Install Git LFS
git lfs install

# Track APK files
git lfs track "*.apk"
git lfs track "*.zip"

# Add .gitattributes
git add .gitattributes
```

### Solution 3: Push to Different Branch

If main branch is protected:

```bash
# Create new branch
git checkout -b develop

# Push to new branch
git push origin develop
```

### Solution 4: Use GitHub Releases

Instead of pushing APK to repo:
1. Go to GitHub → Releases
2. Create new release
3. Upload APK as release asset
4. Don't commit APK to repository

## 🔧 Quick Fix Commands

```bash
# 1. Update .gitignore
cat >> .gitignore << EOF
*.apk
*.zip
google-services.json
servicesAccountsKey.json
backend-server/servicesAccountsKey.json
android-app/app/google-services.json
EOF

# 2. Remove tracked files
git rm --cached android-app/app/build/outputs/apk/debug/app-debug.apk 2>/dev/null
git rm --cached DeviceManager.zip 2>/dev/null
git rm --cached google-services.json 2>/dev/null
git rm --cached backend-server/servicesAccountsKey.json 2>/dev/null

# 3. Commit changes
git add .gitignore
git commit -m "Remove large files and sensitive data from git"

# 4. Try pushing again
git push origin main
```

## ⚠️ Important: Don't Commit

**Never commit these files:**
- ❌ APK files (too large)
- ❌ `google-services.json` (sensitive)
- ❌ `servicesAccountsKey.json` (contains private keys)
- ❌ Any files with API keys or secrets

## ✅ What Should Be Committed

- ✅ Source code (.java files)
- ✅ Resources (XML, images)
- ✅ Build configuration (build.gradle)
- ✅ Documentation (README, guides)
- ✅ Project structure

## 🎯 Recommended Approach

1. **Remove APK and sensitive files from Git**
2. **Update .gitignore** to exclude them
3. **Use GitHub Releases** for APK distribution
4. **Push only source code**

This is the standard practice for Android projects!
