import React from 'react';
import { Shield } from 'lucide-react';

export const Header: React.FC = () => {
  return (
    <header className="w-full bg-card border-b border-border">
      <div className="container mx-auto px-4 py-6">
        <div className="flex items-center justify-center space-x-3">
          <div className="flex items-center justify-center w-12 h-12 bg-primary rounded-lg">
            <Shield className="h-6 w-6 text-primary-foreground" />
          </div>
          <div className="text-center">
            <h1 className="text-2xl font-bold text-foreground">ImageSign</h1>
            <p className="text-sm text-muted-foreground">Secure Image Signing & Verification</p>
          </div>
        </div>
      </div>
    </header>
  );
};