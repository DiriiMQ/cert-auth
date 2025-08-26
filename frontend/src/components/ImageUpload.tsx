import React, { useCallback, useState } from 'react';
import { useDropzone } from 'react-dropzone';
import { Upload, FileCheck, AlertCircle, Download, Loader2 } from 'lucide-react';
import { Card, CardContent } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Alert, AlertDescription } from '@/components/ui/alert';
import { Badge } from '@/components/ui/badge';
import { cn } from '@/lib/utils';

interface ImageUploadResult {
  success: boolean;
  message: string;
  downloadUrl?: string;
}

interface ImageUploadProps {
  mode: 'sign' | 'verify';
  onUpload: (file: File) => Promise<ImageUploadResult>;
  className?: string;
}

export const ImageUpload: React.FC<ImageUploadProps> = ({ mode, onUpload, className }) => {
  const [uploading, setUploading] = useState(false);
  const [result, setResult] = useState<ImageUploadResult | null>(null);
  const [selectedFile, setSelectedFile] = useState<File | null>(null);

  const onDrop = useCallback(async (acceptedFiles: File[]) => {
    const file = acceptedFiles[0];
    if (!file) return;

    setSelectedFile(file);
    setResult(null);
    setUploading(true);

    try {
      const response = await onUpload(file);
      setResult(response);
    } catch (error) {
      setResult({ 
        success: false, 
        message: error instanceof Error ? error.message : 'Upload failed' 
      });
    } finally {
      setUploading(false);
    }
  }, [onUpload]);

  const { getRootProps, getInputProps, isDragActive } = useDropzone({
    onDrop,
    accept: {
      'image/jpeg': ['.jpg', '.jpeg'],
      'image/png': ['.png']
    },
    maxFiles: 1,
    disabled: uploading
  });

  const resetUpload = () => {
    setSelectedFile(null);
    setResult(null);
  };

  return (
    <Card className={cn("w-full max-w-lg", className)}>
      <CardContent className="p-6">
        <div className="text-center mb-4">
          <h3 className="text-lg font-semibold text-foreground mb-2">
            {mode === 'sign' ? 'Sign Image' : 'Verify Signature'}
          </h3>
          <p className="text-sm text-muted-foreground">
            {mode === 'sign' 
              ? 'Upload an image to add a cryptographic signature' 
              : 'Upload a signed image to verify its authenticity'
            }
          </p>
        </div>

        <div
          {...getRootProps()}
          className={cn(
            "border-2 border-dashed rounded-lg p-8 text-center cursor-pointer transition-all duration-200",
            isDragActive ? "border-primary bg-primary/5" : "border-border hover:border-primary/50",
            uploading && "cursor-not-allowed opacity-50"
          )}
        >
          <input {...getInputProps()} />
          
          {uploading ? (
            <div className="space-y-3">
              <Loader2 className="mx-auto h-10 w-10 animate-spin text-primary" />
              <p className="text-sm text-muted-foreground">
                {mode === 'sign' ? 'Signing image...' : 'Verifying signature...'}
              </p>
            </div>
          ) : (
            <div className="space-y-3">
              <Upload className="mx-auto h-10 w-10 text-muted-foreground" />
              <div>
                <p className="text-sm font-medium text-foreground">
                  {isDragActive ? 'Drop the image here' : 'Drop an image here, or click to select'}
                </p>
                <p className="text-xs text-muted-foreground mt-1">
                  Supports JPEG and PNG files
                </p>
              </div>
            </div>
          )}
        </div>

        {selectedFile && (
          <div className="mt-4 p-3 bg-muted rounded-lg">
            <div className="flex items-center justify-between">
              <div className="flex items-center space-x-2">
                <FileCheck className="h-4 w-4 text-muted-foreground" />
                <span className="text-sm font-medium text-foreground">{selectedFile.name}</span>
              </div>
              <Badge variant="secondary">
                {(selectedFile.size / 1024).toFixed(1)} KB
              </Badge>
            </div>
          </div>
        )}

        {result && (
          <Alert className={cn("mt-4", result.success ? "border-success" : "border-destructive")}>
            <div className="flex items-center space-x-2">
              {result.success ? (
                <FileCheck className="h-4 w-4 text-success" />
              ) : (
                <AlertCircle className="h-4 w-4 text-destructive" />
              )}
              <AlertDescription className={result.success ? "text-success" : "text-destructive"}>
                {result.message}
              </AlertDescription>
            </div>
          </Alert>
        )}

        {result && result.success && result.downloadUrl && (
          <div className="mt-4 flex space-x-2">
            <Button asChild variant="default" className="flex-1">
              <a href={result.downloadUrl} download>
                <Download className="mr-2 h-4 w-4" />
                Download Signed Image
              </a>
            </Button>
            <Button variant="outline" onClick={resetUpload}>
              Upload Another
            </Button>
          </div>
        )}

        {result && !result.downloadUrl && (
          <div className="mt-4">
            <Button variant="outline" onClick={resetUpload} className="w-full">
              {mode === 'sign' ? 'Sign Another Image' : 'Verify Another Image'}
            </Button>
          </div>
        )}
      </CardContent>
    </Card>
  );
};