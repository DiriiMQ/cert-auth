import React from 'react';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { Shield, ShieldCheck } from 'lucide-react';

interface NavigationTabsProps {
  children: React.ReactNode;
  defaultValue?: string;
}

export const NavigationTabs: React.FC<NavigationTabsProps> = ({ children, defaultValue = "sign" }) => {
  return (
    <Tabs defaultValue={defaultValue} className="w-full max-w-4xl mx-auto">
      <TabsList className="grid w-full grid-cols-2 mb-8">
        <TabsTrigger value="sign" className="flex items-center space-x-2">
          <Shield className="h-4 w-4" />
          <span>Sign Image</span>
        </TabsTrigger>
        <TabsTrigger value="verify" className="flex items-center space-x-2">
          <ShieldCheck className="h-4 w-4" />
          <span>Verify Signature</span>
        </TabsTrigger>
      </TabsList>
      {children}
    </Tabs>
  );
};