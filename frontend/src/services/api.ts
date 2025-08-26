const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

export interface SignResponse {
  success: boolean;
  data?: Blob;
  filename?: string;
}

export interface VerifyResponse {
  valid: boolean;
}

export class ApiService {
  static async signImage(file: File): Promise<SignResponse> {
    const formData = new FormData();
    formData.append('file', file);

    const response = await fetch(`${API_BASE_URL}/api/v1/sign`, {
      method: 'POST',
      body: formData,
    });

    if (!response.ok) {
      throw new Error(`Failed to sign image: ${response.statusText}`);
    }

    // Get the filename from the Content-Disposition header
    const contentDisposition = response.headers.get('Content-Disposition');
    const filename = contentDisposition?.match(/filename="?([^"]+)"?/)?.[1] || `signed_${file.name}`;

    const blob = await response.blob();
    
    return {
      success: true,
      data: blob,
      filename,
    };
  }

  static async verifyImage(file: File): Promise<VerifyResponse> {
    const formData = new FormData();
    formData.append('file', file);

    const response = await fetch(`${API_BASE_URL}/api/v1/verify`, {
      method: 'POST',
      body: formData,
    });

    if (!response.ok) {
      throw new Error(`Failed to verify image: ${response.statusText}`);
    }

    return await response.json();
  }

  static async healthCheck(): Promise<{ status: string }> {
    const response = await fetch(`${API_BASE_URL}/health`);
    
    if (!response.ok) {
      throw new Error(`Health check failed: ${response.statusText}`);
    }

    return await response.json();
  }
}