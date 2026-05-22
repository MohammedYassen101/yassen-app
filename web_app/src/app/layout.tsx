import type { Metadata } from "next";
import "./globals.css";

export const metadata: Metadata = {
  title: "LeaseFlow AI - Property Management Workspace",
  description: "Cross-platform real estate management powered by Supabase database and Gemini AI drafts.",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body>{children}</body>
    </html>
  );
}
