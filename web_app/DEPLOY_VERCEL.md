# 🚀 Deploying LeaseFlow AI to Vercel

This guide walks you through deploying the LeaseFlow AI web application to **Vercel** in just a few clicks.

---

## 📋 Prerequisites
1. A **GitHub** account.
2. A **Vercel** account (you can sign up for free using your GitHub account at [vercel.com](https://vercel.com)).

---

## 🛠️ Step-by-Step Deployment Instructions

### Step 1: Push your code to GitHub from AI Studio
1. In the Google AI Studio interface, open the **Settings** menu page (or click on the Project dropdown/settings).
2. Select **Push to GitHub** or **Export to GitHub**.
3. Authenticate with your GitHub account and create a new repository (e.g., `leaseflow-ai`).

### Step 2: Import the project in Vercel
1. Go to [vercel.com](https://vercel.com) and log into your dashboard.
2. Click the **Add New...** button and select **Project**.
3. Import the GitHub repository you created in Step 1.

### Step 3: Configure critical project settings (Crucial!)
Since the repository contains multiple directories (Android app, Flutter app, and Web app), you need to tell Vercel to build only the web application:

1. **Root Directory**:
   - Next to **Root Directory**, click **Edit**.
   - Select the `web_app` folder.
   - Click **Keep** / **Continue**.

2. **Framework Preset**:
   - Vercel will automatically detect **Next.js** as the framework using our `vercel.json` and package settings.

3. **Environment Variables** (Optional but highly recommended for production):
   - Expand the **Environment Variables** section.
   - Add the following keys so that your web application connects perfectly to your Supabase instance:
     
     | Key | Value |
     | :--- | :--- |
     | `NEXT_PUBLIC_SUPABASE_URL` | `https://ovjqwlbaobmntyisoqbw.supabase.co` |
     | `NEXT_PUBLIC_SUPABASE_ANON_KEY` | `sb_publishable_hJbw95RBuUoqXUQldBQyzQ_5QFOmw_r` |

### Step 4: Deploy!
1. Click the **Deploy** button at the bottom of the Vercel setup page.
2. Vercel will build and launch your application. Within 1-2 minutes, you'll receive a live production URL!

---

## 🔄 Automatic Redeployments
Every time you make updates in AI Studio and push those changes to your main branch on GitHub, Vercel will **automatically trigger a new, zero-downtime deployment** to update your live website instantly!
