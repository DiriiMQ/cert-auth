import React, { useState, useEffect } from 'react';
import { TabsContent } from '@/components/ui/tabs';
import { Alert, AlertDescription } from '@/components/ui/alert';
import { AlertTriangle, CheckCircle } from 'lucide-react';
import { Header } from '@/components/Header';
import { NavigationTabs } from '@/components/NavigationTabs';
import { ImageUpload } from '@/components/ImageUpload';
import { ApiService } from '@/services/api';
import { useToast } from '@/hooks/use-toast';

const Index = () => {
  const [serviceStatus, setServiceStatus] = useState<'checking' | 'online' | 'offline'>('checking');
  const { toast } = useToast();

  useEffect(() => {
    checkServiceHealth();
  }, []);

  const checkServiceHealth = async () => {
    try {
      await ApiService.healthCheck();
      setServiceStatus('online');
    } catch (error) {
      setServiceStatus('offline');
    }
  };

  const handleSignImage = async (file: File) => {
    try {
      const response = await ApiService.signImage(file);
      
      if (response.success && response.data && response.filename) {
        // Create download URL for the signed image
        const downloadUrl = URL.createObjectURL(response.data);
        
        // Trigger download
        const link = document.createElement('a');
        link.href = downloadUrl;
        link.download = response.filename;
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        
        // Clean up the URL object
        setTimeout(() => URL.revokeObjectURL(downloadUrl), 100);

        toast({
          title: "Success!",
          description: "Image signed and downloaded successfully.",
        });

        return {
          success: true,
          message: `Image successfully signed! Downloaded as ${response.filename}`,
          downloadUrl
        };
      }
      
      throw new Error('Failed to process signed image');
    } catch (error) {
      const message = error instanceof Error ? error.message : 'Failed to sign image';
      toast({
        title: "Error",
        description: message,
        variant: "destructive",
      });
      throw error;
    }
  };

  const handleVerifyImage = async (file: File) => {
    try {
      const response = await ApiService.verifyImage(file);
      
      const message = response.valid 
        ? 'Image signature is valid! ✓' 
        : 'Image signature is invalid or missing! ✗';

      toast({
        title: response.valid ? "Valid Signature" : "Invalid Signature",
        description: message,
        variant: response.valid ? "default" : "destructive",
      });

      return {
        success: response.valid,
        message
      };
    } catch (error) {
      const message = error instanceof Error ? error.message : 'Failed to verify image';
      toast({
        title: "Error",
        description: message,
        variant: "destructive",
      });
      throw error;
    }
  };

  return (
    <div className="min-h-screen bg-background">
      <Header />
      
      <main className="container mx-auto px-4 py-8">
        {/* Service Status */}
        <div className="mb-8 flex justify-center">
          <Alert className={`max-w-md ${serviceStatus === 'online' ? 'border-success' : 'border-warning'}`}>
            <div className="flex items-center space-x-2">
              {serviceStatus === 'online' ? (
                <CheckCircle className="h-4 w-4 text-success" />
              ) : (
                <AlertTriangle className="h-4 w-4 text-warning" />
              )}
              <AlertDescription>
                {serviceStatus === 'checking' && 'Checking service status...'}
                {serviceStatus === 'online' && 'Service is online and ready'}
                {serviceStatus === 'offline' && 'Service is offline - check if the signing service is running on localhost:8080'}
              </AlertDescription>
            </div>
          </Alert>
        </div>

        {/* Main Content */}
        <NavigationTabs>
          <TabsContent value="sign" className="flex justify-center">
            <ImageUpload 
              mode="sign" 
              onUpload={handleSignImage}
              className="w-full max-w-lg"
            />
          </TabsContent>
          
          <TabsContent value="verify" className="flex justify-center">
            <ImageUpload 
              mode="verify" 
              onUpload={handleVerifyImage}
              className="w-full max-w-lg"
            />
          </TabsContent>
        </NavigationTabs>

        {/* Info Section */}
        <div className="mt-12 max-w-4xl mx-auto">
          <div className="grid md:grid-cols-2 gap-6">
            <div className="bg-card rounded-lg p-6 border border-border">
              <h3 className="text-lg font-semibold text-foreground mb-3">How Image Signing Works</h3>
              <ul className="space-y-2 text-sm text-muted-foreground">
                <li>• Upload your JPEG or PNG image</li>
                <li>• A cryptographic signature is embedded in the metadata</li>
                <li>• Download the signed image with embedded proof</li>
                <li>• The image looks identical but now has verifiable authenticity</li>
              </ul>
            </div>
            
            <div className="bg-card rounded-lg p-6 border border-border">
              <h3 className="text-lg font-semibold text-foreground mb-3">Signature Verification</h3>
              <ul className="space-y-2 text-sm text-muted-foreground">
                <li>• Upload a previously signed image</li>
                <li>• The embedded signature is extracted and validated</li>
                <li>• Instant verification of image authenticity</li>
                <li>• Detect if the image has been tampered with</li>
              </ul>
            </div>
          </div>
        </div>
      </main>
    </div>
  );
};

export default Index;
